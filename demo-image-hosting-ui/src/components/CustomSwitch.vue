<script setup lang="ts">
import { computed } from 'vue'

const props = withDefaults(
  defineProps<{
    modelValue: boolean
    text?: string
    reverse?: boolean
  }>(),
  {
    text: 'Switch',
    reverse: false,
  }
)
const active = computed(() => (props.reverse ? !props.modelValue : props.modelValue))
const emit = defineEmits(['update:modelValue'])
function handleClick(val: boolean | undefined) {
  emit('update:modelValue', val)
}
</script>
<template>
  <div class="switch-shell">
    <div
      class="switch-div"
      :class="{ 'switch-div--active': active }"
      @click="handleClick(!modelValue)"
    >
      {{ text }}
    </div>
  </div>
</template>
<style scoped lang="scss">
.switch-shell {
  position: relative;
  .switch-div {
    background-color: #f0f0f0;
    border: 2px solid #d9d9d9;
    border-radius: 6px;
    cursor: pointer;
    transition: all 0.3s ease;
    display: flex;
    align-items: center;
    justify-content: center;
    padding: 2px 6px;
    color: #7c7777;

    &--active {
      background-color: #e6f7ff;
      border-color: #1890ff;
      color: #1890ff;
      box-shadow: 0 0 0 2px rgba(24, 144, 255, 0.2);
    }

    &:hover {
      border-color: #40a9ff;
    }
  }

  &:hover {
    .tip-shell {
      opacity: 1;
      visibility: visible;
    }
  }
}
</style>
