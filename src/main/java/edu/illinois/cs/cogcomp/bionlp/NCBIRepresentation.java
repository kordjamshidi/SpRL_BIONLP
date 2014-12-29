package edu.illinois.cs.cogcomp.bionlp;

import Utility.PreProcessing;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Vector;
import java.util.regex.Pattern;

public class NCBIRepresentation {

	Hashtable NCBIFileRepresentation = new Hashtable();
	ArrayList<String> NCBIIDs=new ArrayList<String>();
	
	ArrayList<NCBIElement> listOfBacteria=new ArrayList<NCBIElement>();
	
	public NCBIRepresentation() {
		 NCBIFileRepresentation = new Hashtable();
	} 
	
	public void addNCBIElement(NCBIElement obEle){
		listOfBacteria.add(obEle);
		//NCBIFileRepresentation.put(id, obEle);
		//NCBIIDs.add(id);
	}
	
	public NCBIElement getNCBIElement(String id){
		NCBIElement retob=(NCBIElement) NCBIFileRepresentation.get(id);
		return retob;
	}

	
	public int getNumberOfNCBI(){
		return NCBIFileRepresentation.size();
	}
	
	
	
	public Integer[] getIndexesOfExistingWords(Vector<ling_features> s){ // the index of the words that occur in obo are retrievd for a sentence
		ArrayList<Integer> objRet=new ArrayList<Integer>();
		//Pattern p1 = Pattern.compile(" ");
		//String[] tokenStr=sentence.split(sentence);
		
		for (int i=0;i<s.size();i++){
			boolean	exist=false;
			for (int j=0;j<listOfBacteria.size();j++){
				if (listOfBacteria.get(j).getName().contains(s.elementAt(i).words.word)) {
					exist=true;
					break;
				}
			}
		if (exist) objRet.add(i);	
		}
		
		Integer[] retArr=new Integer[objRet.size()];
		objRet.toArray(retArr);
		return retArr;
	}	

public ArrayList<String> getListOfBacteria(){
		
		ArrayList<String> arrNames=new ArrayList<String>();
		for (int i=0;i<NCBIIDs.size();i++){
			 String tmpID=NCBIIDs.get(i);
		     NCBIElement NCBIEl=(NCBIElement) NCBIFileRepresentation.get(tmpID);
		     String strTemp=NCBIEl.getName();
		     arrNames.add(strTemp);
		}
		//listOfBacteria=arrNames;
		//return  listOfBacteria;
		return arrNames;
	}


//returns indexed list of integers
public boolean[] match_Bacteria(String phrase){// a[1] shows containment and a[2] shows the overlap
	boolean[] a={false,false};
	for (int j=0;j<listOfBacteria.size();j++){
			if (listOfBacteria.get(j).getIdElement().trim().contains(phrase) | phrase.contains(listOfBacteria.get(j).getIdElement() )) 
			  {
				a[0]= true;
				if (a[1]==true) return a;
				}
		    if (a[1]==true) continue;	
			if (overlap(listOfBacteria.get(j).getName(), phrase))
			    {    
				    a[1]= true;
				    if (a[0]==true) return a;
				 }
				}
		
	   return a;
 }
public boolean overlap(String s1, String s2)
{
	Pattern p=Pattern.compile(" ");
	String[] s1Elements=p.split(s1);
	String[] s2Elements=p.split(s2);
	
 for (int i=0;i<s1Elements.length; i++)
	for (int j=0;j<s2Elements.length;j++)
		 if (s1Elements[i].equalsIgnoreCase(s2Elements[j]))
				 if (!Utility.Util.contains_str(FEx_BIONLP_BB_PerSentence_train_test_MoreGlobalBMC.StopWords,s1Elements[i])) 
					 	return true;
	return false;
	}
public boolean overlap(String[] s1, String[] s2)
{
	
 for (int i=0;i<s1.length; i++)
	for (int j=0;j<s2.length;j++)
		 if (s1[i].equalsIgnoreCase(s2[j]))
				 if (!Utility.Util.contains_str(FEx_BIONLP_BB_PerSentence_train_test_MoreGlobalBMC.StopWords,s1[i])) 
					 	return true;
	return false;
	}

public boolean exists_Bactriun_NCBI(String[] s1) {
	Pattern p=Pattern.compile(" ");
	for (int j=0;j<listOfBacteria.size();j++){
		if (j==61610)
		{
			j=j+0;
			}
		    String s=listOfBacteria.get(j).getIdElement();
		    String[] s1Elements=p.split(s);
		    for (int i=0; i<s1Elements.length;i++)
		    	s1Elements[i]=PreProcessing.BacteriaToken(s1Elements[i]);
		    s1Elements=PreProcessing.BacteriafullName(s1Elements);
		    
		    //if (Arrays.deepEquals(s1Elements,s1))
		    //			return true;
		    if (overlap(s1Elements,s1))
		    	return true;
		    
		    }
	return false;

	}
}
