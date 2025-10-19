<script setup lang="ts">
import { ref, computed } from "vue";
import { message, Modal } from "ant-design-vue";
import { ImageApi } from "@/api";

const props = defineProps<{
  currentItem?: VO.ImageVO["items"][number];
}>();

const emit = defineEmits<{
  reload: [];
}>();

const open = defineModel<boolean>("open", { default: false });
const converting = ref(false);
const selectedFormat = ref<string>("");

const supportedFormats = [
  { label: "JPEG", value: "image/jpeg" },
  { label: "PNG", value: "image/png" },
  { label: "WEBP", value: "image/webp" },
];

const format = computed(() => props.currentItem?.contentType?.split("/")?.[1] || "");

const availableFormats = computed(() => {
  const currentContentType = props.currentItem?.contentType;
  return supportedFormats.filter((f) => f.value !== currentContentType);
});

const handleConvert = async () => {
  if (!selectedFormat.value) {
    message.error("Please select a format");
    return;
  }

  if (selectedFormat.value === props.currentItem?.contentType) {
    message.warning("The selected format is the same as the current format");
    return;
  }

  Modal.confirm({
    title: "Confirm Format Conversion",
    content: `Convert to ${selectedFormat.value.split("/")[1].toUpperCase()} format?`,
    onOk: async () => {
      try {
        converting.value = true;
        await ImageApi.convert(props.currentItem?.idxId as string, selectedFormat.value);
        message.success("Format conversion started, please wait...");
        open.value = false;
        emit("reload");
      } catch (error: any) {
        message.error(error.message || "Format conversion failed");
      } finally {
        converting.value = false;
      }
    },
  });
};

// 监听弹窗打开，设置默认格式
const handleOpen = () => {
  if (open.value) {
    const currentContentType = props.currentItem?.contentType;
    const otherFormat = supportedFormats.find((f) => f.value !== currentContentType);
    selectedFormat.value = otherFormat?.value || "image/jpeg";
  }
};

// Watch for open changes
import { watch } from "vue";
watch(open, handleOpen);
</script>

<template>
  <a-modal
    v-model:open="open"
    title="Convert Image Format"
    @ok="handleConvert"
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
        <p class="convert-tip">Select the target format to convert this image:</p>
      </div>
      <a-select
        v-model:value="selectedFormat"
        style="width: 100%"
        size="large"
        placeholder="Select target format"
      >
        <a-select-option v-for="fmt in availableFormats" :key="fmt.value" :value="fmt.value">
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
</template>

<style scoped lang="scss">
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
</style>
