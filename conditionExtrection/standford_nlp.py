from filterSentenceByVerb.assign_features import write
import os
import pandas as pd
import spacy

def extractEntity(sentence, predictor):
    pharse_list = generate_testFile(sentence)
    testFile = "tmp.tsv"

    write(testFile, predictor)
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
    return phrase_set, sensitive_data


def deleteTmpFile():
    del_file1 = "rm -rf tmp.tsv"
    del_file2 = "rm -rf tmp_feature_v1.tsv"
    del_file3 = "rm -rf /Users/huthvincent/Desktop/paper_works/filter_Sentence_Based_Verb/ner_model/result.txt"
    os.system(del_file1)
    os.system(del_file2)
    os.system(del_file3)


# read the result.txt file and return word whose label is SEC
def getLabeledWord():
    filename = "/Users/huthvincent/Desktop/paper_works/filter_Sentence_Based_Verb/ner_model/result.txt"
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


def generate_testFile(sentence):
    nlp = spacy.load("en_core_web_sm")
    doc = nlp(sentence)
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
    cmd = "java -jar /Users/huthvincent/Desktop/paper_works/extract_condition/policy_extraction/customizeNER/yue_model.jar " + to_filemame
    os.system(cmd)
    del_file1 = "rm -rf features-true.txt"
    del_file2 = "rm -rf true"
    os.system(del_file1)
    os.system(del_file2)
