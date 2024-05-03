import React, { FC, useEffect } from 'react';
import { ICard } from '../../types/types';
import { Button, Col, Container, Row } from 'react-bootstrap';
import ItemCard from '../ItemCard/ItemCard';

interface GridCardsProps {
    cards: ICard[]
}
const GridCards: FC<GridCardsProps> = ({cards}) => {
    
    return (
        <Container className="gridcards d-flex flex-column align-items-center border-block p-3">
            <Row xs={1} md={2} className="g-4">
                {cards.map((card : ICard) => (
                    <Col key={card.id} className="d-flex justify-content-center">
                        <ItemCard {...card} />
                    </Col>
                ))}
            </Row>
            <Button className="w-50 mt-5" variant="success">Добавить транзакцию</Button>
        </Container>
    );
};

export default GridCards;