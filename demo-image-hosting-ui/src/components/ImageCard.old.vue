<script setup lang="ts">
import { ref, reactive, computed } from "vue";
import { message, Modal } from "ant-design-vue";
import {
  EyeOutlined,
  DeleteOutlined,
  CloseOutlined,
  CopyOutlined,
  SwapOutlined,
} from "@ant-design/icons-vue";
import { ImageApi } from "@/api";
import * as CONST from "@/const";

const props = withDefaults(
  defineProps<{
    modelValue: VO.ImageVO;
    permissionMask?: bigint;
    isMultiSelectMode?: boolean;
    selected?: boolean;
  }>(),
  {
    isMultiSelectMode: false,
    selected: false,
    permissionMask: () => 0n,
  }
);

const emit = defineEmits<{
  select: [id: string];
  reload: [];
}>();

const showDetailModal = ref(false);
const showFullscreen = ref(false);
const isEditing = ref(false);
const saving = ref(false);
const showConvertModal = ref(false);
const converting = ref(false);
const selectedFormat = ref<string>("");
const selectedItemId = ref<string>(""); // 当前选中查看的图片项ID
const availableTags = ref<string[]>([]);
const loadingTags = ref(false);

const editForm = reactive({
  name: "",
  introduction: "",
  tags: [] as string[],
});

const supportedFormats = [
  { label: "JPEG", value: "image/jpeg" },
  { label: "PNG", value: "image/png" },
  { label: "WEBP", value: "image/webp" },
];

const handleCardClick = () => {
  if (props.isMultiSelectMode) {
    emit("select", props.modelValue.id);
  } else {
    showDetailModal.value = true;
    isEditing.value = false;
    // 初始化为首选项
    selectedItemId.value =
      props.modelValue.firstItemId || props.modelValue.items?.[0]?.id || "";
    // 加载标签列表
    loadTags();
  }
};

// 加载所有可用标签
const loadTags = async () => {
  if (availableTags.value.length > 0) return; // 已经加载过
  try {
    loadingTags.value = true;
    const res = await ImageApi.listTags();
    availableTags.value = res.data || [];
  } catch (error: any) {
    message.error(error.message || "Failed to load tags");
  } finally {
    loadingTags.value = false;
  }
};

// 当前选中查看的图片项
const currentItem = computed(() => {
  if (!selectedItemId.value) return firstItem.value;
  return (
    props.modelValue.items?.find((i) => i.id === selectedItemId.value) ||
    firstItem.value
  );
});

const firstItem = computed(() => {
  return (
    props.modelValue.items?.find(
      (i) => i.id === props.modelValue.firstItemId
    ) || props.modelValue.items?.[0]
  );
});

// 可用的格式列表（基于items中实际存在的格式）
const availableItems = computed(() => {
  return (
    props.modelValue.items
      ?.filter((item) => item.status === "SUCCESS")
      .map((item) => ({
        id: item.id,
        label: item.contentType.split("/")[1].toUpperCase(),
        contentType: item.contentType,
        size: item.size,
        width: item.width,
        height: item.height,
      })) || []
  );
});

// 切换查看的格式
const handleFormatChange = (itemId: string) => {
  selectedItemId.value = itemId;
};

const url = computed(() =>
  currentItem.value?.id
    ? `/api${ImageApi.SERVER_NAME}/image/view/${currentItem.value.id}`
    : ""
);
const thumbnailUrl = computed(() =>
  currentItem.value?.id
    ? `/api${ImageApi.SERVER_NAME}/image/view/thumbnail/${currentItem.value.id}`
    : ""
);
const format = computed(
  () => currentItem.value?.contentType?.split("/")?.[1] || ""
);

const handleDelete = () => {
  Modal.confirm({
    title: "Confirm Delete",
    content: `Are you sure you want to delete picture "${props.modelValue.name}"? This action cannot be undone.`,
    onOk: async () => {
      try {
        await ImageApi.delete(firstItem.value?.idxId as string);
        message.success("Delete successful");
        emit("reload");
      } catch (error) {
        message.error("Failed to delete picture");
      }
    },
  });
};

const handleSaveEdit = async () => {
  try {
    saving.value = true;
    const data: Req.EditImageReq = {
      tags: editForm.tags,
      idxId: firstItem.value?.idxId as string,
      name: editForm.name,
      introduction: editForm.introduction,
    };
    await ImageApi.edit(data);
    emit("reload");
    showDetailModal.value = false;
    message.success("Save successful");
  } catch (error) {
    message.error("Save failed");
  } finally {
    saving.value = false;
  }
};

const handleFullscreenPreview = () => {
  showDetailModal.value = false;
  showFullscreen.value = true;
};

const closeFullscreen = () => {
  showFullscreen.value = false;
};

const handleEdit = () => {
  isEditing.value = true;
  editForm.name = props.modelValue.name;
  editForm.introduction = props.modelValue.introduction || "";
  editForm.tags = [...(props.modelValue.tags || [])];
};

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

const handleShowConvertModal = () => {
  showConvertModal.value = true;
  // 默认选择当前格式以外的第一个格式
  const currentContentType = firstItem.value?.contentType;
  const otherFormat = supportedFormats.find(
    (f) => f.value !== currentContentType
  );
  selectedFormat.value = otherFormat?.value || "image/jpeg";
};

const handleConvert = async () => {
  if (!selectedFormat.value) {
    message.error("Please select a format");
    return;
  }

  if (selectedFormat.value === firstItem.value?.contentType) {
    message.warning("The selected format is the same as the current format");
    return;
  }

  Modal.confirm({
    title: "Confirm Format Conversion",
    content: `Convert "${props.modelValue.name}" to ${selectedFormat.value
      .split("/")[1]
      .toUpperCase()} format?`,
    onOk: async () => {
      try {
        converting.value = true;
        await ImageApi.convert(
          firstItem.value?.idxId as string,
          selectedFormat.value
        );
        message.success("Format conversion started, please wait...");
        showConvertModal.value = false;
        emit("reload");
      } catch (error: any) {
        message.error(error.message || "Format conversion failed");
      } finally {
        converting.value = false;
      }
    },
  });
};

const availableFormats = computed(() => {
  const currentContentType = firstItem.value?.contentType;
  return supportedFormats.filter((f) => f.value !== currentContentType);
});
</script>

<template>
  <div class="picture-card" @click="handleCardClick" v-if="modelValue">
    <img :src="thumbnailUrl || url" :alt="modelValue.name" />
    <div class="picture-overlay" :class="{ 'multi-select': isMultiSelectMode }">
      <div class="picture-info">
        <p class="picture-name">{{ modelValue.name }}</p>
        <p class="picture-size">{{ formatFileSize(firstItem?.size || "0") }}</p>
      </div>
      <div v-if="!isMultiSelectMode" class="picture-actions">
        <a-button
          type="text"
          size="small"
          @click.stop="handleFullscreenPreview"
        >
          <EyeOutlined style="color: #fff" />
        </a-button>
        <a-button
          type="text"
          size="small"
          @click.stop="handleDelete"
          v-if="permissionMask & CONST.PermissionMask.IMAGE_DELETE"
        >
          <DeleteOutlined style="color: #fff" />
        </a-button>
      </div>
    </div>
    ```
    <!-- Detail Modal -->
    <a-modal
      v-model:open="showDetailModal"
      :title="isEditing ? 'Edit Picture Info' : 'Picture Details'"
      :footer="null"
      :width="800"
      :bodyStyle="{ maxHeight: '70vh', overflowY: 'auto', padding: '20px' }"
      @cancel="showDetailModal = false"
    >
      <div class="picture-detail">
        <!-- Format Selector -->
        <div v-if="availableItems.length > 1" class="format-selector">
          <span class="format-selector-label">View Format:</span>
          <a-segmented
            v-model:value="selectedItemId"
            :options="
              availableItems.map((item) => ({
                label: item.label,
                value: item.id,
              }))
            "
            @change="handleFormatChange"
          />
        </div>

        <div class="detail-image">
          <img :src="url" :alt="modelValue.name" class="detail-img" />
        </div>

        <div class="detail-info">
          <div class="info-row">
            <div class="info-label">Name:</div>
            <a-input
              v-if="isEditing"
              v-model:value="editForm.name"
              placeholder="Please enter picture name"
            />
            <div v-else class="info-value">{{ modelValue.name }}</div>
          </div>

          <div class="info-row">
            <span class="info-label">Description:</span>
            <a-textarea
              v-if="isEditing"
              v-model:value="editForm.introduction"
              placeholder="Please enter picture description"
              :rows="3"
            />
            <span v-else class="info-value">{{
              modelValue.introduction || "No description"
            }}</span>
          </div>
          <div class="info-row">
            <span class="info-label">Tags:</span>
            <div v-if="isEditing" class="info-value">
              <a-select
                v-model:value="editForm.tags"
                mode="tags"
                placeholder="Please select or enter tags"
                :loading="loadingTags"
                style="width: 100%"
                :options="
                  availableTags.map((tag) => ({ label: tag, value: tag }))
                "
                allow-clear
              />
            </div>
            <span v-else class="info-value">
              <a-tag v-for="tag in modelValue.tags" :key="tag" color="blue">{{
                tag
              }}</a-tag>
              <span v-if="!modelValue.tags || modelValue.tags.length === 0"
                >No tags</span
              >
            </span>
          </div>
          <div class="info-row">
            <span class="info-label">Size:</span>
            <span class="info-value"
              >{{ currentItem?.width }} × {{ currentItem?.height }}</span
            >
          </div>

          <div class="info-row">
            <span class="info-label">File Size:</span>
            <span class="info-value">{{
              formatFileSize(currentItem?.size || "0")
            }}</span>
          </div>

          <div class="info-row">
            <span class="info-label">Format:</span>
            <span class="info-value">{{ format?.toUpperCase() }}</span>
          </div>

          <div class="info-row">
            <span class="info-label">URL:</span>
            <div class="url-container">
              <div class="url-text" :title="url">{{ url }}</div>
              <a-button
                type="text"
                size="small"
                @click="copyToClipboard(url)"
                class="copy-btn"
              >
                <CopyOutlined />
              </a-button>
            </div>
          </div>

          <div class="info-row">
            <span class="info-label">Thumbnail:</span>
            <div class="url-container">
              <div class="url-text" :title="thumbnailUrl">
                {{ thumbnailUrl }}
              </div>
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
              <a-avatar
                :src="modelValue.creatorAvatar"
                size="small"
                style="margin-right: 8px"
              >
                {{ modelValue.creatorName.charAt(0) }}
              </a-avatar>
              {{ modelValue.creatorName }}
            </span>
          </div>

          <div class="info-row">
            <span class="info-label">Edit Time:</span>
            <span class="info-value">{{
              formatDate(modelValue.updateTime)
            }}</span>
          </div>
        </div>
        <div
          v-if="permissionMask & CONST.PermissionMask.IMAGE_EDIT"
          class="detail-actions"
        >
          <template v-if="isEditing">
            <a-button @click="isEditing = false">Cancel</a-button>
            <a-button type="primary" @click="handleSaveEdit" :loading="saving"
              >Save</a-button
            >
          </template>
          <template v-else>
            <a-button @click="handleEdit">Edit</a-button>
            <a-button @click="handleShowConvertModal">
              <SwapOutlined />
              Convert Format
            </a-button>
            <a-button type="primary" @click="handleFullscreenPreview"
              >Fullscreen Preview</a-button
            >
          </template>
        </div>
        <div v-else class="detail-actions">
          <a-button type="primary" @click="handleFullscreenPreview"
            >Fullscreen Preview</a-button
          >
        </div>
      </div>
    </a-modal>

    <!-- Format Conversion Modal -->
    <a-modal
      v-model:open="showConvertModal"
      title="Convert Image Format"
      @ok="handleConvert"
      @cancel="showConvertModal = false"
      :confirm-loading="converting"
      ok-text="Convert"
      cancel-text="Cancel"
    >
      <div class="convert-modal-content">
        <div class="convert-info">
          <p class="current-format">
            <span class="label">Current Format:</span>
            <a-tag color="blue">{{ format?.toUpperCase() }}</a-tag>
          </p>
          <p class="convert-tip">
            Select the target format to convert this image:
          </p>
        </div>
        <a-select
          v-model:value="selectedFormat"
          style="width: 100%"
          size="large"
          placeholder="Select target format"
        >
          <a-select-option
            v-for="fmt in availableFormats"
            :key="fmt.value"
            :value="fmt.value"
          >
            {{ fmt.label }}
          </a-select-option>
        </a-select>
        <a-alert
          message="Note"
          description="Format conversion may take some time. The page will automatically refresh after conversion is complete."
          type="info"
          show-icon
          style="margin-top: 16px"
        />
      </div>
    </a-modal>

    <!-- Fullscreen Preview Overlay -->
    <div
      v-if="showFullscreen"
      class="fullscreen-overlay"
      @click="closeFullscreen"
    >
      <div class="fullscreen-content" @click.stop>
        <img :src="url" :alt="modelValue.name" class="fullscreen-image" />
        <div class="fullscreen-controls">
          <a-button type="text" size="large" @click="closeFullscreen">
            <CloseOutlined style="color: #fff; font-size: 24px" />
          </a-button>
        </div>
      </div>
    </div>
    ```
  </div>
</template>

<style scoped lang="scss">
.picture-card {
  position: relative;
  aspect-ratio: 1;
  border-radius: 8px;
  overflow: hidden;
  cursor: pointer;
  transition: all 0.3s ease;
  border: 2px solid transparent;

  &.selected {
    border-color: #1890ff;
    box-shadow: 0 0 0 2px rgba(24, 144, 255, 0.2);
  }

  img {
    width: 100%;
    height: 100%;
    object-fit: cover;
    transition: transform 0.3s ease;
  }

  .picture-overlay {
    position: absolute;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
    background: rgba(0, 0, 0, 0.6);
    color: #fff;
    opacity: 0;
    transition: opacity 0.3s ease;
    display: flex;
    flex-direction: column;
    justify-content: space-between;
    padding: 12px;

    &.multi-select {
      opacity: 0.3;
    }

    .picture-info {
      .picture-name {
        margin: 0 0 4px 0;
        font-size: 14px;
        font-weight: 500;
        white-space: nowrap;
        overflow: hidden;
        text-overflow: ellipsis;
      }

      .picture-size {
        margin: 0;
        font-size: 12px;
        opacity: 0.8;
      }
    }

    .picture-actions {
      display: flex;
      gap: 8px;
      justify-content: flex-end;
    }
  }

  &:hover {
    > img {
      transform: scale(1.05);
    }

    .picture-overlay {
      opacity: 1;
    }
  }
}

.picture-detail {
  .format-selector {
    display: flex;
    align-items: center;
    gap: 12px;
    margin-bottom: 20px;
    padding: 12px;
    background: #f5f5f5;
    border-radius: 8px;

    .format-selector-label {
      font-size: 14px;
      font-weight: 500;
      color: #666;
    }

    :deep(.ant-segmented) {
      background: #fff;
    }
  }

  .detail-image {
    text-align: center;
    margin-bottom: 20px;

    .detail-img {
      max-width: 100%;
      max-height: 300px;
      border-radius: 8px;
      box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
    }
  }
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

      .ant-input,
      .ant-input-affix-wrapper {
        flex: 1;
      }
    }
  }

  .detail-actions {
    margin-top: 20px;
    text-align: right;

    .ant-btn {
      margin-left: 8px;
    }
  }
}

.convert-modal-content {
  .convert-info {
    margin-bottom: 20px;

    .current-format {
      margin-bottom: 12px;
      font-size: 14px;
      display: flex;
      align-items: center;
      gap: 8px;

      .label {
        color: #666;
        font-weight: 500;
      }
    }

    .convert-tip {
      margin-bottom: 12px;
      color: #666;
      font-size: 13px;
    }
  }
}

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
````
