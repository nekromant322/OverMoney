import React, { FC } from 'react';
import { Nav, Navbar, Container } from 'react-bootstrap';
import { Link, useLocation } from 'react-router-dom';

const Header: FC = () => {
  const url = process.env.REACT_APP_PATH_TO_HOST || '/front';
  let location = useLocation();
  return (
    <header className="w-100 m-0">
      <Navbar sticky="top" data-bs-theme="dark">
        <Container className="mt-3 mb-3">
          <Navbar.Brand >OverMoney</Navbar.Brand>
          <Nav className="mr-auto" variant="underline" defaultActiveKey={`${url}/overmoney`}>
            <Nav.Link as={Link} active={location.pathname === url+"/overmoney"} to={`${url}/overmoney`}>Обзор</Nav.Link>
            <Nav.Link as={Link} active={location.pathname === url+"/history"} to={`${url}/history`}>История</Nav.Link>
            <Nav.Link as={Link} active={location.pathname === url+"/analytics"} to={`${url}/analytics`}>Аналитика</Nav.Link>
            <Nav.Link as={Link} active={location.pathname === url+"/micromanagement"} to={`${url}/micromanagement`}>Микроменеджмент</Nav.Link>
            <Nav.Link as={Link} active={location.pathname === url+"/settings"} to={`${url}/settings`}>Настройки</Nav.Link>
          </Nav>
        </Container>
      </Navbar>
    </header>
  );
};

export default Header;