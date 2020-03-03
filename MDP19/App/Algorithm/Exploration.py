import numpy as np
from Helper.Constants import EAST, STARTZONE


class Exploration:

    '''
        timeLimit - To set time limit if there is any.
        currMap - To store current map state
    '''

    def __init__(self, realMap = None, timeLimit = None, calibrateLimit = 4, sim = True ):
        self.timeLimit = timeLimit
        self.exploredArea = 0
        self.currMap = np.zeros([20, 15])

        if (sim):
            from Robot import SimRobot
            self.robot = SimRobot(self.currentMap, EAST, STARTZONE, realMap)


