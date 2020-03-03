package Robot;

import java.awt.*;

import Map.*;

public class RobotCamera {

    private int minRange;
    private int maxRange;

    private Point pos;
    private MapDirections cameraDir;

    //Constructor
    public RobotCamera(int minRange, int maxRange, int cameraRow, int cameraCol, MapDirections cameraDir) {
        this.minRange = minRange;
        this.maxRange = maxRange;
        this.pos = new Point(cameraCol, cameraRow);
        this.cameraDir = cameraDir;
    }

    // Getters and Setters
    public int getMinRange() {
        return minRange;
    }

    public void setMinRange(int minRange) {
        this.minRange = minRange;
    }

    public int getMaxRange() {
        return maxRange;
    }

    public void setMaxRange(int maxRange) {
        this.maxRange = maxRange;
    }

    public Point getPos() {
        return pos;
    }

    public void setPos(Point pos) {
        this.pos = pos;
    }

    public void setPos(int row, int col) {
        this.pos.setLocation(col, row);
    }

    public MapDirections getCameraDir() {
        return cameraDir;
    }

    public void setCameraDir(MapDirections cameraDir) {
        this.cameraDir = cameraDir;
    }

    @Override
    public String toString() {
        String s = String.format("Camera %s at %s facing %s\n", pos.toString(), cameraDir.toString());
        return s;
    }

    public void capture(Map map) {
        for (int range = minRange; range <= maxRange; range++) {
            switch (cameraDir) {
                case UP:
                    if (map.getGrid(pos.x, pos.y + range).isObstacles())

                    break;
                case RIGHT:
                    if (map.getGrid(pos.x + range, pos.y).isObstacles())

                    break;
                case DOWN:
                    if (map.getGrid(pos.x, pos.y - range).isObstacles())

                    break;
                case LEFT:
                    if (map.getGrid(pos.x -  range, pos.y).isObstacles())

                    break;
            }
        }
    }
}
