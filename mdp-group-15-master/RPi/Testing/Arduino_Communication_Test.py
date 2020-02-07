import serial

port = "/dev/ttyACM0" #Depends on what is on the Pi
baud = 9600

#Create serial port, name it serial1 with baud rate as defined
serial1 = serial.Serial(port, baud)

#Clear the input buffer
serial1.flushInput()

compareTo = ["Flash Complete.\r\n","Hello Pi. This is Arduino...\r\n"]

while True:
        if serial1.inWaiting() > 0:
            inputValue = serial1.readline() #If data received, read it
            print(inputValue)

            #Compare read value to the list that has been defined
            if inputValue in compareTo:
                try:
                    #Get the number of times to blink LED
                    flashNum = input("Set Arduino Flash Times: ")
                    serial1.write('%d' % flashNum)

                except:
                    #If it isn't a number
                    print("Input error, please try with a number")
                    serial1.write('0')