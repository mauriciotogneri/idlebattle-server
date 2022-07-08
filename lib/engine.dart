import 'package:idlebattle_server/message.dart';

class Engine {
  const Engine();

  Message? handle(Message message) {
    return const Message(event: 'YES!');
  }
}
