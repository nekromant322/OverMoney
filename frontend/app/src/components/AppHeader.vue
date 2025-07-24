<script setup lang="ts">
import LogoIcon from '@/assets/images/logo.svg';
import { RouterLink } from 'vue-router';
import UserAvatar from './UserAvatar.vue';

const props = defineProps<{
  username: string
  avatar: string | null
}>();

const emits = defineEmits(['showSidebar']);
</script>

<template>
  <header :class="$style.header">
    <RouterLink to="/" :class="$style.homeLink">
      <UserAvatar :src="LogoIcon" :username="username" alt="logo" />
    </RouterLink>
    <button :class="$style.profileButton" @click="emits('showSidebar')">
      <UserAvatar :src="props.avatar" :username="username" alt="avatar" />
    </button> 
  </header>
</template>

<style module>
.header {
  padding: 14px 32px;
  box-sizing: border-box;
  display: flex;
  justify-content: space-between;
  background-color: #010409;
}

.homeLink {
  width: 32px;
  height: 32px;
}

.logo {
  display: block;
}

.profileButton {
  background-color: transparent;
  border: none;
  display: flex;
  padding: 0;
  width: 32px;
  height: 32px;
}
</style>