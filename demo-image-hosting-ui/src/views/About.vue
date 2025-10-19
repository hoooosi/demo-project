<template>
  <div class="home">
    <div class="hero-section">
      <h1 class="title">Image Hosting</h1>
      <p class="subtitle">Simple and easy-to-use image hosting platform</p>
      <div class="action-buttons">
        <a-button type="primary" size="large" @click="goToSpaces"> Start Hosting </a-button>
      </div>
    </div>

    <div class="features">
      <div class="feature-card">
        <div class="feature-icon">
          <CloudUploadOutlined />
        </div>
        <h3>Easy Upload</h3>
        <p>Supports multiple image formats, drag and drop uploads</p>
      </div>

      <div class="feature-card">
        <div class="feature-icon">
          <FolderOutlined />
        </div>
        <h3>Easy Space Management</h3>
        <p>Create personal or team spaces, classify and manage your images</p>
      </div>

      <div class="feature-card">
        <div class="feature-icon">
          <ShareAltOutlined />
        </div>
        <h3>Easy Share</h3>
        <p>Share image links with ease, supports multiple sharing methods</p>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { CloudUploadOutlined, FolderOutlined, ShareAltOutlined } from '@ant-design/icons-vue'
import { useRouter } from 'vue-router'
import { MetaInfo } from '@/router'

const router = useRouter()

const goToSpaces = () => {
  router.push({ name: MetaInfo.Spaces.name })
}
</script>

<style scoped lang="scss">
.home {
  flex: 1;

  .hero-section {
    text-align: center;
    padding: 80px 0;

    .title {
      font-size: 3.5rem;
      font-weight: bold;
      color: #1890ff;
      margin-bottom: 16px;
    }

    .subtitle {
      font-size: 1.2rem;
      color: #666;
      margin-bottom: 40px;
    }

    .action-buttons {
      display: flex;
      gap: 16px;
      justify-content: center;

      .ant-btn {
        height: 48px;
        padding: 0 32px;
        font-size: 16px;
      }
    }
  }

  .features {
    display: grid;
    grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
    gap: 32px;
    max-width: 1200px;
    margin: 80px auto 0;

    .feature-card {
      text-align: center;
      padding: 40px 24px;
      border-radius: 12px;
      background: #fff;
      box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
      transition: transform 0.3s ease;

      &:hover {
        transform: translateY(-4px);
      }

      .feature-icon {
        font-size: 3rem;
        color: #1890ff;
        margin-bottom: 24px;
      }

      h3 {
        font-size: 1.5rem;
        margin-bottom: 16px;
        color: #333;
      }

      p {
        color: #666;
        line-height: 1.6;
      }
    }
  }
}
</style>
