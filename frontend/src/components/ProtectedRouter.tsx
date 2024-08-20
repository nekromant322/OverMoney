import React, { FC, useContext, useEffect, useState } from 'react';
import { Navigate, Outlet, Route, Router, Routes } from 'react-router-dom';
// import { useAuth } from "../hooks/useAuth";
import { AuthContext } from '../context/AuthContext';
import LoginPage from '../pages/LoginPage';
import Overmoney from '../pages/Overmoney';
import History from '../pages/History';
import Analytics from '../pages/Analytics';
import Micromanagement from '../pages/Micromanagement';
import Settings from '../pages/Settings';
import { CategoriesContext } from '../context/CategoriesContext';
import { TransactionsContext } from '../context/TransactionsContext';
import axios from 'axios';

type Props = {}


const AuthRoute = () => {
    const url = process.env.REACT_APP_PATH_TO_HOST || '/front';
    // const auth = useAuth();
    const { authenticated } = useContext(AuthContext);
    if (!authenticated) {
        // user is not authenticated
        return <Navigate to={`${url}/login`} replace/>;
    }
    
    return <Outlet/>;
};

const ProtectedRouter = (props : Props) => {
    const url = process.env.REACT_APP_PATH_TO_HOST || '/front'
    // const baseUrl = process.env.REACT_APP_BASE_URL || 'https://overmoney.tech'

    const { authenticated } = useContext(AuthContext)
    const [categories, setCategories] = useState([]);
    const [transactions, setTransactions] = useState([]);
    useEffect(() => {
        if (authenticated) {
            //вызов API получения списка транзакции
            axios.get(`/categories`)
            .then(response => {
                setCategories(response.data);
            })
            .catch(error => {
                console.error(error);
            });
            axios.get(`/transactions`)
            .then(response => {
                setTransactions(response.data);
            })
            .catch(error => {
                console.error(error);
            });
            
        }
    }, [authenticated])
    return (
        <CategoriesContext.Provider value={categories}>
            <TransactionsContext.Provider value={transactions}>
                <Routes>
                    <Route path={`${url}/login`} element={<LoginPage />} />
                    <Route element={<AuthRoute />}>
                        <Route path={`${url}/overmoney`} element={<Overmoney />} />
                        <Route path={`${url}/history`} element={<History />} />
                        <Route path={`${url}/analytics`} element={<Analytics />} />
                        <Route path={`${url}/micromanagement`} element={<Micromanagement />} />
                        <Route path={`${url}/settings`} element={<Settings />} />
                        <Route path={`${url}/*`} element={<Navigate to={`${url}/overmoney`} />} />
                    </Route>
                </Routes>
            </TransactionsContext.Provider>
        </CategoriesContext.Provider>
    )
}

export default ProtectedRouter;