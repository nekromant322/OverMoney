<script setup lang="ts">
import ArrowUpDownIcon from '@/assets/images/ArrowsUpDown.svg';
import CategoriesIcon from '@/assets/images/Categories.svg'
import HistoryIcon from '@/assets/images/History.svg';
import DynamicIcon from '@/assets/images/Dynamic.svg';
import Tab from './Tab.vue';

const tabs = [
  { icon: ArrowUpDownIcon, title: 'Операции', count: 12, active: true },
  { icon: CategoriesIcon, title: 'Категории', count: 0, active: false },
  { icon: HistoryIcon, title: 'Архив', count: 0, active: false },
  { icon: DynamicIcon, title: 'Динамика', count: 0, active: false }
]
</script>

<template>
  <div class="container">
    <ul class="list">
      <li class="item" v-for="tab in tabs">
        <Tab :key="tab.title" :icon="tab.icon" :title="tab.title" :count="tab.count" :is-active="tab.active" />
      </li>
    </ul>
  </div>
</template>

<style scoped>
.list {
  display: flex;
  justify-content: center;
  border-top: 1px solid #30363D;
  border-bottom: 1px solid #30363D;
  padding: 0 32px;
}

.item {
  width: fit-content;
  margin-left: 8px;
}
 
.item:first-child {
  margin-left: 0;
}
</style>