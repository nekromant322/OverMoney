import React, { FC, useEffect } from 'react';
import { ICard } from '../../types/types';
import { Col, Row } from 'react-bootstrap';
import ItemCard from '../ItemCard/ItemCard';

interface GridCardsProps {
    cards: ICard[]
}
const GridCards: FC<GridCardsProps> = ({cards}) => {
    
    return (
        <Row xs={1} md={2} className="g-4">
            {cards.map((card : ICard) => (
                <Col key={card.id}>
                    <ItemCard card={card} />
                </Col>
            ))}
        </Row>
    );
};

export default GridCards;