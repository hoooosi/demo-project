<template>
  <template v-if="modelValue">
    <a-button @click="openModal"> <UserAddOutlined />APPLY</a-button>

    <a-modal v-model:open="visible" title="SPACE JOIN REQUESTS" width="900px" :footer="null">
      <!-- STATUS -->
      <div class="filter-section">
        <a-form layout="inline">
          <a-form-item label="STATUS">
            <a-select
              v-model:value="queryParms.status"
              placeholder="All Status"
              style="width: 150px"
              @change="query"
            >
              <a-select-option :value="undefined">All Status</a-select-option>
              <a-select-option value="PENDING">PENDING</a-select-option>
              <a-select-option value="APPROVED">APPROVED</a-select-option>
              <a-select-option value="REJECTED">REJECTED</a-select-option>
            </a-select>
          </a-form-item>
        </a-form>
      </div>

      <!-- TABLE -->
      <div class="apply-table-section">
        <a-table
          :columns="COLUMNS_CONFIG"
          :data-source="list"
          :loading="loading"
          :pagination="pagination"
          row-key="id"
          @change="handleTableChange"
        >
          <template #bodyCell="{ column, record }">
            <template v-if="column.key === 'user'">
              <div class="user-info">
                <div class="user-avatar">
                  <img :src="record.userAvatar" :alt="record.userName" />
                </div>
                <div class="user-details">
                  <div class="user-name">{{ record.userName }}</div>
                </div>
              </div>
            </template>
            <template v-if="column.key === 'createTime'">
              {{ new Date(record.createTime).toLocaleString() }}
            </template>
            <template v-if="column.key === 'status'">
              <a-tag :color="getStatusColor(record.status)" class="status-tag">
                {{ record.status }}
              </a-tag>
            </template>
            <template v-if="column.key === 'action'">
              <template v-if="record.status === 'PENDING'">
                <a-button
                  type="primary"
                  size="small"
                  @click="handleApply(record.id, 'APPROVED')"
                  :loading="actionLoading === record.id"
                >
                  APPROVE
                </a-button>
                <a-button
                  danger
                  size="small"
                  @click="handleApply(record.id, 'REJECTED')"
                  :loading="actionLoading === record.id"
                  style="margin-left: 8px"
                >
                  REJECT
                </a-button>
              </template>
              <span v-else class="action-placeholder">-</span>
            </template>
          </template>
        </a-table>
      </div>
    </a-modal>
  </template>
</template>

<script setup lang="ts">
import { computed, ref } from 'vue'
import { message } from 'ant-design-vue'
import { SumApi } from '@/api'
import { UserAddOutlined } from '@ant-design/icons-vue'

const props = defineProps<{
  modelValue: string
}>()

const emit = defineEmits<{
  reload: []
}>()

const visible = ref(false)
const loading = ref(false)
const actionLoading = ref<string>('')
const allApplications = ref<VO.ApplicationVO[]>([])
const queryParms = ref({
  status: undefined as string | undefined,
})

// 分页配置
const pagination = ref({
  current: 1,
  pageSize: 10,
  total: 0,
  showSizeChanger: true,
  showQuickJumper: true,
  showTotal: (total: number) => `Total ${total} items`,
})

// 根据分页计算当前页数据
const list = computed<VO.ApplicationVO[]>(() => {
  const start = (pagination.value.current - 1) * pagination.value.pageSize
  const end = start + pagination.value.pageSize
  return allApplications.value.slice(start, end)
})

const COLUMNS_CONFIG = [
  {
    title: 'USER',
    key: 'user',
    width: 200,
  },
  {
    title: 'APPLY TIME',
    key: 'createTime',
    width: 180,
  },
  {
    title: 'STATUS',
    key: 'status',
    width: 120,
  },
  {
    title: 'ACTION',
    key: 'action',
    width: 180,
  },
]

async function openModal() {
  visible.value = true
  await query()
}

async function query() {
  try {
    loading.value = true

    const res = await SumApi.pageApplicationBySpace(
      {
        status: queryParms.value.status,
      },
      props.modelValue
    )
    allApplications.value = res.data || []
    pagination.value.total = allApplications.value.length
    pagination.value.current = 1
  } catch (e: any) {
    message.error(e.message)
  } finally {
    loading.value = false
  }
}

function handleTableChange(pag: any) {
  pagination.value.current = pag.current
  pagination.value.pageSize = pag.pageSize
}

async function handleApply(applyId: string, status: 'APPROVED' | 'REJECTED') {
  try {
    actionLoading.value = applyId
    const handleData: Req.HandleApplicationReq = {
      applicationId: applyId,
      status,
    }
    await SumApi.handleApplication(handleData)
    message.success(`Application ${status.toLowerCase()} successfully`)
    await query()
    emit('reload')
  } catch (e: any) {
    message.error(e.message)
  } finally {
    actionLoading.value = ''
  }
}

function getStatusColor(status: string): string {
  switch (status) {
    case 'PENDING':
      return 'orange'
    case 'APPROVED':
      return 'green'
    case 'REJECTED':
      return 'red'
    default:
      return 'default'
  }
}
</script>

<style scoped lang="scss">
.filter-section {
  margin-bottom: 16px;
  padding: 16px;
  background: #fafafa;
  border-radius: 8px;
}

.apply-table-section {
  .user-info {
    display: flex;
    align-items: center;

    .user-avatar {
      margin-right: 12px;

      img {
        width: 32px;
        height: 32px;
        border-radius: 50%;
        object-fit: cover;
      }
    }

    .user-details {
      .user-name {
        font-size: 14px;
        font-weight: 500;
        color: #333;
      }
    }
  }

  .status-tag {
    font-weight: 500;
  }

  .action-placeholder {
    color: #ccc;
    font-size: 14px;
  }
}
</style>
