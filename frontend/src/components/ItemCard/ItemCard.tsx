import React, { FC } from 'react';
import { ICard } from '../../types/types';
import { Card } from 'react-bootstrap';


const ItemCard : FC<ICard> = (card: ICard) => {
  return (
    <Card key={card.id} style={{ width: `calc(${40 + 10 *card.size}% + 2vmin)`}}>
      <Card.Body className="d-block text-center" style={{ fontSize: `calc(${7 + 2 *card.size}px + 2vmin)`}}>
        <Card.Text>{card.title}</Card.Text>
        <Card.Text>{card.amount}</Card.Text>
      </Card.Body>
    </Card>
  );
};

export default ItemCard;