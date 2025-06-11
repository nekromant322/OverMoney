<script setup lang="ts">
import LogoIcon from '@/assets/images/logo.svg';
import { RouterLink } from 'vue-router';
import Avatar from './Avatar.vue';

const props = defineProps<{
  avatar: string
}>();

const emits = defineEmits(['showSidebar']);
</script>

<template>
  <header class="header">
    <RouterLink to="/" class="home-link">
      <Avatar :src="LogoIcon" alt="logo" />
    </RouterLink>
    <button class="profile-button" @click="emits('showSidebar')">
      <!-- TODO Replace src to user avatar -->
      <Avatar :src="props.avatar" alt="avatar" />
    </button> 
  </header>
</template>

<style scoped>
.header {
  padding: 14px 32px;
  box-sizing: border-box;
  display: flex;
  justify-content: space-between;
  background-color: #010409;
}

.home-link {
  width: 32px;
  height: 32px;
}

.logo {
  display: block;
}

.profile-button {
  background-color: transparent;
  border: none;
  display: flex;
  padding: 0;
  width: 32px;
  height: 32px;
}
</style>