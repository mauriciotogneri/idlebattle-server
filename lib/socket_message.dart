import 'package:idlebattle_server/event.dart';
import 'package:web_socket_channel/web_socket_channel.dart';

class SocketMessage {
  final WebSocketChannel _webSocket;
  final String _message;

  const SocketMessage(this._webSocket, this._message);

  int get id => _webSocket.hashCode;

  Event get event => Event.fromString(_message);

  void reply(Object object) {
    _webSocket.sink.add('echo ${object.toString().length}');
  }
}
