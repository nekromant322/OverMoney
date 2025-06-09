<script setup lang="ts">
import logo from '@/assets/images/logo.svg';
import { RouterLink } from 'vue-router';
</script>

<template>
  <header class="header">
      <RouterLink to="/">
        <img class="logo" :src="logo" width="32" height="32" alt="app logo">
      </RouterLink>
      <button class="profile-button">
        <!-- TODO Replace src from logo to user avatar -->
        <img class="avatar" :src="logo" width="32" height="32" alt="user avatar">
      </button>
    </header>
</template>

<style scoped>
.header {
  padding: 14px 0;
  box-sizing: border-box;
  display: flex;
  justify-content: space-between;
}

.logo {
  display: block;
}

.profile-button {
  background-color: transparent;
  border: none;
}

.avatar {
  display: block;
}
</style>