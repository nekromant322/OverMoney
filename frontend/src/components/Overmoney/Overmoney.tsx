import React, { FC } from 'react';
import { Col, Container, Row } from 'react-bootstrap';
import GridCards from '../GridCards/GridCards';
import ListCategories from '../ListCategories/ListCategories';

const Overmoney: FC = () => {
    return (
        <Container>
            <Row>
                <Col sm={8}>
                    <GridCards />
                </Col>
                <Col sm={4}>
                    <ListCategories />
                </Col>
            </Row>
        </Container>
    );
};

export default Overmoney;