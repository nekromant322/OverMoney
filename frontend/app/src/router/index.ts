import { createRouter, createWebHistory } from 'vue-router'
import OperationsView from '../views/OperationsView.vue'
import { getCookie } from '@/utils/cookie';

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      name: 'operations',
      component: OperationsView,
      meta: {
        requiresAuthorization: true
      }
    },
    {
      path: '/login',
      name: 'login',
      component: () => import('../views/LoginView.vue'),
      meta: {
        requiresAuthorization: false
      }
    },
  ],
})

router.beforeEach((to, from, next) => {
  const token = getCookie('accessToken');

  if (to.matched.some(record => record.meta.requiresAuthorization)) {
    if (!token) {
      next('/login');
    } else {
      next();
    }
  } else if (to.name === 'login' && token) {
    next('/');
  } else {
    next();
  }
});

export default router
