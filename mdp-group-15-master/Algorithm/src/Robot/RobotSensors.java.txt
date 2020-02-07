package Robot;

import java.awt.*;

import Map.*;

public class RobotSensors {

    private String id;
    private int minRange;
    private int maxRange;

    private Point pos;
    private MapDirections sensorDir;

    public RobotSensors(String id, int minRange, int maxRange, int sensorPosRow, int sensorPosCol, MapDirections sensorDir) {
        this.id = id;
        this.minRange = minRange;
        this.maxRange = maxRange;
        this.pos = new Point(sensorPosCol, sensorPosRow);
        this.sensorDir = sensorDir;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

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

    public int getRow() {
        return pos.y;
    }

    public int getCol() {
        return pos.x;
    }

    public void setPos(int row, int col) {
        this.pos.setLocation(col, row);
    }

    public MapDirections getSensorDir() {
        return sensorDir;
    }

    public void setSensorDir(MapDirections sensorDir) {
        this.sensorDir = sensorDir;
    }

    @Override
    public String toString() {
        String s = String.format("Sensor %s at %s facing %s\n", id, pos.toString(), sensorDir.toString());
        return s;
    }

    /**
     * detect obstacle given a real map and the sensors
     * @param map realMap for simulation
     * @return range at which it detect an obstacle. If no Obstacle detected, -1 will be return
     */
    public int detect(Map map) {

        for (int cur = minRange; cur <= maxRange; cur++) {
            switch (sensorDir) {
                case UP:
                    if (pos.y + cur == MapConstants.MAP_LENGTH)
                        return cur;
                    else if (map.getGrid(pos.y + cur, pos.x).isObstacles())
                        return cur;
                    break;
                case RIGHT:
                    if (pos.x + cur == MapConstants.MAP_WIDTH)
                        return cur;
                    else if (map.getGrid(pos.y, pos.x + cur).isObstacles())
                        return cur;
                    break;
                case DOWN:
                    if (pos.y - cur == -1)
                        return cur;
                    else if (map.getGrid(pos.y - cur, pos.x).isObstacles())
                        return cur;
                    break;
                case LEFT:
                    if (pos.x - cur == -1)
                        return cur;
                    else if (map.getGrid(pos.y, pos.x - cur).isObstacles())
                        return cur;
                    break;
            }
        }
        return -1;
    }
}
