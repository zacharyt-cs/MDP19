package Map;

import java.awt.Point;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

public class Map {

    private final MapGrid[][] grid;
    private double percentageExplored;

    public Map() {
        grid = new MapGrid[MapConstants.MAP_LENGTH][MapConstants.MAP_WIDTH];
        initMap();
    }

    private void initMap() {
        // Init Grids on the map
        for (int row = 0; row < MapConstants.MAP_LENGTH; row++) {
            for (int col = 0; col < MapConstants.MAP_WIDTH; col++) {
                grid[row][col] = new MapGrid(new Point(col, row));

                // Init virtual wall
                if (row == 0 || col == 0 || row == MapConstants.MAP_LENGTH - 1 || col == MapConstants.MAP_WIDTH - 1) {
                    grid[row][col].setVirtualWall(true);
                }
            }
        }
        percentageExplored = 0.00;

    }

    public void resetMap() {
        initMap();
    }

    /**
     * Set the explored variable for all Grids
     * @param explored
     */
    public void setAllExplored(boolean explored) {
        for (int row = 0; row < MapConstants.MAP_LENGTH; row++) {
            for (int col = 0; col < MapConstants.MAP_WIDTH; col++) {
                grid[row][col].setExplored(explored);
            }
        }
        if (explored) {
            percentageExplored = 100.00;
        }
        else {
            percentageExplored = 0.00;
        }
    }

    /**
     * Set the moveThru variable for all Grids
     * @param moveThru
     */
    public void setAllMoveThru(boolean moveThru) {
        for (int row = 0; row < MapConstants.MAP_LENGTH; row++) {
            for (int col = 0; col < MapConstants.MAP_WIDTH; col++) {
                grid[row][col].setMoveThru(moveThru);
            }
        }
    }

    public double getPercentageExplored() {
        updateExploredPercentage();
        return this.percentageExplored;
    }

    public void setPercentageExplored(double percentage) {
        this.percentageExplored = percentage;
    }

    private void updateExploredPercentage() {
        double total = MapConstants.MAP_LENGTH * MapConstants.MAP_WIDTH;
        double explored = 0;

        for (int row = 0; row < MapConstants.MAP_LENGTH; row++) {
            for (int col = 0; col < MapConstants.MAP_WIDTH; col++) {
                if (grid[row][col].getExplored())
                    explored++;
            }
        }

        this.percentageExplored = explored / total * 100;
    }

    /**
     * Get Grid using row and col
     * @param row
     * @param col
     * @return
     */
    public MapGrid getGrid(int row, int col) {
        return grid[row][col];
    }

    /**
     * Get Grid using Point(x, y)
     * @param pos
     * @return
     */
    public MapGrid getGrid(Point pos) {
        return grid[pos.y][pos.x];
    }

    /**
     * Check if the row and col is within the Map
     * @param row
     * @param col
     * @return
     */
    public boolean checkValidGrid(int row, int col) {
        return row >= 0 && col >= 0 && row < MapConstants.MAP_LENGTH && col < MapConstants.MAP_WIDTH;
    }

    /**
     * Check if movement can be made in the rol, col
     * @param row
     * @param col
     * @return true if the Grid is valid, explored and not a virtual wall or obstacle
     */
    public boolean checkValidMove(int row, int col) {
        if(checkValidGrid(row, col)) {
            return !getGrid(row, col).getVirtualWall() && !getGrid(row, col).isObstacles() && getGrid(row,col).getExplored() && canPassThrough(getGrid(row,col));
        } else{
            return false;
        }
    }

    public boolean canPassThrough(MapGrid grid) {
        int row = grid.getPos().y;
        int col = grid.getPos().x;

        MapGrid tempGrid;

        if (getGrid(row,col).isVirtualWall())
            return false;

        for (int i = row - 1; i <= row + 1; i++){
            for (int j = col - 1; j <= col + 1; j++) {
                if (checkValidGrid(i,j)) {
                    tempGrid = getGrid(i, j);
                    if (tempGrid.isObstacles()){
                        return false;
                    }
                } else {
                    return false;
                }


            }
        }

        return true;
    }

    /**
     * Set the moveThru para of the 3x3 grids moved through by the robot
     * @param row y coordinate of the robot centre
     * @param col x coordinate of the robot centre
     */
    public void setPassThru(int row,int col) {
        for(int r = row - 1; r <= row + 1; r++) {
            for(int c = col - 1; c <= col + 1; c++) {
                grid[r][c].setMoveThru(true);
            }
        }
    }

    /**
     * Create new virtual wall around new found obstacles
     * @param obstacle Grid object of new found obstacles
     */
    public void setVirtualWall(MapGrid obstacle, boolean isVirtualWall) {
        for (int r = obstacle.getPos().y - 1; r <= obstacle.getPos().y + 1; r++) {
            for (int c = obstacle.getPos().x - 1; c <= obstacle.getPos().x + 1; c++) {
                if(checkValidGrid(r, c)) {
                    grid[r][c].setVirtualWall(isVirtualWall);
                }
            }
        }
    }

    /**
     * Get all movable neighbours Direction and Grid object
     * @param g Grid of current position
     * @return neighbours HashMap<Direction, Grid>
     */
    public ArrayList<MapGrid> getNeighbours(MapGrid g) {

        ArrayList<MapGrid> neighbours = new ArrayList<MapGrid>();
        Point up = new Point(g.getPos().x , g.getPos().y + 1);
        Point down = new Point(g.getPos().x , g.getPos().y - 1);
        Point left = new Point(g.getPos().x - 1 , g.getPos().y );
        Point right = new Point(g.getPos().x + 1 , g.getPos().y );

        // UP
        if (checkValidMove(up.y, up.x)){
            neighbours.add(getGrid(up));
        }

        // DOWN
        if (checkValidMove(down.y, down.x)) {
            neighbours.add(getGrid(down));
        }

        // LEFT
        if (checkValidMove(left.y, left.x)){
            neighbours.add(getGrid(left));
        }

        // RIGHT
        if (checkValidMove(right.y, right.x)){
            neighbours.add(getGrid(right));
        }

        return neighbours;
    }

    /**
     * Get all left neighbours Direction and Grid object
     * @param g Grid of current position
     * @return neighbours HashMap<Direction, Grid>
     */
    public MapGrid getDirNeighbour(MapGrid g, MapDirections dir) {

        int tempX = g.getPos().x;
        int tempY = g.getPos().y;

        MapGrid grid = null;
        int counter = 0;

        switch (dir){
            case UP:
                do {
                    if (checkValidGrid(tempY, tempX)) {
                        if (getGrid(tempY, tempX).getExplored())
                            if (getGrid(tempY, tempX).isObstacles()){
                                grid = getGrid(tempY, tempX);
                                counter=0;
                            } else {
                                counter++;
                            }
                        else
                            counter++;
                    }
                    tempY++;
                } while(counter!=3);

                return grid;
            case DOWN:
                do {
                    if (checkValidGrid(tempY, tempX)) {
                        if (getGrid(tempY, tempX).getExplored())
                            if (getGrid(tempY, tempX).isObstacles()){
                                grid = getGrid(tempY, tempX);
                                counter=0;
                            } else {
                                counter++;
                            }
                        else
                            counter++;
                    }
                    tempY--;
                } while(counter!=3);

                return grid;
            case LEFT:
                do {
                    if (checkValidGrid(tempY, tempX)) {
                        if (getGrid(tempY, tempX).getExplored())
                            if (getGrid(tempY, tempX).isObstacles()){
                                grid = getGrid(tempY, tempX);
                                counter=0;
                            } else {
                                counter++;
                            }
                        else
                            counter++;
                    }
                    tempX--;
                } while(counter!=3);

                return grid;
            case RIGHT:
                do {
                    if (checkValidGrid(tempY, tempX)) {
                        if (getGrid(tempY, tempX).getExplored())
                            if (getGrid(tempY, tempX).isObstacles()){
                                grid = getGrid(tempY, tempX);
                                counter=0;
                            } else {
                                counter++;
                            }
                        else
                            counter++;
                    }
                    tempX++;
                } while(counter!=3);

                return grid;
        }
        return grid;
    }

    /**
     * Get all left neighbours Direction and Grid object
     * @param g Grid of current position
     * @return neighbours HashMap<Direction, Grid>
     */
    public boolean isStaircase(MapGrid g) {

        boolean isStaircase = false;
        boolean isStaircase2 = true;


        int tempX = g.getPos().x;
        int tempY = g.getPos().y;

        if (checkValidGrid(tempY, tempX)){
            if(getGrid(tempY,tempX).isObstacles()){
                if (checkValidGrid(tempY+1, tempX+1))
                   isStaircase = isStaircase || getGrid(tempY+1,tempX+1).isObstacles();
                if (checkValidGrid(tempY+1, tempX-1))
                    isStaircase = isStaircase ||  getGrid(tempY+1,tempX-1).isObstacles();
                if (checkValidGrid(tempY-1, tempX+1))
                    isStaircase = isStaircase ||  getGrid(tempY-1,tempX+1).isObstacles();
                if (checkValidGrid(tempY-1, tempX+1))
                    isStaircase = isStaircase || getGrid(tempY-1,tempX-1).isObstacles();

                if (checkValidGrid(tempY+1, tempX))
                    isStaircase = isStaircase && !getGrid(tempY+1,tempX).isObstacles();
                if (checkValidGrid(tempY, tempX+1))
                    isStaircase = isStaircase && !getGrid(tempY,tempX+1).isObstacles();
                if (checkValidGrid(tempY-1, tempX))
                    isStaircase = isStaircase && !getGrid(tempY-1,tempX).isObstacles();
                if (checkValidGrid(tempY, tempX+1))
                    isStaircase = isStaircase && !getGrid(tempY,tempX+1).isObstacles();


                return isStaircase;
            }
        }

        return false;
    }
    /**
     * Get all movable neighbours Direction and Grid object
     * @param g Grid of current position
     * @return neighbours HashMap<Direction, Grid>
     */
    public HashMap<MapDirections, MapGrid> getNeighboursMap(MapGrid g) {

        HashMap<MapDirections, MapGrid> neighbours = new HashMap<MapDirections, MapGrid>();
        Point up = new Point(g.getPos().x , g.getPos().y + 1);
        Point down = new Point(g.getPos().x , g.getPos().y - 1);
        Point left = new Point(g.getPos().x - 1 , g.getPos().y );
        Point right = new Point(g.getPos().x + 1 , g.getPos().y );

        // UP
        if (checkValidGrid(up.y, up.x)){
            neighbours.put(MapDirections.UP, getGrid(up));
        }

        // DOWN
        if (checkValidGrid(down.y, down.x)) {
            neighbours.put(MapDirections.DOWN, getGrid(down));
        }

        // LEFT
        if (checkValidGrid(left.y, left.x)){
            neighbours.put(MapDirections.LEFT, getGrid(left));
        }

        // RIGHT
        if (checkValidGrid(left.y, right.x)){
            neighbours.put(MapDirections.RIGHT, getGrid(right));
        }

        return neighbours;
    }


    /**
     * Check if wayPoint is valid to move there cannot move to virtual wall
     * @param row
     * @param col
     * @return true if the way point is not a virtual wall or obstacle (unreachable)
     */
    public boolean wayPointClear(int row, int col) {
        return checkValidGrid(row, col) && !getGrid(row, col).getVirtualWall() && !getGrid(row, col).isObstacles();
    }

    /**
     * Check whether a particular grid is clear for robot to move through
     * @param row
     * @param col
     * @return true if the grid's left and right are valid explored non-obstacle grid
     */
    public boolean clearForRobot(int row, int col) {
        for (int r = row - 1; r <= row + 1; r++) {
            for (int c = col - 1; c <= col + 1; c++) {
                if (!checkValidGrid(r,c) || !grid[r][c].getExplored() || grid[r][c].isObstacles())
                    return false;
            }
        }
        return true;
    }

    /**
     * Return the nearest unexplored Grid from a location
     * @param loc Point location
     * @return nearest unexplored Grid, null if there isnt one
     */
    public MapGrid nearestUnexplored(Point loc) {
        double dist = 1000, tempDist;
        MapGrid nearest = null, tempGrid;

        for (int row = 0; row < MapConstants.MAP_LENGTH; row++) {
            for (int col = 0; col < MapConstants.MAP_WIDTH; col++) {
                tempGrid = grid[row][col];
                tempDist = loc.distance(tempGrid.getPos());
                if ((!tempGrid.getExplored()) && (tempDist < dist)) {
                    nearest = tempGrid;
                    dist = tempDist;
                }
            }
        }
        return nearest;
    }

    /**
     * Get all movable neighbours Direction and Grid object
     * @param pos position of the obstacle
     * @param surfDir Surface direction
     * @return neighbour grid point of that surfDir
     */
    public Point getNeighbour(Point pos, MapDirections surfDir) {

        Point n = null;

        switch (surfDir) {
            case UP:
                n = new Point(pos.x , pos.y + 1);
                break;
            case LEFT:
                n = new Point(pos.x - 1, pos.y);
                break;
            case DOWN:
                n = new Point(pos.x, pos.y - 1);
                break;
            case RIGHT:
                n = new Point(pos.x + 1, pos.y);
                break;
        }
        return n;
    }

    public MapObjectSurface nearestObsSurface(Point loc, HashMap<String, MapObjectSurface> notYetTaken, Map currentMap) {
        double dist = 1000, tempDist;
        Point tempPos;
        MapObjectSurface nearest = null;

        for (MapObjectSurface obstacle: notYetTaken.values()) {
            // neighbour grid of that surface
            if (obstacle.getSurface() == MapDirections.LEFT) {
                tempPos = getNeighbour(obstacle.getPos(), obstacle.getSurface());
                tempDist = loc.distance(tempPos);
                if (tempDist < dist) {
                    dist = tempDist;
                    nearest = obstacle;
                }
            }

        }

//        if (currentMap.checkValidGrid(nearest.getPos().y,  nearest.getPos().x+2)) {
//            if (currentMap.getGrid(nearest.getPos().y, nearest.getPos().x+2).isObstacles()) {
//                nearest = new MapObjectSurface(nearest.getPos().x, nearest.getPos().x+1, MapDirections.UP);
//            }
//        }

        return nearest;
    }


    public MapGrid nearestMovable(MapObjectSurface obsSurface) {
        double distance = 1000, tempDist;
        MapGrid nearest = null;
        MapGrid tempGrid;
        int rowInc = 0, colInc = 0;
        int obsRow = obsSurface.getRow();
        int obsCol = obsSurface.getCol();
        switch (obsSurface.getSurface()) {
            case UP:
                rowInc = 1;
                colInc = 0;
                for (int row = obsRow + 2 * rowInc; row <= obsRow + 3 * rowInc; row++) {
                    for (int col = obsCol - 1; col <= obsCol + 1; col++) {
                        if (checkValidMove(row, col)) {
                            tempGrid = grid[row][col];
                            tempDist = obsSurface.getPos().distance(tempGrid.getPos());
                            if (distance > tempDist) {
                                nearest = tempGrid;
                                distance = tempDist;
                            }
                        }
                    }
                }
                break;
            case DOWN:
                rowInc = -1;
                colInc = 0;
                for (int row = obsRow + 2 * rowInc; row >= obsRow + 3 * rowInc; row--) {
                    for (int col = obsCol - 1; col <= obsCol + 1; col++) {
                        if (checkValidMove(row, col)) {
                            tempGrid = grid[row][col];
                            tempDist = obsSurface.getPos().distance(tempGrid.getPos());
                            if (distance > tempDist) {
                                nearest = tempGrid;
                                distance = tempDist;
                            }
                        }
                    }
                }
                break;
            case LEFT:
                colInc = -1;
                rowInc = 0;
                for (int col = obsCol + 2 * colInc; col >= obsCol + 3 * colInc; col--) {
                    for (int row = obsRow - 1; row <= obsRow + 1; row++) {
                        if (checkValidMove(row, col)) {
                            tempGrid = grid[row][col];
                            tempDist = obsSurface.getPos().distance(tempGrid.getPos());
                            if (distance > tempDist) {
                                nearest = tempGrid;
                                distance = tempDist;
                            }
                        }
                    }
                }
                break;
            case RIGHT:
                colInc = 1;
                rowInc = 0;
                for (int col = obsCol + 2 * colInc; col <= obsCol + 3 * colInc; col++) {
                    for (int row = obsRow - 1; row <= obsRow + 1; row++) {
                        if (checkValidMove(row, col)) {
                            tempGrid = grid[row][col];
                            tempDist = obsSurface.getPos().distance(tempGrid.getPos());
                            if (distance > tempDist) {
                                nearest = tempGrid;
                                distance = tempDist;
                            }
                        }
                    }
                }
                break;
        }

        return nearest;
    }


    public MapGrid nearestMovableOld(Point obsLoc) {
        double distance = 1000, tempdist;
        MapGrid nearest = null;
        MapGrid tempGrid;

        int obsRow = obsLoc.y;
        int obsCol = obsLoc.x;

        for (int row = obsRow - 2; row <= obsRow + 2; row++) {
            for (int col = obsCol - 3; col <= obsCol + 3; col++) {
                if (checkValidGrid(row, col)) {
                    tempGrid = grid[row][col];
                    if (checkValidMove(row, col) && clearForRobot(row, col)) {
//                    if ((distance > obsLoc.distance(tempCell.getPos()) && tempCell.getPos().distance(botLoc) > 0)) {       // actually no need to check for botLoc
                        if (distance > obsLoc.distance(tempGrid.getPos())) {       // actually no need to check for botLoc
                            nearest = tempGrid;
                            distance = obsLoc.distance(tempGrid.getPos());
                        }
                    }
                }

            }
        }
        return nearest;
    }



    /**
     * Return the nearest explored but not move through grid given the nearest unexplored grid
     * @param targetLoc nearest unexplored point location
     * @param botLoc location of the robot
     * @return nearest explored Grid, null if there isn't one
     */
    public MapGrid nearestExplored(Point targetLoc, Point botLoc) {
        MapGrid cell, nearest = null;
        double distance = 1000;
        double botDistance = 1000;

        for (int row = targetLoc.y; row >= 1; row-=2) {
            for (int col = targetLoc.x; col >= 1; col-=2) {
                cell = grid[row][col];
                if (checkValidMove(row, col) && clearForRobot(row, col) && notAreaMoveThru(row, col)) {
                    if ((distance > targetLoc.distance(cell.getPos()) && cell.getPos().distance(botLoc) < botDistance)) {
                        nearest = cell;
                        distance =  targetLoc.distance(cell.getPos());
                        botDistance = cell.getPos().distance(botLoc);
                    }
                }
            }
        }
        return nearest;
    }

    /**
     * Check whether the entire area was moved through by the robot
     * @param row
     * @param col
     * @return true if the grid, its left or its right has not been move through by the robot
     */
    public boolean notAreaMoveThru(int row, int col) {
        for (int r = row - 1; r <= row + 1; r++) {
            for (int c = col - 1; c <= col + 1; c++) {
                if (!grid[r][c].getMoveThru()) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Remove existing grid with path
     */
    public void removeAllPaths() {
        for (int r = 0; r < MapConstants.MAP_LENGTH; r++) {
            for (int c = 0; c < MapConstants.MAP_WIDTH; c++) {
                grid[r][c].setPath(false);
            }
        }
    }

    /**
     * Get the moving direction from point A to point B. (provided A or B has same x or y)
     * Assuming A and B are not the same point.
     * @param A
     * @param B
     * @return
     */
    public MapDirections getGridDir(Point A, Point B) {
        if (A.y - B.y > 0) {
            return MapDirections.DOWN;
        }
        else if (A.y - B.y < 0) {
            return MapDirections.UP;
        }
        else if (A.x - B.x > 0) {
            return MapDirections.LEFT;
        }
        else {
            return MapDirections.RIGHT;
        }
    }

    /**
     * reinit virtual wall when removing phanton blocks
     */
    public void reinitVirtualWall() {
        for (int row = 0; row < MapConstants.MAP_LENGTH; row++) {
            for (int col = 0; col < MapConstants.MAP_WIDTH; col++) {
                // Init Virtual wall
                if (row == 0 || col == 0 || row == MapConstants.MAP_LENGTH - 1 || col == MapConstants.MAP_WIDTH - 1) {
                    grid[row][col].setVirtualWall(true);
                }
                if (grid[row][col].isObstacles()) {
                    for (int r = row - 1; r <= row + 1; r++)
                        for (int c = col - 1; c <= col + 1; c++)
                            if (checkValidGrid(r, c))
                                grid[r][c].setVirtualWall(true);
                }
            }
        }
    }
}
