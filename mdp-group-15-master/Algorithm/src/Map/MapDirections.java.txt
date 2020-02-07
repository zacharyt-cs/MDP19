package Map;

public enum MapDirections {

    UP, LEFT, DOWN, RIGHT;

    public static MapDirections getAntiClockwise(MapDirections currDirection) {
        return values()[(currDirection.ordinal() + 1) % values().length];
    }

    public static MapDirections getClockwise(MapDirections currDirection) {
        return values()[(currDirection.ordinal() + values().length- 1) % values().length];
    }

    public static MapDirections getOpposite(MapDirections currDirection) {
        return values()[(currDirection.ordinal() + 2) % values().length];
    }
}

