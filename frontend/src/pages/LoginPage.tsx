import React, { FC, useEffect, useState } from 'react';
import { Container } from 'react-bootstrap';
import TelegramLoginButton from '../components/TelegramLoginButton';
import { useNavigate } from 'react-router-dom';

const LoginPage: FC = () => {

    const [botName, setBotName] = useState('');

    const navigate = useNavigate()

    useEffect(() => {
        fetch('https://overmoney.tech/login/bot-login', { method: 'GET', mode: 'no-cors' })
            .then(response => response.text())
            .then(data => {
                setBotName(data)
                console.log(data)
                console.log(botName)
            })
            .catch(error => console.log(error))
    }, [])
    // Обработка данных пользователя после аутентификации через Telegram Login Widget
    const handleUserAuth = (user: any) => {
        console.log(user); 
        user = {}
        fetch('https://overmoney.tech/auth/login', 
            {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(user)
            })
            .then(response => {
                if(response.ok) {
                    console.log("success login")
                    navigate('/overmoney')
                } else {
                    console.log("error login")
                }
            })
            .catch(error => console.log(error))


    };
    return (
        <Container className="d-flex align-items-center justify-content-center mt-5">
            {/* <TelegramLoginButton botName={botName} dataOnauth={handleUserAuth} /> */}
            <TelegramLoginButton botName="testSignin_bot" dataOnauth={handleUserAuth} />
        </Container>
    );
};

export default LoginPage;