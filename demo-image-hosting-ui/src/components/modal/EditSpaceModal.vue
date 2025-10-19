<template>
  <a-button type="primary" @click="openModal">
    <EditOutlined v-if="props.space" />
    <PlusOutlined v-else />
    {{ props.space ? "EDIT" : "CREATE" }}
  </a-button>
  <a-modal
    v-model:open="visible"
    :title="props.space ? 'EDIT' : 'CREATE'"
    :footer="null"
    width="900px"
  >
    <a-form ref="editFormRef" :model="form" layout="vertical">
      <!-- Name -->
      <a-form-item label="NAME" name="name" required>
        <a-input
          v-model:value="form.name"
          placeholder="Please input space name"
        />
      </a-form-item>

      <!-- Size of storage -->
      <a-form-item required>
        <a-row :gutter="16">
          <a-col :span="12">
            <a-form-item label="MAX STORAGE" required>
              <div class="slider-container">
                <a-slider
                  v-model:value="form.maxSize"
                  :min="100"
                  :max="10240"
                  :marks="CONFIG.storageMarks"
                />
              </div>
            </a-form-item>
          </a-col>
        </a-row>
      </a-form-item>

      <!-- Permission -->
      <a-form-item label="PERMISSION" required>
        <div class="permission-selector">
          <div style="font-size: 16px; font-weight: bold">CONFIG</div>
          <div style="margin-top: 10px">
            <a-checkbox-group
              v-model:value="publicPermissionList"
              class="permission-checkbox-group"
            >
              <a-checkbox
                v-for="permission of CONFIG_PERMISSIONS"
                :key="permission"
                :value="permission"
              >
                {{ utils.getPermissionName(permission) }}
              </a-checkbox>
            </a-checkbox-group>
          </div>

          <div style="font-size: 16px; font-weight: bold; margin-top: 20px">
            MEMBER PERMISSION
          </div>
          <div style="margin-top: 10px">
            <a-checkbox-group
              v-model:value="memberPermissionList"
              class="permission-checkbox-group"
            >
              <a-checkbox
                v-for="permission of MEMBER_PERMISSIONS"
                :key="permission"
                :value="permission"
              >
                {{ utils.getPermissionName(permission) }}
              </a-checkbox>
            </a-checkbox-group>
          </div>
        </div>
      </a-form-item>

      <!-- Submib -->
      <a-form-item>
        <a-row :gutter="16">
          <a-col :span="12">
            <a-button block @click="visible = false"> 取消 </a-button>
          </a-col>
          <a-col :span="12">
            <a-button
              type="primary"
              block
              @click="handleSubmit"
              :loading="loading"
            >
              {{ props.space ? "SAVE" : "CREATE" }}
            </a-button>
          </a-col>
        </a-row>
      </a-form-item>
    </a-form>
  </a-modal>
</template>

<script setup lang="ts">
import { ref } from "vue";
import { message } from "ant-design-vue";
import { EditOutlined, PlusOutlined } from "@ant-design/icons-vue";
import { SumApi } from "@/api";
import { PermissionMask } from "@/const";
import * as utils from "@/utils";

const props = defineProps<{
  space?: VO.SpaceVO;
}>();
const emit = defineEmits<{
  reload: [];
}>();

const loading = ref(false);
const visible = ref(false);

const CONFIG = {
  storageMarks: {
    100: "100MB",
    1024: "1GB",
    2048: "2GB",
    5120: "5GB",
    10240: "10GB",
    20480: "20GB",
  },
  countMarks: {
    10: "10",
    250: "250",
    500: "500",
    750: "750",
    1000: "1000",
  },
};

const FORM_INIT = {
  id: "",
  name: "",
  maxSize: 5120, // MB
  maxCount: 500,
};
const form = ref(FORM_INIT);

const memberPermissionList = ref<bigint[]>([]);
const publicPermissionList = ref<bigint[]>([]);

const MEMBER_PERMISSIONS = [
  PermissionMask.IMAGE_DELETE,
  PermissionMask.IMAGE_UPLOAD,
  PermissionMask.IMAGE_EDIT,
];
const CONFIG_PERMISSIONS = [
  PermissionMask.SPACE_JOIN,
  PermissionMask.SPACE_AUTO_APPROVE,
  PermissionMask.SPACE_SEARCH,
  PermissionMask.SPACE_VIEW,
  PermissionMask.IMAGE_VIEW,
];

function openModal() {
  visible.value = true;
  if (props.space) {
    form.value.id = props.space.id;
    form.value.name = props.space.name;
    form.value.maxCount = FORM_INIT.maxCount;
    form.value.maxSize = Number(BigInt(props.space.maxSize) / 1024n / 1024n);
    publicPermissionList.value = utils.getPermissionList(
      BigInt(props.space.publicPermissionMask)
    );
    memberPermissionList.value = utils.getPermissionList(
      BigInt(props.space.memberPermissionMask)
    );
  } else {
    form.value = FORM_INIT;
    publicPermissionList.value = [
      PermissionMask.SPACE_SEARCH,
      PermissionMask.SPACE_JOIN,
      PermissionMask.SPACE_VIEW,
    ];
    memberPermissionList.value = [
      PermissionMask.IMAGE_UPLOAD,
      PermissionMask.IMAGE_EDIT,
    ];
  }
}

async function handleSubmit() {
  try {
    loading.value = true;
    if (props.space) await handleEditSpace();
    else await handleCreateSpace();
    emit("reload");
  } catch (e: any) {
    message.error(e.message);
  } finally {
    loading.value = false;
  }
}
async function handleCreateSpace() {
  const createData: Req.AddSpaceReq = {
    name: form.value.name,
    maxSize: (BigInt(form.value.maxSize) * 1024n * 1024n).toString(),

    publicPermissionMask: utils.getPermissionMask(publicPermissionList.value),
    memberPermissionMask: utils.getPermissionMask(memberPermissionList.value),
  };

  await SumApi.createSpace(createData);
  message.success("CREATE SPACE SUCCESS");
  visible.value = false;
}

async function handleEditSpace() {
  const editData: Req.EditSpaceReq = {
    spaceId: form.value.id,
    name: form.value.name,
    maxSize: (BigInt(form.value.maxSize) * 1024n * 1024n).toString(),
    maxCount: form.value.maxCount.toString(),
    publicPermissionMask: utils.getPermissionMask(publicPermissionList.value),
    memberPermissionMask: utils.getPermissionMask(memberPermissionList.value),
  };

  await SumApi.editSpace(editData);
  message.success("UPDATE SPACE SUCCESS");
  visible.value = false;
}
</script>

<style scoped lang="scss">
.permission-selector {
  border: 1px solid #e8e8e8;
  border-radius: 8px;
  padding: 16px;
  background: #fafafa;

  .permission-checkbox-group {
    display: flex;
    flex-wrap: wrap;
    gap: 16px;

    .ant-checkbox-wrapper {
      margin: 0;
      padding: 8px 12px;
      border: 1px solid #e8e8e8;
      border-radius: 6px;
      transition: all 0.3s ease;
      background: white;

      &:hover {
        border-color: #1890ff;
        background: #f0f8ff;
      }

      &.ant-checkbox-wrapper-checked {
        border-color: #1890ff;
        background: #e6f7ff;
      }
    }
  }
}

.ant-input-group {
  display: flex;

  .ant-input-number {
    border-top-right-radius: 0;
    border-bottom-right-radius: 0;
  }

  .ant-select {
    .ant-select-selector {
      border-top-left-radius: 0;
      border-bottom-left-radius: 0;
      border-left: 0;
    }
  }
}

.slider-container {
  padding: 0 8px;

  .ant-slider {
    margin-bottom: 16px;
  }
}
</style>
