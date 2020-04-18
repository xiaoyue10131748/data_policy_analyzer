# -*- coding: utf-8 -*-
import scrapy
from scrapy.linkextractors import LinkExtractor
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


class NlpSpider(scrapy.Spider):
    name = 'nlp'
    filter_third_party = ["third party", "third parties", "any parties", "service provider"]

    #scrapy.selector.unified.SelectorList.extract_unquoted

    #def __init__(self,url,count, *args, **kwargs):
    def __init__(self, url, folder, *args, **kwargs):
        self.allowed_domains = [urlparse(url).hostname.strip()]
        print("11111111111111111" + self.allowed_domains[0])
        self.start_urls = [url]
        self.log = ""
        self.originUrl = ""
        self.folder = folder
        super(NlpSpider, self).__init__(*args, **kwargs)
        d = DesiredCapabilities.CHROME
        d['goog:loggingPrefs'] = {'performance': 'ALL'}
        #DRIVER_BIN = "/usr/local/chromedriver"
        DRIVER_BIN = "../webdriver/chromedriver"
        self.browser = webdriver.Chrome(desired_capabilities=d, executable_path = DRIVER_BIN)
        dispatcher.connect(self.spider_closed, signals.spider_closed)


    def parse(self, response):
        # 打印spider正在进行的事务

        #将源码写入文档
        #filename = self.allowed_domains[0]+str(self.count)
        # test中就是html网页的文本信息
        text = response.text
        #self.saveHtml(self.count,text)
        print(response.url)

        data = response.text
        tree = etree.HTML(data)
        #content_list = response.xpath('//div//text()').extract()
        '''
        print("=======================================")

        sentence = "Take any actions on a user’s behalf, including posting Twitter Content, following/unfollowing other users, modifying profile information, starting a Periscope Broadcast or adding hashtags or other data to the user's Tweets. A user authenticating through your Service does not constitute user consent."
        result = response.xpath('local-name(//*[contains(text(), $content)])', content=sentence)
        tag = result.extract()[0]
        print(tag)
        sublings = response.xpath('//*[contains(text(), $content)]/following-sibling::li', content=sentence)
        for item in sublings:
            print(item.xpath('string(.)').extract()[0])
            print("\n")
        print("=======================================")
        '''
        content_list = tree.xpath('//div//text()')

        cleanData = []
        i = 0
        while i < len(content_list):
            pair = []
            sentence = content_list[i].strip()
            print("my sentence is ")
            print(sentence)
            if not self.isVaildSentence(sentence):
                i += 1
                continue
            result = response.xpath('local-name(//*[contains(text(), $content)])', content=sentence)
            tag = result.extract()[0]
            if sentence.endswith(".") or sentence.endswith(":") or sentence.endswith(";") :
                pair.append(sentence)
                pair.append(tag)
                cleanData.append(pair)
            else:
                string = ""
                while i < len(content_list)-2 and not (sentence.endswith(".") or sentence.endswith(":") or sentence.endswith(";")):

                    string = string + sentence + " "
                    i += 1
                    sentence = content_list[i].strip()
                string = string + content_list[i]
                pair.append(string)
                pair.append(tag)
                cleanData.append(pair)
            i += 1

        #self.writeToCSV(self.allowed_domains[0], cleanData)



        #align list
        aligned_data_set = []
        i = 0
        while i < len(cleanData):
            row = cleanData[i][0]

            if row.endswith(":") and self.isNeedcat(row):
                li_text = cleanData[i + 1][0]
                sublings = response.xpath('//*[contains(text(), $content)]/following-sibling::li', content=li_text)
                count = len(sublings)
                print ("sublings is " + str(count))
                align_first_item = row + " " + li_text
                pair = []
                pair.append(align_first_item)
                print("the first sentence")
                print(align_first_item)
                pair.append(row[1])
                aligned_data_set.append(pair)
                for sub in sublings:
                    sub_text = sub.xpath('string(.)').extract()[0]

                    align_next_item = row + " " + sub_text
                    pair = []
                    pair.append(align_next_item)
                    pair.append(row[1])
                    aligned_data_set.append(pair)
                    i += 1
                i += 1
            else:
                aligned_data_set.append(cleanData[i])
            i += 1



        #
        second_clean_data = []
        for row in aligned_data_set:
            if "=" in row[0] or "{" in  row[0]:
                continue
            else:
                sentence_list = row[0].split(". ")
                for sentence in sentence_list:
                    if sentence.endswith(".") or sentence.endswith("!") or sentence.endswith("?"):
                        second_clean_data.append(sentence)
                    else:
                        second_clean_data.append(sentence + ". ")

        #split : 分句
        third_clean_data = []
        apha = ['(a)', '(b)', '(c)', '(d)', '(e)', '(f)', '(g)', '(h)', '(i)', '(j)', '(k)', '(l)']
        luoma = ['(i)', '(ii)', '(iii)', '(iv)', '(v)', '(vi)', '(vii)', '(viii)', '(ix)', '(x)', '(xi)', '(xii)', '(xiii)']
        for sen in second_clean_data:
            j = 0
            i = 0
            if apha[j] in sen:

                beign = sen.split(apha[j])[0]
                sen = sen.split(apha[j])[1]
                print(beign)

                j += 1

                while apha[j] in sen:
                    print(apha[j])
                    content = sen.split(apha[j])[0]
                    sen = sen.split(apha[j])[1]
                    row = beign + " " + content
                    j += 1
                    third_clean_data.append(row)
                third_clean_data.append(beign + " " + sen)
            elif luoma[i] in sen:
                beign = sen.split(luoma[i])[0]
                sen = sen.split(luoma[i])[1]
                i += 1
                while luoma[i] in sen:
                    content = sen.split(luoma[i])[0]
                    sen = sen.split(luoma[i])[1]
                    row = beign + " " + content
                    i += 1
                    third_clean_data.append(row)
                third_clean_data.append(beign + " " + sen)
            else:
                third_clean_data.append(sen)

        del (third_clean_data[0])
        third_clean_data.pop()
        self.writeToCSV(self.allowed_domains[0], third_clean_data)






    def changeIndex(self, i,content_list, content):
        while i < len(content_list):
            if content_list[i].strip() in content:
                i += 1
            else:
                return i - 1


    def isNeedcat(self,sentence):
        keyword = ["such as", "following","include", "including","follow","concerning", "For example", "see:","below"]
        for key in keyword:
            if key in sentence:
                return True
        return False


    def get_sublings(self,s, sentence):
        try:
            result = s.xpath('//*[contains(text(), $content)]/following-sibling::ul', content=sentence)
            item = result[0].xpath('string(.)').encode('utf-8').strip().decode('utf-8').strip()
            print(str(item))
            return str(item)
        except:
            return None



    def saveHtml(self, filename, file_content):

        path = "/Users/huthvincent/Desktop/paper_works/process_terms_of_use/html" + filename + ".html"
        with open(path, "w") as f:
            f.write(file_content)


    def spider_closed(self,spider):
        self.browser.quit()
        time.sleep(3)
        print("spider closed")


    def writeToCSV(self,filename,content):
        #path = "/Users/xiaoyue/scrapyenv/policy_crawl/maliciousLibrarySpider/spiders/privacy_policy/" + filename +  ".csv"
        #path = "/Users/huthvincent/Desktop/" + filename + ".csv"
        path = self.folder + filename + ".csv"
        with open(path, "w") as csvfile:
            writer = csv.writer(csvfile)
            for pair in content:
                list = []
                list.append(pair)
                writer.writerow(list)

    def isVaildSentence(self,line):
        pattern = re.compile('[0-9a-zA-Z]+')
        match = pattern.findall(line)
        if match:
            #print('contains digital')
            return True
        else:
            #print('not contains digital')
            return False
