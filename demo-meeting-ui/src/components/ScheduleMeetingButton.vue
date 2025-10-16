<script setup lang="ts">
import { Check } from '@element-plus/icons-vue'

defineProps<{
  onClick: () => void
}>()
</script>

<template>
  <div class="action-btn" @click="onClick">
    <div class="btn-icon">
      <el-icon><Check /></el-icon>
    </div>
    <span class="btn-text">预定会议</span>
    <span class="dropdown-arrow">▼</span>
  </div>
</template>

<style scoped lang="scss">
.action-btn {
  background: #1890ff;
  border-radius: 24px;
  width: 100%;
  aspect-ratio: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: all 0.3s ease;
  box-shadow: 0 4px 12px rgba(24, 144, 255, 0.3);
  position: relative;

  &:hover {
    transform: translateY(-4px);
    box-shadow: 0 8px 20px rgba(24, 144, 255, 0.4);
  }

  .btn-icon {
    width: 48px;
    height: 48px;
    background: white;
    border-radius: 12px;
    display: flex;
    align-items: center;
    justify-content: center;
    margin-bottom: 12px;

    .el-icon {
      font-size: 32px;
      color: #1890ff;
    }
  }

  .btn-text {
    color: white;
    font-size: 15px;
    font-weight: 500;
  }

  .dropdown-arrow {
    position: absolute;
    top: 16px;
    right: 16px;
    color: white;
    font-size: 10px;
    opacity: 0.8;
  }
}
</style>
