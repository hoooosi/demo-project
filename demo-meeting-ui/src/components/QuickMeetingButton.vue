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

const handleQuickMeeting = async () => {
  if (loading.value) return

  loading.value = true
  try {
    const meetingName = `Quick Meeting - ${userStore.userInfo?.nickName}`
    await MeetingApi.quickMeeting({
      meetingName,
      password: '',
    })

    ElMessage.success('Quick meeting created successfully')

    if (!userStore.userInfo?.personalMeetingNo) {
      ElMessage.error('Personal meeting number does not exist')
      loading.value = false
      return
    }

    await MeetingApi.preJoinMeeting({
      meetingId: userStore.userInfo.personalMeetingNo,
      password: '',
    })

    router.push(`/meeting/${userStore.userInfo.personalMeetingNo}`)
  } catch (e: any) {
    ElMessage.error(e?.message || 'Quick meeting creation failed')
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
    <span class="btn-text">QUICK MEETING</span>
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
