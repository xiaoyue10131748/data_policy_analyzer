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
    allClasses.append("https://developers.mopub.com/publishers/reference/android/5.12.0/AdapterConfiguration/")
    allClasses.append("https://developers.mopub.com/publishers/reference/android/5.12.0/BaseAdapterConfiguration/")
    allClasses.append("https://developers.mopub.com/publishers/reference/android/5.12.0/Constants/")
    allClasses.append("https://developers.mopub.com/publishers/reference/android/5.12.0/LifeCycleListener/")
    allClasses.append("https://developers.mopub.com/publishers/reference/android/5.12.0/LocationAwareness/")
    allClasses.append("https://developers.mopub.com/publishers/reference/android/5.12.0/LogLevel/")
    allClasses.append("https://developers.mopub.com/publishers/reference/android/5.12.0/MediationSettings/")
    allClasses.append("https://developers.mopub.com/publishers/reference/android/5.12.0/MoPub/")
    allClasses.append("https://developers.mopub.com/publishers/reference/android/5.12.0/MoPubErrorCode/")
    allClasses.append("https://developers.mopub.com/publishers/reference/android/5.12.0/MoPubLog/")
    allClasses.append("https://developers.mopub.com/publishers/reference/android/5.12.0/OnNetworkInitializationFinishedListener/")
    allClasses.append("https://developers.mopub.com/publishers/reference/android/5.12.0/PersonalInfoManager/")
    allClasses.append("https://developers.mopub.com/publishers/reference/android/5.12.0/SdkConfiguration/")
    allClasses.append("https://developers.mopub.com/publishers/reference/android/5.12.0/SdkConfiguration.Builder/")
    allClasses.append("https://developers.mopub.com/publishers/reference/android/5.12.0/SdkInitializationListener/")
    allClasses.append("https://developers.mopub.com/publishers/reference/android/5.12.0/ConsentData/")
    allClasses.append("https://developers.mopub.com/publishers/reference/android/5.12.0/ConsentDialogListener/")
    allClasses.append("https://developers.mopub.com/publishers/reference/android/5.12.0/ConsentStatus/")
    allClasses.append("https://developers.mopub.com/publishers/reference/android/5.12.0/ConsentStatusChangeListener/")
    allClasses.append("https://developers.mopub.com/publishers/reference/android/5.12.0/BannerAdListener/")
    allClasses.append("https://developers.mopub.com/publishers/reference/android/5.12.0/CustomEventBanner/")
    allClasses.append("https://developers.mopub.com/publishers/reference/android/5.12.0/CustomEventBannerListener/")
    allClasses.append("https://developers.mopub.com/publishers/reference/android/5.12.0/MoPubView/")
    allClasses.append("https://developers.mopub.com/publishers/reference/android/5.12.0/CustomEventInterstitial/")
    allClasses.append("https://developers.mopub.com/publishers/reference/android/5.12.0/CustomEventInterstitialListener/")
    allClasses.append("https://developers.mopub.com/publishers/reference/android/5.12.0/InterstitialAdListener/")
    allClasses.append("https://developers.mopub.com/publishers/reference/android/5.12.0/MoPubInterstitial/")
    allClasses.append("https://developers.mopub.com/publishers/reference/android/5.12.0/CustomEventRewardedAd/")
    allClasses.append("https://developers.mopub.com/publishers/reference/android/5.12.0/CustomEventRewardedVideo/")
    allClasses.append("https://developers.mopub.com/publishers/reference/android/5.12.0/MoPubReward/")
    allClasses.append("https://developers.mopub.com/publishers/reference/android/5.12.0/MoPubRewardedVideos/")
    allClasses.append("https://developers.mopub.com/publishers/reference/android/5.12.0/MoPubRewardedVideoManager/")
    allClasses.append("https://developers.mopub.com/publishers/reference/android/5.12.0/MoPubRewardedVideoListener/")
    allClasses.append("https://developers.mopub.com/publishers/reference/android/5.12.0/ImpressionData/")
    allClasses.append("https://developers.mopub.com/publishers/reference/android/5.12.0/ImpressionListener/")
    allClasses.append("https://developers.mopub.com/publishers/reference/android/5.12.0/AdapterHelper/")
    allClasses.append("https://developers.mopub.com/publishers/reference/android/5.12.0/BaseNativeAd/")
    allClasses.append("https://developers.mopub.com/publishers/reference/android/5.12.0/ContentChangeStrategy/")
    allClasses.append("https://developers.mopub.com/publishers/reference/android/5.12.0/CustomEventNative/")
    allClasses.append("https://developers.mopub.com/publishers/reference/android/5.12.0/CustomEventNativeListener/")
    allClasses.append("https://developers.mopub.com/publishers/reference/android/5.12.0/ImageListener/")
    allClasses.append("https://developers.mopub.com/publishers/reference/android/5.12.0/MoPubAdAdapter/")
    allClasses.append("https://developers.mopub.com/publishers/reference/android/5.12.0/MoPubAdRenderer/")
    allClasses.append("https://developers.mopub.com/publishers/reference/android/5.12.0/MoPubClientPositioning/")
    allClasses.append("https://developers.mopub.com/publishers/reference/android/5.12.0/MoPubNative/")
    allClasses.append("https://developers.mopub.com/publishers/reference/android/5.12.0/MoPubNativeEventListener/")
    allClasses.append("https://developers.mopub.com/publishers/reference/android/5.12.0/MoPubNativeAdLoadedListener/")
    allClasses.append("https://developers.mopub.com/publishers/reference/android/5.12.0/MoPubNativeAdPositioning/")
    allClasses.append("https://developers.mopub.com/publishers/reference/android/5.12.0/MoPubRecyclerAdapter/")
    allClasses.append("https://developers.mopub.com/publishers/reference/android/5.12.0/MoPubServerPositioning/")
    allClasses.append("https://developers.mopub.com/publishers/reference/android/5.12.0/MoPubStaticNativeAdRenderer/")
    allClasses.append("https://developers.mopub.com/publishers/reference/android/5.12.0/MoPubStreamAdPlacer/")
    allClasses.append("https://developers.mopub.com/publishers/reference/android/5.12.0/NativeAd/")
    allClasses.append("https://developers.mopub.com/publishers/reference/android/5.12.0/NativeErrorCode/")
    allClasses.append("https://developers.mopub.com/publishers/reference/android/5.12.0/NativeImageHelper/")
    allClasses.append("https://developers.mopub.com/publishers/reference/android/5.12.0/RequestParameters/")
    allClasses.append("https://developers.mopub.com/publishers/reference/android/5.12.0/ViewBinder/")
    allClasses.append("https://developers.mopub.com/publishers/reference/android/5.12.0/ViewBinder.Builder/")

    return allClasses


def writeTotxt(div):
    f = open("test.txt", 'w')
    for d in div:
        f.write(str(d) + "\n")
    f.close()


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
        if entry!="https://developers.mopub.com/publishers/reference/android/5.12.0/ViewBinder.Builder/":
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
        className = entry.split("https://developers.mopub.com/publishers/reference/android/5.12.0/")[1].replace("/","")
        classInfo['class_name'] = className
        soup2 = BeautifulSoup(html, 'html5lib')

        try:
            description = soup2.find('p', {"style": "padding-right: 40px;"})
            classInfo['class_description'] = description.text
        except:
            classInfo['class_description'] = "N/A"

        try:
            methodsTable = soup2.findAll('h4')

            for index,method in enumerate(methodsTable):

                try:

                    methodRow = method.findAll('a')
                    spans = methodRow[0].findAll("span")
                    data_type = spans[0]
                    method = spans[1]
                    classInfo['method'] = method.text
                    classInfo["data_type"] = data_type.text
                except:
                    classInfo['method'] = 'N/A'
                    classInfo["data_type"] = "N/A"

                try:
                    d= '//*[@id="wrapper-container"]/div/main/article/ul[' +str(index+1) + ']/li[1]'
                    print(d)
                    methodDescription =driver.find_element_by_xpath(d)
                    classInfo['method_description'] = methodDescription.text
                except:
                    classInfo['method_description'] = 'N/A'

                # print("\n\n~~~~~~~~~~~")
                # print(classInfo)
                # print("\n\n~~~~~~~~~~~")
                # time.sleep(1)
                classDetails.append(classInfo.copy())




        except:
            classInfo['method'] = 'N/A'
            classInfo['method_description'] = 'N/A'
            classInfo["data_type"] = "N/A"
            classDetails.append(classInfo.copy())

        i += 1
        print("Classes Indexed:" + str(i))

    print("Writing Output....")

    filename = 'mopub.csv'
    with open(filename, 'w') as f:
        w = csv.DictWriter(f, ['class_name', 'class_description', 'method', 'method_description', 'data_type'])
        w.writeheader()
        for classes in classDetails:
            w.writerow(classes)

    print("Done!")

    driver.close()


if __name__ == "__main__":
    main()
