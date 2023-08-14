const tinkoffAccountSelect = $('#tinkoff-account');

window.onload = function () {
    //TODO После загрузки всей старниц
    getTinkoffInfo();

}

tinkoffAccountSelect.on('change', function () {
    let selectedAccount = tinkoffAccountSelect.val();
    getAnnualAndMonthlyReportData(selectedAccount);
});

function getTinkoffInfo() {
    $.ajax({
        url: '/tinkoff',
        method: 'GET',
        contentType: 'application/json; charset=utf8',
        async: false,
        success: function (data) {
            if (data !== null) {
                $('#tinkoff-token').val(data.token);
                addAllAccounts(data.token, data.favoriteAccountId)
            } else {
                console.log("данных о пользователе нет");
            }
        },
        error: function () {
            console.log("ошибка получения данных о пользователе");
        }
    });
}

function savePersonalToken() {
    let token = $('#tinkoff-token').val();
    $.ajax({
        url: '/tinkoff',
        method: 'POST',
        contentType: 'application/json; charset=utf8',
        async: false,
        data: JSON.stringify({
            token: token
        }),
        success: function () {
            addAllAccounts(token, '');
        },
        error: function () {
            console.log("ошибка сохранения токена");
        }
    });
}


function addAllAccounts(token, favoriteAccount) {
    let allAccounts = getUserAccounts(token);
    allAccounts.forEach(function (account) {
        let option = document.createElement('option');
        option.id = account.id;
        option.value = account.name;
        option.text = account.name;
        if (account === favoriteAccount) { //TODO сделать проверку на сохранённый счёт
            option.attr('selected', true);
            // tinkoffAccountSelect.insertAdjacentElement("beforeend", element); //TODO Подумать, как добавить в начало
        }
        tinkoffAccountSelect.append(option);
    });
}

function getUserAccounts(token) {
    let allAccounts = [];
    $.ajax({
        url: '/tinkoff/accounts?token=' + token,
        method: 'GET',
        dataType: 'application/json; charset=utf8',
        async: false,
        success: function (data) {
            allAccounts = data;
        },
        error: function () {
            console.log("Ошибка получения счетов пользователя")
        }
    });
    return allAccounts;
}