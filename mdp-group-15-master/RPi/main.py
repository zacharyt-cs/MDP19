import threading
import queue
import json

from AndroidBluetoothComms import *
from AppletComms import *
from ArduinoComms import *

import cv2

from picamera.array import PiRGBArray
from picamera import PiCamera
import numpy as np

class Main(threading.Thread):
	def __init__(self):
		threading.Thread.__init__(self)

		self.debug = False
		self.iconQueue = queue.Queue()
		
		#initialize all the thread with each component class file
		self.appletThread = AppletComms()
		self.arduinoThread = ArduinoComms()
		self.androidThread = AndroidBluetoothComm()
		
		self.appletInitThread = threading.Thread(target = self.appletThread.connectApp, name = "Applet_Initializer_Thread")
		self.arduinoInitThread = threading.Thread(target = self.arduinoThread.connectArd, name = "Arduino_Initializer_Thread")
		self.androidInitThread = threading.Thread(target = self.androidThread.connectBT, name = "Android_Initializer_Thread")

		self.appletInitThread.setDaemon(True)
		self.arduinoInitThread.setDaemon(True)
		self.androidInitThread.setDaemon(True)

		self.appletInitThread.start()
		self.arduinoInitThread.start()
		self.androidInitThread.start()
		time.sleep(1)
		
		#Either initThread or the actual thread...
		while not (self.appletThread.isConnected() and self.arduinoThread.isConnected() and self.androidThread.isConnected()):
			time.sleep(0.2)

	def initializeReaders(self):
		self.appletRevThread = threading.Thread(target = self.readFromApplet, name = "Applet_Reader_Thread")
		self.arduinoRevThread = threading.Thread(target = self.readFromArduino, name = "Arduino_Reader_Thread")
		self.androidRevThread = threading.Thread(target = self.readFromAndroid, name = "Android_Reader_Thread")
		
		self.appletRevThread.setDaemon(True)
		self.arduinoRevThread.setDaemon(True)
		self.androidRevThread.setDaemon(True)
		print ("All daemon threads initialized successfully!")

		self.appletRevThread.start()
		self.arduinoRevThread.start()
		self.androidRevThread.start()
		print ("All daemon threads started successfully!")

	def readFromApplet(self):
		try:
			while True:
				readPCMessage = self.appletThread.readApp()
				if (readPCMessage is None):
					continue

				#readPCMessage = readPCMessage.split('\n')
				#for msg in readPCMessage:
				#	self.filterMsg(msg)

				readPCMessage = self.parseReceivedMessage(readPCMessage)
				self.filterMsg(readPCMessage)


		except Exception as e:
			print("Error receiving from APPLET: %s" % str(e))

	def writeToApplet(self, message):
		self.appletThread.writeApp(message)
		print("Data transmitted to Applet: %s \r\n" % message.rstrip())

	def readFromArduino(self):
		try:
			while True:
				readArduinoMsg = self.arduinoThread.readArd()
				if (readArduinoMsg is None):
					continue

				readArduinoMsg = self.parseReceivedMessage(readArduinoMsg)
				self.filterMsg(readArduinoMsg)
				#readArduinoMsg = readArduinoMsg.lstrip()
				#if (len(readArduinoMsg) == 0):
				#	continue

				#if (readArduinoMsg[0].upper() == 'X'):
				#	print("Data passed from Arduino to PC: %s" % readArduinoMsg[1:].rstrip())
				#	self.writeToApplet(readArduinoMsg[1:] + "\r\n")

				#elif (readArduinoMsg[0].upper() == 'B'):
				#	print("Data passed from Arduino to Bluetooth: %s" % readArduinoMsg[1:].rstrip())
				#	self.writeToAndroid(readArduinoMsg[1:])

		except socket.error as e:
			print("Arduino disconnected!")

	def writeToArduino(self, message):
		self.arduinoThread.writeArd(message)
		print("Data transmitted to Arduino: %s \r\n" % message.rstrip())

	def readFromAndroid(self):
		while True:
			retry = False

			try:
				while True:
					readBTMessage = self.androidThread.readBT()
					if (readBTMessage is None):
						continue

					readBTMessage = self.parseReceivedMessage(readBTMessage)
					self.filterMsg(readBTMessage)

					#readBTMessage = readBTMessage.lstrip()
					#if (len(readBTMessage) == 0):
					#	continue

					#if (readBTMessage[0].upper() == 'X'):
					#	print("Data passed from Bluetooth to PC: %s" % readBTMessage[1:].rstrip())
					#	self.writeToApplet(readBTMessage[1:]+ "\r\n")

					#elif (readBTMessage[0].upper() == 'A'):
					#	print("Data passed from Bluetooth to Arduino: %s" % readBTMessage[1:].rstrip())
					#	self.writeToArduino(readBTMessage[1:]+"\r\n")

					#else:
					#	print("Incorrect header from Bluetooth (expecting 'X' for PC, 'A' for Arduino): [%s]" % readBTMessage[0])

			except Exception as e:
				print("Error receiving from BLUETOOTH: %s" % str(e))
				retry = True

			if (not retry):
				break

			self.androidThread.disconBT()
			print('Re-establishing bluetooth connection.')
			self.androidThread.connectBT()

	def writeToAndroid(self, message):
		self.androidThread.writeBT(message)
		print("Data transmitted to Android: %s" % message.rstrip())

	#The string to send to Android
	#imgs should be a list of images detected with []
	def parseMapState(self, mapState, robotPosition, imgs):
		toTransmit = {"grid": mapState, "robotPosition": robotPosition, "imgs": imgs}
		return json.dumps(toTransmit)

	def parseReceivedMessage(self, message):
		messages = json.loads(message)
		return messages

    #To direct the messages around
	def filterMsg(self, message):
		if (message is None):
			return

		if (message["from"] == "Android"):
			if (message["com"] == "W"):
				self.writeToArduino("W")
				#Use map class to update moving forward by 1
				self.writeToAndroid(json.dumps('{"status": "Moving forward"};'))
				self.writeToApplet(json.dumps('{"status": "Moving forward"};'))

			elif (message["com"] == "S"):
				self.writeToArduino("S")
				#Use map class to update moving backwards by 1
				self.writeToAndroid(json.dumps('{"status": "Moving backwards"};'))
				self.writeToApplet(json.dumps('{"status": "Moving backwards"};'))

			elif (message["com"] == "A"):
				self.writeToArduino("A")
				#Use map class to update turning left
				self.writeToAndroid(json.dumps('{"status": "Turning left"};'))
				self.writeToApplet(json.dumps('{"status": "Turning left"};'))

			elif (message["com"] == "D"):
				self.writeToArduino("D")
				#Use map class to update turning right
				self.writeToAndroid(json.dumps('{"status": "Turning right"};'))
				self.writeToApplet(json.dumps('{"status": "Turning right"};'))

			elif (message["com"] == "ex"):
				#Shouldn't need to pass to Arduino here, just inform Applet to start
				self.writeToAndroid(json.dumps('{"status": "Exploring"};'))
				self.writeToApplet(json.dumps('{"status": "Exploring"};'))

			elif (message["com"] == "fp"):
				#Shouldn't need to pass to Arduino here, just inform Applet to start
				self.writeToAndroid(json.dumps('{"status": "Running Fastest Path"};'))
				self.writeToApplet(json.dumps('{"status": "Running Fastest Path"};'))

			elif (message["com"] == "getPiMapState"):
				#Use the map class to get current location and facing here
				self.writeToAndroid(self.parseMapState())
				self.writeToApplet(self.parseMapState())

		elif (message["from"] == "Arduino"):
			#Arduino will only report sensor data. Nothing fancy here.
			fr = message["fr"]
			fl = message["fl"]
			mid = message["mid"]
			left = message["left"]
			rf = message["rf"]
			rb = message["rb"]
			#Use map class to update with sensor readings and all

			#Use the map class to get current location and facing here and then send it
			self.writeToAndroid(self.parseMapState())
			self.writeToApplet(self.parseMapState())

		elif (message["from"] == "Applet"):
			if (message["com"] == "move"):
				path = message["path"]

				self.writeToAndroid(json.dumps('{"status": "Exploring"};'))
				self.writeToApplet(json.dumps('{"status": "Exploring"};'))

				#Go forward by 1
				if (path == "W"):
					self.writeToArduino("W")
					#Use map class to update moving forward by 1
					self.writeToAndroid(json.dumps('{"status": "Moving forward"};'))
					self.writeToApplet(json.dumps('{"status": "Moving forward"};'))

				#Turn left 90 degrees
				elif (path == "A"):
					self.writeToArduino("A")
					#Use map class to update turning left
					self.writeToAndroid(json.dumps('{"status": "Turning left"};'))
					self.writeToApplet(json.dumps('{"status": "Turning left"};'))

				#Go backwards by 1
				elif (path == "S"):
					self.writeToArduino("S")
					#Use map class to update moving backwards by 1
					self.writeToAndroid(json.dumps('{"status": "Moving backwards"};'))
					self.writeToApplet(json.dumps('{"status": "Moving backwards"};'))

				#Turn right 90 degrees
				elif (path == "D"):
					self.writeToArduino("D")
					#Use map class to update turning right
					self.writeToAndroid(json.dumps('{"status": "Turning right"};'))
					self.writeToApplet(json.dumps('{"status": "Turning right"};'))


				#Use map class to update moving forward by 1
				self.writeToAndroid(json.dumps('{"status": "Moving forward"};'))
				self.writeToApplet(json.dumps('{"status": "Moving forward"};'))

			elif (message["com"] == "fp"):

				path = message["path"]

				self.writeToAndroid(json.dumps('{"status": "Running Fastest Path"};'))
				self.writeToApplet(json.dumps('{"status": "Running Fastest Path"};'))

				#Chuck all the things to the Arduino to do
				self.writeToArduino(path)

				time.sleep(20)
				self.writeToAndroid(json.dumps('{"status": "End"};'))
				self.writeToApplet(json.dumps('{"status": "End"};'))

			if (message["com"] == "ex"):
				path = message["path"]

				self.writeToAndroid(json.dumps('{"status": "Exploring"};'))
				self.writeToApplet(json.dumps('{"status": "Exploring"};'))

				#Go forward by 1
				if (path == "W"):
					self.writeToArduino("W")
					#Use map class to update moving forward by 1

				#Turn left 90 degrees
				elif (path == "A"):
					self.writeToArduino("A")
					#Use map class to update turning left

				#Go backwards by 1
				elif (path == "S"):
					self.writeToArduino("S")
					#Use map class to update moving backwards by 1

				#Turn right 90 degrees
				elif (path == "D"):
					self.writeToArduino("D")
					#Use map class to update turning right

				elif(path == "End"):
					#Send something to align front and right side
					pass

		'''
			#For the arduino
			std::vector<String> splitStringToVector(String msg)
			{
				std::vector<String> subStrings;
				int j=0;
				for(int i =0; i < msg.length(); i++)
				{
					if(msg.charAt(i) == ',')
					{
						subStrings.push_back(msg.substring(j,i));
						j = i+1;
					}
				}
				subStrings.push_back(msg.substring(j,msg.length())); //to grab the last value of the string
				return subStrings;
			}'''

		'''
		message = message.lstrip()
		if (len(message) == 0):
			return

		if(message[0].upper() == 'B'):
			print("Data passed from Applet to Android: %s" % message[1:].rstrip())
			self.writeToAndroid(message[1:])

		elif(message[0].upper() == 'A'):
			print("Data passed from Applet to Arduino: %s" % message[1:].rstrip())
			self.writeToArduino(message[1:] + "\r\n")

		elif(message[0].upper() == 'I'):
			img = self.picCapture(self.debug)
			msgSplit = message[1:].split('|')

			if (msgSplit[2].upper().rstrip() == 'UP'):
				robotFace = 0

			elif (msgSplit[2].upper().rstrip() == 'DOWN'):
				robotFace = 1

			elif (msgSplit[2].upper().rstrip() == 'LEFT'):
				robotFace = 2

			elif (msgSplit[2].upper().rstrip() == 'RIGHT'):
				robotFace = 3
				
			newCmd = [int(msgSplit[0]), int(msgSplit[1]), robotFace, img]
			self.arrowQueue.put(newCmd)
			print("Scanning for arrow at: X=" + str(newCmd[0]) + ", Y=" + str(newCmd[1]) + ", Face=" + msgSplit[2].upper().rstrip())

		else:
			print("Incorrect header from PC (expecting 'B' for Android, 'A' for Arduino or 'I' for Image Recog.): [%s]" % readPCMessage[0])'''

	
	def picCapture(self, debug):
		self.imgTaken += 1
		self.captureFormat = PiRGBArray(self.camera, size=(1024, 768))
		self.camera.capture(self.captureFormat, format="bgr", use_video_port=True)
		time.sleep(0.1)
		img = self.captureFormat.array

		#To make image brighter
		hsv = cv2.cvtColor(img, cv2.COLOR_BGR2HSV)
		value = 80
		h,s,v = cv2.split(hsv)
		lim = 255 - value
		v[v > lim] = 255
		v[v <= lim] += value
		final_hsv = cv2.merge((h, s, v))
		img = cv2.cvtColor(final_hsv, cv2.COLOR_HSV2BGR)

		if (debug):
			cv2.imwrite("debug/" + str(self.imgTaken) + "raw.png", img)
		
		self.captureFormat.truncate(0)
		return img
	
	def iconRecognition(self):
		try:
			while True:
				if not (self.iconQueue.empty()):
					thisJob = self.iconQueue.get()

					print("ArrowRecog: Processing job X=" + str(thisJob[0]) + ", Y=" + str(thisJob[1]) + ", Face=" + str(thisJob[2]))
					detect = imgRecogMod.ScanArrow(self.debug, thisJob[3])

					print("ArrowRecog: Detection results %s" % detect)
					detectSplit = detect.split('|')
					detectSplit = [int(i) for i in detectSplit]

					if (detectSplit[0] == 1):
						direction = self.findIconDirection(thisJob[2])
						position = self.findIconPosition(thisJob[0], thisJob[1], thisJob[2], detectSplit[1], detectSplit[2])
						print('[ICON_INFO] Arrow at: X = ' + str(position[0]) + ', Y = ' + str(position[1]) + ', direction = ' + direction)

						icon = self.iconCoordinateFormat(position[0], position[1], direction.lower())
						self.writeToAndroid(icon + "\r\n")

				time.sleep(0.1)

		except Exception as e:
			print("[ICON_ERROR] Image recognition error: " + str(e))
	
	def iconCoordinateFormat(self, x, y, direction):
		iconPlace = {"x": x, "y": y, "face": direction}
		icon = {"icon": [data]}
		return json.dumps(icon)
	
	def findIconDirection(self, face):
		if (face == 0):
			return 'DOWN'

		elif (face == 1):
			return 'UP'

		elif (face == 2):
			return 'RIGHT'

		elif (face == 3):
			return 'LEFT'

		return 'UNKNOWN'
		
	def findIconPosition(self, robotX, robotY, robotFace, scanSection, scanGridDistance):
		if (robotFace == 0):
			if (scanSection == 0):
				return [robotX - 1, robotY + scanGridDistance]

			elif (scanSection == 1):
				return [robotX, robotY + scanGridDistance]

			elif (scanSection == 2):
				return [robotX + 1, robotY + scanGridDistance]

		elif (robotFace == 1):
			if (scanSection == 0):
				return [robotX + 1, robotY - scanGridDistance]

			elif (scanSection == 1):
				return [robotX, robotY - scanGridDistance]

			elif (scanSection == 2):
				return [robotX - 1, robotY - scanGridDistance]

		elif (robotFace == 2):
			if (scanSection == 0):
				return [robotX-scanGridDistance, robotY-1]

			elif (scanSection == 1):
				return [robotX-scanGridDistance, robotY]

			elif (scanSection == 2):
				return [robotX-scanGridDistance, robotY+1]

		elif (robotFace == 3):
			if (scanSection == 0):
				return [robotX+scanGridDistance, robotY+1]

			elif (scanSection == 1):
				return [robotX+scanGridDistance, robotY]

			elif (scanSection == 2):
				return [robotX+scanGridDistance, robotY-1]

		return [0, 0]

	def disconAll(self):
		self.appletThread.disconnectApp()
		self.arduinoThread.disconnectArd()
		self.androidThread.disconBT()
		print ("All sockets disconnected")

	def keepLive(self):
		while(1):
			if not (self.appletRevThread.is_alive()):
				print('[APPLET_ERROR] Applet thread is not running')

			if not (self.arduinoRevThread.is_alive()):
				print('[ARDUINO_ERROR] Arduino thread is not running')

			if not (self.androidRevThread.is_alive()):
				print('[ANDROID_ERROR] Android thread is not running')
				
			time.sleep(1)


if __name__ == "__main__":
	try:
		mainThread = Main()
		mainThread.initializeReaders()
		mainThread.keepLive()
		mainThread.disconAll()

	except SystemExit:
		pass









'''from AndroidBluetoothComms import *

def main():
    androidComm = AndroidBluetoothComm()
    androidComm.connectBT()
    
    recString(androidComm)

def recString(androidComm):
    while True:
        retry = False
        try:
            while True:
                readBTMessage = androidComm.readBT()
                if (readBTMessage is None):
                    continue

                readBTMessage = readBTMessage.lstrip()

                if (len(readBTMessage) == 0):
                    continue

                if (readBTMessage == 1):
                    androidComm.writeBT(readBTMessage + 1)

                if (readBTMessage == 'D'):
                    break

                if (readBTMessage[0].upper() == 'X'):
                    #time.sleep(0.1)
                    print("Data passed from Bluetooth to PC: %s" % readBTMessage[1:].rstrip())
                    self.writePC(readBTMessage[1:]+ "\r\n")

                elif (readBTMessage[0].upper() == 'A'):
                    #time.sleep(0.1)
                    print("Data passed from Bluetooth to Arduino: %s" % readBTMessage[1:].rstrip())
                    self.writeArduino(readBTMessage[1:]+"\r\n")

                else:
                    print("Incorrect header from Bluetooth (expecting 'X' for PC, 'A' for Arduino): [%s]" % readBTMessage[0])

        except Exception as e:
            print("main/BT-Recv Error: %s" % str(e))
            retry = True

        if (not retry):
            break

        androidComm.disconBT()
        androidComm.connectBT()


if __name__ == "__main__":
    main()'''