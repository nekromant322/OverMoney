import React, { FC, useEffect, useState } from 'react';
import { Col, ListGroup, Row } from 'react-bootstrap';
import { ICard, ICategory } from '../types/types';
import ItemCategory from './ItemCategory';

interface ListGroupCategoriesProps {
    listItems: ICategory[], 
    isTwoCollumns: boolean,
    handleClickCategory: (listItem: ICategory) => void,
    handleDeleteCard: (card: ICard, category: string) => void
}

const ListGroupCategories: FC<ListGroupCategoriesProps> = ({listItems, isTwoCollumns, handleClickCategory, handleDeleteCard}) => {

    const [firstHalfItems, setFirstHalfItems] = useState<ICategory[]>([]);
    const [secondHalfItems, setSecondHalfItems] = useState<ICategory[]>([]);
    
    useEffect(() => {
        if (isTwoCollumns) {
            const half = Math.ceil(listItems.length / 2);
            setFirstHalfItems(listItems.slice(0, half));
            setSecondHalfItems(listItems.slice(half));
        }
    }, [listItems, isTwoCollumns])

    return (
        <ListGroup as={"ul"} >
            {isTwoCollumns ? 
            <Row>
                <Col className='p-0'>
                    {firstHalfItems.map((item: ICategory) => (
                        <ItemCategory 
                        key={item.id} 
                        item={item} 
                        handleDeleteCard={handleDeleteCard} 
                        handleClickCategory={handleClickCategory}    
                        />
                    ))}
                </Col>
                <Col className='p-0'>
                    {secondHalfItems.map((item: ICategory) => (
                        <ItemCategory 
                        key={item.id} 
                        item={item} 
                        handleDeleteCard={handleDeleteCard} 
                        handleClickCategory={handleClickCategory}    
                        />
                    ))}
                </Col>
            </Row>
            :
            <>
                {listItems.map((item : ICategory) => (
                    <ItemCategory 
                        key={item.id} 
                        item={item} 
                        handleDeleteCard={(card, category) => handleDeleteCard(card, category)} 
                        handleClickCategory={(listItem: ICategory) => handleClickCategory(listItem)}    
                    />
            ))}
            </>}
        </ListGroup>
    );
};

export default ListGroupCategories;