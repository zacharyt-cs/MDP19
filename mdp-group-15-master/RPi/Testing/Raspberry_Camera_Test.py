#Import packages
from picamera.array import PiRGBArray
from picamera import PiCamera
import time
import cv2
 
#Initialize camera, grab reference to raw camera capture
camera = PiCamera()
rawCapture = PiRGBArray(camera)
 
#Brace for warmup!
time.sleep(0.1)
 
#Snap a picture from the camera
camera.capture(rawCapture, format="bgr")
image = rawCapture.array
 
#Display image on screen and wait for keypress
cv2.imshow("Image", image)
cv2.waitKey(0)