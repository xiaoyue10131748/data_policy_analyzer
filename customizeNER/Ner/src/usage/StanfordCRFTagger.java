package usage;
import edu.stanford.nlp.ie.crf.CRFClassifier;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.sequences.SeqClassifierFlags;
import edu.stanford.nlp.util.StringUtils;
import java.io.File;
import java.io.File;
import java.io.IOException;
import java.io.*;
import edu.stanford.nlp.util.Triple;

import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class StanfordCRFTagger {
    public void trainAndWrite(String modelOutPath, String prop, String trainingFilepath) {
        Properties props = StringUtils.propFileToProperties(prop);
        props.setProperty("serializeTo", modelOutPath);

        //if input use that, else use from properties file.
        if (trainingFilepath != null) {
            props.setProperty("trainFile", trainingFilepath);
        }


        SeqClassifierFlags flags = new SeqClassifierFlags(props);


        CRFClassifier<CoreLabel> crf = new CRFClassifier<>(flags);
        crf.train();

        crf.serializeClassifier(modelOutPath);
    }

    public CRFClassifier getModel(String modelPath) {
        return CRFClassifier.getClassifierNoExceptions(modelPath);
    }

    
    
    public Triple<Double,Double,Double> getScores(String propsFile, String trainfile, String modelLoc, String testfile) throws IOException {
 
        StanfordCRFTagger tagger = new StanfordCRFTagger();
        tagger.trainAndWrite(modelLoc, propsFile, trainfile);

        CRFClassifier model = tagger.getModel(modelLoc);
    	Triple<Double,Double,Double> scores = model.classifyAndWriteAnswers(testfile, true);
        //System.out.print("============");
    	
    	return scores;
    }
    



    public static void main(String[] args)throws IOException {
    	
    	String modelLoc = "/Users/huthvincent/Desktop/paper_works/filter_Sentence_Based_Verb/ner_model/Ner/xiaoyue/featureByAllenNLP/model/stanfordCRF.model";
    	String propsFile = "/Users/huthvincent/Desktop/paper_works/filter_Sentence_Based_Verb/ner_model/Ner/xiaoyue/featureByAllenNLP/train_data/data/stanfordNER.prop";
   	 	String trainfile = "/Users/huthvincent/Desktop/paper_works/filter_Sentence_Based_Verb/ner_model/Ner/xiaoyue/featureByAllenNLP/train_data/data/result_feature_v1.tsv";
        StanfordCRFTagger tagger = new StanfordCRFTagger();
        tagger.trainAndWrite(modelLoc, propsFile, trainfile);

    }

}

