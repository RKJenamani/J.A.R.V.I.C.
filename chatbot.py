import socket 
import struct 
from _thread import *
import threading  
from authenticate import authentication 
from history import chat_history
from sentiment_analysis import sentiment_analysis
import time
import torch
from torch.jit import script, trace
import torch.nn as nn
from torch import optim
import torch.nn.functional as F
import csv
import random
import re
import os
import unicodedata
import codecs
from io import open
import itertools
import math
import pickle
import sys

import sys
import argparse

from seq2seq_chatbot.chatbot_class import chatbot as response

USE_CUDA = torch.cuda.is_available()
device = torch.device("cuda" if USE_CUDA else "cpu")

class chatbot:

	def __init__(self,input_host,input_port):
		host = input_host 
		port = input_port
		self.s = socket.socket(socket.AF_INET, socket.SOCK_STREAM) 
		self.s.setsockopt(socket.SOL_SOCKET, socket.SO_REUSEADDR, 1)
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
		print("Sending: ",msg)
		self.c.send(msg)

	def receive_msg(self): 
		data = self.c.recv(1024) 
		if not data: 
			# print('Bye')  
			return "N"
		data = data[2:]
		print("Recieveing: ",str(data.decode()).strip('\n'))
		return str(data.decode()).strip('\n')

	def close_port(self):
		self.s.close()
		# print("Port closed")

def chat_session(sad_replies, happy_replies, emotion_model,args):
# def chat_session(emotion_model,args):
	try:
		jarvic = chatbot(args.host, args.port)
		user_auth = authentication(args.mysqlpass)
		hist = chat_history(args.mysqlpass)
		login_true = 0  
		while(login_true == 0):
			msg=jarvic.receive_msg()
			if not msg: return 0
			values=msg.split('$')
			print(values)
			if(len(values)==2): 
				login_true=user_auth.login(values)
				if(login_true):
					jarvic.send_msg("y")
					print("LOGGED IN")
				else:
					jarvic.send_msg("n")
			elif(len(values)==3):
				reset_true=user_auth.reset_password(values)
				if(reset_true):
					jarvic.send_msg("y")
					print("RESET")
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
		# jarvic.send_msg("Hey")
		while(True):
			try: 
				msg=jarvic.receive_msg()
				# print("Message recievd :",msg)
				if (msg=="N"): 
					# print("returning")
					break
				# print("iteration")
				hist.add_to_history(values[0],"u",msg)
				# print("msg-",msg,"-")
				# letsee=str(msg)
				new_msg=msg.strip('\n')
				# print("letsee-",letsee,"-")
				# print("new_msg-",msg,"-")
				if(new_msg.find("suicide") != -1 or new_msg.find("kill") != -1 or new_msg.find("die") != -1 or new_msg.find("SOS") != -1):
					jarvic.send_msg("critical")
					reply="My crises systems have been triggered because I've recognized an emergency. The concerned authorities have been notified." 
					jarvic.send_msg(reply)
					print("\n\n")
					print("  ___ _ __ ___   ___ _ __ __ _  ___ _ __   ___ _   _ ")
					print(" / _ \ '_ ` _ \ / _ \ '__/ _` |/ _ \ '_ \ / __| | | |")
					print("|  __/ | | | | |  __/ | | (_| |  __/ | | | (__| |_| |")
					print(" \___|_| |_| |_|\___|_|  \__, |\___|_| |_|\___|\__, |")
					print("                          __/ |                 __/ |")
					print("                         |___/                 |___/")
					print("\n")
					print("                 username=",values[0])
					print("\n")
				else:
					emotion=emotion_model.predict_emotion(new_msg)
					jarvic.send_msg(emotion)
					if emotion == 'sad':	
						reply=sad_replies.chat_output(input_str = new_msg)
					else:
						reply = happy_replies.chat_output(input_str = new_msg)
					# reply="lolwut"
					jarvic.send_msg(reply)
				hist.add_to_history(values[0],"c",reply)
			except:
				print("Closing port...")
				jarvic.close_port()
				time.sleep(5)
				return
				jarvic = chatbot(args.host, args.port)
				user_auth = authentication(args.mysqlpass)
				hist = chat_history(args.mysqlpass)
				login_true = 0  
				while(login_true == 0):
					msg=jarvic.receive_msg()
					if not msg: return 0
					values=msg.split('$')
					print(values)
					if(len(values)==2): 
						login_true=user_auth.login(values)
						if(login_true):
							jarvic.send_msg("y")
							print("LOGGED IN")
						else:
							jarvic.send_msg("n")
					elif(len(values)==3):
						reset_true=user_auth.reset_password(values)
						if(reset_true):
							jarvic.send_msg("y")
							print("RESET")
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
	except KeyboardInterrupt:
		sys.exit()

	
if __name__ == '__main__':

	# assert sys.version_info >= (3, 3), \
	# "Must be run in Python 3.3 or later. You are running {}".format(sys.version)
	parser = argparse.ArgumentParser()
	parser.add_argument('--host', type=str, default='127.0.0.1')
	parser.add_argument('--port', type=int, default='2000')
	parser.add_argument('--mysqlpass', type=str, default='software')
	

	args = parser.parse_args()

	sad_replies = response(file_name="seq2seq_chatbot/preprocessing/data/input.txt",model_name = "new_model", corpus_name = 'sad', loadFilename = 'seq2seq_chatbot/models/new_model1/sad/2-2_500/7900_checkpoint.tar')
	happy_replies = response(file_name="seq2seq_chatbot/preprocessing/data/happy/input.txt",model_name = "new_model", corpus_name = 'happy', loadFilename = 'seq2seq_chatbot/models/new_model1/happy/2-2_500/7900_checkpoint.tar')
	# C.train(voc=voc, pairs = pairs, learning_rate = 0.0001, n_iterations = 10000,print_every = 1, save_every=100)
	sad_replies.chat()
	happy_replies.chat()

	emotion_model=sentiment_analysis()
	while(True):
		chat_session(sad_replies,happy_replies, emotion_model,args)
		# chat_session(emotion_model,args)

	
