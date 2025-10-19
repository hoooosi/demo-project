<script setup lang="ts">
import { ref, computed, watch } from "vue";
import { message } from "ant-design-vue";
import { SwapOutlined } from "@ant-design/icons-vue";
import ImageEditForm from "./ImageEditForm.vue";
import ImageInfoDisplay from "./ImageInfoDisplay.vue";
import FormatConvertModal from "./FormatConvertModal.vue";
import { ImageApi } from "@/api";
import * as CONST from "@/const";

const props = defineProps<{
  image: VO.ImageVO;
  permissionMask?: bigint;
}>();

const emit = defineEmits<{
  reload: [];
  fullscreen: [];
}>();

const open = defineModel<boolean>("open", { default: false });
const isEditing = ref(false);
const saving = ref(false);
const showConvertModal = ref(false);
const selectedItemId = ref<string>("");
const availableTags = ref<string[]>([]);
const loadingTags = ref(false);

// 当前选中的图片项
const currentItem = computed(() => {
  if (!selectedItemId.value) return firstItem.value;
  return props.image.items?.find((i) => i.id === selectedItemId.value) || firstItem.value;
});

const firstItem = computed(() => {
  return (
    props.image.items?.find((i) => i.id === props.image.firstItemId) ||
    props.image.items?.[0]
  );
});

// 可用的格式列表
const availableItems = computed(() => {
  return (
    props.image.items
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

// 加载标签
const loadTags = async () => {
  if (availableTags.value.length > 0) return;
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

// 保存编辑
const handleSave = async (data: { name: string; introduction: string; tags: string[] }) => {
  try {
    saving.value = true;
    await ImageApi.edit({
      idxId: firstItem.value?.idxId as string,
      ...data,
    });
    emit("reload");
    open.value = false;
    message.success("Save successful");
  } catch (error) {
    message.error("Save failed");
  } finally {
    saving.value = false;
  }
};

// 切换格式
const handleFormatChange = (itemId: string) => {
  selectedItemId.value = itemId;
};

// 监听弹窗打开
watch(open, (val) => {
  if (val) {
    selectedItemId.value = props.image.firstItemId || props.image.items?.[0]?.id || "";
    isEditing.value = false;
    loadTags();
  }
});
</script>

<template>
  <a-modal
    v-model:open="open"
    :title="isEditing ? 'Edit Picture Info' : 'Picture Details'"
    :footer="null"
    :width="800"
    :bodyStyle="{ maxHeight: '70vh', overflowY: 'auto', padding: '20px' }"
  >
    <div class="picture-detail">
      <!-- Format Selector -->
      <div v-if="availableItems.length > 1" class="format-selector">
        <span class="format-selector-label">View Format:</span>
        <a-segmented
          v-model:value="selectedItemId"
          :options="availableItems.map((item) => ({ label: item.label, value: item.id }))"
          @change="handleFormatChange"
        />
      </div>

      <!-- Image Preview -->
      <div class="detail-image">
        <img :src="url" :alt="image.name" class="detail-img" />
      </div>

      <!-- Edit Form or Info Display -->
      <ImageEditForm
        v-if="isEditing"
        :image="image"
        :available-tags="availableTags"
        :loading-tags="loadingTags"
        :saving="saving"
        @save="handleSave"
        @cancel="isEditing = false"
      />
      <ImageInfoDisplay
        v-else
        :image="image"
        :current-item="currentItem"
        :url="url"
        :thumbnail-url="thumbnailUrl"
      />

      <!-- Actions -->
      <div
        v-if="permissionMask && permissionMask & CONST.PermissionMask.IMAGE_EDIT"
        class="detail-actions"
      >
        <template v-if="isEditing">
          <a-button @click="isEditing = false">Cancel</a-button>
          <a-button type="primary" :loading="saving" @click="() => {}">Save</a-button>
        </template>
        <template v-else>
          <a-button @click="isEditing = true">Edit</a-button>
          <a-button @click="showConvertModal = true">
            <template #icon><SwapOutlined /></template>
            Convert Format
          </a-button>
          <a-button type="primary" @click="emit('fullscreen')">Fullscreen Preview</a-button>
        </template>
      </div>
      <div v-else class="detail-actions">
        <a-button type="primary" @click="emit('fullscreen')">Fullscreen Preview</a-button>
      </div>
    </div>

    <!-- Format Conversion Modal -->    <FormatConvertModal
      v-model:open="showConvertModal"
      :current-item="firstItem"
      @reload="emit('reload')"
    />
  </a-modal>
</template>

<style scoped lang="scss">
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

  .detail-actions {
    margin-top: 20px;
    text-align: right;

    .ant-btn {
      margin-left: 8px;
    }
  }
}
</style>
