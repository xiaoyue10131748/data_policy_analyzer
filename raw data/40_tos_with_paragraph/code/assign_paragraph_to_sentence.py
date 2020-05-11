# -*- coding: utf-8 -*-
import glob
import os
import json
import csv
import spacy
import os
from allennlp.predictors.predictor import Predictor
from nltk import word_tokenize, pos_tag, ne_chunk
from nltk.chunk import tree2conlltags
import pandas as pd
import numpy as np
import nltk
from nltk.parse.corenlp import CoreNLPParser
from nltk.tree import ParentedTree
from nltk.stem import PorterStemmer
from nltk.stem import LancasterStemmer
from nltk.stem import WordNetLemmatizer
from nltk.tree import *
import os



def main():
	nlp = spacy.load("en_core_web_sm")
	exist_files = get_exist_file()
	xlsx_list = get_raw_file()
	for f in xlsx_list:
		if f.split("../data/sentence/")[1] in exist_files:
			continue

		print(f)
		match_paragraph(nlp,f)

	
	




def match_paragraph(nlp,sentence_filename):
	paragrah_filename = "../data/paragraph/" + sentence_filename.split("/sentence/")[1]

	sentence_list = read_xlsx_data(sentence_filename,"sentence")
	paragraph_list = read_xlsx_data(paragrah_filename,"paragraph")
	#print(sentence_list)
	print(paragraph_list)
	for p in paragraph_list:
		print(p)
		print("======================")

	# make paragraph in to word list
	tokenize_paragraph = []
	for p in paragraph_list:
		word_list = []
		doc = nlp(p)
		for t in doc:
			word_list.append(t.text)
		tokenize_paragraph.append(word_list)

	#make sentence in to word list
	tokenize_sentence = []
	for s in sentence_list:
		word_list = []
		doc = nlp(s)
		for t in doc:
			word_list.append(t.text)
		tokenize_sentence.append(word_list)

	align_paragraph = []
	score_list = []
	for i, sen in enumerate(sentence_list):
		para,score= calculate_possibility(i, sentence_list,paragraph_list,tokenize_paragraph,tokenize_sentence)
		align_paragraph.append(para)
		score_list.append(score)


    #write to xlsx
	data = {}
	data["sentence_list"] = sentence_list
	data["align_paragraph"] = align_paragraph
	data["score"] = score_list
	df = pd.DataFrame(data)
	to_filemame = "../data/align_sentence_paragraph/" + sentence_filename.split("sentence/")[1]
	df.to_excel(to_filemame,index=False,encoding="utf8", columns = ["sentence_list", "align_paragraph", "score"])




def get_raw_file():
    xlsx_list = glob.glob('../data/sentence/*.xlsx') #查看同文件夹下的csv文件数
    print(u'共发现%s个CSV文件'% len(xlsx_list))
    print(u'正在处理............')
    return xlsx_list


def get_exist_file():
    xlsx_list = glob.glob('../data/align_sentence_paragraph/*.xlsx') #查看同文件夹下的csv文件数
    print(u'共发现%s个CSV文件'% len(xlsx_list))
    print(u'正在处理............')
    exist_files = [f.split("../data/align_sentence_paragraph/")[1] for f in xlsx_list]
    return exist_files



# 计算一个sentence里的单词，有多少是出现在paragraph中的
def calculate_possibility(index, sentence_list,paragraph_list,tokenize_paragraph,tokenize_sentence):
	word_in_sentence = tokenize_sentence[index]
	score = []
	for p_list in tokenize_paragraph:
		count = 0
		for word in word_in_sentence:
			if word in p_list:
				count += 1
		score.append(count/len(word_in_sentence))

	max_index = score.index(max(score))
	return paragraph_list[max_index], score[max_index]






def read_xlsx_data(filename,column):
    sheet = pd.read_excel(filename)
    return sheet[column].tolist()




   
if __name__ == "__main__":
    main()
