# -*- coding: utf-8 -*-
import glob
import os
import json
import csv
import spacy
import os
from allennlp.predictors.predictor import Predictor
import numpy as np
import nltk
from nltk.parse.corenlp import CoreNLPParser
from nltk.tree import ParentedTree
from nltk.tree import *
def main():

    #getParser()

    #test_leaves()
    predictor = Predictor.from_path("https://s3-us-west-2.amazonaws.com/allennlp/models/elmo-constituency-parser-2018.03.14.tar.gz")
    sent = "Before you submit Your Content to the Snap API Services, you will ensure that you have the necessary rights (including the necessary rights from your users or other third party licensors) to grant Snap this license."

    testAllenNlp(sent,predictor)


def testAllenNlp(sent,predictor):
    tree = predict(sent,predictor)
    constituency_tree = Tree.fromstring(tree)
    constituency_tree.pretty_print()



def predict(sent,predictor):  
    results = predictor.predict(sentence=sent)  
    return results['trees']
   
    #print(results['hierplane_tree']["root"])
    #print(type(results['hierplane_tree']["root"]))



def test_leaves():
    parser = CoreNLPParser(url = "http://localhost:9000")
    parse, = parser.raw_parse("we will collect user informaiton, and google user emails")
    sen_list = ["we", "will", "collect", "user", "informaiton," ,"and", "google","user", "emails."]
    parse.pretty_print()
    newtree = ParentedTree.convert(parse)
    leaf_values = newtree.leaves()


    for i, word in enumerate(sen_list):
        node_index = find_closest_words(i,word,leaf_values)
        tree_location = newtree.leaf_treeposition(node_index)
        print(i)
        print(word)
        print("---------------------")
        print(node_index)
        print (newtree[tree_location[:-1]].leaves()[0])
        print("\n")


def mergeWords(sentence_list):
    sen = ""
    for word in sentence_list:
        sen += word
        sen += " "
    return sen.strip()



def parse_consituency_tree(sentence_list):

    pos_parent = []
    right_sublings_list =[]
    chunk_position=[]
    sen = mergeWords(sentence_list)
    parser = CoreNLPParser(url = "http://localhost:9000")
    parse, = parser.raw_parse(sen)
    parse.pretty_print()
    newtree = ParentedTree.convert(parse)
    leaf_values = newtree.leaves()
    for i, word in enumerate(sentence_list) :
        index=find_closest_words(i,word,leaf_values)
        if index >=0 and index <len(leaf_values):
            tree_location = newtree.leaf_treeposition(index)
            parent = newtree[tree_location[:-2]].label()
            pos_parent.append(parent)
            

            #####################find right_sibling###########################
            right_sibling = newtree[tree_location[:-1]].right_sibling()
            #count = calcuate_nodes((right_sibling))
            if  parent == "NP" and right_sibling is not None  and  calcuate_nodes(right_sibling)== 1:
                count = calcuate_nodes((right_sibling))
                #print(count)
                right_sublings_list.append(right_sibling.leaves()[0])
            else:
                right_sublings_list.append(" ")

            
            ###########################find chunk item position##########################
            height = newtree[tree_location[:-2]].height()
            #只处理最底层的NP tree_height == 3 
            if  parent == "NP" and height == 3:
                chunk_item_list = newtree[tree_location[:-2]].leaves()
                print(newtree[tree_location[:-2]].height())
                for i, item in enumerate(chunk_item_list):
                    if item ==leaf_values[index]:
                        chunk_position.append(i+1)
                        break

            else:
                chunk_position.append(" ")


        else:
            pos_parent.append("null")
            right_sublings_list.append("null")
            chunk_position.append(" ")
    return pos_parent,right_sublings_list,chunk_position



# 判断word1 和 word2 是否一致 
def same_word(word1, word2):
    if word1.strip()==word2.strip() or word1.strip() in word2.strip() or word2.strip() in word1.strip() :
        return True
    else:
        return False





def find_closest_words(index,word,leaf_values):
    if leaf_values[index] == word:
        return index

    left_index = index
    right_index = index

    while left_index >= 0 and not same_word(leaf_values[left_index],word) :
        left_index -=1

    while right_index < len(leaf_values) and not same_word(leaf_values[right_index],word):
        right_index +=1

    if (index - left_index) < (right_index-index):
        return left_index
    else:
        return right_index
    



def getParser():
    parser = CoreNLPParser(url = "http://localhost:9000")
    parse, = parser.raw_parse("Do not interfere with, intercept, disrupt, filter, or disable any features of Google or the Twitter API,  including the Twitter Content of embedded Tweets and embedded timelines")

    parse.pretty_print()

    newtree = ParentedTree.convert(parse)
    #for i, child in enumerate(newtree):
        #print(type(child))
    leaf_values = newtree.leaves()
    print(leaf_values[12:])
    if 'Twitter' in leaf_values[12:]:
        leaf_index = leaf_values.index('Twitter')
        tree_location = newtree.leaf_treeposition(leaf_index)
        print (tree_location[:-1])
        #print (newtree[tree_location[:-2]].right_sibling().leaves())
        #print (newtree[tree_location[:-2]].right_sibling().height())

        #print ( calcuate_nodes(newtree[tree_location[:-1]].right_sibling()))

        #print(newtree[tree_location[:-1]].right_sibling().height())
        #print (tree_location[:-2])
        #print (newtree[tree_location[:-1]].label())

        '''
        path = []
        for l in range(1,len(tree_location)):
            print(newtree[tree_location[:-l]].label())
            path.append(newtree[tree_location[:-l]].label())
        print(obtain_dash_list(path[::-1]))
        '''

        #subtree.left_sibling()

        parent_tree = newtree[tree_location[:-2]].leaves()
        print(parent_tree)

def calcuate_nodes(tree):
    count = 0
    for t in tree.subtrees():
        count += 1
    return count


def obtain_dash_list(list):
    str =""
    for item  in list:
        str += item
        str+="-"
    return str[:-1]


    







   
if __name__ == "__main__":
    main()
