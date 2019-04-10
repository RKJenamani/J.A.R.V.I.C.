from sklearn.naive_bayes import MultinomialNB
from sklearn.feature_extraction.text import CountVectorizer
from sklearn import metrics
import six.moves.cPickle as pickle

from nltk.corpus import stopwords
import string
import glob
import os
import re

class sentiment_analysis:

	def __init__ (self):
		f = open('train.pkl', 'rb')
		reviews = pickle.load(f)
		f.close()

		f = open('test.pkl', 'rb')
		test = pickle.load(f)
		f.close()
		self.vectorizer = CountVectorizer()
		train_features = self.vectorizer.fit_transform([r for r in reviews[0]])
		test_features = self.vectorizer.transform([r for r in test[0]])
		self.nb = MultinomialNB()
		self.nb.fit(train_features, [int(r) for r in reviews[1]])
		# predictions = self.nb.predict(test_features)
		# print(metrics.classification_report(test[1], predictions))
		# print("accuracy: {0}".format(metrics.accuracy_score(test[1], predictions)))

	def extract_words(self,sentences):
		result = []
		stop = stopwords.words('english')
		trash_characters = '?.,!:;"$%^&*()#@+/0123456789<>=\\[]_~{}|`'
		trans = string.maketrans(trash_characters, ' '*len(trash_characters))

		for text in sentences:
			text = re.sub(r'[^\x00-\x7F]+',' ', text)
			text = text.replace('<br />', ' ')
			text = text.replace('--', ' ').replace('\'s', '')
			text = text.translate(trans)
			text = ' '.join([w for w in text.split() if w not in stop])

			words = []
			for word in text.split():
				word = word.lstrip('-\'\"').rstrip('-\'\"')
				if len(word)>2 :
					words.append(word.lower())
			text = ' '.join(words)
			result.append(text.strip())
		return result

	def predict_emotion(self,user_input):
		sentences = []
		sentence = user_input
		sentences.append(sentence)
		input_features = self.vectorizer.transform(self.extract_words(sentences))
		prediction = self.nb.predict(input_features)
		if prediction[0] == 1 :
			output = "calm"
		else:
			output = "sad"
		return output 
		
if __name__ == '__main__':
	
	emotion_model=sentiment_analysis()
	while(True):
		user_input=raw_input("Input sentence: ")
		if(user_input=="exit"):
			break
		print(emotion_model.predict_emotion(user_input))

