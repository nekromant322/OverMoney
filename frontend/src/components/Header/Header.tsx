import React, { FC } from 'react';
import { Nav, Navbar, Container } from 'react-bootstrap';
import { Link, useLocation } from 'react-router-dom';

const Header: FC = () => {
  let location = useLocation();
  return (
    <header>
      <Navbar bg="dark" sticky="top" data-bs-theme="dark">
        <Container className="mt-3 mb-5">
          <Navbar.Brand href="/">OverMoney</Navbar.Brand>
          <Nav className="mr-auto" variant="underline" defaultActiveKey="/">
            <Nav.Link as={Link} active={location.pathname === "/overmoney"} to={"/overmoney"}>Обзор</Nav.Link>
            <Nav.Link as={Link} active={location.pathname === "/history"} to="/history">История</Nav.Link>
            <Nav.Link as={Link} active={location.pathname === "/analytics"} to="/analytics">Аналитика</Nav.Link>
            <Nav.Link as={Link} active={location.pathname === "/micromanagement"} to="/micromanagement">Микроменеджмент</Nav.Link>
            <Nav.Link as={Link} active={location.pathname === "/settings"} to="/settings">Настройки</Nav.Link>
          </Nav>
        </Container>
      </Navbar>
    </header>
  );
};

export default Header;