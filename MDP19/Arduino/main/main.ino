String toSend = "";
String command = "";
int forwarded = 0;
int turned = 0;

void setup()
{
  setupSerialConnection();
  startEncoder();
  setupPID();
  
  delay(100);
  //calibrateStart();
}

void loop()
{
  Serial.setTimeout(50);

  //printSensors();
  //delay(1000);

  //fullSystemCheck();

  //Serial.println(getFrontLeftDirect());
  //Serial.println(getFrontRightDirect());
  //Serial.println(getRightFrontDirect());
  //Serial.println(getRightBackDirect());

  String message = "";
  String com = "";
  if (Serial.available() > 0)
  {
    message = Serial.readString();
    //message.remove(message.length() - 1);
    if (message.charAt(0) == 'K')
      sendSensor();

    else if (message.charAt(0) == 'F')
    {
      calibrateFront();
      sendFin();
    }

    else if (message.charAt(0) == 'R')
    {
      parallelWall();
      sendFin();
    }

    else if (message.charAt(0) == 'E')
    {
      finExp();
    }

    else if (message.charAt(0) == 'C')
    {
      calibrate();
      sendFin();
    }

    else if (message.charAt(0) == 'W')
    {
      goForward(10);
      //if (checkRight()) parallelWall();
      //if (forwarded > 4) calibrate();
      //delay(500);
      sendSensor();
      //toSend = ";{\"from\":\"Arduino\",\"com\":\"SD\",\"fr\":-1,\"fl\":-1,\"fm\":-1,\"rf\":-1,\"rb\":-1,\"left\":-1}";
      //Serial.println(toSend);
    }

    else if (message.charAt(0) == 'S')
    {
      goBackward(10);
      //toCalFront();
      //toCalRight();
      //delay(500);
      sendSensor();
      //toSend = ";{\"from\":\"Arduino\",\"com\":\"SD\",\"fr\":-1,\"fl\":-1,\"fm\":-1,\"rf\":-1,\"rb\":-1,\"left\":-1}";
      //Serial.println(toSend);
    }

    else if (message.charAt(0) == 'A')
    {      
      //calibrate();
      //distanceFront();
      //delay(250);

      turnLeft(90);
      //if (checkRight()) parallelWall();
      //delay(500);
      //if (turned >= 4) calibrate();
      sendSensor();
      //toSend = ";{\"from\":\"Arduino\",\"com\":\"SD\",\"fr\":-1,\"fl\":-1,\"fm\":-1,\"rf\":-1,\"rb\":-1,\"left\":-1}";
      //Serial.println(toSend);
    }

    else if (message.charAt(0) == 'D')
    {
      turnRight(90);
      //if (checkRight()) parallelWall();
      //delay(500);
      //if (turned >= 4) calibrate();
      sendSensor();
      //toSend = ";{\"from\":\"Arduino\",\"com\":\"SD\",\"fr\":-1,\"fl\":-1,\"fm\":-1,\"rf\":-1,\"rb\":-1,\"left\":-1}";
      //Serial.println(toSend);
    }

    else if (message.charAt(0) == 'G')
    {
      delay(15000);
      turnLeft(90);
      parallelWall();
      distanceFront();
      lineFront();
      turnRight(90);
      distanceFront();
      lineFront();
      turnRight(90);

      //if (checkRight()) parallelWall();
      //delay(500);
      //if (turned >= 4) calibrate();
    }

    else if (message.charAt(0) == 'H')
    {
      delay(15000);
      parallelWall();
      distanceFront();
      lineFront();
      turnRight(90);
      distanceFront();
      lineFront();
      turnRight(90);
      //if (checkRight()) parallelWall();
      //delay(500);
      //if (turned >= 4) calibrate();
    }

    else if (message.charAt(0) == 'w' || message.charAt(0) == 's' || message.charAt(0) == 'a' || message.charAt(0) == 'd')
      splitStringToAction(message);
  }
}

void calibrate()
{
  /*if (checkFront() && checkRight())
  {
    distanceFront();
    lineFront();
    turnRight(90);
    distanceFront();
    lineFront();
    turnLeft(90);
    delay(100);
    turnLeft(90);
    parallelWall();
    delay(100);
    turnRight(90);
    forwarded = 0;
    turned = 0;
  }*/
  //if (checkRight())
 // {
    parallelWall();
    delay(250);
    turnRight(90);
    delay(250);
    distanceFront();
    delay(250);
    lineFront();
    delay(250);
    turnLeft(90);
    parallelWall();
  //}
}

void calibrateFront()
{
    distanceFront();
    delay(100);
    lineFront();
    delay(100);
}

void calibrateStart()
{
  if (checkFront() && checkRight())
  {
    distanceFront();
    delay(500);
    lineFront();
    delay(500);
    turnRight(90);
    delay(500);
    distanceFront();
    delay(500);
    lineFront();
    delay(500);
    turnLeft(90);
    delay(500);
    turnLeft(90);
    delay(500);
    parallelWall();
  }
}

void finExp()
{
  if (checkRight())
  {
    lineFront();
    delay(250);
    distanceFront();
    delay(250);
    parallelWall();
    delay(250);
    turnRight(90);
    delay(250);
    lineFront();
    delay(250);
    distanceFront();
    delay(250);
    turnRight(90);
  }

  else
  {
    turnLeft(90);
    delay(250);

    lineFront();
    delay(250);
    distanceFront();
    delay(250);
    parallelWall();
    delay(250);
    turnRight(90);
    delay(250);
    lineFront();
    delay(250);
    distanceFront();
    delay(250);
    turnRight(90);
  }
  
  toSend = ";{\"from\":\"Arduino\",\"com\":\"E\"}";

  Serial.println(toSend);
  Serial.flush();
}

bool checkFront()
{
  if (getFrontLeftDirect() < 81 && getFrontRightDirect() < 81)
    return true;

  else
    return false;
}

bool checkRight()
{
  //Edit these values...
  if (getRightFrontDirect() < 130 && getRightBackDirect() < 130)
    return true;

  else
    return false;
}

void sendSensor()
{
  //delay(1000);
  toSend = ";{\"from\":\"Arduino\",\"com\":\"SD\",\"fr\":";
  toSend.concat(getFrontRight());
  toSend.concat(",\"fl\":");
  toSend.concat(getFrontLeft());
  toSend.concat(",\"fm\":");
  toSend.concat(getMiddle());
  toSend.concat(",\"left\":");
  toSend.concat(getLRDistance());
  //toSend.concat("-1");
  toSend.concat(",\"rf\":");
  toSend.concat(getRightFront());
  //toSend.concat("-1");
  toSend.concat(",\"rb\":");
  toSend.concat(getRightBack());
  //toSend.concat("-1");
  toSend.concat("}");

  Serial.println(toSend);
  Serial.flush();
}

void sendFin()
{
  toSend = ";{\"from\":\"Arduino\",\"com\":\"C\"}";

  Serial.println(toSend);
  Serial.flush();
}

void setupSerialConnection()
{
  Serial.begin(115200);
}

void sharpAvoidance()
{
  while (true)
  {
    goForward(10);

    if ((getFrontLeft() == 1))
    {
      delay(500);
      turnRight(90);
      goForward(20);
      delay(500);
      turnLeft(90);
      delay(500);
      goForward(50);
      turnLeft(90);
      delay(500);
      goForward(20);
      delay(500);
      turnRight(90);
    }

    else if (getFrontRight() == 1)
    {
      delay(500);
      turnLeft(90);
      goForward(20);
      delay(500);
      turnRight(90);
      delay(500);
      goForward(50);
      turnRight(90);
      delay(500);
      goForward(20);
      delay(500);
      turnLeft(90);
    }
  }
}

void tiltAvoidance()
{
  while (true)
  {
    goForward(10);

    if (getFrontLeft() == 1)
    {
      delay(500);
      turnRight(45);
      goForward(20);
      delay(500);
      turnLeft(45);
      delay(500);
      goForward(30);
      delay(500);
      turnLeft(45);
      delay(500);
      goForward(20);
      delay(500);
      turnRight(45);
    }

    else if (getFrontRight() == 1)
    {
      delay(500);
      turnLeft(45);
      goForward(20);
      delay(500);
      turnRight(45);
      delay(500);
      goForward(30);
      delay(500);
      turnRight(45);
      delay(500);
      goForward(20);
      delay(500);
      turnLeft(45);
    }
  }
}

//Kinda works
void parallelWall()
{
  int cycles = 0;

  int rf = getRightFrontDirect();
  int rb = getRightBackDirect();

  double diff = rf - rb; //Calculate offset for direct values

  while (abs(diff) > 4) //Was 5
  {
    if (cycles >= 18)
      break;

    /*if (rf > 110)
    {
      turnRight(4);
      break;
    }

    if (rf < 100)
    {
      turnLeft(4);
      break;
    }*/

    if (rf >= rb)
      //gyrateRight(abs((diff/10) * 8), abs((diff/10)) / (diff/10) * -1);
      turnRight(1);
    
    else
      //gyrateLeft(abs((diff/10) * 8), abs((diff/10)) / (diff/10) * -1);
      turnLeft(1);

    rf = getRightFrontDirect();
    rb = getRightBackDirect();
    diff = rf - rb; //Apply offsets here too
    cycles++;
  }
}

//Kinda works
void lineFront()
{
  int cycles = 0;

  int fr = getFrontRightDirect();
  int fl = getFrontLeftDirect();

  double diff = fr - fl; //Calculate offset for direct values

  while (abs(diff) >= 1) //Was 3
  {
    if (cycles >= 6)
      break;

    if (fr >= fl)
      turnLeft(1);
      //gyrateRight(abs((diff/10) * 8), abs((diff/10)) / (diff/10) * -1);
    
    else
      //gyrateLeft(abs((diff/10) * 8), abs((diff/10)) / (diff/10) * -1);
      turnRight(1);

    int fr = getFrontRightDirect();
    int fl = getFrontLeftDirect();

    double diff = fr - fl; //Calculate offset for direct values

    if (abs(diff) <= 6)
      break;

    cycles++;
  }

  distanceFront();
}

//Change the raw value for the calibration
void distanceFront()
{
  if (getFrontRightDirect() != 100)
  {
    //for (int i = 0; i < 10; i++)
    while (true)
    {
      if (getFrontRightDirect() == 100)
        break;
      
      else if (getFrontRightDirect() < 100)
        goBackwardTicks(5);

      else if (getFrontRightDirect() > 100)
        goForwardTicks(5);
    }
  }
}

void splitStringToAction(String com)
{
  int j = 0;
  for(int i = 0; i < com.length(); i++)
  {
    if(com.charAt(i) == ',')
    {
      command = com.substring(j, i);
      j = i + 1;
      doFastAction(command);
      delay(500);
    }
  }

  command = com.substring(j, com.length());
  doFastAction(command);
}

void doFastAction(String com)
{
  if (com.charAt(0) == 'w')
  {
    if (com.charAt(1) == '1')
    {
      if (com.length() == 2)
        goForwardFast(10);

      else
      {
        if (com.charAt(2) == '0')
          goForwardFast(100);

        else if (com.charAt(2) == '1')
          goForwardFast(110);

        else if (com.charAt(2) == '2')
          goForwardFast(120);

        else if (com.charAt(2) == '3')
          goForwardFast(130);

        else if (com.charAt(2) == '4')
          goForwardFast(140);

        else if (com.charAt(2) == '5')
          goForwardFast(150);
      }
    }

    else if (com.charAt(1) == '2')
      goForwardFast(20);

    else if (com.charAt(1) == '3')
      goForwardFast(30);

    else if (com.charAt(1) == '4')
      goForwardFast(40);

    else if (com.charAt(1) == '5')
      goForwardFast(50);

    else if (com.charAt(1) == '6')
      goForwardFast(60);

    else if (com.charAt(1) == '7')
      goForwardFast(70);

    else if (com.charAt(1) == '8')
      goForwardFast(80);

    else if (com.charAt(1) == '9')
      goForwardFast(90);
  }

  else if (com.charAt(0) == 's')
  {
    if (com.charAt(1) == '1')
    {
      if (com.length() == 2)
        goBackwardFast(10);

      else
      {
        if (com.charAt(2) == '0')
          goBackwardFast(100);

        else if (com.charAt(2) == '1')
          goBackwardFast(110);

        else if (com.charAt(2) == '2')
          goBackwardFast(120);

        else if (com.charAt(2) == '3')
          goBackwardFast(130);

        else if (com.charAt(2) == '4')
          goBackwardFast(140);

        else if (com.charAt(2) == '5')
          goBackwardFast(150);
      }
    }

    else if (com.charAt(1) == '2')
      goBackwardFast(20);

    else if (com.charAt(1) == '3')
      goBackwardFast(30);

    else if (com.charAt(1) == '4')
      goBackwardFast(40);

    else if (com.charAt(1) == '5')
      goBackwardFast(50);

    else if (com.charAt(1) == '6')
      goBackwardFast(60);

    else if (com.charAt(1) == '7')
      goBackwardFast(70);

    else if (com.charAt(1) == '8')
      goBackwardFast(80);

    else if (com.charAt(1) == '9')
      goBackwardFast(90);
  }

  else if (com.charAt(0) == 'a')
  {
    if (com.charAt(1) == '1')
      turnLeftFast(90);

    else if (com.charAt(1) == '2')
      turnLeftFast(180);
  }

  else if (com.charAt(0) == 'd')
  {
    if (com.charAt(1) == '1')
      turnRightFast(90);

    else if (com.charAt(1) == '2')
      turnRightFast(180);
  }
}

void fullSystemCheck()
{
  goForward(10);
  delay(2000);
  goBackward(10);
  delay(2000);
  goForwardFast(20);
  delay(2000);
  goBackwardFast(20);
  delay(2000);

  turnLeft(90);
  delay(2000);
  turnRight(90);
  delay(2000);
  turnLeftFast(90);
  delay(2000);
  turnRightFast(90);
  delay(2000);
}