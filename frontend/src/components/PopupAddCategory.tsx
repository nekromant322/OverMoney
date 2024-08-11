import React, { FC, useState } from 'react';
import { Form } from 'react-bootstrap';
import Popup from './Popup';
import { IListItem } from '../types/types';

interface PopupAddCategoryProps {
    showModalAddCategory: boolean,
    handleCloseModalAddCategory: () => void,
    handleButtonSubmit: (formData: IListItem) => void
}
const PopupAddCategory: FC<PopupAddCategoryProps>  = ({showModalAddCategory, handleCloseModalAddCategory, handleButtonSubmit}) => {

    const defaultFormData : IListItem = {name: '', type: 'EXPENSE', keywords: []};
    
    const [formData, setFormData] = useState<IListItem>(defaultFormData);
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
        show={showModalAddCategory} 
        handleClose={handleCloseModalAddCategory} 
        onSubmit={onSubmit}
        title="Добавить категорию" 
        titleButton="Добавить">
            <Form>
                <Form.Group className="mb-3" controlId="category">
                    <Form.Label>Новая категория:</Form.Label>
                    <Form.Control
                        onChange={onChange}
                        type="text"
                        placeholder='Введите название категории'
                        autoFocus
                        required
                        name='name'
                    />
                </Form.Group>
                <Form.Group className="mb-3" controlId="type-category">
                    <Form.Label>Выберите тип категории:</Form.Label>
                        <Form.Check 
                            onChange={onChange}
                            defaultChecked
                            type={"radio"}
                            id={"expense-radio"}
                            label={"Расходы"}
                            name="type"
                            value="EXPENSE"
                        />
                        <Form.Check 
                            onChange={onChange}
                            type={"radio"}
                            id={"income-radio"}
                            label={"Доходы"}
                            name="type"
                            value="INCOME"
                        />
                </Form.Group>
            </Form>
        </Popup>
    );
};

export default PopupAddCategory;