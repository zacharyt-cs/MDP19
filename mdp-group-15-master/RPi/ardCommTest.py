from ArduinoComms import *
import time

ard = ArduinoComms()
ard.connectArd()

time.sleep(10)
ard.writeArd("w1,a,d")
print("Data sent")

ard.disconnectArd()