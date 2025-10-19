<script setup lang="ts">
import { ref, computed } from "vue";
import { message, Modal } from "ant-design-vue";
import { EyeOutlined, DeleteOutlined } from "@ant-design/icons-vue";
import ImageDetailModal from "./ImageDetailModal.vue";
import ImageFullscreen from "./ImageFullscreen.vue";
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

const firstItem = computed(() => {
  return (
    props.modelValue.items?.find((i) => i.id === props.modelValue.firstItemId) ||
    props.modelValue.items?.[0]
  );
});

const thumbnailUrl = computed(() =>
  firstItem.value?.id
    ? `/api${ImageApi.SERVER_NAME}/image/view/thumbnail/${firstItem.value.id}`
    : ""
);

const url = computed(() =>
  firstItem.value?.id ? `/api${ImageApi.SERVER_NAME}/image/view/${firstItem.value.id}` : ""
);

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

const handleCardClick = () => {
  if (props.isMultiSelectMode) {
    emit("select", props.modelValue.id);
  } else {
    showDetailModal.value = true;
  }
};

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

const handleFullscreen = () => {
  showDetailModal.value = false;
  showFullscreen.value = true;
};
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
        <a-button type="text" size="small" @click.stop="showFullscreen = true">
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

    <!-- Detail Modal -->
    <ImageDetailModal
      v-model:open="showDetailModal"
      :image="modelValue"
      :permission-mask="permissionMask"
      @reload="emit('reload')"
      @fullscreen="handleFullscreen"
    />

    <!-- Fullscreen Preview -->
    <ImageFullscreen v-model:open="showFullscreen" :url="url" :alt="modelValue.name" />
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
</style>
