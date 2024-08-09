import React, { FC, useEffect, useState } from 'react';
import { Button, Container } from 'react-bootstrap';
import { ICard, ICategory } from '../types/types';
import ListGroupCategories from './ListGroupCategories';
import PopupAddCategory from './PopupAddCategory';
import PopupChangeCategory from './PopupChangeCategory';

interface ListCategoriesProps {
    listItems: ICategory[],
    isTwoCollumns: boolean,
    handleDropCard: (card: ICard, category: string) => void,
    handleSubmitAddCategory: (formData: ICategory) => void,
    handleChangeCategory: (formData: ICategory) => void
}

const ListCategories : FC<ListCategoriesProps> = ({listItems, isTwoCollumns, handleSubmitAddCategory, handleDropCard, handleChangeCategory}) => {

    const [listItemsIncome, setListItemsIncome] = useState<ICategory[]>([]);
    const [listItemsExpense, setListItemsExpense] = useState<ICategory[]>([]);
    const [selectedItem, setSelectedItem] = useState<ICategory>({} as ICategory);
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

    const handleClickCategory = (item : ICategory) => {
        setShowModalChangeCategory(true)
        setSelectedItem(item)
    }

    const handleButtonSave = (formData: ICategory) => {
        //вызов API изменения категории 
        handleChangeCategory(formData)
    }

    return (
        <>
        <Container className="listgroup border-block p-2">
            <div className="listgroup__header h4 ms-2 mb-3">Категории</div>
            <Container className='listgroup__items'>
                <ListGroupCategories 
                    isTwoCollumns={isTwoCollumns}
                    handleDeleteCard={(card, category) => handleDropCard(card, category)} 
                    handleClickCategory={handleClickCategory} 
                    listItems={listItemsExpense} />
                <hr></hr>
                <ListGroupCategories 
                    isTwoCollumns={isTwoCollumns}
                    handleDeleteCard={(card, category) => handleDropCard(card, category)} 
                    handleClickCategory={handleClickCategory} 
                    listItems={listItemsIncome} />
            </Container>
            <Button onClick={handleShowModalAddCategory} className="listgroup__button w-100 mt-3" variant="success">Добавить категорию</Button>
        </Container>
        <PopupAddCategory
            showModalAddCategory={showModalAddCategory} 
            handleCloseModalAddCategory={handleCloseModalAddCategory} 
            handleButtonSubmit={(formData: ICategory) => {
                handleSubmitAddCategory(formData)
                // handleShowModalAddCategory();
                handleCloseModalAddCategory()

            }}
        />
        {showModalChangeCategory && <PopupChangeCategory
            item={selectedItem}
            showModalChangeCategory={showModalChangeCategory}
            handleCloseModalChangeCategory={handleCloseModalChangeCategory}
            handleButtonSave={(formData: ICategory) => {
                handleButtonSave(formData)
                handleCloseModalChangeCategory()
            }}
        />
        }

        </>
    );
};

export default ListCategories;