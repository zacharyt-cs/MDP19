
from PyQt5.QtWidgets import *
from PyQt5.QtCore import *
from PyQt5.QtGui import *
from time import strftime

class Timer(QWidget):
    def __init__(self):
        super().__init__()
        timer = QTimer(self)
        timer.timeout.connect(self.update)
        timer.start(1000)

        self.setFixedHeight(100)
        self.seconds = 0
        self.minutes = 0
        text = str(self.minutes) + 'Min  ' + str(self.seconds) + 'Sec'

        self.timerdisplay = QLabel(text)
        self.timerdisplay.setContentsMargins(0, 0, 0, 0)
        self.timerdisplay.setStyleSheet("font: 30pt Arial Black")
        self.timerdisplay.setFixedHeight(100)
        self.timerdisplay.setAlignment(Qt.AlignCenter)

        hlayout = QHBoxLayout()
        hlayout.setContentsMargins(0, 0, 0, 0)
        hlayout.addWidget(self.timerdisplay)
        self.setLayout(hlayout)

    def update(self):
        self.seconds += 1
        if self.seconds > 59:
            self.seconds = 0
            self.minutes += 1

        text = str(self.minutes) + 'Min  ' + str(self.seconds) + 'Sec'
        self.timerdisplay.setText(text)

    def paintEvent(self, event):
        pass
