import { createRouter, createWebHistory } from 'vue-router'
import DashboardView from '@/views/DashboardView.vue';
import routes from './routes';
import { useAuthStore } from '@/stores/auth';

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

router.beforeEach(async (to, from, next) => {
  const authStore = useAuthStore();
  
  try {
    // Checks if user is authenticated
    await authStore.check();
  } catch (err) {
    console.error(err);
  }

  if (to.matched.some(record => record.meta.requiresAuthorization)) {
    if (!authStore.isAuthenticated) {
      next(routes.login.path);
    } else {
      next();
    }
  } else if (to.name === routes.login.name && authStore.isAuthenticated) {
    next(routes.dashboard.path);
  } else {
    next();
  }
});

export default router
