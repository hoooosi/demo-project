<script setup lang="ts">
import { cn } from "~/utils";

interface LabelProps {
  for?: string;
}

defineProps<LabelProps>();
</script>

<template>
  <label
    :for="for"
    :class="
      cn(
        'text-sm font-medium leading-none peer-disabled:cursor-not-allowed peer-disabled:opacity-70'
      )
    "
  >
    <slot />
  </label>
</template>
