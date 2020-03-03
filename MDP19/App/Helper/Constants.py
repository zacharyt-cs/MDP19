import numpy as np

#Directions in anticlockwise

NORTH = 1
WEST = 2
SOUTH = 3
EAST = 4

#Movements

FRONT = "W"
RIGHT = "D"
LEFT = "A"

#Map Constants

MAP_HEIGHT = 20
MAP_WIDTH = 15
STARTZONE = np.asarray([MAP_HEIGHT-3, 1])
GOALZONE = np.asarray([1, MAP_WIDTH-3])
BOTTOM_LEFT_CORNER = STARTZONE
BOTTOM_RIGHT_CORNER = np.asarray([MAP_HEIGHT-3, MAP_WIDTH-3])
TOP_RIGHT_CORNER = GOALZONE
TOP_LEFT_CORNER = np.asarray([1, 1])

