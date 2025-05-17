# OverMoney Landing

## Требования
- [NodeJS 20+](https://nodejs.org)

## Запуск
1. Скопировать файл `.env.local.example` и переименовать в `.env.local`
2. Установить переменные окружения: VITE_BASE_URL - (ngrok)[https://ngrok.com/] домен (Telegram Login Widget не работает на localhost), VITE_APP_URL - localhost где будет запущен OverMoney, VITE_TELEGRAM_BOT_NAME - название Telegram бота (своего, в случае локально запущенного backend или тестового, в случае обращений к тестовому серверу).
3. `npm install`
4. `npm run dev`

## Сборка
1. `npm install`
2. `npm run build`