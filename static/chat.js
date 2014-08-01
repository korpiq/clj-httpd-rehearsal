var ws = new WebSocket("ws://localhost:8080/stream");
ws.onmessage = function (evt) {
  var message = JSON.parse(evt.data);
  var view = document.getElementById("chat-view");
  view.value += "\n";
  if ("id" in message && "username" in message && "message" in message) {
    view.value += message.id + " <" + message.username + ">: " + message.message;
  } else if ("error" in message) {
    view.value += "Error: " + message.error;
  } else {
    view.value += "Invalid message from server";
  }
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
    ws.send(JSON.stringify({ username: chatnick, message: field.value }));
    field.value = '';
  }
}
