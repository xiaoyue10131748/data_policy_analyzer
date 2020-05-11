import requests
import csv
import time
from bs4 import BeautifulSoup
import random
import glob
import os
import fnmatch

def writeTotxt(div):
    f = open("test.txt",'w')
    for d in div:
        f.write(str(d) + "\n")
    f.close()



def get_raw_file():

    path = '/Users/huthvincent/Documents/research/malicious_library_hunting/data_policy_analyzer/raw data/API_documents/40_API_documentations/code/crawler/amazon/AmazonMaps_Android/2.4/docs/API-Reference/reference/com/amazon/geo/mapsv2/'
    configfiles = [os.path.join(dirpath, f)
                   for dirpath, dirnames, files in os.walk(path)
                   for f in fnmatch.filter(files, '*.html')]
    return configfiles


def main():
    file_list =get_raw_file()
    print(file_list)
    getMethod(file_list)

def getMethod(file_list):
    i=0
    classDetails = []
    count = 0
    #for entry in allClasses[4500:5000]:
    for file in file_list:
        classInfo = {}
        '''
        classURL = baseURL+entry['href']
        ua_list = [
            "Mozilla/5.0 (Macintosh; Intel Mac OS X 10.6; rv2.0.1) Gecko/20100101 Firefox/4.0.1",
            "Mozilla/5.0 (Windows NT 6.1; rv2.0.1) Gecko/20100101 Firefox/4.0.1",
            "Opera/9.80 (Macintosh; Intel Mac OS X 10.6.8; U; en) Presto/2.8.131 Version/11.11",
            "Opera/9.80 (Windows NT 6.1; U; en) Presto/2.8.131 Version/11.11",
            "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_7_0) AppleWebKit/535.11 (KHTML, like Gecko) Chrome/17.0.963.56 Safari/535.11"
        ]
        user_agent = random.choice(ua_list)

        ua_headers = {
            'User-Agent': user_agent
        }

        page = requests.get(classURL, headers=ua_headers, timeout=8)
        '''
        #page=requests.get(classURL)
        soup2 = BeautifulSoup(open(file))
        #soup2 = BeautifulSoup(page.content,'html5lib')


        className = file.split("/API-Reference/reference/")[1].split(".html")[0].replace("/", ".")
        classInfo['class_name'] =className
        try:
            description = soup2.find('div', {"class":"jd-descr"})
            classInfo['class_description'] = description.text
        except:
            classInfo['class_description'] = "N/A"

        try:
            methodsTable = soup2.find('table', {"id":"pubmethods"})
            methodRow = methodsTable.findAll('td', {"class": "jd-linkcol"})
            dataType=methodsTable.findAll("td", {"class":"jd-typecol"})
            #print(methodRow)
            #print("\n\n-=-=-=-=-=-=-=-=-=-=-=\n\n")
            j=0
            for method in methodRow:

                try:
                    dt = dataType[j].find("nobr")
                    methodCode = method.find('nobr')
                    classInfo['method'] = methodCode.text
                    classInfo["data_type"] = dt.text
                except:
                    classInfo['method'] = 'N/A'
                    classInfo["data_type"] = "N/A"

                try:
                    methodBlock = method.find('div', {"class": "jd-descrdiv"})
                    methodDescription = methodBlock.text
                    classInfo['method_description'] = methodDescription
                except:
                    classInfo['method_description'] = 'N/A'

                #print("\n\n~~~~~~~~~~~")
                #print(classInfo)
                #print("\n\n~~~~~~~~~~~")
                #time.sleep(1)
                classDetails.append(classInfo.copy())
                j=j+1



        except:
            classInfo['method'] = 'N/A'
            classInfo['method_description'] = 'N/A'
            classInfo["data_type"] = "N/A"
            classDetails.append(classInfo.copy())


        try:
            methodsTable = soup2.find('table', {"id":"promethods"})
            methodRow = methodsTable.findAll('td', {"class": "jd-linkcol"})
            dataType=methodsTable.findAll("td", {"class":"jd-typecol"})
            #print(methodRow)
            #print("\n\n-=-=-=-=-=-=-=-=-=-=-=\n\n")
            j=0
            for method in methodRow:

                try:
                    dt = dataType[j].find("nobr")
                    methodCode = method.find('nobr')
                    classInfo['method'] = methodCode.text
                    classInfo["data_type"] = dt.text
                except:
                    classInfo['method'] = 'N/A'
                    classInfo["data_type"] = "N/A"

                try:
                    methodBlock = method.find('div', {"class": "jd-descrdiv"})
                    methodDescription = methodBlock.text
                    classInfo['method_description'] = methodDescription
                except:
                    classInfo['method_description'] = 'N/A'

                #print("\n\n~~~~~~~~~~~")
                #print(classInfo)
                #print("\n\n~~~~~~~~~~~")
                #time.sleep(1)
                classDetails.append(classInfo.copy())
                j=j+1



        except:
            classInfo['method'] = 'N/A'
            classInfo['method_description'] = 'N/A'
            classInfo["data_type"] = "N/A"
            classDetails.append(classInfo.copy())

        i+=1
        print("Classes Indexed:" + str(i))





    print("Writing Output....")

    filename = 'AmazonSDK3.csv'
    with open(filename, 'a+') as f:
        w = csv.DictWriter(f,['class_name', 'class_description', 'method', 'method_description', 'data_type'])
        w.writeheader()
        for classes in classDetails:
            w.writerow(classes)

    print("Done!")


if __name__ == "__main__":
    main()