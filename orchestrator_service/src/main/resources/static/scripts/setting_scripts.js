let modalConfirmation = document.getElementById("modal-additional-confirmation");
let modalSuccessful = document.getElementById("modal-successful-backup");

window.onclick = function closeModalConfirmationWhenMissClick(event) {
    if (event.target === modalConfirmation) {
        modalConfirmation.style.display = "none";
    }
}

function closeModalConfirmation() {
    modalConfirmation = document.getElementById("modal-additional-confirmation");
    modalConfirmation.style.display = "none"
}

window.onclick = function closeModalSuccessfulWhenMissClick(event) {
    if (event.target === modalSuccessful) {
        modalSuccessful.style.display = "none";
        location.reload();
    }
}

function closeModalSuccessfulBackup() {
    modalSuccessful = document.getElementById("modal-successful-backup");
    modalSuccessful.style.display = "none";
    location.reload();
}

function openModalSuccessfulBackup() {
    modalSuccessful.style.display = "block";
}

$("#downloadButton").click(function () {
    saveFile();
});
$("#downloadExelButton").click(function () {
    downloadExcel();
});

$("#readButton").click(function () {
    modalConfirmation.style.display = "block";
});
$("#subscrButton").click(function (){
    clickSubButton();
});
function clickSubButton(){
    getUserChatId(function(data){
        const apiUrl = './payments/pay/' + data;
        $.ajax({
            url: apiUrl,
            method: 'GET',
            dataType: 'text',
            timeout: 5000,
            success: function(response) {
                console.log(response);
                window.location.href = response;
            },
            error: function(xhr, status, error) {
                console.error("Ошибка:", error);
            }
        })
    })
}

function saveFile() {
    let button = event.target;
    button.disabled = true;

    let originalText = button.innerHTML;
    button.innerHTML = 'Загрузка...';
    $.ajax({
        url: './settings/backup',
        method: 'GET',
        success: function (data) {
            let json = JSON.stringify(data);
            let blob = new Blob([json], {type: "application/json"});
            let url = URL.createObjectURL(blob);

            let a = document.createElement('a');

            // текущая дата в строку в формате dd.mm.yyyy
            let date = new Date();
            let dateString = [
                ('0' + date.getDate()).slice(-2),
                ('0' + (date.getMonth() + 1)).slice(-2),
                date.getFullYear()
            ].join('.');

            //  имя файла с датой и другим текстом
            a.download = dateString + '-overmoney.json';
            a.href = url;
            a.click();

            button.disabled = false;
            button.innerHTML = originalText;
        },
        error: function(){
            button.disabled = false;
            button.innerHTML = originalText;
            alert('Ошибка при загрузке файла');
        }
    });
}
function downloadExcel() {
    let button = event.target;
    button.disabled = true;

    let originalText = button.innerHTML;
    button.innerHTML = 'Загрузка...';

    $.ajax({
        url: './settings/export/excel',
        method: 'GET',
        xhrFields: {
            responseType: 'blob'
        },
        success: function (blob) {
            let url = URL.createObjectURL(blob);

            let a = document.createElement('a');

            // текущая дата в строку в формате dd.mm.yyyy
            let date = new Date();
            let dateString = [
                ('0' + date.getDate()).slice(-2),
                ('0' + (date.getMonth() + 1)).slice(-2),
                date.getFullYear()
            ].join('.');

            // имя файла с датой и расширением .xlsx
            a.download = dateString + '-export.xlsx';
            a.href = url;
            document.body.appendChild(a);
            a.click();
            document.body.removeChild(a);
            URL.revokeObjectURL(url);

            button.disabled = false;
            button.innerHTML = originalText;
        },
        error: function (jqXHR, textStatus, errorThrown) {
            button.disabled = false;
            button.innerHTML = originalText;
            console.error('Ошибка скачивания: ', textStatus, errorThrown);
        }
    });
}

function readFile() {
    $(".loader").css("display", "block");
    let file = document.getElementById("file").files[0];
    console.log(file);

    let reader = new FileReader();
    reader.readAsText(file);

    reader.onload = function () {
        console.log(reader.result)

        fetch('./settings/backup/read', {
            method: 'POST',
            body: reader.result,
            headers: {
                'Content-type': 'application/json; charset=UTF-8',
            },
        })
            .then(response => {
                if (response.status === 413) {
                    response.json()
                        .then(data => alert(data.message));
                } else {
                    return response
                }
            })
            .then(response => response.json())
            .then((data) => {
                console.log(data)
                if (data === "ACCEPTED") {
                    closeModalConfirmation();
                    openModalSuccessfulBackup();
                } else {
                    alert("что то пошло не так")
                    closeModalConfirmation();
                    location.reload();
                }
            })
            .catch(exception => {
                console.log(exception)
            })
            .finally(() => {
                $(".loader").css("display", "none");
            })
    }
}

//Модальное окно и отпрака багрепорта
var modal = document.getElementById('bugReportModal');
var btn = document.getElementById("bugReportButton");
var span = document.getElementsByClassName("close")[0];
var sendButton = $("#sendReport");

btn.onclick = function () {
    modal.style.display = "block";
    messageInput.val("");
}

span.onclick = function () {
    modal.style.display = "none";
}

window.onclick = function (event) {
    if (event.target === modal) {
        modal.style.display = "none"
    }
}

$("#messageEdit").on("keypress", function (e) {
    if (e.which === 13 && !e.shiftKey) {
        e.preventDefault();
    }
});

sendButton.on("click", function () {
    var messageInput = $("#messageEdit");
    var report = messageInput.val();
    var currentDate = new Date();
    var currentDateTime = currentDate.toISOString();

    if (report.trim() === "") {
        alert("Сообщение не может быть пустым");
        return;
    }

    var data = {
        report: report,
        localDateTime: currentDateTime
    };

    $.ajax({
        url: "/bugreport",
        type: "POST",
        contentType: "application/json",
        data: JSON.stringify(data),
        success: function (response) {
            modal.style.display = "none";
            console.log(response);
        },
        error: function (xhr, status, error) {
            modal.style.display = "none";
            console.log(error);
        }
    });
});
$(document).ready(function() {
    const $checkbox = $('#statusCheckbox');
    const $refreshBtn = $('#refreshBtn');
    const $statusMessage = $('#statusMessage');
    const $statusTime = $('#statusTime');
    const $subscrButton =$('#subscrButton');

    let isActive = false;
    let subscriptionDate = '';

    function showTimeOfSubscription(date){
        if(!isActive) {
            $statusTime.text('Подписка не активирована');
        } else {
            $statusTime.text('Подписка действует до: ' + date);
        }
    }

    // Функция показа сообщения
    function showMessage(text, type = 'info') {
        $statusMessage
            .removeClass('success error loading warning')
            .addClass(type)
            .text(text)
            .fadeIn(300);

        if (type !== 'error') {
            setTimeout(() => {
                $statusMessage.fadeOut(300);
            }, 3000);
        }
    }

    function checkStatus() {

        $checkbox.prop('disabled', true);
        $refreshBtn.addClass('loading');
        getUserChatId(function(data) {
        const chatId = data;
        const apiUrl = './payments/subscription/' + data + '/status';

        $.ajax({
            url: apiUrl,
            method: 'GET',
            dataType: 'json',
            timeout: 5000,
            success: function(response) {
                isActive = response.active;
                console.log(isActive);
                $checkbox.prop('checked', isActive);

                if (isActive) {
                    $subscrButton.hide();
                    const dateString = response.endDate;
                    const date = new Date(dateString);
                    const day = String(date.getDate()).padStart(2, '0');
                    const month = String(date.getMonth() + 1).padStart(2, '0');
                    const year = date.getFullYear();

                    subscriptionDate = `${day}.${month}.${year}`;;
                    showTimeOfSubscription(subscriptionDate);
                    showMessage('✅ Подписка активирована', 'success');
                } else {
                    subscriptionDate = '';
                    showTimeOfSubscription('');
                    showMessage('⚠️ Подписка не активирована', 'warning');
                }

                console.log('Статус получен:', response);
            },
            error: function(xhr, status, error) {
                isActive = false;
                $checkbox.prop('checked', false);
                subscriptionDate = '';
                showTimeOfSubscription('');

                let errorText = 'Ошибка проверки: ';
                if (status === 'timeout') {
                    errorText += 'таймаут запроса';
                } else if (status === 'error') {
                    errorText += 'ошибка сети';
                } else {
                    errorText += error || 'неизвестная ошибка';
                }

                showMessage(`❌ ${errorText}`, 'error');
                console.error('Ошибка при проверке статуса:', error);
            },
            complete: function() {
                $checkbox.prop('disabled', false);
                $refreshBtn.removeClass('loading');
            }
        });
        });
    }

    checkStatus();

    $refreshBtn.click(function(e) {
        e.preventDefault();
        checkStatus();
    });

    $checkbox.click(function(e) {
        const targetState = $(this).prop('checked');

        if (targetState === true && !isActive) {
            showMessage('❌ Включение невозможно: подписка неактивна', 'error');
            $(this).prop('checked', false);
            e.preventDefault();
            return false;
        }

        if (targetState === false && isActive) {
            showMessage('⚠️ Вы отключили вкладку', 'warning');
        }

        if (targetState === true && isActive) {
            showMessage('✅ Вкладка включена', 'success');
        }

        showTimeOfSubscription(subscriptionDate || "30 dec 2025");
    });
});
function getUserChatId(callback){
    $.ajax({
        url: './users/currentChatId',
        method: 'GET',
        success: function (data) {
            callback(data);
        }
    });
}
