<script setup lang="ts">
withDefaults(
  defineProps<{
    message?: string;
    description?: string;
  }>(),
  {
    message: "No Data",
    description: "Current there is no data available",
  }
);
</script>

<template>
  <div class="shell">
    <div class="no-data-container">
      <div class="icon">
        <svg viewBox="0 0 64 64" fill="none" xmlns="http://www.w3.org/2000/svg">
          <path
            d="M32 8C18.745 8 8 18.745 8 32s10.745 24 24 24 24-10.745 24-24S45.255 8 32 8zm0 44c-11.046 0-20-8.954-20-20s8.954-20 20-20 20 8.954 20 20-8.954 20-20 20z"
            fill="#d1d5db"
          />
          <path
            d="M32 20c-1.105 0-2 .895-2 2v12c0 1.105.895 2 2 2s2-.895 2-2V22c0-1.105-.895-2-2-2zm0 20c-1.105 0-2 .895-2 2s.895 2 2 2 2-.895 2-2-.895-2-2-2z"
            fill="#9ca3af"
          />
        </svg>
      </div>
      <div class="content">
        <h3 class="title">{{ message }}</h3>
        <p class="description">{{ description }}</p>
      </div>
    </div>
  </div>
</template>

<style lang="scss">
.shell {
  display: flex;
  align-items: center;
  justify-content: center;
  position: absolute;
  width: 100%;
  height: 100%;

  .no-data-container {
    display: flex;
    flex-direction: column;
    align-items: center;
    text-align: center;
    padding: 40px 20px;

    .icon {
      width: 80px;
      height: 80px;
      margin-bottom: 24px;
      opacity: 0.6;

      svg {
        width: 100%;
        height: 100%;
      }
    }

    .content {
      .title {
        font-size: 18px;
        font-weight: 500;
        color: #374151;
        margin: 0 0 8px 0;
        line-height: 1.4;
      }

      .description {
        font-size: 14px;
        color: #6b7280;
        margin: 0;
        line-height: 1.5;
        max-width: 300px;
      }
    }
  }
}
</style>
