import React, { FC, useEffect, useState } from 'react';
import { Button, Container } from 'react-bootstrap';
import { ICard, IListItem } from '../../types/types';
import ListGroupCategories from '../ListGroupCategories/ListGroupCategories';
import PopupAddCategory from '../PopupAddCategory/PopupAddCategory';
import PopupChangeCategory from '../PopupChangeCategory/PopupChangeCategory';

interface ListCategoriesProps {
    listItems: IListItem[],
    handleDropCard: (card: ICard, category: string) => void,
    handleSubmitAddCategory: (formData: IListItem) => void,
    handleChangeCategory: (formData: IListItem) => void
}

const ListCategories : FC<ListCategoriesProps> = ({listItems, handleSubmitAddCategory, handleDropCard, handleChangeCategory}) => {

    const [listItemsIncome, setListItemsIncome] = useState<IListItem[]>([]);
    const [listItemsExpense, setListItemsExpense] = useState<IListItem[]>([]);
    const [selectedItem, setSelectedItem] = useState<IListItem>({} as IListItem);
    const [showModalAddCategory, setShowModalAddCategory] = useState<boolean>(false);
    const handleCloseModalAddCategory = () => setShowModalAddCategory(false);
    const handleShowModalAddCategory = () => setShowModalAddCategory(true);
    const [showModalChangeCategory, setShowModalChangeCategory] = useState<boolean>(false);
    const handleCloseModalChangeCategory = () => setShowModalChangeCategory(false);
    // const handleShowModalChangeCategory = () => setShowModalChangeCategory(true);


    useEffect(() => {
        setListItemsIncome(listItems.filter(item => item.type === "INCOME"));
        setListItemsExpense(listItems.filter(item => item.type === "EXPENSE"));
    }, [listItems]);

    const handleClickCategory = (item : IListItem) => {
        setShowModalChangeCategory(true)
        setSelectedItem(item)
    }

    const handleButtonSave = (formData: IListItem) => {
        //вызов API изменения категории 
        handleChangeCategory(formData)
    }



    return (
        <>
        <Container className="listgroup border-block p-3">
            <div className="listgroup__header h4 mb-3">Категории</div>
            <Container className='listgroup__items'>
                <ListGroupCategories handleDeleteCard={(card, category) => handleDropCard(card, category)} handleClickCategory={handleClickCategory} listItems={listItemsExpense} />
                <hr></hr>
                <ListGroupCategories handleDeleteCard={(card, category) => handleDropCard(card, category)} handleClickCategory={handleClickCategory} listItems={listItemsIncome} />
            </Container>
            <Button onClick={handleShowModalAddCategory} className="listgroup__button w-100 mt-5" variant="success">Добавить категорию</Button>
        </Container>
        <PopupAddCategory
            showModalAddCategory={showModalAddCategory} 
            handleCloseModalAddCategory={handleCloseModalAddCategory} 
            handleButtonSubmit={(formData: IListItem) => {
                handleSubmitAddCategory(formData)
                // handleShowModalAddCategory();
                handleCloseModalAddCategory()

            }}
        />
        {showModalChangeCategory && <PopupChangeCategory
            item={selectedItem}
            showModalChangeCategory={showModalChangeCategory}
            handleCloseModalChangeCategory={handleCloseModalChangeCategory}
            handleButtonSave={(formData: IListItem) => {
                handleButtonSave(formData)
                handleCloseModalChangeCategory()
            }}
        />
        }

        </>
    );
};

export default ListCategories;