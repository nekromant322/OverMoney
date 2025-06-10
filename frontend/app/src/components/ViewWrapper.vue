<script setup lang="ts">
import { ref } from 'vue';
import AppHeader from './AppHeader.vue';
import ProfileSidebar from './ProfileSidebar.vue';
import Overlay from './Overlay.vue';

const props = defineProps<{
  avatar: string,
  username: string
}>()

const isSidebarVisible = ref<boolean>(false);
</script>

<template>
  <AppHeader :avatar="props.avatar" @show-sidebar="isSidebarVisible = true" />
  <div class="wrapper">
    <slot></slot>
  </div>
  <ProfileSidebar :class="{ visible: isSidebarVisible }" :avatar="props.avatar" :username="props.username" @close-sidebar="isSidebarVisible = false" />
  <Overlay v-if="isSidebarVisible" @click="isSidebarVisible = false" />
</template>

<style scoped>
.wrapper {
  position: relative;
  background-color: #0D1117;
  margin: 0 auto;
  min-height: calc(100% - 60px);
  padding: 0 32px;
}
</style>