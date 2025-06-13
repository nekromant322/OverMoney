<script setup lang="ts">
import { ref, onMounted } from 'vue';
import ViewWrapper from '@/components/ViewWrapper.vue';
import ExampleAvatar from '@/assets/images/example-avatar.jpg';
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

const data = ref(null);

// TODO Get data from an endpoint
const username = 'nekromant322';

const activeTab = ref(0);

// TODO Add URL tab GET parameter
const tabs = [
  { icon: ArrowUpDownIcon, title: 'Операции', count: 12, component: OperationsTab },
  { icon: CategoriesIcon, title: 'Категории', count: 0, component: CategoriesTab },
  { icon: HistoryIcon, title: 'Архив', count: 0, component: ArchiveTab },
  { icon: DynamicIcon, title: 'Динамика', count: 0, component: DynamicTab }
]

onMounted(async () => {
  try {
    const response = await fetch(`${import.meta.env.VITE_API_URL}/transactions`, {
      method: 'GET',
      headers: {
      'Content-Type': 'application/json',
    },
    });
    if (!response.ok) {
      throw new Error('Network response was not ok');
    }
    data.value = await response.json();
  } catch (error) {
    console.error('Error fetching data:', error);
  }
});
</script>

<template>
  <ViewWrapper :avatar="ExampleAvatar" :username="username">
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
