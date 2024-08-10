import React, { FC } from 'react';
import './App.scss';
import { BrowserRouter, Navigate,  Route, Routes } from 'react-router-dom';
import Overmoney from './pages/Overmoney';
import History from './pages/History';
import Analytics from './pages/Analytics';
import Micromanagement from './pages/Micromanagement';
import Settings from './pages/Settings';
import Header from './components/Header';
import LoginPage from './pages/LoginPage';

const App: FC = () => {
  const url = process.env.REACT_APP_PATH_TO_HOST || '/front';
  return (
    <BrowserRouter>
      <Header />
      <Routes>
        <Route path={`${url}/login`} Component={LoginPage} />
        <Route path={`${url}/overmoney`} Component={Overmoney} />
        <Route path={`${url}/history`} Component={History} />
        <Route path={`${url}/analytics`} Component={Analytics} />
        <Route path={`${url}/micromanagement`} Component={Micromanagement} />
        <Route path={`${url}/settings`} Component={Settings} />
        <Route path={`${url}/*`} element={<Navigate to={`${url}/overmoney`} />} />
      </Routes>
    </BrowserRouter>
  );
}

export default App;