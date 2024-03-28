import type {ReactNode} from 'react';
import React from "react";
import Draggable, {DraggableCore} from "react-draggable";
//https://www.copycat.dev/blog/react-draggable/

type IDragableTransactionProps = {
    message: ReactNode;
    sum: ReactNode;
};


function DragableTransaction(props: IDragableTransactionProps) {
    const onDragEvent = (e: { type: any; }, data: any) => {
        //change color
        console.log('Event Type', e.type);
        console.log({e, data});
    }
    const onDragFinishedEvent = (e: { type: any; }, data: any) => {
        //commit transaction
        console.log('aaaaaa ', e.type);
        console.log({e, data});
    }


    // @ts-ignore
    return (<Draggable defaultPosition={{x: 0, y: 0}} onDrag={onDragEvent} onStop={onDragFinishedEvent}>
        <div className="dragableTransaction">
            <p>{props.message}</p>
            <p>{props.sum}</p>
        </div>
    </Draggable>)

}

export {DragableTransaction};