import React, { FC } from 'react';
import { Nav, Navbar } from 'react-bootstrap';
import { Container } from 'react-bootstrap';
import { useLocation } from 'react-router-dom';

const Header: FC = () => {
  let location = useLocation();
  return (
    <header>
      <Navbar bg="dark" fixed="top" data-bs-theme="dark">
        <Container >
          <Navbar.Brand href="/">OverMoney</Navbar.Brand>
          <Nav>
            <Nav.Link active={location.pathname === '/overmoney'} href="/overmoney">Обзор</Nav.Link>
            <Nav.Link active={location.pathname === '/history'} href="/history">История</Nav.Link>
            <Nav.Link active={location.pathname === '/analytics'} href="/analytics">Аналитика</Nav.Link>
            <Nav.Link active={location.pathname === '/micromanagement'} href="/micromanagement">Микроменеджмент</Nav.Link>
            <Nav.Link active={location.pathname === '/settings'} href="/settings">Настройки</Nav.Link>
          </Nav>
        </Container>
      </Navbar>
    </header>
  );
};

export default Header;