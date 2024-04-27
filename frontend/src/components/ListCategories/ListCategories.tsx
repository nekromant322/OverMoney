import React, { FC } from 'react';
import { Badge, Container, ListGroup } from 'react-bootstrap';
import ListGroupItem from 'react-bootstrap/ListGroupItem'
import { IListItem } from '../../types/types';

interface ListCategoriesProps {
    items: IListItem[]
}

const ListCategories : FC<ListCategoriesProps> = ({items}) => {
    return (
        <Container>
            <div className="listgroup__header">Категории</div>
            <ListGroup as={"ul"}>
                {items.map((item : IListItem) => (
                    <ListGroupItem as="li" className="d-flex justify-content-between align-items-start " key={item.id}>
                        <div className="mt-4 mb-4 ms-2 me-auto">
                            {item.title}
                        </div>
                        <Badge bg="primary" pill>
                            {item.keywords.length}
                        </Badge>
                    </ListGroupItem>
                ))}
            </ListGroup>
        </Container>
    );
};

export default ListCategories;