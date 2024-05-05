import React, { FC, useCallback, useState } from 'react';
import { Col, Container, Row } from 'react-bootstrap';
import GridCards from '../GridCards/GridCards';
import ListCategories from '../ListCategories/ListCategories';
import { ICard, IListItem } from '../../types/types';
import PopupAddCategory from '../PopupAddCategory/PopupAddCategory';


const Overmoney: FC = () => {

    return (
        <>
            <Container className='mt-5 pb-5'>
                <Row>
                    <Col sm={8}>
                        <GridCards cards={cards} />
                    </Col>
                    <Col sm={4}>
                        <ListCategories />
                    </Col>
                </Row>
            </Container>
            
        </>
    );
};

export default Overmoney;