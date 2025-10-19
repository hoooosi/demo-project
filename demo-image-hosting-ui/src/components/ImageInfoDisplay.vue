<script setup lang="ts">
import { computed } from "vue";
import { message } from "ant-design-vue";
import { CopyOutlined } from "@ant-design/icons-vue";

const props = defineProps<{
  image: VO.ImageVO;
  currentItem?: VO.ImageVO["items"][number];
  url: string;
  thumbnailUrl: string;
}>();

const format = computed(() => props.currentItem?.contentType?.split("/")?.[1] || "");

const formatFileSize = (size: string): string => {
  const units = ["B", "KB", "MB", "GB"];
  let _size = Number(size);
  let index = 0;
  while (_size >= 1024 && index < units.length - 1) {
    _size /= 1024;
    index++;
  }
  return `${_size.toFixed(1)} ${units[index]}`;
};

const formatDate = (dateString: string): string => {
  if (!dateString) return "Unknown time";
  const date = new Date(dateString);
  return date.toLocaleString("zh-CN");
};

const copyToClipboard = async (text: string) => {
  try {
    await navigator.clipboard.writeText(text);
    message.success("Copy successful");
  } catch (error) {
    message.error("Copy failed");
  }
};
</script>

<template>
  <div class="detail-info">
    <div class="info-row">
      <div class="info-label">Name:</div>
      <div class="info-value">{{ image.name }}</div>
    </div>

    <div class="info-row">
      <span class="info-label">Description:</span>
      <span class="info-value">{{ image.introduction || "No description" }}</span>
    </div>

    <div class="info-row">
      <span class="info-label">Tags:</span>
      <span class="info-value">
        <a-tag v-for="tag in image.tags" :key="tag" color="blue">{{ tag }}</a-tag>
        <span v-if="!image.tags || image.tags.length === 0">No tags</span>
      </span>
    </div>

    <div class="info-row">
      <span class="info-label">Size:</span>
      <span class="info-value">{{ currentItem?.width }} × {{ currentItem?.height }}</span>
    </div>

    <div class="info-row">
      <span class="info-label">File Size:</span>
      <span class="info-value">{{ formatFileSize(currentItem?.size || "0") }}</span>
    </div>

    <div class="info-row">
      <span class="info-label">Format:</span>
      <span class="info-value">{{ format?.toUpperCase() }}</span>
    </div>

    <div class="info-row">
      <span class="info-label">URL:</span>
      <div class="url-container">
        <div class="url-text" :title="url">{{ url }}</div>
        <a-button type="text" size="small" @click="copyToClipboard(url)" class="copy-btn">
          <CopyOutlined />
        </a-button>
      </div>
    </div>

    <div class="info-row">
      <span class="info-label">Thumbnail:</span>
      <div class="url-container">
        <div class="url-text" :title="thumbnailUrl">{{ thumbnailUrl }}</div>
        <a-button
          type="text"
          size="small"
          @click="copyToClipboard(thumbnailUrl || 'None')"
          class="copy-btn"
        >
          <CopyOutlined />
        </a-button>
      </div>
    </div>

    <div class="info-row">
      <span class="info-label">Creator:</span>
      <span class="info-value">
        <a-avatar :src="image.creatorAvatar" size="small" style="margin-right: 8px">
          {{ image.creatorName.charAt(0) }}
        </a-avatar>
        {{ image.creatorName }}
      </span>
    </div>

    <div class="info-row">
      <span class="info-label">Edit Time:</span>
      <span class="info-value">{{ formatDate(image.updateTime) }}</span>
    </div>
  </div>
</template>

<style scoped lang="scss">
.detail-info {
  max-height: 400px;
  overflow-y: auto;
  padding-right: 8px;

  &::-webkit-scrollbar {
    width: 6px;
  }

  &::-webkit-scrollbar-thumb {
    background: #d9d9d9;
    border-radius: 3px;

    &:hover {
      background: #bfbfbf;
    }
  }

  &::-webkit-scrollbar-track {
    background: #f5f5f5;
    border-radius: 3px;
  }

  .info-row {
    display: flex;
    margin-bottom: 16px;
    width: 100%;

    .info-label {
      text-align: center;
      width: 80px;
      color: #666;
      font-weight: 500;
      flex-shrink: 0;
    }

    .info-value {
      flex: 1;
      color: #333;
      word-break: break-all;

      .ant-tag {
        margin: 0 4px 4px 0;
      }
    }

    .url-container {
      flex: 1;
      max-width: calc(100% - 80px);
      display: flex;
      justify-content: space-between;
      align-items: center;
      gap: 8px;

      .url-text {
        flex: 1;
        color: #333;
        white-space: nowrap;
        overflow: hidden;
        text-overflow: ellipsis;
        user-select: all;
        cursor: text;
        padding: 4px 8px;
        border: 1px solid #d9d9d9;
        border-radius: 4px;
        background-color: #fafafa;
        font-family: "Courier New", monospace;
        font-size: 12px;

        &:hover {
          border-color: #40a9ff;
        }
      }

      .copy-btn {
        flex-shrink: 0;
        color: #666;

        &:hover {
          color: #1890ff;
        }
      }
    }
  }
}
</style>
