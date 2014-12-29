package edu.illinois.cs.cogcomp.bionlp;//package GeneralData_preprocess;

import java.util.Vector;

//import GeneralData_preprocess.offsets;


public class ling_features {
//	    public class offsets{
//	    	int B=0;
//	    	int E=0;
//	    }
 //public void ling_features(){
	
//}
		public class rel_word_features{
			Vector<String> path=new Vector<String>();
			Vector<Integer> distance=new Vector<Integer>();
			Vector<String> deppath=new Vector <String>();
		}
		public class word_features{
			offsets wordOffset=new offsets();
			String word=null;
			//int windex=0;
			String subcategorization=null;
			String pos=null;
			String head=null; 
			//String phrasetype=null;
			String DPRL=null;
			String SRL=null;
			String lemma=null;
			String a2=null;
			String cocoa=null;
		
		}
		public class phrase_annotations{
		   Vector <offsets> phraseOffset=new Vector<offsets>();	
		 }
		
		word_features words=new word_features();
		rel_word_features word_word=new rel_word_features();
	}// end ling_features definition

