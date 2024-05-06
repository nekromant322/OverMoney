import React, { FC, useCallback, useEffect, useState } from 'react';
import { Badge, Button, Container, ListGroup } from 'react-bootstrap';
import ListGroupItem from 'react-bootstrap/ListGroupItem'
import { IListItem } from '../../types/types';
import ItemGategory from '../ItemGategory/ItemGategory';
import ListGroupCategories from '../ListGroupCategories/ListGroupCategories';
import PopupAddCategory from '../PopupAddCategory/PopupAddCategory';



const ListCategories : FC = ({handleDeleteCard}) => {

    const [listItemsIncome, setListItemsIncome] = useState<IListItem[]>([]);
    const [listItemsExpense, setListItemsExpense] = useState<IListItem[]>([]);
    const [showModalAddCategory, setShowModalAddCategory] = useState<boolean>(false);
    const handleCloseModalAddCategory = () => setShowModalAddCategory(false);
    const handleShowModalAddCategory = () => setShowModalAddCategory(true);

    useEffect(() => {
        setListItemsIncome(listItems.filter(item => item.type === "INCOME"));
        setListItemsExpense(listItems.filter(item => item.type === "EXPENSE"));
    }, [listItems]);

    const handleSubmitAddCategory = useCallback((formData: IListItem) => {
        //вызов API добавления категории 
        setListItems([
            ...listItems,
            {
                name: formData.name,
                type: formData.type,
                keywords: [formData.name]
            }
        ]);
        handleCloseModalAddCategory();
    }, [listItems]);


    return (
        <>
        <Container className="listgroup border-block p-3">
            <div className="listgroup__header h4 mb-3">Категории</div>
            <Container className='listgroup__items'>
                <ListGroupCategories handleDeleteCard={handleDeleteCard} listItems={listItemsExpense} />
                <hr></hr>
                <ListGroupCategories handleDeleteCard={handleDeleteCard} listItems={listItemsIncome} />
            </Container>
            <Button onClick={handleShowModalAddCategory} className="listgroup__button w-100 mt-5" variant="success">Добавить категорию</Button>
        </Container>
        <PopupAddCategory
            showModalAddCategory={showModalAddCategory} 
            handleCloseModalAddCategory={handleCloseModalAddCategory} 
            handleButtonSubmit={handleSubmitAddCategory}
        />
        </>
    );
};

export default ListCategories;