
//Скачаивание файла бекапа
$("#downloadButton").click(function() {
    $.ajax({
        url: './settings/backup',
        method: 'GET',
        success: function(data) {
            var json = JSON.stringify(data);
            var blob = new Blob([json], {type: "application/json"});
            var url  = URL.createObjectURL(blob);

            var a = document.createElement('a');

            // текущая дата в строку в формате dd.mm.yyyy
            var date = new Date();
            var dateString = [
                ('0' + date.getDate()).slice(-2),
                ('0' + (date.getMonth()+1)).slice(-2),
                date.getFullYear()
            ].join('.');

            //  имя файла с датой и другим текстом
            a.download = dateString + '-overmoney.json';
            a.href = url;
            a.click();
        }
    });
});


//Модальное окно и отпрака багрепорта
var modal = document.getElementById('bugReportModal');
var btn = document.getElementById("bugReportButton");
var span = document.getElementsByClassName("close")[0];
var sendButton = $("#sendReport");

btn.onclick = function (){
    modal.style.display = "block";
    messageInput.val("");
}

span.onclick = function (){
    modal.style.display = "none";
}

window.onclick = function (event){
    if  (event.target == modal){
        modal.style.display = "none"
    }
}

$("#messageEdit").on("keypress", function(e) {
    if (e.which === 13 && !e.shiftKey) {
        e.preventDefault();
    }
});

sendButton.on("click", function() {
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
        success: function(response) {
            modal.style.display = "none";
            console.log(response);
        },
        error: function(xhr, status, error) {
            modal.style.display = "none";
            console.log(error);
        }
    });
});