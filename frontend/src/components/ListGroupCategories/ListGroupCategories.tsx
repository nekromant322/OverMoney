import React, { FC } from 'react';
import { ListGroup } from 'react-bootstrap';
import { ICard, IListItem } from '../../types/types';
import ItemGategory from '../ItemGategory/ItemGategory';

interface ListGroupCategoriesProps {
    listItems: IListItem[], 
    handleDeleteCard: (card: ICard) => void
}

const ListGroupCategories: FC<ListGroupCategoriesProps> = ({listItems, handleDeleteCard}) => {
    return (
        <ListGroup as={"ul"} >
            {listItems.map((item : IListItem) => (
                <ItemGategory key={item.id} item={item} handleDeleteCard={handleDeleteCard} />
            ))}
        </ListGroup>
    );
};

export default ListGroupCategories;