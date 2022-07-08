import 'package:web_socket_channel/web_socket_channel.dart';

class SocketServer {
  const SocketServer();

  void handler(WebSocketChannel webSocket) {
    print('Connection opened: ${webSocket.hashCode}');

    webSocket.stream.listen(
      (message) => handleMessage(webSocket, message),
      onError: handleError,
      onDone: () => handleDone(webSocket),
    );
  }

  void handleMessage(WebSocketChannel webSocket, dynamic message) {
    print('<<<[${webSocket.hashCode}] $message');
    webSocket.sink.add('echo ${message.toString().length}');
    print('>>>[${webSocket.hashCode}] ${message.toString().length}');
  }

  void handleError(Object error) {
    print('ERROR: $error');
  }

  void handleDone(WebSocketChannel webSocket) {
    print('DONE');
  }
}
