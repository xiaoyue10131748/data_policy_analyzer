# -*- coding: utf-8 -*-

# Define your item pipelines here
#
# Don't forget to add your pipeline to the ITEM_PIPELINES setting
# See: https://doc.scrapy.org/en/latest/topics/item-pipeline.html
import os
import csv

import csv

class MaliciouslibraryspiderPipeline(object):
    # init 方法是可选的，作为类的初始化
    def __init__(self):
        # csv 文件的位置，无需事先创建
        store_file = os.path.dirname(__file__) + '/spiders/mapping.csv'
        # 打开（创建）文件
        self.file = open(store_file,"w",encoding='utf_8_sig')
        columns = ['url','content']
        self.writer = csv.DictWriter(self.file, columns)
        #self.writer.writeheader()


    # 处理Item 数据， 必须写的
    def process_item(self, item, spider):

        # mobiborn
        '''
        for str in item["content"][0].split("."):
            row = {}
            row['url'] = item["url"]
            row['content'] = str
            self.writer.writerow(row)
        '''

        '''
        # umeng
        self.writer.writerow(item)
        return item
        '''

        '''
        # lexis
        for str in item["content"][0].split('\n'):
            if str.strip():
                row = {}
                row['url'] = item["url"]
                row['content'] = str.strip()
                self.writer.writerow(row)
        return item
        '''
        # appsgeyser
        item['content'] = item['content'][0].replace("\n", " ")
        self.writer.writerow(item)
        return item


    # 可选，执行结束后调用
    def close_spider(self, spider):
        self.file.close()