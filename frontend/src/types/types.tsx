export interface ICard {
    id: number;
    title: string;
    amount: number;
    size: number;
}

export interface IListItem {
    id: number;
    title: string;
    subtype: "income" | "expense";
    keywords: string[];
}