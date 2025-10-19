<script setup lang="ts">
import GlobalHeader from '@/components/GlobalHeader.vue'
</script>

<style scoped>
.basicLayout {
  height: 100vh;
  display: flex;
  /* overflow: hidden; */
  flex-direction: column;
}

.header {
  background: #fff;
}

.content {
  overflow: auto;
  flex: 1;
  display: flex;
  background: #f5f5f5;
}

.footer {
  background: #ffffff;
  text-align: center;
  padding: 12px 0;
  border-top: 1px solid #e8e8e8;
  color: #666;

  a {
    color: #1890ff;
    text-decoration: none;
  }
}
</style>

<template>
  <div class="basicLayout">
    <div class="header">
      <GlobalHeader />
    </div>
    <div class="content">
      <router-view />
    </div>
  </div>
</template>
