import React from 'react';
import './App.css';
import {DragableTransaction} from "./components/DragableTransaction";


function App() {
    return (
        <>
            <DragableTransaction message={"test"} sum={123}></DragableTransaction>
        </>
    );
}

export default App;
