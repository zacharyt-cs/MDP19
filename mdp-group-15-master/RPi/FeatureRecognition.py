#Import for OpenCV
import cv2
import numpy as np

#Import for Pi Camera
#Import for time to sleep
from picamera.array import PiRGBArray
from picamera import PiCamera
import time

#Getting the reference images itself
#up = cv2.imread("/home/pi/Desktop/Up.JPG")
#down = cv2.imread("/home/pi/Desktop/Down.JPG")
#left = cv2.imread("/home/pi/Desktop/Left.JPG")
#right = cv2.imread("/home/pi/Desktop/Right.JPG")
#stop = cv2.imread("/home/pi/Desktop/Stop.JPG")
#a = cv2.imread("/home/pi/Desktop/A.JPG")
#b = cv2.imread("/home/pi/Desktop/B.JPG")
#c = cv2.imread("/home/pi/Desktop/C.JPG")
#d = cv2.imread("/home/pi/Desktop/D.JPG")
#e = cv2.imread("/home/pi/Desktop/E.JPG")
#one = cv2.imread("/home/pi/Desktop/1.JPG")
#two = cv2.imread("/home/pi/Desktop/2.JPG")
#three = cv2.imread("/home/pi/Desktop/3.JPG")
#four = cv2.imread("/home/pi/Desktop/4.JPG")
#five = cv2.imread("/home/pi/Desktop/5.JPG")

#Getting the path to the reference images instead
up = "/home/pi/Desktop/Up.JPG"
down = "/home/pi/Desktop/Down.JPG"
left = "/home/pi/Desktop/Left.JPG"
right = "/home/pi/Desktop/Right.JPG"
stop = "/home/pi/Desktop/Stop.JPG"
a = "/home/pi/Desktop/A.JPG"
b = "/home/pi/Desktop/B.JPG"
c = "/home/pi/Desktop/C.JPG"
d = "/home/pi/Desktop/D.JPG"
e = "/home/pi/Desktop/E.JPG"
one = "/home/pi/Desktop/1.JPG"
two = "/home/pi/Desktop/2.JPG"
three = "/home/pi/Desktop/3.JPG"
four = "/home/pi/Desktop/4.JPG"
five = "/home/pi/Desktop/5.JPG"

#Storing these images as a template
templateImg = []
templateImg.append(up)
templateImg.append(down)
templateImg.append(left)
templateImg.append(right)
templateImg.append(stop)
templateImg.append(a)
templateImg.append(b)
templateImg.append(c)
templateImg.append(d)
templateImg.append(e)
templateImg.append(one)
templateImg.append(two)
templateImg.append(three)
templateImg.append(four)
templateImg.append(five)

#templateImg = [up, down, left, right, stop, a, b, c, d, e, one, two, three, four, five]

#Initialize the Camera
camera = PiCamera()
rawCapture = PiRGBArray(camera)

#Take a picture
camera.capture(rawCapture, format="bgr")
image = rawCapture.array
gray = cv2.cvtColor(image, cv2.COLOR_BGR2GRAY) #Stupid function only works on grayscale

#For each image in the template
for t in templateImg:
    template = cv2.imread(t, 0)
    w, h = template.shape[::-1]

    res = cv2.matchTemplate(gray, template, cv2.TM_CCOEFF_NORMED)
    threshold = 0.8
    loc = np.where( res >= threshold)

    for pt in zip(*loc[::-1]):
        cv2.rectangle(image, pt, (pt[0] + w, pt[1] + h), (0,0,255), 2)


#Got results or not?
cv2.imshow("Image", image)
cv2.waitKey(0)

#cv2.imwrite('res.png',img_rgb)