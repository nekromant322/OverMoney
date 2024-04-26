import React, { FC } from 'react';
import 'bootstrap/dist/css/bootstrap.min.css';
import './App.scss';
import { BrowserRouter, Route, Routes} from 'react-router-dom';
import Overmoney from '../Overmoney/Overmoney';
import History from '../History/History';
import Analytics from '../Analytics/Analytics';
import Micromanagement from '../Micromanagement/Micromanagement';
import Settings from '../Settings/Settings';
import Header from '../Header/Header';
import { Container } from 'react-bootstrap';

const App: FC = () => {


  return (
    <BrowserRouter>
      <Container className="App">
          <Header />
          <Routes>
            <Route path="/" Component={Overmoney} />
            <Route path="/overmoney" Component={Overmoney} />
            <Route path="/history" Component={History} />
            <Route path="/analytics" Component={Analytics} />
            <Route path="/micromanagement" Component={Micromanagement} />
            <Route path="/settings" Component={Settings} />
          </Routes>
      </Container>
    </BrowserRouter>
  );
}

export default App;