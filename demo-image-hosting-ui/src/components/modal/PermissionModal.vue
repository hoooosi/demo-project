<template>
  <a-button @click="openModal" :disabled="selfMask == PermissionMask.LONG_MAX">CONFIG</a-button>
  <a-modal v-model:open="visible" title="MEMBER PERMISSION" :footer="null" width="600px">
    <div class="member-info">
      <div class="member-avatar">
        <img :src="props.modelValue.avatar" :alt="props.modelValue.name" />
      </div>
      <div class="member-details">
        <div class="member-name">{{ props.modelValue.name }}</div>
        <div class="member-account">{{ props.modelValue.account }}</div>
      </div>
    </div>

    <a-form layout="vertical">
      <a-form-item label="PERMISSION" required>
        <div class="permission-selector">
          <div class="permission-title">MEMBER PERMISSION</div>
          <div class="permission-content">
            <a-checkbox-group v-model:value="permissions" class="permission-checkbox-group">
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

      <a-form-item>
        <a-row :gutter="16">
          <a-col :span="12">
            <a-button block @click="visible = false">取消</a-button>
          </a-col>
          <a-col :span="12">
            <a-button type="primary" block @click="handleUpdate" :loading="loading">
              SAVE
            </a-button>
          </a-col>
        </a-row>
      </a-form-item>
    </a-form>
  </a-modal>
</template>

<script setup lang="ts">
import { computed, ref } from 'vue'
import { message } from 'ant-design-vue'
import * as utils from '@/utils'
import { PermissionMask } from '@/const'
import { SumApi } from '@/api'

const props = defineProps<{
  modelValue: VO.MemberVo
}>()

const emit = defineEmits<{
  reload: []
}>()

const selfMask = computed<bigint>(() => BigInt(props.modelValue.permissionMask))
const loading = ref(false)
const visible = ref(false)
const permissions = ref<bigint[]>([])
const MEMBER_PERMISSIONS = [
  PermissionMask.IMAGE_DELETE,
  PermissionMask.IMAGE_UPLOAD,
  PermissionMask.IMAGE_EDIT,
]

function openModal() {
  visible.value = true
  permissions.value = utils.getPermissionList(BigInt(props.modelValue.permissionMask))
}

async function handleUpdate() {
  try {
    loading.value = true
    const updateData: Req.EditMemberReq = {
      memberId: props.modelValue.id,
      mask: utils.getPermissionMask(permissions.value),
    }

    await SumApi.editMember(updateData)
    message.success('UPDATE SUCCESS')
    visible.value = false
    emit('reload')
  } catch (e: any) {
    message.error(e.message)
  } finally {
    loading.value = false
  }
}
</script>

<style scoped lang="scss">
.member-info {
  display: flex;
  align-items: center;
  margin-bottom: 24px;
  padding: 16px;
  background: #fafafa;
  border-radius: 8px;

  .member-avatar {
    margin-right: 16px;

    img {
      width: 48px;
      height: 48px;
      border-radius: 50%;
      object-fit: cover;
    }
  }

  .member-details {
    .member-name {
      font-size: 16px;
      font-weight: bold;
      color: #333;
      margin-bottom: 4px;
    }

    .member-account {
      font-size: 14px;
      color: #666;
    }
  }
}

.permission-selector {
  border: 1px solid #e8e8e8;
  border-radius: 8px;
  padding: 16px;
  background: #fafafa;

  .permission-title {
    font-size: 16px;
    font-weight: bold;
    margin-bottom: 12px;
  }

  .permission-content {
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
}
</style>
