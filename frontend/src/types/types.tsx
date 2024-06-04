export interface ICard {
    id?: string;
    message: string;
    amount: number;
    size?: number;
}

export interface IListItem {
    id?: string;
    name: string;
    type: "INCOME" | "EXPENSE";
    keywords: string[];
}

export interface ITransaction {
    id: string;
    amount: number; 
    categoryName: string;
    date: string;
    message: string;
    telegramUserName: string;
}