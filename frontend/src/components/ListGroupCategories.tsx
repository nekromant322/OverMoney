import React, { FC, useEffect, useState } from 'react';
import { Col, ListGroup, Row } from 'react-bootstrap';
import { ICard, IListItem } from '../types/types';
import ItemCategory from './ItemCategory';

interface ListGroupCategoriesProps {
    listItems: IListItem[], 
    isTwoCollumns: boolean,
    handleClickCategory: (listItem: IListItem) => void,
    handleDeleteCard: (card: ICard, category: string) => void
}

const ListGroupCategories: FC<ListGroupCategoriesProps> = ({listItems, isTwoCollumns, handleClickCategory, handleDeleteCard}) => {

    const [firstHalfItems, setFirstHalfItems] = useState<IListItem[]>([]);
    const [secondHalfItems, setSecondHalfItems] = useState<IListItem[]>([]);
    
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
                    {firstHalfItems.map((item: IListItem) => (
                        <ItemCategory 
                        key={item.id} 
                        item={item} 
                        handleDeleteCard={handleDeleteCard} 
                        handleClickCategory={handleClickCategory}    
                        />
                    ))}
                </Col>
                <Col className='p-0'>
                    {secondHalfItems.map((item: IListItem) => (
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
                {listItems.map((item : IListItem) => (
                    <ItemCategory 
                        key={item.id} 
                        item={item} 
                        handleDeleteCard={(card, category) => handleDeleteCard(card, category)} 
                        handleClickCategory={(listItem: IListItem) => handleClickCategory(listItem)}    
                    />
            ))}
            </>}
        </ListGroup>
    );
};

export default ListGroupCategories;