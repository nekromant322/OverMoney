import React, { FC, useContext } from 'react';
import { Container } from 'react-bootstrap';
import TelegramLoginButton from '../components/TelegramLoginButton';
import { useNavigate } from 'react-router-dom';
import { AuthContext } from '../context/AuthContext';
import axios from 'axios';
import { IUser } from '../types/types';

const LoginPage: FC = () => {
    const url = process.env.REACT_APP_PATH_TO_HOST || '/front'
    const baseUrl = process.env.REACT_APP_BASE_URL || 'https://overmoney.tech'

    const { setAuthenticated } = useContext(AuthContext)

    const navigate = useNavigate()

    // Обработка данных пользователя после аутентификации через Telegram Login Widget
    const handleUserAuth = (user: IUser) => {
        console.log(user); 
        axios.post(`${baseUrl}/auth/login`, user)
            .then(response => {
                    console.log("success login")
                    setAuthenticated(true)
                    navigate(`${url}/overmoney`)
                }
            )
            .catch(error => console.log("error login"))
    };
    return (
        <Container className="d-flex align-items-center justify-content-center mt-5">
            <TelegramLoginButton dataOnauth={handleUserAuth} />
        </Container>
    );
};

export default LoginPage;