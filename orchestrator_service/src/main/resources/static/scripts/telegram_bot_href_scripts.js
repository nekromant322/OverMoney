function getTelegramBotName() {
    return new Promise(function (resolve, reject) {
        $.ajax({
            url: '/properties/telegramBotName',
            method: 'GET',
            dataType: 'text',
            success: function (response) {
                var environmentVariable = response;
                resolve(environmentVariable);
            },
            error: function (xhr, status, error) {
                reject(error);
            }
        });
    });
}

getTelegramBotName().then(function (telegramBotName) {
    let telegramLink = 'https://t.me/' + telegramBotName;
    let linkElement = document.getElementById("telegram-bot-link");
    linkElement.setAttribute("href", telegramLink);
    linkElement.addEventListener("click", function (event) {
        event.preventDefault();
        window.open(telegramLink, "_blank");
    });
}).catch(function (error) {
    console.error(error);
});