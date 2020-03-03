import numpy as np


class SimRobot:

    def __init__(self, exploredArea, direction, start, realMap):
        self.exploredArea = exploredArea
        self.direction = direction
        self.start = np.asarray(start)
