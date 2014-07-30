var ws = new WebSocket("ws://localhost:8080/stream");
ws.onmessage = function (evt) {
  var message = evt.data;
  var view = document.getElementById("chat-view");
  view.value += "\n" + message;
  view.scrollTop = view.scrollHeight;
}

var chatnick = "";
function setNameFrom(field) {
  if (field.value) {
    chatnick = field.value;
    field.disabled = "disabled";
    document.getElementById('talk').disabled = false;
  }
}

function sendOnEnter(evt, field) {
  return evt.keyCode != 13 || sendValueFrom(field);
}

function sendValueFrom(field) {
  if (chatnick && field.value) {
    ws.send(chatnick + ': ' + field.value);
    field.value = '';
  }
}
