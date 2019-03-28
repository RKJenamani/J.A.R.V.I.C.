# first of all import the socket library 
import socket			 

# send message to client

# next create a socket object 
s = socket.socket()		 
print "Socket successfully created"

# reserve a port on your computer in our 
# case it is 12345 but it can be anything
host = "localhost" 
port = 2006				

# Next bind to the port 
# we have not typed any ip in the ip field 
# instead we have inputted an empty string 
# this makes the server listen to requests 
# coming from other computers on the network
s.setsockopt(socket.SOL_SOCKET, socket.SO_REUSEADDR, 1) 
s.bind((host, port))		 
print "socket binded to %s" %(port) 

# put the socket into listening mode 
s.listen(5)	 
print "socket is listening"			

c, addr = s.accept()	 
print 'Got connection from', addr 


def send_message() :
    try :
        print "Type message :"
        message = raw_input()
        # connection.send(message)  
        # print message

        return message

    except :
        print "ERROR"
        return -1


# a forever loop until we interrupt it or 
# an error occurs 
msg = None
while msg != "exit": 

    # Establish connection with client. 
    print("Send message?(Y/N)")
    command = raw_input()
    if command is "Exit" or command is "exit" :
        break
    if command is not "N" and command is not "n" :
        message = send_message()
        c.send(str(message) + "\n")
    msg = c.recv(1024)
    print "Client :" + msg[2:] 
    # Close the connection with the client 
c.close() 
