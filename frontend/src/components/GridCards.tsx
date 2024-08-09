import React, { FC, useCallback, useEffect, useState } from 'react';
import { ICard } from '../types/types';
import { Button, Col, Container, Row } from 'react-bootstrap';
import ItemCard from './ItemCard';
import PopupAddCard from './PopupAddCard';


interface GridCardsProps {
    cards: ICard[];
    // handleDropCard: (card: ICard) => void;
    handleSubmitAddCard: (formData: ICard) => void;
}

const GridCards: FC<GridCardsProps> = ({cards, handleSubmitAddCard}) => {

    const [showModalAddCard, setShowModalAddCard] = useState<boolean>(false);
    const handleShowModalAddCard = () => setShowModalAddCard(true);
    const handleCloseModalAddCard = () => setShowModalAddCard(false);

    return (
        <>
            <Container className="gridcards d-flex flex-column align-items-center border-block p-3">
                <Row xs={1} md={2} className="gridcards__items mt-1 g-4">
                    {cards.map((card : ICard) => (
                        <Col key={card.id} className="d-flex">
                            <ItemCard key={card.id} {...card} />
                        </Col>
                    ))}
                </Row>
                <Button onClick={handleShowModalAddCard} className="w-50 mt-5" variant="success">Добавить транзакцию</Button>
            </Container>
            <PopupAddCard 
                showModalAddCard={showModalAddCard} 
                handleCloseModalAddCard={handleCloseModalAddCard} 
                handleButtonSubmit={(formData: ICard) => {
                    handleSubmitAddCard(formData); 
                    handleCloseModalAddCard();
                }}
                />
        </>
    );
};

export default GridCards;