# -*- coding: utf-8 -*-
from filterSentenceByVerb.get_verb_entities import *
import sys
import getopt
from filterSentenceByVerb.assign_features import write

def main():
    #inputSentence = get_args(sys.argv[1:])
    inputSentence = "Returns whether the current access token is active or not."
    inputSentence = "Set the attachment ID of the item to share."
    inputSentence = "Helper to show the provided share content using the provided Fragment."
    inputSentence = "Gets the access token source indicating how this access token was obtained."
    inputSentence = "Sets the advertiser id collection flag for the application"

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