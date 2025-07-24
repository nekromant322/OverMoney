<script setup lang="ts">
import UserAvatar from './UserAvatar.vue';
import CloseIcon from '@/assets/images/Close.svg';
import SettingsItem from './SettingsItem.vue';
import SettingsIcon from '@/assets/images/Settings.svg';
import LinkIcon from '@/assets/images/Link.svg';
import EmailIcon from '@/assets/images/Email.svg';
import LogoutIcon from '@/assets/images/Logout.svg';
import { useRouter } from 'vue-router';
import routes from '@/router/routes';
import { useAuthStore } from '@/stores/auth';

const router = useRouter();
const authStore = useAuthStore();

const props = defineProps<{
  avatar: string | null,
  username: string
}>();

const emits = defineEmits(['closeSidebar']);

const logout = async () => {
  await authStore.logout();
  router.push(routes.login.path)
}
</script>

<template>
  <div :class="$style.sidebar">
    <header :class="$style.header">
      <div :class="$style.user">
        <UserAvatar :src="props.avatar" :username="username" alt="avatar" />
        <div :class="$style.username">{{ props.username }}</div>
      </div>
      <button :class="$style.closeButton" @click="emits('closeSidebar')">
        <img :src="CloseIcon" alt="close">
      </button>
    </header>
    <nav :class="$style.settings">
      <ul>
        <SettingsItem :icon="SettingsIcon" text="Настройки" />
      </ul>
      <div :class="$style.divider"></div>
      <ul>
        <SettingsItem :icon="LinkIcon" text="Политика конфиденциальности" />
        <SettingsItem :icon="EmailIcon" text="Написать в поддержку" />
      </ul>
      <div :class="$style.divider"></div>
      <ul>
        <SettingsItem :icon="LogoutIcon" text="Выйти" @click="logout" />
      </ul>
    </nav>
  </div>
</template>

<style module>
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
  z-index: 2;
}

.settings {
  margin-top: 16px;
}

.divider {
  height: 1px;
  width: 100%;
  background-color: #30363D;
  margin: 8px 0;
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
  color: #C9D1D9;
  font-family: 'Noto Sans', Arial, Helvetica, sans-serif;
  font-size: 14px;
  font-weight: 600;
}

.closeButton {
  width: 32px;
  height: 32px;
  display: flex;
  justify-content: center;
  align-items: center;
  background: transparent;
  border: none;
  padding: 0;
}

.closeButton:hover {
  background-color: #21262D;
}
</style>