<template>
  <a-button type="primary" @click="openModal">
    <UserAddOutlined />
    JOIN REQUEST
  </a-button>

  <a-modal
    v-model:open="visible"
    title="MY JOIN REQUESTS"
    width="900px"
    :footer="null"
  >
    <!-- FILTER SECTION -->
    <div class="filter-section">
      <a-form layout="inline">
        <a-form-item label="STATUS">
          <a-select
            v-model:value="queryParams.status"
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

    <!-- TABLE SECTION -->
    <div class="table-section">
      <a-table
        :columns="COLUMNS_CONFIG"
        :data-source="list"
        :loading="loading"
        :pagination="pagination"
        row-key="id"
        @change="handleTableChange"
      >
        <template #bodyCell="{ column, record }">
          <template v-if="column.key === 'space'">
            <div class="space-info">
              <div class="space-details">
                <div class="space-name">{{ record.spaceName }}</div>
                <div class="space-owner">
                  Owner: {{ record.ownerName || "-" }}
                </div>
              </div>
            </div>
          </template>

          <template v-if="column.key === 'createTime'">
            {{ new Date(record.createTime).toLocaleString() }}
          </template>

          <template v-if="column.key === 'updateTime'">
            {{
              record.updateTime
                ? new Date(record.updateTime).toLocaleString()
                : "-"
            }}
          </template>

          <template v-if="column.key === 'status'">
            <a-tag :color="getStatusColor(record.status)" class="status-tag">
              {{ record.status }}
            </a-tag>
          </template>
        </template>
      </a-table>
    </div>
  </a-modal>
</template>

<script setup lang="ts">
import { ref, computed } from "vue";
import { message } from "ant-design-vue";
import { SumApi } from "@/api";
import { UserAddOutlined } from "@ant-design/icons-vue";

const emit = defineEmits<{
  reload: [];
}>();

const visible = ref(false);
const loading = ref(false);
const allApplications = ref<VO.ApplicationVO[]>([]);
const queryParams = ref({
  status: undefined as string | undefined,
});

// 分页配置
const pagination = ref({
  current: 1,
  pageSize: 10,
  total: 0,
  showSizeChanger: true,
  showQuickJumper: true,
  showTotal: (total: number) => `Total ${total} items`,
});

// 根据分页和筛选条件计算当前页数据
const list = computed<VO.ApplicationVO[]>(() => {
  const start = (pagination.value.current - 1) * pagination.value.pageSize;
  const end = start + pagination.value.pageSize;
  return allApplications.value.slice(start, end);
});

const COLUMNS_CONFIG = [
  {
    title: "SPACE",
    key: "space",
    width: 250,
  },
  {
    title: "APPLY TIME",
    key: "createTime",
    width: 180,
  },
  {
    title: "UPDATE TIME",
    key: "updateTime",
    width: 180,
  },
  {
    title: "STATUS",
    key: "status",
    width: 120,
  },
];

async function openModal() {
  visible.value = true;
  await query();
}

async function query() {
  try {
    loading.value = true;
    const res = await SumApi.pageApplicationByOperator({
      status: queryParams.value.status,
    });
    allApplications.value = res.data || [];
    pagination.value.total = allApplications.value.length;
    pagination.value.current = 1;
  } catch (e: any) {
    message.error(e.message);
  } finally {
    loading.value = false;
  }
}

function handleTableChange(pag: any) {
  pagination.value.current = pag.current;
  pagination.value.pageSize = pag.pageSize;
}

function getStatusColor(status: string): string {
  switch (status) {
    case "PENDING":
      return "orange";
    case "APPROVED":
      return "green";
    case "REJECTED":
      return "red";
    default:
      return "default";
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

.table-section {
  .space-info {
    display: flex;
    align-items: center;

    .space-details {
      .space-name {
        font-size: 14px;
        font-weight: 600;
        color: #333;
        margin-bottom: 4px;
      }

      .space-owner {
        font-size: 12px;
        color: #8c8c8c;
      }
    }
  }

  .status-tag {
    font-weight: 500;
  }
}
</style>
