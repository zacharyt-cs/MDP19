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