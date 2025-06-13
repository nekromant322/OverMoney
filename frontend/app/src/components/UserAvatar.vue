<script setup lang="ts">
const { src, alt } = defineProps<{
  src: string
  alt: string
}>();
</script>

<template>
  <img class="avatar" :src="src" width="32" height="32" :alt="alt">
</template>

<style scoped>
.avatar {
  display: block;
  border-radius: 50%;
}
</style>