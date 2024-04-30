import React, { FC } from 'react';
import './App.scss';
import { BrowserRouter, Navigate,  Route, Routes, useLocation } from 'react-router-dom';
import Overmoney from '../Overmoney/Overmoney';
import History from '../History/History';
import Analytics from '../Analytics/Analytics';
import Micromanagement from '../Micromanagement/Micromanagement';
import Settings from '../Settings/Settings';
import Header from '../Header/Header';

const App: FC = () => {
  const url = process.env.REACT_APP_PATH_TO_HOST || '/front';
  return (
    <BrowserRouter>
      <Header />
      <Routes>
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