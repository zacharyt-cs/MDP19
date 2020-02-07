package Map;

import java.awt.*;

public class MapObjectSurface {

    private Point pos;
    private MapDirections surface;

    // constructor

    public MapObjectSurface(Point pos, MapDirections surface) {
        this.pos = pos;
        this.surface = surface;
    }

    // overloading

    public MapObjectSurface(int row, int col, MapDirections surface) {
        this.pos = new Point(col, row);
        this.surface = surface;
    }

    /**
     * Getters
     **/

    public Point getPos() {
        return pos;
    }

    public MapDirections getSurface() {
        return surface;
    }

    public int getRow() {
        return this.pos.y;
    }

    public int getCol() {
        return this.pos.x;
    }

    /**
     * Setters
     **/

    public void setPos(Point pos) {
        this.pos = pos;
    }

    public void setPos(int col, int row) {
        this.pos = new Point(col, row);
    }

    public void setSurface(MapDirections surface) {
        this.surface = surface;
    }

    @Override
    public String toString() {
        return String.format("%d|%d|%s", this.pos.y, this.pos.x, this.surface.toString());   // row|col|surface
    }
}
