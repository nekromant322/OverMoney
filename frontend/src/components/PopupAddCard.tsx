import React, { FC, useState } from 'react';
import { ICard } from '../types/types';
import Popup from './Popup';
import { Form } from 'react-bootstrap';

interface PopupAddCardProps {
    showModalAddCard: boolean,
    handleCloseModalAddCard: () => void,
    handleButtonSubmit: (formData: ICard) => void
}

const PopupAddCard : FC<PopupAddCardProps> = ({showModalAddCard, handleCloseModalAddCard, handleButtonSubmit}) => {

    const defaultFormData : ICard = {message: '', amount: 0, size: 0}
    const [formData, setFormData] = useState<ICard>(defaultFormData);

    const onChange = (event: React.ChangeEvent<HTMLInputElement>) => {
        setFormData({
            ...formData,
            [event.target.name]: event.target.value
        });
    };

    const onSubmit: React.MouseEventHandler<HTMLButtonElement> = (event) => {
        event.preventDefault();
        console.log(formData);
        handleButtonSubmit(formData);
        setFormData(defaultFormData);
    };

    return (
        <Popup 
            title='Добавить транзакцию'
            titleButton='Добавить'
            show={showModalAddCard}
            handleClose={handleCloseModalAddCard}
            onSubmit={onSubmit}
        >
            <Form>
                <Form.Group className="mb-3" controlId="transaction">
                    <Form.Label>Новая транзакция:</Form.Label>
                    <Form.Control
                        onChange={onChange}
                        type="text"
                        placeholder='Введите название транзакции'
                        autoFocus
                        required
                        name='message'
                    />
                </Form.Group>
                <Form.Group className="mb-3" controlId="amount">
                    <Form.Label>Сумма:</Form.Label>
                    <Form.Control 
                        onChange={onChange}
                        type="number"
                        placeholder='Введите сумму'
                        required
                        name='amount'
                    />
                </Form.Group>
            </Form>
        </Popup>
    );
};

export default PopupAddCard;