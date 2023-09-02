window.onload = function () {
    setAvailableYearsForAnnualAndMonthlyTotalStatistics();
}

function setAvailableYearsForAnnualAndMonthlyTotalStatistics() {
    $.ajax({
        url: '/analytics/available-years',
        type: 'GET',
        dataType: 'json',
        success: function (years) {
            years.sort(compareYear);
            years.forEach(function (year) {
                const option = $('<option>').val(year).text(year);
                if (year === new Date().getFullYear()) {
                    option.attr('selected', true);
                }
                yearSelectAnnualAndMonthlyTotalStatistics.append(option);
                getAnnualAndMonthlyReportData(years[0]);
            });
        },
        error: function (xhr, status, error) {
            console.error(error);
        }
    });
}

function compareYear(year1, year2) {
    return year2 - year1;
}

const yearSelectAnnualAndMonthlyTotalStatistics = $('#yearSelectAnnualAndMonthlyTotalStatistics');

yearSelectAnnualAndMonthlyTotalStatistics.on('change', function () {
    const selectedYear = yearSelectAnnualAndMonthlyTotalStatistics.val();
    getAnnualAndMonthlyReportData(selectedYear);
});

function getAnnualAndMonthlyReportData(year) {
    let url = "./analytics/total/" + year;
    $.ajax({
        method: 'GET',
        url: url,
        contentType: "application/json; charset=utf8",
        async: false,
        success: function (data) {
            if (data.length === 0) {
                alert("У вас пока нет никаких транзакций.")
                console.log("data is null")
            } else {
                parseDataAndCreateTableOfExpense(data);
            }
        },
        error: function () {
            console.log("ERROR! Something wrong happened")
        }
    });
}

function parseDataAndCreateTableOfExpense(data) {
    let monthlyExpenseToCategoryName = [];
    let monthlyOpacityToCategoryName = [];
    let numberOfRows = data.length;

    for (i = 0; i < data.length; i++) {

        let monthlyExpense = parseMapOfMonthlyAnalysesAndShareAndGetExpense(data[i]["monthlyAnalytics"]);
        let monthlyOpacities = parseMapOfMonthlyAnalysesAndShareAndGetExpense(data[i]["shareOfMonthlyExpenses"]);
        monthlyExpenseToCategoryName.push({
            "categoryId": data[i].categoryId,
            "categoryName": data[i].categoryName,
            "january": monthlyExpense[0],
            "february": monthlyExpense[1],
            "march": monthlyExpense[2],
            "april": monthlyExpense[3],
            "may": monthlyExpense[4],
            "june": monthlyExpense[5],
            "july": monthlyExpense[6],
            "august": monthlyExpense[7],
            "september": monthlyExpense[8],
            "october": monthlyExpense[9],
            "november": monthlyExpense[10],
            "december": monthlyExpense[11]
        });
        monthlyOpacityToCategoryName.push({
            "categoryId": data[i].categoryId,
            "categoryName": data[i].categoryName,
            "january": monthlyOpacities[0],
            "february": monthlyOpacities[1],
            "march": monthlyOpacities[2],
            "april": monthlyOpacities[3],
            "may": monthlyOpacities[4],
            "june": monthlyOpacities[5],
            "july": monthlyOpacities[6],
            "august": monthlyOpacities[7],
            "september": monthlyOpacities[8],
            "october": monthlyOpacities[9],
            "november": monthlyOpacities[10],
            "december": monthlyOpacities[11]
        });
    }

    drawTableOfExpense(monthlyExpenseToCategoryName, monthlyOpacityToCategoryName, totalList(data), numberOfRows);
}

function parseMapOfMonthlyAnalysesAndShareAndGetExpense(data) {
    let expenses = [];
    for (let key in data) {
        expenses.push(data[key]);
    }
    return expenses;
}

function totalList(data) {
    let total = [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0];
    let numberBeforeDelimiter = 2;
    for (i = 0; i < data.length; i++) {
        let monthlyExpense = parseMapOfMonthlyAnalysesAndShareAndGetExpense(data[i]["monthlyAnalytics"]);
        for (j = 0; j < monthlyExpense.length; j++) {
            total[j] = +(total[j] + monthlyExpense[j]).toFixed(numberBeforeDelimiter);
        }
    }
    return total;
}

function drawTableOfExpense(dataExpense, dataOpacity, totalExpense, numberOfRows) {
    let tfoot = document.getElementById("total-analytics-table").getElementsByTagName("tfoot")[0];
    clearTableOfExpense(tfoot);
    let tbody = document.getElementById("total-analytics-table").getElementsByTagName("tbody")[0];
    clearTableOfExpense(tbody);

    addRowBodyInTableOfExpense(totalExpense, numberOfRows);
    for (let i = 0; i < dataExpense.length; i++) {
        addRowFootInTableOfExpense(dataExpense[i], dataOpacity[i], numberOfRows);
    }
}

function addRowBodyInTableOfExpense(totalExpense, numberOfRows) {
    let table = document.getElementById("total-analytics-table").getElementsByTagName("tbody")[0];
    let tr = table.insertRow(table.rows.length);

    insertTdToBodyInTableOfExpense('ИТОГО:', tr, numberOfRows);
    totalExpense.forEach(monthlyTotalItem => {
        insertTdToBodyInTableOfExpense(monthlyTotalItem, tr, numberOfRows);
    });
}

function addRowFootInTableOfExpense(dataExpense, dataOpacity, numberOfRows) {
    let table = document.getElementById("total-analytics-table").getElementsByTagName("tfoot")[0];
    let tr = table.insertRow(table.rows.length);

    createCategoryTdToFootInTableOfExpense(dataExpense.categoryId, tr);
    insertTdToFootInTableOfExpense(0, dataExpense.categoryName, tr, numberOfRows);
    insertTdToFootInTableOfExpense(dataOpacity.january, dataExpense.january, tr, numberOfRows);
    insertTdToFootInTableOfExpense(dataOpacity.february, dataExpense.february, tr, numberOfRows);
    insertTdToFootInTableOfExpense(dataOpacity.march, dataExpense.march, tr, numberOfRows);
    insertTdToFootInTableOfExpense(dataOpacity.april, dataExpense.april, tr, numberOfRows);
    insertTdToFootInTableOfExpense(dataOpacity.may, dataExpense.may, tr, numberOfRows);
    insertTdToFootInTableOfExpense(dataOpacity.june, dataExpense.june, tr, numberOfRows);
    insertTdToFootInTableOfExpense(dataOpacity.july, dataExpense.july, tr, numberOfRows);
    insertTdToFootInTableOfExpense(dataOpacity.august, dataExpense.august, tr, numberOfRows);
    insertTdToFootInTableOfExpense(dataOpacity.september, dataExpense.september, tr, numberOfRows);
    insertTdToFootInTableOfExpense(dataOpacity.october, dataExpense.october, tr, numberOfRows);
    insertTdToFootInTableOfExpense(dataOpacity.november, dataExpense.november, tr, numberOfRows);
    insertTdToFootInTableOfExpense(dataOpacity.december, dataExpense.december, tr, numberOfRows);
}

function insertTdToFootInTableOfExpense(opacity, value, parent, numberOfRows) {
    let element = document.createElement("td");
    element.scope = "row";
    element.innerText = value;
    element.style.height = 50 / numberOfRows + '%'; //Не работает в силу того, что position != fixed
    if (value === 0) {
        element.style.backgroundColor = 'rgba(190, 190, 190,' + 0.2 + ')';
        element.style.opacity = String(0.4);
    } else {
        element.style.backgroundColor = 'rgba(255, 165, 0,' + opacity + ')';
    }
    parent.insertAdjacentElement("beforeend", element);
}

function createCategoryTdToFootInTableOfExpense(value, parent) {
    let element = document.createElement("td");
    element.scope = "row";
    element.innerText = value;
    element.setAttribute("hidden", "hidden");
    parent.insertAdjacentElement("beforeend", element);
}

function insertTdToBodyInTableOfExpense(value, parent, numberOfRows) {
    let element = document.createElement("td");
    element.scope = "row";
    element.innerText = value;
    element.style.height = 80 / numberOfRows + '%'; //Не работает в силу того, что position != fixed
    if (value === 0) {
        element.style.backgroundColor = 'rgba(128, 128, 128,' + 0.7 + ')';
        element.style.opacity = String(0.8);
    } else {
        element.style.backgroundColor = 'rgb(128, 128, 128)';
    }
    parent.insertAdjacentElement("beforeend", element);
}

function clearTableOfExpense(table) {
    if (table.rows.length > 0) {
        let len = table.rows.length;
        for (let i = 0; i < len; i++) {
            table.deleteRow(0);
        }
    }
}


$(document).on("click", "#total-analytics-table-foot td", function () {
    let rowIndex = $(this).closest("tr").index();
    
    let categoryId = $("#total-analytics-table-foot").find("tr").eq(rowIndex).find("td").eq(0).html();
    let month = $(this).closest("td").index() - 1;
    let year = $("#yearSelectAnnualAndMonthlyTotalStatistics").val();
    getTransactionsByCategoryAndMonth(year, month, categoryId);
})

function getTransactionsByCategoryAndMonth(year, month, categoryId) {
    $('#categoryInfoModal').modal('show');

    $.ajax({
        method: 'GET',
        url: '/transactions/info?year=' + year + '&month=' + month + '&categoryId=' + categoryId,
        contentType: 'application/json; charset=utf8',
        async: false,
        success: function (data) {
            let list = document.querySelector('#infoField');
            list.innerHTML = "";

            for (i = 0; i < data.length; i++) {
                let elem = document.createElement('p');
                elem.innerHTML = (i + 1) + '. ' + data[i].message + ' ' + data[i].amount;
                list.appendChild(elem);
            }
        }
    })
}