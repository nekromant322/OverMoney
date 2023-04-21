function onTelegramAuth(user) {
    let userJson = JSON.stringify(user);
    $.ajax({
        method: 'POST',
        url: '/auth/login',
        data: userJson,
        contentType: "application/json; charset=utf8",
        success: function () {
            console.log("Successfully authenticated")
        },
        error: function () {
            console.log("ERROR! Something wrong happened")
        }
    })
}