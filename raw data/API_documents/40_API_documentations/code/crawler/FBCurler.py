
import requests
import csv
import time
from bs4 import BeautifulSoup

i=0
classDetails = []

baseURL='https://developers.facebook.com'
url ="https://developers.facebook.com/docs/reference/androidsdk/current/facebook/com/facebook/profile.html/"

page=requests.get(url)
soup = BeautifulSoup(page.content, 'html5lib')

allClasses = soup.findAll('a', {"class":"_3cx9"})

print("Classes To Index:"+ str(len(allClasses)))

for entry in allClasses:
    classInfo = {}
    
    classURL = baseURL+entry['href']
    page=requests.get(classURL)
    soup2 = BeautifulSoup(page.content,'html5lib')

    
    pkg = soup2.findAll('ul', {"class":"inheritance"})
     
    try:
         lastPkg=len(pkg)-1
         classInfo['class_name'] = pkg[lastPkg].text#+"."+entry.text
    except:
         classInfo['class_name'] = entry.text
    
    
    try:
        description = soup2.find('div', {"class":"block"})
        classInfo['class_description'] = description.text
    except:
        classInfo['class_description'] = "N/A"
    
    #print("\n\nPREMETHOD ADDING:")
    #print(classInfo)
    #print("\n\n")
    try:
        tables = soup2.findAll('table', {"class":"_4-ss memberSummary _4-sv"})
        lenT=len(tables)-1        
        #print(tables[lenT])
        #time.sleep(5)     
        
        methodRows = tables[lenT].findAll('td', {"class": "colLast"})
        modifier = tables[lenT].findAll('td', {"class": "colFirst"})

        #print(methodRows)
        #print("\n\n-=-=-=-=-=-=-=-=-=-=-=\n\n")
        j=0
        for method in methodRows:
            try:   
                methodDef = method.find('code')
                dt = modifier[j].find('code')

                classInfo['method'] = methodDef.text
                classInfo['data_type'] = dt.text
            except:
                classInfo['method'] = 'N/A'
                classInfo['data_type'] = "N/A"
            try:
                methodBlock = method.find('div', {"class": "block"})
                methodDescription = methodBlock.text
                classInfo['method_description'] = methodDescription
            except:
                classInfo['method_description'] = 'N/A'
      
            #print("\n\n~~~~~~~~~~~")
            #print(classInfo)
            #print("\n\n~~~~~~~~~~~")
            #time.sleep(2)
            classDetails.append(classInfo.copy())
            j=j+1
      
     
    except:
        classInfo['method'] = 'N/A'
        classInfo['method_description'] = 'N/A'
        classInfo['data_type'] = "N/A"
        classDetails.append(classInfo.copy())
    
    #print(classInfo)
    i+=1
    print("Classes Indexed:" + str(i))
    



print("Writing Output....")

filename = 'FBSDK_6_0_0.csv'
with open(filename, 'w') as f: 
    w = csv.DictWriter(f,['class_name', 'class_description', 'method', 'method_description', 'data_type'])
    w.writeheader()
    for classes in classDetails:
        w.writerow(classes)

print("Done!")
