<script setup lang="ts">
const { src } = defineProps<{
  src: string,
  alt: string
}>();
</script>

<template>
  <img :src="src" :class="$style.icon" width="16" height="16" :alt="alt">
</template>

<style module>
.icon {
  display: block;
}
</style>