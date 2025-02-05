

import 'package:flutter/cupertino.dart';
import '../../../domain/ship/utils/classes/direction.dart';


/// Widget representing a simple ship.
///
/// - [scale] the scale of the ship
/// - [direction] the direction of navigation
/// - [isFighting] boolean value to check if the ship is fighting
///
/// If the ship is not fighting, then present the simple image,
/// else present the red circle as well to indicate the fighting state.
class ShipView extends StatelessWidget {
  final double scale;
  final Direction direction;
  final bool isFighting;
  // constructor
  const ShipView({super.key, required this.scale, required this.direction, required this.isFighting});

  @override
  Widget build(BuildContext context) => switch(isFighting) {
    false => ship(),
    true => target(ship())
  };

  /// Builds the ship with the fighting indication.
  Widget target(Widget child) => Stack(
    children: [
      Image.asset(
        'assets/images/ship/target.png',
        width: scale,
        height: scale,
      ),
      child
    ],
  );

  /// Builds the simple ship Image widget.
  Widget ship() => Image.asset(
    _getShipAsset(),
    width: scale,
    height: scale,
  );

  /// Gets the correct ship asset according to its direction.
  String _getShipAsset() => switch(direction) {
    Direction.north => 'assets/images/ship/ship_315.png',
    Direction.northEast => 'assets/images/ship/ship_0.png',
    Direction.east => 'assets/images/ship/ship_45.png',
    Direction.southEast => 'assets/images/ship/ship_90.png',
    Direction.south => 'assets/images/ship/ship_135.png',
    Direction.southWest => 'assets/images/ship/ship_180.png',
    Direction.west => 'assets/images/ship/ship_225.png',
    Direction.northWest => 'assets/images/ship/ship_270.png',
  };
}