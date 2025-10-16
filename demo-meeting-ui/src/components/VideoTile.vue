<script setup lang="ts">
import { ref, watchEffect, computed, onMounted, watch, nextTick } from 'vue'
import { Microphone, VideoCamera } from '@element-plus/icons-vue'
import { createMediaStream } from '@/utils/track'

const videoShellRef = ref<HTMLVideoElement>()
const userId = defineModel<string>('userId')
const nickName = defineModel<string | undefined>('nickName')
const stream = defineModel<MediaStream | undefined>('stream')
const isLocal = defineModel<boolean>('isLocal', { default: false })
const isAudioEnabled = defineModel<boolean>('isAudioEnabled', { default: true })
const isVideoEnabled = defineModel<boolean>('isVideoEnabled', { default: true })

const displayName = computed(() => {
  if (isLocal.value) return `${nickName.value || '我'} (我)`
  return nickName.value || userId.value
})

const createVideo = async (stream: MediaStream) => {
  if (!videoShellRef.value) return
  const existingVideo = videoShellRef.value.querySelector('video')
  if (existingVideo) videoShellRef.value.removeChild(existingVideo)

  const video = document.createElement('video')
  video.muted = true
  video.autoplay = true
  video.srcObject = stream
  video.play()
  videoShellRef.value.appendChild(video)
}

watchEffect(async () => {
  const currentStream = stream.value
  const videoElement = videoShellRef.value
  if (currentStream && videoElement) await createVideo(currentStream)
})
</script>

<template>
  <div class="video-tile" :class="{ 'local-video': isLocal }">
    <div class="video-shell" ref="videoShellRef"></div>
    <div class="video-info">
      <span class="name">{{ displayName }}</span>
      <div class="status-icons">
        <el-icon v-if="!isAudioEnabled" class="status-icon muted">
          <Microphone />
        </el-icon>
        <el-icon v-if="!isVideoEnabled" class="status-icon video-off">
          <VideoCamera />
        </el-icon>
      </div>
    </div>
  </div>
</template>

<style scoped lang="scss">
.video-tile {
  background-color: #2c2c2c;
  position: relative;
  aspect-ratio: 16 / 9;
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.3);
  transition: all 0.3s ease;

  &:hover {
    box-shadow: 0 6px 16px rgba(0, 0, 0, 0.4);
  }

  .video-shell {
    width: 100%;
    height: 100%;
    object-fit: cover;
    background-color: #aac1c3;
    video {
      width: 100%;
      height: 100%;
      object-fit: cover;
    }
  }

  .video-info {
    position: absolute;
    bottom: 12px;
    left: 12px;
    right: 12px;
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 8px 12px;
    background: rgba(0, 0, 0, 0.6);
    backdrop-filter: blur(4px);
    border-radius: 6px;

    .name {
      color: #fff;
      font-size: 14px;
      font-weight: 500;
      text-overflow: ellipsis;
      overflow: hidden;
      white-space: nowrap;
    }

    .status-icons {
      display: flex;
      gap: 8px;
      flex-shrink: 0;

      .status-icon {
        color: #ff4d4f;
        font-size: 18px;

        &.muted {
          color: #ff4d4f;
        }

        &.video-off {
          color: #faad14;
        }
      }
    }
  }

  &.local-video {
    border: 2px solid #409eff;
  }
}
</style>
