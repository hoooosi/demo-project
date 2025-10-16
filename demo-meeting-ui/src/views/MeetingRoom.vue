<script setup lang="ts">
import { ref, onMounted, onBeforeUnmount, watch, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Microphone, VideoCamera, PhoneFilled } from '@element-plus/icons-vue'
import { useUserStore } from '@/stores/user'
import { MeetingApi } from '@/api'
import { useWebRTC } from '@/composables/useWebRTC'
import VideoTile from '@/components/VideoTile.vue'
import { createMediaStream } from '@/utils/track'
import { lo } from 'element-plus/es/locales.mjs'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

// 本地媒体流
const localStream = ref<MediaStream>(createMediaStream())
const isAudioEnabled = ref(true)
const isVideoEnabled = ref(true)

// 使用 WebRTC composable
const {
  localMap,
  createPeerConnection,
  registerMessageHandlers,
  unregisterMessageHandlers,
  closeAllConnections,
} = useWebRTC(localStream)

const memberList = computed(() => {
  return Array.from(localMap.values())
})

// 切换音频
const toggleAudio = () => {
  if (localStream.value) {
    const audioTracks = localStream.value.getAudioTracks()
    audioTracks.forEach((track) => {
      track.enabled = !track.enabled
    })
    isAudioEnabled.value = !isAudioEnabled.value
    ElMessage.info(isAudioEnabled.value ? '麦克风已开启' : '麦克风已关闭')
  }
}

// 切换视频
const toggleVideo = () => {
  if (localStream.value) {
    const videoTracks = localStream.value.getVideoTracks()
    videoTracks.forEach((track) => {
      track.enabled = !track.enabled
    })
    isVideoEnabled.value = !isVideoEnabled.value
    ElMessage.info(isVideoEnabled.value ? '摄像头已开启' : '摄像头已关闭')
  }
}

// 离开会议
const leaveMeeting = () => {
  ElMessage.success('已离开会议')
  router.push('/dashboard')
}

// 加入会议
const joinMeeting = async () => {
  try {
    const meetingIdParam = route.params.id as string
    const password = (route.query.pwd as string) || ''

    if (!meetingIdParam) {
      ElMessage.error('会议ID不存在')
      router.push('/dashboard')
      return
    }

    // 调用加入会议 API
    const res = await MeetingApi.joinMeeting({
      meetingId: Number(meetingIdParam),
      password,
      nickName: userStore.userInfo?.nickName || '匿名用户',
    })

    // 为每个已存在的参与者创建连接
    res.data.forEach((userId) => {
      if (userId !== userStore.userInfo!.userId) {
        createPeerConnection(userId)
      }
    })

    console.log('Successfully joined meeting:', meetingIdParam)
  } catch (error: any) {
    console.error('Failed to join meeting:', error)
    ElMessage.error(error?.message || '加入会议失败')
    setTimeout(() => {
      router.push('/dashboard')
    }, 2000)
  }
}

onMounted(async () => {
  try {
    try {
      // localStream.value = await navigator.mediaDevices.getUserMedia({
      //   video: {
      //     width: { ideal: 1280 },
      //     height: { ideal: 720 },
      //   },
      //   audio: {
      //     echoCancellation: true,
      //     noiseSuppression: true,
      //   },
      // })
    } catch (e) {}
    registerMessageHandlers()
    await joinMeeting()
  } catch (error) {
    console.error('Failed to initialize meeting room:', error)
  }
})

// 组件卸载清理
onBeforeUnmount(() => {
  closeAllConnections()
  localStream.value?.getTracks().forEach((track) => {
    track.stop()
  })
  unregisterMessageHandlers()
})
</script>

<template>
  <div class="meeting-room-shell">
    <!-- 视频区域 -->
    <div class="video-area">
      <div class="main-video-container">
        <!-- 本地视频 -->
        <VideoTile
          :user-id="userStore.userInfo?.userId || ''"
          :nick-name="userStore.userInfo?.nickName"
          v-model:stream="localStream"
          :is-local="true"
          :is-audio-enabled="isAudioEnabled"
          :is-video-enabled="isVideoEnabled"
        />

        <!-- 远程参与者视频 -->
        <VideoTile
          v-for="participant in memberList"
          :key="participant.userId"
          :user-id="participant.userId"
          :nick-name="participant.nickName"
          :stream="participant.stream"
          :is-local="false"
        />
      </div>
    </div>

    <!-- 控制栏 -->
    <div class="control-bar">
      <div class="control-buttons">
        <!-- 麦克风 -->
        <el-button
          :type="isAudioEnabled ? 'primary' : 'danger'"
          circle
          size="large"
          @click="toggleAudio"
        >
          <el-icon :size="24">
            <Microphone />
          </el-icon>
        </el-button>

        <!-- 摄像头 -->
        <el-button
          :type="isVideoEnabled ? 'primary' : 'danger'"
          circle
          size="large"
          @click="toggleVideo"
        >
          <el-icon :size="24">
            <VideoCamera />
          </el-icon>
        </el-button>

        <!-- 离开会议 -->
        <el-button type="danger" circle size="large" @click="leaveMeeting">
          <el-icon :size="24">
            <PhoneFilled />
          </el-icon>
        </el-button>
      </div>

      <!-- 会议信息 -->
      <div class="meeting-info">
        <span>会议 ID: {{ route.params.id }}</span>
        <span>参与者: {{ memberList.length + 1 }}</span>
      </div>
    </div>
  </div>
</template>

<style scoped lang="scss">
.meeting-room-shell {
  width: 100vw;
  height: 100vh;
  background: #1a1a1a;
  display: flex;
  flex-direction: column;
  position: relative;
  overflow: hidden;

  .video-area {
    flex: 1;
    display: flex;
    align-items: center;
    justify-content: center;
    padding: 20px;
    overflow: auto;

    .main-video-container {
      display: grid;
      grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
      gap: 16px;
      width: 100%;
      max-width: 1400px;
    }
  }

  .control-bar {
    position: relative;
    bottom: 0;
    left: 0;
    right: 0;
    background: rgba(30, 30, 30, 0.95);
    backdrop-filter: blur(10px);
    padding: 20px;
    display: flex;
    justify-content: space-between;
    align-items: center;
    border-top: 1px solid rgba(255, 255, 255, 0.1);

    .control-buttons {
      display: flex;
      gap: 16px;
      flex: 1;
      justify-content: center;

      .el-button {
        width: 56px;
        height: 56px;
        transition: all 0.3s ease;

        &:hover {
          transform: scale(1.1);
        }

        &.is-circle {
          border-radius: 50%;
        }
      }
    }

    .meeting-info {
      position: absolute;
      right: 20px;
      display: flex;
      flex-direction: column;
      gap: 4px;
      color: #fff;
      font-size: 14px;

      span {
        background: rgba(255, 255, 255, 0.1);
        padding: 4px 12px;
        border-radius: 4px;
      }
    }
  }
}
</style>
