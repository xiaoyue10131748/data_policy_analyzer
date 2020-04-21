import requests
import csv
import time
from bs4 import BeautifulSoup

i=0
classDetails = []

baseURL='https://dropbox.github.io/dropbox-sdk-java/api-docs/v2.1.x/'
url ="https://dropbox.github.io/dropbox-sdk-java/api-docs/v2.1.x/allclasses-noframe.html"


page=requests.get(url)
soup = BeautifulSoup(page.content, 'html5lib')
allClasses = soup.findAll('a')

print("Classes To Index:"+ str(len(allClasses)))

for entry in allClasses:
    classInfo = {}
    
    classURL = baseURL+entry['href']
    page=requests.get(classURL)
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

filename = 'DropBox.csv'
with open(filename, 'w') as f: 
    w = csv.DictWriter(f,['class_name', 'class_description', 'method', 'method_description', 'data_type'])
    w.writeheader()
    for classes in classDetails:
        w.writerow(classes)

print("Done!")
