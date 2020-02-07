#include <EnableInterrupt.h>
#include <DualVNH5019MotorShield.h>
#include <PID_v1.h>

const int LEFT_PULSE = 11;
const int RIGHT_PULSE = 3;
const int MOVE_FAST_SPEED = 390;
const int MOVE_SLOW_SPEED = 310;

double rightTick = 0;
double leftTick = 0;
int TICKS[15] = {287, 609, 904, 1183, 1480, 1790, 2090, 2390, 2705, 2980, 3275, 3590, 3855, 4130, 4430}; //276
int TICKS_FAST[15] = {280, 599, 894, 1158, 1470, 1780, 2080, 2380, 2695, 2970, 3265, 3580, 3845, 4120, 4420};
const int LEFTTICK[14] = {20, 25, 30, 35, 40, 360, 50, 55, 489, 65, 70, 75, 80, 85};
const int RIGHTTICK[14] = {20, 25, 30, 35, 40, 313, 50, 55, 450, 65, 70, 75, 80, 85};

double left_ticks[30];
double right_ticks[30];

int TURN_TICKS_L = 379; //376 for SPL
int TURN_TICKS_L_10 = 43;
int TURN_TICKS_L_45 = 181;
int TURN_TICKS_L_1 = 4;
int TURN_TICKS_L_FAST = 378;

int TURN_TICKS_R = 378; //380 ticks for 380 speed 378 for SPL
int TURN_TICKS_R_10 = 43;
int TURN_TICKS_R_45 = 181;
int TURN_TICKS_R_1 = 4;
int TURN_TICKS_R_FAST = 376;

int numOfTimes = 0;

//initialise var for PID
double tickOffset = 0;
double previous_tick_R = 0;
double previous_error = 0;
// 11 8
//11,9.2,0
//16
//0.52
//0.425,0.08,0.002
//0.49 for long dist
//const double kp = 0.49, ki = 0.01, kd = 0;
//const double kp2 = 11, ki2 = 8, kd2 = 0;

const double kp = 11, ki = 8.75, kd = 0; //Short
//const double kp = 11, ki = 8.75, kd = 0; //Short
//const double kp = 11.5, ki = 8.75, kd = 0;
const double kp2 = 11, ki2 = 8.75, kd2 = 0; //Long

double difference = 0;

//double speed = 300.00;

DualVNH5019MotorShield md;

//Setup PID
PID ShortTurnPID(&leftTick, &tickOffset, &rightTick, kp, ki, kd, DIRECT);
PID LongPID(&leftTick, &tickOffset, &rightTick, kp2, ki2, kd2, DIRECT);

void goForward(int cm)
{
  setTicks();
  startMotor();
  int distance = cmToTicks(cm); // in ticks
  double currentSpeed = MOVE_SLOW_SPEED;
  double offset = 0;
  int last_tick_R = 0;

  while (rightTick <= distance || leftTick <= distance)
  {
    if (distance - rightTick < 150)
      currentSpeed = 100;
    
    if ((rightTick - last_tick_R) >= 10 || rightTick == 0 || rightTick == last_tick_R)
    {
      last_tick_R = rightTick;
      offset += 0.1;
    }

    if (cm <= 70)
    {
      if (ShortTurnPID.Compute() || rightTick == last_tick_R)
      {
        if (offset >= 1)
        {
          //md.setSpeeds(currentSpeed + tickOffset, currentSpeed - tickOffset);
          md.setM2Speed(currentSpeed - tickOffset);
          delay(3);
          md.setM1Speed(currentSpeed + tickOffset);
        }

        else
        {
          //md.setSpeeds(offset * (currentSpeed + tickOffset), offset * (currentSpeed - tickOffset));
          md.setM2Speed(offset * (currentSpeed - tickOffset));
          delay(3);
          md.setM1Speed(offset * (currentSpeed + tickOffset));
        }
      }
    }

    else
    {
      if (LongPID.Compute() || rightTick == last_tick_R)
      {
        if (offset >= 1)
        {
          md.setSpeeds(currentSpeed + tickOffset, currentSpeed - tickOffset);
          //md.setM2Speed(currentSpeed - tickOffset);
          //delay(2);
          //md.setM1Speed(currentSpeed + tickOffset);
        }

        else
        {
          md.setSpeeds(offset * (currentSpeed + tickOffset), offset * (currentSpeed - tickOffset));
        }
      }
    }
    //Serial.println((String)"Left ticks: " + leftTick + (String)" Right ticks: " + tick_R);
    //if(leftTick < tick_R) {
    //  difference += tick_R-leftTick;
    //} else {
    //  difference += leftTick-tick_R;
    //}
  }

  endMotor();
  //Serial.println((String)"Total left ticks: " + totalLeft + (String)" Total right ticks: " + totalRight); 
  //Serial.println((String)"Difference: " + (double)difference);
}

void goForwardFast(int cm)
{
  setTicks();
  startMotor();
  int distance = cmToTicksFast(cm); // in ticks
  double currentSpeed = MOVE_FAST_SPEED;
  double offset = 0;
  int last_tick_R = 0;

  while (rightTick <= distance || leftTick <= distance)
  {
    if (distance - rightTick < 150)
      currentSpeed = 100;
    
    if ((rightTick - last_tick_R) >= 10 || rightTick == 0 || rightTick == last_tick_R)
    {
      last_tick_R = rightTick;
      offset += 0.1;
    }

    if (cm <= 70)
    {
      if (ShortTurnPID.Compute() || rightTick == last_tick_R)
      {
        if (offset >= 1)
        {
          md.setSpeeds(currentSpeed + tickOffset, currentSpeed - tickOffset);
          //md.setM2Speed(currentSpeed - tickOffset);
          //delay(2);
          //md.setM1Speed(currentSpeed + tickOffset);
        }

        else
          md.setSpeeds(offset * (currentSpeed + tickOffset), offset * (currentSpeed - tickOffset));
      }
    }

    else
    {
      if (LongPID.Compute() || rightTick == last_tick_R)
      {
        if (offset >= 1)
          md.setSpeeds(currentSpeed + tickOffset, currentSpeed - tickOffset);

        else
          md.setSpeeds(offset * (currentSpeed + tickOffset), offset * (currentSpeed - tickOffset));
      }
    }
  }

  endMotor();
}

void goForwardTicks(int ticks)
{
  setTicks();
  startMotor();
  int distance = ticks; // in ticks
  double currentSpeed = 100;
  double offset = 0;

  while (rightTick <= distance || leftTick <= distance)
  {
    if (ShortTurnPID.Compute())
      md.setSpeeds(currentSpeed + tickOffset, currentSpeed - tickOffset);
  }

  endMotor();
}

int cmToTicks(int cm)
{
  int dist = (cm / 10) - 1;
  if (dist < 15)
    return TICKS[dist];

  return 0;
}

int cmToTicksFast(int cm)
{
  int dist = (cm / 10) - 1;
  if (dist < 15)
    return TICKS_FAST[dist];

  return 0;
}

void goBackward(int cm)
{
  setTicks();
  startMotor();
  int distance = cmToTicks(cm); // in ticks
  double currentSpeed = MOVE_SLOW_SPEED;
   
  while (rightTick <= distance || leftTick <= distance)
  {
    if (ShortTurnPID.Compute())
    {
      md.setSpeeds(-(currentSpeed + tickOffset), -(currentSpeed - tickOffset));
    }
  }

  endMotor();
}

void goBackwardFast(int cm)
{
  setTicks();
  startMotor();
  int distance = cmToTicks(cm); // in ticks
  double currentSpeed = MOVE_FAST_SPEED;
   
  while (rightTick <= distance || leftTick <= distance)
  {
    if (ShortTurnPID.Compute())
    {
      md.setSpeeds(-(currentSpeed + tickOffset), -(currentSpeed - tickOffset));
    }
  }

  endMotor();
}

void goBackwardTicks(int ticks)
{
  setTicks();
  startMotor();
  int distance = ticks; // in ticks
  double currentSpeed = 100;
  double offset = 0;

  while (rightTick <= distance || leftTick <= distance)
  {
    if (ShortTurnPID.Compute())
      md.setSpeeds(-(currentSpeed + tickOffset), -(currentSpeed - tickOffset));
  }

  endMotor();
}

void turnLeft(int angle)
{
  if (angle == 0)
    angle = 90;

  int i = 0;
  double currentSpeed = 280;

  if (angle >= 90 && angle % 90 == 0)
  {
    for (i = 0; i < angle; i += 90)
    {
      setTicks();
      startMotor();

      while (rightTick < TURN_TICKS_L || leftTick < TURN_TICKS_L)
      {
        if (ShortTurnPID.Compute())
          md.setSpeeds(-(currentSpeed + tickOffset), currentSpeed - tickOffset);
      }
    }
  }

  else if (angle % 45 == 0)
  {
    for (i = 0; i < angle; i += 45)
    {
      setTicks();
      startMotor();
      while (rightTick < TURN_TICKS_L_45 || leftTick < TURN_TICKS_L_45)
      {
        if (ShortTurnPID.Compute())
          md.setSpeeds(-(currentSpeed + tickOffset), currentSpeed - tickOffset);
      }
    }
  }

  else if (angle % 10 == 0)
  {
    for (i = 0; i < angle; i += 10)
    {
      setTicks();
      startMotor();
      while (rightTick < TURN_TICKS_L_10 || leftTick < TURN_TICKS_L_10)
      {
        if (ShortTurnPID.Compute())
          md.setSpeeds(-(currentSpeed + tickOffset), currentSpeed - tickOffset);
      }
    }
  }

  else
  {
    currentSpeed = 100;
    for (i = 0; i < angle; i++)
    {
      setTicks();
      startMotor();
      while (rightTick < TURN_TICKS_L_1 || leftTick < TURN_TICKS_L_1)
      {
        if (ShortTurnPID.Compute())
          md.setSpeeds(-(currentSpeed + tickOffset), currentSpeed - tickOffset);
      }
    }
  }
  
  endMotor();
}

void turnLeftFast(int angle)
{
  if (angle == 0)
    angle = 90;

  int i = 0;
  double currentSpeed = 300;

  if (angle >= 90 && angle % 90 == 0)
  {
    for (i = 0; i<angle; i += 90)
    {
      setTicks();
      startMotor();

      while (rightTick < TURN_TICKS_L_FAST || leftTick < TURN_TICKS_L_FAST)
      {
        if (ShortTurnPID.Compute())
          md.setSpeeds(-(currentSpeed + tickOffset), currentSpeed - tickOffset);
      }
    }
  }

  else if (angle % 45 == 0)
  {
    for (i = 0; i < angle; i += 45)
    {
      setTicks();
      startMotor();
      while (rightTick < TURN_TICKS_L_45 || leftTick < TURN_TICKS_L_45)
      {
        if (ShortTurnPID.Compute())
          md.setSpeeds(-(currentSpeed + tickOffset), currentSpeed - tickOffset);
      }
    }
  }

  else if (angle % 10 == 0)
  {
    for (i = 0; i < angle; i += 10)
    {
      setTicks();
      startMotor();
      while (rightTick < TURN_TICKS_L_10 || leftTick < TURN_TICKS_L_10)
      {
        if (ShortTurnPID.Compute())
          md.setSpeeds(-(currentSpeed + tickOffset), currentSpeed - tickOffset);
      }
    }
  }

  else
  {
    currentSpeed = 100;
    for (i = 0; i < angle; i++)
    {
      setTicks();
      startMotor();
      while (rightTick < TURN_TICKS_L_1 || leftTick < TURN_TICKS_L_1)
      {
        if (ShortTurnPID.Compute())
          md.setSpeeds(-(currentSpeed + tickOffset), currentSpeed - tickOffset);
      }
    }
  }
  
  endMotor();
}

void turnRight(int angle)
{
  if (angle == 0)
    angle = 90;

  int i = 0;
  double currentSpeed = 280; //150 Speed use 395 ticks

  if (angle >= 90 && angle % 90 == 0)
  {
    for (i=0; i < angle; i += 90)
    {
      setTicks();
      startMotor();
      while (rightTick < (TURN_TICKS_R) || leftTick < (TURN_TICKS_R))
      {
        if (ShortTurnPID.Compute())
          md.setSpeeds((currentSpeed + tickOffset), -(currentSpeed - tickOffset));
      }
    }
  }

    else if (angle % 45 == 0)
  {
    for (i = 0; i < angle; i += 45)
    {
      setTicks();
      startMotor();
      while (rightTick < TURN_TICKS_R_45 || leftTick < TURN_TICKS_R_45)
      {
        if (ShortTurnPID.Compute())
          md.setSpeeds((currentSpeed + tickOffset), -(currentSpeed - tickOffset));
      }
    }
  }

  else if (angle % 10 == 0)
  {
    for (i = 0; i < angle; i++)
    {
      setTicks();
      startMotor();
      while (rightTick < TURN_TICKS_R_1 || leftTick < TURN_TICKS_R_1)
      {
        if (ShortTurnPID.Compute())
          md.setSpeeds((currentSpeed + tickOffset), -(currentSpeed - tickOffset));
      }
    }
  }

  else
  {
    currentSpeed = 100;
    for (i = 0; i < angle; i++)
    {
      setTicks();
      startMotor();
      while (rightTick < TURN_TICKS_R_1 || leftTick < TURN_TICKS_R_1)
      {
        if (ShortTurnPID.Compute())
          md.setSpeeds((currentSpeed + tickOffset), -(currentSpeed - tickOffset));
      }
    }
  }
  
  endMotor();
}

void turnRightFast(int angle)
{
  if (angle == 0)
    angle = 90;

  int i = 0;
  double currentSpeed = 300; //150 Speed use 395 ticks

  if (angle >= 90 && angle % 90 == 0)
  {
    for (i=0; i < angle; i += 90)
    {
      setTicks();
      startMotor();
      while (rightTick < (TURN_TICKS_R_FAST) || leftTick < (TURN_TICKS_R_FAST))
      {
        if (ShortTurnPID.Compute())
          md.setSpeeds((currentSpeed + tickOffset), -(currentSpeed - tickOffset));
      }
      endMotor();
    }
  }

    else if (angle % 45 == 0)
  {
    for (i = 0; i < angle; i += 45)
    {
      setTicks();
      startMotor();
      while (rightTick < TURN_TICKS_R_45 || leftTick < TURN_TICKS_R_45)
      {
        if (ShortTurnPID.Compute())
          md.setSpeeds((currentSpeed + tickOffset), -(currentSpeed - tickOffset));
      }
    }
  }

  else if (angle % 10 == 0)
  {
    for (i = 0; i < angle; i++)
    {
      setTicks();
      startMotor();
      while (rightTick < TURN_TICKS_R_1 || leftTick < TURN_TICKS_R_1)
      {
        if (ShortTurnPID.Compute())
          md.setSpeeds((currentSpeed + tickOffset), -(currentSpeed - tickOffset));
      }
    }
  }

  else
  {
    currentSpeed = 100;
    for (i = 0; i < angle; i++)
    {
      setTicks();
      startMotor();
      while (rightTick < TURN_TICKS_R_1 || leftTick < TURN_TICKS_R_1)
      {
        if (ShortTurnPID.Compute())
          md.setSpeeds((currentSpeed + tickOffset), -(currentSpeed - tickOffset));
      }
    }
  }
  
  endMotor();
}

void setupPID()
{
  ShortTurnPID.SetMode(AUTOMATIC);
  ShortTurnPID.SetOutputLimits(-400, 400);
  ShortTurnPID.SetSampleTime(5);

  LongPID.SetMode(AUTOMATIC);
  LongPID.SetOutputLimits(-400, 400);
  LongPID.SetSampleTime(5);
}

void startEncoder()
{
  md.init();
  pinMode(LEFT_PULSE, INPUT);
  pinMode(RIGHT_PULSE, INPUT);
  enableInterrupt(LEFT_PULSE, leftMotorTicks, RISING);
  enableInterrupt(RIGHT_PULSE, rightMotorTicks, RISING);  
}

void stopEncoder()
{
  disableInterrupt(LEFT_PULSE);
  disableInterrupt(RIGHT_PULSE);
}

void leftMotorTicks()
{
  leftTick++;
}

void rightMotorTicks()
{
  rightTick++;
}

void setTicks()
{
  rightTick = 0;
  leftTick = 0;
  tickOffset = 0;
  previous_tick_R = 0;
  previous_tick_R = 0;

}

void startMotor()
{
  md.setSpeeds(0, 0);
  md.setBrakes(0, 0);
}

void endMotor()
{
  md.setSpeeds(0, 0);
  md.setBrakes(400, 400);
  delay(50);
}