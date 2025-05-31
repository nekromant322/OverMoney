<script setup lang="ts">
import logo from '@/assets/images/logo.svg';
import ViewWrapper from '@/components/ViewWrapper.vue';
import { onMounted } from 'vue';
import type { TelegramUser } from '../../global';

const LOGIN_URL = `${import.meta.env.VITE_BASE_URL}/login`;
const REDIRECT_URL = `${import.meta.env.VITE_BASE_URL}`;
const TELEGRAM_WIDGET_SRC = `https://telegram.org/js/telegram-widget.js?${import.meta.env.VITE_TELEGRAM_WIDGET_VERSION}`;

window.onTelegramAuth = async (user: TelegramUser) => {
  try {
    const response = await fetch(LOGIN_URL, {
      method: 'POST',
      body: JSON.stringify(user),
      headers: {
        'Content-Type': 'application/json; charset=utf-8',
      },
    });

    if (!response.ok) {
      // TODO Replace with the UI error
      console.error('Authorization failed');
      return;
    }

    window.location.href = REDIRECT_URL;
  } catch (err) {
    // TODO Replace with the UI error
    console.error('Login error:', err);
  }
};

onMounted(() => {
  const element = document.getElementById('login-button');
  if (!element) return;

  const script = document.createElement('script');
  script.async = true;
  script.src = TELEGRAM_WIDGET_SRC;
  script.setAttribute('data-telegram-login', import.meta.env.VITE_TELEGRAM_BOT_NAME);
  script.setAttribute('data-size', 'medium');
  script.setAttribute('data-onauth', 'onTelegramAuth(user)');
  script.setAttribute('data-request-access', 'write');
  script.setAttribute('data-lang', 'ru');
  script.setAttribute('data-radius', '6');
  script.setAttribute('data-userpic', 'true');

  element.appendChild(script);
});
</script>

<template>
  <ViewWrapper>
    <header class="header">
      <a href="%VITE_LANDING_URL%">
        <img class="logo" :src="logo" width="32" height="32" alt="app logo">
      </a>
    </header>
    <main class="content">
      <div class="login-form">
        <h1 class="title">OverMoney</h1>
        <h2 class="subtitle">Твой трекер финансов</h2>
        <div id="login-button" class="login-button-wrapper">
          <!-- Telegram login widget will be appended here -->
        </div>
      </div>
    </main>
    <footer class="footer">
      <a href="/">
        <img class="logo" :src="logo" width="32" height="32" alt="app logo">
      </a>
      <span class="copyright">© 2024 OverMoney</span>
    </footer>
  </ViewWrapper>
</template>
<style scoped>
.header {
  padding: 14px 0;
  box-sizing: border-box;
  display: flex;
}

.logo {
  display: block;
}

.content {
  display: flex;
  align-items: center;
  height: calc(100vh - 160px - 60px);
}

.login-form {
  margin: 0 auto;
  display: flex;
  flex-direction: column;
  text-align: center;
}

.title {
  line-height: normal;
  font-size: 72px;
  font-weight: 800;
  background-image: linear-gradient(to right, #FF8491, #FFA84F);
  color: transparent;
  background-clip: text;
  display: inline-block;
  margin: 0;
}

.subtitle {
  line-height: normal;
  font-size: 32px;
  font-weight: 500;
  background-image: linear-gradient(to right, #D16C77, #C9833C);
  color: transparent;
  background-clip: text;
  display: inline-block;
  margin: 16px 0 0 0;
}

.footer {
  margin-top: 80px;
  position: relative;
  display: flex;
  align-items: center;
  justify-content: center;
  width: 100%;
  padding: 0 32px 48px 32px;
}

.copyright {
  color: #8B949E;
  font-size: 12px;
  white-space: nowrap;
  margin-left: 8px;
}

.login-button-wrapper {
  margin-top: 60px;
}
</style>
