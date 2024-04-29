export interface ICard {
    id: string;
    message: string;
    amount: number;
    size: number;
}

export interface IListItem {
    id: number;
    name: string;
    type: "INCOME" | "EXPENSE";
    keywords: string[];
}