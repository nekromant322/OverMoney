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
  <li :class="$style.item">
    <button :class="[$style.button, {[$style.active]: isActive }]">
      <div :class="$style.info">
        <img :class="$style.icon" :src="icon" :alt="title">
        <div :class="[$style.title, { [$style.activeTitle]: isActive }]">{{ title }}</div>
        <CountItem v-if="count > 0" :class="$style.count" :count="count" />
      </div>
      <div :class="[$style.highlight, { [$style.activeHighlight]: isActive }]"></div>
    </button>
  </li>
</template>

<style module>
.item {
  width: fit-content;
  margin-left: 8px;
  user-select: none;
}

@container (max-width: 466px) {
  .item .title {
    display: none;
  }
}
 
.item:first-child {
  margin-left: 0;
}

.button {
  padding: 5px 8px;
  position: relative;
  display: flex;
  align-items: stretch;
  height: 42px;
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

.activeTitle {
  color: #fff;
}

.activeHighlight {
  opacity: 1;
}
</style>