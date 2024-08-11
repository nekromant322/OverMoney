import React, { useEffect } from 'react';
import { IUser } from '../types/types';

interface TelegramLoginButtonProps {
    botName: string; // Имя вашего бота в Telegram
    dataOnauth: (user: IUser) => void; // Функция обратного вызова для обработки данных пользователя после аутентификации
}

const TelegramLoginButton: React.FC<TelegramLoginButtonProps> = ({ botName, dataOnauth }) => {
    // Подготовка ссылки для Telegram Login Widget
    const versionWidget = process.env.REACT_APP_VERSION_WIDGET || '22';
    const scriptSrc = `https://telegram.org/js/telegram-widget.js?${versionWidget}`;

    useEffect(() => {
        const script = document.createElement('script');
        script.src = scriptSrc;
        script.setAttribute('data-telegram-login', botName);
        script.setAttribute('data-size', 'large'); // Размер кнопки
        script.setAttribute('data-radius', '5'); // Радиус углов кнопки
        script.setAttribute('data-onauth', 'handleTelegramAuth(user)'); // Обработчик события аутентификации
        script.async = true;

        document.querySelector('#telegram-button')?.appendChild(script);

        (window as any).handleTelegramAuth = (user: IUser) => {
        dataOnauth(user);
        };

        return () => {
        document.querySelector('#telegram-button')?.removeChild(script);
        };
    }, []);

    return <div id="telegram-button"></div>;
};

export default TelegramLoginButton;