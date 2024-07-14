import React, { FC, useEffect, useState } from 'react';
import { ITransaction } from '../types/types';
import Popup from './Popup';
import { Form } from 'react-bootstrap';
import { constlistItems } from '../utils/utils';

interface PopupChangeTransactionProps {
    showModalChangeTransaction: boolean,
    handleCloseModalChangeTransaction: () => void,
    item: ITransaction,
    handleButtonSave: (formData: ITransaction) => void,

}

const PopupChangeTransaction: FC<PopupChangeTransactionProps> = ({showModalChangeTransaction, handleCloseModalChangeTransaction, item, handleButtonSave}) => {

    const [formData, setFormData] = useState<ITransaction>({} as ITransaction);

    useEffect(() => {
        setFormData(item)
    }, [item]);

    const onChange = (event: React.ChangeEvent<HTMLInputElement>) => {
        if (event.target.name === 'amount') {
            setFormData({
                ...formData,
                [event.target.name]: Number(event.target.value)
            });
        } else {
        setFormData({
            ...formData,
            [event.target.name]: event.target.value
        });
        }
    };

    const onSubmit: React.MouseEventHandler<HTMLButtonElement> = (event) => {
        event.preventDefault();
        console.log(formData);
        handleButtonSave(formData);
    };

    return (
        <Popup
            title='Редактировать транзакцию'
            titleButton='Сохранить'
            show={showModalChangeTransaction}
            handleClose={handleCloseModalChangeTransaction}
            onSubmit={onSubmit}
        >
            <Form>
                <Form.Group className="mb-3" controlId="category">
                    <Form.Label>Категория:</Form.Label>
                    <Form.Select aria-label="Категория" defaultValue={formData.categoryName}>
                        {constlistItems.map((el) => (
                            <option key={el.id} value={el.id} >{el.name}</option>
                        ))}
                    </Form.Select>
                </Form.Group>
                <Form.Group className="mb-3" controlId="message">
                    <Form.Label>Примечание:</Form.Label>
                    <Form.Control
                        onChange={onChange}
                        type="text"
                        placeholder='Введите примечание'
                        name='message'
                        value={formData.message}
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
                        value={formData.amount}
                    />
                </Form.Group>
            </Form>
        </Popup>
    );
};

export default PopupChangeTransaction;