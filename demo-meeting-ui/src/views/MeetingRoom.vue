<script setup lang="ts">
import { ref, onMounted, onBeforeUnmount, watch, computed, watchEffect } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import {
  Microphone,
  VideoCamera,
  PhoneFilled,
  Monitor,
  ChatDotRound,
} from '@element-plus/icons-vue'
import { useUserStore } from '@/stores/user'
import { MeetingApi } from '@/api'
import { useWebRTC } from '@/utils/rtc'
import VideoTile from '@/components/VideoTile.vue'
import ChatPanel from '@/components/ChatPanel.vue'
import { getCameraStream, getScreenStream } from '@/utils/track'
import { wsManager } from '@/utils/websocket'
const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

// Stream
const stream = ref<MediaStream>()
const isAudioEnabled = ref(true)
const isVideoEnabled = ref(true)
const isScreenSharing = ref(false)
const isChatOpen = ref(false)
const { localMap, cleanup, createRTC, setStream } = useWebRTC()

// Maximize video state
const maximizedUserId = ref<string | null>(null)

const memberList = computed(() => {
  return Array.from(localMap.values())
})

// Toggle maximize video
const toggleMaximize = (userId: string) => {
  if (maximizedUserId.value === userId) {
    maximizedUserId.value = null
  } else {
    maximizedUserId.value = userId
  }
}

// Check if video is maximized
const isMaximized = (userId: string) => {
  return maximizedUserId.value === userId
}

// Switch audio
const toggleAudio = () => {
  if (stream.value) {
    const audioTracks = stream.value.getAudioTracks()
    audioTracks.forEach((track) => {
      track.enabled = !track.enabled
    })
    isAudioEnabled.value = !isAudioEnabled.value
  }
}

// Switch video
const toggleVideo = () => {
  if (stream.value) {
    const videoTracks = stream.value.getVideoTracks()
    videoTracks.forEach((track) => {
      track.enabled = !track.enabled
    })
    isVideoEnabled.value = !isVideoEnabled.value
  }
}

// Toggle screen sharing
const toggleScreenSharing = async () => {
  if (isScreenSharing.value) {
    stream.value?.getTracks().forEach((track) => track.stop())
    isScreenSharing.value = false
    stream.value = await getCameraStream()
  } else {
    startScreenSharing()
  }
}

// Leave meeting
const leaveMeeting = () => {
  router.push('/dashboard')
}

// Toggle chat panel
const toggleChat = () => {
  isChatOpen.value = !isChatOpen.value
}

// Join meeting
const joinMeeting = async () => {
  try {
    const meetingIdParam = route.params.id as string
    const password = (route.query.pwd as string) || ''

    if (!meetingIdParam) {
      ElMessage.error('Meeting ID does not exist')
      router.push('/dashboard')
      return
    }

    // Call join meeting API
    const res = await MeetingApi.joinMeeting({
      meetingId: Number(meetingIdParam),
      password,
      nickName: userStore.userInfo?.nickName || 'Anonymous',
    })

    // Create connections for each existing participant
    res.data.forEach((userId) => {
      if (userId !== userStore.userInfo!.userId) {
        createRTC(userId)
      }
    })
  } catch (error: unknown) {
    throw new Error('Join meeting failed')
  }
}

const startScreenSharing = async () => {
  try {
    const screenStream = await getScreenStream()
    isScreenSharing.value = true
    stream.value?.getTracks().forEach((track) => track.stop())
    stream.value = screenStream
  } catch (e) {
    ElMessage.error('Getting screen stream failed')
  }
}

watchEffect(() => {
  if (stream.value) setStream(stream.value)
})

onMounted(async () => {
  try {
    stream.value = await getCameraStream()
    await wsManager.waitForConnection()
    await joinMeeting()
  } catch (e: any) {
    ElMessage.error((e as Error).message)
    setTimeout(() => {
      router.push('/dashboard')
    }, 2000)
  }
})

onBeforeUnmount(cleanup)
</script>

<template>
  <div class="meeting-room-shell">
    <!-- VIDEO AREA -->
    <div class="video-area" :class="{ 'chat-open': isChatOpen }">
      <div class="main-video-container" :class="{ 'has-maximized': maximizedUserId !== null }">
        <!-- LOCAL VIDEO -->
        <VideoTile
          :user-id="userStore.userInfo?.userId || ''"
          :nick-name="userStore.userInfo?.nickName"
          v-model:stream="stream"
          :is-local="true"
          :is-audio-enabled="isAudioEnabled"
          :is-video-enabled="isVideoEnabled"
          :is-maximized="isMaximized(userStore.userInfo?.userId || '')"
          @click="toggleMaximize(userStore.userInfo?.userId || '')"
        />

        <!-- REMOTE PARTICIPANT VIDEO -->
        <VideoTile
          v-for="participant in memberList"
          :key="participant.userId"
          :user-id="participant.userId"
          :nick-name="undefined"
          :stream="participant.stream"
          :is-local="false"
          :is-maximized="isMaximized(participant.userId)"
          @click="toggleMaximize(participant.userId)"
        />
      </div>
    </div>

    <!-- CHAT PANEL -->
    <div class="chat-sidebar" :class="{ open: isChatOpen }">
      <ChatPanel />
    </div>

    <!-- CONTROL BAR -->
    <div class="control-bar">
      <div class="control-buttons">
        <!-- MICROPHONE -->
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

        <!-- CAMERA -->
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

        <!-- SCREEN SHARING -->
        <el-button
          :type="isScreenSharing ? 'success' : 'info'"
          circle
          size="large"
          @click="toggleScreenSharing"
        >
          <el-icon :size="24">
            <Monitor />
          </el-icon>
        </el-button>

        <!-- CHAT -->
        <el-button :type="isChatOpen ? 'success' : 'info'" circle size="large" @click="toggleChat">
          <el-icon :size="24">
            <ChatDotRound />
          </el-icon>
        </el-button>

        <!-- LEAVE MEETING -->
        <el-button type="danger" circle size="large" @click="leaveMeeting">
          <el-icon :size="24">
            <PhoneFilled />
          </el-icon>
        </el-button>
      </div>

      <!-- MEETING INFO -->
      <div class="meeting-info">
        <span>Meeting ID: {{ route.params.id }}</span>
        <span>Participants: {{ memberList.length + 1 }}</span>
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
    overflow-y: hidden;
    transition: margin-right 0.3s ease;

    &.chat-open {
      margin-right: 360px;
    }

    .main-video-container {
      display: grid;
      grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
      grid-auto-rows: minmax(200px, auto);
      gap: 16px;
      width: 100%;
      padding: 20px;
      overflow-y: auto;
      overflow-x: hidden;
      align-content: start;
      position: relative;
      transition: all 0.3s ease;

      &.has-maximized {
        display: flex;
        align-items: center;
        justify-content: center;
      }
    }
  }

  .chat-sidebar {
    position: fixed;
    right: -360px;
    top: 0;
    bottom: 0;
    width: 360px;
    background: #1e1e1e;
    border-left: 1px solid rgba(255, 255, 255, 0.1);
    transition: right 0.3s ease;
    z-index: 100;

    &.open {
      right: 0;
    }
  }

  .control-bar {
    position: relative;
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
