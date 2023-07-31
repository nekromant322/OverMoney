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

function getTelegramBotName() {
    return new Promise(function (resolve, reject) {
        $.ajax({
            url: '/settings/environment/telegramBotName',
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

    data.categoryName == null ? data.categoryName = "Нераспозанное" : data.categoryName;

    insertTd(data.categoryName, tr);
    insertTd(data.amount, tr);
    insertTd(data.comment, tr);
    insertTd(data.date, tr);
    insertTd(data.telegramUserName, tr);
    insertTdButton(data, tr);
}

function insertTd(value, parent) {
    let element = document.createElement("td");
    element.scope = "row";
    element.innerText = value;
    parent.insertAdjacentElement("beforeend", element);
}

function insertTdButton(data, parent) {
    let buttonElement = document.createElement("button");
    buttonElement.innerHTML = "x"
    buttonElement.className = "custom-button";
    buttonElement.id = data.id;
    parent.insertAdjacentElement("beforeend", buttonElement)
}

$(document).on("click", "#transactions-table tr", function () {
    let trId;
    trId = $(this).attr('id');
    getTransactionById(trId);
    $('#editModal').modal('show');
})

$(document).on("click", "#transactions-table .custom-button", function (event) {
    let id;
    event.stopPropagation();
    id = $(this).attr('id');
    getTransactionByIdForDelete(id)
    $('#deleteModal').modal('show');
})

function getTransactionByIdForDelete(id) {
    $.ajax({
        method: "GET",
        url: "./history/" + id,
        async: false,

        success: function (response) {
            fillModalWindowForDelete(response);
        }
    })
}

function getTransactionById(id) {
    $.ajax({
        method: "GET",
        url: "./history/" + id,
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
    response.categoryName == null ? categoryNow = "Нераспознанное" : categoryNow = response.categoryName;
    writeAllCategoryInModal();
    $("#nameCategoryEdit").html(options);
}

function fillModalWindowForDelete(response) {
    $("#idTransactionInFormDelete").val(response.id);
    $("#amountDelete").text(response.amount);
    $("#messageDelete").text(response.message);
}

function writeAllCategoryInModal() {
    $.ajax({
        method: "GET",
        url: "./categories/",
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
            url: "./transactions",
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

function deleteButtonClick() {
    let id = $('#idTransactionInFormDelete').val();
    $.ajax({
        method: "DELETE",
        url: "./transaction/" + id,
        async: false,
        success: () => {
            location.reload();
        },
        error: function (error) {
            console.log(error);
        }
    })
}


