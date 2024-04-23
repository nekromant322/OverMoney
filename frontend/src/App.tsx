import React from 'react';
import './App.scss';
import { BrowserRouter, Route, Router } from 'react-router-dom';
import { Nav, Navbar } from 'react-bootstrap';
import Container from 'react-bootstrap/Container';

const App: React.FC = () => {
  return (
    <BrowserRouter>
      <div className="App">
        <header className="App-header">
          <Navbar bg="dark" fixed="top" data-bs-theme="dark">
            <Container>
              <Navbar.Brand href="/">OverMoney</Navbar.Brand>
              <Nav className="me-auto">
                <Nav.Link href="/overmoney">Обзор</Nav.Link>
                <Nav.Link href="/history">История</Nav.Link>
                <Nav.Link href="/analytics">Аналитика</Nav.Link>
                <Nav.Link href="/micromanagement">Микроменеджмент</Nav.Link>
                <Nav.Link href="/settings">Настройки</Nav.Link>
              </Nav>
            </Container>
          </Navbar>
          {/* <Route exact path="/" component={Nav} >
          </Route>
          <Route path="/overmoney" component={Nav} />
          <Route path="/history" component={Nav} />
          <Route path="/analytics" component={Nav} />
          <Route path="/micromanagement" component={Nav} />
          <Route path="/settings" component={Nav} /> */}
        </header>
      </div>
    </BrowserRouter>
  );
}

export default App;