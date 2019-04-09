import socket 
import struct 
from thread import *
import threading  
from authenticate import authentication 
from history import chat_history
from pender_chatbot.response_generator import response

import sys
import argparse

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
			# print('Bye')  
			return "N"
		data = data[2:]
		return data.decode()

	def close_port(self):
		self.s.close()

def chat_session(chat_replies,args):
	jarvic=chatbot()
	user_auth = authentication()
	hist = chat_history()
	login_true=0  #Set this to zero11
	while(login_true==0):
		msg=jarvic.receive_msg()
		if not msg: return 0
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
	# jarvic.send_msg("u$history")
	user_history=hist.load_history(values[0])
	if(user_history != None):
		jarvic.send_msg(user_history)
	print("IN SESSION")
	while(True):
		msg=jarvic.receive_msg()
		# print("Message recievd :",msg)
		if (msg=="N"): 
			# print("returning")
			break
		# print("iteration")
		hist.add_to_history(values[0],"u",msg)
		reply=chat_replies.chat(msg)
		# reply="lolwut"
		jarvic.send_msg(reply)
		hist.add_to_history(values[0],"c",reply)
	jarvic.close_port()
	
if __name__ == '__main__':

	assert sys.version_info >= (3, 3), \
	"Must be run in Python 3.3 or later. You are running {}".format(sys.version)
	parser = argparse.ArgumentParser()
	parser.add_argument('--save_dir', type=str, default='pender_chatbot/models/reddit',
					   help='model directory to store checkpointed models')
	parser.add_argument('-n', type=int, default=500)
	parser.add_argument('--prime', type=str, default=' ')
	parser.add_argument('--beam_width', type=int, default=2)
	parser.add_argument('--temperature', type=float, default=1.0)
	parser.add_argument('--topn', type=int, default=-1)
	parser.add_argument('--relevance', type=float, default=-1.)
	args = parser.parse_args()

	
	chat_replies=response(args)

	while(True):
		chat_session(chat_replies,args)
	chat_replies.close_sess()
	
