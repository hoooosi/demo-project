<template>
  <div class="home-shell">
    <!-- SEARCH BAR -->
    <div class="search-container">
      <div class="search-main">
        <a-input
          v-model:value="queryParams.keyword"
          placeholder="Search for images by keyword..."
          class="search-input"
          allow-clear
          @pressEnter="handleSearch"
        >
          <template #prefix>
            <SearchOutlined class="search-icon" />
          </template>
        </a-input>
        <Sort v-model="queryParams.asc" />
        <a-dropdown
          v-model:open="tagDropdownVisible"
          :trigger="['click']"
          placement="bottomRight"
        >
          <a-button class="tag-filter-btn">
            <template #icon>
              <TagsOutlined />
            </template>
            Tags
            <span v-if="queryParams.tags.length > 0" class="tag-count">
              {{ queryParams.tags.length }}
            </span>
          </a-button>          <template #overlay>
            <div class="tag-dropdown" @click.stop>
              <div class="tag-dropdown-header">
                <span class="tag-dropdown-title">Filter by Tags</span>
                <a-button
                  v-if="queryParams.tags.length > 0"
                  type="link"
                  size="small"
                  @click="clearAllTags"
                >
                  Clear All
                </a-button>
              </div>
              <a-divider style="margin: 8px 0" />
              <div v-if="loadingTags" class="tag-list-loading">
                <a-spin size="small" />
                <span style="margin-left: 8px">Loading tags...</span>
              </div>
              <div v-else-if="availableTags.length === 0" class="tag-list-empty">
                No tags available
              </div>
              <div v-else class="tag-list">
                <a-checkable-tag
                  v-for="tag in availableTags"
                  :key="tag"
                  :checked="queryParams.tags.includes(tag)"
                  @change="(checked: boolean) => handleTagChange(tag, checked)"
                  class="custom-tag"
                >
                  {{ tag }}
                </a-checkable-tag>
              </div>
            </div>
          </template>
        </a-dropdown>
      </div>

      <!-- SELECTED TAGS DISPLAY -->
      <div v-if="queryParams.tags.length > 0" class="selected-tags">
        <span class="selected-tags-label">Active Filters:</span>
        <a-tag
          v-for="tag in queryParams.tags"
          :key="tag"
          closable
          color="blue"
          @close="removeTag(tag)"
          class="selected-tag"
        >
          {{ tag }}
        </a-tag>
      </div>
    </div>
    <!-- GALLERY -->
    <GridCom :loading="loading">
      <template v-if="images.length == 0 && !loading">
        <NoData />
      </template>
      <template v-else>
        <ImageCard
          v-for="item in images"
          :key="item.id"
          :permissionMask="mask"
          :modelValue="item"
        />
      </template>
    </GridCom>
    <!-- PAGINATION -->
    <Pagination v-model="page" @reload="query"></Pagination>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, watch } from "vue";
import { message } from "ant-design-vue";
import Pagination from "@/components/Pagination.vue";
import ImageCard from "@/components/ImageCard.vue";
import NoData from "@/components/NoData.vue";
import GridCom from "@/components/GridCom.vue";
import Sort from "@/components/Sort.vue";
import {
  SearchOutlined,
  TagsOutlined,
  SortAscendingOutlined,
  SortDescendingOutlined,
} from "@ant-design/icons-vue";
import { ImageApi } from "@/api";
import { debounce } from "lodash-es";
import * as CONST from "@/const";
import * as Utils from "@/utils";

const loading = ref(false);
const queryFlag = ref<boolean>(false);
const page = ref<Page<VO.ImageVO>>(CONST.page());
const images = computed<VO.ImageVO[]>(() => page.value.records);
const mask = computed(() =>
  queryFlag.value ? CONST.PermissionMask.LONG_MAX : 0n
);
const queryParams = ref<{
  keyword: string | undefined;
  tags: string[];
  asc: boolean;
}>({
  keyword: undefined,
  tags: [],
  asc: true,
});
const availableTags = ref<string[]>([]);
const loadingTags = ref(false);

const tagDropdownVisible = ref(false);

// 加载所有可用标签
async function loadTags() {
  if (loadingTags.value) return;
  try {
    loadingTags.value = true;
    const res = await ImageApi.listTags();
    availableTags.value = res.data || [];
  } catch (e: any) {
    message.error(e.message || "Failed to load tags");
  } finally {
    loadingTags.value = false;
  }
}

async function query() {
  if (loading.value) return;
  loading.value = true;
  try {
    const res = await ImageApi.pageByPublic({
      ...Utils.conversionQueryParams(page.value),
      asc: queryParams.value.asc,
      keyword: queryParams.value.keyword,
      tags: queryParams.value.tags,
    });
    page.value = Utils.conversionPage(res.data);
  } catch (e: any) {
    message.error(e.message);
  } finally {
    loading.value = false;
  }
}

const handleSearch = debounce(async () => {
  page.value.current = 1;
  await query();
}, 500);

function handleTagChange(tag: string, checked: boolean) {
  if (checked) {
    queryParams.value.tags.push(tag);
  } else {
    queryParams.value.tags = queryParams.value.tags.filter((t) => t !== tag);
  }
}

function removeTag(tag: string) {
  queryParams.value.tags = queryParams.value.tags.filter((t) => t !== tag);
}

function clearAllTags() {
  queryParams.value.tags = [];
}

// 初始化加载标签
loadTags();

watch([() => queryParams.value, () => queryFlag.value], handleSearch, {
  deep: true,
});
</script>

<style scoped lang="scss">
.home-shell {
  flex: 1;
  height: 100%;
  display: flex;
  flex-direction: column;
  padding: 8px 16px;
}

.search-container {
  margin-bottom: 16px;
  background: #fff;
  padding: 16px;
  border-radius: 8px;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.08);

  .search-main {
    display: flex;
    gap: 10px;
    align-items: center;
  }

  .search-input {
    flex: 0.3;
    border-radius: 6px;

    :deep(.ant-input) {
      font-size: 14px;
    }

    :deep(.ant-input-prefix) {
      margin-right: 10px;
    }

    .search-icon {
      font-size: 16px;
      color: #8c8c8c;
    }
  }

  .tag-filter-btn {
    margin-left: auto;
    border-radius: 6px;
    display: flex;
    align-items: center;
    gap: 6px;
    padding: 0 16px;
    height: 32px;
    border: 1px solid #d9d9d9;
    transition: all 0.3s;

    &:hover {
      border-color: #40a9ff;
      color: #40a9ff;
    }

    .tag-count {
      display: inline-flex;
      align-items: center;
      justify-content: center;
      min-width: 18px;
      height: 18px;
      padding: 0 5px;
      background: #1890ff;
      color: #fff;
      border-radius: 9px;
      font-size: 11px;
      font-weight: 500;
      margin-left: 4px;
    }
  }

  .selected-tags {
    display: flex;
    align-items: center;
    flex-wrap: wrap;
    gap: 8px;
    margin-top: 12px;
    padding-top: 12px;
    border-top: 1px solid #f0f0f0;

    .selected-tags-label {
      font-size: 12px;
      font-weight: 500;
      color: #8c8c8c;
      margin-right: 4px;
    }

    .selected-tag {
      margin: 0;
      padding: 2px 10px;
      border-radius: 4px;
      font-size: 12px;
      display: flex;
      align-items: center;
      gap: 4px;
      transition: all 0.3s;

      &:hover {
        opacity: 0.8;
      }
    }
  }
}

.tag-dropdown {
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 3px 12px rgba(0, 0, 0, 0.12);
  padding: 12px;
  min-width: 320px;
  max-width: 400px;

  .tag-dropdown-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 0 4px;

    .tag-dropdown-title {
      font-size: 14px;
      font-weight: 600;
      color: #262626;
    }
  }

  .tag-list {
    display: flex;
    flex-wrap: wrap;
    gap: 8px;
    max-height: 280px;
    overflow-y: auto;
    padding: 4px;

    &::-webkit-scrollbar {
      width: 6px;
    }

    &::-webkit-scrollbar-track {
      background: #f0f0f0;
      border-radius: 3px;
    }

    &::-webkit-scrollbar-thumb {
      background: #bfbfbf;
      border-radius: 3px;

      &:hover {
        background: #8c8c8c;
      }
    }

    .custom-tag {
      padding: 5px 14px;
      border-radius: 4px;
      border: 1px solid #d9d9d9;
      background: #fafafa;
      cursor: pointer;
      transition: all 0.3s;
      font-size: 13px;

      &:hover {
        border-color: #40a9ff;
        color: #40a9ff;
      }

      &.ant-tag-checkable-checked {
        background: #e6f7ff;
        border-color: #1890ff;
        color: #1890ff;
      }
    }
  }

  .tag-list-loading,
  .tag-list-empty {
    display: flex;
    align-items: center;
    justify-content: center;
    padding: 20px;
    color: #8c8c8c;
    font-size: 13px;
  }
}
</style>
