import { ICard, IListItem, ITransaction } from "../types/types";

export const constlistItems: IListItem[] = [
    {
        id: '1',
        name: 'Категория 1',
        type: "INCOME",
        keywords: ['Ключевое слово 1', 'Ключевое слово 2', 'Ключевое слово 3'],
    },
    {
        id: '3',
        name: 'Категория 3',
        type: "INCOME",
        keywords: ['Ключевое слово 1', 'Ключевое слово 2', 'Ключевое слово 3'],
    },
    {
        id: '2',
        name: 'Категория 2',
        type: "EXPENSE",
        keywords: ['Ключевое слово 1', 'Ключевое слово 2', 'Ключевое слово 3'],
    },
    {
        id: '4',
        name: 'Категория 4',
        type: "EXPENSE",
        keywords: ['Ключевое слово 1', 'Ключевое слово 2', 'Ключевое слово 3'],
    },

];

export const constCards: ICard[] = [
    {
        id: '1',
        message: 'Карта 1sddsagagsgdsads',
        amount: 1000,
    },
    {
        id: '2',
        message: 'Карта 2',
        amount: 800,
    },
    {
        id: '3',
        message: 'Карта 3',
        amount: 600,
    },
    {
        id: '4',
        message: 'Карта 4',
        amount: 400,
    },
    {
        id: '5',
        message: 'Карта 5',
        amount: 200,
    },
    {
        id: '6',
        message: 'Карта 6',
        amount: 100,
    },

];

export const transactions: ITransaction[] = [
        {
            id: "1",
            categoryName: "Нераспознаное",
            message: "вода",
            amount: 100,
            date: "2024-05-29T22:50:35.883633",
            // "suggestedCategoryId": 432,
            // "telegramUserId": 307935244,
            telegramUserName: "NikoTiN_RnD"
        },
        {
            id: "2",
            categoryName: "Продукты",
            message: "вода",
            amount: 100,
            date: "2024-05-29T22:50:05.630252",
            // "suggestedCategoryId": 432,
            // "telegramUserId": 307935244,
            telegramUserName: "NikoTiN_RnD"
        },
        {
            id: "3",
            categoryName: "Нераспознаное",
            message: "PG",
            amount: 50000,
            date: "2024-04-27T10:34:11.493071",
            // "suggestedCategoryId": 431,
            // "telegramUserId": 307935244,
            telegramUserName: "NikoTiN_RnD"
        },
        {
            id: "4",
            categoryName: "Рестораны",
            message: "gjgsn",
            amount: 100,
            date: "2024-04-27T10:33:25.796361",
            // "suggestedCategoryId": 431,
            // "telegramUserId": 307935244,
            telegramUserName: "NikoTiN_RnD"
        },
        {
            id: "5",
            categoryName: "Нераспознаное",
            message: "сон",
            amount: 50,
            date: "2024-04-27T10:29:06.763435",
            // "suggestedCategoryId": 434,
            // "telegramUserId": 307935244,
            telegramUserName: "NikoTiN_RnD"
        },
        {
            id: "6",
            categoryName: "Рестораны",
            message: "еда",
            amount: 300,
            date: "2024-04-26T22:10:06.062529",
            // "suggestedCategoryId": 432,
            // "telegramUserId": 307935244,
            telegramUserName: "NikoTiN_RnD"
        },
        {
            id: "7",
            categoryName: "Рестораны",
            message: "cat",
            amount: 100,
            date: "2024-04-19T20:07:19.621413",
            // "suggestedCategoryId": 431,
            // "telegramUserId": 307935244,
            telegramUserName: "NikoTiN_RnD"
        },
        {
            id: "8",
            categoryName: "Продукты",
            message: "вода",
            amount: 100,
            date: "2024-04-19T20:06:42.288489",
            // "suggestedCategoryId": 432,
            // "telegramUserId": 307935244,
            telegramUserName: "NikoTiN_RnD"
        },
        {
            id: "9",
            categoryName: "Продукты",
            message: "вода",
            amount: 100,
            date: "2024-04-19T19:50:02.730589",
            // "suggestedCategoryId": 432,
            // "telegramUserId": 307935244,
            telegramUserName: "NikoTiN_RnD"
        },
        {
            id: "10",
            categoryName: "Нераспознаное",
            message: "вода",
            amount: 100,
            date: "2024-05-29T22:50:35.883633",
            // "suggestedCategoryId": 432,
            // "telegramUserId": 307935244,
            telegramUserName: "NikoTiN_RnD"
        },
        {
            id: "11",
            categoryName: "Продукты",
            message: "вода",
            amount: 100,
            date: "2024-05-29T22:50:05.630252",
            // "suggestedCategoryId": 432,
            // "telegramUserId": 307935244,
            telegramUserName: "NikoTiN_RnD"
        },
        {
            id: "12",
            categoryName: "Нераспознаное",
            message: "PG",
            amount: 50000,
            date: "2024-04-27T10:34:11.493071",
            // "suggestedCategoryId": 431,
            // "telegramUserId": 307935244,
            telegramUserName: "NikoTiN_RnD"
        },
        {
            id: "13",
            categoryName: "Рестораны",
            message: "gjgsn",
            amount: 100,
            date: "2024-04-27T10:33:25.796361",
            // "suggestedCategoryId": 431,
            // "telegramUserId": 307935244,
            telegramUserName: "NikoTiN_RnD"
        },
        {
            id: "14",
            categoryName: "Нераспознаное",
            message: "сон",
            amount: 50,
            date: "2024-04-27T10:29:06.763435",
            // "suggestedCategoryId": 434,
            // "telegramUserId": 307935244,
            telegramUserName: "NikoTiN_RnD"
        },
        {
            id: "15",
            categoryName: "Рестораны",
            message: "еда",
            amount: 300,
            date: "2024-04-26T22:10:06.062529",
            // "suggestedCategoryId": 432,
            // "telegramUserId": 307935244,
            telegramUserName: "NikoTiN_RnD"
        },
        {
            id: "16",
            categoryName: "Рестораны",
            message: "cat",
            amount: 100,
            date: "2024-04-19T20:07:19.621413",
            // "suggestedCategoryId": 431,
            // "telegramUserId": 307935244,
            telegramUserName: "NikoTiN_RnD"
        },
        {
            id: "17",
            categoryName: "Продукты",
            message: "вода",
            amount: 100,
            date: "2024-04-19T20:06:42.288489",
            // "suggestedCategoryId": 432,
            // "telegramUserId": 307935244,
            telegramUserName: "NikoTiN_RnD"
        },
        {
            id: "18",
            categoryName: "Продукты",
            message: "вода",
            amount: 100,
            date: "2024-04-19T19:50:02.730589",
            // "suggestedCategoryId": 432,
            // "telegramUserId": 307935244,
            telegramUserName: "NikoTiN_RnD"
        },
        
    ]