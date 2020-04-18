# -*- coding: utf-8 -*-
import glob
import os
import json
import csv
import spacy
import os
from allennlp.predictors.predictor import Predictor

import numpy as np

def main():

    getAllResults()


    
    



def getAllResults():
    csv_list = glob.glob('../data/*.tsv') #查看同文件夹下的csv文件数
    print(u'共发现%s个TSV文件'% len(csv_list))
    print(u'正在处理............')
    for i in csv_list: #循环读取同文件夹下的csv文件
        fr = open(i,'rb').read()
        with open('../data/result.tsv','ab') as f: #将结果保存为result.csv
            f.write(fr)
    print(u'合并完毕！')







   
if __name__ == "__main__":
    main()
