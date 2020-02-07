from AndroidBluetoothComms import *
from ArduinoComms import *

def main():
    anComms = AndroidBluetoothComm()
    arComms = ArduinoComms()

    anComms.connectBT()
    arComms.connectArd()

    received = comms.readBT()
    arComms.writeArd(received)

    receivedFromArd = arComms.readArd()
    anComms.writeBT(receivedFromArd)

    anComms.disconBT()
    arComms.disconArd()

if __name__ == "__main__":
    main()