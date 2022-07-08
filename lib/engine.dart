import 'package:idlebattle_server/socket_message.dart';

class Engine {
  const Engine();

  String? handle(SocketMessage message) {
    print('RECEIVED [${message.id}] ${message.event}');
    message.reply(message.event);
    print('SENT     [${message.id}] ${message.event}');
  }
}
