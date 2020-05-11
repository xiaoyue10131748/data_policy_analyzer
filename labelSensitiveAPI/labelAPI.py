# -*- coding: utf-8 -*-

import sys
import getopt
from filterSentenceByVerb.assign_features import write
import pandas as pd
import os
import spacy
from allennlp.predictors.predictor import Predictor
import glob

def main():

    xlsx_list = get_raw_file()
    print(xlsx_list)
    for filename in xlsx_list:
        if filename != "/Users/huthvincent/Documents/research/malicious_library_hunting/data_policy_analyzer/raw data/API_documents/40_API_documentations/data/post_processed_data/200test.xlsx":
            continue
        f = filename.split("/data/post_processed_data/")[1]
        to_filemame = "/Users/huthvincent/Documents/research/malicious_library_hunting/data_policy_analyzer/raw data/API_documents/40_API_documentations/labelAPI/" + f
        print("===============================" + str(to_filemame))
        if os.path.exists(to_filemame):
            continue
        label_API(filename)

def label_API(filename):

    sheet = pd.read_excel(filename)
    sentence_list = sheet["hump_expression"]
    nlp = spacy.load("en_core_web_sm")
    consituency_preditor = Predictor.from_path("https://s3-us-west-2.amazonaws.com/allennlp/models/elmo-constituency-parser-2018.03.14.tar.gz")
    sensitive = []
    for index, sentence in enumerate(sentence_list):
        if len(str(sentence)) <5:
            sensitive.append("nan")
            continue
        phrase_set, sensitive_data = extractEntity(sentence,nlp,consituency_preditor)

        ## 如果没有匹配到，判断sensitive token in the sentence
        if len(phrase_set) == 0:
            sen_list = check_sensitive_token(sentence)
            for item in sen_list:
                phrase_set.add(item)
        sensitive.append(str(phrase_set))
    sheet["labelAPI"] = sensitive
    f = filename.split("/data/post_processed_data/")[1]
    to_filemame = "/Users/huthvincent/Documents/research/malicious_library_hunting/data_policy_analyzer/raw data/API_documents/40_API_documentations/labelAPI/" + f
    sheet.to_excel(to_filemame, index=False, encoding="utf8",
                header=["class_name", "class_description","method","method_description","data_type","hump_expression","is_hump","labelAPI"])


def check_sensitive_token(sentence):
    necessary_list = ["token", "id", "ID", "Id","Token","id.","ID.","GUID","ids","ids."]
    word_list = sentence.split()
    sen_list = []
    for s in necessary_list:
        for w in word_list :
            if s == w:
                sen_list.append(s)
    return sen_list




def get_raw_file():
    xlsx_list = glob.glob('/Users/huthvincent/Documents/research/malicious_library_hunting/data_policy_analyzer/raw data/API_documents/40_API_documentations/data/post_processed_data/*.xlsx')
    print(u'have found %s csv files' % len(xlsx_list))
    print(u'正在处理............')
    return xlsx_list



# get sensitive data items and their phrase
# input: Vungle and its Demand Partners use Tracking Technologies in order to collect certain Ad Data.
# output: {'certain Ad Data'}, ['Ad', 'Data']
def extractEntity(sentence,nlp,consituency_preditor):
    doc = nlp(sentence)
    pharse_list = generate_testFile(doc)
    testFile = "tmp.tsv"

    write(testFile, consituency_preditor)
    to_filemame = "tmp_feature_v1.tsv"
    generate_results(to_filemame)
    sensitive_data = getLabeledWord()
    phrase_set = set()
    for data in sensitive_data:
        for phrase in pharse_list:
            p_list = phrase.split(" ")
            if data in p_list:
                phrase_set.add(phrase)
    # print(phrase_set)
    deleteTmpFile()
    phrase_set = phrase_set
    sensitive_data = sensitive_data
    return phrase_set, sensitive_data


def generate_testFile(doc):
    sentence_list = [token.text for token in doc]
    # assign O to each word
    label_list = ["O" for i in range(0, len(sentence_list))]
    data = {}
    data["sentence_list"] = sentence_list
    data["label_list"] = label_list
    df = pd.DataFrame(data)
    df.to_csv("tmp.tsv", sep='\t', index=False, header=False, encoding="utf8", mode='w')

    # Merge the noun phrases
    for phrase in list(doc.noun_chunks):
        phrase.merge(phrase.root.tag_, phrase.root.lemma_, phrase.root.ent_type_)
    pharse_list = [phrase.text for phrase in doc]
    # print("====================================================================")
    # print(pharse_list)
    return pharse_list


# invoke trained model and get the results
# parameter: testFile
def generate_results(to_filemame):
    curr = os.getcwd()
    ner_path = "../customizeNER/"
    os.chdir(ner_path)
    cmd = "java -jar ner.jar " + curr + "/" + to_filemame
    os.system(cmd)
    del_file1 = "rm -rf features-true.txt"
    del_file2 = "rm -rf true"
    os.system(del_file1)
    os.system(del_file2)
    os.chdir(curr)

# read the result.txt file and return word whose label is SEC
def getLabeledWord():
    # filename = "/Users/huthvincent/Desktop/paper_works/filter_Sentence_Based_Verb/ner_model/result.txt"
    filename = "../customizeNER/result.txt"
    f = open(filename)
    content = f.readlines()
    f.close()
    sensitive_data = []
    for row in content:
        item_list = row.split("\t")
        # print("==============" + str(len(item_list)))
        # print(item_list)
        # print("\n")
        if len(item_list) < 3:
            continue
        if "SEC" in item_list[2]:
            sensitive_data.append(item_list[0])
    return sensitive_data

def deleteTmpFile():
    del_file1 = "rm -rf tmp.tsv"
    del_file2 = "rm -rf tmp_feature_v1.tsv"
    # del_file3 = "rm -rf /Users/huthvincent/Desktop/paper_works/filter_Sentence_Based_Verb/ner_model/result.txt"
    del_file3 = "rm -rf ../customizeNER/result.txt"
    os.system(del_file1)
    os.system(del_file2)
    os.system(del_file3)




if __name__ == "__main__":
    main()