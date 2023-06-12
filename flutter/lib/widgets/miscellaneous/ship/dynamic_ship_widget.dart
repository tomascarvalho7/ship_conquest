import 'dart:math';

import 'package:flutter/cupertino.dart';
import 'package:ship_conquest/domain/ship/utils/logic.dart';
import 'package:ship_conquest/utils/constants.dart';
import 'package:ship_conquest/widgets/miscellaneous/ship/ship_view.dart';
import 'package:ship_conquest/widgets/miscellaneous/ship/utils.dart';
import '../../../domain/isometric/isometric.dart';
import '../../../domain/ship/utils/classes/direction.dart';
import '../../../domain/ship/ship.dart';
import '../../../domain/space/position.dart';

class DynamicShipWidget extends StatefulWidget {
  final Animation<double> waveAnim;
  final MobileShip ship;
  final double tileSize;
  // constructor
  const DynamicShipWidget({super.key, required this.ship, required this.waveAnim, required this.tileSize});

  @override
  State<StatefulWidget> createState() => DynamicShipWidgetState();
}

class DynamicShipWidgetState extends State<DynamicShipWidget> with TickerProviderStateMixin {
  get path => widget.ship.path;
  late final waveAnim = widget.waveAnim;
  late AnimationController controller = AnimationController(
      duration: path.getCurrentDuration(),
      vsync: this
  )..forward();
  late Animation<double> animation = Tween<double>(begin: path.getStartFromTime(), end: path.landmarks.length.toDouble()).animate(controller);
  late final scale = widget.tileSize * 4;
  late final isFighting = widget.ship.isFighting(DateTime.now());

  @override
  void didUpdateWidget(covariant DynamicShipWidget oldWidget) {
    if(oldWidget.ship != widget.ship) {
      controller = AnimationController(
          duration: path.getCurrentDuration(),
          vsync: this
      )..forward();
      animation = Tween<double>(begin: 0, end: path.landmarks.length.toDouble()).animate(controller);
    }
    super.didUpdateWidget(oldWidget);
  }

  @override
  void dispose() {
    controller.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) =>
      AnimatedBuilder(
          animation: animation,
          builder: (context, _) {
            Position position = toIsometric(path.getPosition(animation.value));
            double angle = path.getAngle(animation.value);
            Direction direction = getOrientationFromAngle(angle);
            double waveOffset = (position.x + position.y) / -3;
            return Transform.translate(
                offset: (
                    addWaveHeightToPos(position, waveAnim.value + waveOffset) - Position(x: scale / 2, y: scale / 2)
                ).toOffset(),
                child: ShipView(
                  isFighting: isFighting,
                  scale: scale,
                  direction: direction
                )
            );
          }
      );
}