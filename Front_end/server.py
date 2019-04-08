# import socket programming library 
import socket 
import struct 
from _thread import *
import threading 

print_lock = threading.Lock() 

# thread fuction 
def threaded_receive(c): 
	while True: 

		# data received from client 
		data = c.recv(1024) 
		if not data: 
			print('Bye') 
	        # lock released on exit 
			print_lock.release() 
			break
		data = data[2:]
		print("\nUser: " + data.decode()) 

	# connection closed 
	c.close() 


def threaded_send(c):
	while True:
		msg = input("\n")
		if msg is "\n" :
			print("ERROR")
			break
		# msg = msg + "\n"
		msg = msg.encode("utf-8", 'ignore')
		c.send(struct.pack("!H", len(msg)))
		c.send(msg)
	c.close()


def Main(): 
	host = "10.147.148.105" 

	# reverse a port on your computer 
	# in our case it is 12345 but it 
	# can be anything 
	port = int(input("Port ?"))
	s = socket.socket(socket.AF_INET, socket.SOCK_STREAM) 
	s.bind((host, port)) 
	print("socket binded to post", port) 

	# put the socket into listening mode 
	s.listen(5) 
	print("socket is listening") 

	# a forever loop until client wants to exit 
	while True: 

		# establish connection with client 
		c, addr = s.accept() 

		# lock acquired by client 
		print('Connected to :', addr[0], ':', addr[1]) 

		print_lock.acquire() 

		# Start a new thread and return its identifier
		start_new_thread(threaded_send,(c,))
		start_new_thread(threaded_receive, (c,))
	s.close()

if __name__ == '__main__': 
	Main() 
