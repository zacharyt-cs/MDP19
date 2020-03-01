package com.example.myapplication;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;

public class GridMap extends View {
    private String TAG = "GRIDMAPLOGTAG";
    private final String FROMANDROID = "\"from\":\"Android\",";

    private Context context;
    private HashMap<GRID_TYPE, Paint> gridTypeColorMap = new HashMap<>();
    private final int ROW = 20, COL = 15;
    private float gridSize;
    private Grid[][] grids;
    private int[] robotCoor = {-1, -1, -1};
    private int[] wayPointCoor = {-1, -1};

    private boolean resetMap = true;

    private String obstacleStr = "";
    private String exploredStr = "";

    private int[] curCoor = {-1, -1};
//    private int[] startCoor = {-1, -1};

    private ArrayList<int[]> imgList = new ArrayList<>();
    private String fastestPath = "";
    private boolean isAutoUpdate = true;
    private boolean manualUpdateSwitch = true;
    private static boolean validPosition = false;
    private boolean allowSetObstacle = false;
    private boolean allowSetWaypoint = false;
    private boolean allowSetStartingPoint = false;

    public GridMap(Context context) {
        super(context);
        this.context = context;
        initColor();

    }

    public GridMap(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        initColor();
        setWillNotDraw(false);
    }

    private void initColor() {
        gridTypeColorMap.put(GRID_TYPE.UNEXPLORED, getPaintWithColor(Color.GRAY));
        gridTypeColorMap.put(GRID_TYPE.EXPLORED, getPaintWithColor(Color.LTGRAY));
        gridTypeColorMap.put(GRID_TYPE.OBSTACLE, getPaintWithColor(getResources().getColor(R.color.red)));
        gridTypeColorMap.put(GRID_TYPE.WAY_POINT, getPaintWithColor(getResources().getColor(R.color.yellow)));
        gridTypeColorMap.put(GRID_TYPE.ROBOT, getPaintWithColor(getResources().getColor(R.color.turquoise)));
        gridTypeColorMap.put(GRID_TYPE.ROBOT_SPACE, getPaintWithColor(Color.LTGRAY));
        gridTypeColorMap.put(GRID_TYPE.FASTEST_PATH, getPaintWithColor(Color.GREEN));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (resetMap) {
            resetMap = false;
            this.obstacleStr = "";
            this.exploredStr = "";
            this.populateGrid();

        }
        this.clearRobotAndWaypoint();
        this.updateFastestPath();
        this.updateWaypoint();
        this.updateRobot();
        this.drawGrid(canvas);
        this.drawImg(canvas);

        this.drawGridNumber(canvas);
    }

    public int getRobotX() {
        return robotCoor[1] == -1 ? 0 : robotCoor[1] - 1;
    }

    public int getRobotY() {
        return robotCoor[0] == -1 ? 0 : getRowCoor(robotCoor[0]);
    }

    public void clearObstacle() {
        for (int x = 0; x <= ROW; x++) {
            for (int y = 0; y <= COL; y++) {
                if (grids[x][y].type == GRID_TYPE.OBSTACLE) {
                    grids[x][y].setType(GRID_TYPE.UNEXPLORED);
                }
            }
        }
    }

    //TODO:
    public void updateMap(String update) {
        ObjectMapper objectMapper = new ObjectMapper();
        Log.d(TAG, "String: " + update);
        try {
            JsonNode input = objectMapper.readTree(update);
            if (input.get("mapState") != null && manualUpdateSwitch) {
                if (!isAutoUpdate) {//if manual mode, only update once
                    manualUpdateSwitch = false;
                }
                this.clearObstacle();
                JsonNode mapState = input.get("mapState");
                if (mapState.get("obstacles") != null && mapState.get("explored") != null) {
                    this.exploredStr = mapState.get("explored").asText();
                    this.obstacleStr = mapState.get("obstacles").asText();

                    String eBitIndex = new BigInteger(exploredStr, 16).toString(2);
                    String oBitIndex = new BigInteger(obstacleStr, 16).toString(2);

                    eBitIndex = eBitIndex.substring(2, eBitIndex.length() - 2);
                    oBitIndex = String.format("%" + obstacleStr.length() * 4 + "s", oBitIndex).replace(' ', '0');
                    StringBuilder sb = new StringBuilder(eBitIndex);
                    int j = 0;
                    for (int i = 0; i < eBitIndex.length(); i++) {
                        if (eBitIndex.charAt(i) == '1') {
                            if (oBitIndex.charAt(j) == '0') {
                                sb.setCharAt(i, '0');
                            }
                            j++;
                        }
                    }
                    String bitIndex = sb.toString();
                    bitIndex = String.format("%300s", bitIndex).replace(' ', '0');
                    for (int i = 0; i < bitIndex.length(); i++) {
                        int x = 19 - (i / 15);
                        int y = (i % 15) + 1;

                        if (bitIndex.toCharArray()[i] == '1') {
                            grids[x][y].setType(GRID_TYPE.OBSTACLE);
                        }
                    }

                }
                if (mapState.get("explored") != null) {
                    this.exploredStr = mapState.get("explored").asText();
                    String bitIndex = new BigInteger(exploredStr, 16).toString(2);
                    bitIndex = bitIndex.substring(2, bitIndex.length() - 2);
                    bitIndex = String.format("%300s", bitIndex).replace(' ', '0');
                    for (int i = 0; i < bitIndex.length(); i++) {
                        int x = 19 - (i / 15);
                        int y = (i % 15) + 1;

                        if (bitIndex.toCharArray()[i] == '1') {
                            if (grids[x][y].type == GRID_TYPE.WAY_POINT || grids[x][y].type == GRID_TYPE.OBSTACLE) {
                                continue;
                            }
                            grids[x][y].setType(GRID_TYPE.EXPLORED);
                        }
                    }

                }
                if (mapState.get("robotPosition") != null) {
                    if (mapState.get("robotPosition").isArray() && mapState.get("robotPosition").size() == 3) {
                        this.updateExplored();
                        for (int i = 0; i < 3; i++) {
                            robotCoor[i] = mapState.get("robotPosition").get(i).asInt();
                        }
                        robotCoor[1] = getRowCoor(robotCoor[1]);
                        this.translateRobotCoorFromXYToRowCol();
                        //this.translateCoorFromXYToRowCol(robotCoor);
                    }
                }
                if (mapState.get("imgs") != null && mapState.get("imgs").isArray()) {
                    JsonNode imgs = mapState.get("imgs");
                    for (JsonNode img : imgs) {
                        if (img.isArray() && img.size() == 3) {
                            int[] imgContent = new int[3];
                            for (int i = 0; i < 3; i++) {
                                imgContent[i] = img.get(i).asInt();
                            }
                            imgContent[1] = getRowCoor(imgContent[1]);
                            this.translateCoorFromXYToRowCol(imgContent);
                            imgList.add(imgContent);
                        }
                    }
                }
            }
            this.invalidate();
        } catch (Exception e) {
            Log.d(TAG, "json conversion not successful bro");
            Log.d(TAG, "String:" + update);
            e.printStackTrace();
        }

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            int col = getColIndex(event.getX()); //get the column index of the grid in 2d array (this will auto +1)
            int row = getRowIndex(event.getY()); //get the row index of the grid in 2d array

            Log.d("ontoucheventtag", "col:" + col);
            Log.d("ontoucheventtag", "row:" + row);

            if (isAllowSetObstacle() || isAllowSetStartingPoint() || isAllowSetWaypoint()) {
                ToastUtil.showToast(this.context, "Selected" + "(" + (col - 1) + "," + getRowCoor(row) + ")");
            }

            if (isAllowSetObstacle()) {
                grids[row][col].setType(GRID_TYPE.OBSTACLE);
                this.invalidate();
                return true;
            } else if (isAllowSetStartingPoint()) {
                if (col - 1 < 1) {
                    col++;
                }
                if (col + 1 > 15) {
                    col--;
                }
                if (row - 1 < 0) {
                    row++;
                }
                if (row + 1 > 19) {
                    row--;
                }
                robotCoor[0] = row;
                robotCoor[1] = col;
                robotCoor[2] = 90;
                sendStartingPoint();
                this.invalidate();
                return true;
            } else if (isAllowSetWaypoint()) {
                setWayPointCoor(row, col);
                sendWaypoint();
                this.invalidate();
                return true;
            }
        }
        return false;
    }


    private void setWayPointCoor(int row, int col) {
        wayPointCoor[0] = row;
        wayPointCoor[1] = col;
        grids[row][col].setType(GRID_TYPE.WAY_POINT);
    }

    private int getRowIndex(float y) {
        return (int) (y / gridSize);
    }

    private int getColIndex(float x) { //auto +1
        return (int) (x / gridSize);
    }

    private int getRowCoor(int y) {
        return 19 - y;
    }

    private Paint getPaintWithColor(int color) {
        Paint paint = new Paint();
        paint.setColor(color);
        return paint;
    }

    private void calculateGridSize() {
        this.gridSize = getWidth() / (COL + 1); //or try COL+1
    }

    private void populateGrid() {
        this.grids = new Grid[ROW + 1][COL + 1];
        this.calculateGridSize();

        for (int x = 0; x <= ROW; x++) {
            for (int y = 0; y <= COL; y++) {
                grids[x][y] = new Grid(y * gridSize, x * gridSize, (y + 1) * gridSize - (gridSize / 30), (x + 1) * gridSize - (gridSize / 30), GRID_TYPE.UNEXPLORED);
            }
        }

        for (int i = 0; i < 9; i++) {
            int row = 17 + (i / 3);
            int col = 1 + (i % 3);
            grids[row][col].setType(GRID_TYPE.EXPLORED);
            row = 0 + (i / 3);
            col = 13 + (i % 3);
            grids[row][col].setType(GRID_TYPE.EXPLORED);
        }

    }

    public boolean isAllowSetObstacle() {
        return allowSetObstacle;
    }

    public void setAllowSetObstacle(boolean allowSetObstacle) {
        this.allowSetObstacle = allowSetObstacle;
    }

    public boolean isAllowSetWaypoint() {
        return allowSetWaypoint;
    }

    public void setAllowSetWaypoint(boolean allowSetWaypoint) {
        this.allowSetWaypoint = allowSetWaypoint;
    }

    public boolean isAllowSetStartingPoint() {
        return allowSetStartingPoint;
    }

    public void setAllowSetStartingPoint(boolean allowSetStartingPoint) {
        this.allowSetStartingPoint = allowSetStartingPoint;
    }

    private void updateFastestPath() {
        if (fastestPath != "") {
            String bitIndex = new BigInteger(fastestPath, 16).toString(2);
            bitIndex = String.format("%300s", bitIndex).replace(' ', '0');
            for (int i = 0; i < bitIndex.length(); i++) {
                int x = i / 15;
                int y = (i % 15) + 1;
                if (bitIndex.toCharArray()[i] == '1') {
                    grids[x][y].setType(GRID_TYPE.FASTEST_PATH);
                }
            }

        }
    }

    private void drawGrid(Canvas canvas) {
        for (int x = 0; x < ROW; x++) {
            for (int y = 1; y <= COL; y++) {
                Grid grid = grids[x][y];
                canvas.drawRect(grid.xStart, grid.yStart, grid.xEnd, grid.yEnd, grid.color);
            }
        }
    }

    private void drawImg(Canvas canvas) {
        if (imgList.size() == 0) {
            return;
        }
        for (int[] img : imgList) {
            if (img.length != 3) {
                continue;
            }

            int row = img[0];
            int col = img[1];

            Grid grid = grids[row][col];

            canvas.drawRect(grid.xStart, grid.yStart, grid.xEnd, grid.yEnd, getPaintWithColor(Color.RED));
            canvas.drawText(Integer.toString(img[2]), grid.xStart + (gridSize / 3f), grid.yStart + (gridSize / 1.5f), getPaintWithColor(Color.WHITE));
        }
    }

    private void drawGridNumber(Canvas canvas) {
        for (int x = 1; x <= COL; x++) {
            Grid grid = grids[20][x];
            canvas.drawText(Integer.toString(x - 1), grid.xStart + (gridSize / 3), grid.yStart + (gridSize / 3), getPaintWithColor(Color.BLACK));
        }
        for (int y = 0; y < ROW; y++) {
            Grid grid = grids[y][0];
            int index = 19 - y;
            if (index > 9) {
                canvas.drawText(Integer.toString(index), grid.xStart + (gridSize / 2), grid.yStart + (gridSize / 1.5f), getPaintWithColor(Color.BLACK));
            } else {
                canvas.drawText(Integer.toString(index), grid.xStart + (gridSize / 1.5f), grid.yStart + (gridSize / 1.5f), getPaintWithColor(Color.BLACK));
            }
        }
    }

    private void updateExplored() {
        for (int i : robotCoor) { //no robot = don't draw
            if (i < 0) {
                return;
            }
        }
        int robotStartRow = robotCoor[0] - 1;
        int robotStartCol = robotCoor[1] - 1;
        for (int i = 0; i < 9; i++) {
            grids[robotStartRow + (i / 3)][robotStartCol + (i % 3)].setType(GRID_TYPE.EXPLORED);
        }

    }

    private void updateRobot() {
        for (int i : robotCoor) { //no robot = don't draw
            if (i < 0) {
                return;
            }
        }
        int degree = robotCoor[2];

        int robotGrayArea = 0;
        switch (degree) {
            case 0:
                robotGrayArea |= 1 << 1;
                break;
            case 90:
                robotGrayArea |= 1 << 5;
                break;
            case 180:
                robotGrayArea |= 1 << 7;
                break;
            case 270:
                robotGrayArea |= 1 << 3;
                break;
        }

        int robotStartRow = robotCoor[0] - 1;
        int robotStartCol = robotCoor[1] - 1;
        if (this.checkOutOfBound(robotCoor[0], robotCoor[1])) {
            return;
        }
        for (int i = 0; i < 9; i++) {
            if ((1 << (i) & robotGrayArea) > 0) {
                grids[robotStartRow + (i / 3)][robotStartCol + (i % 3)].setType(GRID_TYPE.ROBOT_SPACE);
            } else {
                grids[robotStartRow + (i / 3)][robotStartCol + (i % 3)].setType(GRID_TYPE.ROBOT);
            }
        }
    }

    private void updateWaypoint() {
        for (int i : wayPointCoor) { //no robot = don't draw
            if (i < 0) {
                return;
            }
        }
        int row = wayPointCoor[0];
        int col = wayPointCoor[1];

        grids[row][col].setType(GRID_TYPE.WAY_POINT);
    }

    private void clearRobotAndWaypoint() {
        for (int x = 0; x < ROW; x++) {
            for (int y = 1; y <= COL; y++) {
                Grid grid = grids[x][y];
                if (grid.type == GRID_TYPE.ROBOT ||
                        grid.type == GRID_TYPE.ROBOT_SPACE ||
                        grid.type == GRID_TYPE.WAY_POINT) {
                    grids[x][y].setType(GRID_TYPE.UNEXPLORED);
                }
            }
        }
    }

    public void sendStartingPoint() {
        int x = robotCoor[1] - 1;
        int y = robotCoor[0];

        String msg = ";{" + FROMANDROID + "\"com\":\"startingPoint\",\"startingPoint\":[" + x + "," + getRowCoor(y) + "," + robotCoor[2] + "]}";
        try {
            byte[] bytes = msg.getBytes();
            BluetoothConnectionService.write(bytes);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendWaypoint() {
        int x = wayPointCoor[1] - 1;
        int y = wayPointCoor[0];
        String msg = ";{" + FROMANDROID + "\"com\":\"wayPoint\",\"wayPoint\":[" + x + "," + getRowCoor(y) + "]}";

        try {
            byte[] bytes = msg.getBytes();
            BluetoothConnectionService.write(bytes);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void promptArenaUpdate() {
        this.manualUpdateSwitch = true;
    }

    private void translateRobotCoorFromXYToRowCol() { //the one being sent is in x,y,deg, but we are using row(y),col(x),deg
        int row = robotCoor[1];
        int col = robotCoor[0] + 1;

        this.robotCoor[0] = row;
        this.robotCoor[1] = col;
    }

    private void translateCoorFromXYToRowCol(int[] coor) {
        int row = coor[1];
        int col = coor[0] + 1;

        coor[0] = row;
        coor[1] = col;
    }


    public void setAutoUpdate(boolean autoUpdate) {
        this.isAutoUpdate = autoUpdate;
        this.manualUpdateSwitch = autoUpdate;
    }

    public void clearMap() {
        this.resetMap = true;
        for (int i = 0; i < robotCoor.length; i++) {
            robotCoor[i] = -1;
        }
        for (int i = 0; i < wayPointCoor.length; i++) {
            wayPointCoor[i] = -1;
        }
        imgList.clear();
        fastestPath = "";
        this.invalidate();
    }

    public boolean move(MOVE_TYPE move) { //this method probably will be deprecated as
        if (robotCoor[0] == -1 || robotCoor[1] == -1) {
            return false;
        }

//        this.updateExplored();
        int r = 0;
        int c = 0;
        switch (move) {
            case FORWARD:
            case BACK:
                r = robotCoor[2] % 180 != 0 ? 0 : robotCoor[2] == 180 ? 1 : -1;
                c = (robotCoor[2] - 90) % 180 != 0 ? 0 : robotCoor[2] == 270 ? -1 : 1;
                if (move == MOVE_TYPE.BACK) {
                    r *= -1;
                    c *= -1;
                }
                r += robotCoor[0];
                c += robotCoor[1];
                boolean outOfBound = this.checkOutOfBound(r, c);
                return !outOfBound;
//            case TURNLEFT:
//                robotCoor[2] = (robotCoor[2] + 270) % 360;
//                break;
//            case TURNRIGHT:
//                robotCoor[2] = (robotCoor[2] + 90) % 360;
//                break;
        }

//        this.invalidate();
        return true;
    }


    public boolean checkOutOfBound(int row, int col) {
        boolean outOfBound = false;
        if (col - 1 < 1 || col + 1 > 15 || row - 1 < 0 || row + 1 > 19) {
            outOfBound = true;
        }
        return outOfBound;
    }

    public enum MOVE_TYPE {
        FORWARD,
        BACK,
        TURNLEFT,
        TURNRIGHT
    }

    public enum GRID_TYPE {
        UNEXPLORED,
        EXPLORED,
        OBSTACLE,
        WAY_POINT,
        ROBOT,
        ROBOT_SPACE,
        FASTEST_PATH
    }

    private class Grid {
        float xStart, xEnd, yStart, yEnd;
        GRID_TYPE type;
        Paint color;

        public Grid(float xStart, float yStart, float xEnd, float yEnd, GRID_TYPE type) {
            this.xStart = xStart;
            this.yStart = yStart;
            this.xEnd = xEnd;
            this.yEnd = yEnd;
            this.setType(type);
        }

        public void setType(GRID_TYPE type) {
            this.type = type;
            this.color = gridTypeColorMap.get(type);
        }
    }
}
