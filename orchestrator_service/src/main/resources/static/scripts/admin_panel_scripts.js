function sendAnnounce() {
    let announceText = document.getElementById('announce_text').value;
    if (!announceText == "") {
        let text = JSON.stringify(announceText);
        $.ajax({
            method: 'POST',
            url: '/admin/announce',
            contentType: "application/json; charset=utf8",
            data: text,
            success: function () {
                console.log("Announce sent!")
            },
            error: function () {
                console.log("ERROR! Something wrong happened")
            }
        })
    }

    let statusList = getStatusMailList();
    drawStatusListOnPage(statusList);
    document.getElementById("send-announce-button").disabled = true;
}

function getStatusMailList() {
    let statusList;
    $.ajax({
        method: 'GET',
        url: '/admin/mail/status',
        async: false,
        contentType: "application/json; charset=utf8",
        success: function (data) {
            console.log("Status get success")
            statusList = data
        },
        error: function () {
            console.log("ERROR! Something wrong happened")
        }
    })
    return statusList;
}

function drawStatusListOnPage(statusList) {
    console.log(statusList)
    let successNumber = 0;
    let errorNumber = 0;
    let pendingNumber = 0;
    statusList.forEach(function (mail) {
        if (mail.statusMail == "SUCCESS") {
            successNumber = mail.countOfMails
        }
        if (mail.statusMail == "ERROR") {
            errorNumber = mail.countOfMails
        }
        if (mail.statusMail == "PENDING") {
            pendingNumber = mail.countOfMails
        }
    });
    let sumOfStatusCount = errorNumber + pendingNumber + successNumber;

        let htmlStatus = ` 
        <label for="status_all">Всего:</label>
        <input type="number" readonly id="status_all" name="status_all" value="${sumOfStatusCount}"/>
        <label for="status_success">Успешно отправлено:</label>
        <input type="number" readonly id="status_success" name="status_success" value="${successNumber}"/>
        <label for="status_error">Ошибка:</label>
        <input type="number" readonly id="status_error" name="status_error" value="${errorNumber}"/>
        <label for="status_pending">В ожидании отправки:</label>
        <input type="number" readonly id="status_pending" name="status_pending" value="${pendingNumber}"/>
   `
    $('#statusList').html(htmlStatus);

}

function updateStatusList() {
    let statusList = getStatusMailList();
    drawStatusListOnPage(statusList);
    let errorNumber = 0;
    let pendingNumber = 0;
    statusList.forEach(function (mail) {
        if (mail.statusMail == "ERROR") {
            errorNumber = mail.countOfMails
        }
        if (mail.statusMail == "PENDING") {
            pendingNumber = mail.countOfMails
        }
    });
    console.log("Ошибка " + errorNumber);
    console.log("В ожидании" + pendingNumber);
    if (errorNumber == 0 & pendingNumber == 0) {
        document.getElementById("send-announce-button").disabled = false;
    }
}