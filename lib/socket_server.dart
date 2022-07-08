import 'package:idlebattle_server/engine.dart';
import 'package:idlebattle_server/socket_message.dart';
import 'package:web_socket_channel/web_socket_channel.dart';

class SocketServer {
  final Engine _engine;

  const SocketServer(this._engine);

  void handler(WebSocketChannel webSocket) {
    print('OPENED   [${webSocket.hashCode}]');

    webSocket.stream.listen(
      (message) => handleMessage(webSocket, message),
      onError: handleError,
      onDone: () => handleDone(webSocket),
    );
  }

  void handleMessage(WebSocketChannel webSocket, dynamic message) {
    final SocketMessage socketMessage =
        SocketMessage(webSocket, message.toString());
    _engine.handle(socketMessage);
  }

  void handleError(Object error) {
    print('ERROR: $error');
  }

  void handleDone(WebSocketChannel webSocket) {
    print('DONE     [${webSocket.hashCode}]');
  }
}
