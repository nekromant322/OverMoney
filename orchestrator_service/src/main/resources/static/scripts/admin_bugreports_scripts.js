window.onload = function () {
    getBugReportList();
}

function getBugReportList() {
    $.ajax({
        url: "./bugreports",
        type: "GET",
        async: false,
        success: function (data) {
            let table = "";

            for (i = 0; i < data.length; i++) {
                table = table + "<tr>" +
                    "<td>" + data[i].id + "</td>" +
                    "<td><a href=https://t.me/" + data[i].username + ">" + data[i].username + "</td>" +
                    "<td>" + data[i].localDateTime.replace("T", " ").slice(0, 19) + "</td>" +
                    "<td>" + data[i].report + "</td>" +
                    "<td><button onclick='deleteBugreport(" + data[i].id + ")' " +
                    "type='button' " +
                    "class='btn btn-danger' " +
                    "data-bs-toggle='modal' " +
                    "data-bs-target='#modalDeleteBugreport'>Delete</button></td>" +
                    "</tr>";
            }

            $('#bug-reports-table-body').html(table);
        },
        error: function (error) {
            console.log(error);
        }
    })
}

function deleteBugreport(id) {
    $('#modal-delete').modal({
        show: true
    })

    $.ajax({
        url: "./bugreports/" + id,
        method: "GET",
        success: function (data) {
            $('#shower-delete-id').html(data.id);
            $('#hidden-delete-id').val(data.id);
        },
        error: function (error) {
            console.log(error);
        }
    })
}

function deleteBugreportClick() {
    let id = $('#hidden-delete-id').val();

    $.ajax({
        url: "./bugreports/" + id,
        method: "DELETE",
        async: false,
        success: () => {
            location.reload();
        },
        error: function (error) {
            console.log(error);
        }
    })
}