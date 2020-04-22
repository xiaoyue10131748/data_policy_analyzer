import requests
import csv
import time
from bs4 import BeautifulSoup

i=0
classDetails = []

baseURL='https://zoom.github.io/zoom-sdk-android/'
url ="https://zoom.github.io/zoom-sdk-android/allclasses-noframe.html"


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
    classInfo['class_name'] = "uz.zoom.sdk."+entry.text
    
    try:
        descriptionHeader = soup2.find("div", {"class":"description"})
        description = descriptionHeader.find('div', {"class":"block"})
        classInfo['class_description'] = description.text
    except:
        classInfo['class_description'] = "N/A"
    
    try:
        methodsTable = soup2.find('table', {"summary":"Method Summary table, listing methods, and an explanation"})
        methodRow = methodsTable.findAll('td', {"class": "colLast"})
        dataType=methodsTable.findAll("td", {"class":"colFirst"})
        #print(methodRow)
        j=0    
        #print(methodRow)
        #print("\n\n-=-=-=-=-=-=-=-=-=-=-=\n\n")
        
        for method in methodRow:
            try:   
                methodCode = method.find('code')
                classInfo['method'] = methodCode.text
                
                dt = dataType[j].find("code")
                classInfo["data_type"] = dt.text
            except:
                classInfo['method'] = 'N/A'
                classInfo["data_type"] ="N/A"
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
    
    #print(classInfo)
    i+=1
    print("Classes Indexed:" + str(i))





print("Writing Output....")

filename = 'Zoom_SDK.csv'
with open(filename, 'w') as f: 
    w = csv.DictWriter(f,['class_name', 'class_description', 'method', 'method_description', 'data_type'])
    w.writeheader()
    for classes in classDetails:
        w.writerow(classes)

print("Done!")
