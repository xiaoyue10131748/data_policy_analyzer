## data_policy_analyzer
The goal ofData Policy Analyzer(DPA) is to extract third-party  data  sharing  policies  from  an  SDK  ToS

## Project Title
data_policy_analyzer

## Getting Started

These instructions will get you a copy of the project up and running on your local machine for development and testing purposes. See deployment for notes on how to deploy the project on a live system.

### Prerequisites

We use seleimun, webdriver, scrapy framework to dynamic crawler the sentence in each Tos url. 
1. install seleimun
```
pip install selenium
```
2. install webdriver: we have already download the webdriver for chrome Version 81.0.4044.113. If you chrome version is inconsistent with this version, please re-download the webdriver for the right verion. Please check [here]https://chromedriver.chromium.org/downloads 
```
DRIVER_BIN = "../webdriver/chromedriver"
```
3. install scrapy: It is an open source and collaborative framework for extracting the data you need from websites in a fast, simple, yet extensible way.
``` 
pip install selenium
``` 


## usage

Here we will explain how to run the each module for this system.

### Moudle one: web crawler 

The crawler will extract all the text from website and split them into sentence. Put urls you want to crawl in the file "domainList_developer.txt" and specify which folder you want to store the crawl results. Each url will generate a csv file that contains sentences of the webpage.

```
python3 execute.py -i domainList_developer.txt -o /Users/huthvincent/Desktop/

```

### And coding style tests

Explain what these tests test and why

```
Give an example
```

## Deployment

Add additional notes about how to deploy this on a live system

## Built With

* [Dropwizard](http://www.dropwizard.io/1.0.2/docs/) - The web framework used
* [Maven](https://maven.apache.org/) - Dependency Management
* [ROME](https://rometools.github.io/rome/) - Used to generate RSS Feeds

## Contributing

Please read [CONTRIBUTING.md](https://gist.github.com/PurpleBooth/b24679402957c63ec426) for details on our code of conduct, and the process for submitting pull requests to us.

## Versioning

We use [SemVer](http://semver.org/) for versioning. For the versions available, see the [tags on this repository](https://github.com/your/project/tags). 

## Authors

* **Billie Thompson** - *Initial work* - [PurpleBooth](https://github.com/PurpleBooth)

See also the list of [contributors](https://github.com/your/project/contributors) who participated in this project.

## License

This project is licensed under the MIT License - see the [LICENSE.md](LICENSE.md) file for details

## Acknowledgments

* Hat tip to anyone whose code was used
* Inspiration
* etc

