import { createRouter, createWebHistory } from 'vue-router'
import DashboardView from '@/views/DashboardView.vue';
import routes from './routes';
import { ref } from 'vue';

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
  // TODO Send check auth request
  const isAuthenticated = ref(false); // TODO Move it to store

  if (to.matched.some(record => record.meta.requiresAuthorization)) {
    if (!isAuthenticated.value) {
      next(routes.login.path);
    } else {
      next();
    }
  } else if (to.name === routes.login.name && isAuthenticated.value) {
    next(routes.dashboard.path);
  } else {
    next();
  }
});

export default router
