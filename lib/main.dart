import 'dart:io';
import 'package:idlebattle_server/socket_server.dart';
import 'package:shelf/shelf.dart';
import 'package:shelf/shelf_io.dart';
import 'package:shelf_web_socket/shelf_web_socket.dart';

void main() {
  const SocketServer socketServer = SocketServer();
  final Handler handler = webSocketHandler(socketServer.handler);
  serve(handler, 'localhost', 8080).then((HttpServer server) {
    print('Serving at ws://${server.address.host}:${server.port}');
  });
}
