import React, { FC } from 'react';
import { Badge, ListGroupItem } from 'react-bootstrap';
import { IListItem } from '../../types/types';

interface ItemGategoryProps {
    item: IListItem
}

const ItemGategory: FC<ItemGategoryProps> = ({item}) => {
    return (
        <ListGroupItem as="li" className="listgroup__item d-flex justify-content-between align-items-start" key={item.id}>
            <p className="listgroup__text-item mt-2 mb-2 ms-2 me-auto">
                {item.name}
            </p>
            {/* <Badge bg={item.type === "INCOME" ? "success" : "danger"} pill>
                {item.keywords?.length}
            </Badge> */}
        </ListGroupItem>
    );
};

export default ItemGategory;