<script setup lang="ts">
import Count from './Count.vue';

defineProps<{
  icon: string,
  title: string,
  count: number,
  isActive: boolean
}>();
</script>

<template>
  <button :class="['tab', {'active': isActive }]">
    <div class="info">
      <img class="icon" :src="icon" :alt="title">
      <div class="title">{{ title }}</div>
      <Count v-if="count > 0" class="count" :count="count" />
    </div>
    <div v-if="isActive" class="highlight"></div>
  </button>
</template>

<style scoped>
.tab {
  padding: 5px 8px;
  position: relative;
}

.tab.active .title {
  font-weight: 600;
}

.info {
  display: flex;
  align-items: center;
}

.title {
  margin-left: 8px;
  line-height: 32px;
  color: #C9D1D9;
  font-family: 'NotoSans', Arial, Helvetica, sans-serif;
  font-size: 14px;
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
}
</style>