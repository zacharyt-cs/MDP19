#TODO: https://www.pyimagesearch.com/2015/01/26/multi-scale-template-matching-using-python-opencv/

#IDEAS: Multi-Scaling, Canny (Hysteresis Thresholding)/Sobel X * Sobel Y, Template Matching, HAAR CASCADES???

import cv2
import imutils as im
import numpy as np

#Import for Pi Camera
#Import for time to sleep
from picamera.array import PiRGBArray
from picamera import PiCamera
import time

#Getting the path to the reference images
#To change to grey-scale pictures?
upO = "/home/pi/Desktop/Up.JPG"
downO = "/home/pi/Desktop/Down.JPG"
leftO = "/home/pi/Desktop/Left.JPG"
rightO = "/home/pi/Desktop/Right.JPG"
stopO = "/home/pi/Desktop/Stop.JPG"
aO = "/home/pi/Desktop/A.JPG"
bO = "/home/pi/Desktop/B.JPG"
cO = "/home/pi/Desktop/C.JPG"
dO = "/home/pi/Desktop/D.JPG"
eO = "/home/pi/Desktop/E.JPG"
oneO = "/home/pi/Desktop/1.JPG"
twoO = "/home/pi/Desktop/2.JPG"
threeO = "/home/pi/Desktop/3.JPG"
fourO = "/home/pi/Desktop/4.JPG"
fiveO = "/home/pi/Desktop/5.JPG"

#Threshold everything
up = cv2.imread(upO) #Read the image
up = cv2.cvtColor(up, cv2.COLOR_BGR2GRAY) #Convert it to gray scale
retVal, upThreshed = cv2.threshold(up, 127, 255, cv2.THRESH_BINARY_INV) #Original, threshold value, maxval, type
up = cv2.Canny(up, 50, 200) #Image, Thresh 1, Thresh 2 (Hysteresis Thresholding)

down = cv2.imread(downO)
down = cv2.cvtColor(down, cv2.COLOR_BGR2GRAY)
retVal, downThreshed = cv2.threshold(down, 127, 255, cv2.THRESH_BINARY_INV)
down = cv2.Canny(down, 50, 200)

left = cv2.imread(leftO)
left = cv2.cvtColor(left, cv2.COLOR_BGR2GRAY)
retVal, leftThreshed = cv2.threshold(left, 127, 255, cv2.THRESH_BINARY_INV)
left = cv2.Canny(left, 50, 200)

right = cv2.imread(rightO)
right = cv2.cvtColor(right, cv2.COLOR_BGR2GRAY)
retVal, rightThreshed = cv2.threshold(right, 127, 255, cv2.THRESH_BINARY_INV)
right = cv2.Canny(right, 50, 200)

stop = cv2.imread(stopO)
stop = cv2.cvtColor(stop, cv2.COLOR_BGR2GRAY)
retVal, stopThreshed = cv2.threshold(stop, 127, 255, cv2.THRESH_BINARY_INV)
stop = cv2.Canny(stop, 50, 200)

a = cv2.imread(aO)
a = cv2.cvtColor(a, cv2.COLOR_BGR2GRAY)
retVal, aThreshed = cv2.threshold(a, 100, 255, cv2.THRESH_BINARY_INV)
a = cv2.Canny(a, 50, 200)

b = cv2.imread(bO)
b = cv2.cvtColor(b, cv2.COLOR_BGR2GRAY)
retVal, bThreshed = cv2.threshold(b, 127, 255, cv2.THRESH_BINARY_INV)
b = cv2.Canny(b, 50, 200)

c = cv2.imread(cO)
c = cv2.cvtColor(c, cv2.COLOR_BGR2GRAY)
retVal, cThreshed = cv2.threshold(c, 127, 255, cv2.THRESH_BINARY_INV)
c = cv2.Canny(c, 50, 200)

d = cv2.imread(dO)
d = cv2.cvtColor(d, cv2.COLOR_BGR2GRAY)
retVal, dThreshed = cv2.threshold(d, 127, 255, cv2.THRESH_BINARY_INV)
d = cv2.Canny(d, 50, 200)

e = cv2.imread(eO)
e = cv2.cvtColor(e, cv2.COLOR_BGR2GRAY)
retVal, eThreshed = cv2.threshold(e, 127, 255, cv2.THRESH_BINARY_INV)
e = cv2.Canny(e, 50, 200)

one = cv2.imread(oneO)
one = cv2.cvtColor(one, cv2.COLOR_BGR2GRAY)
retVal, oneThreshed = cv2.threshold(one, 127, 255, cv2.THRESH_BINARY_INV)
one = cv2.Canny(one, 50, 200)

two = cv2.imread(twoO)
two = cv2.cvtColor(two, cv2.COLOR_BGR2GRAY)
retVal, twoThreshed = cv2.threshold(two, 127, 255, cv2.THRESH_BINARY_INV)
two = cv2.Canny(two, 50, 200)

three = cv2.imread(threeO)
three = cv2.cvtColor(three, cv2.COLOR_BGR2GRAY)
retVal, threeThreshed = cv2.threshold(three, 127, 255, cv2.THRESH_BINARY_INV)
three = cv2.Canny(three, 50, 200)

four = cv2.imread(fourO)
four = cv2.cvtColor(four, cv2.COLOR_BGR2GRAY)
retVal, fourThreshed = cv2.threshold(four, 127, 255, cv2.THRESH_BINARY_INV)
four = cv2.Canny(four, 50, 200)

five = cv2.imread(fiveO)
five = cv2.cvtColor(five, cv2.COLOR_BGR2GRAY)
retVal, fiveThreshed = cv2.threshold(five, 127, 255, cv2.THRESH_BINARY_INV)
five = cv2.Canny(five, 50, 200)

#Get the contours
_, contUp, _ = cv2.findContours(upThreshed, cv2.RETR_TREE, cv2.CHAIN_APPROX_SIMPLE)
_, contDown, _ = cv2.findContours(downThreshed, cv2.RETR_TREE, cv2.CHAIN_APPROX_SIMPLE)
_, contLeft, _ = cv2.findContours(leftThreshed, cv2.RETR_TREE, cv2.CHAIN_APPROX_SIMPLE)
_, contRight, _ = cv2.findContours(rightThreshed, cv2.RETR_TREE, cv2.CHAIN_APPROX_SIMPLE)
_, contStop, _ = cv2.findContours(stopThreshed, cv2.RETR_TREE, cv2.CHAIN_APPROX_SIMPLE)
_, contA, _ = cv2.findContours(aThreshed, cv2.RETR_TREE, cv2.CHAIN_APPROX_SIMPLE)
_, contB, _ = cv2.findContours(bThreshed, cv2.RETR_TREE, cv2.CHAIN_APPROX_SIMPLE)
_, contC, _ = cv2.findContours(cThreshed, cv2.RETR_TREE, cv2.CHAIN_APPROX_SIMPLE)
_, contD, _ = cv2.findContours(dThreshed, cv2.RETR_TREE, cv2.CHAIN_APPROX_SIMPLE)
_, contE, _ = cv2.findContours(eThreshed, cv2.RETR_TREE, cv2.CHAIN_APPROX_SIMPLE)
_, cont1, _ = cv2.findContours(oneThreshed, cv2.RETR_TREE, cv2.CHAIN_APPROX_SIMPLE)
_, cont2, _ = cv2.findContours(twoThreshed, cv2.RETR_TREE, cv2.CHAIN_APPROX_SIMPLE)
_, cont3, _ = cv2.findContours(threeThreshed, cv2.RETR_TREE, cv2.CHAIN_APPROX_SIMPLE)
_, cont4, _ = cv2.findContours(fourThreshed, cv2.RETR_TREE, cv2.CHAIN_APPROX_SIMPLE)
_, cont5, _ = cv2.findContours(fiveThreshed, cv2.RETR_TREE, cv2.CHAIN_APPROX_SIMPLE)

#Store the contours, set the threshold
contourTemp = [contUp[0], contDown[0], contLeft[0], contRight[0], contStop[0], contA[0], contB[0], contC[0], contD[0], contE[0], cont1[0], cont2[0], cont3[0], cont4[0], cont5[0]]
tempName = ["Up", "Down", "Left", "Right", "Stop", "A", "B", "C", "D", "E", "1", "2", "3", "4", "5"]
tempSize = [up.shape[:: -1], down.shape[:: -1], left.shape[:: -1], right.shape[:: -1], stop.shape[:: -1], a.shape[:: -1], b.shape[:: -1], c.shape[:: -1], d.shape[:: -1], e.shape[:: -1], one.shape[:: -1], two.shape[:: -1], three.shape[:: -1], four.shape[:: -1], five.shape[:: -1]]
matchResult = [None] * 15
matchLoc = [None] * 15

matchThresh = 0.8 #Match if 80% matching. Can reduce accordingly

#Initialize the Camera
camera = PiCamera()
rawCapture = PiRGBArray(camera)

def matchTemp():
    capped = imageGrab()
    grayCap = cv2.cvtColor(capped, cv2.COLOR_BGR2GRAY)
    for i in range(15):
        res = cv2.matchTemplate(grayCap, contourTemp[i], cv2.TM_CCOEFF_NORMED)

        if (res >= matchThresh):
            matchResult[i] = res
        
        else:
            matchResult[i] = None

    bestMatch = matchResult.index(max(matchResult))

    minVal, maxVal, minLoc, maxLoc = cv2.minMaxLoc(matchResult[bestMatch])
    topLeft = maxLoc #Because using TM_CCOEFF_NORMED
    bottomRight = (topLeft[0] + tempSize[bestMatch][0], topLeft[1] + tempSize[bestMatch][1])

    cv2.rectangle(capped, topLeft, bottomRight, (255, 255, 0), 2)
    cv2.putText(capped, tempName[bestMatch], (topLeft[0], topLeft[1] - 10), cv2.FONT_HERSHEY_SIMPLEX, 0.7, (100,255,100), 2)

    cv2.imshow('detected', capped)
    cv2.waitKey(0)

def imageGrab():
    #Take a picture
    camera.capture(rawCapture, format="bgr")
    image = rawCapture.array
    return image