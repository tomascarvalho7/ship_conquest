import 'package:flutter/material.dart';
import 'package:ship_conquest/domain/minimap.dart';
import 'dart:ui' as ui;
import 'dart:async';
import 'dart:typed_data';

import '../../../canvas/image_painter.dart';

class MinimapView extends StatelessWidget {
  final Minimap? minimap;
  final Color background = const Color.fromRGBO(0, 0, 0, 0.1);
  const MinimapView({super.key, required this.minimap});

  @override
  Widget build(BuildContext context) => Container(
      width: 350,
      height: 350,
      alignment: Alignment.center,
      child: loadMinimap()
  );

  Widget loadMinimap() {
    final curMiniMap = minimap;
    if (curMiniMap == null || curMiniMap.length == 0) return loading();

    return FutureBuilder<ui.Image>(
        future: _generateMinimapTexture(curMiniMap),
        builder: (context, snapshot) {
          if (snapshot.hasData) {
            return minimapRender(snapshot.data!, curMiniMap.length);
          } else {
            return loading();
          }
        }
    );
  }


  Widget minimapRender(ui.Image img, int size) =>
      CustomPaint(
          painter: ImagePainter(
              image: img,
              size: 325,
              length: size
          )
      );

  Widget loading() =>
      const SizedBox(
        width: 60,
        height: 60,
        child: CircularProgressIndicator(),
      );

  Future<ui.Image> _generateMinimapTexture(Minimap minimap) async {
    final int size = minimap.length;
    final completer = Completer<ui.Image>();

    Int32List pixels = Int32List(size * size);

    for(var x = 0; x < size; x++) {
      for(var y = 0; y < size; y++) {
        int index = y * size + x;
        Color? color = minimap.get(x: x, y: y);

        pixels[index] = color?.value ?? background.value;
      }
    }

    ui.decodeImageFromPixels(
        pixels.buffer.asUint8List(),
        size,
        size,
        ui.PixelFormat.bgra8888,
            (ui.Image result) {
              completer.complete(result);
            }
    );

    return completer.future;
  }
}