import 'package:shelf/shelf.dart';
import 'package:shelf_web_socket/shelf_web_socket.dart';
import 'package:web_socket_channel/web_socket_channel.dart';

class SocketHandler {
  late final WebSocketChannel webSocket;

  void handler(WebSocketChannel newWebSocket) {
    webSocket = newWebSocket;

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

  static Handler create() {
    final SocketHandler socketHandler = SocketHandler();

    return webSocketHandler(socketHandler.handler);
  }
}
