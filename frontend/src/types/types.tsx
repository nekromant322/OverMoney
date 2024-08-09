export interface ICard {
    id?: string;
    message: string;
    amount: number;
    size?: number;
}

export interface ICategory {
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

export interface IUser {
    id: number,
    first_name: string,
    username: string,
    photo_url: string,
    auth_date: number,
    hash: string,
}