<script setup lang="ts">
import { ref } from 'vue'
import { Lightning } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { MeetingApi } from '@/api'

const router = useRouter()
const userStore = useUserStore()
const loading = ref(false)

// 快速会议逻辑
const handleQuickMeeting = async () => {
  if (loading.value) return

  loading.value = true
  try {
    // 1. 创建快速会议
    const meetingName = `${userStore.userInfo?.nickName}的快速会议`
    await MeetingApi.quickMeeting({
      meetingName,
      password: '', // 快速会议默认无密码
    })

    ElMessage.success('快速会议创建成功')

    // 2. 验证个人会议号
    if (!userStore.userInfo?.personalMeetingNo) {
      ElMessage.error('个人会议号不存在')
      loading.value = false
      return
    }

    // 3. 预加入验证
    await MeetingApi.preJoinMeeting({
      meetingId: userStore.userInfo.personalMeetingNo,
      password: '',
    })

    // 4. 跳转到会议室（真正的加入会在 MeetingRoom 组件中完成）
    router.push(`/meeting/${userStore.userInfo.personalMeetingNo}`)
  } catch (error: any) {
    console.error('Quick meeting failed:', error)
    ElMessage.error(error?.message || '创建快速会议失败')
  } finally {
    loading.value = false
  }
}
</script>

<template>
  <div class="action-btn" :class="{ loading: loading }" @click="handleQuickMeeting">
    <div class="btn-icon">
      <el-icon v-if="!loading"><Lightning /></el-icon>
      <el-icon v-else class="is-loading"><i-ep-loading /></el-icon>
    </div>
    <span class="btn-text">快速会议</span>
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

  &:hover:not(.loading) {
    transform: translateY(-4px);
    box-shadow: 0 8px 20px rgba(24, 144, 255, 0.4);
  }

  &.loading {
    cursor: not-allowed;
    opacity: 0.7;
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

      &.is-loading {
        animation: rotating 1s linear infinite;
      }
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

@keyframes rotating {
  from {
    transform: rotate(0deg);
  }
  to {
    transform: rotate(360deg);
  }
}
</style>
