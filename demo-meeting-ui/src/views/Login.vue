<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { User, Lock, Message, Loading } from '@element-plus/icons-vue'
import { UserApi } from '@/api'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'

const router = useRouter()
const userStore = useUserStore()

// 表单状态
const isLogin = ref(true)
const loading = ref(false)
const checkCodeLoading = ref(false)

// 验证码信息
const checkCodeInfo = ref<VO.CheckCodeVO | null>(null)

// 登录表单数据
const loginForm = reactive<DTO.LoginDTO>({
  email: 'testemail@qq.com',
  password: '12345678',
})

// 注册表单数据
const registerForm = reactive<DTO.RegisterDTO>({
  email: 'testemail@qq.com',
  password: '12345678',
  nickName: 'testuser',
  checkCode: '',
  checkCodeKey: '',
})

// 登录表单验证规则
const loginRules = {
  email: [
    { required: true, message: '请输入邮箱', trigger: 'blur' },
    { type: 'email', message: '请输入正确的邮箱格式', trigger: 'blur' },
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, message: '密码长度不能少于6位', trigger: 'blur' },
  ],
}

// 注册表单验证规则
const registerRules = {
  email: [
    { required: true, message: '请输入邮箱', trigger: 'blur' },
    { type: 'email', message: '请输入正确的邮箱格式', trigger: 'blur' },
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, message: '密码长度不能少于6位', trigger: 'blur' },
  ],
  nickName: [
    { required: true, message: '请输入昵称', trigger: 'blur' },
    { min: 2, message: '昵称长度不能少于2位', trigger: 'blur' },
  ],
  checkCode: [{ required: true, message: '请输入验证码', trigger: 'blur' }],
}

const loginFormRef = ref()
const registerFormRef = ref()

// 切换登录/注册模式
const toggleMode = () => {
  isLogin.value = !isLogin.value
  // 清空表单
  if (loginFormRef.value) {
    loginFormRef.value.resetFields()
  }
  if (registerFormRef.value) {
    registerFormRef.value.resetFields()
  }
}

// 登录处理
const handleLogin = async () => {
  if (!loginFormRef.value) return

  const valid = await loginFormRef.value.validate().catch(() => false)
  if (!valid) return

  loading.value = true
  try {
    const result = await UserApi.login(loginForm)
    // 保存token
    localStorage.setItem('token', result.data)

    // 获取用户信息
    const userInfo = await UserApi.me()
    userStore.setUserInfo(userInfo.data)

    ElMessage.success('登录成功!')

    // 跳转到Dashboard
    router.push('/dashboard')
  } catch (error: any) {
    ElMessage.error(error.message || '登录失败')
  } finally {
    loading.value = false
  }
}

// 注册处理
const handleRegister = async () => {
  if (!registerFormRef.value) return

  const valid = await registerFormRef.value.validate().catch(() => false)
  if (!valid) return

  loading.value = true
  try {
    await UserApi.register(registerForm)
    ElMessage.success('注册成功!')
    isLogin.value = true // 注册成功后切换到登录模式
  } catch (error: any) {
    ElMessage.error(error.message || '注册失败')
  } finally {
    loading.value = false
  }
}

// 获取验证码
const getCheckCode = async () => {
  checkCodeLoading.value = true
  try {
    const result = await UserApi.checkCode()
    checkCodeInfo.value = result.data
    registerForm.checkCodeKey = result.data.key
    ElMessage.success('验证码获取成功')
  } catch (error: any) {
    ElMessage.error(error.message || '获取验证码失败')
  } finally {
    checkCodeLoading.value = false
  }
}

// 刷新验证码
const refreshCheckCode = () => {
  getCheckCode()
}

// 检查登录状态
const checkLoginStatus = async () => {
  const token = localStorage.getItem('token')
  if (token) {
    try {
      const result = await UserApi.me()
      userStore.setUserInfo(result.data)

      router.push('/dashboard')
    } catch (error) {
      userStore.clearUserInfo()
    }
  }
}

// 组件挂载时获取验证码和检查登录状态
onMounted(() => {
  checkLoginStatus()
  getCheckCode()
})
</script>

<template>
  <div class="login-shell">
    <div class="login-container">
      <div class="login-card">
        <div class="login-header">
          <h1 class="login-title">{{ isLogin ? '登录' : '注册' }}</h1>
          <p class="login-subtitle">欢迎使用会议系统</p>
        </div>

        <div class="login-form-wrapper">
          <!-- 登录表单 -->
          <el-form
            v-if="isLogin"
            ref="loginFormRef"
            :model="loginForm"
            :rules="loginRules"
            class="login-form"
          >
            <el-form-item prop="email" class="form-item">
              <el-input
                v-model="loginForm.email"
                placeholder="请输入邮箱"
                :prefix-icon="Message"
                size="large"
                class="form-input"
              />
            </el-form-item>

            <el-form-item prop="password" class="form-item">
              <el-input
                v-model="loginForm.password"
                type="password"
                placeholder="请输入密码"
                :prefix-icon="Lock"
                size="large"
                show-password
                class="form-input"
              />
            </el-form-item>

            <el-form-item class="form-item form-submit">
              <el-button
                type="primary"
                size="large"
                :loading="loading"
                @click="handleLogin"
                class="submit-button"
              >
                {{ loading ? '登录中...' : '登录' }}
              </el-button>
            </el-form-item>
          </el-form>

          <!-- 注册表单 -->
          <el-form
            v-else
            ref="registerFormRef"
            :model="registerForm"
            :rules="registerRules"
            class="register-form"
          >
            <el-form-item prop="email" class="form-item">
              <el-input
                v-model="registerForm.email"
                placeholder="请输入邮箱"
                :prefix-icon="Message"
                size="large"
                class="form-input"
              />
            </el-form-item>

            <el-form-item prop="nickName" class="form-item">
              <el-input
                v-model="registerForm.nickName"
                placeholder="请输入昵称"
                :prefix-icon="User"
                size="large"
                class="form-input"
              />
            </el-form-item>

            <el-form-item prop="password" class="form-item">
              <el-input
                v-model="registerForm.password"
                type="password"
                placeholder="请输入密码"
                :prefix-icon="Lock"
                size="large"
                show-password
                class="form-input"
              />
            </el-form-item>
            <el-form-item prop="checkCode" class="form-item">
              <div class="check-code-wrapper">
                <el-input
                  v-model="registerForm.checkCode"
                  placeholder="请输入验证码"
                  size="large"
                  class="check-code-input"
                />
                <div class="check-code-image-wrapper">
                  <img
                    v-if="checkCodeInfo"
                    :src="checkCodeInfo.base64"
                    alt="验证码"
                    class="check-code-image"
                    @click="refreshCheckCode"
                  />
                  <div v-else class="check-code-placeholder" @click="refreshCheckCode">
                    <el-icon v-if="checkCodeLoading" class="is-loading">
                      <Loading />
                    </el-icon>
                    <span v-else>点击获取</span>
                  </div>
                </div>
              </div>
            </el-form-item>

            <el-form-item class="form-item form-submit">
              <el-button
                type="primary"
                size="large"
                :loading="loading"
                @click="handleRegister"
                class="submit-button"
              >
                {{ loading ? '注册中...' : '注册' }}
              </el-button>
            </el-form-item>
          </el-form>
        </div>

        <div class="login-footer">
          <p class="toggle-text">
            {{ isLogin ? '还没有账号？' : '已有账号？' }}
            <el-button type="text" @click="toggleMode" class="toggle-button">
              {{ isLogin ? '立即注册' : '立即登录' }}
            </el-button>
          </p>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped lang="scss">
.login-shell {
  width: 100vw;
  height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  position: relative;
  overflow: hidden;

  .login-container {
    position: relative;
    z-index: 1;
    width: 100%;
    max-width: 440px;
    padding: 20px;

    .login-card {
      background: rgba(255, 255, 255, 0.98);
      border-radius: 24px;
      backdrop-filter: blur(20px);
      overflow: hidden;
      transition: all 0.3s ease;
      .login-header {
        text-align: center;
        padding: 48px 48px 32px;
        background: transparent;
        color: #2d3748;

        .login-title {
          font-size: 32px;
          font-weight: 800;
          margin: 0 0 12px;
          letter-spacing: -0.5px;
          background: linear-gradient(135deg, #667eea, #764ba2);
          -webkit-background-clip: text;
          -webkit-text-fill-color: transparent;
          background-clip: text;
        }

        .login-subtitle {
          font-size: 16px;
          margin: 0;
          color: #64748b;
          font-weight: 400;
        }
      }
      .login-form-wrapper {
        padding: 0 48px 32px;

        .login-form,
        .register-form {
          .form-item {
            margin-bottom: 28px;

            .form-input {
              :deep(.el-input__wrapper) {
                border-radius: 16px;
                border: 1px solid #e2e8f0;
                box-shadow: none;
                transition: all 0.3s ease;
                padding: 0 24px;
                height: 56px;
                background: rgba(248, 250, 252, 0.8);

                &:hover {
                  border-color: #667eea;
                  background: rgba(248, 250, 252, 1);
                }

                &.is-focus {
                  border-color: #667eea;
                  background: rgba(248, 250, 252, 1);
                }
              }

              :deep(.el-input__inner) {
                font-size: 16px;
                font-weight: 500;
                color: #2d3748;

                &::placeholder {
                  color: #94a3b8;
                  font-weight: 400;
                }
              }
            }

            .check-code-wrapper {
              display: flex;
              gap: 16px;

              .check-code-input {
                flex: 1;

                :deep(.el-input__wrapper) {
                  border-radius: 16px;
                  border: 1px solid #e2e8f0;
                  box-shadow: none;
                  transition: all 0.3s ease;
                  padding: 0 24px;
                  height: 56px;
                  background: rgba(248, 250, 252, 0.8);

                  &:hover {
                    border-color: #667eea;
                    background: rgba(248, 250, 252, 1);
                  }

                  &.is-focus {
                    border-color: #667eea;
                    background: rgba(248, 250, 252, 1);
                  }
                }
              }

              .check-code-image-wrapper {
                width: 120px;
                height: 56px;
                border-radius: 16px;
                border: 1px solid #e2e8f0;
                background: rgba(248, 250, 252, 0.8);
                display: flex;
                align-items: center;
                justify-content: center;
                cursor: pointer;
                transition: all 0.3s ease;
                overflow: hidden;

                &:hover {
                  border-color: #667eea;
                  background: rgba(248, 250, 252, 1);
                }

                .check-code-image {
                  width: 100%;
                  height: 100%;
                  object-fit: cover;
                  cursor: pointer;
                }

                .check-code-placeholder {
                  display: flex;
                  align-items: center;
                  justify-content: center;
                  width: 100%;
                  height: 100%;
                  color: #64748b;
                  font-size: 14px;
                  font-weight: 500;
                  cursor: pointer;

                  .is-loading {
                    animation: rotate 1s linear infinite;
                  }
                }
              }
            }
            &.form-submit {
              margin-bottom: 0;
              margin-top: 40px;

              .submit-button {
                width: 100%;
                height: 56px;
                font-size: 16px;
                font-weight: 600;
                border-radius: 16px;
                background: linear-gradient(135deg, #667eea, #764ba2);
                border: none;
                transition: all 0.3s ease;

                &:hover {
                  transform: translateY(-2px);
                  filter: brightness(1.1);
                }

                &:active {
                  transform: translateY(0);
                }
              }
            }
          }
        }
      }
      .login-footer {
        padding: 24px 48px 48px;
        text-align: center;
        background: transparent;

        .toggle-text {
          margin: 0;
          color: #64748b;
          font-size: 15px;
          display: flex;
          align-items: center;
          justify-content: center;
          gap: 8px;

          .toggle-button {
            padding: 0;
            font-size: 15px;
            font-weight: 600;
            color: #667eea;
            transition: all 0.3s ease;

            &:hover {
              color: #764ba2;
            }
          }
        }
      }
    }
  }
}

// 动画
@keyframes rotate {
  from {
    transform: rotate(0deg);
  }
  to {
    transform: rotate(360deg);
  }
}

// 响应式设计
@media (max-width: 480px) {
  .login-shell {
    .login-container {
      max-width: 100%;
      padding: 16px;

      .login-card {
        border-radius: 20px;

        .login-header {
          padding: 40px 32px 24px;

          .login-title {
            font-size: 28px;
          }

          .login-subtitle {
            font-size: 14px;
          }
        }

        .login-form-wrapper {
          padding: 0 32px 24px;

          .login-form,
          .register-form {
            .form-item {
              margin-bottom: 24px;

              .form-input {
                :deep(.el-input__wrapper) {
                  height: 52px;
                  padding: 0 20px;
                }
              }

              .check-code-wrapper {
                flex-direction: column;
                gap: 12px;

                .check-code-input {
                  :deep(.el-input__wrapper) {
                    height: 52px;
                    padding: 0 20px;
                  }
                }

                .check-code-image-wrapper {
                  width: 100%;
                  height: 52px;
                }
              }

              &.form-submit {
                .submit-button {
                  height: 52px;
                  font-size: 15px;
                }
              }
            }
          }
        }

        .login-footer {
          padding: 20px 32px 40px;
        }
      }
    }
  }
}
</style>
