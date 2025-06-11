<script setup lang="ts">
import ArrowUpDownIcon from '@/assets/images/ArrowsUpDown.svg';
import CategoriesIcon from '@/assets/images/Categories.svg'
import HistoryIcon from '@/assets/images/History.svg';
import DynamicIcon from '@/assets/images/Dynamic.svg';
import Tab from './Tab.vue';

</script>

<template>
  <div class="container">
    <ul class="list">
      <li class="item">
        <Tab :icon="ArrowUpDownIcon" title="Операции" :count="12" :is-active="true" />
      </li>
      <li class="item">
        <Tab :icon="CategoriesIcon" title="Категории" :count="0" :is-active="false" />
      </li>
      <li class="item">
        <Tab :icon="HistoryIcon" title="Архив" :count="0" :is-active="false" />
      </li>
      <li class="item">
        <Tab :icon="DynamicIcon" title="Динамика" :count="0" :is-active="false" />
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