let pageSize = 50;
let pageNumber = 0;
let working = false;
let options = [];
let categoryNow;

$(document).ready(function () {
    getTransactions();
})

$(window).scroll(function () {
    if ($(window).scrollTop() >= $(document).height() - $(window).height() - 10) {
        if (working === false) {
            working = true;
            getTransactions();
        }
    }
})

function getTransactions() {
    $.ajax({
        type: "GET",
        url: "./transactions/history?pageSize=" + pageSize + "&pageNumber=" + pageNumber,
        contentType: "application/json; charset=utf8",
        async: false,
        success: function (data) {
            prepareAndDraw(data);
            working = false;
        },
        error: function () {
            console.log("Something went wrong!");
        }
    })
}

function prepareAndDraw(data) {
    let transactionsData = []
    for (let i = 0; i < data.length; i++) {
        transactionsData.push({
            "id": data[i].id,
            "categoryName": data[i].categoryName,
            "comment": data[i].message,
            "amount": data[i].amount,
            "telegramUserName": data[i].telegramUserName,
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
    tr.id = data.id;
    tr.dataset.toggle = "modal";
    tr.dataset.target = "#editModal";

    data.categoryName == null ? data.categoryName = "Нераспозанное" : data.categoryName;

    insertTd(data.categoryName, tr);
    insertTd(data.amount, tr);
    insertTd(data.comment, tr);
    insertTd(data.date, tr);
    insertTd(data.telegramUserName, tr);
}

function insertTd(value, parent) {
    let element = document.createElement("td");
    element.scope = "row";
    element.innerText = value;
    parent.insertAdjacentElement("beforeend", element);
}

$(function rowTableClick() {
    let trId;
    $('#transactions-table tr').click(function () {
        trId = $(this).attr('id');
        getTransactionById(trId);
    })
})

function getTransactionById(id) {
    $.ajax({
        method: "GET",
        url: "/history/" + id,
        async: false,

        success: function (response) {
            fillModalWindow(response);
        }
    })
}

function fillModalWindow(response) {
    $("#idTransactionInForm").val(response.id);
    $("#amountEdit").val(response.amount);
    $("#messageEdit").val(response.message);
    categoryNow = response.categoryName;
    writeAllCategoryInModal();
    $("#nameCategoryEdit").html(options);
}

function writeAllCategoryInModal() {
    $.ajax({
        method: "GET",
        url: "/categories/",
        async: false,

        success: function (data) {
            let listOption = [];
            listOption[0] = `<option value=` + categoryNow + `>` + categoryNow + `</option>`;
            for (let i = 0; i < data.length; i++) {
                if (data[i].name !== categoryNow) {
                    listOption[i + 1] = `<option value=` + data[i].name + `>` + data[i].name + `</option>`;
                }
            }
            options = listOption;
        }
    })
}

$(function editButtonClick() {
    $('#editBtn').click(function (event) {
        event.preventDefault();
        let transaction = {
            id: $('#idTransactionInForm').val(),
            categoryName: $('#nameCategoryEdit').val(),
            amount: $('#amountEdit').val(),
            message: $('#messageEdit').val(),
        }
        $.ajax({
            method: "PUT",
            url: "/transactions",
            async: false,
            contentType: "application/json",
            data: JSON.stringify(transaction),
            success: () => {
                location.reload();
            },
            error: function (error) {
                console.log(error);
            }
        })
    })
})

