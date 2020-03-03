from AppletComms import *

wifi = AppletComms()
wifi.connectApp()
wifi.writeApp("Connected!")

while True:
    msg = wifi.readApp()

    if (msg == "q"):
        break

    wifi.writeApp("Received {0}".format(msg))

wifi.disconnectApp()