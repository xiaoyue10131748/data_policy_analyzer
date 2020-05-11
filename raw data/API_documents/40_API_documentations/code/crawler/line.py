import requests
import csv
import time
from bs4 import BeautifulSoup
from selenium import webdriver
from selenium.webdriver.chrome.options import Options
import time


def getClassUrl():
    '''
    url = "https://developers.mopub.com/publishers/reference/android/5.12.0/MoPubAdAdapter/"
    page = requests.get(url)
    soup = BeautifulSoup(page.content, 'html5lib')
    #soup = BeautifulSoup(open('here.html'))
    # div = soup.find_all('div')
    allClasses = soup.find_all('li', style="font-size: larger; user-select: none;")
    writeTotxt(allClasses)
    print("Classes To Index:" + str(len(allClasses)))
    '''

    # manully construct the class url
    allClasses = []
    allClasses.append("https://developers.line.biz/en/reference/android-sdk/reference/com/linecorp/linesdk/LoginDelegate.html")
    allClasses.append("https://developers.line.biz/en/reference/android-sdk/reference/com/linecorp/linesdk/LoginDelegate.html")
    allClasses.append("https://developers.line.biz/en/reference/android-sdk/reference/com/linecorp/linesdk/LoginListener.html")
    allClasses.append("https://developers.line.biz/en/reference/android-sdk/reference/com/linecorp/linesdk/LineAccessToken.html")
    allClasses.append("https://developers.line.biz/en/reference/android-sdk/reference/com/linecorp/linesdk/LineApiError.html")
    allClasses.append("https://developers.line.biz/en/reference/android-sdk/reference/com/linecorp/linesdk/LineApiResponse.html")
    allClasses.append("https://developers.line.biz/en/reference/android-sdk/reference/com/linecorp/linesdk/LineCredential.html")
    allClasses.append("https://developers.line.biz/en/reference/android-sdk/reference/com/linecorp/linesdk/LineFriendshipStatus.html")
    allClasses.append("https://developers.line.biz/en/reference/android-sdk/reference/com/linecorp/linesdk/LineIdToken.html")
    allClasses.append("https://developers.line.biz/en/reference/android-sdk/reference/com/linecorp/linesdk/LineProfile.html")
    allClasses.append("https://developers.line.biz/en/reference/android-sdk/reference/com/linecorp/linesdk/LoginDelegate.Factory.html")
    allClasses.append("https://developers.line.biz/en/reference/android-sdk/reference/com/linecorp/linesdk/Scope.html")
    allClasses.append("https://developers.line.biz/en/reference/android-sdk/reference/com/linecorp/linesdk/LineApiResponseCode.html")
    allClasses.append("https://developers.line.biz/en/reference/android-sdk/reference/com/linecorp/linesdk/api/LineApiClient.html")
    allClasses.append("https://developers.line.biz/en/reference/android-sdk/reference/com/linecorp/linesdk/api/LineApiClientBuilder.html")
    allClasses.append("https://developers.line.biz/en/reference/android-sdk/reference/com/linecorp/linesdk/auth/LineAuthenticationParams.html")
    allClasses.append("https://developers.line.biz/en/reference/android-sdk/reference/com/linecorp/linesdk/auth/LineAuthenticationParams.Builder.html")
    allClasses.append("https://developers.line.biz/en/reference/android-sdk/reference/com/linecorp/linesdk/auth/LineLoginApi.html")
    allClasses.append("https://developers.line.biz/en/reference/android-sdk/reference/com/linecorp/linesdk/auth/LineLoginResult.html")
    allClasses.append("https://developers.line.biz/en/reference/android-sdk/reference/com/linecorp/linesdk/auth/LineAuthenticationParams.BotPrompt.html")
    allClasses.append("https://developers.line.biz/en/reference/android-sdk/reference/com/linecorp/linesdk/widget/LoginButton.html")

    return allClasses



def main():
    allClasses = getClassUrl()

    allMethods = getMethod(allClasses)


def getMethod(allClasses):
    chrome_options = Options()
    # chrome_options.add_argument('--headless')
    chrome_options.add_argument("--start-maximized");
    driver = webdriver.Chrome(
        "/Users/huthvincent/Documents/research/malicious_library_hunting/data_policy_analyzer/crawlRawData/maliciousLibrarySpider/webdriver/chromedriver",
        chrome_options=chrome_options)

    i = 0
    classDetails = []
    for entry in allClasses:
        '''
        if entry!="https://developers.line.biz/en/reference/android-sdk/reference/com/linecorp/linesdk/LineAccessToken.html":
            continue
        '''
        classInfo = {}
        classURL =entry
        print(classURL)
        # 加载界面
        driver.get(classURL)
        #driver.switch_to.frame('documentationIframe')
        time.sleep(4)
        html = driver.execute_script("return document.documentElement.outerHTML")
        className = entry.split("https://developers.line.biz/en/reference/android-sdk/reference/")[1].replace("/",".").replace(".html","")
        classInfo['class_name'] = className
        soup2 = BeautifulSoup(html, 'html5lib')

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
                    dt = dataType[j]
                    methodCode = method
                    description = method.find('div', {"class": "jd-descrdiv"}).text
                    classInfo['method'] = methodCode.text.split(description)[0].strip()
                    classInfo["data_type"] = dt.text.strip()
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

    filename = 'line.csv'
    with open(filename, 'w') as f:
        w = csv.DictWriter(f, ['class_name', 'class_description', 'method', 'method_description', 'data_type'])
        w.writeheader()
        for classes in classDetails:
            w.writerow(classes)

    print("Done!")

    driver.close()


if __name__ == "__main__":
    main()
