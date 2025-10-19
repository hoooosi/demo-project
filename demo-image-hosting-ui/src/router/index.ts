import { createRouter, createWebHistory } from 'vue-router'
import BasicLayout from '@/views/common/BasicLayout.vue'
import useDataStore from '@/stores/dataStore'
import { UserApi } from '@/api'
import { MetaInfo } from './meta'
const router = createRouter({
  history: createWebHistory(),
  routes: [
    {
      path: '/',
      name: 'BasicLayout',
      redirect: MetaInfo.About.path,
      component: BasicLayout,
      children: [
        {
          name: MetaInfo.Images.name,
          path: MetaInfo.Images.path,
          component: () => import('@/views/Images.vue'),
        },
        {
          name: MetaInfo.Spaces.name,
          path: MetaInfo.Spaces.path,
          component: () => import('@/views/Spaces.vue'),
          meta: { requiresAuth: true },
          beforeEnter: (to, from, next) => {
            if (useDataStore().user)
              next()
            else
              next({ name: MetaInfo.Login.name })
          },
        },
        {
          path: MetaInfo.Space.path,
          name: MetaInfo.Space.name,
          component: () => import('@/views/Space.vue'),
          meta: { requiresAuth: true },
        },
      ],
    },
    {
      path: MetaInfo.Login.path,
      name: MetaInfo.Login.name,
      component: () => import('@/views/Login.vue'),
      beforeEnter: async (to, from, next) => {
        try {
          await UserApi.auth()
          next({ name: MetaInfo.Spaces.name })
        } catch (e: any) {
          next()
        }
      },
    },
    {
      path: MetaInfo.About.path,
      name: MetaInfo.About.name,
      component: () => import('@/views/About.vue'),
      meta: { requiresAuth: true },
    },
    {
      path: '/test',
      name: 'Test',
      component: () => import('@/views/Test.vue'),
    },
  ],
})

router.beforeEach((to, from, next) => {
  next()
})

export { MetaInfo }
export default router
