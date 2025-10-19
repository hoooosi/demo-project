<template>
  <a-button type="primary" @click="visable = true">
    <UploadOutlined />UPLOAD
  </a-button>
  <a-modal
    :open="visable"
    @cancel="handleCancel"
    title="UPLOAD"
    :footer="null"
    width="600px"
  >
    <a-upload-dragger
      v-model:file-list="fileList"
      :before-upload="beforeUpload"
      :custom-request="handleUpload"
      :multiple="true"
      accept="image/*"
      @remove="handleRemove"
    >
      <p class="ant-upload-drag-icon">
        <inbox-outlined />
      </p>
      <p class="ant-upload-text">Click or drag image to this area to upload</p>
      <p class="ant-upload-hint">
        Support for a single or bulk upload. Only image files are supported.
      </p>
    </a-upload-dragger>
  </a-modal>
</template>

<script setup lang="ts">
import { ref } from "vue";
import { message } from "ant-design-vue";
import type { UploadProps } from "ant-design-vue";
import { UploadOutlined, InboxOutlined } from "@ant-design/icons-vue";
import { ImageApi } from "@/api";
import axios from "axios";
import * as Utils from "@/utils";

const props = defineProps<{
  spaceId: string;
}>();

const emit = defineEmits<{
  reload: [];
}>();

const visable = ref(false);
const fileList = ref<any[]>([]);

const beforeUpload: UploadProps["beforeUpload"] = (file) => {
  const isImage = file.type.startsWith("image/");
  if (!isImage) {
    message.error("You can only upload image files!");
    return false;
  }

  const isLt10M = file.size / 1024 / 1024 < 10;
  if (!isLt10M) {
    message.error("Image must be smaller than 10MB!");
    return false;
  }

  return true;
};

async function handleUpload(options: any) {
  const { file, onSuccess, onError, onProgress } = options;

  try {
    onProgress({ percent: 10 });
    const md5 = await Utils.calculateFileMD5(file);

    onProgress({ percent: 30 });

    const checkRes = await ImageApi.checkUpload(props.spaceId, {
      filename: file.name,
      contentType: file.type,
      md5: md5,
      size: file.size,
    });

    if (checkRes.data.uploaded) {
      message.success({ content: "Upload successful!", key: file.uid });
      onSuccess(checkRes.data, file);
      emit("reload");
      return;
    }

    onProgress({ percent: 50 });
    const formData = new FormData();
    if (checkRes.data.formData) {
      Object.keys(checkRes.data.formData).forEach((key) => {
        formData.append(key, checkRes.data.formData[key]);
      });
    }
    formData.append("file", file);

    await axios.post(checkRes.data.url, formData, {
      headers: {
        "Content-Type": "multipart/form-data",
      },
      onUploadProgress: (progressEvent) => {
        const percent = progressEvent.total
          ? Math.round((progressEvent.loaded * 50) / progressEvent.total) + 50
          : 50;
        onProgress({ percent });
      },
    });

    message.success({ content: "Upload successful!", key: file.uid });
    onSuccess(checkRes.data, file);
    emit("reload");
  } catch (error: any) {
    message.error({
      content: error.message || "Upload failed!",
      key: file.uid,
    });
    onError(error);
  }
}

function handleRemove(file: any) {
  const index = fileList.value.indexOf(file);
  if (index > -1) {
    fileList.value.splice(index, 1);
  }
}

function handleCancel() {
  visable.value = false;
  fileList.value = [];
}
</script>

<style scoped lang="scss">
.ant-upload-drag-icon {
  .anticon {
    font-size: 48px;
    color: #40a9ff;
  }
}

.ant-upload-text {
  margin: 0 0 4px;
  color: rgba(0, 0, 0, 0.85);
  font-size: 16px;
}

.ant-upload-hint {
  color: rgba(0, 0, 0, 0.45);
  font-size: 14px;
}
</style>
