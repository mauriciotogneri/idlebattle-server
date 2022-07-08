import 'dart:io';
import 'package:idlebattle_server/socket_handler.dart';
import 'package:shelf/shelf.dart';
import 'package:shelf/shelf_io.dart';

void main() {
  final Handler handler = SocketHandler.create();
  serve(handler, 'localhost', 8080).then((HttpServer server) {
    print('Serving at ws://${server.address.host}:${server.port}');
  });
}
