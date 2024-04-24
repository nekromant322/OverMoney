import React, { FC, useState } from 'react';
import 'bootstrap/dist/css/bootstrap.min.css';
import './App.scss';
import { BrowserRouter, Route, Routes} from 'react-router-dom';
import Overmoney from '../Overmoney/Overmoney';
import History from '../History/History';
import Analytics from '../Analytics/Analytics';
import Micromanagement from '../Micromanagement/Micromanagement';
import Settings from '../Settings/Settings';
import Navigate from '../Navigate/Navigate';

const App: FC = () => {

  const [activeSatate, setActiveState] = useState();

  return (
    <BrowserRouter>
      <div className="App">
        <header className="App-header">
          <Navigate activePage={activeSatate} />
          <Routes>
            <Route path="/" Component={Overmoney} />
            <Route path="/overmoney" Component={Overmoney} />
            <Route path="/history" Component={History} />
            <Route path="/analytics" Component={Analytics} />
            <Route path="/micromanagement" Component={Micromanagement} />
            <Route path="/settings" Component={Settings} />
          </Routes>
        </header>
      </div>
    </BrowserRouter>
  );
}

export default App;