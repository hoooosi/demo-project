<template>
  <a-config-provider :locale="en_US">
    <router-view />
  </a-config-provider>
</template>

<style scoped></style>

<script setup lang="ts">
import en_US from "ant-design-vue/es/locale/en_US";
import useDataStore from "./stores/dataStore";

const user = localStorage.getItem("user");
if (user) useDataStore().user = JSON.parse(user);
</script>
