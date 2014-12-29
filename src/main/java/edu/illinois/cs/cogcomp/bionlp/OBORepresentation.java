package edu.illinois.cs.cogcomp.bionlp;

import Utility.PreProcessing;
import Utility.Util;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Vector;
import java.util.regex.Pattern;

public class OBORepresentation {

	Hashtable oboFileRepresentation = new Hashtable();
	ArrayList<String> oboIDs=new ArrayList<String>();
	
	ArrayList<String> listOfExactSyns,listOfRelatedSyns, listOfNames;
	
	public OBORepresentation() {
		 oboFileRepresentation = new Hashtable();
	} 
	
	public void addOBOElement(String id,OBOElement obEle){
		
		oboFileRepresentation.put(id, obEle);
		oboIDs.add(id);
	}
	
	public OBOElement getOBOElement(String id){
		OBOElement retob=(OBOElement) oboFileRepresentation.get(id);
		return retob;
	}

	public String[] getParents(String oboID){
		OBOElement obEle=getOBOElement(oboID);
		ArrayList<String> parentIDs= obEle.getIs_a();
		String[] tmpStr=new String[parentIDs.size()];
		parentIDs.toArray(tmpStr);
		return tmpStr;	
	}
	
	public String[] getXREFs(String oboID){
		OBOElement obEle=getOBOElement(oboID);
		ArrayList<String> xrefIDs= obEle.getXref();
		String[] tmpStr=new String[xrefIDs.size()];
		xrefIDs.toArray(tmpStr);
		return tmpStr;	
	}
	
	public String[] getExactSynonyms(String oboID){
		OBOElement obEle=getOBOElement(oboID);
		ArrayList<String> exactSynonyms= obEle.getExact_synonym();
		String[] tmpStr=new String[exactSynonyms.size()];
		exactSynonyms.toArray(tmpStr);
		return tmpStr;	
	}
	
	public String[] getRelatedSynonyms(String oboID){
		OBOElement obEle=getOBOElement(oboID);
		ArrayList<String> relatedSynonyms= obEle.getRelated_synonym();
		String[] tmpStr=new String[relatedSynonyms.size()];
		relatedSynonyms.toArray(tmpStr);
		return tmpStr;	
	}
	
	public int getNumberOfObo(){
		return oboFileRepresentation.size();
	}
	
	public ArrayList<String> getExactAllSynonyms(){
		
		ArrayList<String> arrExactSyns=new ArrayList<String>();
		for (int i=0;i<oboIDs.size();i++){
			String tmpID=oboIDs.get(i);
		     OBOElement oboEl=(OBOElement) oboFileRepresentation.get(tmpID);
		     ArrayList<String>  arr2=oboEl.getExact_synonym();
		     for (int j=0;j<arr2.size();j++){
		    	 arrExactSyns.add(arr2.get(j));
		     }
		}
		listOfExactSyns=arrExactSyns;
		return  arrExactSyns;	
	}

	public ArrayList<String> getRelatedAllSynonyms(){
		
		ArrayList<String> arrRelatedSyns=new ArrayList<String>();
		for (int i=0;i<oboIDs.size();i++){
			String tmpID=oboIDs.get(i);
		     OBOElement oboEl=(OBOElement) oboFileRepresentation.get(tmpID);
		     ArrayList<String>  arr2=oboEl.getRelated_synonym();
		     for (int j=0;j<arr2.size();j++){
		    	 arrRelatedSyns.add(arr2.get(j));
		     }
		}
		listOfRelatedSyns=arrRelatedSyns;
		return  listOfRelatedSyns;	
	}
	
public ArrayList<String> getAllNames(){
		
		ArrayList<String> arrNames=new ArrayList<String>();
		for (int i=0;i<oboIDs.size();i++){
			 String tmpID=oboIDs.get(i);
		     OBOElement oboEl=(OBOElement) oboFileRepresentation.get(tmpID);
		     String strTemp=oboEl.getName();
		     arrNames.add(strTemp);
		}
		listOfNames=arrNames;
		return  listOfNames;	
	}

//returns indexed list of integers
public Integer[] getIndexesOfExistingWords(Vector<ling_features> s){
	ArrayList<Integer> objRet=new ArrayList<Integer>();
	//Pattern p1 = Pattern.compile(" ");
	//String[] tokenStr=sentence.split(sentence);
	
	for (int i=0;i<s.size();i++){
		boolean	exist=false;
		for (int j=0;j<listOfExactSyns.size();j++){
			if (listOfExactSyns.get(j).contains(s.elementAt(i).words.word)) {
				exist=true;
				break;
			}
		}
		for (int j=0;j<listOfRelatedSyns.size();j++){
			if (listOfRelatedSyns.get(j).contains(s.elementAt(i).words.word)) {
				exist=true;
				break;
			}
		}
		
		for (int j=0;j<listOfNames.size();j++){
			if (listOfNames.get(j).contains(s.elementAt(i).words.word)) {
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
public boolean[] match_Habitat(String phrase){// a[1] shows containment and a[2] shows the overlap
	boolean[] a={false,false,false,false};
	for (int j=0;j<listOfNames.size();j++){
			if (listOfNames.get(j).trim().contains(phrase) | phrase.contains(listOfNames.get(j))) 
			  {
				a[0]= true;
				if (a[1]==true) return a;
				}
		    if (a[1]==true) continue;	
			if (overlap(listOfNames.get(j), phrase))
			    {    
				    a[1]= true;
				    if (a[0]==true) return a;
				 }
				}
		
	   return a;
 }
public boolean exists_Habitat_inOBO(String[] s1){// a[1] shows containment and a[2] shows the overlap
	//boolean a=false;//,false,false,false};
	// BacteriaToken can be applied for both habitats and bacteria 
	//it is just cleaning not alphanumeric characters and case, etc. 
	String ephrase="";
	for (int w=0;w<s1.length;w++){
		ephrase=ephrase+" "+s1[w];
		}
	ephrase=PreProcessing.BacteriaToken(ephrase);
	for (int j=0;j<listOfNames.size();j++){
		String s=PreProcessing.BacteriaToken(listOfNames.get(j));
		//if (s.equals(ephrase)) 
			//	 return true;
		 if (overlap(s,ephrase))
			return true;
	   }	
		
	for (int j=0;j<this.listOfExactSyns.size();j++){
		String s=PreProcessing.BacteriaToken(listOfExactSyns.get(j));
		//if ( s.equals(ephrase)) 
			// return true;
		if (overlap(s,ephrase))
			return true;
	}
	for (int j=0;j<this.listOfRelatedSyns.size();j++){
		String s=PreProcessing.BacteriaToken(listOfRelatedSyns.get(j));
		//if (s.equals(ephrase)) 
		  // return true;
		if (overlap(s,ephrase))
			return true;
	}
	   return false;
}
public boolean overlap(String s1, String s2)
{
	Pattern p=Pattern.compile(" ");
	String[] s1Elements=p.split(s1);
	String[] s2Elements=p.split(s2);
	
 for (int i=0;i<s1Elements.length; i++)
	for (int j=0;j<s2Elements.length;j++)
		 if (s1Elements[i].equalsIgnoreCase(s2Elements[j]) )
				 if (!Util.contains_str(FEx_BIONLP_BB_PerSentence_train_test_MoreGlobalBMC.StopWords,s1Elements[i])) 
					 	return true;
	return false;
	}
}
