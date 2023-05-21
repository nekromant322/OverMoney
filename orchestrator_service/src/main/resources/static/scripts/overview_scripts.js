"use strict";

window.onload = function () {
    drawUndefinedCircles(getUndefinedTransactionsData());
    getCategoriesData();

    let circles = document.querySelectorAll('.undefined-circle')
    circles.forEach(function (circle) {
        circle.addEventListener('dragstart', handleDragStart);
        circle.addEventListener('dragend', handleDragEnd);
    })
}

const minUndefinedCircleSize = 100;
const maxUndefinedCircleSize = 200;

function getMaxSingleTransactionAmount() {
    return 1000;
}

function getUndefinedTransactionsData() {
    const undefinedTransactionsData = [
        {"id": "1", "comment": "Пиво", "amount": '200'},
        {"id": "2", "comment": "Сигарет", "amount": '800'},
        {"id": "3", "comment": "Фильтр", "amount": '1000'},
        {"id": "4", "comment": "Стрижка", "amount": '600'},
        {"id": "5", "comment": "Стрижка", "amount": '778'},
        {"id": "6", "comment": "Стрижка", "amount": '350'},
        {"id": "7", "comment": "Стрижка", "amount": '500'},
        {"id": "8", "comment": "Очень длинное ключевое слово", "amount": '100'},
        {"id": "9", "comment": "Марсоход ровер", "amount": '200'}
    ]
    Object.freeze(undefinedTransactionsData)
    return undefinedTransactionsData;
}

function getCategoriesData() {
    $.ajax({
        method: 'GET',
        url: './categories/',
        contentType: "application/json; charset=utf8",
        success: function (data) {
            console.log("Successfully get categories")
            drawCategories(data)
        },
        error: function () {
            console.log("ERROR! Something wrong happened")
        }
    })

}

function handleDragStart(e) {
    this.style.opacity = 0.4;
    e.dataTransfer.setData("amount", this.dataset.amount);
    e.dataTransfer.setData("comment", this.dataset.comment);
    e.dataTransfer.setData("transactionId", this.dataset.id);
    e.dataTransfer.setData("elementId", this.id);
}

function handleDragEnd(e) {
    this.style.opacity = 1.0;
}

function handleDrop(e) {
    e.preventDefault();
    const categoryId = this.dataset.id;
    const transactionId = e.dataTransfer.getData("transactionId");
    const circleId = e.dataTransfer.getData("elementId");
    alert("Transaction ID: " + transactionId + " Category ID: " + categoryId)
    this.classList.remove('over');
    document.getElementById(circleId).remove();
}

function handleDragEnter(e) {
    this.classList.add('over');
}

function handleDragLeave(e) {
    this.classList.remove('over');
}

function handleDragOver(e) {
    if (e.preventDefault) {
        e.preventDefault();
    }
}

function drawUndefinedCircles(data) {
    data.forEach(transaction => drawCircle(transaction))
}

function drawCircle(transaction) {
    let undefinedSpace = document.querySelector('.undefined-space');
    let newCircle = document.createElement('div')
    newCircle.className = "undefined-circle"
    newCircle.draggable = true;
    newCircle.id = transaction.id;
    newCircle.dataset.comment = transaction.comment;
    newCircle.dataset.amount = transaction.amount;
    newCircle.dataset.id = transaction.id;
    newCircle.innerText = transaction.comment + '\n' + transaction.amount
    setCircleDimensions(newCircle, transaction.amount, getMaxSingleTransactionAmount());
    undefinedSpace.insertAdjacentElement('beforeend', newCircle);
}

function setCircleDimensions(circle, transactionAmount, maxSingleTransactionAmount) {
    let size = (transactionAmount / maxSingleTransactionAmount) * maxUndefinedCircleSize;
    if (size < minUndefinedCircleSize) {
        circle.style.width = minUndefinedCircleSize * 1.5 + 'px';
        circle.style.lineHeight = minUndefinedCircleSize / 2 + 'px';
    } else {
        circle.style.width = size * 1.5 + 'px';
        circle.style.lineHeight = size / 2 + 'px';
    }
}

function drawCategories(data) {
    data.forEach(category => drawCategory(category, data.length))
    let categories = document.querySelectorAll('.category')
    categories.forEach(function (category) {
        category.addEventListener('dragenter', handleDragEnter)
        category.addEventListener('dragleave', handleDragLeave)
        category.addEventListener('dragover', handleDragOver);
        category.addEventListener('drop', handleDrop);
    })
}

function drawCategory(category, length) {
    let categorySpace = document.querySelector('.categories-space');
    let newCategory = document.createElement('div');
    newCategory.className = "category";
    newCategory.innerText = category.name;
    newCategory.style.height = 100 / length + '%';
    let keywords = writeKeywordsOfCategory(category)
    newCategory.dataset.id = category.id;
    newCategory.dataset.name = category.name;
    newCategory.dataset.type = category.type;
    newCategory.dataset.keywords = keywords;
    newCategory.onclick = function () {
        let body = `<h3>Информация о категории</h3>
                    <form class="modal-category">
                   <p class="modal-category-close" href="#">X</p>
                        <div>
                            <label for="name">Наименование категории:</label>
                            <input type="text" class="input-modal-category" readonly id="name" value="${newCategory.dataset.name}" >
                        </div>
                        <div>
                            <label for="type">Тип категории:</label>
                            <input type="text" readonly class="input-modal-category" id="type" value="${newCategory.dataset.type}" >
                        </div>
                        <div>
                            <label for="keywords">Ключевые слова категории:</label>
                            <input type="text" readonly class="input-modal-category" id="keywords" value="${newCategory.dataset.keywords}">
                        </div>
                    </form>`
        $('.modal-category-content').html(body)

        $('.modal-category-fade').fadeIn();

        $('.modal-category-close').click(function () {
            $(this).parents('.modal-category-fade').fadeOut();
        });

        $('.modal-category-fade').click(function (e) {
            if ($(e.target).closest('.modal-category-content').length == 0) {
                $(this).fadeOut();
            }
        });
    }
    categorySpace.insertAdjacentElement('beforeend', newCategory);


}

function writeKeywordsOfCategory(category) {
    let allKeywords = '';
    for (let j = 0; j < category.keywords.length; j++) {
        let keywordStr = category.keywords[j]
        if (j != category.keywords.length - 1) {
            allKeywords += String(keywordStr + ', ')
        } else {
            allKeywords += String(keywordStr)
        }
    }
    return allKeywords
}