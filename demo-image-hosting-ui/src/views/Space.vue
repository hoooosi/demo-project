<template>
  <div class="space-detail" v-if="space">
    <div class="page-content">
      <ImageList
        :modelValue="list"
        :mask="permissionMask"
        @reload="query"
        :loading="loading"
      >
        <template #header-left>
          <div class="title-container">
            <!-- Title -->
            <div v-if="space" class="space-title-wrapper">
              <h3 class="space-title">
                {{ space.name }}
              </h3>
              <!-- Info Card -->
              <div class="space-card-container">
                <SpaceCard :modelValue="space" />
              </div>
            </div>
          </div>
          <span class="item-count">({{ page.total || 0 }})</span>
        </template>

        <template #header-right>
          <template v-if="permissionMask & Const.PermissionMask.SPACE_MANGE">
            <MemberManagementModal :sid="sid" />
            <EditSpaceModal :space="space" @reload="loadSpace" />
            <JoinRequestModal :modelValue="sid" />
          </template>
          <template v-if="permissionMask & Const.PermissionMask.IMAGE_UPLOAD">
            <UploadModal :spaceId="space.id" @reload="query" />
          </template>
          <template v-if="true">
            <a-button
              v-if="
                permissionMask == 0n &&
                !(permissionMask & Const.PermissionMask.SPACE_JOIN)
              "
              type="primary"
              @click="join"
            >
              <UserAddOutlined />Join
            </a-button>
            <a-button
              v-else-if="permissionMask == Const.PermissionMask.LONG_MAX"
              danger
              @click="deleteSpace"
            >
              <DeleteOutlined />Delete
            </a-button>
            <a-button v-else-if="permissionMask != 0n" @click="leave">
              <LogoutOutlined />Quit
            </a-button>
          </template>
        </template>
      </ImageList>
    </div>
    <!-- Page -->
    <div class="pagination"><Pagination v-model="page" @reload="query" /></div>
  </div>
</template>

<script setup lang="ts">
import { ref, watch, computed, onMounted } from "vue";
import ImageList from "@/components/ImageList.vue";
import { message, Modal } from "ant-design-vue";
import {
  DeleteOutlined,
  UserAddOutlined,
  LogoutOutlined,
} from "@ant-design/icons-vue";
import { SumApi, ImageApi } from "@/api";
import UploadModal from "@/components/modal/UploadModal.vue";
import MemberManagementModal from "@/components/modal/MemberMangeModal.vue";
import EditSpaceModal from "@/components/modal/EditSpaceModal.vue";
import JoinRequestModal from "@/components/modal/ApplyModal.vue";
import { useRoute, useRouter } from "vue-router";
import SpaceListModal from "@/components/modal/SpaceListModal.vue";
import SpaceCard from "@/components/SpaceCard.vue";
import * as Const from "@/const";
import * as Utils from "@/utils";
import { MetaInfo } from "@/router";
import Pagination from "@/components/Pagination.vue";

const route = useRoute();
const router = useRouter();
const sid = computed<string>(() => route.params.sid as string);
const space = ref<VO.SpaceVO | null>(null);

const permissionMask = ref<bigint>(0n);
const loading = ref(false);
const page = ref<Page<VO.ImageVO>>(Const.page());
const list = computed(() => page.value.records);
const queryParams = ref({ asc: false });

async function loadSpace() {
  try {
    const res = await SumApi.getSpace(sid.value);
    if (res.data) space.value = res.data;
  } catch (e: any) {
    message.error(e.message);
    await Utils.wait(1000);
    router.push({ name: MetaInfo.Spaces.name });
  }
}

async function loadPermissionMask() {
  try {
    const res = await SumApi.getSpacePermission(sid.value);
    permissionMask.value = BigInt(res.data);
  } catch (e: any) {
    message.error(e.message);
  }
}

async function query() {
  if (loading.value || !sid.value) return;
  loading.value = true;
  try {
    await new Promise((resolve) => setTimeout(resolve, 100));
    const res = await ImageApi.pageBySpace({
      current: page.value.current,
      size: page.value.size,
      spaceId: sid.value,
      ...queryParams.value,
    });
    page.value = Utils.conversionPage(res.data);
  } catch (e: any) {
    message.error(e.message);
  } finally {
    loading.value = false;
  }
}

async function deleteSpace() {
  Modal.confirm({
    title: "Confirm Delete Space",
    content: `Are you sure you want to delete space "${space.value?.name}"? This action cannot be undone and will delete all pictures in this space.`,
    okText: "Delete",
    cancelText: "Cancel",
    okType: "danger",
    onOk: async () => {
      try {
        await SumApi.deleteSpace(space.value?.id as string);
        message.success("Space deleted successfully");
        router.push({ name: MetaInfo.Spaces.name });
      } catch (error: any) {
        message.error(error.message);
      } finally {
      }
    },
  });
}
async function leave() {
  Modal.confirm({
    title: "Confirm Leave Space",
    content: `Are you sure you want to leave space "${space.value?.name}"?`,
    okText: "Leave",
    cancelText: "Cancel",
    okType: "danger",
    onOk: async () => {
      try {
        await SumApi.exitSpace(sid.value);
        message.success("Successfully left the space");
        router.push({ name: MetaInfo.Spaces.name });
      } catch (error: any) {
        message.error(error.message);
      } finally {
      }
    },
  });
}

async function join() {
  try {
    await SumApi.apply(sid.value);
    message.info("Joining space requires an invitation code or admin approval");
  } catch (error: any) {
    message.error(error.message);
  } finally {
  }
}

onMounted(async () => {
  await loadSpace();
  await loadPermissionMask();
});
</script>

<style scoped lang="scss">
.space-detail {
  flex: 1;
  height: 100%;
  display: flex;
  flex-direction: column;
  background: #f5f5f5;

  .page-content {
    flex: 1;
    display: flex;
    flex-direction: column;
    overflow: hidden;
    position: relative;

    .title-container {
      .space-card-container {
        position: absolute;
        z-index: 1;
        opacity: 0;
        visibility: hidden;
        transition: 0.3s ease-in-out;
        width: 20em;
      }

      :hover {
        .space-card-container {
          opacity: 1;
          visibility: visible;
        }
      }
    }
  }

  .pagination {
    padding: 16px 24px;
    display: flex;
    justify-content: center;
  }
}

.member-management {
  .add-member-section {
    margin-bottom: 20px;

    h4 {
      margin-bottom: 16px;
      color: #1890ff;
      font-weight: 600;
    }
  }

  .member-list-section {
    h4 {
      margin-bottom: 16px;
      color: #1890ff;
      font-weight: 600;
    }
  }
}

:deep(.ant-table) {
  .ant-table-thead > tr > th {
    background-color: #fafafa;
    font-weight: 600;
  }
}

:deep(.ant-form-inline .ant-form-item) {
  margin-right: 16px;
  margin-bottom: 16px;
}
</style>
