<template>
  <template v-if="modelValue">
    <a-pagination
      style="margin-top: 0.5em"
      v-model:current="modelValue.current"
      v-model:page-size="modelValue.size"
      :showSizeChanger="false"
      :align="'center'"
      :total="modelValue.total"
    />
  </template>
</template>

<script setup lang="ts">
import { onMounted, watch } from "vue";
import { debounce } from "lodash-es";
import useDataStore from "@/stores/dataStore";

const props = defineProps<{
  modelValue: Page<any>;
}>();

const emit = defineEmits<{
  reload: [];
}>();

const reload = debounce(() => {
  emit("reload");
}, 100);

watch(
  [() => props.modelValue.current, () => props.modelValue.size],
  (newValues, oldValues) => {
    if (props.modelValue.size == 0) return;
    const [newCurrent, newSize] = newValues;
    const [oldCurrent, oldSize] = oldValues;
    if (newCurrent == 0) return;
    if (newCurrent != oldCurrent || newSize != oldSize) reload();
  }
);

watch(
  () => useDataStore().gridMaxSize,
  (newVal) => (props.modelValue.size = newVal)
);

onMounted(() => {
  props.modelValue.current = 1;
  props.modelValue.size = useDataStore().gridMaxSize;
});
</script>
