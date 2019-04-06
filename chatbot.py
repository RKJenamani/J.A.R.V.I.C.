import socket 
import struct 
from thread import *
import threading  

class chatbot:

    def __init__(self):
        host = "127.0.0.1" 
        port = 2003 
        self.s = socket.socket(socket.AF_INET, socket.SOCK_STREAM) 
        self.s.bind((host, port)) 
        print("socket binded to post", port) 
        self.s.listen(5) 
        print("socket is listening") 
        self.c, addr = self.s.accept() 
        print('Connected to :', addr[0], ':', addr[1])

    def send_msg(self,msg):

        if msg is "\n" :
            print("ERROR")
        msg = msg + "\n"
        msg = msg.encode("utf-8", 'ignore')
        self.c.send(struct.pack("!H", len(msg)))
        self.c.send(msg)

    def receive_msg(self): 
        data = self.c.recv(1024) 
        if not data: 
            print('Bye')  
            return "N"
        data = data[2:]
        return data.decode()
    def close_port(self):
        self.s.close() 

if __name__ == '__main__':
    jarvic = chatbot()
    jarvic.send_msg("HEY")
    print(jarvic.receive_msg())
    jarvic.close_port()