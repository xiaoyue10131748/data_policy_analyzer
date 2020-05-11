import glob
import nltk
import pandas as pd
import re
import numpy as np
import os

def main():
    xlsx_list = get_raw_file()
    print(xlsx_list)
    for filename in xlsx_list:
        #if filename != "/Users/huthvincent/Documents/research/malicious_library_hunting/data_policy_analyzer/raw data/API_documents/40_API_documentations/data/raw data/test.csv":
            #continue
        part = filename.split("/data/raw data/")[1].replace(".csv", ".xlsx")
        to_filemame = "/Users/huthvincent/Documents/research/malicious_library_hunting/data_policy_analyzer/raw data/API_documents/40_API_documentations/data/post_processed_data/" + part
        print("===============================" + str(to_filemame))
        if os.path.exists(to_filemame):
            continue
        process_hump(filename)



def process_hump(filename):
    sheet = pd.read_csv(filename,index_col=False)
    sentence_list = sheet["method_description"]
    words = set(nltk.corpus.words.words())
    hump_expression = []
    is_hump = []
    for index, sentence in enumerate(sentence_list):
        if len(str(sentence)) < 5:
            hump_expression.append("")
            is_hump.append("False")
            continue
        ## filter non-english-character
        ## file wechat and kakao need to clean the sentence first
        sentence = clean(sentence,words)

        new_sentence, is_contain = contains_hump(sentence)
        hump_expression.append(new_sentence)
        is_hump.append(is_contain)


    sheet["hump_expression"] = hump_expression
    sheet["is_hump"] = is_hump
    to_filemame = "/Users/huthvincent/Documents/research/malicious_library_hunting/data_policy_analyzer/raw data/API_documents/40_API_documentations/data/post_processed_data/" + \
                  filename.split("/data/raw data/")[1].replace(".csv", ".xlsx")
    sheet.to_excel(to_filemame, index=False, encoding="utf8",
                header=["class_name", "class_description","method","method_description","data_type","hump_expression","is_hump"])


def clean(sentence,words):

    sent = sentence
    #new_sent = " ".join(w for w in nltk.wordpunct_tokenize(sent) if w.lower() in words or not w.isalpha())
    new_sent = " ".join(w for w in nltk.wordpunct_tokenize(sent) if w.lower() in words)
    return new_sent


def get_raw_file():
    xlsx_list = glob.glob('/Users/huthvincent/Documents/research/malicious_library_hunting/data_policy_analyzer/raw data/API_documents/40_API_documentations/data/raw data/*.csv')
    print(u'have found %s csv files' % len(xlsx_list))
    print(u'正在处理............')
    return xlsx_list



def contains_hump(sentence):
    word_list = sentence.split()
    new_word_list = []
    is_contain = False
    for word in word_list:
        new_word, flag = hump2underline(word)
        new_word_list.append(new_word)
        if flag:
            is_contain = True
    new_sentence = " "
    new_sentence = new_sentence.join(new_word_list)
    return new_sentence, is_contain





def hump2underline(hunp_str):
    '''
    驼峰形式字符串转成下划线形式
    :param hunp_str: 驼峰形式字符串
    :return: 字母全小写的下划线形式字符串
    '''
    # 匹配正则，匹配小写字母和大写字母的分界位置
    p = re.compile(r'([a-z]|\d)([A-Z])')
    # 这里第二个参数使用了正则分组的后向引用
    sub = re.sub(p, r'\1 \2', hunp_str).lower()
    flag = False
    if hunp_str.lower() == sub:
        sub = hunp_str
    elif hunp_str.lower() != sub:
        flag = True
    return sub,flag





if __name__ == "__main__":
    new_sentence, is_contain = contains_hump("Set the attachmentID of the item to share.")
    print(new_sentence)
    print(is_contain)
    main()