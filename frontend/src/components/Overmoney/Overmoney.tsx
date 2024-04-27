import React, { FC } from 'react';
import { Col, Container, Row } from 'react-bootstrap';
import GridCards from '../GridCards/GridCards';
import ListCategories from '../ListCategories/ListCategories';
import { ICard, IListItem } from '../../types/types';

const cards = [
    {
        id: 1,
        title: 'Карта 1',
        amount: 1000,
        size: 4
    },
    {
        id: 2,
        title: 'Карта 2',
        amount: 800,
        size: 3
    },
    {
        id: 3,
        title: 'Карта 3',
        amount: 600,
        size: 2
    },
    {
        id: 4,
        title: 'Карта 4',
        amount: 400,
        size: 1
    },
    {
        id: 5,
        title: 'Карта 5',
        amount: 200,
        size: 1
    },
    {
        id: 6,
        title: 'Карта 6',
        amount: 100,
        size: 0
    },

] as ICard[];

const ListItems = [
    {
        id: 1,
        title: 'Категория 1',
        subtype: "income",
        keywords: ['Ключевое слово 1', 'Ключевое слово 2', 'Ключевое слово 3'],
    },
    {
        id: 3,
        title: 'Категория 3',
        subtype: "income",
        keywords: ['Ключевое слово 1', 'Ключевое слово 2', 'Ключевое слово 3'],
    },
    {
        id: 2,
        title: 'Категория 2',
        subtype: "expense",
        keywords: ['Ключевое слово 1', 'Ключевое слово 2', 'Ключевое слово 3'],
    },
    {
        id: 4,
        title: 'Категория 4',
        subtype: "expense",
        keywords: ['Ключевое слово 1', 'Ключевое слово 2', 'Ключевое слово 3'],
    },

] as IListItem[];
const Overmoney: FC = () => {
    return (
        <Container>
            <Row>
                <Col sm={8}>
                    <GridCards cards={cards} />
                </Col>
                <Col sm={4}>
                    <ListCategories items={ListItems} />
                </Col>
            </Row>
        </Container>
    );
};

export default Overmoney;