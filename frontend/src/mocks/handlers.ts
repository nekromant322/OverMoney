import { http, HttpResponse } from 'msw';

const postsResolver = () => {
    return HttpResponse.json([
        {
            title:
                "Что такое генераторы статических сайтов и почему Astro — лучший фреймворк для разработки лендингов",
            url: "https://habr.com/ru/articles/779428/",
            author: "@AlexGriss",
        },
        {
            title: "Как использовать html-элемент <dialog>?",
            url: "https://habr.com/ru/articles/778542/",
            author: "@AlexGriss",
        },
    ]);
};

const getBotName = () => {
    return HttpResponse.text("overmoney-bot");
}

const auth = () => {
    return HttpResponse.text("login success");
}

const postsHandler = http.get("/api/posts", postsResolver);
const botNameHandler = http.get("/login/bot-login", getBotName);
const loginHandler = http.post("/auth/login", auth);

export const handlers = [postsHandler, botNameHandler, loginHandler];