import { createRouter, createWebHistory } from 'vue-router'
import { UserApi } from '@/api'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      name: 'home',
      redirect: '/dashboard'
    },
    {
      path: '/login',
      name: "Login",
      component: () => import('@/views/Login.vue')
    },
    {
      path: '/dashboard',
      name: 'Dashboard',
      component: () => import('@/views/Dashboard.vue'),
      meta: { requiresAuth: true }
    },
    {
      path: '/meeting/:id',
      name: 'MeetingRoom',
      component: () => import('@/views/MeetingRoom.vue'),
      meta: { requiresAuth: true }
    }
  ],
})

// Route guard
router.beforeEach(async (to, from, next) => {
  next()
})

export default router
