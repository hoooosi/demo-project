<template>
  <div class="space-card-shell" @click="handleClick">
    <!-- SPACE COVER -->
    <div class="space-cover">
      <div class="space-type-icon">
        <TeamOutlined />
      </div>
      <!-- JOIN BUTTON -->
      <div class="opt-shell" v-if="isMember || isCanApply || (!isMember && !isCanApply)">
        <a-button
          v-if="isMember"
          type="default"
          size="small"
          disabled
          class="joined-btn"
          @click.stop
        >
          JOINED
        </a-button>
        <a-button
          v-else-if="isCanApply"
          type="primary"
          size="small"
          class="join-btn"
          @click.stop="apply"
        >
          JOIN
        </a-button>
        <a-button v-else type="default" size="small" disabled class="join-btn-disabled" @click.stop>
          JOIN
        </a-button>
      </div>
    </div>

    <!-- SPACE INFO -->
    <div class="space-info">
      <h4 class="space-name" :title="modelValue.name">{{ modelValue.name }}</h4>

      <!-- USAGE -->
      <div class="space-usage">
        <div class="usage-item">
          <span class="usage-label">Storage:</span>
          <div class="usage-bar">
            <div
              class="usage-progress"
              :style="{ width: Utils.getStoragePercent(modelValue) + '%' }"
            ></div>
          </div>
          <span class="usage-text">
            {{ Utils.formatSize(modelValue.totalSize) }} /
            {{ Utils.formatSize(modelValue.maxSize) }}
          </span>
        </div>
        <div class="usage-item">
          <span class="usage-label">Pictures:</span>
          <div class="usage-bar">
            <div
              class="usage-progress"
              :style="{ width: Utils.getCountPercent(modelValue) + '%' }"
            ></div>
          </div>
          <span class="usage-text">
            0 / 0
          </span>
        </div>
      </div>

      <!-- SPACE META -->
      <div class="space-meta">
        <div class="time-info">
          <span class="create-time">Created: {{ Utils.formatDate(modelValue.createTime) }}</span>
        </div>
        <div class="mask-detail-shell">
          <!-- PERMISSION ICON -->
          <div class="mask-detail-box">
            <SafetyOutlined />
            <!-- PERMISSION TOOLTIP -->
            <div class="permission-tooltip">
              <div class="tooltip-title">Permissions</div>
              <div class="permission-list">
                <div v-for="item in allPermissionNames" class="permission-item">
                  {{ item }}
                </div>
                <div v-if="allPermissionNames.length === 0" class="no-permissions">
                  No specific permissions
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { TeamOutlined, SafetyOutlined } from '@ant-design/icons-vue'
import { useRouter } from 'vue-router'
import { MetaInfo } from '@/router'
import * as Utils from '@/utils'
import * as Const from '@/const'
import { computed } from 'vue'
import { SumApi } from '@/api'
import { message } from 'ant-design-vue'

const router = useRouter()
const props = defineProps<{
  modelValue: VO.SpaceVO
}>()
const emit = defineEmits<{
  reload: []
}>()

const allMask = computed<bigint>(() => selfMask.value | pubMask.value)
const pubMask = computed<bigint>(() => BigInt(props.modelValue.publicPermissionMask || 0))
const selfMask = computed<bigint>(() => BigInt(props.modelValue.permissionMask || 0))
const isMember = computed<boolean>(() => selfMask.value != 0n)
const isCanApply = computed<boolean>(
  () => !isMember.value && Utils.hasPermission(pubMask.value, Const.PermissionMask.SPACE_JOIN)
)
const isCanView = computed<boolean>(() =>
  Utils.hasPermission(
    pubMask.value | selfMask.value,
    Const.PermissionMask.SPACE_VIEW | Const.PermissionMask.IMAGE_VIEW
  )
)
const allPermissionNames = computed<string[]>(() => {
  const list = Utils.getPermissionList(allMask.value)
  return list.map((mask) => {
    const name = Utils.getPermissionName(mask)
    if (!name) return 'Unknown Permission'
    return name
      .replace(/_/g, ' ')
      .toLowerCase()
      .replace(/\b\w/g, (l) => l.toUpperCase())
  })
})

// Event handler
function handleClick() {
  router.push({
    name: MetaInfo.Space.name,
    params: {
      sid: props.modelValue.id,
    },
  })
}

async function apply() {
  try {
    await SumApi.apply(props.modelValue.id)
    message.info('Joining space requires an invitation code or admin approval')
  } catch (e: any) {
    message.error(e.message)
  } finally {
    emit('reload')
  }
}
</script>

<style scoped lang="scss">
.space-card-shell {
  width: 100%;
  height: 100%;
  background: #fff;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
  transition: all 0.3s ease;
  cursor: pointer;
  overflow: hidden;
  position: relative;
  display: flex;
  flex-direction: column;

  &:hover {
    box-shadow: 0 4px 16px rgba(0, 0, 0, 0.12);
    transform: translateY(-2px);
  }

  .space-cover {
    flex-shrink: 0;
    height: 120px;
    min-height: 120px;
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
    position: relative;
    display: flex;
    align-items: center;
    justify-content: center;

    .space-type-icon {
      font-size: 48px;
      color: rgba(255, 255, 255, 0.9);
    }

    .opt-shell {
      position: absolute;
      top: 12px;
      right: 12px;
      display: flex;
      gap: 1em;

      .joined-btn {
        background-color: rgba(255, 255, 255, 0.9);
        border-color: #52c41a;
        color: #52c41a;
        font-weight: 600;
        cursor: default;

        &:hover {
          background-color: rgba(255, 255, 255, 0.9) !important;
          border-color: #52c41a !important;
          color: #52c41a !important;
        }
      }

      .join-btn {
        background-color: rgba(24, 144, 255, 0.9);
        border-color: #1890ff;
        color: white;
        font-weight: 600;
        backdrop-filter: blur(4px);

        &:hover {
          background-color: rgba(64, 169, 255, 0.9) !important;
          border-color: #40a9ff !important;
          transform: scale(1.05);
        }
      }

      .join-btn-disabled {
        background-color: rgba(255, 255, 255, 0.6);
        border-color: #d9d9d9;
        color: #838282;
        cursor: not-allowed;

        &:hover {
          background-color: rgba(255, 255, 255, 0.6) !important;
          border-color: #d9d9d9 !important;
        }
      }
    }
  }

  .space-info {
    flex: 1;
    padding: 20px;
    display: flex;
    flex-direction: column;
    min-height: 0;

    .space-name {
      margin: 0 0 8px 0;
      font-size: 18px;
      font-weight: 600;
      color: #262626;
      overflow: hidden;
      text-overflow: ellipsis;
      white-space: nowrap;
      flex-shrink: 0;
    }

    .space-usage {
      flex: 1;
      margin-bottom: 12px;
      min-height: 0;

      .usage-item {
        display: flex;
        align-items: center;
        margin-bottom: 12px;
        font-size: 12px;

        &:last-child {
          margin-bottom: 0;
        }

        .usage-label {
          width: 40px;
          color: #8c8c8c;
          flex-shrink: 0;
        }

        .usage-bar {
          flex: 1;
          height: 6px;
          background: #f0f0f0;
          border-radius: 3px;
          margin: 0 12px;
          overflow: hidden;

          .usage-progress {
            height: 100%;
            background: linear-gradient(90deg, #52c41a, #73d13d);
            border-radius: 3px;
            transition: width 0.3s ease;
          }
        }

        .usage-text {
          color: #595959;
          font-size: 11px;
          white-space: nowrap;
        }
      }
    }

    .space-meta {
      flex-shrink: 0;
      margin-top: auto;
      display: flex;
      justify-content: space-between;

      .time-info {
        display: flex;
        flex-direction: column;
        gap: 2px;

        span {
          color: #bfbfbf;
          font-size: 10px;
          line-height: 1.2;
        }

        .create-time {
          color: #8c8c8c;
        }
      }

      .mask-detail-shell {
        color: #8c8c8c;
        position: relative;
        font-size: 10px;
        line-height: 1.2;

        .mask-detail-box {
          position: relative;
          cursor: pointer;
          padding: 4px;
          border-radius: 4px;
          transition: all 0.3s ease;

          &:hover {
            background-color: rgba(140, 140, 140, 0.1);
            color: #1890ff;

            .permission-tooltip {
              opacity: 1;
              visibility: visible;
              transform: translateY(-8px);
            }
          }

          .permission-tooltip {
            position: absolute;
            bottom: 100%;
            right: 0;
            background: #333;
            color: white;
            padding: 12px;
            border-radius: 6px;
            box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
            opacity: 0;
            visibility: hidden;
            transform: translateY(0);
            transition: all 0.3s ease;
            z-index: 1000;
            min-width: 180px;
            white-space: nowrap;

            &::after {
              content: '';
              position: absolute;
              top: 100%;
              right: 12px;
              border: 6px solid transparent;
              border-top-color: #333;
            }

            .tooltip-title {
              font-size: 12px;
              font-weight: 600;
              margin-bottom: 8px;
              color: #fff;
              border-bottom: 1px solid rgba(255, 255, 255, 0.2);
              padding-bottom: 6px;
            }

            .permission-list {
              .permission-item {
                font-size: 11px;
                color: #e6f7ff;
                margin-bottom: 4px;
                padding: 2px 0;

                &:last-child {
                  margin-bottom: 0;
                }

                &:before {
                  content: '•';
                  margin-right: 6px;
                  color: #52c41a;
                }
              }

              .no-permissions {
                font-size: 11px;
                color: #bfbfbf;
                font-style: italic;
              }
            }
          }
        }
      }
    }
  }
}
</style>
