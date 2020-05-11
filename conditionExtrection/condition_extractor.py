import spacy
import networkx as nx
from conditionExtrection.prunTree import *
import matplotlib.pyplot as plt
import pandas as pd
from allennlp.predictors.predictor import Predictor
from filterSentenceByVerb.get_entities import *

from conditionExtrection.get_clauses import *
from conditionExtrection.pattern import *
import datetime

class PolicyStatement:

    predictor = Predictor.from_path("https://s3-us-west-2.amazonaws.com/allennlp/models/elmo-constituency-parser-2018.03.14.tar.gz")
    co_refence_predictor = Predictor.from_path("https://s3-us-west-2.amazonaws.com/allennlp/models/coref-model-2018.02.05.tar.gz")
    nlp = spacy.load("en_core_web_sm")
    standford_nlp = stanza.Pipeline()

    def __init__(self, sentence,paragraph):
        self.sentence = sentence
        self.paragraph = paragraph
        self.graph = None
        self.convert_spacy_tree_to_graph()




    #covert the tree genereated by spacy to graph
    def convert_spacy_tree_to_graph(self):
        doc = PolicyStatement.nlp(self.sentence)
        for phrase in list(doc.noun_chunks):
            phrase.merge(phrase.root.tag_, phrase.root.lemma_, phrase.root.ent_type_)
        G = nx.DiGraph()
        root = None
        for token in doc:
            #print(token.text + " --> " + token.dep_ + "-->" + token.head.text)
            if token == token.head:
                root = token
            G.add_edge(token, token.head, dep=token.dep_)
            ##check if there is condition role
            if self.is_condition(token):
                G.nodes[token]['role'] = "condition"
            else:
                G.nodes[token]['role'] = "other"
        G.remove_edge(root, root)
        self.graph = G
        return G


    def is_condition(self,token):
        conditionList = ["subject","consent","permission","approval", "privacy policy","privacy notice","only","comply","complies","compliance","according","accord","accordance", "if","when"]
        for c in conditionList:
            if c in token.text.lower():
                return True
        return False

    @classmethod
    def is_sentence_condition_pattern_1(self,sentence):
        conditionList = ["consent","permission","approval"]
        for c in conditionList:
            if c in sentence.lower():
                return True
        return False

    @classmethod
    def is_sentence_condition_pattern_2(self,sentence):

        conditionList = ["privacy policy","privacy notice"]
        for c in conditionList:
            if c in sentence.lower():
                return True
        return False

    @classmethod
    def is_sentence_condition_pattern_3(self,sentence):

        conditionList = ["only"]
        for c in conditionList:
            if c in sentence.lower():
                return True
        return False

    @classmethod
    def is_sentence_condition_pattern_4(self,sentence):
        conditionList = ["comply","complies","compliance","subject to"]
        for c in conditionList:
            if c in sentence.lower():
                return True
        return False

    @classmethod
    def is_sentence_condition_pattern_5(self,sentence):
        conditionList = ["according","accord","accordance"]
        for c in conditionList:
            if c in sentence.lower():
                return True
        return False

    @classmethod
    def is_sentence_condition_pattern_6(self,sentence):
        conditionList = ["if ", "when"]
        for c in conditionList:
            if c in sentence.lower():
                return True
        return False

    @classmethod
    def is_sentence_condition_pattern_7(self,sentence):
        conditionList = ["except", "unless","other than", "as long as","agree to"]
        for c in conditionList:
            if c in sentence.lower() and "exceptionally" not in sentence.lower():
                return True
        return False

    @classmethod
    def pattern_7_key_word(self, sentence):
        keyword = ""
        conditionList = ["except", "unless", "other than", "as long as", "agree to","agrees to"]
        for c in conditionList:
            if c in sentence.lower():
                keyword = c
                return keyword
        return keyword

    # find condition node
    def getCondition(self):
        for node in self.graph.nodes():
            if self.graph.nodes[node]['role'] == "condition":
                return node
        return None

    #based on data anchor
    def getDataAnchor(self,dataText):
        curr = None
        node_list = self.graph.nodes()
        for index, n in enumerate(node_list) :
            if n.text in dataText or dataText in n.text:
                #print("yueyueyue===" + str(index))
                self.graph.nodes[n]['role'] ="data"
                curr = n
                return curr
        return curr


    # find the verb or the noun modifier of the data anchor
    def getAction(self, dataText):
        verb = getDirectVerb(dataText, self.sentence, self.nlp)
        items = dataText.split()
        nmod=""
        for item in items:
            n = get_word_head(self.sentence, item, self.standford_nlp)
            if n is not None:
                nmod = n

        action = ""
        if verb:
            action = verb
        elif nmod:
            action = nmod

        node_list = self.graph.nodes()
        for index, n in enumerate(node_list) :
            if action in n.text.split():
                if self.graph.nodes[n]['role'] != "condition":
                    self.graph.nodes[n]['role'] ="action"
        return action


    #nodesList is a list of node, this method return subgraph contains those nodes
    def getSubGraph(self,nodesList):
        subGraphList = []
        for nodes in nodesList:
            sub = self.graph.subgraph(nodes).copy()
            subGraphList.append(sub)
        return subGraphList


    def get_most_similar_graph(self, category,maxDepth,pattern):

        if category == "consent" or category == "privacy_policy" or category == "comply" or category == "if" or category == "only" or category == "accord":
            # 找到condition node
            condition_node = self.getCondition()
            #print("=====current condition anchor is ===========" + condition_node.text)
            curr = condition_node

        elif  category == "other":
            object = policy_verb_entity.predict(self.sentence, self.paragraph)
            object.extractEntity()
            phrase_set = object.phrase_set
            sensitive_data = object.sensitive_data

            if len(sensitive_data) != 0:
                dataText = sensitive_data[0]
                data_node = self.getDataAnchor(dataText)
                print("=====current data node is ===========" + data_node.text)

                # 找到 action 并且 assign role
                action = self.getAction(data_node.text)
                print("the verb or the modifier of the data is:  " +action)
            else:
                data_node = None


            if data_node is not None:
                curr = data_node
            else:
                curr = self.getCondition()


        # 找到所有含有  anchor 的sub_graph
        res = []
        # 如果原句子标有condition node,那么subgraph一定要包含此node
        condition_node = self.getCondition()
        if condition_node is not None:
            require_node = condition_node
        else:
            require_node = curr
        self.search([], [curr], [curr], res, 0, maxDepth, require_node)
        sub_graph_list = self.getSubGraph(res)
        print("===the total subgraph is " + str(len(sub_graph_list)))
        score = []
        sub = []
        match_pattern = []
        count = 0
        flag = False
        for G1 in sub_graph_list:
            ## 如果已经找到最小的子图,就不需要再找了
            if flag:
                break
            #print("===the subgraph node data is " + str(G1.nodes.data()))
            ## 如果test_graph 里面有标condition，子图里面没有，则继续：
            if not self.contains_condition_anchor(G1):
                continue
            count += 1
            print("=================the nodes=======" + str(count))
            print(G1.nodes())
            for G2 in pattern:
                match_pattern.append(G2)
                # score.append(nx.graph_edit_distance(G1, G2, edge_match=PolicyStatement.ematch))
                s =  nx.graph_edit_distance(G1, G2, node_match=PolicyStatement.nmatch, edge_match=PolicyStatement.ematch)
                score.append(s)
                # score.append(nx.graph_edit_distance(G1, G2))
                sub.append(G1)
                if s == 0:
                    flag = True
                    break
        print(score)
        print("-----------------------------")
        #如果没有结果，直接返回
        if len(score) == 0:
            return [],[]

        min_score = min(score)
        print(min(score))
        ########################stat get all min socre#####################
        min_score_list = []
        for index, s in enumerate(score):
            if s == min_score:
                min_score_list.append(index)
        ########################end get all min socre######################
        print(min_score_list)

        all_matched_condition = []

        for index in min_score_list:
            condition_nodes = self.plot_min_sub(index, sub)
            all_matched_condition.append(condition_nodes)
        p = match_pattern[score.index(min(score))]
        self.plot_match_pattern(min_score_list[0], match_pattern)
        get_match_node = [n for n in p.nodes()]
        return all_matched_condition, get_match_node



    def search(self, cur_subgraph, visited, to_visit, res, depth,maxDepth,require_node):
        res_text = []
        for s in res:
            for token in s:
                res_text.append(token.text.lower())

        if require_node.text.lower() in res_text and depth > maxDepth:
            return
        if (cur_subgraph and cur_subgraph not in res):
            res.append(cur_subgraph.copy())
        if (to_visit):
            vertex = to_visit.pop(0)
            # do not select vertex
            self.search(cur_subgraph.copy(), visited.copy(), to_visit.copy(), res,depth+1,maxDepth,require_node)
            # select vertex
            for node in  self.get_neighbour(self.graph,vertex):
                if node not in visited:
                    to_visit.append(node)
                    visited.append(node)
            cur_subgraph.append(vertex)
            self.search(cur_subgraph.copy(), visited.copy(), to_visit.copy(), res,depth+1,maxDepth,require_node)



    #the node will only have one neighbour each time(data --> collect)
    #because tree structure
    def get_neighbour(self, g, currNode):
        neighnour_list =[]
        for n, nbrs in g.adj.items():
            if n == currNode:
                pre_node = g.predecessors(currNode)
                for p in pre_node:
                    neighnour_list.append(p)
                for nbr, eattr in nbrs.items():
                    #print("-----------------------")
                    #print("the neighnour is "+nbr.text)
                    #print(type(nbr))
                    #print(type(eattr))
                    #print("-----------------------")
                    #print("\n"):
                    neighnour_list.append(nbr)
        return list(set(neighnour_list))



    @classmethod
    def ematch(self, e1,e2):
        return e1['dep'] == e2['dep']

    @classmethod
    def nmatch(self, n1,n2):

        if n1['role'] == n2['role']:
            return True
        else:
            return False


    def constructEdgeLabels(self,graph):
        lables = {}
        for n, nbrs in graph.adj.items():
            if n.dep_ == "ROOT":
                continue
            for nbr, eattr in nbrs.items():
                pair = []
                print(n.text +" : " + str(nbr)+ " : "+ str(eattr) )
                pair.append(n)
                pair.append(nbr)
                key = tuple(pair)
                value = eattr['dep']
                lables[key] = value
        return lables


    @classmethod
    def customize_draw(self,graph,nodelist):
        pos = nx.spring_layout(graph)
        nx.draw(graph, pos, arrows=True, edge_color='black', width=1, linewidths=1, node_size=500, node_color='pink',alpha=0.9, labels={node: node for node in graph.nodes()})
        edge_label_map = self.constructEdgeLabels(graph)
        nx.draw_networkx_nodes(graph, pos, arrows=True, nodelist=nodelist)
        nx.draw_networkx_edge_labels(graph, pos, edge_labels=edge_label_map, font_color='red')
        plt.show()


    def plot_min_sub(self, min_sub_index,sub):

        min_sub = sub[min_sub_index]
        print(min_sub.nodes())
        print(min_sub.edges())
        for n, nbrs in min_sub.adj.items():
            for nbr, eattr in nbrs.items():
                print(n.text + " : " + str(nbr) + " : " + str(eattr))

        node_list = []
        for n in self.graph.nodes():
            for sub_n in min_sub.nodes():
                if n.text == sub_n.text and n.dep_ == sub_n.dep_ and n.head == sub_n.head:
                    node_list.append(n)
        #PolicyStatement.customize_draw(test_graph, node_list)
        return min_sub.nodes()



    def plot_match_pattern(self, min_sub_index, match_pattern):
        p = match_pattern[min_sub_index]
        print("\n")
        print(p.nodes())
        print(p.edges())
        for n, nbrs in p.adj.items():
            for nbr, eattr in nbrs.items():
                print(n.text +" : " + str(nbr)+ " : "+ str(eattr) )
        print("-----------------------------")
        #PolicyStatement.customize_draw(p, p.nodes())
        return p.nodes()


    def contains_condition_anchor(self,G1):
        parent_has_condition = False
        child_has_condition = False
        for n in self.graph.nodes():
            if self.graph.nodes[n]['role'] == "condition":
                parent_has_condition = True
                break
        if parent_has_condition:
            for n in G1.nodes():
                if G1.nodes[n]['role'] == "condition":
                    child_has_condition = True
                    break
        return child_has_condition



# read raw data
def read_data(filename):
    sheet = pd.read_excel(filename)
    return sheet


# to better present the results
def liststoString(all_matched_condition):
    s= ""
    for c in all_matched_condition:
        s +=  str(c)+ "\n"
    return s


def max_length(all_matched_condition):
    max_len = 0
    max_index = -1
    for index, c in enumerate(all_matched_condition):
        if len(c) >= max_len:
            max_len = len(c)
            max_index = index

    candidate = []
    for c in all_matched_condition:
        if len(c) == max_len:
            candidate.append(c)

    string_len_max = 0
    string_len_max_index = -1
    for index , c in enumerate(candidate):
        if len(str(c)) >= string_len_max:
            string_len_max = len(str(c))
            string_len_max_index = index

    return candidate[string_len_max_index]


def get_raw_file():
    xlsx_list = glob.glob('../raw data/40_post_processed_data/policy_statement_discovery/*.xlsx')  # get all the filenames of the prepossed 40 toses
    print(u'have found %s xlsx files' % len(xlsx_list))
    print(u'正在处理............')
    return xlsx_list



def extract_statement(filename,pattern_map):
    # filename = "../data/raw data/facebook.xlsx"

    sheet = read_data(filename)

    # get match pattern

    sheet_with_filter= sheet.loc[sheet["predict_label"] == 1]
    sentence_list = sheet_with_filter["sentence_list"]
    all_matched_condition_list = []
    pattern_condition_list = []
    #sentence_list = ["Your privacy policy must (i) provide notice of your use of a tracking pixel, agent or any other visitor identification technology that collects, uses, shares and stores data about end users of your applications and Recommendations; "]

    for index, sentence in enumerate(sentence_list):
        ##############################################obtain_condition##################################################
        paragraph = sheet.loc[index, "align_paragraph"]

        all_matched_condition, pattern_condition = get_condition(sentence,paragraph,pattern_map)
        if len(all_matched_condition) == 0:
            all_matched_condition_list.append("")
            pattern_condition_list.append("")
        else:
            all_matched_condition_list.append(str(max_length(all_matched_condition)))
            pattern_condition_list.append(str(pattern_condition))


    new_sheet = sheet.loc[sheet["predict_label"] == 1].copy()
    new_sheet["all_matched_condition_list"] = all_matched_condition_list
    new_sheet["pattern_condition_list"] = pattern_condition_list

    to_filemame = "../raw data/40_post_processed_data/condition_extraction/" + filename.split("policy_statement_discovery/")[1]
    new_sheet.to_excel(to_filemame, index=False, encoding="utf8",
                header=["sentence_list", "align_paragraph", "subject_co_reference", "co_reference_list",
                        "verb_subject_list", "score", "predict_label", "verb_entity_list_without_filter",
                        "verb_entity_list_with_filter", "nmod_entity_list","all_matched_condition_list","pattern_condition_list"])





def get_condition(sentence,paragraph,pattern_map):
    all_matched_condition = []
    pattern_condition = []

    ### if sentence match pattern 1
    if PolicyStatement.is_sentence_condition_pattern_1(sentence):
        pattern = pattern_map["consent"]
        clauses = split_clauses(PolicyStatement.predictor, sentence, 1)
        try:
            target_clause = [c for c in clauses if PolicyStatement.is_sentence_condition_pattern_1(c)][0]
            print("the target clause is :  " + target_clause)
            policy_state_object = PolicyStatement(target_clause,paragraph)
        except:
            policy_state_object = PolicyStatement(sentence, paragraph)
        all_matched_condition, pattern_condition = policy_state_object.get_most_similar_graph("consent",3,pattern)
        print("all matched condition is " +str(all_matched_condition))
        print("pattern_condition " + str(pattern_condition))


    elif PolicyStatement.is_sentence_condition_pattern_7(sentence):
        clauses = split_clauses(PolicyStatement.predictor, sentence, 1)
        try:

            target_clause = [c for c in clauses if PolicyStatement.is_sentence_condition_pattern_7(c)][0]
            keyword = PolicyStatement.pattern_7_key_word(target_clause)
            index = clauses.index(target_clause)
            sub = target_clause.lower().split(keyword)[1]
            if len(sub) != 0:
                key_clause =keyword+ " " + target_clause.split(keyword)[1]
                p = [t.text for t in PolicyStatement.nlp(key_clause)]
                all_matched_condition.append(p)
            else:
                next_clause = clauses[index+1]
                key_clause = keyword + " " + next_clause
                p = [t.text for t in PolicyStatement.nlp(key_clause)]
                all_matched_condition.append(p)

        except:
            keyword = PolicyStatement.pattern_7_key_word(sentence)
            key_sentence = keyword + " "+sentence.lower().split(keyword)[1]
            p=[t.text for t in PolicyStatement.nlp(key_sentence)]
            all_matched_condition.append(p)
        pattern_condition = ["Except", "to",  "you","have","a separate agreement"]


    ### if sentence match pattern 2
    elif PolicyStatement.is_sentence_condition_pattern_2(sentence):
        pattern = pattern_map["privacy_policy"]
        clauses = split_clauses(PolicyStatement.predictor, sentence, 1)
        try:
            target_clause = [c for c in clauses if PolicyStatement.is_sentence_condition_pattern_2(c)][0]
            print("the target clause is :  " + target_clause)
            policy_state_object = PolicyStatement(target_clause, paragraph)
        except:
            policy_state_object = PolicyStatement(sentence, paragraph)
        all_matched_condition, pattern_condition = policy_state_object.get_most_similar_graph("privacy_policy",5,pattern)
        print("all matched condition is " + str(all_matched_condition))
        print("pattern_condition " + str(pattern_condition))


    ### if sentence match pattern 3
    elif PolicyStatement.is_sentence_condition_pattern_3(sentence):
        pattern = pattern_map["only"]
        clauses = split_clauses(PolicyStatement.predictor, sentence, 0)
        try:
            target_clause = [c for c in clauses if PolicyStatement.is_sentence_condition_pattern_3(c)][0]
            print("the target clause is :  " + target_clause)
            policy_state_object = PolicyStatement(target_clause, paragraph)
        except:
            policy_state_object = PolicyStatement(sentence, paragraph)
        all_matched_condition, pattern_condition = policy_state_object.get_most_similar_graph( "only", 7,pattern)
        print("all matched condition is " + str(all_matched_condition))
        print("pattern_condition " + str(pattern_condition))


    ### if sentence match pattern 4
    elif PolicyStatement.is_sentence_condition_pattern_4(sentence):
        pattern = pattern_map["comply"]
        clauses = split_clauses(PolicyStatement.predictor, sentence, 1)
        try:
            target_clause = [c for c in clauses if PolicyStatement.is_sentence_condition_pattern_4(c)][0]
            print("the target clause is :  " + target_clause)
            policy_state_object = PolicyStatement(target_clause, paragraph)
        except:
            policy_state_object = PolicyStatement(sentence, paragraph)
        all_matched_condition, pattern_condition = policy_state_object.get_most_similar_graph("comply", 7, pattern)
        print("all matched condition is " + str(all_matched_condition))
        print("pattern_condition " + str(pattern_condition))


    ### if sentence match pattern 5
    elif PolicyStatement.is_sentence_condition_pattern_5(sentence):
        pattern = pattern_map["accord"]
        clauses = split_clauses(PolicyStatement.predictor, sentence, 0)
        try:
            target_clause = [c for c in clauses if PolicyStatement.is_sentence_condition_pattern_5(c)][0]
            print("the target clause is :  " + target_clause)
            policy_state_object = PolicyStatement(target_clause, paragraph)
        except:
            policy_state_object = PolicyStatement(sentence, paragraph)
        all_matched_condition, pattern_condition = policy_state_object.get_most_similar_graph( "accord",7,pattern)
        print("all matched condition is " + str(all_matched_condition))
        print("pattern_condition " + str(pattern_condition))

    ### if sentence match pattern 6
    elif PolicyStatement.is_sentence_condition_pattern_6(sentence):
        pattern = pattern_map["if"]
        clauses = split_clauses(PolicyStatement.predictor, sentence, 0)
        try:
            target_clause = [c for c in clauses if PolicyStatement.is_sentence_condition_pattern_6(c)][0]
            print("the target clause is :  " + target_clause)
            policy_state_object = PolicyStatement(target_clause, paragraph)
        except:
            policy_state_object = PolicyStatement(sentence, paragraph)
        all_matched_condition, pattern_condition = policy_state_object.get_most_similar_graph( "if", 4,pattern)
        print("all matched condition is " + str(all_matched_condition))
        print("pattern_condition " + str(pattern_condition))



    return all_matched_condition, pattern_condition






def main():
    start = datetime.datetime.now()
    xlsx_list = get_raw_file()
    print(xlsx_list)
    pattern = Pattern()
    pattern_map = pattern.map
    for filename in xlsx_list:
        to_filemame = "../raw data/40_post_processed_data/condition_extraction/" + filename.split("policy_statement_discovery/")[1]
        #if filename.split("policy_statement_discovery/")[1] != "test.xlsx":
            #continue
        if os.path.exists(to_filemame):
            continue
        print("===============================" + str(to_filemame))
        extract_statement(filename,pattern_map)
    end = datetime.datetime.now()
    print (end-start)


if __name__ == '__main__':
    main()
