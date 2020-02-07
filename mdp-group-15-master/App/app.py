import sys
import numpy
from PyQt5.QtWidgets import *
from PyQt5.QtCore import *
from PyQt5.QtGui import *
from time import strftime
#from Helper.timer import Timer

class Color(QWidget):
    def __init__(self, color, *args, **kwargs):
        super(Color, self).__init__(*args, **kwargs)
        #self.setAutoFillBackground(True)
        palette = self.palette()
        palette.setColor(QPalette.Window, QColor(color))
        self.setPalette(palette)

class MapWrapper(QWidget):
    def __init__(self):
        super().__init__()
        vlayout = QVBoxLayout()
        
        vlayout.addWidget(Map())
        vlayout.addWidget(Legends())
        vlayout.setContentsMargins(0,0,0,0)
        self.setLayout(vlayout)
        self.setFixedWidth(375)

class Legend(QWidget):
    def __init__(self, name):
        super().__init__()
        self.setContentsMargins(0,0,0,0)
        hlayout = QHBoxLayout()
        hlayout.setAlignment(Qt.AlignLeft) 
        hlayout.setContentsMargins(0,0,0,0)
        
        img_label = QLabel()
        img_label.setFixedSize(16,16)
        img_label.setStyleSheet('background-color: ' + self.getColor(name) + ';')

        hlayout.addWidget(img_label)
        hlayout.addWidget(QLabel(' - ' + name))
        self.setLayout(hlayout)

    def getColor(self, name):
        color = {
            'Start': '#8BC34A',
            'Goal' : '#03A9F4',
            'Obstacles': '#2c3e50',
            'Explored' : '#bdc3c7',
            'Unexplored' : '#F44336',
            'Current Position': '#f1c40f'
        }
        
        return color[name]

class Legends(QWidget):
    def __init__(self):
        super().__init__()
        hlayout = QHBoxLayout()
        hlayout.setSpacing(0)
        hlayout.setContentsMargins(0,0,0,0)

        vlayout1 = QVBoxLayout()
        vlayout1.setAlignment(Qt.AlignLeft) 
        vlayout1.setContentsMargins(0,0,0,0)
        vlayout1.addWidget(Legend('Start'))
        vlayout1.addWidget(Legend('Obstacles'))

        vlayout2 = QVBoxLayout()
        vlayout2.setAlignment(Qt.AlignLeft) 
        vlayout2.setContentsMargins(0,0,0,0)
        vlayout2.addWidget(Legend('Goal'))
        vlayout2.addWidget(Legend('Explored'))

        vlayout3 = QVBoxLayout()
        vlayout3.setAlignment(Qt.AlignLeft) 
        vlayout3.setContentsMargins(0,0,0,0)
        vlayout3.addWidget(Legend('Current Position'))
        vlayout3.addWidget(Legend('Unexplored'))


        hlayout.addLayout(vlayout1)
        hlayout.addLayout(vlayout2)
        hlayout.addLayout(vlayout3)
        self.setLayout(hlayout)

class DPad(QWidget):
    def __init__(self):
        super().__init__()
        self.setMouseTracking(True)
        self.brushup = QBrush(QColor(0x009688)) 
        self.brushdown = QBrush(QColor(0x009688)) 
        self.brushleft = QBrush(QColor(0x009688)) 
        self.brushright = QBrush(QColor(0x009688)) 
        self.setFixedHeight(130)

        self.up = QPolygon()
        self.up.append(QPoint(64,0))
        self.up.append(QPoint(40,24))
        self.up.append(QPoint(88,24))

        self.left = QPolygon()
        self.left.append(QPoint(0,64))
        self.left.append(QPoint(24,40))
        self.left.append(QPoint(24,88))

        self.right = QPolygon()
        self.right.append(QPoint(128,64))
        self.right.append(QPoint(104,40))
        self.right.append(QPoint(104,88))

        self.down = QPolygon()
        self.down.append(QPoint(64,128))
        self.down.append(QPoint(40,104))
        self.down.append(QPoint(88,104))
        self.update()
    
    def paintEvent(self, e):
        qp = QPainter()
        qp.setPen(QPen(QBrush(Qt.darkGreen), 0, Qt.NoPen))

        qp.begin(self)

        qp.setBrush(self.brushup)
        qp.drawPolygon(self.up)

        qp.setBrush(self.brushleft)
        qp.drawPolygon(self.left)

        qp.setBrush(self.brushright)
        qp.drawPolygon(self.right)

        qp.setBrush(self.brushdown)
        qp.drawPolygon(self.down)

        qp.end()

    def mouseMoveEvent(self, event):
        point = event.localPos()
        if self.collisionDetection(self.up ,point):
            self.brush = QBrush(QColor(0x00766B))
            self.update()
            return
        if self.collisionDetection(self.left ,point):
            self.brush = QBrush(QColor(0x00766B))
            self.update()
            return
        if self.collisionDetection(self.right ,point):
            self.brush = QBrush(QColor(0x00766B))
            self.update()
            return
        if self.collisionDetection(self.down ,point):
            self.brush = QBrush(QColor(0x00766B))
            self.update()
            return

    
    def leaveEvent(self, event):
        self.brushup = QBrush(QColor(0x009688))
        self.brushdown = QBrush(QColor(0x009688))
        self.brushleft = QBrush(QColor(0x009688))
        self.brushright = QBrush(QColor(0x009688))
        self.update()
    
    def collisionDetection(self, triangle, point):
        area = 24 * 24
        p1 = triangle.point(0)
        p2 = triangle.point(1)
        p3 = triangle.point(2)

        a1 = self.calculateArea(point, p1, p2)
        a2 = self.calculateArea(point, p2, p3)
        a3 = self.calculateArea(point, p3, p1)
        print(a1 + a2 + a3)
        if int(a1 + a2 + a3) == area:
            print("Tr")
            return True

        return False

    def calculateArea(self, p1, p2, p3):
        px = p1.x()
        py = p1.y()
        x1 = p2.x()
        y1 = p2.y()
        x2 = p3.x()
        y2 = p3.y()
        return numpy.abs((x1-px)*(y2-py) - (x2-px)*(y1-py))


class Map(QGraphicsView):
    def __init__(self):
        super().__init__()
        self.color = [[QPen(QBrush(QColor(0x607D8B)), 1, Qt.SolidLine), QBrush(QColor(0xF44336))],
                      [QPen(QBrush(Qt.darkGreen), 0, Qt.NoPen), QBrush(QColor(0x8BC34A))],
                      [QPen(QBrush(Qt.darkRed), 0, Qt.NoPen), QBrush(QColor(0x03A9F4))],
                      [QPen(QBrush(QColor(0x607D8B)), 1, Qt.SolidLine), QBrush(QColor(0x03A9F4))],
                      [QPen(QBrush(QColor(0x607D8B)), 1, Qt.SolidLine), QBrush(QColor(0x03A9F4))],
                      [QPen(QBrush(QColor(0x607D8B)), 1, Qt.SolidLine), QBrush(QColor(0x03A9F4))]]

        self.map_array = numpy.zeros((20,15), dtype=int)
        self.map_array[17:, 0:3] = 1
        self.map_array[0:3, 12:] = 2
        self.width = 25
        
        self.drawMap()
        self.setFrameStyle(0)
        self.setFixedSize(375,500)
        self.setSceneRect(0, 0, 375, 500)
        self.setVerticalScrollBarPolicy(Qt.ScrollBarAlwaysOff)
        self.setHorizontalScrollBarPolicy(Qt.ScrollBarAlwaysOff)    
    
    def drawMap(self):
        #Generate Map scene
        map_array = self.map_array
        width = self.width
        scene = QGraphicsScene()
        for row in range(map_array.shape[0]):
            for column in range(map_array.shape[1]):
                index = map_array[row, column]
                pen, brush = self.color[index]
                scene.addRect(column * width, row * width, width, width, pen , brush)
        
        brush = QBrush()
        pen = QPen(QBrush(Qt.black), 6, Qt.SolidLine)
        scene.addRect(0, 0, 375, 500, pen, brush)    

        pen = QPen(QBrush(Qt.black), 3, Qt.SolidLine)
        scene.addRect(300, 0, 75, 75, pen, brush)
        scene.addRect(0, 425, 75, 75, pen, brush)      
        self.setScene(scene)
    
    def updateMap(self, row, column, status):
        pass

class CustomButtonWrapper(QWidget):
    def __init__(self, label):
        super().__init__()
        self.height = 50
        self.setFixedHeight(self.height)
        self.button = CustomButton(label)

        vlayout = QVBoxLayout()
        vlayout.setContentsMargins(0,0,0,0)
        vlayout.addWidget(self.button)
        self.setLayout(vlayout)

        shadow = QGraphicsDropShadowEffect()
        shadow.setBlurRadius(20) 
        shadow.setOffset(2)
        self.setGraphicsEffect(shadow)

    def connect(self, callback):
        self.button.clicked.connect(callback)

class CustomButton(QPushButton):
    def __init__(self, label):
        super().__init__()
        self.label = label
        self.height = 50
        self.setFixedHeight(self.height)
        self.brush = QBrush(QColor(0x009688))     
        self.update()
        
    
    def paintEvent(self, e):
        qp = QPainter()
        qp.begin(self)
        qp.setPen(QPen(QBrush(Qt.darkGreen), 0, Qt.NoPen))
        qp.setBrush(self.brush)
        qp.setFont(QFont('Roboto', 20))
        width = self.size().width()
        rect = QRect(0, 0, width, self.height)
        qp.drawRect(rect)
        qp.setPen(QPen(QBrush(QColor(0xecf0f1)), 1, Qt.SolidLine))
        qp.drawText(rect, Qt.AlignCenter, self.label)
        qp.end()

    def enterEvent(self, event):
        self.brush = QBrush(QColor(0x00766B))
        self.update()

    def leaveEvent(self, QEvent):
        self.brush = QBrush(QColor(0x009688))
        self.update()

# Subclass QMainWindow to customise your application's main window
class MainWindow(QMainWindow):

    def __init__(self, *args, **kwargs):
        super(MainWindow, self).__init__(*args, **kwargs)
        self.setWindowFlags(Qt.WindowCloseButtonHint | Qt.WindowMinimizeButtonHint)
        self.setWindowTitle("MDP Group15")
        
        #Get resolution of monitor screen
        screen_resolution = app.desktop().screenGeometry()
        screen_width = screen_resolution.width()
        screen_height = screen_resolution.height()

        #centralize
        self.width = 800
        self.height = 600
        self.setFixedSize(self.width, self.height)

        self.top = (screen_height - self.height) / 2
        self.left = (screen_width - self.width) / 2
        self.setGeometry(self.left, self.top, self.width, self.height)

        main_layout = QHBoxLayout()
        main_layout.addWidget(MapWrapper())

        side_layout = QVBoxLayout()
        timer = Timer() 
        side_layout.addWidget(timer)
        side_layout.addStretch()
        side_layout.addWidget(DPad())
        simulate = CustomButtonWrapper('Simulate')
        simulate.connect(timer.startTimer)
        side_layout.addWidget(simulate)
        #connect = CustomButtonWrapper('Connect')
        
        
        main_layout.addLayout(side_layout)
        
        central_widget = QWidget()
        central_widget.setLayout(main_layout)
        self.setCentralWidget(central_widget)

class Timer(QWidget):
    def __init__(self):
        super().__init__()      
        self.seconds = 0
        self.minutes = 0
        self.setFixedHeight(136)

        shadow = QGraphicsDropShadowEffect()
        shadow.setBlurRadius(20) 
        shadow.setOffset(2)
        self.setGraphicsEffect(shadow)
        
        self.timer = QTimer(self)
        self.timer.timeout.connect(self.updateTimer)
        self.update   
    
    def paintEvent(self, e):
        qp = QPainter()
        qp.begin(self)
        qp.setPen(QPen(QBrush(Qt.darkGreen), 0, Qt.NoPen))
        qp.setBrush(QBrush(QColor(0x009688))) 
        qp.setFont(QFont('Roboto', 20))
        width = self.size().width()
        top_rect = QRect(0, 0, width, 36)
        qp.drawRect(top_rect)
        qp.setBrush(QBrush(QColor(0xFBFBFB))) 
        content_rect = QRect(0, 36, width, 100)
        qp.drawRect(content_rect)
        qp.setPen(QPen(QBrush(QColor(0xecf0f1)), 1, Qt.SolidLine))
           
        qp.drawText(QRect(10, 0, width, 36), Qt.AlignLeft | Qt.AlignVCenter, 'Timer')
        qp.setFont(QFont('Roboto', 36))
        qp.setPen(QPen(QBrush(QColor(0x696969)), 1, Qt.SolidLine))
        text = str(self.minutes) + 'Min  ' + str(self.seconds) + 'Sec' 
        qp.drawText(content_rect, Qt.AlignCenter, text)
        qp.end()

    def updateTimer(self):
        self.seconds += 1
        if self.seconds > 59 :
            self.seconds = 0
            self.minutes += 1

        self.update()

    def startTimer(self):      
        self.timer.start(1000)

    def stopTimer(self):
        self.timer.stop()

if __name__ == '__main__':
    app = QApplication(sys.argv)
    window = MainWindow()
    window.show()
    app.exec_()