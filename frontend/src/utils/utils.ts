import { ICard, IListItem, IRowHistory } from "../types/types";

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

export const transactions: IRowHistory[] = [
        {
            id: "b110c552-f1eb-455e-bdf7-54c1045b1d78",
            categoryName: "Нераспознаное",
            message: "вода",
            amount: 100,
            date: "2024-05-29T22:50:35.883633",
            // "suggestedCategoryId": 432,
            // "telegramUserId": 307935244,
            telegramUserName: "NikoTiN_RnD"
        },
        {
            id: "1b2ad92a-a552-4bf4-b603-ab8abab57b05",
            categoryName: "Продукты",
            message: "вода",
            amount: 100,
            date: "2024-05-29T22:50:05.630252",
            // "suggestedCategoryId": 432,
            // "telegramUserId": 307935244,
            telegramUserName: "NikoTiN_RnD"
        },
        {
            id: "edf469ea-c0b1-4b67-8d83-c07e516504b0",
            categoryName: "Нераспознаное",
            message: "PG",
            amount: 50000,
            date: "2024-04-27T10:34:11.493071",
            // "suggestedCategoryId": 431,
            // "telegramUserId": 307935244,
            telegramUserName: "NikoTiN_RnD"
        },
        {
            id: "310c6525-297d-49ce-9a5d-60dac115c640",
            categoryName: "Рестораны",
            message: "gjgsn",
            amount: 100,
            date: "2024-04-27T10:33:25.796361",
            // "suggestedCategoryId": 431,
            // "telegramUserId": 307935244,
            telegramUserName: "NikoTiN_RnD"
        },
        {
            id: "14cd53bf-37a1-4ee2-ade9-bacff6364665",
            categoryName: "Нераспознаное",
            message: "сон",
            amount: 50,
            date: "2024-04-27T10:29:06.763435",
            // "suggestedCategoryId": 434,
            // "telegramUserId": 307935244,
            telegramUserName: "NikoTiN_RnD"
        },
        {
            id: "ef367e34-8adc-4e8e-9067-5c8cd1526d91",
            categoryName: "Рестораны",
            message: "еда",
            amount: 300,
            date: "2024-04-26T22:10:06.062529",
            // "suggestedCategoryId": 432,
            // "telegramUserId": 307935244,
            telegramUserName: "NikoTiN_RnD"
        },
        {
            id: "9aa660f7-8ed0-4c6d-bdd2-f8e8baa73822",
            categoryName: "Рестораны",
            message: "cat",
            amount: 100,
            date: "2024-04-19T20:07:19.621413",
            // "suggestedCategoryId": 431,
            // "telegramUserId": 307935244,
            telegramUserName: "NikoTiN_RnD"
        },
        {
            id: "a65490eb-5a19-486e-81c0-91e21e81536e",
            categoryName: "Продукты",
            message: "вода",
            amount: 100,
            date: "2024-04-19T20:06:42.288489",
            // "suggestedCategoryId": 432,
            // "telegramUserId": 307935244,
            telegramUserName: "NikoTiN_RnD"
        },
        {
            id: "ffe4bcea-be4e-4242-b0d3-9ef7b7dbff95",
            categoryName: "Продукты",
            message: "вода",
            amount: 100,
            date: "2024-04-19T19:50:02.730589",
            // "suggestedCategoryId": 432,
            // "telegramUserId": 307935244,
            telegramUserName: "NikoTiN_RnD"
        },
        {
            id: "b110c552-f1eb-455e-bdf7-54c1045b1d78",
            categoryName: "Нераспознаное",
            message: "вода",
            amount: 100,
            date: "2024-05-29T22:50:35.883633",
            // "suggestedCategoryId": 432,
            // "telegramUserId": 307935244,
            telegramUserName: "NikoTiN_RnD"
        },
        {
            id: "1b2ad92a-a552-4bf4-b603-ab8abab57b05",
            categoryName: "Продукты",
            message: "вода",
            amount: 100,
            date: "2024-05-29T22:50:05.630252",
            // "suggestedCategoryId": 432,
            // "telegramUserId": 307935244,
            telegramUserName: "NikoTiN_RnD"
        },
        {
            id: "edf469ea-c0b1-4b67-8d83-c07e516504b0",
            categoryName: "Нераспознаное",
            message: "PG",
            amount: 50000,
            date: "2024-04-27T10:34:11.493071",
            // "suggestedCategoryId": 431,
            // "telegramUserId": 307935244,
            telegramUserName: "NikoTiN_RnD"
        },
        {
            id: "310c6525-297d-49ce-9a5d-60dac115c640",
            categoryName: "Рестораны",
            message: "gjgsn",
            amount: 100,
            date: "2024-04-27T10:33:25.796361",
            // "suggestedCategoryId": 431,
            // "telegramUserId": 307935244,
            telegramUserName: "NikoTiN_RnD"
        },
        {
            id: "14cd53bf-37a1-4ee2-ade9-bacff6364665",
            categoryName: "Нераспознаное",
            message: "сон",
            amount: 50,
            date: "2024-04-27T10:29:06.763435",
            // "suggestedCategoryId": 434,
            // "telegramUserId": 307935244,
            telegramUserName: "NikoTiN_RnD"
        },
        {
            id: "ef367e34-8adc-4e8e-9067-5c8cd1526d91",
            categoryName: "Рестораны",
            message: "еда",
            amount: 300,
            date: "2024-04-26T22:10:06.062529",
            // "suggestedCategoryId": 432,
            // "telegramUserId": 307935244,
            telegramUserName: "NikoTiN_RnD"
        },
        {
            id: "9aa660f7-8ed0-4c6d-bdd2-f8e8baa73822",
            categoryName: "Рестораны",
            message: "cat",
            amount: 100,
            date: "2024-04-19T20:07:19.621413",
            // "suggestedCategoryId": 431,
            // "telegramUserId": 307935244,
            telegramUserName: "NikoTiN_RnD"
        },
        {
            id: "a65490eb-5a19-486e-81c0-91e21e81536e",
            categoryName: "Продукты",
            message: "вода",
            amount: 100,
            date: "2024-04-19T20:06:42.288489",
            // "suggestedCategoryId": 432,
            // "telegramUserId": 307935244,
            telegramUserName: "NikoTiN_RnD"
        },
        {
            id: "ffe4bcea-be4e-4242-b0d3-9ef7b7dbff95",
            categoryName: "Продукты",
            message: "вода",
            amount: 100,
            date: "2024-04-19T19:50:02.730589",
            // "suggestedCategoryId": 432,
            // "telegramUserId": 307935244,
            telegramUserName: "NikoTiN_RnD"
        },
        
    ]