export interface ICard {
    id: number;
    title: string;
    money: number;
    size: number;
}

export interface IListItem {
    id: number;
    title: string;
    subtype: "income" | "expense";
}