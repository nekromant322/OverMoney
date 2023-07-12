let modal = document.getElementById("modal-additional-confirmation");

window.onclick = function closeModalWhenMissClick(event) {
    if (event.target === modal) {
        modal.style.display = "none";
    }
}

function closeModalConfirmation() {
    modal = document.getElementById("modal-additional-confirmation");
    modal.style.display = "none"
}

$("#downloadButton").click(function () {
    saveFile();
});

$("#readButton").click(function () {
    modal.style.display = "block";
    saveFile();
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
            body: JSON.stringify(reader.result),
            headers: {
                'Content-type': 'application/json; charset=UTF-8',
            },
        })
            .then((response) => response.json())
            .then((data) => {
                console.log(data)
            })
    }
}

// function readFile(input) {
//     let file = input.files[0];
//
//     console.log(file)
//
//     let reader = new FileReader();
//
//     reader.readAsText(file);
//
//     reader.onload = function () {
//         console.log(reader.result);
//         $.ajax({
//             url: './settings/backup/read',
//             data: JSON.stringify(reader.result),
//             dataType: 'application/json',
//             processData: false,
//             contentType: "application/json; charset=utf8",
//             type: 'POST',
//             async: false,
//             success: function (data) {
//                 console.log('upload success!')
//             },
//             error: function (error) {
//                 console.log(error)
//             }
//         });
//     };
// }


// //$('#formToFile').submit(function () {
// function readFile(input) {
//     let fd = new FormData();
//     //let selectedFile;
//     if (window.FormData === undefined) {
//         alert('В вашем браузере FormData не поддерживается')
//     } else {
//         fd.append('file', $('#input')[0].files[0]);
//         // selectedFile = $('#input').get(0).files[0];
//     }
//     $.ajax({
//         url: './settings/backup/read',
//         data: fd,
//         dataType: 'json',
//         processData: false,
//         contentType: "application/json; charset=utf8",
//         type: 'POST',
//         async: false,
//         success: function (data) {
//             console.log('upload success!')
//         },
//         error: function (error) {
//             console.log(error)
//         }
//     });
// //});
// }


