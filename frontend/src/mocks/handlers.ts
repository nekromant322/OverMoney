import { http, HttpResponse } from 'msw'
import { ICard, ICategory, ITransaction } from '../types/types'

const state = {
    categories: [
        {
            id: '1',
            name: 'Категория 1',
            type: "INCOME",
            keywords: ['Ключевое слово 11', 'Ключевое слово 12', 'Ключевое слово 13'],
        },
        {
            id: '3',
            name: 'Категория 3',
            type: "INCOME",
            keywords: ['Ключевое слово 31', 'Ключевое слово 32', 'Ключевое слово 33'],
        },
        {
            id: '2',
            name: 'Категория 2',
            type: "EXPENSE",
            keywords: ['Ключевое слово 21', 'Ключевое слово 22', 'Ключевое слово 23'],
        },
        {
            id: '4',
            name: 'Категория 4',
            type: "EXPENSE",
            keywords: ['Ключевое слово 41', 'Ключевое слово 42', 'Ключевое слово 43'],
        },
        {
            id: '5',
            name: 'Категория 5',
            type: "EXPENSE",
            keywords: ['Ключевое слово 51', 'Ключевое слово 52', 'Ключевое слово 53'],
        },
        {
            id: '6',
            name: 'Категория 6',
            type: "EXPENSE",
            keywords: ['Ключевое слово 61', 'Ключевое слово 62', 'Ключевое слово 63'],
        },
        {
            id: '7',
            name: 'Категория 7',
            type: "EXPENSE",
            keywords: ['Ключевое слово 71', 'Ключевое слово 72', 'Ключевое слово 73'],
        },
    ] as ICategory[],
    transactions: [
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
        }
    ] as ICard[],
    history: [
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
        }
    ] as ITransaction[]
}

const botNameHandler = http.get("/login/bot-login", () => {
    return HttpResponse.text("overmoney-bot")
})

const loginHandler = http.post("/auth/login", () => {
    return new HttpResponse('login success', {
        status: 200,
        headers: {
            'Set-Cookie': 'accessToken=qwerty123'
        }
    })
})

const getCategoriesHandler = http.get("/categories", ({ cookies }) => {
    if (cookies.accessToken === 'qwerty123') {
        return HttpResponse.json(state.categories)
    }
    return HttpResponse.text('login failed', {
        status: 401
    })
} )

const addCategoryHandler = http.post("/categories", async ({ request, cookies }) => {
    if (cookies == null || cookies.accessToken !== 'qwerty123') {
        return HttpResponse.text('login failed', {
            status: 401
        })
    }

    const body = await request.json();
    if (body == null || typeof body !== 'object') {
        return HttpResponse.text('invalid request', {
            status: 400
        })
    }

    const newCategory = body as ICategory;
    if (newCategory == null || newCategory.name == null || newCategory.id == null) {
        return HttpResponse.text('invalid request', {
            status: 400
        })
    }

    state.categories = [...state.categories, newCategory]
    return HttpResponse.json(state.categories)
})

const getTransactionsHandler = http.get("/transactions", ({ cookies}) => {
    if (cookies.accessToken === 'qwerty123') {
        return HttpResponse.json(state.transactions)
    }
    return HttpResponse.text('login failed', {
        status: 401
    })
})

const addTransactionHandler = http.post("/transactions", async ({ request, cookies }) => {
    if (cookies == null || cookies.accessToken !== 'qwerty123') {
        return HttpResponse.text('login failed', {
            status: 401
        })
    }

    const body = await request.json();
    if (body == null || typeof body !== 'object') {
        return HttpResponse.text('invalid request', {
            status: 400
        })
    }

    const newTransaction = body as ICard;
    if (newTransaction == null || newTransaction.message == null || newTransaction.amount == null) {
        return HttpResponse.text('invalid request', {
            status: 400
        })
    }

    state.transactions = [...state.transactions, {
        id: String(state.transactions.length + 1) , 
        amount: newTransaction.amount, 
        message: newTransaction.message, 
    }] as ICard[]
    return HttpResponse.json(state.transactions)
})

const getHistoryHandler = http.get("/transactions/history", ({ request, cookies }) => {
    if (cookies.accessToken === 'qwerty123') {
        // const url = new URL(request.url)

        // const pageSize = Number.parseInt(url.searchParams.get('pageSize') || "0")
        // const pageNumber = Number.parseInt(url.searchParams.get('pageNumber') || "0")
        // if (pageSize && pageNumber) {
            // return HttpResponse.json(state.history.slice((pageNumber - 1) * pageSize, pageNumber * pageSize))
            return HttpResponse.json(state.history)
        // }
    }
    return HttpResponse.text('login failed', {
        status: 401
    })
})



export const handlers = [ 
    botNameHandler, 
    loginHandler, 
    getCategoriesHandler, 
    addCategoryHandler, 
    getTransactionsHandler,
    addTransactionHandler,
    getHistoryHandler]