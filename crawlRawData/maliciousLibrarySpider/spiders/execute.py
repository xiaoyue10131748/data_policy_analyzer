# -*- coding: utf-8 -*-

import os
import time

def main():

    #循环处理每一个网站
    f = open("domainList_developer.txt","r")
    content = f.readlines()
    f.close()
    i = 0
    while i < len(content):
        url = content[i]
        i += 1
        if url.startswith("#"):
            continue

        print( url +  "===============" + str(i) )
        print (os.getcwd())

        #加上http://

        if isFound(url):
            print("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~has found")
            continue
        cammand = "scrapy crawl  nlp -a url={} -a count={}".format(url.strip(),i-1)
        cammand = "scrapy crawl  nlp -a url={} ".format(url)
        print (cammand)

        os.system(cammand)
        writeToTxt(url)
        time.sleep(3)

    

def writeToTxt(url):
    f = open("isFound_developer.txt", "a")
    f.write(url)
    f.write("\n")
    f.close()

def isFound(url):
    f = open("isFound_developer.txt","r")
    content = f.readlines()
    f.close()
    for item in content:
        if url in item:
            return True
    return False



if __name__ == "__main__":
    main()

        
    

