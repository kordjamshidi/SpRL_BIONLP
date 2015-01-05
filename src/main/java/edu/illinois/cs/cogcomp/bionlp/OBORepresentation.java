package edu.illinois.cs.cogcomp.bionlp;




import edu.illinois.cs.cogcomp.bionlp.Utility.PreProcessing;
import edu.illinois.cs.cogcomp.bionlp.Utility.StringSimilarity;
import edu.illinois.cs.cogcomp.bionlp.Utility.Util;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Vector;
import java.util.regex.Pattern;

import static org.apache.commons.lang.math.NumberUtils.max;
import static weka.core.Utils.maxIndex;

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

    public OBOElement FindByExactSynonyms(String s){
        for (int i=0;i<oboIDs.size();i++){
            String tmpID=oboIDs.get(i);
            OBOElement oboEl=(OBOElement) oboFileRepresentation.get(tmpID);
            ArrayList<String>  arr2=oboEl.getExact_synonym();
            for (int j=0;j<arr2.size();j++){
                if (arr2.get(j).equals(s))
                return  oboEl;
            }
        }
    return null;

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
    public OBOElement FindOboBySynonyms(String s){
         for (int i=0;i<oboIDs.size();i++){
            String tmpID=oboIDs.get(i);
            OBOElement oboEl=(OBOElement) oboFileRepresentation.get(tmpID);
            ArrayList<String>  arr2=oboEl.getRelated_synonym();
            for (int j=0;j<arr2.size();j++){
                if (s.equals(arr2.get(j)))
                  return oboEl;
            }
        }
        return null;
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

    public  OBOElement FindObobyName(String name){
        for (int i=0;i<oboIDs.size();i++){
            String tmpID=oboIDs.get(i);
            OBOElement oboEl=(OBOElement) oboFileRepresentation.get(tmpID);
            String strTemp=oboEl.getName();
            if (name.equals(strTemp))
                return(oboEl);
        }
      return null;
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
		String s= PreProcessing.BacteriaToken(listOfExactSyns.get(j));
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
				 if (!Util.contains_str(FEx_BIONLP_BB_PerSentence_train_test_MoreGlobalBMC.StopWords, s1Elements[i]))
					 	return true;
	return false;
	}
public OBOElement closest_OntoNode(String[] s1){
    //boolean a=false;//,false,false,false};
    // BacteriaToken can be applied for both habitats and bacteria
    //it is just cleaning not alphanumeric characters and case, etc.
    double[]sim=new double[3];
    String[] node=new String[3];
    String ephrase="";
    for (int w=0;w<s1.length;w++){
        ephrase=ephrase+" "+s1[w];
    }
    ///////////////////////////////////////////////
    double[] simil=new double[listOfNames.size()];
    for (int j=0;j<listOfNames.size();j++){
        String s=PreProcessing.BacteriaToken(listOfNames.get(j));
        simil[j]= 1-(StringSimilarity.editDistance(s, ephrase.trim()));
        System.out.println(s+" "+ephrase.trim()+" "+simil[j] );
    }
    sim[0]=max(simil);
    node[0]=listOfNames.get(maxIndex(simil));
    ////////////////////////////////////////////

    simil=new double[listOfExactSyns.size()];
    for (int j=0;j<this.listOfExactSyns.size();j++){
        String s= PreProcessing.BacteriaToken(listOfExactSyns.get(j));
        simil[j]= 1-(StringSimilarity.editDistance(s, ephrase.trim()));
    }
    sim[1]= max(simil);
    node[1]=listOfExactSyns.get(maxIndex(simil));
    ////////////////////////////////////////////////

    simil=new double[listOfRelatedSyns.size()];
    for (int j=0;j<this.listOfRelatedSyns.size();j++) {
        String s = PreProcessing.BacteriaToken(listOfRelatedSyns.get(j));
        simil[j] = 1 - (StringSimilarity.editDistance(s, ephrase));
    }
    sim[2]= max(simil);
    node[2]=listOfRelatedSyns.get(maxIndex(simil));
    //////////////////////////////////

    int closestNod=maxIndex(sim);
    OBOElement ob=new OBOElement();
    if (closestNod==0)
        ob=FindObobyName(node[0]);
    if (closestNod==1)
        ob=FindByExactSynonyms(node[1]);
    if (closestNod==2)
        ob=FindOboBySynonyms(node[2]);
    System.out.println("closest to "+ s1+ ob.getName() );
 return ob;
}// end closestOntoNode
    //Obo ontology based similarity
double[] OboSimilarity(String[]S1, String[]S2){
    OBOElement Ob1=new OBOElement();
    OBOElement Ob2=new OBOElement();
    double[] sim={0,0};
    Ob1=closest_OntoNode(S1);
    Ob2=closest_OntoNode(S2);
    if (Ob1.getIs_a().get(0).equals(Ob2.getIdOBOElement())| Ob2.getIs_a().get(0).equals(Ob1.getIdOBOElement()))
      sim[0]= 1;
    if (overlap(Ob1.getName(), Ob2.getName())) sim[1] = 1;
    else sim[1] = 0;
    System.out.println(S1+" "+S2+" "+ sim);
    return (sim);}

}//end OboRepresentation



