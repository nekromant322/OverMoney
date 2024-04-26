import React, { FC } from 'react';
import { ICard } from '../../types/types';
import { Card } from 'react-bootstrap';


const ItemCard : FC<ICard> = (card: ICard) => {
  return (
    <Card key={card.id}>
      <Card.Body>
        <Card.Title>{card.title}</Card.Title>
        <Card.Text>{card.money}</Card.Text>
      </Card.Body>
    </Card>
  );
};

export default ItemCard;