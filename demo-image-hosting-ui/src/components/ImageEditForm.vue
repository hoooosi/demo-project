<script setup lang="ts">
import { reactive, watch } from "vue";

const props = defineProps<{
  image: VO.ImageVO;
  availableTags: string[];
  loadingTags: boolean;
  saving: boolean;
}>();

const emit = defineEmits<{
  save: [data: { name: string; introduction: string; tags: string[] }];
  cancel: [];
}>();

const form = reactive({
  name: "",
  introduction: "",
  tags: [] as string[],
});

// 初始化表单
watch(
  () => props.image,
  (image) => {
    if (image) {
      form.name = image.name;
      form.introduction = image.introduction || "";
      form.tags = [...(image.tags || [])];
    }
  },
  { immediate: true }
);

const handleSubmit = () => {
  emit("save", { ...form });
};
</script>

<template>
  <div class="edit-form">
    <div class="form-item">
      <div class="form-label">Name:</div>
      <a-input v-model:value="form.name" placeholder="Please enter picture name" />
    </div>

    <div class="form-item">
      <div class="form-label">Description:</div>
      <a-textarea
        v-model:value="form.introduction"
        placeholder="Please enter picture description"
        :rows="3"
      />
    </div>

    <div class="form-item">
      <div class="form-label">Tags:</div>
      <a-select
        v-model:value="form.tags"
        mode="tags"
        placeholder="Please select or enter tags"
        :loading="loadingTags"
        style="width: 100%"
        :options="availableTags.map((tag) => ({ label: tag, value: tag }))"
        allow-clear
      />
    </div>

    <div class="form-actions">
      <a-button @click="emit('cancel')">Cancel</a-button>
      <a-button type="primary" :loading="saving" @click="handleSubmit">Save</a-button>
    </div>
  </div>
</template>

<style scoped lang="scss">
.edit-form {
  .form-item {
    display: flex;
    margin-bottom: 16px;
    width: 100%;

    .form-label {
      width: 80px;
      text-align: center;
      color: #666;
      font-weight: 500;
      flex-shrink: 0;
      line-height: 32px;
    }

    .ant-input,
    .ant-textarea,
    .ant-select {
      flex: 1;
    }
  }

  .form-actions {
    display: flex;
    justify-content: flex-end;
    gap: 8px;
    margin-top: 20px;
  }
}
</style>
