import React, { FC } from 'react';
import { ListGroup } from 'react-bootstrap';
import { ICard, IListItem } from '../../types/types';
import ItemCategory from '../ItemCategory/ItemCategory';

interface ListGroupCategoriesProps {
    listItems: IListItem[], 
    handleClickCategory: (listItem: IListItem) => void,
    handleDeleteCard: (card: ICard, category: string) => void
}

const ListGroupCategories: FC<ListGroupCategoriesProps> = ({listItems, handleClickCategory, handleDeleteCard}) => {
    return (
        <ListGroup as={"ul"} >
            {listItems.map((item : IListItem) => (
                <ItemCategory 
                    key={item.id} 
                    item={item} 
                    handleDeleteCard={(card, category) => handleDeleteCard(card, category)} 
                    handleClickCategory={(listItem: IListItem) => handleClickCategory(listItem)}    
                />
            ))}
        </ListGroup>
    );
};

export default ListGroupCategories;