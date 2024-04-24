import React, { FC } from 'react';
import { Nav, Navbar } from 'react-bootstrap';
import { Container } from 'react-bootstrap';

const Navigate: FC = () => {
    return (
        <Navbar bg="dark" fixed="top" data-bs-theme="dark">
            <Container >
              <Navbar.Brand href="/">OverMoney</Navbar.Brand>
              <Nav>
                <Nav.Link active={false} href="/overmoney">Обзор</Nav.Link>
                <Nav.Link active={true} href="/history">История</Nav.Link>
                <Nav.Link href="/analytics">Аналитика</Nav.Link>
                <Nav.Link href="/micromanagement">Микроменеджмент</Nav.Link>
                <Nav.Link href="/settings">Настройки</Nav.Link>
              </Nav>
            </Container>
          </Navbar>
    );
};

export default Navigate;