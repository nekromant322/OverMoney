import React, { FC } from 'react';
import './App.scss';
import { BrowserRouter, Route, Routes} from 'react-router-dom';
import Overmoney from '../Overmoney/Overmoney';
import History from '../History/History';
import Analytics from '../Analytics/Analytics';
import Micromanagement from '../Micromanagement/Micromanagement';
import Settings from '../Settings/Settings';
import Header from '../Header/Header';

const App: FC = () => {

  return (
    <BrowserRouter>
          <Header />
          <Routes>
            <Route path="/" Component={Overmoney} />
            <Route path="/overmoney" Component={Overmoney} />
            <Route path="/history" Component={History} />
            <Route path="/analytics" Component={Analytics} />
            <Route path="/micromanagement" Component={Micromanagement} />
            <Route path="/settings" Component={Settings} />
          </Routes>
    </BrowserRouter>
  );
}

export default App;