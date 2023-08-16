const tinkoffAccountSelect = $('#tinkoff-account');

window.onload = function () {
    getTinkoffInfo();

}

function isEmptyObj(object) {
    for (let key in object) {
        if (object.hasOwnProperty(key)) {
            return true;
        }
    }
    return false;
}

function getTinkoffInfo() {
    $.ajax({
        url: '/tinkoff',
        method: 'GET',
        contentType: 'application/json; charset=utf8',
        async: false,
        success: function (data) {
            if (isEmptyObj(data)) {
                $('#tinkoff-token').val(data.token); //добавляем токен на страницу
                if (data.favoriteAccountId === null) {
                    addAllAccounts(data.token, '');
                } else {
                    addAllAccounts(data.token, data.favoriteAccountId);
                }
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

function saveFavoriteAccountId() {
    let favoriteAccountId = $('#tinkoff-account').val();
    let token = $('#tinkoff-token').val();
    if (token !== null && favoriteAccountId !== null) {
        $.ajax({
            url: '/tinkoff',
            method: 'POST',
            contentType: 'application/json; charset=utf8',
            async: false,
            data: JSON.stringify({
                id: 0,
                token: token,
                favoriteAccountId: favoriteAccountId
            }),
            success: function () {
                getStatistics(token, favoriteAccountId);
            },
            error: function () {
                console.log("ошибка сохранения токена");
            }
        });
    }
}


function addAllAccounts(token, favoriteAccountId) {
    let allAccounts = getUserAccounts(token);
    allAccounts.forEach(function (account) {
        const option = $('<option>').val(account.id).text(account.name);
        if (Number(account.id) === Number(favoriteAccountId)) {
            option.attr('selected', true);
            getStatistics(token, favoriteAccountId);
        }
        tinkoffAccountSelect.append(option);
    });
}

function getUserAccounts(token) {
    let allAccounts = [];
    $.ajax({
        url: '/tinkoff/accounts?token=' + token,
        method: 'GET',
        contentType: 'application/json; charset=utf8',
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

function getStatistics(token, accountId) {
    let statistics = [];
    $.ajax({
        url: '/tinkoff/moex?token=' + token + "&accountId=" + accountId,
        method: 'GET',
        contentType: 'application/json; charset=utf8',
        async: false,
        success: function (data) {
            for (i = 0; i < data.length; i++) {
                statistics.push({
                    "ticker": data[i].tinkoffActiveDTO.ticker,
                    "name": data[i].tinkoffActiveDTO.name,
                    "lot": data[i].lot,
                    "currentPrice": data[i].tinkoffActiveDTO.currentPrice,
                    "averagePositionPrice": data[i].tinkoffActiveDTO.averagePositionPrice,
                    "currentTotalPrice": data[i].currentTotalPrice,
                    "moexWeight": data[i].moexWeight,
                    "currentWeight": data[i].currentWeight,
                    "percentFollowage": data[i].percentFollowage,
                    "correctQuantity": data[i].correctQuantity,
                    "quantity": data[i].quantity
                });
            }
        },
        error: function () {
            console.log("Ошибка получения данных о счёте пользователя")
        }
    });

    drawTableOfMOEX(statistics);
}

function drawTableOfMOEX(statistics) {
    let tbody = document.getElementById("MOEX-table").getElementsByTagName("tbody")[0];
    clearTableOfMOEX(tbody);

    for (let i = 0; i < statistics.length; i++) {
        addRowTableOfMOEX(statistics[i]);
    }
}

function addRowTableOfMOEX(dataMOEX) {
    let table = document.getElementById("MOEX-table").getElementsByTagName("tbody")[0];
    let tr = table.insertRow(table.rows.length);

    insertTdToFootInTableOfMOEX(dataMOEX.ticker, tr);
    insertTdToFootInTableOfMOEX(dataMOEX.name, tr);
    insertTdToFootInTableOfMOEX(dataMOEX.lot, tr);
    insertTdToFootInTableOfMOEX(dataMOEX.currentPrice, tr);
    insertTdToFootInTableOfMOEX(dataMOEX.averagePositionPrice, tr);
    insertTdToFootInTableOfMOEX(dataMOEX.currentTotalPrice, tr);
    insertTdToFootInTableOfMOEX(dataMOEX.moexWeight, tr);
    insertTdToFootInTableOfMOEX(dataMOEX.currentWeight, tr);
    insertTdToFootInTableOfMOEX(dataMOEX.percentFollowage, tr);
    insertTdToFootInTableOfMOEX(dataMOEX.correctQuantity, tr);
    insertTdToFootInTableOfMOEX(dataMOEX.quantity, tr);
}

function insertTdToFootInTableOfMOEX(value, parent) {
    let element = document.createElement("td");
    element.scope = "row";
    element.innerText = value;
    parent.insertAdjacentElement("beforeend", element);
}

function clearTableOfMOEX(table) {
    if (table.rows.length > 0) {
        let len = table.rows.length;
        for (let i = 0; i < len; i++) {
            table.deleteRow(0);
        }
    }
}