package Robot;

public enum RoboCmd {
    FORWARD, BACKWARD, LEFT_TURN, RIGHT_TURN, SEND_SENSORS, TAKE_IMG,  ALIGN_FRONT, ALIGN_RIGHT, INIT_CAL, FAST_FORWARD, FAST_BACKWARD, ERROR, START_EXP, ENDEXP, START_FAST, ENDFAST, ROBOT_POS;


    public enum AndroidCtrl{
        front, back, left, right
    }

    public enum ArduinoCtrl{
        W, S, A, D, K, I, F, R, N,
    }
}
