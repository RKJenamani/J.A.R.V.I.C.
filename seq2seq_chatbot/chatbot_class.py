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

from seq2seq_chatbot.preprocessing.utils import loadPrepareData, batch2TrainData
from seq2seq_chatbot.model.model import *
from seq2seq_chatbot.eval import GreedySearchDecoder, evaluateInput
from seq2seq_chatbot.train import trainIters

PAD_token = 0  # Used for padding short sentences
SOS_token = 1  # Start-of-sentence token
EOS_token = 2  # End-of-sentence token

MAX_LENGTH = 40
USE_CUDA = torch.cuda.is_available()
device = torch.device("cuda" if USE_CUDA else "cpu")

class chatbot:
    def __init__(self, file_name, model_name, corpus_name, att_model='concat', hidden_size=500, encoder_n_layers=2, decoder_n_layers=2, dropout=0.1, batch_size=32, save_dir='seq2seq_chatbot/models', loadFilename=None, checkpoint_iter=4000):
        self.model_name = model_name
        self.corpus_name = corpus_name
        self.attn_model = att_model
        self.hidden_size = hidden_size
        self.encoder_n_layers = encoder_n_layers
        self.decoder_n_layers = decoder_n_layers
        self.dropout = dropout
        self.batch_size = batch_size
        self.save_dir = save_dir, 
        self.voc, self.pairs = loadPrepareData("my_data", corpus_name, file_name, "preprocessing/data" )
        # Set checkpoint to load from; set to None if starting from scratch
        self.loadFilename =loadFilename
        self.checkpoint_iter = checkpoint_iter
        #self.loadFilename = os.path.join(save_dir, model_name, corpus_name,
        #                            '{}-{}_{}'.format(encoder_n_layers, decoder_n_layers, hidden_size),
        #                            '{}_checkpoint.tar'.format(checkpoint_iter))


        # Load model if a loadFilename is provided
        if self.loadFilename:
            # If loading on same machine the model was trained on
            self.checkpoint = torch.load(loadFilename)
            # If loading a model trained on GPU to CPU
            #checkpoint = torch.load(loadFilename, map_location=torch.device('cpu'))
            self.encoder_sd = self.checkpoint['en']
            self.decoder_sd = self.checkpoint['de']
            self.encoder_optimizer_sd = self.checkpoint['en_opt']
            self.decoder_optimizer_sd = self.checkpoint['de_opt']
            self.embedding_sd = self.checkpoint['embedding']
        #    self.s voc.__dict__ = checkpoint['voc_dict']
        else:
            self.checkpoint = None


        print('Building encoder and decoder ...')
        # Initialize word embeddings
        self.embedding = nn.Embedding(self.voc.num_words, hidden_size)
        if self.loadFilename:
            self.embedding.load_state_dict(self.embedding_sd)
        # Initialize encoder & decoder models
        self.encoder = Encoder(self.hidden_size, self.embedding, self.encoder_n_layers, self.dropout)
        self.decoder = Decoder(self.attn_model, self.embedding, self.hidden_size, self.voc.num_words, self.decoder_n_layers, self.dropout)
        if self.loadFilename:
            self.encoder.load_state_dict(self.encoder_sd)
            self.decoder.load_state_dict(self.decoder_sd)
        # Use appropriate device
        self.encoder = self.encoder.to(device)
        self.decoder = self.decoder.to(device)
        print('Models built and ready to go!')
        self.searcher = None
    
    def train(self,learning_rate, n_iterations, print_every, save_every, clip=50.0, teacher_forcing_ratio=1.0, decoder_learning_ratio=2.0):

        # Ensure dropout layers are in train mode
        self.encoder.train()
        self.decoder.train()

        # Initialize optimizers
        print('Building optimizers ...')
        encoder_optimizer = optim.Adam(self.encoder.parameters(), lr=learning_rate)
        decoder_optimizer = optim.Adam(self.decoder.parameters(), lr=learning_rate * decoder_learning_ratio)
        if self.loadFilename:
            encoder_optimizer.load_state_dict(self.encoder_optimizer_sd)
            decoder_optimizer.load_state_dict(self.decoder_optimizer_sd)

        # Run training iterations
        print("Starting Training!")
        trainIters(self.model_name, self.voc, self.pairs, self.encoder, self.decoder, encoder_optimizer, decoder_optimizer,
                self.embedding, self.encoder_n_layers, self.decoder_n_layers, self.save_dir, n_iterations, self.batch_size,
                print_every, save_every, clip, self.corpus_name, self.loadFilename,teacher_forcing_ratio, self.hidden_size, self.checkpoint )

    def chat(self):
        self.encoder.eval()
        self.decoder.eval()
        self.searcher = GreedySearchDecoder(self.encoder, self.decoder)
    
    def chat_output(self, input_str):
        return evaluateInput(self.encoder, self.decoder, self.searcher, self.voc, input_str)