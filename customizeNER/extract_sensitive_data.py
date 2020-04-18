# -*- coding: utf-8 -*-
from filterSentenceByVerb.get_verb_entities import *
import sys
import getopt


def main():
    inputSentence = get_args(sys.argv[1:])
    #sentence = "If any portion of the Site or Services requires You to register, You will provide true and accurate information in your user profile."
    object = policy_verb_entity.predict(inputSentence,inputSentence)
    object.extractEntity()
    print(object.phrase_set)



def get_args(argv):
    inputSentence = ''

    try:
        opts, args = getopt.getopt(argv, "hi:", ["isentence="])
    except getopt.GetoptError:
        print('extract_sensitive_data.py -i <sentence> ')
        sys.exit(2)
    for opt, arg in opts:
        if opt == '-h':
            print('extract_sensitive_data.py -i <sentence>')
            sys.exit()
        elif opt in ("-i", "--isentence"):
            inputSentence = arg


    return inputSentence


if __name__ == "__main__":
    main()