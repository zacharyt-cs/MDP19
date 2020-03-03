from bluetooth import *

serverSocket = BluetoothSocket(RFCOMM)
serverSocket.bind(("", PORT_ANY))
serverSocket.listen(1)

port = serverSocket.getsocketname()[1]

uuid = "" #Some 128-bit value self-defined in Android code

advertiseService(serverSocket, "BluetoothServer",
                 serviceID = uuid,
                 serviceClasses = [ uuid, SERIAL_PORT_CLASS ],
                 profiles = [ SERIAL_PORT_PROFILE ],
                 #protocols = [ OBEX_UUID ]
                )

print("Waiting for connection on RFCOMM channel %d" % port)

clientSocket, clientInfo = serverSocket.accept()
print("Accepted connection from ", clientInfo)

try:
    while True:
        data = clientSocket.recv(1024)
        if len(data) == 0: continue
        print("Received [%s]" % data)
        strData = ("FROM PI: " + data.decode('utf-8'))
        clientSocket.send(strData.encode('utf-8') + " to Android")

except IOError:
    pass

print("disconnected")

clientSocket.close()
serverSocket.close()
print("All done.")