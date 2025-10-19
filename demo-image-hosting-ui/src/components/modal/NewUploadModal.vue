<template>
  <a-button type="primary" @click="visable = true">
    <UploadOutlined />UPLOAD
  </a-button>
  <a-modal
    :open="visable"
    @cancel="visable = false"
    title="UPLOAD"
    :footer="null"
  >
    <a-upload-dragger
      v-model:fileList="fileList"
      multiple
      name="file"
      :customRequest="handleCustomUpload"
      @change="handleUploadChange"
    >
      <p class="ant-upload-drag-icon">
        <InboxOutlined />
      </p>
      <p class="ant-upload-text">Click or drag files to this area to upload</p>
      <p class="ant-upload-hint">
        Support single or batch upload of image files
      </p>
    </a-upload-dragger>
  </a-modal>
</template>

<script setup lang="ts">
import { ref, watch } from "vue";
import { message, Upload } from "ant-design-vue";
import type { UploadChangeParam, FormInstance } from "ant-design-vue";
import {
  InboxOutlined,
  UploadOutlined,
  EditOutlined,
} from "@ant-design/icons-vue";
import { ImageApi } from "@/api";
const props = defineProps<{
  sid: string;
}>();
const emit = defineEmits<{
  reload: [];
}>();
const visable = ref(false);
const fileList = ref([]);

watch(
  () => visable,
  (val) => {
    if (val) fileList.value = [];
  }
);

async function handleCustomUpload(options: any) {
  try {
    const res = await ImageApi.upload(options.file, props.sid);
    options.onSuccess(res.message, options);
  } catch (error) {
    options.onError(error as Error);
  }
}

async function handleUploadChange(info: UploadChangeParam) {
  if (info.file.status === "done") {
    message.success(`${info.file.name} UPLOAD SUCCESS`);
    emit("reload");
  } else if (info.file.status === "error") {
    message.error(`${info.file.name} UPLOAD FAILED`);
  }
}
</script>

<style scoped lang="scss">

</style>
