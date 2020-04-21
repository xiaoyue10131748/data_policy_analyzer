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
	filename = "../raw data/40_post_processed_data/assign_condition/total.xlsx"
	to_filename = "../raw data/40_post_processed_data/assign_condition/200_extract_reference.xlsx"
	df = read_data(filename)
	df2=df.sample(frac=0.1)
	df2.to_excel(to_filename,index=False,encoding="utf8", header = ["sentence_list", "align_paragraph", "subject_co_reference","co_reference_list","verb_subject_list","score","predict_label","verb_entity_list_without_filter","verb_entity_list_with_filter","nmod_entity_list","all_matched_condition_list","pattern_condition_list"])





#read raw data
def read_data(filename):
    sheet = pd.read_excel(filename)
    return sheet




if __name__ == "__main__":
    main()