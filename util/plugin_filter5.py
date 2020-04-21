# -*- coding: utf-8 -*-
import pandas as pd
from filterSentenceByVerb.assign_features import write
from filterSentenceByVerb.get_nmod_of_entities import *
from filterSentenceByVerb.convert_word_format import *
from filterSentenceByVerb.request_co_reference import *

nlp = spacy.load("en_core_web_sm")

def filter(sentence):
    doc = nlp(sentence)
    flag = False
    ### condition 5: move "means"
    for phrase in list(doc.noun_chunks):
        phrase.merge(phrase.root.tag_, phrase.root.lemma_, phrase.root.ent_type_)
    for item in doc:
        if item.pos_ == "VERB":
            if "mean" in item.text:
                flag =  True
            break
    return flag


# read raw data
def read_data(filename):
    sheet = pd.read_excel(filename)
    return sheet


def get_raw_file():
    xlsx_list = glob.glob('../raw data/40_post_processed_data/*.xlsx')  # get all the filenames of the prepossed 40 toses
    print(u'have found %s xlsx files' % len(xlsx_list))
    print(u'正在处理............')
    return xlsx_list



def main():
    xlsx_list = get_raw_file()
    print(xlsx_list)
    for filename in xlsx_list:
        to_filemame = "../raw data/40_post_processed_data/new/" + filename.split("40_post_processed_data/")[1]
        print("===============================" + str(to_filemame))
        if os.path.exists(to_filemame):
            continue
        getNewResults(filename)


def getNewResults(filename):
    to_filemame = "../raw data/40_post_processed_data/new/" + filename.split("40_post_processed_data/")[1]
    sheet = read_data(filename)
    for i, v in sheet["sentence_list"].items():
        label = sheet["predict_label"][i]
        sentence = sheet["sentence_list"][i]
        if label == 0:
            continue
        if filter(sentence):
            print(sentence)
            print("============================")
            sheet.loc[i, "predict_label"] = 0
            sheet.loc[i, "verb_entity_list_with_filter"] =""
            sheet.loc[i, "nmod_entity_list"]=""

    sheet.to_excel(to_filemame, index=False, encoding="utf8",
                header=["sentence_list", "align_paragraph", "subject_co_reference", "co_reference_list",
                        "verb_subject_list", "score", "predict_label", "verb_entity_list_without_filter",
                        "verb_entity_list_with_filter", "nmod_entity_list"])


if __name__ == "__main__":
    main()