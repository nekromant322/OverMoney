import React, { FC, useEffect, useState } from 'react';
import { ICard } from '../../types/types';
import { Button, Col, Container, Row } from 'react-bootstrap';
import ItemCard from '../ItemCard/ItemCard';

const constCards = [
    {
        id: '1',
        message: 'Карта 1sddsagagsgdsads',
        amount: 1000,
        size: 4
    },
    {
        id: '2',
        message: 'Карта 2',
        amount: 800,
        size: 3
    },
    {
        id: '3',
        message: 'Карта 3',
        amount: 600,
        size: 2
    },
    {
        id: '4',
        message: 'Карта 4',
        amount: 400,
        size: 1
    },
    {
        id: '5',
        message: 'Карта 5',
        amount: 200,
        size: 1
    },
    {
        id: '6',
        message: 'Карта 6',
        amount: 100,
        size: 0
    },

] as ICard[];

const GridCards: FC = () => {

    const [cards, setCards] = useState<ICard[]>(constCards);
    const [showModalAddCard, setShowModalAddCard] = useState<boolean>(false);
    const handleShowModalAddCard = () => setShowModalAddCard(true);
    const handleCloseModalAddCard = () => setShowModalAddCard(false);
    
    useEffect(() => {
        setCards(cards);
    }, [cards]);


    
    return (
        <>
            <Container className="gridcards d-flex flex-column align-items-center border-block p-3">
                <Row xs={1} md={2} className="g-4">
                    {cards.map((card : ICard) => (
                        <Col key={card.id} className="d-flex justify-content-center">
                            <ItemCard key={card.id} {...card} />
                        </Col>
                    ))}
                </Row>
                <Button onClick={handleShowModalAddCard} className="w-50 mt-5" variant="success">Добавить транзакцию</Button>
            </Container>
            
        </>
    );
};

export default GridCards;