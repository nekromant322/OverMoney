const placeForExpense = document.getElementById('expenseAnalytics');
const placeForIncome = document.getElementById('incomeAnalytics');
const placeForExpenseTable = document.getElementById('table_expense');
const placeForIncomeTable = document.getElementById('table_income');
const placeForMonthlyYearIncomeAnalytics = document.getElementById('monthlyYearIncomeAnalytics');
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
    getAnalyticsData(EXPENSE, placeForExpense, placeForExpenseTable);
    getAnalyticsData(INCOME, placeForIncome, placeForIncomeTable);
    setAvailableYearsForMonthlyAnalytics();
}

function getAnalyticsData(type, place, placeTable) {
    let data = getCategoriesAndSumAmountByType(type)
    let categoriesName = [];
    let color = [];
    let mediumAmountOfTransactions = [];
    let dataSorted = data.sort(comparatorByFieldName)

    for (let i = 0; i < dataSorted.length & i < 50; i++) {
        color.push(colors[i])
        categoriesName.push(dataSorted[i].categoryName)
        mediumAmountOfTransactions.push(dataSorted[i].mediumAmountOfTransactions)
    }
    drawAnalytics(categoriesName, mediumAmountOfTransactions, color, place, placeTable);

}


function drawAnalytics(categories, transactionSums, color, place, placeTable) {

    let chartData = new Chart(place, {
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
    }).data;
    drawTable(chartData, placeTable);
}

function drawTable(chartData, placeTable) {

    let tbody = document.createElement('tbody');
    chartData.labels.forEach(function (label, index) {
        let value = chartData.datasets[0].data[index];
        let color = chartData.datasets[0].backgroundColor[index];
        let category = chartData.labels[index];

        let row = document.createElement('tr');
        let valueElement = document.createElement('td');
        valueElement.textContent = value;
        let colorElement = document.createElement('td');
        colorElement.style.backgroundColor = color;
        colorElement.style.width = "10px";
        let categoryElement = document.createElement('td');
        categoryElement.textContent = category;
        row.appendChild(colorElement);
        row.appendChild(categoryElement);
        row.appendChild(valueElement);
        tbody.appendChild(row);
    });
    placeTable.appendChild(tbody);
}

function comparatorByFieldName(dataOne, dataTwo) {
    if (dataOne.categoryName.toLowerCase() < dataTwo.categoryName.toLowerCase()) {
        return -1;
    }
    if (dataOne.categoryName.toLowerCase() > dataTwo.categoryName.toLowerCase()) {
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

//Расходы доходы по месяцам
const yearSelect = $('#yearSelect');
const checkbox = $('.info__switch');

checkbox.on('change', function () {
    loadData(new Date().getFullYear());
});

yearSelect.on('change', function () {
    const selectedYear = yearSelect.val();
    loadData(selectedYear);
});

$.ajax({
    url: '/analytics/available-years',
    type: 'GET',
    dataType: 'json',
    success: function (years) {
        years.forEach(function (year) {
            const option = $('<option>').val(year).text(year);
            if (year === new Date().getFullYear()) {
                option.attr('selected', true); // Устанавливаем атрибут selected для текущего года
            }
            yearSelect.append(option);
        });
    },
    error: function (xhr, status, error) {
        console.error(error);
    }
});

function loadData(selectedYear) {
    $.ajax({
        url: '/analytics/totalIncomeOutcome/' + selectedYear,
        method: 'GET',
        dataType: 'json',
        success: function (data) {
            data.sort(function (a, b) {
                return new Date(Date.parse('01 ' + a.month + ' 2000')) - new Date(Date.parse('01 ' + b.month + ' 2000'));
            });
            const labels = data.map(item => item.month);
            const dataset1 = data.map(item => item.totalExpense);
            const dataset2 = data.map(item => item.totalIncome);
            const chartData = {
                labels: labels,
                datasets: [
                    {
                        label: 'Расходы',
                        data: dataset1,
                        borderColor: 'rgb(255, 99, 132)',
                        backgroundColor: 'rgb(255, 99, 132)',
                    },
                    {
                        label: 'Доходы',
                        data: dataset2,
                        borderColor: 'rgb(54, 162, 235)',
                        backgroundColor: 'rgb(54, 162, 235)',
                    }
                ]
            };
            var ctx = document.getElementById('totalExpenseAnalitics').getContext('2d');
            if (window.myChart) {
                window.myChart.destroy();
            }
            // Очищаем canvas перед рисованием нового графика
            ctx.clearRect(0, 0, ctx.canvas.width, ctx.canvas.height);
            var myChart = new Chart(ctx, {
                type: 'bar',
                data: chartData,
                options: {}
            });
            window.myChart = myChart;
        },
        error: function (error) {
            console.error(error);
        }
    });
}


const yearSelectForMonthlyAnalytics = $('#yearSelectForMonthlyAnalytics');
const checkboxForMonthlyAnalytics = $('#info__body_4');

checkboxForMonthlyAnalytics.on('change', function () {
    drawMonthlyAnalyticsForYear(new Date().getFullYear());
});

yearSelectForMonthlyAnalytics.on('change', function () {
    const selectedYear = yearSelectForMonthlyAnalytics.val();
    drawMonthlyAnalyticsForYear(selectedYear);
});

function drawMonthlyAnalyticsForYear(year) {
    if (window.placeForMonthlyYearIncomeAnalytics) {
        removeData(placeForMonthlyYearIncomeAnalytics);
    }
    ;
    let monthlyReportData = getMonthlyReportData(year);
    let months = ["Янв", "Фев.", "Мар.", "Апр", "Май", "Июнь", "Июль", "Авг.", "Сен.", "Окт.", "Ноя.", "Дек."];


    var ctx = document.getElementById('monthlyYearIncomeAnalytics').getContext('2d');
    if (window.monthlyAnalyticsChart) {
        window.monthlyAnalyticsChart.destroy();
    }
    // Очищаем canvas перед рисованием нового графика
    ctx.clearRect(0, 0, ctx.canvas.width, ctx.canvas.height);
    let monthlyAnalyticsChart = new Chart(placeForMonthlyYearIncomeAnalytics, {
        type: "line",
        data: {
            labels: months
        },
        options: {
            legend: {display: true}
        }
    });
    let counter = 0;
    monthlyReportData.forEach(dataItem => {
        let legend = dataItem.categoryName;
        let object = dataItem.monthlyAnalytics;
        let array = Object.entries(object);
        let values = [];
        array.forEach(item => {
            values.push(item[1]);
        });
        addDataset(monthlyAnalyticsChart, values, legend, colors[counter]);
        counter++;
    });
    window.monthlyAnalyticsChart = monthlyAnalyticsChart;
}

function setAvailableYearsForMonthlyAnalytics() {
    $.ajax({
        url: '/analytics/available-years',
        type: 'GET',
        dataType: 'json',
        success: function (years) {
            years.forEach(function (year) {
                const option = $('<option>').val(year).text(year);
                if (year === new Date().getFullYear()) {
                    option.attr('selected', true); // Устанавливаем атрибут selected для текущего года
                }
                yearSelectForMonthlyAnalytics.append(option);
            });
        },
        error: function (xhr, status, error) {
            console.error(error);
        }
    });
}

function getMonthlyReportData(year) {
    let url = './analytics/income/' + year;
    let monthlyIncomeForYear;
    $.ajax({
        method: 'GET',
        url: url,
        contentType: "application/json; charset=utf8",
        async: false,
        success: function (data) {
            monthlyIncomeForYear = data
            if (data.length === 0) {
                console.log("data is null")
            }
        },
        error: function () {
            console.log("ERROR! Something wrong happened")
        }
    })
    return monthlyIncomeForYear;
}

function getMonthName(monthNumber) {
    const date = new Date();
    date.setMonth(monthNumber - 1);

    return date.toLocaleString('en-US', {month: 'short'});
}

function addDataset(chart, dataset, labelName, color) {
    chart.data.datasets.push({
        label: labelName,
        data: dataset,
        borderColor: color
    });
    chart.update();
}

function removeData(chart) {
    chart.data.datasets.delete();
    chart.update();
}

