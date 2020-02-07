#include <ZSharpIR.h>
#include <RunningMedian.h>

#define SRmodel 1080
#define LRmodel 20150

#define s1 A4 //Front Right
#define s2 A3 //Middle
#define s3 A1 //Right Front
#define s4 A2 //Right Back
#define s5 A5 //Front Left

#define s6 A0 //Left

//short IR sensor
ZSharpIR sr1 = ZSharpIR(s1, SRmodel); //Front Right
ZSharpIR sr2 = ZSharpIR(s2, SRmodel); //Middle
ZSharpIR sr3 = ZSharpIR(s3, SRmodel); //Right Front
ZSharpIR sr4 = ZSharpIR(s4, SRmodel); //Right Back
ZSharpIR sr5 = ZSharpIR(s5, SRmodel); //Front Left

//long IR sensor
ZSharpIR sr6 = ZSharpIR(s6, LRmodel); //Left

const int MEDIAN_SAMPLES = 25;
int medianValue;

/*void setup()
{
  // put your setup code here, to run once:
  Serial.begin(115200);
}

void loop()
{
  // put your main code here, to run repeatedly:
  printSensors();
  delay(1000);
}*/

void printSensors()
{
  Serial.print("\nSensor values:");
  Serial.print("\nFront-Right: ");
  Serial.print(getFrontRight());
  
  Serial.print("\nFront-Left: ");
  Serial.print(getFrontLeft());

  Serial.print("\nMiddle: ");
  Serial.print(getMiddle());
  
  Serial.print("\nRight-Front: ");
  Serial.print(getRightFront());
  
  Serial.print("\nRight-Back: ");
  Serial.print(getRightBack());
  
  Serial.print("\nLeft: ");
  //Serial.println(sr6.distance());
  Serial.print(getLRDistance()); //Use this!!
}

//Tweak after checklist
int getLRDistance()
{
  RunningMedian lr_median = RunningMedian(MEDIAN_SAMPLES);
  int grid = 0;

  for (int n = 0; n < MEDIAN_SAMPLES; n++)
  {
    double irDistance = sr6.distance();   
    
    lr_median.add(irDistance);
    
    if (lr_median.getCount() == MEDIAN_SAMPLES)
    {
      if (lr_median.getHighest() - lr_median.getLowest() > 70)
        return -10;
      
      medianValue = lr_median.getMedian();
      //return medianValue;
    }
  }

  if (medianValue >= 170 && medianValue <= 210) //3 to 10, 183, 204
  {
    grid = 1;
    return grid;
  }
  
  if (medianValue >= 211 && medianValue <= 286) //11 to 20, 205, 282
  {
    grid = 2;
    return grid;
  }

  if (medianValue >= 287 && medianValue <= 390) //21 to 30, 283, 386
  {
    grid = 3;
    return grid;
  }

  if (medianValue >= 391 && medianValue <= 499) //31 to 40, 387, 429
  {
    grid = 4;
    return grid;
  }

  if (medianValue >= 500 && medianValue <= 592) //41 to 50, 430, 458
  {
    grid = 5;
    return grid;
  }

  /*if (medianValue >= 590 && medianValue <= 709) //51 to 60, 459, 474
  {
    grid = 6;
    return grid;
  }

  if (medianValue >= 710 && medianValue <= 900) //61 to 70, 183, 204
  {
    grid = 7;
    return grid;
  }*/
  
  return -1;

  /*if ((medianValue/10) >= 29 && (medianValue/10) <= 43) //30 to 40
    return (medianValue/10) - 3;

  if ((medianValue/10) >= 44 && (medianValue/10) <= 46) //41 to 45
    return (medianValue/10) - 1;

  if ((medianValue/10) >= 47 && (medianValue/10) <= 51) //46 to 50
    return (medianValue/10);

  if ((medianValue/10) >= 52 && (medianValue/10) <= 54) //51 to 55
    return (medianValue/10) + 1;

  if ((medianValue/10) >= 55 && (medianValue/10) <= 57) //56 to 60
    return (medianValue/10) + 4;

  if ((medianValue/10) >= 58 && (medianValue/10) <= 58) //61 to 65
  {
    if ((medianValue/10) + 5 == 63)
      return (medianValue/10) + 6;      
    
    return (medianValue/10) + 5;
  }

  if ((medianValue/10) >= 59 && (medianValue/10) <= 63) //66 to 70
    return (medianValue/10) + 7;
  
  return (medianValue/10);*/
}

//Done
int getFrontRight()
{
  RunningMedian fr_median = RunningMedian(MEDIAN_SAMPLES);
  int grid = 0;

  for (int n = 0; n < MEDIAN_SAMPLES; n++)
  {
    double irDistance = sr1.distance();
    
    fr_median.add(irDistance);
    
    if (fr_median.getCount() == MEDIAN_SAMPLES)
    {
      if (fr_median.getHighest() - fr_median.getLowest() > 60)
        return -1;
      
      medianValue = fr_median.getMedian();
      //return medianValue;
    }
  }

  //Change these for new positioning
  if (medianValue >= 71 && medianValue <= 165) //3 to 10, 73, 172
  {
    grid = 1;
    return grid;
  }

  if (medianValue >= 166 && medianValue <= 285) //11 to 20, 173, 279
  {
    grid = 2;
    return grid;
  }

  /* Original position values
  if (medianValue >= 62 && medianValue <= 117) //3 to 10, 73, 172
  {
    grid = 1;
    return grid;
  }

  if (medianValue >= 118 && medianValue <= 225) //11 to 20, 173, 279
  {
    grid = 2;
    return grid;
  }

  if (medianValue >= 225 && medianValue <= 317) //21 to 30, 280, 382
  {
    grid = 3;
    return grid;
  }

  if (medianValue >= 308 && medianValue <= 362) //31 to 40, 383, 462
  {
    grid = 4;
    return grid;
  }

  if (medianValue >= 363 && medianValue <= 445) //41 to 50, 383, 462
  {
    grid = 5;
    return grid;
  }*/
  
  return -1;
}

//Done
int getFrontLeft()
{
  RunningMedian fl_median = RunningMedian(MEDIAN_SAMPLES);
  int grid = 0;
  
  for (int n = 0; n < MEDIAN_SAMPLES; n++)
  {
    double irDistance = sr5.distance();   
    
    fl_median.add(irDistance);
    
    if (fl_median.getCount() == MEDIAN_SAMPLES)
    {
      if (fl_median.getHighest() - fl_median.getLowest() > 60)
        return -1;
      
      medianValue = fl_median.getMedian();
      //return medianValue;
    }
  }

  //Change these for the new sensor values
  if (medianValue >= 71 && medianValue <= 163) //3 to 10, 74, 184
  {
    grid = 1;
    return grid;
  }

  if (medianValue >= 164 && medianValue <= 275) //11 to 20, 185, 287
  {
    grid = 2;
    return grid;
  }

  /* Original position values
  if (medianValue >= 65 && medianValue <= 127) //3 to 10, 74, 184
  {
    grid = 1;
    return grid;
  }

  if (medianValue >= 128 && medianValue <= 224) //11 to 20, 185, 287
  {
    grid = 2;
    return grid;
  }

  if (medianValue >= 233 && medianValue <= 332) //21 to 30, 288, 385
  {
    grid = 3;
    return grid;
  }

  if (medianValue >= 340 && medianValue <= 420) //31 to 35, 386, 442
  {
    grid = 4;
    return grid;
  }*/
  
  return -1;
}

//Done
int getMiddle()
{
  RunningMedian middle_median = RunningMedian(MEDIAN_SAMPLES);
  int grid = 0;
  
  for (int n = 0; n < MEDIAN_SAMPLES; n++)
  {
    double irDistance = sr2.distance();   
    
    middle_median.add(irDistance);
    
    if (middle_median.getCount() == MEDIAN_SAMPLES)
    {
      if (middle_median.getHighest() - middle_median.getLowest() > 60)
        return -1;
      
      medianValue = middle_median.getMedian();
      //return medianValue;
    }
  }

  if (medianValue >= 65 && medianValue <= 127) //3 to 10
  {
    grid = 1;
    return grid;
  }

  if (medianValue >= 128 && medianValue <= 234) //11 to 20
  {
    grid = 2;
    return grid;
  }

  /*if (medianValue >= 251 && medianValue <= 359) //21 to 30
  {
    grid = 3;
    return grid;
  }

  /*if (medianValue >=  && medianValue <= ) //31 to 40
  {
    grid = 4;
    return grid;
  }*/
  
  return -1;
}

//Done
int getRightFront()
{
  RunningMedian rf_median = RunningMedian(MEDIAN_SAMPLES);
  int grid = 0;
  
  for (int n = 0; n < MEDIAN_SAMPLES; n++)
  {
    double irDistance = sr3.distance();   
    
    rf_median.add(irDistance);
    
    if (rf_median.getCount() == MEDIAN_SAMPLES)
    {
      if (rf_median.getHighest() - rf_median.getLowest() > 60)
        return -1;
      
      medianValue = rf_median.getMedian();
      //return medianValue;
    }
  }

  if (medianValue >= 70 && medianValue <= 169) //3 to 10, 71, 109
  {
    grid = 1;
    return grid;
  }

  if (medianValue >= 170 && medianValue <= 270) //11 to 20, 71, 109
  {
    grid = 2;
    return grid;
  }
  
  /*if (medianValue >= 286 && medianValue <= 384) //21 to 30, 110, 219
  {
    grid = 3;
    return grid;
  }

  if (medianValue >= 410 && medianValue <= 630) //31 to 40, 220, 339
  {
    grid = 4;
    return grid;
  }*/

  /*if (medianValue >=  && medianValue <= ) //41 to 50, 340, 479
  {
    grid = 5;
    return grid;
  }*/
  
  return -1;
}

//Done
int getRightBack()
{
  RunningMedian rb_median = RunningMedian(MEDIAN_SAMPLES);
  int grid = 0;
  
  for (int n = 0; n < MEDIAN_SAMPLES; n++)
  {
    double irDistance = sr4.distance();   
    
    rb_median.add(irDistance);
    
    if (rb_median.getCount() == MEDIAN_SAMPLES)
    {
      if (rb_median.getHighest() - rb_median.getLowest() > 60)
        return -1;
      
      medianValue = rb_median.getMedian();
      //return medianValue;
    }
  }

  if (medianValue >= 70 && medianValue <= 162) //3 to 10, 71, 115
  {
    grid = 1;
    return grid;
  }
  
  if (medianValue >= 163 && medianValue <= 254) //11 to 20, 116, 219
  {
    grid = 2;
    return grid;
  }

  /*if (medianValue >= 265 && medianValue <= 372) //21 to 30, 220, 322
  {
    grid = 3;
    return grid;
  }

  if (medianValue >= 373 && medianValue <= 465) //31 to 40, 323, 420
  {
    grid = 4;
    return grid;
  }

  if (medianValue >= 466 && medianValue <= 593) //41 to 50, 421, 474
  {
    grid = 5;
    return grid;
  }*/
  
  return -1;
}

//For calibration
int getRightFrontDirect()
{
  return sr3.distance();
}

int getRightBackDirect()
{
  return sr4.distance();
}

int getFrontLeftDirect()
{
  return sr5.distance();
}

int getMiddleDirect()
{
  return sr2.getRaw();
}

int getFrontRightDirect()
{
  return sr1.distance();
}
