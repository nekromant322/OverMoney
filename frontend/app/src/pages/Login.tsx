import { useEffect, useRef } from 'react';

type TelegramUser = {
  id: number;
  first_name: string;
  last_name?: string;
  username?: string;
  photo_url?: string;
  auth_date: number;
  hash: string;
};

declare global {
  interface Window {
    onTelegramAuth: (user: TelegramUser) => void;
  }
}

export default function Login() {
  const widgetSlot = useRef<HTMLDivElement>(null);

  useEffect(() => {
    window.onTelegramAuth = async (user) => {
      try {
        const response = await fetch('/auth/login', {
          method: 'POST',
          headers: { 'Content-Type': 'application/json; charset=utf-8' },
          credentials: 'include',
          body: JSON.stringify(user),
        });
        if (!response.ok) {
          throw new Error(`Auth failed: ${response.status}`);
        }
        window.location.href = '/front/';
      } catch (e) {
        console.error('ERROR! Something wrong happened', e);
      }
    };

    let cancelled = false;

    fetch('/login/bot-login', { credentials: 'include' })
      .then((r) => r.text())
      .then((botName) => {
        if (cancelled || !widgetSlot.current) return;
        const script = document.createElement('script');
        script.src = 'https://telegram.org/js/telegram-widget.js?22';
        script.async = true;
        script.setAttribute('data-telegram-login', botName.trim());
        script.setAttribute('data-size', 'large');
        script.setAttribute('data-onauth', 'onTelegramAuth(user)');
        script.setAttribute('data-request-access', 'write');
        widgetSlot.current.appendChild(script);
      })
      .catch((e) => console.error('Failed to load bot name', e));

    return () => {
      cancelled = true;
    };
  }, []);

  return (
    <main className="login-page">
      <h1>OverMoney</h1>
      <p>Войдите через Telegram</p>
      <div ref={widgetSlot} />
    </main>
  );
}
