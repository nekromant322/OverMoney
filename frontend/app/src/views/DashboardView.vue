<script setup lang="ts">
import { ref, onMounted } from 'vue';
import { useAuthStore } from '@/stores/auth';
import { toast, type ToastOptions } from 'vue3-toastify';
import ViewWrapper from '@/components/ViewWrapper.vue';
import AppTabs from '@/components/AppTabs.vue';
import ArrowUpDownIcon from '@/assets/images/ArrowsUpDown.svg';
import CategoriesIcon from '@/assets/images/Categories.svg'
import HistoryIcon from '@/assets/images/History.svg';
import DynamicIcon from '@/assets/images/Dynamic.svg';
import TabButton from '@/components/TabButton.vue';
import OperationsTab from '@/components/OperationsTab.vue';
import CategoriesTab from '@/components/CategoriesTab.vue';
import ArchiveTab from '@/components/ArchiveTab.vue';
import DynamicTab from '@/components/DynamicTab.vue';

const authStore = useAuthStore();
const data = ref(null);
const activeTab = ref(0);

const tabs = [
  { id: 'operations', icon: ArrowUpDownIcon, title: 'Операции', count: 12, component: OperationsTab },
  { id: 'categories', icon: CategoriesIcon, title: 'Категории', count: 0, component: CategoriesTab },
  { id: 'archive', icon: HistoryIcon, title: 'Архив', count: 0, component: ArchiveTab },
  { id: 'dynamic', icon: DynamicIcon, title: 'Динамика', count: 0, component: DynamicTab }
]

onMounted(async () => {
  try {
    await authStore.getUserInfo();
  } catch (err) {
    toast(`${err}`, {
      type: 'error',
      autoClose: 2000,
      position: toast.POSITION.BOTTOM_RIGHT,
    } as ToastOptions);
  }
});
</script>

<template>
  <ViewWrapper v-if="authStore.user" :avatar="authStore.user.photoBase64Format" :username="authStore.user.username">
    <AppTabs>
      <TabButton 
        v-for="tab, index in tabs" 
        :key="tab.title" 
        :icon="tab.icon" 
        :title="tab.title" 
        :count="tab.count" 
        :is-active="tab.title === tabs[activeTab].title"
        @click="activeTab = index" />
    </AppTabs>
    <KeepAlive>
      <component :is="tabs[activeTab].component" />
    </KeepAlive>
  </ViewWrapper>
</template>
