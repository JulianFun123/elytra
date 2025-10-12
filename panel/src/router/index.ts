import { createRouter, createWebHistory } from 'vue-router'
import HomeView from '../views/HomeView.vue'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      name: 'home',
      component: HomeView,
    },
    {
      path: '/panel',
      name: 'name-route',
      component: () => import('../components/MainLayout.vue'),
      children: [
        {
          path: 'servers',
          name: 'servers',
          component: () => import('../views/ServerListView.vue'),
        }
      ]
    },
    {
      path: '/login',
      name: 'login',
      component: () => import('../views/AuthView.vue'),
    },
  ],
})

export default router
