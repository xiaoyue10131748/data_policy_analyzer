import requests
import csv
import time
from bs4 import BeautifulSoup

i=0
classDetails = []

baseURL='http://mixpanel.github.io/mixpanel-android/'
url="http://mixpanel.github.io/mixpanel-android/allclasses.html"

page=requests.get(url)
soup = BeautifulSoup(page.content, 'html5lib')
allClasses = soup.findAll('a')

print("Classes To Index:"+ str(len(allClasses)))

for entry in allClasses:
    classInfo = {}
    
    classURL = baseURL+entry['href']
    page=requests.get(classURL)
    soup2 = BeautifulSoup(page.content,'html5lib')

    #print(classURL)
    header=soup2.find('div', {"class":"header"})
    pkg = header.find('a', {"href":"package-summary.html"}) 


    classInfo['class_name'] = pkg.text+"."+entry.text
    
    try:
        description = soup2.find('div', {"class":"block"})
        classInfo['class_description'] = description.text
    except:
        classInfo['class_description'] = "N/A"

    
    methodsTable = soup2.findAll('table', {"class":"memberSummary"})
    try:
         methodsTable = methodsTable[len(methodsTable)-1]
         #print(methodsTable)
         flagStr = methodsTable.find('th', {"class":"colSecond"})
         #print("-=-=-=-=-=-")
         if "Method" in flagStr:
              methods=methodsTable.findAll('th', {"class":"colSecond","scope":"row"})
              descriptions=methodsTable.findAll('td', {"class":"colLast"})
              dataType=methodsTable.findAll("td", {"class":"colFirst"})
              #print(descriptions)
              j=0
              for method in methods:
                   dt = dataType[j].find("code")
                   classInfo['method'] = method.text
                   classInfo["data_type"] = dt.text
                   if((descriptions[j].text).strip()==""):
                        classInfo['method_description'] = "N/A"
                   else:
                        classInfo['method_description'] = descriptions[j].text
                   if((dt.text).strip()==""):
                        classInfo["data_type"] = "N/A"
                   else:
                        classInfo["data_type"] = dt.text
                   #print(str(method.text)+"----"+str(descriptions[j].text)+"\n\n")
                   j=j+1
                   classDetails.append(classInfo.copy())
              print("Grabbed Method Table")
    except:
         classInfo['method'] = 'N/A'
         classInfo['method_description'] = 'N/A'
         classInfo["data_type"] = "N/A"
         classDetails.append(classInfo.copy())
    """
    #methodRow = methodsTable.findAll('td', {"class": "colLast"})
    for table in methodsTable:
        table = table.prettify(formatter=lambda s: s.replace(u'\xa0', ' '))
        rightTable = table.find('span')
        print(rightTable)
        if(rightTable.text=="All Methods "):
             print("at methods table")
        else:
             continue
    
    #print(methodsTable)
    print("\n\n-=-=-=-=-=-=-=-=-=-=-=\n\n")
    #except:        
        for method in methodRow:
            try:   
                methodCode = method.find('code')
                classInfo['method'] = methodCode.text
            except:
                classInfo['method'] = 'N/A'

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
    """
    #classDetails.append(classInfo.copy())


    """
    except:
        classInfo['method'] = 'N/A'
        classInfo['method_description'] = 'N/A'
        classDetails.append(classInfo.copy())
    """
    i+=1
    print("Classes Indexed:" + str(i))




print("Writing Output....")

filename = 'MixPanel_SDK.csv'
with open(filename, 'w') as f: 
    w = csv.DictWriter(f,['class_name', 'class_description', 'method', 'method_description', 'data_type'])
    w.writeheader()
    for classes in classDetails:
        w.writerow(classes)

print("Done!")
