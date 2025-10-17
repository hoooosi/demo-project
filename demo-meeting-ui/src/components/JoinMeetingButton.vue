<script setup lang="ts">
import { ref } from 'vue'
import { Plus } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import { useRouter } from 'vue-router'
import { MeetingApi } from '@/api'

const router = useRouter()
const showJoinDialog = ref(false)
const joinMeetingId = ref('')
const joinPassword = ref('')
const loading = ref(false)

const handleClick = () => {
  showJoinDialog.value = true
}

const confirmJoinMeeting = async () => {
  if (!joinMeetingId.value.trim()) {
    ElMessage.warning('Please enter the meeting ID')
    return
  }

  loading.value = true
  try {
    await MeetingApi.preJoinMeeting({
      meetingId: Number(joinMeetingId.value),
      password: joinPassword.value,
    })

    const meetingId = joinMeetingId.value
    const password = joinPassword.value

    joinMeetingId.value = ''
    joinPassword.value = ''
    showJoinDialog.value = false

    router.push({
      path: `/meeting/${meetingId}`,
      query: password ? { pwd: password } : undefined,
    })
  } catch (error: any) {
    console.error('Pre-join meeting failed:', error)
    ElMessage.error(error?.message || 'Meeting validation failed')
  } finally {
    loading.value = false
  }
}

const handleCancel = () => {
  joinMeetingId.value = ''
  joinPassword.value = ''
  showJoinDialog.value = false
}
</script>

<template>
  <div>
    <div class="action-btn" @click="handleClick">
      <div class="btn-icon">
        <el-icon><Plus /></el-icon>
      </div>
      <span class="btn-text">JOIN MEETING</span>
    </div>

    <!-- JOIN MEETING DIALOG -->
    <el-dialog
      v-model="showJoinDialog"
      title="JOIN MEETING"
      width="450px"
      :close-on-click-modal="false"
    >
      <div class="join-meeting-content">
        <p class="dialog-desc">Please enter the meeting ID or personal link name</p>
        <el-input
          v-model="joinMeetingId"
          placeholder="Please enter the meeting ID"
          size="large"
          class="input-field"
          @keyup.enter="confirmJoinMeeting"
        />
        <el-input
          v-model="joinPassword"
          placeholder="Please enter the meeting password (optional)"
          size="large"
          class="input-field"
          type="password"
          show-password
          @keyup.enter="confirmJoinMeeting"
        />
      </div>
      <template #footer>
        <el-button @click="handleCancel" :disabled="loading">Cancel</el-button>
        <el-button type="primary" @click="confirmJoinMeeting" :loading="loading">Join</el-button>
      </template>
    </el-dialog>
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
}

.join-meeting-content {
  padding: 20px 0;

  .dialog-desc {
    color: #666;
    font-size: 14px;
    margin-bottom: 20px;
  }

  .input-field {
    margin-bottom: 16px;
  }
}
</style>
