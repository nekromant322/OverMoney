<script setup lang="ts">
import Avatar from './Avatar.vue';
import CloseIcon from '@/assets/images/Close.svg';

const props = defineProps<{
  avatar: string,
  username: string
}>();

const emits = defineEmits(['closeSidebar']);
</script>

<template>
  <div class="sidebar">
    <header class="header">
      <div class="user">
        <Avatar :src="props.avatar" alt="avatar" />
        <div class="username app-username">{{ props.username }}</div>
      </div>
      <button class="close-button" @click="emits('closeSidebar')">
        <img :src="CloseIcon" alt="close">
      </button>
    </header>
  </div>
</template>

<style scoped>
.sidebar {
  height: 100%;
  min-width: 320px;
  background-color: #151B23;
  position: fixed;
  top: 0;
  padding: 16px;
  box-sizing: border-box;
  border-top: #30363D 1px solid;
  border-left: #30363D 1px solid;
  border-bottom: #30363D 1px solid;
  border-top-left-radius: 6px;
  border-bottom-left-radius: 6px;
  right: -320px;
  transition: right 0.3s ease-in-out;
}

.sidebar.visible {
  right: 0;
}

.header {
  display: flex;
  justify-content: space-between;
}

.user {
  display: flex;
}

.username {
  margin-left: 8px;
  line-height: 32px;
}

.close-button {
  width: 32px;
  height: 32px;
  display: flex;
  justify-content: center;
  align-items: center;
  background: transparent;
  border: none;
  padding: 0;
}
</style>