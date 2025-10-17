<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import {
  VideoCamera,
  ChatLineSquare,
  Document,
  VideoPlay,
  Message,
  Setting,
  User,
  Calendar,
  ArrowRight,
  Close,
} from '@element-plus/icons-vue'
import { UserApi } from '@/api'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import JoinMeetingButton from '@/components/JoinMeetingButton.vue'
import QuickMeetingButton from '@/components/QuickMeetingButton.vue'
import ScheduleMeetingButton from '@/components/ScheduleMeetingButton.vue'
import ShareScreenButton from '@/components/ShareScreenButton.vue'

const router = useRouter()
const userStore = useUserStore()

const currentDate = ref('')
const currentWeekday = ref('')

const initDate = () => {
  const now = new Date()
  const month = now.getMonth() + 1
  const date = now.getDate()
  currentDate.value = `${month}Month${date}Day`

  const weekdays = ['Sunday', 'Monday', 'Tuesday', 'Wednesday', 'Thursday', 'Friday', 'Saturday']
  const dayIndex = now.getDay()
  currentWeekday.value = `Weekday ${weekdays[dayIndex]}`
}

// User information dialog
const showUserDialog = ref(false)

// Check user status
const checkUserStatus = async () => {
  try {
    const result = await UserApi.me()
    userStore.setUserInfo(result.data)
  } catch (error) {
    ElMessage.error('Please log in first')
    router.push('/login')
  }
}

// Operation button events
const handleScheduleMeeting = () => {
  ElMessage.info('Schedule meeting feature is under development...')
}

const handleShareScreen = () => {
  ElMessage.info('Screen sharing feature is under development...')
}

const showUserInfo = () => {
  showUserDialog.value = true
}

const logout = () => {
  userStore.clearUserInfo()
  router.push('/login')
  ElMessage.success('Logged out successfully')
}

const showBanner = ref(true)

onMounted(async () => {
  initDate()
  await checkUserStatus()
})
</script>

<template>
  <div class="dashboard-shell">
    <div class="dashboard-container">
      <!-- Sidebar Section -->
      <div class="sidebar-section">
        <!-- Avatar Area -->
        <div class="avatar-area">
          <div class="avatar-wrapper">
            <div class="avatar">
              <el-icon><User /></el-icon>
            </div>
            <span class="status-dot online"></span>
            <span class="notification-badge">2</span>
          </div>
        </div>

        <!-- Main Menu -->
        <div class="main-menu">
          <div class="menu-item active">
            <div class="menu-icon">
              <el-icon><VideoCamera /></el-icon>
            </div>
            <span class="menu-text">Meeting</span>
          </div>
          <div class="menu-item">
            <div class="menu-icon">
              <el-icon><ChatLineSquare /></el-icon>
            </div>
            <span class="menu-text">Contacts</span>
          </div>
          <div class="menu-item">
            <div class="menu-icon">
              <el-icon><VideoPlay /></el-icon>
            </div>
            <span class="menu-text">Recording</span>
          </div>
        </div>

        <!-- Bottom Menu -->
        <div class="bottom-menu">
          <div class="menu-item">
            <el-icon><Message /></el-icon>
            <span class="notification-dot"></span>
          </div>
          <div class="menu-item">
            <el-icon><Setting /></el-icon>
            <span class="notification-dot"></span>
          </div>
          <div class="menu-item" @click="showUserInfo">
            <el-icon><User /></el-icon>
          </div>
        </div>
      </div>

      <!-- Operation Section -->
      <div class="opt-section">
        <div class="action-buttons">
          <JoinMeetingButton />
          <QuickMeetingButton />
          <ScheduleMeetingButton :on-click="handleScheduleMeeting" />
          <ShareScreenButton :on-click="handleShareScreen" />
        </div>
      </div>

      <!-- Information Section -->
      <div class="info-section">
        <!-- Date Area -->
        <div class="date-area">
          <div class="date-display">
            <h1 class="date-text">{{ currentDate }}</h1>
            <p class="weekday-text">{{ currentWeekday }}</p>
          </div>
          <div class="all-meetings-link">
            <span>All Meetings</span>
            <el-icon><ArrowRight /></el-icon>
          </div>
        </div>

        <!-- Meetings List Area -->
        <div class="meetings-area">
          <div class="empty-state">
            <div class="empty-illustration">
              <div class="coffee-cup"></div>
            </div>
            <p class="empty-text">No meetings available</p>
          </div>
        </div>

        <!-- Bottom right calendar button -->
        <div class="calendar-fab">
          <el-icon><Calendar /></el-icon>
        </div>
      </div>
    </div>

    <!-- User information dialog -->
    <el-dialog v-model="showUserDialog" title="User Information" width="400px">
      <div v-if="userStore.userInfo" class="user-info-content">
        <div class="user-info-item">
          <span class="label">Email:</span>
          <span class="value">{{ userStore.userInfo.email }}</span>
        </div>
        <div class="user-info-item">
          <span class="label">Nickname:</span>
          <span class="value">{{ userStore.userInfo.nickName }}</span>
        </div>
        <div class="user-info-item">
          <span class="label">Personal Meeting ID:</span>
          <span class="value">{{ userStore.userInfo.personalMeetingNo }}</span>
        </div>
      </div>
      <template #footer>
        <el-button @click="showUserDialog = false">Cancel</el-button>
        <el-button type="primary" @click="logout">Log Out</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<style scoped lang="scss">
.dashboard-shell {
  width: 100vw;
  height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 20px;
  position: relative;
  overflow: hidden;

  .dashboard-container {
    display: flex;
    width: 65%;
    height: 80%;
    background: white;
    border-radius: 24px;
    overflow: hidden;
    box-shadow: 0 8px 32px rgba(0, 0, 0, 0.15);

    // ========== 区域1: 侧边栏 ==========
    .sidebar-section {
      width: 68px;
      background: #f8f9fa;
      display: flex;
      flex-direction: column;
      align-items: center;
      padding: 16px 0;
      border-right: 1px solid #e8e8e8;

      .avatar-area {
        margin-bottom: 32px;
        position: relative;

        .avatar-wrapper {
          position: relative;
          width: 48px;
          height: 48px;

          .avatar {
            width: 48px;
            height: 48px;
            border-radius: 50%;
            background: linear-gradient(135deg, #a0a0a0, #707070);
            display: flex;
            align-items: center;
            justify-content: center;
            cursor: pointer;

            .el-icon {
              font-size: 24px;
              color: white;
            }
          }

          .status-dot {
            position: absolute;
            bottom: 2px;
            right: 2px;
            width: 12px;
            height: 12px;
            border-radius: 50%;
            border: 2px solid #f8f9fa;

            &.online {
              background: #52c41a;
            }
          }

          .notification-badge {
            position: absolute;
            top: -4px;
            right: -4px;
            background: #ff4d4f;
            color: white;
            font-size: 10px;
            padding: 2px 5px;
            border-radius: 10px;
            min-width: 16px;
            height: 16px;
            display: flex;
            align-items: center;
            justify-content: center;
          }
        }
      }

      .main-menu {
        flex: 1;
        display: flex;
        flex-direction: column;
        gap: 8px;
        width: 100%;
        padding: 0 8px;

        .menu-item {
          display: flex;
          flex-direction: column;
          align-items: center;
          gap: 6px;
          padding: 12px 0;
          border-radius: 12px;
          cursor: pointer;
          transition: all 0.3s ease;
          color: #666;

          .menu-icon {
            width: 32px;
            height: 32px;
            display: flex;
            align-items: center;
            justify-content: center;

            .el-icon {
              font-size: 22px;
            }
          }

          .menu-text {
            font-size: 12px;
            line-height: 1;
          }

          &:hover {
            background: #e6f7ff;
            color: #1890ff;
          }

          &.active {
            background: #1890ff;
            color: white;

            .menu-icon .el-icon {
              color: white;
            }
          }
        }
      }

      .bottom-menu {
        display: flex;
        flex-direction: column;
        gap: 12px;
        padding: 0 8px;

        .menu-item {
          width: 40px;
          height: 40px;
          display: flex;
          align-items: center;
          justify-content: center;
          border-radius: 8px;
          cursor: pointer;
          transition: all 0.3s ease;
          color: #666;
          position: relative;

          .el-icon {
            font-size: 20px;
          }

          .notification-dot {
            position: absolute;
            top: 8px;
            right: 8px;
            width: 8px;
            height: 8px;
            border-radius: 50%;
            background: #ff4d4f;
          }

          &:hover {
            background: #e6f7ff;
            color: #1890ff;
          }
        }
      }
    }

    // ========== 区域2: 操作区 ==========
    .opt-section {
      width: 360px;
      background: white;
      display: flex;
      align-items: center;
      justify-content: center;
      padding: 40px 32px;
      overflow: auto;

      .action-buttons {
        display: grid;
        grid-template-columns: repeat(2, 1fr);
        gap: 20px;
        width: 100%;
      }
    }

    // ========== 区域3: 信息区 ==========
    .info-section {
      flex: 1;
      background: #fafbfc;
      display: flex;
      flex-direction: column;
      padding: 24px 32px 32px;
      position: relative;
      overflow-y: auto;

      .date-area {
        display: flex;
        align-items: flex-start;
        justify-content: space-between;
        margin-bottom: 32px;

        .date-display {
          .date-text {
            font-size: 56px;
            font-weight: 300;
            color: #262626;
            margin: 0;
            line-height: 1.2;
          }

          .weekday-text {
            font-size: 14px;
            color: #8c8c8c;
            margin: 4px 0 0 0;
          }
        }

        .all-meetings-link {
          display: flex;
          align-items: center;
          gap: 4px;
          color: #1890ff;
          font-size: 14px;
          cursor: pointer;
          padding-top: 12px;

          &:hover {
            opacity: 0.8;
          }

          .el-icon {
            font-size: 16px;
          }
        }
      }

      .meetings-area {
        flex: 1;
        display: flex;
        align-items: center;
        justify-content: center;

        .empty-state {
          display: flex;
          flex-direction: column;
          align-items: center;
          gap: 20px;

          .empty-illustration {
            .coffee-cup {
              width: 140px;
              height: 140px;
              background: linear-gradient(135deg, #e8f4ff 0%, #d9eeff 100%);
              border-radius: 50%;
              position: relative;
              display: flex;
              align-items: center;
              justify-content: center;

              &::before {
                content: '☕';
                font-size: 64px;
                opacity: 0.4;
              }
            }
          }

          .empty-text {
            font-size: 16px;
            color: #8c8c8c;
            margin: 0;
          }
        }
      }

      .calendar-fab {
        position: absolute;
        bottom: 32px;
        right: 32px;
        width: 56px;
        height: 56px;
        background: white;
        border-radius: 50%;
        display: flex;
        align-items: center;
        justify-content: center;
        box-shadow: 0 4px 16px rgba(0, 0, 0, 0.12);
        cursor: pointer;
        transition: all 0.3s ease;

        .el-icon {
          font-size: 24px;
          color: #262626;
        }

        &:hover {
          transform: scale(1.1);
          box-shadow: 0 6px 20px rgba(0, 0, 0, 0.16);
        }
      }
    }
  }
}

// 弹窗样式
.user-info-content {
  .user-info-item {
    display: flex;
    justify-content: space-between;
    padding: 12px 0;
    border-bottom: 1px solid #f0f0f0;

    &:last-child {
      border-bottom: none;
    }

    .label {
      color: #666;
      font-weight: 500;
    }

    .value {
      color: #333;
    }
  }
}

.join-meeting-content {
  .dialog-desc {
    color: #666;
    font-size: 14px;
    margin-bottom: 16px;
  }
}

// 响应式
@media (max-width: 1200px) {
  .dashboard-shell .dashboard-container {
    .opt-section {
      width: 300px;
      padding: 24px 20px;

      .action-buttons {
        gap: 16px;
      }
    }
  }
}

@media (max-width: 768px) {
  .dashboard-shell {
    padding: 10px;

    .dashboard-container {
      height: calc(100vh - 20px);
      border-radius: 16px;

      .opt-section {
        display: none;
      }

      .info-section {
        padding: 16px 20px 20px;

        .date-area .date-display .date-text {
          font-size: 40px;
        }
      }
    }
  }
}
</style>
