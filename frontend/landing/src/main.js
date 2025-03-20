const LOGIN_URL = `${import.meta.env.VITE_BASE_URL}/auth/login`;
const APP_URL = '/overmoney';

window.addEventListener('load', async () => {
  embedTelegramWidget()
})

window.onTelegramAuth = async (user) => {
  console.log(LOGIN_URL);
  const response = await fetch({
    method: 'POST',
    url: LOGIN_URL,
    data: JSON.stringify(user),
    contentType: "application/json; charset=utf8",
  });

  if (response.status !== 200) {
    console.error('Authorization failed');
    return;
  }

  window.location.href = `${import.meta.env.VITE_APP_URL}/overmoney`;
}

function embedTelegramWidget() {
  const script = document.createElement('script');

  script.async = true;
  script.setAttribute('src', `https://telegram.org/js/telegram-widget.js?${import.meta.env.VITE_TELEGRAM_WIDGET_VERSION}`);
  script.setAttribute('data-telegram-login', import.meta.env.VITE_TELEGRAM_BOT_NAME);
  script.setAttribute("data-size", "medium");
  script.setAttribute("data-onauth", "onTelegramAuth(user)");
  script.setAttribute("data-request-access", "write");
  script.setAttribute('data-lang', 'ru');
  script.setAttribute('data-radius', '6');
  script.setAttribute('data-userpic', 'true');

  const element = document.getElementById('login-button');
  element.appendChild(script);
}
