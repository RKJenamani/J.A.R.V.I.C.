import socket 
import struct 
from thread import *
import threading  
from authenticate import authentication 
from history import chat_history

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
		# msg = msg + "\n"
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
	user_auth = authentication()
	hist = chat_history()
	login_true=0
	while(login_true==0):
		msg=jarvic.receive_msg()
		values=msg.split('$')
		print(values)
		if(len(values)==2): 
			login_true=user_auth.login(values)
			if(login_true):
				jarvic.send_msg("y")
				print("ALOUD")
			else:
				jarvic.send_msg("n")
		else:
			user_auth.signup(values)
			jarvic.send_msg("y")

	jarvic.send_msg(hist.load_history(values[0]))

	jarvic.close_port()
