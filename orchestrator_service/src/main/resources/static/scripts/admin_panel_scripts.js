function sendAnnounce() {
    let announceText = document.getElementById('announce_text').value;
    if(!announceText == ""){
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
}