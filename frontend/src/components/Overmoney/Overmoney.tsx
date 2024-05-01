import React, { FC, useCallback, useState } from 'react';
import { Col, Container, Row } from 'react-bootstrap';
import GridCards from '../GridCards/GridCards';
import ListCategories from '../ListCategories/ListCategories';
import { ICard, IListItem } from '../../types/types';
import PopupAddCategory from '../PopupAddCategory/PopupAddCategory';

const cards = [
    {
        id: '1',
        message: 'Карта 1sddsagagsgdsads',
        amount: 1000,
        size: 4
    },
    {
        id: '2',
        message: 'Карта 2',
        amount: 800,
        size: 3
    },
    {
        id: '3',
        message: 'Карта 3',
        amount: 600,
        size: 2
    },
    {
        id: '4',
        message: 'Карта 4',
        amount: 400,
        size: 1
    },
    {
        id: '5',
        message: 'Карта 5',
        amount: 200,
        size: 1
    },
    {
        id: '6',
        message: 'Карта 6',
        amount: 100,
        size: 0
    },

] as ICard[];

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
const Overmoney: FC = () => {

    const [listItems, setListItems] = React.useState<IListItem[]>(constlistItems);

    const [showModalAddCategory, setShowModalAddCategory] = useState<boolean>(false);
    const handleCloseModalAddCategory = () => setShowModalAddCategory(false);
    const handleShowModalAddCategory = () => setShowModalAddCategory(true);

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
            <Container className='mt-5 pb-5'>
                <Row>
                    <Col sm={8}>
                        <GridCards cards={cards} />
                    </Col>
                    <Col sm={4}>
                        <ListCategories items={listItems} handleClickAddCategory={handleShowModalAddCategory} />
                    </Col>
                </Row>
            </Container>
            <PopupAddCategory 
                showModalAddCategory={showModalAddCategory} 
                handleCloseModalAddCategory={handleCloseModalAddCategory} 
                handleButtonSubmit={handleSubmitAddCategory}
            />
        </>
    );
};

export default Overmoney;