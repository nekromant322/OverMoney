<script setup lang="ts">
import CountItem from './CountItem.vue';

defineProps<{
  icon: string,
  title: string,
  count: number,
  isActive: boolean
}>();
</script>

<template>
  <li class="item">
    <button :class="['button', {'active': isActive }]">
      <div class="info">
        <img class="icon" :src="icon" :alt="title">
        <div class="title">{{ title }}</div>
        <CountItem v-if="count > 0" class="count" :count="count" />
      </div>
      <div class="highlight"></div>
    </button>
  </li>
</template>

<style scoped>
.item {
  width: fit-content;
  margin-left: 8px;
  user-select: none;
}
 
.item:first-child {
  margin-left: 0;
}

.button {
  padding: 5px 8px;
  position: relative;
}

.button.active .title {
  color: #fff;
}

.button.active .highlight {
  opacity: 1;
}

.button:hover .title {
  color: #fff;
}

.info {
  display: flex;
  align-items: center;
}

.title {
  margin-left: 8px;
  line-height: 32px;
  color: #C9D1D9;
  font-family: 'Noto Sans', Arial, Helvetica, sans-serif;
  font-size: 14px;
  transition: color 0.3s ease;
}

.count {
  margin-left: 8px;
}

.highlight {
  background-color: #F78166;
  width: 100%;
  height: 2px;
  border-radius: 6px;
  position: absolute;
  bottom: 0;
  lefT: 0;
  opacity: 0;
  transition: opacity 0.3s ease;
}
</style>