import 'package:idlebattle_server/engine.dart';
import 'package:idlebattle_server/event.dart';
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

  void handleMessage(WebSocketChannel webSocket, String message) {
    final Event event = Event.fromString(message.toString());

    print('RECEIVED [${webSocket.hashCode}] $message');

    final String? response = _engine.handle(event);

    if (response != null) {
      webSocket.sink.add(response);
      print('SENT     [${webSocket.hashCode}] $response');
    }
  }

  void handleError(Object error) {
    print('ERROR: $error');
  }

  void handleDone(WebSocketChannel webSocket) {
    print('DONE     [${webSocket.hashCode}]');
  }
}
