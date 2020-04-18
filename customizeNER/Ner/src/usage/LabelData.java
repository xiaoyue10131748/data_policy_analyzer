package usage;
import edu.stanford.nlp.ie.AbstractSequenceClassifier;
import edu.stanford.nlp.ie.crf.*;
import edu.stanford.nlp.io.IOUtils;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.sequences.DocumentReaderAndWriter;
import edu.stanford.nlp.util.Triple;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class LabelData {
	
	//static String modelLoc = "/Users/huthvincent/Desktop/paper_works/filter_Sentence_Based_Verb/ner_model/Ner/xiaoyue/featureByAllenNLP/model/stanfordCRF.model";
    
	static String modelLoc = "/Users/huthvincent/Documents/research/malicious_library_hunting/data_policy_analyzer/customizeNER/stanfordCRF.model";
	public static CRFClassifier getModel(String modelPath) {
        return CRFClassifier.getClassifierNoExceptions(modelPath);
    }
    

	public static void main(String[] args)throws Exception {
		System.out.println(args.length);
		System.out.println(args[0]);
		
		File defaultPath =new File(System.getProperty("user.dir"));
		String currentPath = new File("").getAbsolutePath();
		System.out.println(currentPath);
		
		String[] a = currentPath.split("/Ner");
		 String modelLoc = a[0] + "/stanfordCRF.model";
		 //System.out.println("===============modelpath");
		 System.out.println(modelLoc);
	    AbstractSequenceClassifier<CoreLabel> classifier = getModel(modelLoc);

	      
	    if (args.length >= 1) {

	        /* For the file, it shows (1) how to run NER on a String, (2) how
	           to get the entities in the String with character offsets, and
	           (3) how to run NER on a whole file (without loading it into a String).
	        */
	    	String testfile = args[0];
	    	Triple<Double,Double,Double> scores = classifier.classifyAndWriteAnswers(testfile, true);
	    	
	      }
			

	}


}
