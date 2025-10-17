<script setup lang="ts">
import { ref, nextTick, onMounted, onBeforeUnmount } from 'vue'
import { ElMessage } from 'element-plus'
import { ChatDotRound } from '@element-plus/icons-vue'
import { wsManager, MessageType, MessageTargetType } from '@/utils/websocket'
import type { WSMessage } from '@/utils/websocket'
import { useUserStore } from '@/stores/user'
import { useRoute } from 'vue-router'

const userStore = useUserStore()
const route = useRoute()

interface ChatMessage {
  id: string
  senderId: string
  senderName: string
  content: string
  time: string
  isLocal: boolean
}

const messages = ref<ChatMessage[]>([])
const inputMessage = ref('')
const chatListRef = ref<HTMLElement>()

const sendMessage = () => {
  const text = inputMessage.value.trim()
  if (!text) return

  const meetingId = route.params.id as string

  try {
    wsManager.sendChat(text, MessageTargetType.MEETING, meetingId)
    inputMessage.value = ''
    scrollToBottom()
  } catch (e: any) {
    ElMessage.error('Send message faile')
  }
}

const scrollToBottom = () => {
  nextTick(() => {
    if (chatListRef.value) {
      chatListRef.value.scrollTop = chatListRef.value.scrollHeight
    }
  })
}

const handleIncomingMessage = (wsMessage: WSMessage) => {
  if (wsMessage.messageType !== MessageType.TEXT) return

  messages.value.push({
    id: Date.now().toString() + Math.random(),
    senderId: wsMessage.senderId,
    senderName: wsMessage.senderId,
    content: wsMessage.content,
    time: new Date(wsMessage.sendTime).toLocaleTimeString('zh-CN', {
      hour: '2-digit',
      minute: '2-digit',
    }),
    isLocal: false,
  })

  scrollToBottom()
}

let unsubscribe: (() => void) | null = null

onMounted(() => {
  unsubscribe = wsManager.on(MessageType.TEXT, handleIncomingMessage)
})

onBeforeUnmount(() => {
  if (unsubscribe) {
    unsubscribe()
  }
})

const handleKeydown = (e: KeyboardEvent) => {
  if (e.key === 'Enter' && !e.shiftKey) {
    e.preventDefault()
    sendMessage()
  }
}
</script>

<template>
  <div class="chat-panel">
    <div class="chat-header">
      <div class="header-title">
        <el-icon :size="20">
          <ChatDotRound />
        </el-icon>
        <span>聊天</span>
      </div>
    </div>

    <div class="chat-messages" ref="chatListRef">
      <div
        v-for="message in messages"
        :key="message.id"
        class="message-item"
        :class="{ 'message-local': message.isLocal }"
      >
        <div class="message-sender">{{ message.senderName }}</div>
        <div class="message-content">
          <div class="message-bubble">{{ message.content }}</div>
          <div class="message-time">{{ message.time }}</div>
        </div>
      </div>
      <div v-if="messages.length === 0" class="empty-state">
        <el-icon :size="48" color="#666">
          <ChatDotRound />
        </el-icon>
        <p>暂无消息</p>
      </div>
    </div>

    <div class="chat-input">
      <el-input
        v-model="inputMessage"
        type="textarea"
        :rows="3"
        resize="none"
        placeholder="输入消息... (Enter发送, Shift+Enter换行)"
        @keydown="handleKeydown"
      />
      <el-button type="primary" @click="sendMessage" :disabled="!inputMessage.trim()">
        发送
      </el-button>
    </div>
  </div>
</template>

<style scoped lang="scss">
.chat-panel {
  width: 100%;
  height: 100%;
  display: flex;
  flex-direction: column;
  background: #1e1e1e;
  color: #fff;

  .chat-header {
    padding: 16px 20px;
    border-bottom: 1px solid rgba(255, 255, 255, 0.1);
    background: rgba(30, 30, 30, 0.95);

    .header-title {
      display: flex;
      align-items: center;
      gap: 8px;
      font-size: 16px;
      font-weight: 600;
    }
  }

  .chat-messages {
    flex: 1;
    overflow-y: auto;
    padding: 16px;
    display: flex;
    flex-direction: column;
    gap: 16px;

    .message-item {
      display: flex;
      flex-direction: column;
      align-items: flex-start;

      &.message-local {
        align-items: flex-end;

        .message-sender {
          text-align: right;
        }

        .message-bubble {
          background: #409eff;
          color: #fff;
        }

        .message-time {
          text-align: right;
        }
      }

      .message-sender {
        font-size: 12px;
        color: #999;
        margin-bottom: 4px;
      }

      .message-content {
        max-width: 80%;

        .message-bubble {
          background: rgba(255, 255, 255, 0.1);
          padding: 10px 14px;
          border-radius: 8px;
          word-wrap: break-word;
          word-break: break-word;
          line-height: 1.5;
        }

        .message-time {
          font-size: 11px;
          color: #666;
          margin-top: 4px;
        }
      }
    }

    .empty-state {
      flex: 1;
      display: flex;
      flex-direction: column;
      align-items: center;
      justify-content: center;
      color: #666;
      gap: 12px;

      p {
        margin: 0;
        font-size: 14px;
      }
    }
  }

  .chat-input {
    padding: 16px;
    border-top: 1px solid rgba(255, 255, 255, 0.1);
    background: rgba(30, 30, 30, 0.95);
    display: flex;
    flex-direction: column;
    gap: 12px;

    :deep(.el-textarea__inner) {
      background: rgba(255, 255, 255, 0.05);
      border-color: rgba(255, 255, 255, 0.1);
      color: #fff;

      &::placeholder {
        color: #666;
      }

      &:focus {
        border-color: #409eff;
      }
    }

    .el-button {
      align-self: flex-end;
    }
  }
}

// 滚动条样式
.chat-messages::-webkit-scrollbar {
  width: 6px;
}

.chat-messages::-webkit-scrollbar-track {
  background: rgba(255, 255, 255, 0.05);
  border-radius: 3px;
}

.chat-messages::-webkit-scrollbar-thumb {
  background: rgba(255, 255, 255, 0.2);
  border-radius: 3px;

  &:hover {
    background: rgba(255, 255, 255, 0.3);
  }
}
</style>
