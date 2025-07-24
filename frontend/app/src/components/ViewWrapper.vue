<script setup lang="ts">
import { ref } from 'vue';
import AppHeader from './AppHeader.vue';
import ProfileSidebar from './ProfileSidebar.vue';
import AppOverlay from './AppOverlay.vue';

const props = defineProps<{
  avatar: string | null,
  username: string
}>()

const isSidebarVisible = ref<boolean>(false);
</script>

<template>
  <AppHeader :avatar="props.avatar" :username="username" @show-sidebar="isSidebarVisible = true" />
  <div :class="$style.wrapper">
    <slot></slot>
  </div>
  <ProfileSidebar :class="[$style.sidebar, { [$style.visible]: isSidebarVisible }]" :avatar="props.avatar" :username="props.username" @close-sidebar="isSidebarVisible = false" />
  <transition name="fade">
    <AppOverlay v-if="isSidebarVisible" @click="isSidebarVisible = false" />
  </transition>
</template>

<style module>
.wrapper {
  position: relative;
  background-color: #0D1117;
  margin: 0 auto;
  min-height: calc(100% - 60px);
}

.sidebar {
  right: -320px;
  transition: right 0.3s ease-in-out;
}

.visible {
  right: 0;
}
</style>