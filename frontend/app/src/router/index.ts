import { createRouter, createWebHistory } from 'vue-router'
import OperationsView from '../views/OperationsView.vue'

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

function getCookie(name: string) {
  const value = `; ${document.cookie}`;
  const parts = value.split(`; ${name}=`);
  if (parts.length === 2) return parts.pop()?.split(';').shift();
  return null;
}

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
