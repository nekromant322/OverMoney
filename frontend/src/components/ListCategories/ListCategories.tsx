import React, { FC } from 'react';
import { Badge, Button, Container, ListGroup } from 'react-bootstrap';
import ListGroupItem from 'react-bootstrap/ListGroupItem'
import { IListItem } from '../../types/types';

interface ListCategoriesProps {
    items: IListItem[]
}

const ListCategories : FC<ListCategoriesProps> = ({items}) => {
    return (
        <Container className="listgroup border-block p-3">
            <div className="listgroup__header mb-3">Категории</div>
            <ListGroup as={"ul"}>
                {items.map((item : IListItem) => (
                    <ListGroupItem as="li" className="listgroup__item d-flex justify-content-between align-items-start " key={item.id}>
                        <div className="mt-4 mb-4 ms-2 me-auto">
                            {item.name}
                        </div>
                        <Badge bg="primary" pill>
                            {item.keywords.length}
                        </Badge>
                    </ListGroupItem>
                ))}
            </ListGroup>
            <Button className="listgroup__button w-100 mt-5"  variant="success">Добавить категорию</Button>
        </Container>
    );
};

export default ListCategories;