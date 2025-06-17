import { defineStore } from "pinia";
import { Store } from "./stores";
import { ref } from "vue";
import type { TelegramUser } from "../../global";

const LOGIN_URL = `${import.meta.env.VITE_API_URL}/auth/login`;
const LOGOUT_URL = `${import.meta.env.VITE_API_URL}/auth/logout`;
const CHECK_URL = `${import.meta.env.VITE_API_URL}/auth/check`;
const USER_INFO_URL = `${import.meta.env.VITE_API_URL}/users/current`;

interface User {
  username: string,
  photoBase64Format: string
}

export const useAuthStore = defineStore(Store.Auth, () => {
  const isAuthenticated = ref(false);
  const user = ref<User | null>(null);

  const login = async (user: TelegramUser) => {
    const response = await fetch(LOGIN_URL, {
      method: 'POST',
      body: JSON.stringify(user),
      headers: {
        'Content-Type': 'application/json; charset=utf-8',
        'Accept': 'application/json'
      },
      credentials: 'include'
    });

    if (!response.ok) {
      throw new Error(`${response.status}: ${response.statusText}`);
    }

    isAuthenticated.value = true;
  }

  const logout = async () => {
    const response = await fetch(LOGOUT_URL, {
      method: 'POST',
      credentials: 'include'
    })

    if (!response.ok) {
      throw new Error(`${response.status}: ${response.statusText}`);
    }

    isAuthenticated.value = false;
  }

  const check = async () => {
    const response = await fetch(CHECK_URL, {
      method: 'GET',
      credentials: 'include',
      headers: {
        'Accept': 'application/json'
      }
    })

    if (!response.ok) {
      throw new Error(`${response.status}: ${response.statusText}`);
    }

    isAuthenticated.value = await response.json();
  }

  const getUserInfo = async () => {
    const response = await fetch(USER_INFO_URL, {
      method: 'GET',
      credentials: 'include',
      headers: {
        'Accept': 'application/json'
      }
    })

    if (!response.ok) {
      throw new Error(`${response.status}: ${response.statusText}`);
    }

    user.value = await response.json();
  }

  return {
    isAuthenticated,
    user,
    login,
    logout,
    check,
    getUserInfo
  } 
});