import requests
import csv
import time
from bs4 import BeautifulSoup

i=0
classDetails = []

baseURL='https://zendesk.github.io/mobile_sdk_javadocs/supportv2/v301/'
url ="https://zendesk.github.io/mobile_sdk_javadocs/supportv2/v301/allclasses-noframe.html"


page=requests.get(url)
soup = BeautifulSoup(page.content, 'html5lib')
allClasses = soup.findAll('a')

print("Classes To Index:"+ str(len(allClasses)))
for entry in allClasses:
    classInfo = {}
    
    classURL = baseURL+entry['href']
    #print(classURL)
    page=requests.get(classURL)
    #print(page)
    soup2 = BeautifulSoup(page.content,'html5lib')
    header=soup2.find('div', {"class":"header"})
    pkg=header.find('a',{"target":"classFrame"})
    try:
         classInfo['class_name'] = pkg.text+"."+entry.text
    except:
         classInfo['class_name'] = "N/A"

    
    try:
        descriptionHeader = soup2.find("div", {"class":"description"})
        description = descriptionHeader.find('div', {"class":"block"})
        classInfo['class_description'] = description.text
    except:
        classInfo['class_description'] = "N/A"
    #print("Assigned Class")
    methodsTable = soup2.findAll('table', {"class":"memberSummary"})
    try:
        methodsTable = methodsTable[len(methodsTable)-1]
        #print(methodsTable)
        flagStr = methodsTable.find('th', {"class":"colLast"})
        #print(flagStr.text)
        if "Method and Description" in flagStr:
             methodRow=methodsTable.findAll('td', {"class":"colLast"})
             dataType=methodsTable.findAll("td", {"class":"colFirst"})
             #print(methodRow)
             j=0
             for method in methodRow:
                  #print("1")
                  mname=method.find("code")
                  dt = dataType[j].find("code")
                  #print("APPENDING:" + mname.text)
                  classInfo['method'] = mname.text
                  #print("APPENDING:" + mname.text)
                  try:
                       mdesc=method.find("div", {"class":"block"})
                       classInfo['method_description'] = mdesc.text
                       classInfo["data_type"] = dt.text
                  except:
                       classInfo['method_description'] = "N/A"
                       classInfo["data_type"] = "N/A"
                  classDetails.append(classInfo.copy())
                  j=j+1
             #print("Grabbed Method Table")
    except:
         classInfo['method'] = 'N/A'
         classInfo['method_description'] = 'N/A'
         classInfo["data_type"] = "N/A"
         classDetails.append(classInfo.copy())
    
    #print(classInfo)
    i+=1
    print("Classes Indexed:" + str(i))





print("Writing Output....")

filename = 'ZenDesk_SDK.csv'
with open(filename, 'w') as f: 
    w = csv.DictWriter(f,['class_name', 'class_description', 'method', 'method_description', 'data_type'])
    w.writeheader()
    for classes in classDetails:
        w.writerow(classes)

print("Done!")
