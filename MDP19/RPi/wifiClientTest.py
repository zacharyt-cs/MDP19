import socket

s = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
s.connect('192.168.15.15', 36126)

while True:
    msg = s.recv(2048)
    msg.decode("utf-8")

    print(msg)
    
    text = input("Hi, say something: ")
    s.sendall(text)