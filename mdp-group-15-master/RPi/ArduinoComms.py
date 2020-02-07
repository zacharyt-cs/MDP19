import serial
import time

class ArduinoComms(object):

    #Initialize the items required for Arduino Comms
    #Remember to check baud rate and communication port!
    def __init__(self):
        self.commPort = 'COM3' #'/dev/ttyACM0' #TOCHECK from Pi on connections
        self.isEstablished = False
        self.baud = 115200 #Technical documentation

    #Good to have to check if connected
    def isConnected(self):
        return self.isEstablished

    def connectArd(self):
        while True:
            retry = False
            try:
                #Let's wait for connection
                print ('[ARDUINO_INFO] Waiting for serial connection from Arduino')

                self.serialConn = serial.Serial(self.commPort, self.baud)
                print('[ARDUINO_ACCEPTED] Connected to Arduino.')
                self.isEstablished = True
                retry = False

            except Exception as e:
                print('[ARDUINO_ERROR] Arduino Connection Error: %s' % str(e))
                retry = True

            #When established, break the while(true)
            if (not retry):
                break

            #When not yet established, keep retrying
            print('[ARDUINO_INFO] Retrying Arduino Establishment')
            time.sleep(1)

    #Disconnect when done
    def disconnectArd(self):
        if not (self.serialConn is None): #if (self.serialConn):
            print('[ARDUINO_CLOSE] Shutting down Arduino Connection')
            self.serialConn.close()
            self.isConnected = False

    #The fundamental trying to receive
    def readArd(self):
        try:
            #self.serialConn.flush() #Clean the pipe
            readData = self.serialConn.readline()
            self.serialConn.flush() #Clean the pipe
            readData = readData.decode('utf-8')
            print('[ARDUINO_INFO] Received: ' + readData)
            return readData

        except Exception as e:
            print('[ARDUINO_ERROR] Receiving Error: %s' % str(e))
            if ('Input/output error' in str(e)):
                self.disconnectArd()
                print('[ARDUINO_INFO] Re-establishing Arduino Connection.')
                self.connectArd()

    #The fundamental trying to send
    def writeArd(self, message):
        try:
            #Make sure there is a connection first before sending
            if (self.isEstablished):
                message = message.encode('utf-8')
                self.serialConn.write(message)
                return

            #There is no connections. Send what?
            else:
                print('[ARDUINO_INVALID] No Arduino Connections')

        except Exception as e:
            print('[ARDUINO_ERROR] Cannot send message: %s' % str(e))