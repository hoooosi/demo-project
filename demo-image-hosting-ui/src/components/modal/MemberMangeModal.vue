<template>
  <template v-if="sid">
    <a-button @click="openModal"> <TeamOutlined /> MEMBER </a-button>
    <a-modal v-model:open="visible" title="MEMBER MANGE" width="800px" :footer="null">
      <div class="member-list-section">
        <a-table
          :columns="COLUMNS_CONFIG"
          :data-source="list"
          :loading="loading"
          :pagination="false"
          row-key="id"
        >
          <template #bodyCell="{ column, record }">
            <template v-if="column.key === 'createTime'">
              {{ new Date(record.createTime).toLocaleString() }}
            </template>
            <template v-if="column.key === 'permissionMask'">
              <PermissionModal :model-value="record" @reload="loadList" />
            </template>
            <template v-if="column.key === 'action'">
              <a-button
                type="link"
                danger
                @click="remove(record)"
                :disabled="BigInt(record.permissionMask) === PermissionMask.LONG_MAX"
              >
                REMOVE
              </a-button>
            </template>
          </template>
        </a-table>
      </div>
    </a-modal>
  </template>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { message, Modal } from 'ant-design-vue'
import { SumApi } from '@/api'
import { TeamOutlined } from '@ant-design/icons-vue'
import { PermissionMask } from '@/const'
import PermissionModal from './PermissionModal.vue'

const props = defineProps<{
  sid: string
}>()

const visible = ref(false)
const loading = ref(false)
const list = ref<VO.MemberVo[]>([])

const COLUMNS_CONFIG = [
  {
    title: 'ACCOUNT',
    dataIndex: 'account',
    key: 'account',
  },
  {
    title: 'JOIN TIME',
    key: 'createTime',
  },
  {
    title: 'PERMISSION MASK',
    key: 'permissionMask',
  },
  {
    title: 'OPT',
    key: 'action',
  },
]

async function openModal() {
  visible.value = true
  await loadList()
}

async function loadList() {
  try {
    loading.value = true
    const res = await SumApi.listMember(props.sid)
    list.value = res.data || []
    visible.value = true
  } catch (e: any) {
    message.error(e.message)
  } finally {
    loading.value = false
  }
}

async function remove(member: VO.MemberVo) {
  Modal.confirm({
    title: 'Confirm Remove Member',
    content: `Are you sure you want to remove user ${member.account}?`,
    onOk: async () => {
      try {
        await SumApi.removeMember(member.id)
        message.success('Member removed successfully')
        await loadList()
      } catch (error) {
        message.error('Failed to remove member')
      }
    },
  })
}
</script>

<style scoped lang="scss">
.member-list-section {
  h4 {
    margin-bottom: 16px;
    font-weight: 600;
  }
}
</style>
