import React, { useEffect } from 'react';
import { IUser } from '../types/types';
import axios from 'axios';

interface TelegramLoginButtonProps {
    dataOnauth: (user: IUser) => void; // Функция обратного вызова для обработки данных пользователя после аутентификации
}

const TelegramLoginButton: React.FC<TelegramLoginButtonProps> = ({ dataOnauth }) => {
    // Подготовка ссылки для Telegram Login Widget
    const versionWidget = process.env.REACT_APP_VERSION_WIDGET || '22';
    const scriptSrc = `https://telegram.org/js/telegram-widget.js?${versionWidget}`;
    const baseUrl = process.env.REACT_APP_BASE_URL || 'https://overmoney.tech'

    useEffect(() => {

        axios.get(`${baseUrl}/login/bot-login`)
            .then(response => {
                console.log(response.data)
                const script = document.createElement('script');
                script.src = scriptSrc;
                script.setAttribute('data-telegram-login', response.data);
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
                
            })
            .catch(error => console.log(error))
    }, []);

    return <div id="telegram-button"></div>;
};

export default TelegramLoginButton;