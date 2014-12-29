package edu.illinois.cs.cogcomp.bionlp;//package GeneralData_preprocess;

import java.util.ArrayList;
import java.util.Vector;
	public class phrase_annotations{
       public int head_index=-1;	
	   Vector <offsets> wordOffsets=new Vector<offsets>();	
	   offsets phrase_offset=new offsets();
	   public String phrase_content="";
	   Vector <String> label=new Vector <String>(); // this includes the multi-labels form the ontology of habitats for the case of role habitat
	   String id;
	   String role; // each entity has one of these roles {habitat, bacterium, geographical} 
	   Integer[] wordIndexes={}; 
	   Integer sentence_id;
	   int[] coreferences;
	   ArrayList<String> features=new ArrayList<String>();
	   
	   public String content(BIOsentence s){
	   phrase_content="";
	   for (int i=0;i<wordIndexes.length;i++)
			{
			  //if (phrase_content+s.sentence_feat.elementAt(wordIndexes[i]).words.word) 
			if(i==0)
				phrase_content=s.sentence_feat.elementAt(wordIndexes[i]).words.word; 
			else
			  if (s.sentence_feat.elementAt(wordIndexes[i-1]).words.wordOffset.E==s.sentence_feat.elementAt(wordIndexes[i]).words.wordOffset.B)
			      phrase_content=phrase_content+s.sentence_feat.elementAt(wordIndexes[i]).words.word;
			  else
				  phrase_content=phrase_content+" "+s.sentence_feat.elementAt(wordIndexes[i]).words.word; 
			}
		return phrase_content;
	   }
	   
	   public String span(BIOsentence s){ 
		   //it produces the string related to the span of the phrase according to the format of the a2 files. when the span is not continues
		   // the span of the continuous parts are separated  by simicolon and all written.
			String phrase_span=Integer.toString(s.sentence_feat.elementAt(wordIndexes[0]).words.wordOffset.B);
			int endO=s.sentence_feat.elementAt(wordIndexes[0]).words.wordOffset.E;
			for (int i=1;i<wordIndexes.length;i++)
				{
				  if (s.sentence_feat.elementAt(wordIndexes[i]).words.wordOffset.B==endO+1||s.sentence_feat.elementAt(wordIndexes[i]).words.wordOffset.B==endO)
				    endO=s.sentence_feat.elementAt(wordIndexes[i]).words.wordOffset.E;
				  
				  else
					{
					  phrase_span=phrase_span+" "+Integer.toString(s.sentence_feat.elementAt(wordIndexes[i-1]).words.wordOffset.E);
					  phrase_span=phrase_span+";" +Integer.toString(s.sentence_feat.elementAt(wordIndexes[i]).words.wordOffset.B);
					}
				}
			phrase_span=phrase_span+" "+Integer.toString(s.sentence_feat.elementAt(wordIndexes[wordIndexes.length-1]).words.wordOffset.E);
			return phrase_span;
		   }
     }
	//public void add_annotations(phrase_annotations x){
		//discourse_ann.addElement(x);
//	}
	

