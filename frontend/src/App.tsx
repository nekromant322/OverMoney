import React, { FC, useContext } from 'react';
import './App.scss';
import { BrowserRouter, Navigate, Route, Routes } from 'react-router-dom';
import { AuthContext, AuthProvider } from './context/AuthContext';
import Overmoney from './pages/Overmoney';
import History from './pages/History';
import Analytics from './pages/Analytics';
import Micromanagement from './pages/Micromanagement';
import Settings from './pages/Settings';
import Header from './components/Header';
import LoginPage from './pages/LoginPage';
import ProtectedRouter from './components/ProtectedRouter';

const App: FC = () => {
    // const url = process.env.REACT_APP_PATH_TO_HOST || '/front';
    // const { authenticated } = useContext(AuthContext);
    return (
        <BrowserRouter>
            <Header />
            {/* <Routes> */}
                {/* <Route path="/" element={<Navigate to={`${url}/overmoney`} />} /> */}
                {/* <Route path={`${url}/login`} Component={LoginPage} /> */}
                <AuthProvider >
                    <ProtectedRouter />
                    {/* <Routes>
                        <Route path={`${url}/overmoney`} element={<ProtectedRoute element={Overmoney} />}/>
                        <Route path={`${url}/history`} element={<ProtectedRoute element={History} />} />
                        <Route path={`${url}/analytics`} element={<ProtectedRoute element={Analytics} />} />
                        <Route path={`${url}/micromanagement`} element={<ProtectedRoute element={Micromanagement} />} />
                        <Route path={`${url}/settings`} element={<ProtectedRoute element={Settings} />} />
                        <Route path={`${url}/*`} element={<Navigate to={`${url}/overmoney`} />} />
                    </Routes> */}
                </AuthProvider>
            {/* </Routes> */}
        </BrowserRouter>
    );
}

export default App;