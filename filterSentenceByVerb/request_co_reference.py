import requests
import json
from filterSentenceByVerb.request_co_reference import *


def main():
    document = "We are looking for a region of central Italy bordering the Adriatic Sea. The area is mostly mountainous and includes Mt. Corno, the highest peak of the mountain range. It also includes many sheep and an Italian entrepreneur has an idea about how to make a little money of them."
    request_co_reference(document)


def request_co_reference(document):
    headers = {
        'authority': 'demo.allennlp.org',
        'accept': 'application/json',
        'sec-fetch-dest': 'empty',
        'user-agent': 'Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_2) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/80.0.3987.132 Safari/537.36',
        'content-type': 'application/json',
        'origin': 'https://demo.allennlp.org',
        'sec-fetch-site': 'same-origin',
        'sec-fetch-mode': 'cors',
        'referer': 'https://demo.allennlp.org/coreference-resolution',
        'accept-language': 'zh-CN,zh;q=0.9,en;q=0.8',
        'cookie': '_ga=GA1.2.1227307081.1581447768; _gid=GA1.2.2052530112.1584851273; _gat=1',
    }
    dic = {}

    dic["document"] = document
    dumpJson = json.dumps(dic)

    response = requests.post('https://demo.allennlp.org/predict/coreference-resolution', headers=headers, data=dumpJson)
    # print(response.text)
    print(type(response.text))
    return response.text


if __name__ == "__main__":
    main()