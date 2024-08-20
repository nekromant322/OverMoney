import React, { FC, useContext, useEffect, useRef, useState } from 'react';
import { AgGridReact } from '@ag-grid-community/react'; 
// import { transactions } from '../utils/utils';
import { TransactionsContext } from '../context/TransactionsContext';
import { Button, Container } from 'react-bootstrap';
import { CellClickedEvent, ColDef } from '@ag-grid-community/core';
import { ModuleRegistry } from '@ag-grid-community/core';
import { ClientSideRowModelModule } from '@ag-grid-community/client-side-row-model';
import PopupChangeTransaction from '../components/PopupChangeTransaction';
import { ITransaction } from '../types/types';
import axios from 'axios';
// import axios from 'axios';

ModuleRegistry.registerModules([ ClientSideRowModelModule ]);


const History: FC = () => {

    const gridRef = useRef<AgGridReact>(null);
    const transactions = useContext(TransactionsContext)

    const [rowData, setRowData] = useState(transactions);
    const [showModalChangeTransaction, setShowModalChangeTransaction] = useState(false);
    const handleCloseModalChangeTransaction = () => setShowModalChangeTransaction(false);
    const [selectedRowEdit, setSelectedRowEdit] = useState<ITransaction>({} as ITransaction);
    

    const handleShowModalChangeTransaction = (event: CellClickedEvent) => {
        console.log(event.data);
        setSelectedRowEdit(event.data);
        setShowModalChangeTransaction(true);
    }

    const [colDefs] = useState<(ColDef<any, any>)[]>([
        { field: "categoryName", headerName: "Категория", flex: 4, headerCheckboxSelection: true, checkboxSelection: true, showDisabledCheckboxes: true, },
        { field: "amount", headerName: "Сумма", width: 100, flex: 2 },
        { field: "message", headerName: "Примечание", flex: 3 },
        { field: "date", headerName: "Дата", flex: 3, valueFormatter: (params) => new Date(params.value).toLocaleString('ru-RU') },
        { field: "telegramUserName", headerName: "Пользователь", flex: 2 },
        { field: "edit", headerName: "", width: 55, 
        cellRenderer: () => <Button variant="outline-light"><i className="bi bi-pencil"></i></Button>, 
        cellStyle: {textAlign: 'center'},
        onCellClicked: handleShowModalChangeTransaction
        },
    ])

    useEffect(() => {
        //вызов API получения списка транзакции
        axios.get('/transactions/history?pageSize=50&pageNumber=0')
        .then(response => {
            setRowData(response.data);
        })
        .catch(error => {
            console.error(error);
        });
    }, [])

    const handleModalSaveButton = (formData: ITransaction) => {
        //вызов API изменения транзакции
        setRowData(rowData?.map(item => item.id === formData.id ? formData : item))
        setShowModalChangeTransaction(false)
    }

    const handleDeleteTransaction = () => {
        // let tempRows : ITransaction[] = [];
        //вызов API удаления транзакции
        gridRef.current?.api.getSelectedRows().forEach(el => {
            // console.log(el);
            setRowData((rowData) => rowData.filter(item => item.id !== el.id))
            // console.log(tempRows);
        })
        // setRowData(tempRows)
    }


    return (
        <Container className="history pb-3 d-flex flex-column h-100" >
            <Container className="toolbar p-2 d-flex justify-content-end">
                <Button variant="success"><i className="bi bi-plus-square" style={{fontSize: '26px'}}></i></Button>
                <Button onClick={handleDeleteTransaction} className="ms-3" variant="danger"><i className="bi bi-trash3" style={{fontSize: '26px'}}></i></Button>
            </Container>
            <Container className="ag-theme-material-dark h-100" >
                <AgGridReact
                    ref={gridRef}
                    rowSelection={"multiple"}
                    rowData={rowData}
                    columnDefs={colDefs}
                />
            </Container>  
        <PopupChangeTransaction
            showModalChangeTransaction={showModalChangeTransaction}
            handleCloseModalChangeTransaction={handleCloseModalChangeTransaction}
            item={selectedRowEdit}
            handleButtonSave={handleModalSaveButton}
        />

        </Container>
    );
};

export default History;