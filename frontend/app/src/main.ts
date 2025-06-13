import './assets/styles/main.css'
import 'vue3-toastify/dist/index.css';
import Vue3Toastify, { type ToastContainerOptions } from 'vue3-toastify';

import { createApp } from 'vue'
import App from './App.vue'
import router from './router'
import { createPinia } from 'pinia';

const pinia = createPinia()
const app = createApp(App)

app.use(pinia)
app.use(router)
app.use(Vue3Toastify, {
  autoClose: 3000
} as ToastContainerOptions)

app.mount('#app')
