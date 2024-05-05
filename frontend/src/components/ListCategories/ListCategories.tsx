import React, { FC, useCallback, useEffect, useState } from 'react';
import { Badge, Button, Container, ListGroup } from 'react-bootstrap';
import ListGroupItem from 'react-bootstrap/ListGroupItem'
import { IListItem } from '../../types/types';
import ItemGategory from '../ItemGategory/ItemGategory';
import ListGroupCategories from '../ListGroupCategories/ListGroupCategories';
import PopupAddCategory from '../PopupAddCategory/PopupAddCategory';

const constlistItems = [
    {
        id: 1,
        name: 'Категория 1',
        type: "INCOME",
        keywords: ['Ключевое слово 1', 'Ключевое слово 2', 'Ключевое слово 3'],
    },
    {
        id: 3,
        name: 'Категория 3',
        type: "INCOME",
        keywords: ['Ключевое слово 1', 'Ключевое слово 2', 'Ключевое слово 3'],
    },
    {
        id: 2,
        name: 'Категория 2',
        type: "EXPENSE",
        keywords: ['Ключевое слово 1', 'Ключевое слово 2', 'Ключевое слово 3'],
    },
    {
        id: 4,
        name: 'Категория 4',
        type: "EXPENSE",
        keywords: ['Ключевое слово 1', 'Ключевое слово 2', 'Ключевое слово 3'],
    },

] as IListItem[];

const ListCategories : FC = () => {

    const [listItems, setListItems] = useState<IListItem[]>(constlistItems);
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
                <ListGroupCategories listItems={listItemsExpense} />
                <hr></hr>
                <ListGroupCategories listItems={listItemsIncome} />
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