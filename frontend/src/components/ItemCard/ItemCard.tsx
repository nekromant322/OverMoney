import React, { FC } from 'react';
import { ICard } from '../../types/types';
import { Card } from 'react-bootstrap';


const ItemCard: FC<ICard> = (card: ICard) => {
    let size: number;
    let amount = card.amount;
    switch (true) {
        case amount >= 0 && amount < 100:
            size = 0;
            break;
        case amount >= 100 && amount < 500:
            size = 1;
            break;
        case amount >= 500 && amount < 1000:
            size = 2;
            break;
        case amount >= 1000 && amount < 5000:
            size = 3;
            break;
        case amount >= 5000 && amount < 10000:
            size = 4;
            break;
        case amount >= 10000:
            size = 5;
            break;
        default:
            size = 0;
    }

    const [isDrag, setIsDrag] = React.useState<boolean>(false);

    const handleDragStart = (event: React.DragEvent<HTMLDivElement>, card: ICard) => {
        setIsDrag(true);
        event.dataTransfer.setData("ICard", JSON.stringify(card));
    }

    const handleDragEnd = () => {
        setIsDrag(false);
    }

    return (
        <Card 
            onDragStart={(event: React.DragEvent<HTMLDivElement>) => handleDragStart(event, card)} 
            onDragEnd={handleDragEnd} 
            draggable 
            style={{ width: `calc(${40 + 10 * size}% + 2vmin)`, opacity: isDrag ? 0.3 : 1 }}>
            <Card.Body className="d-block text-center" style={{ fontSize: `calc(${7 + 2 * size}px + 2vmin)` }}>
                <Card.Text>{card.message}</Card.Text>
                <Card.Text>{card.amount}</Card.Text>
            </Card.Body>
        </Card>
    );
};

export default ItemCard;