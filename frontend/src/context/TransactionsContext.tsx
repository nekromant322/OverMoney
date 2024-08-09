import React from 'react';
import { ITransaction } from '../types/types';

export const TransactionsContext = React.createContext([] as ITransaction[]);