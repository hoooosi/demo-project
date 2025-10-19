<template>
  <a-button @click="modelValue = !modelValue" class="sort-btn">
    <template #icon>
      <SortAscendingOutlined v-if="modelValue" />
      <SortDescendingOutlined v-else />
    </template>
  </a-button>
</template>

<script setup lang="ts">
import {
  SortAscendingOutlined,
  SortDescendingOutlined,
} from "@ant-design/icons-vue";
const modelValue = defineModel<boolean>();
</script>
<style scoped lang="scss">
.sort-btn {
  border-radius: 6px;
  width: 32px;
  height: 32px;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 0;
  border: 1px solid #d9d9d9;
  transition: all 0.3s;

  &:hover {
    border-color: #40a9ff;
    color: #40a9ff;
  }

  .anticon {
    font-size: 16px;
  }
}
</style>
