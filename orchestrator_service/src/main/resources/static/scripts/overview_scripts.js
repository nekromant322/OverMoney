"use strict";
const INCOME = "INCOME";
const EXPENSE = "EXPENSE";
window.onload = function () {
    getUndefinedTransactionsData();
    getCategoriesData();
    drawEmptyCircleForModal();
    setInterval(checkUserIsActive, timeout / 2);
    let toast = toastr["success"]("Загрузка нераспознанных транзакций");
    toastr.options = {
        "closeButton": false,
        "debug": true,
        "newestOnTop": false,
        "progressBar": false,
        "positionClass": "toast-bottom-left",
        "preventDuplicates": false,
        "onclick": null,
        "showDuration": "300",
        "hideDuration": "1000",
        "timeOut": "7000",
        "extendedTimeOut": "1000",
        "showEasing": "swing",
        "hideEasing": "linear",
        "showMethod": "fadeIn",
        "hideMethod": "fadeOut"
    }
    toast.focus().click();
}

const minUndefinedCircleSize = 100;
const maxUndefinedCircleSize = 200;
let maxSingleTransactionAmount;

function getTelegramBotName() {
    return new Promise(function (resolve, reject) {
        $.ajax({
            url: '/properties/telegramBotName',
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

function setMaxSingleTransactionAmount(value) {
    maxSingleTransactionAmount = value;
}

function getMaxSingleTransactionAmount() {
    return maxSingleTransactionAmount;
}
// ---- НАЧАЛО РАБОТЫ С LONG POLLING ----

let fibonacciCounter = fibonacci();
let getNewUndefinedTransactions = false;
let timer;
let timeout = getPeriodOfInactivity();
let lastActiveTimestamp = 0;
let userIsActive = false;

window.addEventListener('mousemove', active, true);
window.addEventListener('keypress', active, true);
window.addEventListener('click', active, true);
window.addEventListener('ontouchstart', active, true);
window.addEventListener('ontouchmove', active, true);
window.addEventListener('scroll', active, true);


function fibonacci() {
    let fibonacciInner = [1, 1];

    return {
        getNext: function() {
            let lastNumber = fibonacciInner[1];
            fibonacciInner[1] = fibonacciInner[0] + fibonacciInner[1];
            fibonacciInner[0] = lastNumber;
            return fibonacciInner[1];
        },
        get: function() {
            return fibonacciInner[1];
        },
        set: function (array) {
            if (array.length === 2) {
                fibonacciInner = array;
            } else {
                console.log('некорректноей длины передали массив ы fibonacci.set(array)');
            }
        },
        reset: function() {
            fibonacciInner = [1, 1];
        }
    };
}

function checkUserIsActive() {
    if (userIsActive && new Date().getTime() - lastActiveTimestamp > timeout){
        userIsActive = false;
        fibonacciCounter.reset();
    }
}

function active() {
    lastActiveTimestamp = new Date().getTime();
    if (!userIsActive) {
        userIsActive = true;
    }
    idleLongPolling();
}

function idleLongPolling() {
    if (!getNewUndefinedTransactions) {
        clearTimeout(timer);
        let timeDelay = fibonacciCounter.getNext() * 1000;
        timer = setTimeout(getUndefinedTransactionsData, timeDelay);
        getNewUndefinedTransactions = true;
    }
}

function getPeriodOfInactivity() {
    let periodOfInactivity;
    $.ajax({
        method: 'GET',
        url: './properties/longPolling',
        contentType: "application/json; charset=utf8",
        async: false,
        success: function (data) {
            periodOfInactivity = data;
        },
        error: function () {
            console.log("Ошибка получения данных для long polling");
        }
    })
    return periodOfInactivity;
}

function checkAvailability(arr, val) {
    return arr.some(arrVal => val === arrVal);
}

// ---- КОНЕЦ РАБОТЫ С LONG POLLING ----

function getUndefinedTransactionsData() {
    $.ajax({
        method: 'GET',
        url: './transactions',
        contentType: "application/json; charset=utf8",
        success: function (data) {
            let undefinedTransactionsData = []
            let maxAmount = -1
            for (let i = 0; i < data.length; i++) {
                if (data[i].amount > maxAmount) {
                    maxAmount = data[i].amount
                }

                undefinedTransactionsData.push({
                    "id": data[i].id,
                    "comment": data[i].message,
                    "amount": data[i].amount,
                    "suggestedCategoryId": data[i].suggestedCategoryId
                })
            }

            let allUndefinedCircles = Array.from(document.querySelectorAll(".undefined-circle"));
            if (undefinedTransactionsData.length > allUndefinedCircles.length) {
                let undefinedTransactionsData_new = [];

                if (allUndefinedCircles.length !== 0) {
                    for (let index = 0; index < undefinedTransactionsData.length; index++) {
                        if (!checkAvailability(allUndefinedCircles.map(unTr => unTr.id), undefinedTransactionsData[index].id)) {
                            undefinedTransactionsData_new.push(undefinedTransactionsData[index]);
                        }
                    }
                } else {
                    undefinedTransactionsData_new = undefinedTransactionsData;
                }

                Object.freeze(undefinedTransactionsData_new)
                setMaxSingleTransactionAmount(maxAmount)
                drawUndefinedCircles(undefinedTransactionsData_new)

                let circles = document.querySelectorAll('.undefined-circle')
                circles.forEach(function (circle) {
                    circle.addEventListener('dragstart', handleDragStart);
                    circle.addEventListener('dragend', handleDragEnd);
                });
            }
            getNewUndefinedTransactions = false;
        },
        error: function () {
            registerAccount()
        }
    })
}

function registerAccount() {
    $.ajax({
        method: 'GET',
        url: './account/register/single',
        success: function () {
            if (alert('Аккаунт успешно зарегистрирован')) {
            } else window.location.reload();
        },
        error: function () {
        }
    });
}

function getCategoriesData() {
    $.ajax({
        method: 'GET',
        url: './categories/',
        contentType: "application/json; charset=utf8",
        success: function (data) {
            let noDefaultCategory = localStorage.getItem("noDefaultCategory");
            console.log("Successfully get categories")
            if (data.length === 0 && noDefaultCategory == null) {
                console.log("data is null")
                drawModalDefaultCategories()
            }
            drawCategories(data)
        },
        error: function () {
            console.log("ERROR! Something wrong happened")
        }
    })
}

function getCategoryById(id) {
    let url = './categories/' + id;
    let category;
    $.ajax({
        method: 'GET',
        url: url,
        contentType: "application/json; charset=utf8",
        async: false,
        success: function (data) {
            console.log("Successfully get category")
            category = data
            if (data.length === 0) {
                console.log("data is null")
            }
        },
        error: function () {
            console.log("ERROR! Something wrong happened")
        }
    })
    return category;
}

function drawModalDefaultCategories() {
    let modal = document.getElementById("modal-default-category");
    modal.style.display = "block";

    window.onclick = function (event) {
        if (event.target === modal) {
            modal.style.display = "none";
        }
    }
}

function closeModal() {
    let modal = document.getElementById("modal-default-category");
    modal.style.display = "none"
}

function rejectionAndCloseModal() {
    let modal = document.getElementById("modal-default-category");
    modal.style.display = "none"
    localStorage.setItem("noDefaultCategory", "true");
}

function addDefaultCategories() {
    $.ajax(
        $.ajax({
            method: 'POST',
            url: './categories/add-default-categories',
            contentType: "application/json; charset=utf8",
            success: function () {
                window.location.reload()
            },
            error: function () {
                console.log("ERROR! Something wrong happened")
            }
        })
    )
}

function handleDragStart(e) {
    this.style.opacity = 0.4;
    e.dataTransfer.setData("amount", this.dataset.amount);
    e.dataTransfer.setData("comment", this.dataset.comment);
    e.dataTransfer.setData("transactionComment", this.dataset.comment);
    e.dataTransfer.setData("elementId", this.id);
    if (this.dataset.suggestedCategoryId != "null") {
        let suggestedCategoryId = this.dataset.suggestedCategoryId;
        let suggestedCategory = document.querySelector('div[data-id=\"' + suggestedCategoryId + '\"]');
        suggestedCategory.style.backgroundColor = "green";
    }
}

function handleDragEnd(e) {
    this.style.opacity = 1.0;
    if (this.dataset.suggestedCategoryId != "null") {
        let suggestedCategoryId = this.dataset.suggestedCategoryId;
        let suggestedCategory = document.querySelector('div[data-id=\"' + suggestedCategoryId + '\"]');
        suggestedCategory.style.backgroundColor = "transparent";
    }
}

function handleDrop(e) {
    e.preventDefault();
    const categoryId = this.dataset.id;
    const categoryName = this.dataset.name;
    const transactionComment = e.dataTransfer.getData("transactionComment");
    const transactionId = e.dataTransfer.getData("elementId");
    console.log(transactionId)
    const transactionDefined = {
        transactionId: transactionId,
        categoryId: categoryId
    }
    let url = './transaction/define'
    $.ajax({
        url: url,
        type: 'POST',
        headers: {
            'Content-Type': 'application/json',
        },
        async: false,
        data: JSON.stringify(transactionDefined),
        dataType: "json",
        success: function () {
            console.log("Successfully updated transactions")
        },
        error: function (error) {
            console.log(error)
        }
    });
    drawToast(e, categoryName, transactionDefined, transactionComment);
    this.classList.remove('over');

    var circles = document.querySelectorAll(".undefined-circle")
    for (var i = 0; i < circles.length; i++) {
        if (circles[i].dataset.comment === transactionComment) {
            circles[i].remove();
        }
    }
}

function drawToast(e, categoryName, transactionDefined, transactionComment) {
    let transactionCommentNoSpace = transactionComment.replaceAll(" ", "_");
    let transactionCommentNoQuote = transactionCommentNoSpace.replaceAll("\'", "_");
    let transactionCommentForButtonName = transactionCommentNoQuote.replaceAll("\"", "_");
    toastr["success"](
        '<div><text font-size="30">' +
        e.dataTransfer.getData("comment") + ' ' + e.dataTransfer.getData("amount") + ' добавлено в категорию ' + categoryName +
        '</text>' +
        '<div class="buttonUndefine" id="undefineButtonFor_' + transactionCommentForButtonName + '">' +
        '<a>Отменить</a>' +
        '</div></div>'
    )

    toastr.options = {
        "closeButton": false,
        "debug": true,
        "newestOnTop": false,
        "progressBar": false,
        "positionClass": "toast-bottom-left",
        "preventDuplicates": false,
        "onclick": null,
        "showDuration": "300",
        "hideDuration": "1000",
        "timeOut": "7000",
        "extendedTimeOut": "1000",
        "showEasing": "swing",
        "hideEasing": "linear",
        "showMethod": "fadeIn",
        "hideMethod": "fadeOut"
    }

    const button = document.querySelector('#undefineButtonFor_' + transactionCommentForButtonName);

    button.onclick = () => {
        undefineTransaction(transactionDefined);
        let circles = document.querySelectorAll('.undefined-circle')
        circles.forEach(circle => document.getElementById(circle.id).remove())
        setTimeout(function () {
            getUndefinedTransactionsData();
        }, 200)
    };
}

function drawCustomToast(message) {
    toastr["success"](message)

    toastr.options = {
        "closeButton": false,
        "debug": true,
        "newestOnTop": false,
        "progressBar": false,
        "positionClass": "toast-bottom-left",
        "preventDuplicates": false,
        "onclick": null,
        "showDuration": "300",
        "hideDuration": "1000",
        "timeOut": "7000",
        "extendedTimeOut": "1000",
        "showEasing": "swing",
        "hideEasing": "linear",
        "showMethod": "fadeIn",
        "hideMethod": "fadeOut"
    }
}

function drawErrorToast(message) {
    toastr.error(message);

    toastr.options = {
        "closeButton": false,
        "debug": true,
        "newestOnTop": false,
        "progressBar": false,
        "positionClass": "toast-bottom-left",
        "preventDuplicates": false,
        "onclick": null,
        "showDuration": "300",
        "hideDuration": "1000",
        "timeOut": "7000",
        "extendedTimeOut": "1000",
        "showEasing": "swing",
        "hideEasing": "linear",
        "showMethod": "fadeIn",
        "hideMethod": "fadeOut",
        "iconClass": null
    };
}

function undefineTransaction(transactionDefined) {
    let url = './transaction/undefine'
    $.ajax({
        url: url,
        type: 'POST',
        headers: {
            'Content-Type': 'application/json',
        },
        async: false,
        data: JSON.stringify(transactionDefined),
        dataType: "json",
        success: function () {
            console.log("Successfully updated transactions")
        },
        error: function (error) {
            console.log(error)
        }
    });
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
    newCircle.dataset.suggestedCategoryId = transaction.suggestedCategoryId;
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
    data.sort((a, b) => {
        if (a.type === EXPENSE && b.type === INCOME) {
            return -1;
        } else if (a.type === INCOME && b.type === EXPENSE) {
            return 1;
        } else {
            return 0;
        }
    });

    let emptyCategory = {
        id: 'empty',
        name: '',
        type: INCOME
    };

    let lastIncomeIndex = data.findIndex(category => category.type === INCOME);
    data.splice(lastIncomeIndex, 0, emptyCategory);
    data.forEach(category => {
        if (category.name === '') {
            drawEmptyCategory(category);
        } else {
            drawCategory(category, data.length);
        }
    });
    let categories = document.querySelectorAll('.category');
    categories.forEach(function (category) {
        category.addEventListener('dragenter', handleDragEnter);
        category.addEventListener('dragleave', handleDragLeave);
        category.addEventListener('dragover', handleDragOver);
        category.addEventListener('drop', handleDrop);
    });
}

function drawEmptyCategory(category) {
    let categorySpace = document.querySelector('.categories-space');
    let newCategory = document.createElement('div');
    newCategory.className = "category";
    newCategory.style.height = '2px';
    newCategory.dataset.id = category.id;
    newCategory.dataset.name = category.name;
    newCategory.style.backgroundColor = "rgba(51,64,86,0.5)";
    newCategory.style.pointerEvents = 'none'; // Запрет наведения курсора
    categorySpace.insertAdjacentElement('beforeend', newCategory);
}

function drawCategory(category, length) {
    let categorySpace = document.querySelector('.categories-space');
    let newCategory = document.createElement('div');
    newCategory.className = "category";
    newCategory.innerText = category.name;
    newCategory.style.height = 85 / length + '%';
    newCategory.dataset.id = category.id;
    newCategory.dataset.name = category.name;
    let typeCategory;
    if (category.type === INCOME) {
        typeCategory = "Доходы";
    } else if (category.type === EXPENSE) {
        typeCategory = "Расходы";

    }
    newCategory.dataset.type = typeCategory;
    newCategory.onclick = function () {
        let body = `<h3>Информация о категории</h3>
                    <form class="modal-category" id="formModalCategory">
                   <p class="modal-category-close" href="#">X</p>
                   <a href="" class="open-merge-form">Слияние</a>
                        <div>
                            <label for="name">Наименование категории:</label>
                            <input type="text" class="input-modal-category" id="name" value="${newCategory.dataset.name}" >
                        </div>
                        
                        <div>
                            <label>Тип категории:</label>
                            <div class="select-category-type" id="selectCategoryType">
                                <p>${typeCategory}</p>
                             </div>
                         </div>
                          <div>
                            <label for="keywords">Ключевые слова категории:</label>
                            <div class="space-for-keywords">
                            <div class="keywords-list"></div>
                           </div>
                        </div>
                        <div class="space-button-edit-category">
                        <button type="button" class="button-edit-category">Сохранить</button>
                        </div>
                        </form>
                        <div class="space-merge-functional" id="spaceForMergeForm">
                        <label>Категории для слияния:</label>
                        <select class="Select-Categories-Merge" id="selectCategoryMerge">
                        </select>
                        <button type="button" class="button-merge-category" id="buttonMergeCategory">Слияние категории</button>
                        </div>`
        $('.modal-category-content').html(body)
        writeKeywordsOfCategory(getCategoryById(newCategory.dataset.id));
        drawSelectCategoryForMerge(getCategoryByType(category.type), category.id)
        $('.modal-category-fade').fadeIn()

        $('.button-delete-keyword').on("click", function (evt) {
            evt.preventDefault();
            let keywordToDeleteValue = $(this).attr('data-keywordValue');
            let keywordToDeleteId = $(this).attr('id');
            verificationToDeleteKeyword(keywordToDeleteValue, keywordToDeleteId)
        });

        $('.open-merge-form').click(function (event) {
            event.preventDefault();
            let mergeForm = document.getElementById("spaceForMergeForm");
            mergeForm.style.display = "block";
        });

        $('.button-merge-category').click(function (event) {
            event.preventDefault();
            let categoryIdForChange = $('.Select-Categories-Merge option:selected').val();
            let categoryIdForMerge = category.id;
            if (!(categoryIdForChange === '') & !(categoryIdForMerge === '')) {
                verificationToMerge(categoryIdForChange, categoryIdForMerge);
            }
        });

        $('.button-edit-category').click(function () {
            let idCategory = newCategory.dataset.id;
            let keywordsCategory = category.keywords;
            let nameValue = $('#formModalCategory').find('#name').val();
            let typeValue = category.type;

            if (!(nameValue === '') && !(keywordsCategory === '')) {
                let data = {
                    id: idCategory,
                    name: nameValue,
                    type: typeValue,
                    keywords: keywordsCategory
                }
                updateCategory(data);
                $(this).parents('.modal-category-fade').fadeOut();
                location.reload();
            }
        });

        $('.modal-category-close').click(function () {
            $(this).parents('.modal-category-fade').fadeOut();
        });
    }
    categorySpace.insertAdjacentElement('beforeend', newCategory);

}

function writeKeywordsOfCategory(category) {
    if (category.keywords.length === 0) {
        $('.keywords-list').append("Нет ключевых слов")
    }
    for (let j = 0; j < category.keywords.length; j++) {
        $('.keywords-list').append(
            `<span class="keyword-info" id="keyword-value-${category.keywords[j].name}">
                ${category.keywords[j].name}
                <button class="button-delete-keyword" 
                id="${category.keywords[j].accountId}" 
                data-keywordValue="${category.keywords[j].name}" type="button">x
             </button></span>`)
    }
}

function drawModalToAddCategory() {
    let body = `<h3>Добавление категории</h3>
                    <form class="modal-category" id="formAddCategory">
                   <p class="modal-category-close" href="#">X</p>
                        <div>
                            <label for="newCategoryName">Наименование категории:</label>
                            <p class="error-name" id="errorName" style="display:none; color:red; margin: 0 auto 10px; width: 322px;"></p>
                            <input type="text" required class="input-modal-category" id="newCategoryName">
                        </div>
                        <div>
                        <label for="newCategoryType">Выберите тип категории:</label>
                            <select class="select-modal-category" id="newCategoryType">
                                <option value="INCOME">Доходы</option>
                                <option selected value="EXPENSE">Расходы</option>
                            </select>
                        </div>
                        </form>                    
                        <div class="listOfKeywords">
                        </div>
                        <div>
                        <button type="submit" class="button-save-category">Добавить категорию</button>
                        </div>
                    </form>`
    $('.modal-category-content').html(body)
    $('.modal-category-fade').fadeIn();
    $('.modal-category-close').click(function () {
        $(this).parents('.modal-category-fade').fadeOut();
    });

    $('.button-save-category').click(function () {
        let formAddCategory = $('#formAddCategory')
        let nameValue = formAddCategory.find('#newCategoryName').val().trim();
        let typeValue = $('#newCategoryType option:selected').val();

        if (!(nameValue === '') && !(typeValue === '')) {
            let data = {
                name: nameValue,
                type: typeValue,
            }
            let checkError = createNewCategory(data)
            console.log(checkError)
            if (!checkError) {
                $(this).parents('.modal-category-fade').fadeOut();
                location.reload();
            }
        }
    });
}

function createNewCategory(category) {
    let checkError = false;
    $.ajax({
        type: 'POST',
        url: './categories/',
        headers: {
            'Content-Type': 'application/json',
        },
        data: JSON.stringify(category),
        async: false,
        dataType: 'json',
        success: function (data) {
            checkError = false;
        },
        error: function (error) {
            checkError = true
            drawCategoryNotUniqueException(error)
        }
    })
    return checkError;
}

function updateCategory(category) {
    $.ajax({
        type: 'PUT',
        url: './categories/',
        headers: {
            'Content-Type': 'application/json',
        },
        data: JSON.stringify(category),
        async: false,
        dataType: 'json',
        success: function () {
            console.log("Successfully updated categories")
        },
        error: function (error) {
            console.log(error)
        }
    })
}

function mergeCategory(mergeCategoryDTO) {
    $.ajax({
        type: 'POST',
        url: './categories/merge/',
        headers: {
            'Content-Type': 'application/json',
        },
        data: JSON.stringify(mergeCategoryDTO),
        async: false,
        dataType: 'json',
        success: function () {
            console.log("Successfully merged categories")
            location.reload();
        },
        error: function (error) {
            console.log(error)
        }
    })
}

function getCategoryByType(type) {
    let url = './categories/types/' + type;
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

function drawSelectCategoryForMerge(categories, id) {
    for (let i = 0; i < categories.length; i++) {
        if (0 === categories.length - 1 & categories[i].id === id) {
            $('#selectCategoryMerge').append($('<option>', {
                value: "",
                text: "Нет категорий для слияния"
            })).attr('disabled', 'disabled');
            continue;
        }

        if (categories[i].id === id) {
            continue;
        }
        $('#selectCategoryMerge').append($('<option>', {
            value: categories[i].id,
            text: categories[i].name
        }));
    }
}

function verificationToMerge(categoryToChangeId, categoryToMergeId) {
    let infoAboutMerge = 'Транзакции и ключевые слова из категории \"' + getCategoryById(categoryToMergeId).name +
        '\" будут перенесены в категорию \"' + getCategoryById(categoryToChangeId).name + '\", после чего первая категория' +
        ' будет удалена. '
    console.log(infoAboutMerge)
    $('.information-merge').html(infoAboutMerge);
    $('.modal-verification-merge').fadeIn();
    $('.formVerificationMerge').submit(function (e) {
        e.preventDefault();
        let mergeCategoryDTO = {
            categoryToChangeId: categoryToChangeId,
            categoryToMergeId: categoryToMergeId
        }
        mergeCategory(mergeCategoryDTO)
    })
}

function verificationToDeleteKeyword(keywordValue, keywordAccId) {
    let infoAboutMerge = 'Ключевое слово \"' + keywordValue +
        '\" будет безвозратно удалено'
    $('.information-delete-keyword').html(infoAboutMerge);
    $('.modal-verification-delete-keyword').fadeIn();
    $('.formVerificationDeleteKeyword').submit(function (e) {
        e.preventDefault();
        let keywordIDtoDelete = {
            accountId: keywordAccId,
            name: keywordValue,
        }
        deleteKeyword(keywordIDtoDelete)
    })
}

function closeModalVerification() {
    $('.modal-verification-merge').fadeOut();
}

function closeModalDeleteKeyword() {
    $('.modal-verification-delete-keyword').fadeOut();
}

function deleteKeyword(keywordId) {
    $.ajax({
        type: 'DELETE',
        url: './categories/keywords',
        headers: {
            'Content-Type': 'application/json',
        },
        data: JSON.stringify(keywordId),
        async: false,
        dataType: 'json',
        success: function () {
            console.log("Successfully deleted keyword")
            document.getElementById(String('keyword-value-' + keywordId.name)).remove()
            $('.modal-verification-delete-keyword').fadeOut();
        },
        error: function (error) {
            console.log(error)
        }
    })
}

function drawCategoryNotUniqueException(error) {
    console.log(error.responseJSON.code)
    if (error.responseJSON.code == "ORCHESTRA_CATEGORY_NAME_NOT_UNIQUE_EXCEPTION") {
        $("#errorName").text(error.responseJSON.message);
        document.getElementById("errorName").style.display = "block";
    }

}

let modal = document.getElementById('modal-add-transaction');
let sendTransactionButton = jQuery("#sendTransaction");
let span = document.getElementById('close');

function openModal() {
    modal.style.display = "block";
    $("#messageEdit").val("");
}

function closeModal() {
    modal.style.display = "none";
}

span.onclick = function () {
    closeModal();
}

sendTransactionButton.on("click", function () {
    var messageInput = $("#messageEdit");
    var message = messageInput.val();
    var currentDate = new Date();
    var currentDateTime = currentDate.toISOString();

    if (message.trim() === "") {
        alert("Сообщение не может быть пустым");
        return;
    }

    var data = {
        message: message,
        date: currentDateTime
    };

    $.ajax({
        url: "/transaction",
        type: "POST",
        contentType: "application/json",
        data: JSON.stringify(data),
        success: function (response) {
            closeModal();
            console.log(response);
            drawCustomToast('Транзакция успешно добавлена')
        },
        error: function (xhr, status, error) {
            closeModal();
            modal.style.display = "none";
            console.log(error);
            drawErrorToast(status)

        }
    });
});

window.onclick = function (event) {
    if (event.target == modal) {
        closeModal();
    }
}

function drawEmptyCircleForModal() {
    let undefinedSpace = document.querySelector('.undefined-space');
    let newCircle = document.createElement('div')
    newCircle.className = "add-transaction-circle"
    newCircle.draggable = false
    newCircle.innerText = ' ' + '+' + '\n'
    newCircle.onclick = function () {
        openModal();
    }
    setCircleDimensions(newCircle, 100, 400);
    undefinedSpace.insertAdjacentElement('beforeend', newCircle);
}






