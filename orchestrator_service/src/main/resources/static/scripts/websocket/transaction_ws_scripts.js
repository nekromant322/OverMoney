ws = new WebSocket("ws://localhost:8081/transactions-ws"); // "ws://" + location.host + "/transactions-ws"

ws.onopen = function () {
    console.log("Соединение по websocket произошло.");
    sendRequestToServer()
}

//TODO Получаем сообщение от сервера
ws.onmessage = function (ev) {
    console.log(ev);
    action(ev.data);
}

ws.onerror = function (ev) {}
ws.onclose = function (ev) {}

function sendRequestToServer() {
    ws.send("");
}

function action(message) {
    console.log("зашли в action и вывели сообщение:");
    console.log(message);
}