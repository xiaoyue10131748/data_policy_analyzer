import requests
import csv
import time
from bs4 import BeautifulSoup
from selenium import webdriver
from selenium.webdriver.chrome.options import Options
import time




def getClassUrl():

    url = "https://developer.here.com/documentation/android-premium/3.15/api_reference_java/index.html"

    #page = requests.get(url)
    #soup = BeautifulSoup(page.content, 'html5lib')
    soup = BeautifulSoup(open('here.html'))

    #div = soup.find_all('div')

    allClasses = soup.find_all('a',target="classFrame")
    writeTotxt(allClasses)
    print("Classes To Index:" + str(len(allClasses)))
    return allClasses

def writeTotxt(div):
    f = open("test.txt",'w')
    for d in div:
        f.write(str(d) + "\n")
    f.close()

def main():
    allClasses = getClassUrl()

    allMethods= getMethod(allClasses)


def getMethod(allClasses):
    chrome_options = Options()
    # chrome_options.add_argument('--headless')
    chrome_options.add_argument("--start-maximized");
    driver = webdriver.Chrome("/Users/huthvincent/Documents/research/malicious_library_hunting/data_policy_analyzer/crawlRawData/maliciousLibrarySpider/webdriver/chromedriver", chrome_options=chrome_options)

    baseURL = "https://developer.here.com/documentation/android-premium/3.15/api_reference_java/"
    i = 0
    classDetails = []
    for entry in allClasses:
        classInfo = {}
        classURL = baseURL + entry['href']
        print(classURL)
        # 加载界面
        driver.get(classURL)
        driver.switch_to.frame('documentationIframe')
        time.sleep(4)
        html = driver.execute_script("return document.documentElement.outerHTML")
        className = entry['href'].split(".html")[0].replace("/", ".")
        classInfo['class_name'] = className
        soup2 = BeautifulSoup(html, 'html5lib')


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
    
    filename = 'here.csv'
    with open(filename, 'w') as f: 
        w = csv.DictWriter(f,['class_name', 'class_description', 'method', 'method_description', 'data_type'])
        w.writeheader()
        for classes in classDetails:
            w.writerow(classes)
    
    print("Done!")

    driver.close()

if __name__ == "__main__":
    main()
