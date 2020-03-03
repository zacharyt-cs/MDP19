import numpy as np
import cv2

up = cv2.imread("Up.jpg")
down = cv2.imread("Down.jpg")
left = cv2.imread("Left.jpg")
right = cv2.imread("Right.jpg")
stop = cv2.imread("Stop.jpg")
a = cv2.imread("A.jpg")
b = cv2.imread("B.jpg")
c = cv2.imread("C.jpg")
d = cv2.imread("D.jpg")
e = cv2.imread("E.jpg")
one = cv2.imread("1.jpg")
two = cv2.imread("2.jpg")
three = cv2.imread("3.jpg")
four = cv2.imread("4.jpg")
five = cv2.imread("5.jpg")

up = cv2.cvtColor(up, cv2.COLOR_BGR2GRAY)
cv2.imwrite('grey_up.jpg', up)

down = cv2.cvtColor(down, cv2.COLOR_BGR2GRAY)
cv2.imwrite('grey_down.jpg', down)

left = cv2.cvtColor(left, cv2.COLOR_BGR2GRAY)
cv2.imwrite('grey_left.jpg', left)

right = cv2.cvtColor(right, cv2.COLOR_BGR2GRAY)
cv2.imwrite('grey_right.jpg', right)

stop = cv2.cvtColor(stop, cv2.COLOR_BGR2GRAY)
cv2.imwrite('grey_stop.jpg', stop)

a = cv2.cvtColor(a, cv2.COLOR_BGR2GRAY)
cv2.imwrite('grey_a.jpg', a)

b = cv2.cvtColor(b, cv2.COLOR_BGR2GRAY)
cv2.imwrite('grey_b.jpg', b)

c = cv2.cvtColor(c, cv2.COLOR_BGR2GRAY)
cv2.imwrite('grey_c.jpg', c)

d = cv2.cvtColor(d, cv2.COLOR_BGR2GRAY)
cv2.imwrite('grey_d.jpg', d)

e = cv2.cvtColor(e, cv2.COLOR_BGR2GRAY)
cv2.imwrite('grey_e.jpg', e)

one = cv2.cvtColor(one, cv2.COLOR_BGR2GRAY)
cv2.imwrite('grey_one.jpg', one)

two = cv2.cvtColor(two, cv2.COLOR_BGR2GRAY)
cv2.imwrite('grey_two.jpg', two)

three = cv2.cvtColor(three, cv2.COLOR_BGR2GRAY)
cv2.imwrite('grey_three.jpg', three)

four = cv2.cvtColor(four, cv2.COLOR_BGR2GRAY)
cv2.imwrite('grey_four.jpg', four)

five = cv2.cvtColor(five, cv2.COLOR_BGR2GRAY)
cv2.imwrite('grey_five.jpg', five)