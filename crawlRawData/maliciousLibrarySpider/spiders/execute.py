# -*- coding: utf-8 -*-

import os
import time
import sys
import getopt
def main():
    inputfile, outputfile = get_args(sys.argv[1:])
    print('the number of parameter is: {}'.format(len(sys.argv)))

    #process each url in the domainList_developer.txt
    f = open(inputfile,"r")
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

        #add http://
        # if the url has been processed before just skip it.
        if isFound(url):
            print("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~has found")
            continue
        cammand = "scrapy crawl  nlp -a url={} -a folder={} ".format(url.strip(),outputfile)
        print (cammand)

        os.system(cammand)
        writeToTxt(url,"isFound_developer.txt")

        time.sleep(3)


def get_args(argv):
    inputfile = ''
    outputfile = ''
    try:
        opts, args = getopt.getopt(argv, "hi:o:", ["ifile=", "ofile="])
    except getopt.GetoptError:
        print('test.py -i <inputfile> -o <outputfile>')
        sys.exit(2)
    for opt, arg in opts:
        if opt == '-h':
            print('test.py -i <inputfile> -o <outputfile>')
            sys.exit()
        elif opt in ("-i", "--ifile"):
            inputfile = arg
        elif opt in ("-o", "--ofile"):
            outputfile = arg

    return inputfile, outputfile


def writeToTxt(url,inputfile):
    f = open(inputfile, "a")
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

        
    

