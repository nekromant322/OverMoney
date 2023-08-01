let modalConfirmation = document.getElementById("modal-additional-confirmation");
let modalSuccessful = document.getElementById("modal-successful-backup");

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

$("#readButton").click(function () {
    modalConfirmation.style.display = "block";
});

function saveFile() {
    $.ajax({
        url: './settings/backup',
        method: 'GET',
        async: false,
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
