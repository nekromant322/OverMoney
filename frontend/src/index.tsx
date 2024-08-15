import React, { StrictMode } from 'react';
import { createRoot } from 'react-dom/client';
import App from './App';
import './index.css';

async function enableMocking() {
    if (process.env.NODE_ENV !== 'development') {
        return
    }
    const { worker } = await import('./mocks/browser')
    return worker.start()
}

const container = document.getElementById('root');
const root = createRoot(container!);

enableMocking().then(() => {
    root.render(
        <StrictMode>
            <App />
        </StrictMode>
    );
})