# -*- coding: utf-8 -*-
import scrapy
from scrapy.linkextractors import LinkExtractor
from maliciousLibrarySpider.items import MaliciouslibraryspiderItem
from urllib.parse import urlparse
from urllib.parse import urljoin
from selenium import webdriver
from scrapy.xlib.pydispatch import dispatcher
from scrapy import signals
import time
from scrapy import signals
from lxml import etree
import json
from selenium.webdriver.common.desired_capabilities import DesiredCapabilities
import re
import csv
import pandas as pd



class NlpSpider(scrapy.Spider):
    name = 'nlp'



    def __init__(self,url,count, *args, **kwargs):
        self.count = count
        self.allowed_domains = [urlparse(url).hostname.strip()]
        print("11111111111111111" + self.allowed_domains[0])
        self.start_urls = [url]
        self.log = ""
        self.originUrl = ""
        super(NlpSpider, self).__init__(*args, **kwargs)
        d = DesiredCapabilities.CHROME
        d['goog:loggingPrefs'] = {'performance': 'ALL'}
        DRIVER_BIN = "/usr/local/chromedriver"
        self.browser = webdriver.Chrome(desired_capabilities=d, executable_path = DRIVER_BIN)
        dispatcher.connect(self.spider_closed, signals.spider_closed)


    def parse(self, response):
        # 打印spider正在进行的事务

        #将源码写入文档
        filename = self.allowed_domains[0]
        # test中就是html网页的文本信息
        text = response.text
        print(response.url)

        data = response.text
        tree = etree.HTML(data)
        #content_list = response.xpath('//div//text()').extract()
        
        content_list = tree.xpath('//p//text()')
        #content_list = tree.xpath('//body//text()')
        print(content_list)



        #self.writeInTxt(self.allowed_domains[0],content_list,response.url)
        self.writeToExcel(self.allowed_domains[0],content_list)


    def spider_closed(self,spider):
        self.browser.quit()
        time.sleep(3)
        print("spider closed")


    def writeToExcel(self,filename,content):
        path = "/Users/huthvincent/Desktop/paper_works/clean_data_40_tos/40_tos_with_paragraph/data/paragraph/" + filename + ".xlsx"
        data = {}
        data["paragraph"] = content
        df = pd.DataFrame(data)
        df.to_excel(path, index=False, encoding="utf8", columns=["paragraph"])


