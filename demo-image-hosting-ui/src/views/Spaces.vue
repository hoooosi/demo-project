<template>
  <div class="spaces-shell">
    <!-- SEARCH -->
    <template v-if="queryParams">
      <div class="search-shell">
        <div class="search-section">
          <a-input
            v-model:value="queryParams.name"
            placeholder="Please enter space name"
            style="width: 200px"
            allow-clear
          >
            <template #suffix>
              <SearchOutlined style="color: #bfbfbf" />
            </template>
          </a-input>
          <Sort v-model="queryParams.asc" />
        </div>
        <div class="opt-section">
          <EditSpaceModal @reload="query"></EditSpaceModal>
          <JoinModal />
          <SpaceListModal />
        </div>
      </div>
    </template>
    <!-- SPACE LIST -->
    <template v-if="list">
      <div class="page-content">
        <div class="space-list">
          <div class="content-body">
            <GridCom
              :loading="loading"
              :aspect-ratio="1"
              :itemMinWidth="300"
              :gap="20"
            >
              <template v-if="list.length == 0 && !loading">
                <NoData />
              </template>
              <template v-else>
                <SpaceCard
                  v-for="space in list"
                  :key="space.id"
                  :modelValue="space"
                  @reload="query"
              /></template>
            </GridCom>
          </div>
        </div>
      </div>
    </template>
    <!-- PAGINATION -->
    <Pagination v-model="page" @reload="query" />
  </div>
</template>

<script setup lang="ts">
import { ref, watch, computed, onMounted } from "vue";
import { message } from "ant-design-vue";
import { debounce } from "lodash-es";
import { SumApi } from "@/api";
import SpaceListModal from "@/components/modal/SpaceListModal.vue";
import SpaceCard from "@/components/SpaceCard.vue";
import Sort from "@/components/Sort.vue";
import GridCom from "@/components/GridCom.vue";
import Pagination from "@/components/Pagination.vue";
import JoinModal from "@/components/modal/JoinModal.vue";
import NoData from "@/components/NoData.vue";
import { SearchOutlined, ReloadOutlined } from "@ant-design/icons-vue";
import EditSpaceModal from "@/components/modal/EditSpaceModal.vue";
import * as CONST from "@/const";
import * as Utils from "@/utils";

const loading = ref(false);
const page = ref<Page<VO.SpaceVO>>(CONST.page());
const list = computed<VO.SpaceVO[]>(() => page.value.records);
const queryFlag = ref(false);
const queryParams = ref({
  name: undefined,
  asc: true,
  mask: undefined,
});

async function query() {
  if (loading.value) return;
  loading.value = true;
  try {
    await Utils.wait(500);

    const res = await SumApi.pageSpaceByOperator({
      current: page.value.current,
      size: page.value.size,
      asc: queryParams.value.asc,
      name: queryParams.value.name,
      mask: queryParams.value.mask,
    });

    page.value = Utils.conversionPage(res.data);
  } catch (e: any) {
    message.error(e.message);
  } finally {
    loading.value = false;
  }
}

watch([() => queryParams.value, () => queryFlag.value], debounce(query, 300), {
  deep: true,
});
</script>

<style scoped lang="scss">
.spaces-shell {
  flex: 1;
  height: 100%;
  display: flex;
  flex-direction: column;
  padding: 8px 16px;
  background: #f5f5f5;

  .search-shell {
    padding: 20px 24px;
    background: #fff;
    border-radius: 8px;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
    margin-bottom: 16px;
    display: flex;
    justify-content: space-between;

    .search-section {
      align-items: center;
      display: flex;
      gap: 1em;
      margin-top: auto;
    }

    .opt-section {
      align-items: center;
      display: flex;
      gap: 1em;
      margin-top: auto;
    }
  }

  .page-content {
    flex: 1;
    overflow: hidden;
    .space-list {
      height: 100%;
      display: flex;
      flex-direction: column;

      .content-header {
        display: flex;
        justify-content: space-between;
        align-items: center;
        padding: 16px 24px;
        background: #fff;
        border-bottom: 1px solid #e8e8e8;

        .header-left {
          display: flex;
          align-items: center;
          gap: 12px;

          h3 {
            margin: 0;
            font-size: 20px;
            font-weight: 600;
            color: #262626;
          }

          .item-count {
            color: #8c8c8c;
            font-size: 14px;
          }
        }

        .header-right {
          display: flex;
          gap: 12px;
        }
      }

      .content-body {
        flex: 1;
        padding: 24px;
        overflow-y: auto;
        display: flex;
        flex-direction: column;

        .space-grid {
          display: grid;
          gap: 24px;
          grid-template-columns: repeat(auto-fill, minmax(320px, 1fr));
        }
      }
    }
  }
}
</style>
