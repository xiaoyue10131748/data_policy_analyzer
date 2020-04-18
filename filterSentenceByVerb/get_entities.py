# -*- coding: utf-8 -*-
from filterSentenceByVerb.get_verb_entities import *


def main():
    xlsx_list = get_raw_file()
    print(xlsx_list)
    for filename in xlsx_list:
        to_filemame = "../raw data/40_post_processed_data/" + filename.split("40_pre_processed_data/")[1]
        print("===============================" + str(to_filemame))
        if os.path.exists(to_filemame):
            continue
        get_result_file(filename)


    '''
    #sentence = "We may limit or remove your access to Messenger if you receive large amounts of negative feedback or violate our policies, as determined by us in our sole discretion."
    sentence = "you can monitor or collect data related to your use of SDKs."
    predictor = predictor = Predictor.from_path("https://s3-us-west-2.amazonaws.com/allennlp/models/elmo-constituency-parser-2018.03.14.tar.gz")  
    nlp = spacy.load("en_core_web_sm") 
    #phrase_set = ["data"]
    co_refence_predictor = Predictor.from_path("https://s3-us-west-2.amazonaws.com/allennlp/models/coref-model-2018.02.05.tar.gz")

    #print(assign_verb_and_entity_with_filter(sentence,predictor,nlp,phrase_set))
    paragraph = "We respect the Intellectual Property Rights of others and we expect our users to do the same. We will respond to clear notices of copyright infringement consistent with the Digital Millennium Copyright Act (“DMCA”). You can learn more about Adobe's IP Takedown policies and practices here: http://www.adobe.com/legal/dmca.html."
    print(get_co_reference(paragraph,co_refence_predictor))
    #print(is_first_party(sentence,nlp,predictor,verb))
    #standford_nlp = stanza.Pipeline()
    #print(assign_nmod_of_sensitive_word(sentence,standford_nlp,predictor))
    '''


def get_raw_file():
    xlsx_list = glob.glob('../raw data/40_pre_processed_data/*.xlsx')  # get all the filenames of the prepossed 40 toses
    print(u'have found %s xlsx files' % len(xlsx_list))
    print(u'正在处理............')
    return xlsx_list


def get_result_file(filename):
    # filename = "../data/raw data/facebook.xlsx"
    predictor = Predictor.from_path(
        "https://s3-us-west-2.amazonaws.com/allennlp/models/elmo-constituency-parser-2018.03.14.tar.gz")
    co_refence_predictor = Predictor.from_path(
        "https://s3-us-west-2.amazonaws.com/allennlp/models/coref-model-2018.02.05.tar.gz")

    nlp = spacy.load("en_core_web_sm")
    standford_nlp = stanza.Pipeline()
    sentence_list, align_paragraph, score = read_data(filename)

    # get verb,entities with filter
    verb_entity_list_with_filter = []
    # get verb,entities without filter
    verb_entity_list_without_filter = []
    # get nmod,entities with filter
    nmod_entity_list = []
    # get the subject of the verb
    verb_subject_list = []
    # co_reference relationship
    co_reference_list = []

    # get the subject of the co_reference
    subject_co_reference_list = []

    for index, sentence in enumerate(sentence_list):
        policy = policy_verb_entity(sentence, align_paragraph[index],predictor,co_refence_predictor,nlp,standford_nlp)
        ## asign phrase_set, sensitive_data
        policy.extractEntity()
        ## assign co_reference
        policy.get_co_reference()
        ## assign verb_entitiy_string_without_filter
        policy.assign_verb_and_entity_without_filter()
        ## assign verb_entitiy_string_with_filter, verb_subjects
        verb_entitiy_string, flag, subject_list = policy.assign_verb_and_entity_with_filter()
        ## assign subject_co_reference
        policy.get_subject_co_reference(subject_list)
        ## assign nmod_entitiy_string
        if flag:
            nmod_entity_list.append("")
        else:
            policy.assign_nmod_of_sensitive_word()
            nmod_entity_list.append(policy.nmod_entitiy_string)

        verb_entity_list_without_filter.append(policy.verb_entitiy_string_without_filter)
        verb_entity_list_with_filter.append(policy.verb_entitiy_string_with_filter)
        verb_subject_list.append(policy.verb_subjects)
        co_reference_list.append(policy.co_reference)
        subject_co_reference_list.append(policy.subject_co_reference)


    # construct the label of the sentence
    # if the value in verb_entity_list_with_filter and nmod_entity_list is not null, we label 1, otherwise label 0
    predict_label = []
    for i in range(0, len(sentence_list)):
        if len(verb_entity_list_with_filter[i]) != 0 or len(nmod_entity_list[i]) != 0:
            predict_label.append(1)
        else:
            predict_label.append(0)

    data = {}
    data["sentence_list"] = sentence_list
    data["align_paragraph"] = align_paragraph
    data["subject_co_reference"] = subject_co_reference_list
    data["co_reference_list"] = co_reference_list
    data["verb_subject_list"] = verb_subject_list

    data["score"] = score
    data["predict_label"] = predict_label
    data["verb_entity_list_without_filter"] = verb_entity_list_without_filter
    data["verb_entity_list_with_filter"] = verb_entity_list_with_filter
    data["nmod_entity_list"] = nmod_entity_list
    df = pd.DataFrame(data)
    to_filemame = "../raw data/40_post_processed_data/" + filename.split("40_pre_processed_data/")[1]
    df.to_excel(to_filemame, index=False, encoding="utf8",
                header=["sentence_list", "align_paragraph", "subject_co_reference", "co_reference_list",
                        "verb_subject_list", "score", "predict_label", "verb_entity_list_without_filter",
                        "verb_entity_list_with_filter", "nmod_entity_list"])






# read raw data
def read_data(filename):
    sheet = pd.read_excel(filename)
    return sheet["sentence_list"].tolist(), sheet["align_paragraph"].tolist(), sheet["score"].tolist()



def getDirectVerb(phrase_item, sentence, nlp):
    doc = nlp(sentence)
    # Merge the noun phrases
    for phrase in list(doc.noun_chunks):
        phrase.merge(phrase.root.tag_, phrase.root.lemma_, phrase.root.ent_type_)

    verb_list = []
    for token in doc:
        if token.text == phrase_item:
            curr = token
            while curr.dep_ != "ROOT" and curr.pos_ != "VERB":
                curr = curr.head
                # print(curr)
            if curr.pos_ == "VERB":
                verb_list.append(curr.text)
                return verb_list[0]
    return []




if __name__ == "__main__":
    main()
