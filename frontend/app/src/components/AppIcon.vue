<script setup lang="ts">
const { src } = defineProps<{
  src: string,
  alt: string
}>();
</script>

<template>
  <img :src="src" class="icon" width="16" height="16" :alt="alt">
</template>

<style scoped>
.icon {
  display: block;
}
</style>