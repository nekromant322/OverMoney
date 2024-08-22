import React, { FC, useCallback, useContext, useEffect, useState } from 'react'
import { Button, Col, Container, Row, Toast } from 'react-bootstrap'
import GridCards from '../components/GridCards'
import ListCategories from '../components/ListCategories'
import { ICard, ICategory } from '../types/types'
import { CategoriesContext } from '../context/CategoriesContext'
import { TransactionsContext } from '../context/TransactionsContext'
import axios from 'axios'
// import { constCards, constlistItems } from '../utils/utils'


const Overmoney: FC = () => {
    const categories = useContext(CategoriesContext);
    const transactions = useContext(TransactionsContext);
    const [cards, setCards] = useState<ICard[]>(transactions)
    const [listItems, setListItems] = useState<ICategory[]>(categories)
    const [isTwoCollumns, setIsTwoColumns] = useState<boolean>(false)
    const [lastDeletedCard, setLastDeletedCard] = useState<ICard>({} as ICard)
    //TODO удалить при рифакторинге добавления suggestedCategoryId к карточке транзакции
    const [lastCategory, setLastCategory] = useState<string>("")
    const [showAddCardToast, setShowAddCardToast] = useState<boolean>(false)

    useEffect(() => {
        setCards(cards)
        setListItems(listItems)
        if (listItems.length > 12) {
            setIsTwoColumns(true)
        }
    }, [cards, listItems])

    const handleSubmitAddCard = useCallback((formData: ICard) => {
        //вызов API добавления категории 
        axios.post('/transactions', formData)
            .then(respons => {
                setCards(respons.data)
            })
        //     setCards([
        //     ...cards,
        //     {
        //         id: `${cards.length + 1}`,
        //         message: formData.message,
        //         amount: formData.amount,
        //     }
        // ])
    }, [cards])

    const handleDeleteCard = useCallback((card: ICard, category: string) => {
        //вызов API удаления транзакции
        setLastDeletedCard(card)
        setLastCategory(category)
        setCards(cards.filter(item => item.id !== card.id))
        setShowAddCardToast(true)
    }, [cards])

    const handleSubmitAddCategory = useCallback((formData: ICategory) => {
        //вызов API добавления категории 
        setListItems([
            ...listItems,
            {
                id: `${listItems.length + 1}`,
                name: formData.name,
                type: formData.type,
                keywords: [formData.name]
            }
        ])
    }, [listItems])

    const handleChangeCategory = (formData : ICategory) => {
        //вызов API изменения категории
        setListItems((listItems.map(item => item.id === formData.id ? formData : item)))
    }

    const onClickCancelDropCard = () => {
        handleSubmitAddCard(lastDeletedCard)
        setShowAddCardToast(false)
    }


    return (
        <Container className='mt-5 pb-1 h-100 d-flex'>
            <Row>
                <Col sm={isTwoCollumns ? 7 : 8}>
                    <GridCards 
                        cards={cards}
                        handleSubmitAddCard={handleSubmitAddCard}
                        />
                </Col>
                <Col sm={isTwoCollumns ? 5 : 4}>
                    <ListCategories 
                        listItems={listItems} 
                        isTwoCollumns={isTwoCollumns}
                        handleSubmitAddCategory={handleSubmitAddCategory}
                        handleDropCard={handleDeleteCard}
                        handleChangeCategory={handleChangeCategory}
                    />
                </Col>
            </Row>
            <Toast bg='dark' onClose={() => setShowAddCardToast(false)} show={showAddCardToast} delay={3000} autohide>
                <Toast.Header closeVariant='white' >
                    <strong className="me-auto">Успешно!</strong>
                    <Button variant='outline-danger' onClick={onClickCancelDropCard}>Отменить</Button>
                </Toast.Header>
                <Toast.Body>Транзакция {`${lastDeletedCard.message} ${lastDeletedCard.amount}`} была успешно добавлена в категорию {lastCategory}</Toast.Body>
            </Toast>
        </Container>
    )
}

export default Overmoney