let pageSize = 50;
let pageNumber = 0;
let working = false;

$(document).ready(function() {
    getTransactions()
})

$(window).scroll(function() {
    if ($(window).scrollTop() >= $(document).height() - $(window).height() - 10) {
        if (working === false) {
            working = true;
            getTransactions()
        }
    }
})

function getTransactions() {
    $.ajax({
        type: "GET",
        url: "./transactions/history?pageSize=" + pageSize  + "&pageNumber=" + pageNumber,
        contentType: "application/json; charset=utf8",
        async: false,
        success: function(data) {
            prepareAndDraw(data)
            working = false;
        },
        error: function() {
            console.log("Something went wrong!");
        }
    });
}

function prepareAndDraw(data) {
    let transactionsData = []
    for (let i = 0; i < data.length; i++) {
        transactionsData.push({
            "id": data[i].id,
            "categoryName": data[i].categoryName,
            "comment": data[i].message,
            "amount": data[i].amount,
            "date": moment(new Date(data[i].date)).format("DD.MM.YY HH:mm")
        })
    }

    drawTable(transactionsData)
    pageNumber++;
}

function drawTable(data) {
    for (let i = 0; i < data.length; i++) {
        addRow(data[i]);
    }
}

function addRow(data) {
    let table = document.getElementById("transactions-table").getElementsByTagName("tbody")[0];
    let tr = table.insertRow(table.rows.length);

    data.categoryName == null ? data.categoryName = "Нераспозанное" : data.categoryName

    insertTd(data.categoryName, tr)
    insertTd(data.amount, tr)
    insertTd(data.comment, tr)
    insertTd(data.date, tr)
}

function insertTd(value, parent) {
    let element = document.createElement("td");
    element.scope = "row";
    element.innerText = value;
    parent.insertAdjacentElement("beforeend", element);
}