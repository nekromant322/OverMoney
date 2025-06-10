<script setup lang="ts">
import Icon from './Icon.vue';

const props = defineProps<{
  icon: string,
  text: string
}>();
</script>

<template>
  <li class="item">
    <Icon :src="icon" :alt="text" />
    <span class="text">{{ text }}</span>
  </li>
</template>

<style scoped>
.item {
  display: flex;
  align-items: center;
  padding: 8px;
}

.text {
  margin-left: 8px;
  color: #C9D1D9;
  font-family: 'NotoSans', Arial, Helvetica, sans-serif;
  font-size: 14px;
}
</style>