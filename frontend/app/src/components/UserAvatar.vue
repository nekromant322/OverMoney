<script setup lang="ts">
import { computed } from 'vue';

const { src, alt, username } = defineProps<{
  username: string
  src: string | null
  alt: string
}>();

const firstLetter = computed(() => {
  return username.slice(0,1);
})
</script>

<template>
  <img v-if="src" class="avatar" :src="src" width="32" height="32" :alt="alt">
  <div v-else class="plug">{{ firstLetter }}</div>
</template>

<style scoped>
.avatar {
  display: block;
  border-radius: 50%;
}

.plug {
  width: 32px;
  height: 32px;
  display: flex;
  justify-content: center;
  align-items: center;
  font-family: 'Noto Sans', Arial, Helvetica, sans-serif;
  font-size: 16px;
  line-height: 16px;
  color: #fff;
  background: linear-gradient(125deg, #FF8491 0%, #FFA84F 100%);
  border-radius: 50%;
}
</style>