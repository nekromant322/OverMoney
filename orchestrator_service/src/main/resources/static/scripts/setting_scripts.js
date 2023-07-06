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