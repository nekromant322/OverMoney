let usersCount;
let accountsCount;
let transactionsCount;
let activeAccountCount;
let transactionsLastThirtyDays;
let transactionsLastFullDay;
let transactionsAverage;
let numberDays = 30;

window.onload = function () {
    updateTableHeader()
    getUsersCount();
    getGroupAccountsCount();
    getTransactionsCount();
    getActiveAccountCount();
    getTransactionsCountLastThirtyDays();
    getTransactionsCountLastFullDay();
    getTransactionsAverage();

    getStat();
    getStatLastLastThirtyDays();
    getDeepSeekBalance();

}

function getStat() {
    let table = "<tr>" +
        "<td>" + usersCount + "</td>" +
        "<td>" + accountsCount + "</td>" +
        "<td>" + transactionsCount + "</td>" +
        "</tr>"

    $('#stat-table-body').html(table);
}

function getUsersCount() {
    $.ajax({
        url: "/users/count",
        type: "GET",
        async: false,
        success: function (data) {
            usersCount = data;
        }
    })
}

function getGroupAccountsCount() {
    $.ajax({
        url: "/account/count",
        type: "GET",
        async: false,
        success: function (data) {
            accountsCount = data;
        }
    })
}

function getTransactionsCount() {
    $.ajax({
        url: "/transactions/count",
        type: "GET",
        async: false,
        success: function (data) {
            transactionsCount = data;
        }
    })
}

function getStatLastLastThirtyDays() {
    let table = "<tr>" +
        "<td>" + activeAccountCount + "</td>" +
        "<td>" + transactionsLastThirtyDays + "</td>" +
        "<td>" + transactionsLastFullDay + "</td>" +
        "<td>" + transactionsAverage + "</td>" +
        "</tr>"

    $('#stat-table-last-thirty-days-body').html(table);
}

function getActiveAccountCount() {
    $.ajax({
        url: "/admin/activeAccountCount/" + numberDays,
        type: "GET",
        async: false,
        success: function (data) {
            activeAccountCount = data;
        }
    })
}

function getTransactionsCountLastThirtyDays() {
    $.ajax({
        url: "/admin/countTransactionsLastDays/" + numberDays,
        type: "GET",
        async: false,
        success: function (data) {
            transactionsLastThirtyDays = data;
        }
    })
}

function getTransactionsCountLastFullDay() {
    $.ajax({
        url: "/admin/countTransactionsLastDays/" + 1,
        type: "GET",
        async: false,
        success: function (data) {
            transactionsLastFullDay = data;
        }
    })
}

function getTransactionsAverage() {
    transactionsAverage = (transactionsLastThirtyDays / numberDays).toFixed(2)
}

function updateTableHeader() {
    const date = new Date();
    date.setDate(date.getDate() - numberDays);
    const dateString = date.toLocaleDateString('ru-RU');
    const header = document.getElementById('stat-table-last-thirty-days-header');
    header.textContent = `Статистика использования трекера с ${dateString}:`;
}


function getDeepSeekBalance() {
    $.ajax({
        url: "/admin/deepseek/balance",
        type: "GET",
        async: false,
        success: function (data) {
            const row = "<tr><td>" + (data ?? "н/д") + "</td></tr>";
            $('#deepseek-table-body').html(row);
        }
    });
}