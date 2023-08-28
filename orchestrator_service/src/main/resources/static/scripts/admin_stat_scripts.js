let usersCount;
let accountsCount;
let transactionsCount;

window.onload = function () {
    getUsersCount();
    getGroupAccountsCount();
    getTransactionsCount();

    getStat();
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