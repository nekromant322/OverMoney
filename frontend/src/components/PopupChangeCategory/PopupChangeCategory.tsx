import React, { FC, useState } from 'react';
import { Badge, CloseButton, Form } from 'react-bootstrap';
import Popup from '../Popup/Popup';
import { IListItem } from '../../types/types';

interface PopupChangeCategoryProps {
    item: IListItem,
    showModalChangeCategory: boolean,
    handleCloseModalChangeCategory: () => void,
    handleButtonSave: (formData: IListItem) => void
}
const PopupChangeCategory: FC<PopupChangeCategoryProps>  = ({item, showModalChangeCategory, handleCloseModalChangeCategory, handleButtonSave}) => {

    // const itemCategory : IListItem = {id: item.id, name: item.name, type: item.type, keywords: item.keywords};
    const [formData, setFormData] = useState<IListItem>(item)
    const [keywords, setKeywords] = useState<string[]>(item.keywords)
    const onChange = (event: React.ChangeEvent<HTMLInputElement>) => {
        setFormData({
            ...formData,
            [event.target.name]: event.target.value
        });
    };

    const onSubmit: React.MouseEventHandler<HTMLButtonElement> = (event) => {
        event.preventDefault()
        console.log(formData)
        formData.keywords = keywords
        // setKeywords(formData.keywords)
        handleButtonSave(formData)
        // setFormData(defaultFormData);
    };

    return (
        <Popup 
        show={showModalChangeCategory} 
        handleClose={handleCloseModalChangeCategory} 
        onSubmit={onSubmit}
        title="Информация о категории" 
        titleButton="Сохранить">
            <Form>
                <Form.Group className="mb-3" controlId="category">
                    <Form.Label>Название категории:</Form.Label>
                    <Form.Control
                        onChange={onChange}
                        type="text"
                        placeholder='Введите название категории'
                        required
                        name='name'
                        value={formData.name}
                    />
                </Form.Group>
                <Form.Group className="mb-3" controlId="type-category">
                    <Form.Label>Тип категории:</Form.Label>
                        <Form.Check 
                            onChange={onChange}
                            checked={formData.type === 'EXPENSE'}
                            type={"radio"}
                            id={"expense-radio"}
                            label={"Расходы"}
                            name="type"
                            value="EXPENSE"
                        />
                        <Form.Check 
                            onChange={onChange}
                            checked={formData.type === 'INCOME'}
                            type={"radio"}
                            id={"income-radio"}
                            label={"Доходы"}
                            name="type"
                            value="INCOME"
                        />
                </Form.Group>
                <Form.Group className="mb-3" >
                    <Form.Label>Ключевые слова:</Form.Label>
                </Form.Group>
                <Form.Group>
                    {keywords.map((keyword: string, index: number) => (
                            <Badge  key={index} bg="secondary" className="me-2 mb-3 pe-1">
                                {keyword}
                                <CloseButton 
                                    onClick={() => setKeywords(keywords.filter((elem: string) => elem !== keyword))} 
                                    className='ms-2'
                                    style={{outline: 'none'}}
                                    />
                            </Badge>
                    ))}
                </Form.Group>
            </Form>
        </Popup>
    );
};

export default PopupChangeCategory;