function authorize(user) {
    document.getElementById('username').value = user.username;
    document.getElementById('password').value = user.username;
    document.getElementById('submit').click();
}

function onTelegramAuth(user) {
    let userJson = JSON.stringify(user);
    $.ajax({
        method: 'POST',
        url: '/auth/telegram',
        data: userJson,
        contentType: "application/json; charset=utf8",
        success: function (result) {
            console.log("Authenticated" + result)
            authorize(user)
        },
        error: function () {
            console.log("ERROR! Something wrong happened")
        }
    })
}