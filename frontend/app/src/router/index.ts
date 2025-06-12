import { createRouter, createWebHistory } from 'vue-router'
import OperationsView from '../views/OperationsView.vue'
import { getCookie } from '@/utils/cookie';
import routes from './routes';

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: routes.operations.path,
      name: routes.operations.name,
      component: OperationsView,
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
  console.log(token);
  debugger;

  if (to.matched.some(record => record.meta.requiresAuthorization)) {
    if (!token) {
      next(routes.login.path);
    } else {
      next();
    }
  } else if (to.name === routes.login.name && token) {
    next(routes.operations.path);
  } else {
    next();
  }
});

export default router
