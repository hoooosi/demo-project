<script setup lang="ts">
import { ref, watchEffect, computed } from 'vue'

const videoShellRef = ref<HTMLVideoElement>()
const userId = defineModel<string>('userId')
const nickName = defineModel<string | undefined>('nickName')
const stream = defineModel<MediaStream | undefined>('stream')
const isLocal = defineModel<boolean>('isLocal', { default: false })
const isMaximized = defineModel<boolean>('isMaximized', { default: false })

const displayName = computed(() => {
  if (isLocal.value) return `${nickName.value || 'ME'} (ME)`
  return nickName.value || userId.value
})

watchEffect(async () => {
  const currentStream = stream.value
  const videoShell = videoShellRef.value
  if (currentStream && videoShell) {
    const existingVideo = videoShell.querySelector('video')
    if (existingVideo) videoShell.removeChild(existingVideo)
    const video = document.createElement('video')
    video.muted = true
    video.autoplay = true
    video.srcObject = currentStream
    video.play()
    videoShell.appendChild(video)
  }
})
</script>

<template>
  <div class="video-tile" :class="{ 'local-video': isLocal, maximized: isMaximized }">
    <div class="stream-info">{{ stream }}</div>
    <div class="video-shell" ref="videoShellRef"></div>
    <div class="video-info">
      <span class="name">{{ displayName }}</span>
      <span class="maximize-hint" v-if="!isMaximized">Click to Maximize</span>
      <span class="maximize-hint" v-else>Click to Restore</span>
    </div>
  </div>
</template>

<style scoped lang="scss">
.video-tile {
  background-color: #2c2c2c;
  position: relative;
  width: 100%;
  aspect-ratio: 16 / 9;
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.3);
  transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);
  min-height: 0;
  cursor: pointer;

  &:hover {
    transform: scale(1.02);
    box-shadow: 0 8px 24px rgba(0, 0, 0, 0.5);

    .maximize-hint {
      opacity: 1;
    }
  }

  &.maximized {
    position: fixed;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
    z-index: 100;
    border: none !important;
    border-radius: 0;
    box-shadow: 0 20px 60px rgba(0, 0, 0, 0.8);
    animation: maximizeIn 0.4s cubic-bezier(0.4, 0, 0.2, 1);

    &:hover {
      transform: none;
    }
  }

  .stream-info {
    position: absolute;
    top: 8px;
    left: 8px;
    color: #fff;
    font-size: 12px;
    background: rgba(0, 0, 0, 0.5);
    padding: 2px 6px;
    border-radius: 4px;
    z-index: 10;
  }

  .video-shell {
    display: flex;
    width: 100%;
    height: 100%;
    background-color: #aac1c3;
    justify-content: center;
    video {
      object-fit: cover;
      flex: 1;
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
    transition: all 0.3s ease;

    .name {
      color: #fff;
      font-size: 14px;
      font-weight: 500;
      text-overflow: ellipsis;
      overflow: hidden;
      white-space: nowrap;
    }

    .maximize-hint {
      color: rgba(255, 255, 255, 0.8);
      font-size: 12px;
      opacity: 0;
      transition: opacity 0.3s ease;
      background: rgba(64, 158, 255, 0.3);
      padding: 2px 8px;
      border-radius: 4px;
    }
  }

  &.local-video {
    border: 2px solid #409eff;
  }
}

@keyframes maximizeIn {
  from {
    opacity: 0.8;
    transform: scale(0.95);
  }
  to {
    opacity: 1;
    transform: scale(1);
  }
}
</style>
