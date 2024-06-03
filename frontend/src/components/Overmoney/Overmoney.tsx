import React, { FC, useCallback, useEffect, useState } from 'react'
import { Button, Col, Container, Row, Toast } from 'react-bootstrap'
import GridCards from '../GridCards/GridCards'
import ListCategories from '../ListCategories/ListCategories'
import { ICard, IListItem } from '../../types/types'
import { constCards, constlistItems } from '../../utils/utils'


const Overmoney: FC = () => {

    const [cards, setCards] = useState<ICard[]>(constCards)
    const [listItems, setListItems] = useState<IListItem[]>(constlistItems)
    const [lastDeletedCard, setLastDeletedCard] = useState<ICard>({} as ICard)
    //TODO удалить при рифакторинге добавления suggestedCategoryId к карточке транзакции
    const [lastCategory, setLastCategory] = useState<string>("")
    const [showAddCardToast, setShowAddCardToast] = useState<boolean>(false)

    useEffect(() => {
        setCards(cards)
    }, [cards])

    const handleSubmitAddCard = useCallback((formData: ICard) => {
        //вызов API добавления категории 
        setCards([
            ...cards,
            {
                id: `${cards.length + 1}`,
                message: formData.message,
                amount: formData.amount,
            }
        ])
    }, [cards])

    const handleDeleteCard = useCallback((card: ICard, category: string) => {
        //вызов API удаления транзакции
        setLastDeletedCard(card)
        setLastCategory(category)
        setCards(cards.filter(item => item.id !== card.id))
        setShowAddCardToast(true)
    }, [cards])

    const handleSubmitAddCategory = useCallback((formData: IListItem) => {
        //вызов API добавления категории 
        setListItems([
            ...listItems,
            {
                name: formData.name,
                type: formData.type,
                keywords: [formData.name]
            }
        ])
    }, [listItems])

    const handleChangeCategory = (formData : IListItem) => {
        //вызов API изменения категории
        setListItems((listItems.map(item => item.id === formData.id ? formData : item)))
    }

    const onClickCancelDropCard = () => {
        handleSubmitAddCard(lastDeletedCard)
        setShowAddCardToast(false)
    }


    return (
        <Container className='mt-5 pb-5'>
            <Row>
                <Col sm={8}>
                    <GridCards 
                        cards={cards}
                        handleSubmitAddCard={handleSubmitAddCard}
                        />
                </Col>
                <Col sm={4}>
                    <ListCategories 
                        listItems={listItems} 
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