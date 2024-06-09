import React, { FC, useState } from 'react';
import { Badge, ListGroupItem } from 'react-bootstrap';
import { ICard, IListItem } from '../../types/types';

interface ItemCategoryProps {
    item: IListItem,
    handleDeleteCard: (card: ICard, category: string) => void,
    handleClickCategory: (listItem: IListItem) => void
}

const ItemCategory: FC<ItemCategoryProps> = ({item, handleDeleteCard, handleClickCategory}) => {

    const [isDragOver, setIsDragOver] = useState<boolean>(false);

    const dropHandler = (e: React.DragEvent<HTMLDivElement>, item: IListItem) => {
        e.preventDefault()
        setIsDragOver(false)
        const data = e.dataTransfer.getData("ICard");
        const card : ICard = JSON.parse(data)
        item.keywords?.push(card.message);
        console.log('DROP', card)
        handleDeleteCard(card, item.name);
    }

    const dragWithPreventHandler = (e: React.DragEvent<HTMLDivElement>) => {
        e.preventDefault()
        setIsDragOver(true)
    }

    const leaveHandler = (e: React.DragEvent<HTMLDivElement>) => {
        e.preventDefault()
        setIsDragOver(false)
    }

    const onClickHandler = (e: React.MouseEvent<HTMLDivElement>) => {
        e.preventDefault()
        handleClickCategory(item)
    }

    return (
        <ListGroupItem 
            key={item.id}
            as="li" 
            className="listgroup__item d-flex justify-content-between align-items-start" 
            onDrop={(e: React.DragEvent<HTMLDivElement>) => dropHandler(e, item)} 
            onDragLeave={leaveHandler} 
            onDragOver={dragWithPreventHandler} 
            onClick={onClickHandler}
            style={{backgroundColor: isDragOver ? 'blue' : 'inherit'}}
        >
            <span className="listgroup__text-item mt-1 mb-1 ms-1 me-auto">
                {item.name}
            </span>
            <Badge bg={item.type === "INCOME" ? "success" : "danger"} pill>
                {item.keywords?.length}
            </Badge>
        </ListGroupItem>
    );
};

export default ItemCategory;