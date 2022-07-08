import 'dart:io';
import 'package:shelf/shelf.dart';
import 'package:shelf/shelf_io.dart' as shelf_io;
import 'package:shelf_web_socket/shelf_web_socket.dart';
import 'package:web_socket_channel/web_socket_channel.dart';

void main() {
  final Handler handler = webSocketHandler(socketHandler);
  shelf_io.serve(handler, 'localhost', 8080).then((HttpServer server) {
    print('Serving at ws://${server.address.host}:${server.port}');
  });
}

void socketHandler(WebSocketChannel webSocket) {
  print('Connection opened');

  webSocket.stream.listen(
    (message) => handleMessage(webSocket, message),
    onError: handleError,
    onDone: () => handleDone(webSocket),
  );
}

void handleMessage(WebSocketChannel webSocket, dynamic message) {
  print('<<< $message');
  webSocket.sink.add('echo ${message.toString().length}');
  print('>>> ${message.toString().length}');
}

void handleError(Object error) {
  print('ERROR: $error');
}

void handleDone(WebSocketChannel webSocket) {
  print('DONE');
}
