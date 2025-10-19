<template>
  <a-button type="primary" @click="openModal">
    <SearchOutlined />
    BROWSE SPACES
  </a-button>

  <a-modal
    v-model:open="visible"
    title="BROWSE PUBLIC SPACES"
    width="1000px"
    :footer="null"
  >
    <!-- SEARCH & FILTER SECTION -->
    <div class="filter-section">
      <a-form layout="inline">
        <a-form-item label="SPACE NAME">
          <a-input
            v-model:value="queryParams.name"
            placeholder="Search by space name"
            style="width: 250px"
            allow-clear
            @pressEnter="handleSearch"
          >
            <template #prefix>
              <SearchOutlined />
            </template>
          </a-input>
        </a-form-item>

        <a-form-item>
          <a-checkbox v-model:checked="queryParams.notShowJoined">
            Hide Joined Spaces
          </a-checkbox>
        </a-form-item>

        <a-form-item>
          <a-button type="primary" @click="handleSearch"> Search </a-button>
        </a-form-item>
      </a-form>
    </div>

    <!-- TABLE SECTION -->
    <div class="table-section">
      <a-table
        :columns="COLUMNS_CONFIG"
        :data-source="list"
        :loading="loading"
        :pagination="false"
        row-key="id"
      >
        <template #bodyCell="{ column, record }">
          <template v-if="column.key === 'name'">
            <div class="space-name-cell">
              <TeamOutlined class="space-icon" />
              <span class="space-name-text">{{ record.name }}</span>
            </div>
          </template>

          <template v-if="column.key === 'storage'">
            <div class="storage-info">
              <a-progress
                :percent="getStoragePercent(record)"
                :show-info="false"
                size="small"
                :stroke-color="getProgressColor(getStoragePercent(record))"
              />
              <span class="storage-text">
                {{ formatSize(record.totalSize) }} /
                {{ formatSize(record.maxSize) }}
              </span>
            </div>
          </template>

          <template v-if="column.key === 'createTime'">
            {{ formatDate(record.createTime) }}
          </template>

          <template v-if="column.key === 'permissions'">
            <a-tooltip placement="top">
              <template #title>
                <div class="permission-tooltip">
                  <div
                    v-for="perm in getPermissionNames(
                      record.publicPermissionMask
                    )"
                    :key="perm"
                  >
                    • {{ perm }}
                  </div>
                  <div
                    v-if="
                      getPermissionNames(record.publicPermissionMask).length ===
                      0
                    "
                  >
                    No public permissions
                  </div>
                </div>
              </template>
              <SafetyOutlined class="permission-icon" />
            </a-tooltip>
          </template>

          <template v-if="column.key === 'action'">
            <a-button
              v-if="checkIfJoined(record.id)"
              type="default"
              size="small"
              disabled
              class="joined-btn"
            >
              <CheckOutlined />
              JOINED
            </a-button>
            <a-button
              v-else-if="checkIfPending(record.id)"
              type="default"
              size="small"
              disabled
              class="pending-btn"
            >
              <ClockCircleOutlined />
              PENDING
            </a-button>
            <a-button
              v-else
              type="primary"
              size="small"
              @click="handleApply(record.id)"
              :loading="applyingSpaceId === record.id"
              class="apply-btn"
            >
              <UserAddOutlined />
              JOIN
            </a-button>
          </template>
        </template>
      </a-table>
    </div>

    <!-- PAGINATION -->
    <Pagination v-model="page" @reload="query" />
  </a-modal>
</template>

<script setup lang="ts">
import { computed, ref } from "vue";
import { message } from "ant-design-vue";
import { SumApi } from "@/api";
import {
  SearchOutlined,
  TeamOutlined,
  SafetyOutlined,
  UserAddOutlined,
  CheckOutlined,
  ClockCircleOutlined,
} from "@ant-design/icons-vue";
import Pagination from "@/components/Pagination.vue";
import * as CONST from "@/const";
import * as Utils from "@/utils";

const emit = defineEmits<{
  reload: [];
}>();

const visible = ref(false);
const loading = ref(false);
const applyingSpaceId = ref<string>("");
const page = ref<Page<VO.SpaceVO>>(CONST.page());
const queryParams = ref({
  name: undefined as string | undefined,
  notShowJoined: true,
});

// 已加入的空间ID集合（可以从其他地方获取或通过API获取）
const joinedSpaceIds = ref<Set<string>>(new Set());
// 待处理的申请空间ID集合
const pendingSpaceIds = ref<Set<string>>(new Set());

const list = computed<VO.SpaceVO[]>(() => page.value.records);

const COLUMNS_CONFIG = [
  {
    title: "SPACE NAME",
    key: "name",
    width: 250,
  },
  {
    title: "STORAGE",
    key: "storage",
    width: 200,
  },
  {
    title: "CREATED",
    key: "createTime",
    width: 150,
  },
  {
    title: "PERMISSIONS",
    key: "permissions",
    width: 120,
    align: "center" as const,
  },
  {
    title: "ACTION",
    key: "action",
    width: 140,
    align: "center" as const,
  },
];

async function openModal() {
  visible.value = true;
  await loadSpaceStatus();
  await query();
}

// 加载空间状态（已加入和待处理）
async function loadSpaceStatus() {
  try {
    // 获取已加入的空间ID列表
    const joinedRes = await SumApi.listJoinedSpaceIds();
    joinedSpaceIds.value = new Set(joinedRes.data || []);

    // 获取待处理的申请，筛选出PENDING状态的空间ID
    const applicationsRes = await SumApi.pageApplicationByOperator({
      status: "PENDING",
    });
    const pendingIds = (applicationsRes.data || [])
      .filter((app) => app.status === "PENDING")
      .map((app) => app.spaceId);
    pendingSpaceIds.value = new Set(pendingIds);
  } catch (e: any) {
    console.error("Failed to load space status:", e);
  }
}

async function query() {
  try {
    loading.value = true;
    const res = await SumApi.pageSpaceByPublic({
      current: page.value.current,
      size: 10,
      name: queryParams.value.name,
      notShowJoined: queryParams.value.notShowJoined,
    });
    page.value = Utils.conversionPage(res.data);
  } catch (e: any) {
    message.error(e.message);
  } finally {
    loading.value = false;
  }
}

async function handleSearch() {
  page.value.current = 1;
  await query();
}

async function handleApply(spaceId: string) {
  try {
    applyingSpaceId.value = spaceId;
    await SumApi.apply(spaceId);
    message.success("Application submitted successfully!");
    pendingSpaceIds.value.add(spaceId);
    emit("reload");
  } catch (e: any) {
    message.error(e.message);
  } finally {
    applyingSpaceId.value = "";
  }
}

function checkIfJoined(spaceId: string): boolean {
  return joinedSpaceIds.value.has(spaceId);
}

function checkIfPending(spaceId: string): boolean {
  return pendingSpaceIds.value.has(spaceId);
}

function getStoragePercent(space: VO.SpaceVO): number {
  const total = Number(space.totalSize);
  const max = Number(space.maxSize);
  if (max === 0) return 0;
  return Math.min(Math.round((total / max) * 100), 100);
}

function getProgressColor(percent: number): string {
  if (percent < 50) return "#52c41a";
  if (percent < 80) return "#faad14";
  return "#ff4d4f";
}

function formatSize(size: string | number): string {
  return Utils.formatSize(String(size));
}

function formatDate(date: string): string {
  return Utils.formatDate(date);
}

function getPermissionNames(mask: string): string[] {
  const permissions: string[] = [];
  const maskBigInt = BigInt(mask);

  if ((maskBigInt & CONST.PermissionMask.IMAGE_VIEW) !== 0n) {
    permissions.push("View");
  }
  if ((maskBigInt & CONST.PermissionMask.IMAGE_UPLOAD) !== 0n) {
    permissions.push("Upload");
  }
  if ((maskBigInt & CONST.PermissionMask.IMAGE_EDIT) !== 0n) {
    permissions.push("Edit");
  }
  if ((maskBigInt & CONST.PermissionMask.IMAGE_DELETE) !== 0n) {
    permissions.push("Delete");
  }

  return permissions;
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
  .space-name-cell {
    display: flex;
    align-items: center;
    gap: 10px;

    .space-icon {
      font-size: 18px;
      color: #1890ff;
    }

    .space-name-text {
      font-size: 14px;
      font-weight: 600;
      color: #333;
    }
  }

  .storage-info {
    display: flex;
    flex-direction: column;
    gap: 4px;

    .storage-text {
      font-size: 12px;
      color: #8c8c8c;
    }
  }

  .permission-icon {
    font-size: 18px;
    color: #52c41a;
    cursor: pointer;
    transition: all 0.3s;

    &:hover {
      color: #73d13d;
      transform: scale(1.1);
    }
  }

  .permission-tooltip {
    text-align: left;

    div {
      padding: 2px 0;
    }
  }

  .apply-btn {
    display: flex;
    align-items: center;
    gap: 4px;
  }

  .joined-btn {
    display: flex;
    align-items: center;
    gap: 4px;
    background: #f6ffed;
    border-color: #b7eb8f;
    color: #52c41a;
    cursor: not-allowed;
  }

  .pending-btn {
    display: flex;
    align-items: center;
    gap: 4px;
    background: #fff7e6;
    border-color: #ffd591;
    color: #faad14;
    cursor: not-allowed;
  }
}
</style>
