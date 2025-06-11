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
  <transition name="fade">
    <Overlay v-if="isSidebarVisible" @click="isSidebarVisible = false" />
  </transition>
</template>

<style scoped>
.wrapper {
  position: relative;
  background-color: #0D1117;
  margin: 0 auto;
  min-height: calc(100% - 60px);
}

.fade-enter-active, .fade-leave-active {
  transition: opacity 0.3s ease;
}
.fade-enter-from, .fade-leave-to {
  opacity: 0;
}
.fade-enter-to, .fade-leave-from {
  opacity: 0.4;
}
</style>