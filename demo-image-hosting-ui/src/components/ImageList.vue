<template>
  <div class="image-list-shell">
    <!-- HEADER -->
    <template v-if="true">
      <div class="header-shell">
        <div>
          <slot name="header-left"> </slot>
        </div>
        <div>
          <template
            v-if="
              mask & CONST.PermissionMask.IMAGE_DELETE && modelValue.length > 0
            "
          >
            <template v-if="multiSelectMode">
              <a-button @click="selectAll">ALL</a-button>
              <a-button @click="selectedList = []">CLEAR</a-button>
              <a-button
                danger
                @click="batchDel"
                :disabled="selectedList.length === 0"
              >
                <DeleteOutlined />DELETE ({{ selectedList.length }})
              </a-button>
              <a-button @click="(multiSelectMode = false), (selectedList = [])"
                >EXIT</a-button
              >
            </template>
            <template v-else>
              <a-button @click="toggleMultiSelectMode">
                <SettingOutlined /> BATCH
              </a-button>
            </template>
          </template>
          <template v-if="!multiSelectMode">
            <slot name="header-right" v-if="!multiSelectMode"> </slot>
          </template>
        </div>
      </div>
    </template>
    <!-- CONTENT -->
    <template v-if="true">
      <div class="content-body" style="position: relative">
        <GridCom :loading="props.loading">
          <template v-if="modelValue.length == 0 && !loading">
            <NoData />
          </template>
          <template v-else>
            <ImageCard
              v-for="item in modelValue"
              :key="item.id"
              :permissionMask="mask"
              :modelValue="item"
              :is-multi-select-mode="multiSelectMode"
              :selected="selectedList.includes(item.id)"
              :class="{ selected: selectedList.includes(item.id) }"
              @select="toggleSelection"
              @reload="emit('reload')"
            />
          </template>
        </GridCom>
      </div>
    </template>
  </div>
</template>

<script setup lang="ts">
import { computed, ref } from "vue";
import { message, Modal } from "ant-design-vue";
import { DeleteOutlined, SettingOutlined } from "@ant-design/icons-vue";
import { ImageApi } from "@/api";
import ImageCard from "./ImageCard.vue";
import GridCom from "@/components/GridCom.vue";
import NoData from "@/components/NoData.vue";

import * as CONST from "@/const";

const props = withDefaults(
  defineProps<{
    loading?: boolean;
    modelValue?: VO.ImageVO[];
    mask?: bigint;
  }>(),
  {
    loading: false,
    modelValue: () => [] as VO.ImageVO[],
    mask: () => 0n,
  }
);

const emit = defineEmits<{
  reload: [];
}>();

const multiSelectMode = ref(false);
const selectedList = ref<string[]>([]);

function toggleMultiSelectMode() {
  multiSelectMode.value = true;
  selectedList.value = [];
}

function toggleSelection(id: string) {
  const index = selectedList.value.indexOf(id);
  if (index > -1) {
    selectedList.value.splice(index, 1);
  } else {
    selectedList.value.push(id);
  }
}

function selectAll() {
  if (!props.modelValue) return;
  selectedList.value = props.modelValue.map((picture) => picture.id);
}

async function batchDel() {
  if (selectedList.value.length === 0) {
    message.warning("Please select pictures to delete first");
    return;
  }
  Modal.confirm({
    title: "Confirm Batch Delete",
    content: `Are you sure you want to delete the selected ${selectedList.value.length} pictures? This action cannot be undone.`,
    onOk: async () => {
      try {
        await ImageApi.deleteBatch(selectedList.value);
        message.success(
          `Successfully deleted ${selectedList.value.length} pictures`
        );
        selectedList.value = [];
        emit("reload");
      } catch (error: any) {
        message.error(error.message);
      }
    },
  });
}
</script>

<style lang="scss">
.image-list-shell {
  height: 100%;
  flex: 1;
  display: flex;
  flex-direction: column;

  .header-shell {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 16px 24px;

    > div {
      display: flex;
      align-items: center;
      gap: 12px;
    }
  }

  .content-body {
    flex: 1;
    overflow-y: auto;
    text-align: center;
    display: flex;
  }
}
</style>
