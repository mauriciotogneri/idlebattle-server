import 'dart:io';
import 'package:idlebattle_server/socket_handler.dart';
import 'package:shelf/shelf.dart';
import 'package:shelf/shelf_io.dart';
import 'package:shelf_web_socket/shelf_web_socket.dart';

void main() {
  const SocketHandler socketHandler = SocketHandler();
  final Handler handler = webSocketHandler(socketHandler.handler);
  serve(handler, 'localhost', 8080).then((HttpServer server) {
    print('Serving at ws://${server.address.host}:${server.port}');
  });
}
