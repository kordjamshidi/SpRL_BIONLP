package edu.illinois.cs.cogcomp.bionlp;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Vector;
import org.w3c.dom.Node;


public class iEntity extends phrase_annotations{
	public ArrayList <String> features=new ArrayList <String>();
	public int[] featureMap;
	public void initialize(int lexicon_num){
			featureMap=new int[lexicon_num];
		
	}
	//public iEntity(List <Node> tokens, Node context) {
		
		//features= new ArrayList<String>();
//		for (int i=0;i<=tokens.size();i++)
//		 {
// 			if (i==0)
//			 features.add("wb_"+tokens);
// 			else
// 			if (i==tokens.size()-1)
// 			  features.add("we_"+tokens);
// 			else
//			 features.add("w_"+tokens);
//		 }
//		// for all tokens in the phrase compute the features
	//  }

	public Vector<String> getLabel() {
		return label;
	}
    public List<String> getFeatures() {
		return Collections.unmodifiableList(features);
	}
    public double[] doubleFeatureMatrix(){
    	double f[]=new double [featureMap.length];
    	for(int i=0;i<featureMap.length;i++)
    	{
    		 f[i]=((double) (featureMap[i]));    	}
		return f;
	}
    public int getFeatureDimension(){
    	return(featureMap.length);
    }
    public void add_features(BIOsentence s, Integer[] indices){
    	for (int i=0; i<indices.length;i++)
    	{
    		wordOffsets.add(s.sentence_feat.elementAt(indices[i]).words.wordOffset);
    		if (i==0)
    		 {
    			features.add("B_"+s.sentence_feat.elementAt(i).words.word);
    		 	features.add("B_"+s.sentence_feat.elementAt(i).words.pos);
    		    features.add("B_"+s.sentence_feat.elementAt(i).words.DPRL);
    		    features.add("B_"+s.sentence_feat.elementAt(i).words.subcategorization);
    		    features.add("B_"+s.sentence_feat.elementAt(i).words.SRL);
    		    features.add("B_"+s.sentence_feat.elementAt(i).words.lemma);
    		    //features.add("B_P_"+s.sentence_feat.elementAt(i).words.PDPRL);
    		    }
    		else
    			
    			if (i==indices.length-1)
    				{
    					features.add("E_"+s.sentence_feat.elementAt(i).words.word);
    					features.add("E_"+s.sentence_feat.elementAt(i).words.word);
    	    	    	features.add("E_"+s.sentence_feat.elementAt(i).words.pos);
    	    		    features.add("E_"+s.sentence_feat.elementAt(i).words.DPRL);
    	    		    features.add("E_"+s.sentence_feat.elementAt(i).words.subcategorization);
    	    		    features.add("E_"+s.sentence_feat.elementAt(i).words.SRL);
    	    		    features.add("E_"+s.sentence_feat.elementAt(i).words.lemma);
    	    		  //  features.add("E_P_"+s.sentence_feat.elementAt(i).words.PDPRL);
    				}
    	    		else
    	    			{
    	    			  features.add("M_"+s.sentence_feat.elementAt(i).words.word);
    	    			  features.add("M_"+s.sentence_feat.elementAt(i).words.word);
	    	    	      features.add("M_"+s.sentence_feat.elementAt(i).words.pos);
	    		          features.add("M_"+s.sentence_feat.elementAt(i).words.subcategorization);
	    		          features.add("M_"+s.sentence_feat.elementAt(i).words.SRL);
	    		          features.add("M_"+s.sentence_feat.elementAt(i).words.lemma);
	    		        //  features.add("M_P_"+s.sentence_feat.elementAt(i).words.PDPRL);
	    		          }
     		features.add(s.sentence_feat.elementAt(i).words.word);
    		features.add("cocoa_"+s.sentence_feat.elementAt(i).words.cocoa);
       }
    }
    
    
  }
