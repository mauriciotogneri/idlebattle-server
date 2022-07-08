import 'dart:convert';
import 'package:idlebattle_server/engine.dart';
import 'package:idlebattle_server/message.dart';
import 'package:web_socket_channel/web_socket_channel.dart';

class SocketServer {
  final Engine _engine;

  const SocketServer(this._engine);

  void handler(WebSocketChannel webSocket) {
    print('OPENED   [${webSocket.hashCode}]');

    webSocket.stream.listen(
      (message) => handleMessage(webSocket, message.toString()),
      onError: handleError,
      onDone: () => handleDone(webSocket),
    );
  }

  void handleMessage(WebSocketChannel webSocket, String input) {
    final Message message = Message.fromJson(jsonDecode(input.toString()));

    print('RECEIVED [${webSocket.hashCode}] $input');

    final Message? response = _engine.handle(message);

    if (response != null) {
      final String output = jsonEncode(response.toJson());
      webSocket.sink.add(output);
      print('SENT     [${webSocket.hashCode}] $output');
    }
  }

  void handleError(Object error) {
    print('ERROR: $error');
  }

  void handleDone(WebSocketChannel webSocket) {
    print('DONE     [${webSocket.hashCode}]');
  }
}
