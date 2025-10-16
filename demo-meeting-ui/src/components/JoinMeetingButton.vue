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

// 打开加入会议对话框
const handleClick = () => {
  showJoinDialog.value = true
}

// 确认加入会议
const confirmJoinMeeting = async () => {
  if (!joinMeetingId.value.trim()) {
    ElMessage.warning('请输入会议ID')
    return
  }

  loading.value = true
  try {
    // 1. 预加入验证
    await MeetingApi.preJoinMeeting({
      meetingId: Number(joinMeetingId.value),
      password: joinPassword.value,
    })

    // 2. 验证成功，跳转到会议室（真正的加入会在 MeetingRoom 组件中完成）
    const meetingId = joinMeetingId.value
    const password = joinPassword.value

    // 重置表单
    joinMeetingId.value = ''
    joinPassword.value = ''
    showJoinDialog.value = false

    ElMessage.success('正在加入会议...')

    // 通过 query 参数传递密码（如果需要）
    router.push({
      path: `/meeting/${meetingId}`,
      query: password ? { pwd: password } : undefined,
    })
  } catch (error: any) {
    console.error('Pre-join meeting failed:', error)
    ElMessage.error(error?.message || '会议验证失败')
  } finally {
    loading.value = false
  }
}

// 取消
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
      <span class="btn-text">加入会议</span>
    </div>

    <!-- 加入会议对话框 -->
    <el-dialog
      v-model="showJoinDialog"
      title="加入会议"
      width="450px"
      :close-on-click-modal="false"
    >
      <div class="join-meeting-content">
        <p class="dialog-desc">请输入会议ID或个人链接名称</p>
        <el-input
          v-model="joinMeetingId"
          placeholder="请输入会议ID"
          size="large"
          class="input-field"
          @keyup.enter="confirmJoinMeeting"
        />
        <el-input
          v-model="joinPassword"
          placeholder="请输入会议密码（选填）"
          size="large"
          class="input-field"
          type="password"
          show-password
          @keyup.enter="confirmJoinMeeting"
        />
      </div>
      <template #footer>
        <el-button @click="handleCancel" :disabled="loading">取消</el-button>
        <el-button type="primary" @click="confirmJoinMeeting" :loading="loading"> 加入 </el-button>
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
