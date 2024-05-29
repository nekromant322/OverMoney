import React, { FC, useEffect, useState } from 'react';
import { AgGridReact } from '@ag-grid-community/react'; 
import "@ag-grid-community/styles/ag-grid.css"; // Core CSS
import "@ag-grid-community/styles/ag-theme-material.css"; // Theme
import { transactions } from '../../utils/utils';
import { CloseButton } from 'react-bootstrap';
import { ColDef } from '@ag-grid-community/core';
import { ModuleRegistry } from '@ag-grid-community/core';
import { ClientSideRowModelModule } from '@ag-grid-community/client-side-row-model';

ModuleRegistry.registerModules([ ClientSideRowModelModule ]);


const History: FC = () => {

    const [rowData, setRowData] = useState(transactions);
    const [colDefs, setColDefs] = useState<(ColDef<any, any>)[]>([
        { field: "categoryName", headerName: "Категория", flex: 2 },
        { field: "amount", headerName: "Сумма", width: 100, flex: 1 },
        { field: "message", headerName: "Примечание", flex: 3 },
        { field: "date", headerName: "Дата", flex: 3, valueFormatter: (params) => new Date(params.value).toLocaleDateString() },
        { field: "telegramUserName", headerName: "Пользователь", flex: 2 },
        { field: "", cellRenderer: () => <CloseButton variant='white'/>, flex: 1 }
    ])

    useEffect(() => {
        setRowData(rowData)
    }, [rowData])



    return (
        <div className="ag-theme-material-dark" style={{ width: '100%', height: '85dvh' }}>
            <AgGridReact
                rowData={rowData}
                columnDefs={colDefs}
            />
        </div>
    );
};

export default History;