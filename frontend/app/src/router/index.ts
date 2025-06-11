import { createRouter, createWebHistory } from 'vue-router'
import DashboardView from '../views/DashboardView.vue'
import { getCookie } from '@/utils/cookie';
import routes from './routes';

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: routes.dashboard.path,
      name: routes.dashboard.name,
      component: DashboardView,
      meta: {
        requiresAuthorization: true
      }
    },
    {
      path: routes.login.path,
      name: routes.login.name,
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
      next(routes.login.path);
    } else {
      next();
    }
  } else if (to.name === routes.login.name && token) {
    next(routes.dashboard.path);
  } else {
    next();
  }
});

export default router
