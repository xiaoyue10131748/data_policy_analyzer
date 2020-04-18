package edu.stanford.nlp.sequences;
import edu.stanford.nlp.util.logging.Redwood;
import usage.StanfordCRFTagger;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;

import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.objectbank.DelimitRegExIterator;
import edu.stanford.nlp.objectbank.IteratorFromReaderFactory;
import java.util.function.Function;
import edu.stanford.nlp.util.StringUtils;

import edu.stanford.nlp.simple.*;
/**
 * DocumentReader for column format.
 *
 * @author Jenny Finkel
 */
public class ColumnDocumentReaderAndWriter implements DocumentReaderAndWriter<CoreLabel>  {

  /** A logger for this class */
  private static final Redwood.RedwoodChannels log = Redwood.channels(ColumnDocumentReaderAndWriter.class);

  private static final long serialVersionUID = 3806263423697973704L;
  private static final boolean includeProbabilities = false;

//  private SeqClassifierFlags flags; // = null;
  //map can be something like "word=0,tag=1,answer=2"
  @SuppressWarnings("rawtypes")
  private Class[] map; // = null;
  private IteratorFromReaderFactory<List<CoreLabel>> factory;

//  public void init(SeqClassifierFlags flags) {
//    this.flags = flags;
//    this.map = StringUtils.mapStringToArray(flags.map);
//    factory = DelimitRegExIterator.getFactory("\n(\\s*\n)+", new ColumnDocParser());
//  }

  @Override
  public void init(SeqClassifierFlags flags) {
    init(flags.map);
  }


  public void init(String map) {
    // this.flags = null;
    this.map = CoreLabel.parseStringKeys(StringUtils.mapStringToArray(map));
    factory = DelimitRegExIterator.getFactory("\n(?:\\s*\n)+", new ColumnDocParser());
  }

  @Override
  public Iterator<List<CoreLabel>> getIterator(Reader r) {
    return factory.getIterator(r);
  }

  // private int num; // = 0;


  private class ColumnDocParser implements Serializable, Function<String,List<CoreLabel>> {

    private static final long serialVersionUID = -6266332661459630572L;
    private final Pattern whitePattern = Pattern.compile("\\s+"); // should this really only do a tab?

    private int lineCount; // = 0;

    @Override
    public List<CoreLabel> apply(String doc) {
      // if (num > 0 && num % 1000 == 0) { log.info("["+num+"]"); } // cdm: Not so useful to do in new logging world
      // num++;
    	

      //Sentence sent = new Sentence("Lucy is in the sky with diamonds.");
      //List<String> nerTags = sent.nerTags();  // [PERSON, O, O, O, O, O, O, O]
      //String firstPOSTag = sent.posTag(0); 
      List<CoreLabel> words = new ArrayList<>();
      String[] lines = doc.split("\n");

      for (String line : lines) {
        ++lineCount;
        if (line.trim().isEmpty()) {
          continue;
        }
        // Optimistic splitting on tabs first. If that doesn't work, use any whitespace (slower, because of regexps).
        String[] info = line.split("\t");

        /*
         * String[] info;
        //判断是否加了feature
        if (info1.length == 2) {
        	 info = info1;
        }else {
             info = new String[4];
            for(int i=0;i<info1.length;i++)
    		{
            	info[i]=info1[i];
    		}
            info[info1.length] = info1[info1.length-1];
        }
        */

        if (info.length == 1) {
          info = whitePattern.split(line);
        }
        CoreLabel wi;
        try {
          wi = new CoreLabel(map, info);
          // Since the map normally only specified answer, we copy it to GoldAnswer unless they've put something else there!
          if ( ! wi.containsKey(CoreAnnotations.GoldAnswerAnnotation.class) && wi.containsKey(CoreAnnotations.AnswerAnnotation.class)) {
            wi.set(CoreAnnotations.GoldAnswerAnnotation.class, wi.get(CoreAnnotations.AnswerAnnotation.class));
          }
        } catch (RuntimeException e) {
          log.info("Error on line " + lineCount + ": " + line);
          throw e;
        }
        words.add(wi);
      }
      return words;
    }

  } // end class ColumnDocParser


  @Override
  public void printAnswers(List<CoreLabel> doc, PrintWriter out) {
	  //String filename_yue = "/Users/huthvincent/Desktop/paper_works/filter_Sentence_Based_Verb/ner_model/result.txt";
	  File defaultPath =new File(System.getProperty("user.dir"));
	  String currentPath = new File("").getAbsolutePath();
	  String[] a = currentPath.split("/Ner");
	  //System.out.println("===============yueyueyuey");
	  System.out.println(currentPath);
	  String filename_yue = a[0]+"/result.txt" ;
	  try {
          BufferedWriter out1 = new BufferedWriter(new FileWriter(filename_yue,true));
          for (CoreLabel wi : doc) {
              String answer = wi.get(CoreAnnotations.AnswerAnnotation.class);
              String goldAnswer = wi.get(CoreAnnotations.GoldAnswerAnnotation.class);
              if (includeProbabilities) {
                double answerProb = wi.get(CoreAnnotations.AnswerProbAnnotation.class);
                out.println(wi.word() + '\t' + goldAnswer + '\t' + answer + '\t' + answerProb);
              } else {
                out.println(wi.word() + '\t' + goldAnswer + '\t' + answer);

                out1.write(wi.word() + '\t' + goldAnswer + '\t' + answer + "\n");
                
                
              }
            }
          out1.write("\n");
          out1.close();
          out.println();
  } catch (IOException e) {
  }

    

  }
  
  
  /* orignal code
  public void printAnswers(List<CoreLabel> doc, PrintWriter out) {

    for (CoreLabel wi : doc) {
      String answer = wi.get(CoreAnnotations.AnswerAnnotation.class);
      String goldAnswer = wi.get(CoreAnnotations.GoldAnswerAnnotation.class);
      if (includeProbabilities) {
        double answerProb = wi.get(CoreAnnotations.AnswerProbAnnotation.class);
        out.println(wi.word() + '\t' + goldAnswer + '\t' + answer + '\t' + answerProb);
      } else {
        out.println(wi.word() + '\t' + goldAnswer + '\t' + answer);
        
      }
    }
    out.println();
    

  }
  */
  
  
  
  //add by yue 
  // write the results to file
  public void writeAnswers(List<CoreLabel> doc, String filename) {
      try {
          BufferedWriter out1 = new BufferedWriter(new FileWriter(filename,true));
    	  for (CoreLabel wi : doc) {
    		  String answer = wi.get(CoreAnnotations.AnswerAnnotation.class);
    	      String goldAnswer = wi.get(CoreAnnotations.GoldAnswerAnnotation.class);
    	      String word = wi.word();
              out1.write(wi.word() + '\t' + goldAnswer + '\t' + answer + "\n");
              out1.close();

    	  }
    	  
    	  out1.write("\n");
          //System.out.println("文件创建成功！");
      } catch (IOException e) {
      }

  }

}
