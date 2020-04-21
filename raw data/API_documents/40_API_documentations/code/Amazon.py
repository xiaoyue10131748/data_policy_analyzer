import requests
import csv
import time
from bs4 import BeautifulSoup
import random


def writeTotxt(div):
    f = open("test.txt",'w')
    for d in div:
        f.write(str(d) + "\n")
    f.close()


def main():

    url ="https://docs.aws.amazon.com/AWSJavaSDK/latest/javadoc/allclasses-noframe.html"
    page=requests.get(url)
    soup = BeautifulSoup(page.content, 'html5lib')
    allClasses = soup.findAll('a')
    writeTotxt(allClasses)
    print("Classes To Index:"+ str(len(allClasses)))

    for i in range(1,40):
        start = 6000
        end = start + i*100
        new_list = allClasses[start:end]
        try:
            getMethod(new_list)
            time.sleep(200)
        except:
            time.sleep(1000)


def getMethod(allClasses):
    i=0
    classDetails = []
    baseURL = 'https://docs.aws.amazon.com/AWSJavaSDK/latest/javadoc/'
    count = 0
    #for entry in allClasses[4500:5000]:
    for entry in allClasses:
        classInfo = {}

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
        #page=requests.get(classURL)
        soup2 = BeautifulSoup(page.content,'html5lib')



        pkg = soup2.find('div', {"class":"subTitle"})
        classInfo['class_name'] = pkg.text+"."+entry.text

        try:
            description = soup2.find('div', {"class":"block"})
            classInfo['class_description'] = description.text
        except:
            classInfo['class_description'] = "N/A"

        try:
            methodsTable = soup2.find('table', {"summary":"Method Summary table, listing methods, and an explanation"})
            methodRow = methodsTable.findAll('td', {"class": "colLast"})
            dataType=methodsTable.findAll("td", {"class":"colFirst"})
            #print(methodRow)
            #print("\n\n-=-=-=-=-=-=-=-=-=-=-=\n\n")
            j=0
            for method in methodRow:

                try:
                    dt = dataType[j].find("code")
                    methodCode = method.find('code')
                    classInfo['method'] = methodCode.text
                    classInfo["data_type"] = dt.text
                except:
                    classInfo['method'] = 'N/A'
                    classInfo["data_type"] = "N/A"

                try:
                    methodBlock = method.find('div', {"class": "block"})
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

    filename = 'AmazonSDK.csv'
    with open(filename, 'a+') as f:
        w = csv.DictWriter(f,['class_name', 'class_description', 'method', 'method_description', 'data_type'])
        w.writeheader()
        for classes in classDetails:
            w.writerow(classes)

    print("Done!")


if __name__ == "__main__":
    main()