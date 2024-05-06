import React, { FC, useCallback, useEffect, useState } from 'react';
import { Col, Container, Row } from 'react-bootstrap';
import GridCards from '../GridCards/GridCards';
import ListCategories from '../ListCategories/ListCategories';
import { ICard, IListItem } from '../../types/types';
import PopupAddCategory from '../PopupAddCategory/PopupAddCategory';
import { constCards } from '../../utils/utils';
import { constlistItems } from '../../utils/utils';


const Overmoney: FC = () => {

    const [cards, setCards] = useState<ICard[]>(constCards);
    const [listItems, setListItems] = useState<IListItem[]>(constlistItems);

    useEffect(() => {
        setCards(cards);
    }, [cards]);

    const handleSubmitAddCard = useCallback((formData: ICard) => {
        //вызов API добавления категории 
        setCards([
            ...cards,
            {
                message: formData.message,
                amount: formData.amount,
            }
        ]);
        handleCloseModalAddCard();
    }, [cards]);

    const handleDeleteCard = useCallback((card: ICard) => {
        setCards(cards.splice(cards.indexOf(card), 1));
    }, [cards]);


    return (
        <>
            <Container className='mt-5 pb-5'>
                <Row>
                    <Col sm={8}>
                        <GridCards cards={cards} />
                    </Col>
                    <Col sm={4}>
                        <ListCategories listItems={listItems} handleDeleteCard={handleDeleteCard} />
                    </Col>
                </Row>
            </Container>
            
        </>
    );
};

export default Overmoney;