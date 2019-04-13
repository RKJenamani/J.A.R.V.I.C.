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

# from preprocessing.utils import loadPrepareData, batch2TrainData
print("Hi")
from seq2seq_chatbot.chatbot_class import chatbot

PAD_token = 0  # Used for padding short sentences
SOS_token = 1  # Start-of-sentence token
EOS_token = 2  # End-of-sentence token

MAX_LENGTH = 40
USE_CUDA = torch.cuda.is_available()
device = torch.device("cuda" if USE_CUDA else "cpu")


# pickle_pair_in = open("preprocessing/data/cornell/cornell_pairs.pickle", "rb")
# pairs = pickle.load(pickle_pair_in)
# pickle_pair_in.close()
# print("Pairs loaded")
# print("Length of pairs: {}".format(len(pairs)))
# print(pairs[0:10])
# # pickle_voc_in = open("preprocessing/data/cornell/cornell_voc.pickle", "rb")
# # voc = pickle.load(pickle_voc_in)
# voc, _ = loadPrepareData("cornell", "cornell", "", "", pairs)


# save_dir = "preprocessing/data/"
# voc, pairs = loadPrepareData("imput.txt", "my_corpus", "preprocessing/data/input.txt", save_dir)
# # Print some pairs to validate
# print(len(pairs))
# print("\npairs:")
# for pair in pairs[:10]:
#     print(pair)

print("Vocs Loaded")
C = chatbot(file_name = "preprocessing/data/happy/input.txt", model_name = "new_model", corpus_name = 'happy')
C.train(learning_rate = 0.0001, n_iterations = 10000,print_every = 1, save_every=100)
# C.chat()
# while True:
#     str = input('\n>')
#     s = C.chat_output(voc = voc, input_str = str)
#     print(s)