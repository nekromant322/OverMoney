const placeForExpense = document.getElementById('expenseAnalytics');
const placeForIncome = document.getElementById('incomeAnalytics');
const INCOME = "INCOME";
const EXPENSE = "EXPENSE";
const colors =
    ["rgb(205, 92, 92)", "rgb(255, 192, 203)", "rgb(255, 165, 0)",
        "rgb(250, 250, 210)", "rgb(230, 230, 250)", "rgb(147, 112, 219)",
        "rgb(75, 0, 130)", "rgb(188, 143, 143)", "rgb(192, 192, 192)",
        "rgb(0, 255, 0)", "rgb(100, 149, 237)", "rgb(255, 0, 0)",
        "rgb(0, 128, 0)", "rgb(32, 178, 170)", "rgb(128, 0, 128)",
        "rgb(250, 235, 215)", "rgb(105, 105, 105)", "rgb(255, 255, 0)",
        "rgb(255, 20, 147)", "rgb(0, 255, 255)", "rgb(233, 150, 122)",
        "rgb(255, 182, 193)", "rgb(154, 205, 50)", "rgb(0, 0, 255)",
        "rgb(70,165,178)", "rgb(50, 205, 50)", "rgb(255, 228, 225)",
        "rgb(0, 0, 0)", "rgb(128,128,10)", "rgb(255, 0, 255)",
        "rgb(127, 255, 212)", "rgb(255, 215, 0)", "rgb(46, 139, 87)",
        "rgb(220, 20, 60)", "rgb(65, 105, 225)", "rgb(230, 230, 250)",
        "rgb(189, 183, 107)", "rgb(112, 128, 144)", "rgb(50, 205, 50)",
        "rgb(218, 112, 214)", "rgb(25, 25, 112)", "rgb(255, 160, 122)",
        "rgb(255, 239, 213)", "rgb(95, 158, 160)", "rgb(139, 69, 19)",
        "rgb(220, 220, 220)", "rgb(139, 0, 139)", "rgb(255, 69, 0)",
        "rgb(0, 255, 127)", "rgb(135, 206, 235)"]

window.onload = function () {
    getAnalyticsData(EXPENSE, placeForExpense);
    getAnalyticsData(INCOME, placeForIncome);
}

function getAnalyticsData(type, place) {
    let data = getCategoriesAndSumAmountByType(type)
    let categoriesName = [];
    let color = [];
    let sumOfTransactions = [];
    let dataSorted = data.sort(comparatorByFieldName)

    for (let i = 0; i < dataSorted.length & i < 50; i++) {
        color.push(colors[i])
        categoriesName.push(dataSorted[i].category.name)
        sumOfTransactions.push(dataSorted[i].sumOfTransactions)
    }
    drawAnalytics(categoriesName, sumOfTransactions, color, place)
}

function drawAnalytics(categories, transactionSums, color, place) {

    new Chart(place, {
        type: 'pie',
        data: {
            labels: categories,
            datasets: [{
                label: 'Рублей',
                data: transactionSums,
                backgroundColor: color,
                hoverOffset: 1
            }]
        },
        options: {
            scales: {
                y: {
                    beginAtZero: true
                }
            }
        }
    });
}
function comparatorByFieldName(dataOne, dataTwo) {
    if (dataOne.category.name.toLowerCase() < dataTwo.category.name.toLowerCase()) {
        return -1;
    }
    if (dataOne.category.name.toLowerCase() > dataTwo.category.name.toLowerCase()) {
        return 1;
    }
    return 0;
}

function getCategoriesAndSumAmountByType(type) {
    let url = './analytics/totalCategorySums/' + type;
    let categories;
    $.ajax({
        method: 'GET',
        url: url,
        contentType: "application/json; charset=utf8",
        async: false,
        success: function (data) {
            categories = data
            if (data.length === 0) {
                console.log("data is null")
            }
        },
        error: function () {
            console.log("ERROR! Something wrong happened")
        }
    })
    return categories;
}