# import socket programming library 
import socket 
import struct 
# from _thread import *
# import threading 

class server :

    # print_lock = threading.Lock() 
    def __init__(self, host, port):
        self.host = host
        self.port = port
        self.s = socket.socket(socket.AF_INET, socket.SOCK_STREAM) 
        self.s.bind((self.host, self.port)) 
        print("socket binded to port: " + str(self.port))

    def initialise_connection(self):
        self.s.listen(5) 
        print("Socket is listening")

        # establish connection with client 
        self.c, self.addr = self.s.accept() 

        print("Connected to : " + str(self.addr[0]) + ":" + str(self.addr[1]))

    def receive_msg(self): 
            # data received from client 
            data = self.c.recv(1024) 
            if not data: 
                return "Nan"
            else :
                data = data[2:]
                # print("\nUser: " + data.decode()) 
                return data.decode()    

    def send_msg(self, msg):
            # msg = input("\n")
            if msg is "" :
                print("ERROR")
                # break
            msg = msg + "\n"
            msg = msg.encode("utf-8", 'ignore')
            self.c.send(struct.pack("!H", len(msg)))
            self.c.send(msg)
