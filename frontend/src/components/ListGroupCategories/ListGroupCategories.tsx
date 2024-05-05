import React, { FC } from 'react';
import { ListGroup } from 'react-bootstrap';
import { IListItem } from '../../types/types';
import ItemGategory from '../ItemGategory/ItemGategory';

interface ListGroupCategoriesProps {
    listItems: IListItem[]
}

const ListGroupCategories: FC<ListGroupCategoriesProps> = ({listItems}) => {
    return (
        <ListGroup as={"ul"} >
            {listItems.map((item : IListItem) => (
                <ItemGategory key={item.id} item={item} />
            ))}
        </ListGroup>
    );
};

export default ListGroupCategories;