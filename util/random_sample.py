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
import stanza
import ast



def main():
	filename = "/Users/huthvincent/Documents/research/malicious_library_hunting/data_policy_analyzer/raw data/API_documents/40_API_documentations/evaluate/evaluate_label_API.xlsx"
	to_filename = "/Users/huthvincent/Documents/research/malicious_library_hunting/data_policy_analyzer/raw data/API_documents/40_API_documentations/evaluate/here.xlsx"
	df = pd.read_excel(filename,sheet_name = "here")
	df2=df.sample(frac=0.06)
	#df2.to_excel(to_filename,index=False,encoding="utf8", header = ["sentence_list", "align_paragraph", "subject_co_reference","co_reference_list","verb_subject_list","score","predict_label","verb_entity_list_without_filter","verb_entity_list_with_filter","nmod_entity_list","all_matched_condition_list","pattern_condition_list"])
	df2.to_excel(to_filename,index=False,encoding="utf8", header = ["class_name","class_description","method","method_description","data_type","labelAPI"])




#read raw data
def read_data(filename):
    sheet = pd.read_excel(filename)
    return sheet




if __name__ == "__main__":
    main()