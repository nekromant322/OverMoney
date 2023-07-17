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

function readFile(input) {
    let file = input.files[0];
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
            .then((response) => response.json())
            .then((data) => {
                console.log(data)
            })
            .then((closeModalConfirmation))
          //  .then(location.reload())
            .then((openModalSuccessfulBackup))

    }
}



