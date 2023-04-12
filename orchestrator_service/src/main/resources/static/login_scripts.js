function onTelegramAuth(user) {
    alert('Logged in as ' + user.first_name
        + ' ' + user.last_name + ' ('
        + user.id + (user.username ? ', @'
            + user.username : '') + ')');
}