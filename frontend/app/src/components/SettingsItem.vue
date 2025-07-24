<script setup lang="ts">
import AppIcon from './AppIcon.vue';

defineProps<{
  icon: string,
  text: string,
}>();
</script>

<template>
  <li :class="$style.item">
      <button :class="$style.button">
        <AppIcon :src="icon" :alt="text" />
        <span :class="$style.text">{{ text }}</span>
      </button>
  </li>
</template>

<style module>
.item {
  padding: 8px;
  border-radius: 6px;
}

.item:hover {
  background-color: #21262D;
  cursor: pointer;
}

.button {
  display: flex;
  align-items: center;
}

.text {
  margin-left: 8px;
  color: #C9D1D9;
  font-family: 'Noto Sans', Arial, Helvetica, sans-serif;
  font-size: 14px;
}
</style>