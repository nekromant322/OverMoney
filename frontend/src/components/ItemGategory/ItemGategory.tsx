import React, { FC, useState } from 'react';
import { Badge, ListGroupItem } from 'react-bootstrap';
import { ICard, IListItem } from '../../types/types';

interface ItemGategoryProps {
    item: IListItem,
    handleDeleteCard: (card: ICard) => void
}

const ItemGategory: FC<ItemGategoryProps> = ({item, handleDeleteCard}) => {

    const [isDragOver, setIsDragOver] = useState<boolean>(false);

    const dropHandler = (e: React.DragEvent<HTMLDivElement>, item: IListItem) => {
        e.preventDefault()
        setIsDragOver(false)
        const data = e.dataTransfer.getData("ICard");
        const card : ICard = JSON.parse(data)
        item.keywords?.push(card.message);
        console.log('DROP', card)
        handleDeleteCard(card);
    }

    const dragWithPreventHandler = (e: React.DragEvent<HTMLDivElement>) => {
        e.preventDefault()
        setIsDragOver(true)
        console.log('DRAGover', e.target)
    }

    const leaveHandler = (e: React.DragEvent<HTMLDivElement>) => {
        e.preventDefault()
        console.log('LEAVE', e.target)
        setIsDragOver(false)
    }

    return (
        <ListGroupItem 
            key={item.id}
            as="li" 
            className="listgroup__item d-flex justify-content-between align-items-start" 
            onDrop={(e: React.DragEvent<HTMLDivElement>) => dropHandler(e, item)} 
            onDragLeave={leaveHandler} 
            onDragOver={dragWithPreventHandler} 
            style={{backgroundColor: isDragOver ? 'lightgreen' : 'inherit'}}
        >
            <p className="listgroup__text-item mt-2 mb-2 ms-2 me-auto">
                {item.name}
            </p>
            <Badge bg={item.type === "INCOME" ? "success" : "danger"} pill>
                {item.keywords?.length}
            </Badge>
        </ListGroupItem>
    );
};

export default ItemGategory;