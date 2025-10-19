<template>
  <div class="global-header">
    <div class="header-left">
      <div class="logo">
        <PictureOutlined />
        <span>Image Hosting</span>
      </div>
    </div>

    <div class="header-center">
      <a-menu mode="horizontal" :selected-keys="selectedKeys" class="nav-menu">
        <a-menu-item key="images">
          <router-link to="/images"> <PictureOutlined /> IMAGES </router-link>
        </a-menu-item>
        <a-menu-item key="spaces">
          <router-link to="/spaces"> <TeamOutlined /> SPACES </router-link>
        </a-menu-item>
      </a-menu>
    </div>

    <div class="header-right">
      <template v-if="user">
        <a-dropdown>
          <a-button type="text" class="user-info">
            <UserOutlined />
            {{ user.name }}
            <DownOutlined />
          </a-button>
          <template #overlay>
            <a-menu>
              <a-menu-item @click="goProfile"> <UserOutlined />Profile </a-menu-item>
              <a-menu-divider />
              <a-menu-item @click="handleLogout"> <LogoutOutlined />Logout </a-menu-item>
            </a-menu>
          </template>
        </a-dropdown>
      </template>
      <template v-else>
        <a-button @click="goLogin">Login</a-button>
        <a-button type="primary" @click="goRegister">Register</a-button>
      </template>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { message } from 'ant-design-vue'
import useDataStore from '@/stores/dataStore'
import {
  PictureOutlined,
  TeamOutlined,
  UserOutlined,
  DownOutlined,
  LogoutOutlined,
} from '@ant-design/icons-vue'
import { UserApi } from '@/api'
import { MetaInfo } from '@/router'

const router = useRouter()
const route = useRoute()
const store = useDataStore()
const user = computed(() => store.user)
const selectedKeys = computed(() => [route.path.substring(1)])

function goLogin() {
  router.push({ name: MetaInfo.Login.name, query: { mode: 'login' } })
}

function goRegister() {
  router.push({ name: MetaInfo.Login.name, query: { mode: 'register' } })
}

function goProfile() {
  // TODO
  message.info('Profile feature is under development')
}

async function handleLogout() {
  try {
    await UserApi.logout()
    message.success('LOGOUT SUCCESS')
    useDataStore().user = undefined
    localStorage.removeItem('user')
    router.push({ name: MetaInfo.About.name })
  } catch (e: any) {
    message.error(e.message)
    router.push('/')
  }
}
</script>

<style scoped lang="scss">
.global-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  height: 64px;
  padding: 0 24px;
  background: #fff;
  border-bottom: 1px solid #e8e8e8;

  .header-left {
    .logo {
      display: flex;
      align-items: center;
      cursor: pointer;
      font-size: 20px;
      font-weight: bold;
      color: #1890ff;

      .anticon {
        margin-right: 8px;
        font-size: 24px;
      }

      span {
        font-size: 18px;
      }
    }
  }

  .header-center {
    flex: 1;
    display: flex;
    justify-content: center;

    .nav-menu {
      display: flex;
      justify-content: center;
      border-bottom: none;
      background: transparent;
      width: 100%;

      .ant-menu-item {
        display: flex;
        align-items: center;
        gap: 4px;

        .anticon {
          font-size: 16px;
        }
      }
    }
  }

  .header-right {
    display: flex;
    align-items: center;
    gap: 12px;

    .user-info {
      display: flex;
      align-items: center;
      gap: 4px;
      height: 32px;
      padding: 0 12px;

      &:hover {
        background: #f5f5f5;
      }
    }
  }
}
</style>
