<script setup lang="ts">
import { CloseOutlined } from "@ant-design/icons-vue";

defineProps<{
  url: string;
  alt: string;
}>();

const open = defineModel<boolean>("open", { default: false });
</script>

<template>
  <div v-if="open" class="fullscreen-overlay" @click="open = false">
    <div class="fullscreen-content" @click.stop>
      <img :src="url" :alt="alt" class="fullscreen-image" />
      <div class="fullscreen-controls">
        <a-button type="text" size="large" @click="open = false">
          <CloseOutlined style="color: #fff; font-size: 24px" />
        </a-button>
      </div>
    </div>
  </div>
</template>

<style scoped lang="scss">
.fullscreen-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.9);
  z-index: 9999;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;

  .fullscreen-content {
    display: flex;
    position: relative;
    width: 100%;
    height: 100%;
    cursor: default;

    .fullscreen-image {
      margin: auto;
      height: auto;
      width: auto;
      max-width: 100%;
      max-height: 100%;
      object-fit: contain;
      border-radius: 8px;
    }

    .fullscreen-controls {
      position: absolute;
      top: 20px;
      right: 20px;

      .ant-btn {
        aspect-ratio: 1;
        border: none;

        &:hover {
          scale: 1.2;
        }
      }
    }
  }
}
</style>
