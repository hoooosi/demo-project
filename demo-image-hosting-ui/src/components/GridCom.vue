<script setup lang="ts">
import useDataStore from "@/stores/dataStore";
import { onMounted, onUnmounted, ref } from "vue";
const gridShellDom = ref<HTMLElement>();

const maxSize = ref(0);

const props = withDefaults(
  defineProps<{
    gap?: number;
    itemMinWidth?: number;
    aspectRatio?: number;
    loading?: boolean;
  }>(),
  {
    gap: 10,
    itemMinWidth: 200,
    aspectRatio: 1,
    loading: false,
  }
);
const emit = defineEmits<{
  "upload-success": [];
  "update-visable": [value: boolean];
  "max-size": [value: number];
}>();
const initStyle = () => {
  if (!gridShellDom.value) return;
  gridShellDom.value.style.setProperty("--gap", `${props.gap}px`);
  gridShellDom.value.style.setProperty("--minwidth", `${props.itemMinWidth}px`);
  gridShellDom.value.style.setProperty(
    "--aspect-ratio",
    `${props.aspectRatio}`
  );
};

const resize = () => {
  if (!gridShellDom.value) return;
  const shellClientWidth = gridShellDom.value.clientWidth;
  const shellClientHeight = gridShellDom.value.clientHeight;
  const rows = Math.floor(
    (shellClientWidth + props.gap) / (props.itemMinWidth + props.gap)
  );
  const itemWidth = (shellClientWidth + props.gap) / rows - props.gap;
  const itemHeight = itemWidth / props.aspectRatio;
  const cols = Math.floor(
    (shellClientHeight + props.gap) / (itemHeight + props.gap)
  );
  maxSize.value = Math.floor(rows) * Math.floor(cols);
  emit("max-size", Math.max(maxSize.value, 1));
  useDataStore().gridMaxSize = Math.max(maxSize.value, 1);
  useDataStore().gridMaxSize = Math.max(rows * 5, 1);
  useDataStore().gridRows = Math.max(rows * 10, 1);
};

onMounted(() => {
  initStyle();
  resize();
  window.addEventListener("resize", resize);
});
onUnmounted(() => {
  window.removeEventListener("resize", resize);
});
</script>
<template>
  <div class="grid-shell" ref="gridShellDom">
    <div class="loading-wrapper" :class="{ 'loading-wrapper-active': loading }">
      <a-spin :spinning="loading" size="large"></a-spin>
    </div>
    <div class="grid" ref="grid">
      <slot></slot>
    </div>
  </div>
</template>
<style lang="scss">
.grid-shell {
  --gap: 10px;
  --aspect-ratio: 1;
  --minwidth: 200px;

  flex: 1;
  position: relative;

  .loading-wrapper {
    height: 100%;
    position: absolute;
    inset: 0;
    z-index: 100;
    display: flex;
    align-items: center;
    justify-content: center;
    opacity: 0;
    transition: 0.3s;
    pointer-events: none;
    background-color: rgba(255, 255, 255, 0.336);
  }

  .loading-wrapper-active {
    pointer-events: auto;
    opacity: 1;
  }

  .grid {
    display: grid;
    grid-template-columns: repeat(auto-fill, minmax(var(--minwidth), 1fr));
    gap: var(--gap);

    > div {
      aspect-ratio: var(--aspect-ratio);
    }
  }
}
</style>
