package edu.illinois.cs.cogcomp.bionlp;

import GeneralData_preprocess.build_RelDB;
import LBJ2.nlp.SentenceSplitter;
import LBJ2.nlp.Word;
import LBJ2.nlp.WordSplitter;
import LBJ2.nlp.seg.PlainToTokenParser;
import LBJ2.parse.Parser;
import RLpackage.TreeNode;
import RLpackage.TreeRepresentation;
import Utility.PreProcessing;
import Utility.StringSimilarity;
import Utility.Util;
import edu.illinois.cs.cogcomp.lbj.chunk.Chunker;
import edu.ucdenver.ccp.nlp.biolemmatizer.BioLemmatizer;
import edu.ucdenver.ccp.nlp.biolemmatizer.LemmataEntry;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;
import java.util.regex.Pattern;

public class BIOdiscourse {
	String name="";
	//public class discourse_features{
//	private class simTriplet{
//		String id1="";
//		String id2="";
//		double value=0;
//		int label=0;
//	}
//	ArrayList <simTriplet> LocLocMatrix=new ArrayList<simTriplet>(); 
	Vector <BIOsentence> allsentences=new Vector<BIOsentence>(); 
	annotations discourse_annotations=new annotations();
	int[][]CorefMatrix={}; 
	double[][]Similarity_Matrix={};
	int paragraph_num=0;
	
	ArrayList <phrase_annotations> tr_candidates=new ArrayList<phrase_annotations>();
	ArrayList <phrase_annotations> lm_candidates=new ArrayList<phrase_annotations>();
    Vector <iEntity> sp_candidates=new Vector<iEntity>();
    
    ArrayList <String> habitatList=new ArrayList<String>();
	ArrayList <String> bacteriumList=new ArrayList<String>();
	Vector<Integer[]> SpList=new Vector<Integer []>(); 
	Vector<Integer [][]> entityList=new Vector<Integer [][]>(); // to store the entities (integer indexes of them) annotated per sentence, I could have this inside sentence class as we
	
	/* this matrix contains an integer array of the co-references sorted in each row based on their location in the sentence. 
	each integer indicates the integer part of the identifier of an entity. */

	Vector<offsets> Find_id(int sen_id, int tok_id,int BeSpan,int EnSpan){
		
		//ling_features sen=allsentences.elementAt(sen_id).sentence_feat;
		Vector<offsets> toks=new Vector<offsets>();
		for (int i=tok_id; i<allsentences.elementAt(sen_id).sentence_feat.size();i++){
		  if ((allsentences.elementAt(sen_id).sentence_feat.elementAt(i).words.wordOffset.B>=BeSpan && allsentences.elementAt(sen_id).sentence_feat.elementAt(i).words.wordOffset.E<=EnSpan) || (allsentences.elementAt(sen_id).sentence_feat.elementAt(i).words.wordOffset.B<=BeSpan && allsentences.elementAt(sen_id).sentence_feat.elementAt(i).words.wordOffset.E>=EnSpan))
		  {
			 toks.addElement(allsentences.elementAt(sen_id).sentence_feat.elementAt(i).words.wordOffset);}
			 else
				 {
				   if (!toks.isEmpty()) {
					 break;
					 }
				 }
		  }
		return toks;
	}
	public static boolean isInteger(String s) {
	    try { 
	        Integer.parseInt(s); 
	    } catch(NumberFormatException e) { 
	        return false; 
	    }
	    // only got here if we didn't return false
	    return true;
	}	
 Integer[] Find_tokindex(int sen_id, int tok_id,int BeSpan,int EnSpan){
		
		Integer[] toks={};
		for (int i=tok_id; i<allsentences.elementAt(sen_id).sentence_feat.size();i++){
		  if ((allsentences.elementAt(sen_id).sentence_feat.elementAt(i).words.wordOffset.B>=BeSpan && allsentences.elementAt(sen_id).sentence_feat.elementAt(i).words.wordOffset.E<=EnSpan) || (allsentences.elementAt(sen_id).sentence_feat.elementAt(i).words.wordOffset.B<=BeSpan && allsentences.elementAt(sen_id).sentence_feat.elementAt(i).words.wordOffset.E>=EnSpan)){
			  toks  = Arrays.copyOf(toks, toks.length + 1);
			  toks[toks.length-1]=i;}
			 else
				 {
				   if (toks.length!=0) {
					 //System.out.print("empty token");
					 break;
					 }
				 }
		  }
		return toks;
	}
	
	int find_sen(int st,int BeSpan)
	{		
	  
	  while (BeSpan>allsentences.elementAt(st).sentence_feat.lastElement().words.wordOffset.E)
	    {
		  st++;
		}
	   return st;
	}
	
	public Vector<Vector<String>> fetch_lth_matrix(BufferedReader lth_file) throws IOException{
		Vector<Vector <String>> Feature_Matrix=new Vector <Vector<String>>();
		while (lth_file.ready()) {
			String input=lth_file.readLine();
			System.out.println(input);
			Vector <String> Rows=new Vector <String>();
			StringTokenizer st = new StringTokenizer(input);
			while (st.hasMoreTokens())
			{
				Rows.addElement(st.nextToken());}
			if (Rows.size()>=3)
				Feature_Matrix.addElement(Rows);
		}
		return Feature_Matrix;}
	
	public Vector<TreeNode> fetch_chars(BufferedReader brChar) throws IOException{
		TreeNode top=null;
		Vector <TreeNode> tops=new Vector <TreeNode>();
		while (brChar.ready()) {
		String sen1=brChar.readLine();
		System.out.println(sen1);
//		if (sen1.toUpperCase()==sen1){sen1=brChar.readLine();}
		//if (sen1.toUpperCase() != sen1) {
			top = TreeRepresentation.readTree(sen1);
			tops.addElement(top);
		//}
		}
		return tops;
	}
	//public void add_annotations(phrase_annotations x){
		//discourse_annotations.addElement(x);
//	}
	public String readCocoa(offsets f, BufferedReader coco, String co) throws IOException{
	 String CocoaAnn=null;
	 if (co==null) // I could not implement this with a simple or because the null string error
	  {
		if (coco.ready())
			co=coco.readLine();
		else return null;
		
	  }
	 else 
     if (!co.matches("T\\d(.*)"))
	  {
		 if (coco.ready())
				co=coco.readLine();
		 else 
			 return null;
			
	  }
		 
	 //File Cocoa=new File("./data sets/BIONLP/BioNLP-ST-2013_Bacteria_Biotopes_cocoa/");
	 int flag=0;
	 do{ 
		 if (flag==1)
    	   {
			 if (coco.ready()) 
			  co=coco.readLine(); 
			 else 
		      return null;
    	   }
    	 int bei=co.indexOf(" ");
    	 int eni=co.indexOf(" ",bei+1);
    	 if (f.B==1383)
    	 System.out.println("f="+ f.B+"\t"+f.E);
    	   if (co==null)
    		   System.out.print("");   
    	 if (!isInteger(co.substring(bei+1,eni)))
    	    System.out.print("stop for check!");
    	 int be=Integer.parseInt(co.substring(bei+1,eni));
    	 bei=eni+1;
    	 eni=co.indexOf("\t",bei);
    	 int en = Integer.parseInt(co.substring(bei,eni));
    	 if (f.B>=be && f.E<=en )
    		 {
    		  int b=co.indexOf("\t")+1;
    	      int e=co.indexOf(" ");
    		  CocoaAnn= co.substring(b,e);
    	      return CocoaAnn;
    	     }
    	 if (be>f.E)
    		 return co;
    	 flag=1;
        } while (coco.ready());
	 return CocoaAnn;
	}
	
	public void fetch_t1a1_annotations(BufferedReader brt1a2) throws IOException{
		String textline=null;
		String lastline=null;
		//Vector <Vector<annotations.offsets>> discourse_offsets=new Vector<Vector<annotations.offsets>>();
		//Vector<String> ontoBiotope=new Vector<String>();
		int t=0; 
		int sen_id=0; 
		while (brt1a2.ready())
		{   
			int flag=1;
			textline=brt1a2.readLine();
			int tok_id=0;
			Vector <offsets> toks=new Vector<offsets>();
			textline=textline.replaceAll(";","\t;");
			
			if (textline.charAt(t)!='T')
				{
				 discourse_annotations.entities.elementAt(0).label.addElement(textline.substring(textline.indexOf("MBTO")));
				 break;
				}
			//// semicolon counter i.e. the number of long distance tokens that build a phrase
			int b=textline.indexOf("Habitat")+8;
			int d=textline.indexOf(" ",b);
			int Be=Integer.parseInt(textline.substring(b,d));
			sen_id=find_sen(sen_id,Be);
			discourse_annotations.add_new_Entity();;
			while (flag==1){
				
			d=textline.indexOf(" ",b);
		    int e=textline.indexOf("\t",d+1);
		    Be=Integer.parseInt(textline.substring(b,d));
		    int En=Integer.parseInt(textline.substring(d+1,e));// read the offsets from a2 and find the related offsets in the sentence
		    //if (Be<=allsentences.elementAt(sen_id).sentence_feat.lastElement().words.wordOffset.E)
		      //      sen_id++;
		    toks=Find_id(sen_id, tok_id,Be,En);// find the offsets by starting the search after the previous tok-id
		    tok_id=tok_id+toks.size();    // add the size of the read tokens to the tok-id for to be used in the next iteration
		    for (int toklist=0;toklist<toks.size();toklist++)
		    {	
		    	discourse_annotations.entities.lastElement().wordOffsets.addElement(toks.elementAt(toklist));}
		      
		      //discourse_ann.lastElement().wordOffsets.lastElement().E=Integer.parseInt(textline.substring(d+1,e));}
		   // Boff.addElement(textline.substring(b,d));
		    //Eoff.addElement(textline.substring(d+1,e));}
			if (textline.contains(";"))
			 { 
			   textline=textline.substring(textline.indexOf(";")+1);	
	           b=0;		 
			 } 
			else 
			 {
				 flag=0;
				}
				
			}//while flag
		}
		
		while (brt1a2.ready())
		 {
			lastline=textline; // this is to check multiple annotations later.
			textline=brt1a2.readLine();
			if (textline.contains("MBTO")){
				int i1=textline.indexOf("Annotation:T")+("Annotation:T").length();
				int i2=textline.indexOf(" ",i1);
				String entityname1=textline.substring(i1,i2);
				i1=lastline.indexOf("Annotation:T")+("Annotation:T").length();
				i2=lastline.indexOf(" ",i1);
				String entityname2=lastline.substring(i1,i2);
				if (!entityname1.equals(entityname2)) 
				   t++;
			
			   discourse_annotations.entities.elementAt(t).label.addElement(textline.substring(textline.indexOf("MBTO")));
			   }
		  else{
			   System.out.print("There is no node assignment in this line or the line is empty.");
			   }
		 }
		//int sen_num=0;
		//int word_num=0;
		}
	
	/**
	 * @param brt2a1
	 * @param brt2a2
	 * @throws java.io.IOException
	 */
	public void fetch_t2a1a2_annotationsAndCand(BufferedReader brt2a1, BufferedReader brt2a2) throws IOException {

		String textline=null;
		int t=0; 
		int sen_id=0; 
		String id="";
		int Entity_num=0;
		
		entityList.setSize(allsentences.size());
		if (brt2a1!=null)
		while (brt2a1.ready())
		 {   
			int flag=1;
			//offsets off=new offsets();
			Entity_num=Entity_num+1;
			String role="";
			textline=brt2a1.readLine();
			CountSemicolons(textline);
			int tok_id=0;
		    int roleIndex;
		    Integer[] toks={}; Integer[]entity={}; 
			textline=textline.replaceAll(";","\t;");
			
			int idIndex=textline.indexOf("\t");
			if (textline.charAt(t)=='T')
				id=textline.substring(t,idIndex);
			if (textline.charAt(idIndex+1)=='H')
			    {
				 role="Habitat";
			     habitatList.add("T"+Entity_num);
			     FEx_BIONLP_BB_PerSentence_train_test_MoreGlobalBMC.statis.Habitat_num++;
			     }
			if (textline.charAt(idIndex+1)=='B')
			   {
				role="Bacteria";
			    bacteriumList.add("T"+Entity_num);
			   FEx_BIONLP_BB_PerSentence_train_test_MoreGlobalBMC.statis.Bacteria_num++;
			   }
			if (textline.charAt(idIndex+1)=='G')
			   {
				role="Geographical";
				habitatList.add("T"+Entity_num);
				FEx_BIONLP_BB_PerSentence_train_test_MoreGlobalBMC.statis.Habitat_num++;
			   }
			//// semicolon counter i.e. the number of long distance tokens that build a phrase
			roleIndex=textline.indexOf(role)+role.length()+1;
			int d=textline.indexOf(" ",roleIndex);
			int Be=Integer.parseInt(textline.substring(roleIndex,d));
			sen_id=find_sen(sen_id,Be);
			//discourse_annotations.add_new();
			while (flag==1){
			d=textline.indexOf(" ",roleIndex);
		    int e=textline.indexOf("\t",d+1);
		    Be=Integer.parseInt(textline.substring(roleIndex,d));
		    int En=Integer.parseInt(textline.substring(d+1,e));// read the offsets from a2 and find the related offsets in the sentence
		    toks=Find_tokindex(sen_id, tok_id,Be,En);// find the offsets by starting the search after the previous tok-id
		    tok_id=tok_id+toks.length;    // add the size of the read tokens to the tok-id for to be used in the next iteration
		   
		//    off.set(Be, En);
		    for (int toklist=0;toklist<toks.length;toklist++)
		     {
		    	entity=Arrays.copyOf(entity,entity.length+1);
		    	entity[entity.length-1]=toks[toklist];
		     }
		    if (textline.contains(";"))
			 { 
			   textline=textline.substring(textline.indexOf(";")+1);	
	           roleIndex=0;		 
			 } 
			else 	
				flag=0;
		}//while flag
		//	e1.id=id;
		//	e1.role=role;
		//	//e1.add_features(allsentences.elementAt(sen_id),toks);
        //    //allsentences.elementAt(it).lm_candidates.add(e1);}
		//       if (role=="Bacteria")
		 //   		allsentences.elementAt(sen_id).tr_candidates.add(e1);
		 //      if (role=="Habitat" || role=="Geographical")
		 //   	    allsentences.elementAt(sen_id).lm_candidates.add(e1);
		       discourse_annotations.add_new_Entity();
		       discourse_annotations.entities.lastElement().wordIndexes=entity;
		       discourse_annotations.entities.lastElement().role=role;
		       discourse_annotations.entities.lastElement().sentence_id=sen_id;
		       discourse_annotations.entities.lastElement().id=id;
		      // discourse_annotations.entities.lastElement().wordOffsets.addElement(off);
		       //entity=Arrays.copyOf(entity,entity.length+1);
		       //entity[entity.length-1]=toks[toklist];
		       Integer[][] temp={};
		       if (entityList.elementAt(sen_id)!=null) 
		       {
		            temp=entityList.elementAt(sen_id);
		       }
		       
		       temp=Arrays.copyOf(temp, temp.length+1);
		       temp[temp.length-1]=new Integer[entity.length];
		       temp[temp.length-1]=entity;
		       entityList.set(sen_id,temp);
		
		       //      for (int l=0;l<entity.length;l++)
		   //    {
		    //	  entityList.elementAt(sen_id)[entityList.elementAt(sen_id).length-1]=Arrays.copyOf(entityList[sen_id],entityList[sen_id].length+1);
		      //    entityList[sen_id][entityList[sen_id].length-1]=entity[l];
		      // }                                                            
	 }
 // CountOvelaps();		
  Add_Negative_roles_chunks();		


//		
		/// produce sentence level entities using coref if possible otherwise ignore the relation
	if (brt2a2!=null)	   
	while (brt2a2.ready())
		 {  
			String rel=""; 
			String id1,id2;
						
			textline=brt2a2.readLine();
			int idIndex=textline.indexOf("\t");
			if (textline.charAt(t)=='R')
				id=textline.substring(t,idIndex);
			else 
				{
				  if (textline.charAt(t)=='*') // Here are Equiv relations for coreferences to be handeled later.
					  {
					    int[] A=readCoreferences(textline);
					    int prevRowCount = CorefMatrix.length;
					    int[][] withExtraRow = new int[prevRowCount + 1][];
					    System.arraycopy(CorefMatrix, 0, withExtraRow, 0, CorefMatrix.length);
					    withExtraRow[prevRowCount] =A;
					    CorefMatrix=withExtraRow;
					    // add the read coreferences to the individual entities, first remove the entity itself from the list 
					    // the coreferences are the index of the identifiers as in the coref matrix
					    for (int corlist=0;corlist<A.length;corlist++)
					    {
					    	int Eind=A[corlist]-1;// the index of the entity in the discourse is the id minus one
					    	discourse_annotations.entities.elementAt(Eind).coreferences=new int[A.length-1];
					    	int corlist3=0;
					    	for (int corlist2=0;corlist2<A.length;corlist2++)
					    	  if (A[corlist2]!=Eind+1){	
					        	discourse_annotations.entities.elementAt(Eind).coreferences[corlist3]=A[corlist2];
					        	corlist3++;}
					    }
				        //  CorefMatrix[sen_id][entityList[sen_id].length-1]=entity[l];
				        continue; 
					  }
				      
				  else break;
					  
				} 
			if (textline.charAt(idIndex+1)=='L')
			    {
				rel="Localization"; 
				FEx_BIONLP_BB_PerSentence_train_test_MoreGlobalBMC.statis.Loc_annotated++;
			    }
			if (textline.charAt(idIndex+1)=='P')
			    {
				rel="PartOf";
				FEx_BIONLP_BB_PerSentence_train_test_MoreGlobalBMC.statis.PartOf_annotated++;
			    }
			int ind=textline.indexOf(":");
			id1=textline.substring(ind+1,textline.indexOf(" ",ind));
			ind=textline.indexOf(":",ind+1);
			id2=textline.substring(ind+1);
			discourse_annotations.add_new_relation();
			discourse_annotations.relations.lastElement().id1=id1;
			discourse_annotations.relations.lastElement().id2=id2;
			discourse_annotations.relations.lastElement().relationship=rel;
	   }
//	Add_Coref_Loc();
	Add_B_H_candidateNegRel();
	Add_B_H_Sp_candidates();
//	AddNegative_Relations();
	}
private void CountOvelaps() {
	for (int i=0; i< entityList.size();i++){
		if (entityList.elementAt(i)!=null)
		 for (int j=0;j<entityList.elementAt(i).length;j++){
			for (int k=j+1;k<entityList.elementAt(i).length;k++)
			{
					if (Util.overlap_tok_index(entityList.elementAt(i)[j],entityList.elementAt(i)[k]))
							 FEx_BIONLP_BB_PerSentence_train_test_MoreGlobalBMC.statis.nestedMentions++;		
				
			}
		}
	}
		
	}
private void CountSemicolons(String textline) {
		for (int i=0;i<textline.length();i++)
			if (textline.charAt(i)==';')
			   FEx_BIONLP_BB_PerSentence_train_test_MoreGlobalBMC.statis.discontinuousMentions++;
		}
/////////////////////////	
public void Add_H_H_candidate_NegRel(){
	for (int Hindex1=0;Hindex1<habitatList.size();Hindex1++)
	 for(int Hindex2=0; Hindex2<habitatList.size();Hindex2++)
	  {    
		if(Hindex1!=Hindex2){
			int h1=find_entity(habitatList.get(Hindex1));
			int h2=find_entity(habitatList.get(Hindex2));
			if (discourse_annotations.entities.elementAt(h1).sentence_id==discourse_annotations.entities.elementAt(h2).sentence_id)
				{
				if (no_relation(habitatList.get(Hindex1),habitatList.get(Hindex2)))
			
					{
					    FEx_BIONLP_BB_PerSentence_train_test_MoreGlobalBMC.statis.partOf_neg++;
					    discourse_annotations.add_new_relation();
						discourse_annotations.relations.lastElement().id1=habitatList.get(Hindex1);
					    discourse_annotations.relations.lastElement().id2=habitatList.get(Hindex2);
					    discourse_annotations.relations.lastElement().relationship="";
					}// if there is no relation between them
				}//if in the same sentence
			else 
				if (discourse_annotations.entities.elementAt(h1).sentence_id<discourse_annotations.entities.elementAt(h2).sentence_id) break; //h2 is in a senentece after h1, so stop searching
			} // if two different habitats
		}// for Hindex2
}// end of fetch annotations



////////////////////	

public void Add_B_H_Sp_candidates(){
	
	for (int Bindex=0;Bindex<bacteriumList.size();Bindex++)
	{ 
		String bstr=bacteriumList.get(Bindex);
		for(int Spindex=0; Spindex<SpList.size();Spindex++)
		{
			
			//String Spstr=SpList.get(Spindex);
			
			//int b=Integer.parseInt(bstr.substring(bstr.indexOf("T")+1));
			//int Sp=SpList.get(Spindex);//Integer.parseInt(hstr.substring(hstr.indexOf("T")+1));
			int s=find_entity("Sp"+(Spindex+1));
			int b=find_entity(bstr);
			if (discourse_annotations.entities.elementAt(b).sentence_id<discourse_annotations.entities.elementAt(s).sentence_id)
				break;
			if (discourse_annotations.entities.elementAt(b).sentence_id==discourse_annotations.entities.elementAt(s).sentence_id)
			 if(discourse_annotations.entities.elementAt(b).wordIndexes[0]<discourse_annotations.entities.elementAt(s).wordIndexes[0])
			    {
					//FEx_BIONLP_BB_PerSentence.statis.Loc_neg++;
					discourse_annotations.add_new_relation();
					discourse_annotations.relations.lastElement().id1=bstr;//bacteriumList.get(Bindex);
				    discourse_annotations.relations.lastElement().id2="Sp"+(Spindex+1);//habitatList.get(Hindex);
				    discourse_annotations.relations.lastElement().relationship="Tr_Sp";
			      }
				}
		}
	
	for (int Hindex=0;Hindex<habitatList.size();Hindex++)
	{ 
		String hstr=habitatList.get(Hindex);
		for(int Spindex=0; Spindex<SpList.size();Spindex++)
		{
			
			//String Spstr=SpList.get(Spindex);
			
			//int b=Integer.parseInt(bstr.substring(bstr.indexOf("T")+1));
			//int Sp=SpList.get(Spindex);//Integer.parseInt(hstr.substring(hstr.indexOf("T")+1));
			int s=find_entity("Sp"+(Spindex+1));
			int h=find_entity(hstr);
			if (discourse_annotations.entities.elementAt(h).sentence_id<discourse_annotations.entities.elementAt(s).sentence_id)
				break;
			if (discourse_annotations.entities.elementAt(h).sentence_id==discourse_annotations.entities.elementAt(s).sentence_id)
			 if(discourse_annotations.entities.elementAt(h).wordIndexes[0]>discourse_annotations.entities.elementAt(s).wordIndexes[0])
			    {
					//FEx_BIONLP_BB_PerSentence.statis.Loc_neg++;
					discourse_annotations.add_new_relation();
					discourse_annotations.relations.lastElement().id1=hstr;//bacteriumList.get(Bindex);
				    discourse_annotations.relations.lastElement().id2="Sp"+(Spindex+1);//habitatList.get(Hindex);
				    discourse_annotations.relations.lastElement().relationship="Lm_Sp";
			      }
				}
		}
}	
////////////////	
	void Add_Negative_roles_chunks(){
		int Sp_index=0;
		int RoleCand=0;
	for  (int i=0;i<allsentences.size();i++)
	{
	  	
	  for (int j=0;j<allsentences.elementAt(i).sen_chuncks.size();j++)
	  {   
		//  if (!Util.contains_int(entityList[i],allsentences.elementAt(i).chunks[j])){
		//  if (!Util.contains_int(entityList[i],j) && entityList[i].length!=0) 
			if (!Util.d2Int_d2_Int(entityList.elementAt(i),allsentences.elementAt(i).sen_chuncks.elementAt(j).wordIndexes))
		     {
			  Integer[] temp=allsentences.elementAt(i).sen_chuncks.elementAt(j).wordIndexes;//{new Integer(j)};
			  ling_features.word_features ws=allsentences.elementAt(i).sentence_feat.elementAt(FEx_BIONLP_BB_PerSentence_train_test_MoreGlobalBMC.HeadW_with_index(temp, allsentences.elementAt(i).sentence_feat)).words;//HeadW_with_index(temp, allsentences.elementAt(i).sentence_feat));
			  if (ws.pos.contains("IN")| ws.pos.contains("VB")){
				   //  if (allsentences.elementAt(i).parseTree.getLeafNodes().elementAt(temp[0]).hasVPAncestor()){
				   //if (inVerbPhrase(temp,allsentences.elementAt(i).parseTree)){
				   //     String w=allsentences.elementAt(i).sentence_feat.elementAt(temp[0]).words.word;
					   //if (!FEx_BIONLP_BB.find_(FEx_BIONLP_BB.StopWords,w) && w.length()>1) //exclude puntuation
	                   FEx_BIONLP_BB_PerSentence_train_test_MoreGlobalBMC.statis.Sp_candidate++;  
					   discourse_annotations.add_new_Entity();
					   discourse_annotations.entities.lastElement().wordIndexes=temp;
					   discourse_annotations.entities.lastElement().role="Sp_candidate";
					   discourse_annotations.entities.lastElement().sentence_id=i;
					   discourse_annotations.entities.lastElement().id="Sp"+(Sp_index+1);
					   Sp_index++;
					   SpList.add(temp); //** This list seems to be redundant
					 //  }
				      }
			  else
			  
			  if (!inVerbPhrase(temp,allsentences.elementAt(i).parseTree)){
				  String w=allsentences.elementAt(i).sentence_feat.elementAt(temp[0]).words.word;
				  if (!FEx_BIONLP_BB.find_(FEx_BIONLP_BB_PerSentence_train_test_MoreGlobalBMC.StopWords,w) && w.length()>1) //exclude puntuation
	               { 
				    FEx_BIONLP_BB_PerSentence_train_test_MoreGlobalBMC.statis.Entity_neg_num++;  
				   discourse_annotations.add_new_Entity();
				   discourse_annotations.entities.lastElement().wordIndexes=temp;
				   discourse_annotations.entities.lastElement().role="";
				   discourse_annotations.entities.lastElement().sentence_id=i;
				   discourse_annotations.entities.lastElement().id="R"+(RoleCand+1);
				   RoleCand++;
				   }
			      }
			  //discourse_annotations.entities.lastElement().id=id;
		  }
	   }
	}
	}

	
	////////////////
	
	public void Add_B_H_candidateNegRel(){

	for (int Bindex=0;Bindex<bacteriumList.size();Bindex++)
	{ 
		String bstr=bacteriumList.get(Bindex);
		for(int Hindex=0; Hindex<habitatList.size();Hindex++)
		{
			
			String hstr=habitatList.get(Hindex);
			int b=Integer.parseInt(bstr.substring(bstr.indexOf("T")+1));
			int h=Integer.parseInt(hstr.substring(hstr.indexOf("T")+1));
			
			if (no_relation(bacteriumList.get(Bindex),habitatList.get(Hindex))){
				if( b<h)
			    {
					FEx_BIONLP_BB_PerSentence_train_test_MoreGlobalBMC.statis.Loc_neg++;
					discourse_annotations.add_new_relation();
					discourse_annotations.relations.lastElement().id1=bstr;//bacteriumList.get(Bindex);
				    discourse_annotations.relations.lastElement().id2=hstr;//habitatList.get(Hindex);
				    discourse_annotations.relations.lastElement().relationship="";
			      }
				}
				else
					if (h<b)
						FEx_BIONLP_BB_PerSentence_train_test_MoreGlobalBMC.statis.HB++;
			}
		
	}
	}
////////////////////////////
	
 void Add_Negative_roles_words()
	{
		int Sp_index=0;
		int RoleCand=0;
		for  (int i=0;i<allsentences.size();i++)
		{
			for (Integer j=0;j<allsentences.elementAt(i).sentence_feat.size();j++)
			{   
				Integer[] temp={new Integer(j)};
				if (!Util.d2Int_d2_Int(entityList.elementAt(i),temp) && entityList.elementAt(i).length!=0) 
				{  
					ling_features.word_features ws=allsentences.elementAt(i).sentence_feat.elementAt(j).words;
					if (ws.pos.contains("IN")| ws.pos.contains("VB")){
						//  if (allsentences.elementAt(i).parseTree.getLeafNodes().elementAt(temp[0]).hasVPAncestor()){
						//if (inVerbPhrase(temp,allsentences.elementAt(i).parseTree)){
						//     String w=allsentences.elementAt(i).sentence_feat.elementAt(temp[0]).words.word;
						//if (!FEx_BIONLP_BB.find_(FEx_BIONLP_BB.StopWords,w) && w.length()>1) //exclude puntuation
						//				  { 
						FEx_BIONLP_BB_PerSentence_train_test_MoreGlobalBMC.statis.Sp_candidate++;  
						discourse_annotations.add_new_Entity();
						discourse_annotations.entities.lastElement().wordIndexes=temp;
						discourse_annotations.entities.lastElement().role="Sp_candidate";
						discourse_annotations.entities.lastElement().sentence_id=i;
						discourse_annotations.entities.lastElement().id="Sp"+(Sp_index+1);
						Sp_index++;
						SpList.add(temp); //** This list seems to be redundant
						//  }
					}
					else

						if (!inVerbPhrase(temp,allsentences.elementAt(i).parseTree)){
							String w=allsentences.elementAt(i).sentence_feat.elementAt(temp[0]).words.word;
							if (!FEx_BIONLP_BB.find_(FEx_BIONLP_BB_PerSentence_train_test_MoreGlobalBMC.StopWords,w) && w.length()>1) //exclude puntuation
							{ 
								FEx_BIONLP_BB_PerSentence_train_test_MoreGlobalBMC.statis.Entity_neg_num++;  
								discourse_annotations.add_new_Entity();
								discourse_annotations.entities.lastElement().wordIndexes=temp;
								discourse_annotations.entities.lastElement().role="";
								discourse_annotations.entities.lastElement().sentence_id=i;
								discourse_annotations.entities.lastElement().id="R"+(RoleCand+1);
								RoleCand++;
							}
						}
					//discourse_annotations.entities.lastElement().id=id;
				}
			}
		}
	}
//////////////////////////////////////////////
	
	void Add_Coref_Loc(){
	for (int cor1=0;cor1<CorefMatrix.length;cor1++)
	 {   
		
		String[] habitats;
	
		for (int cor2=0;cor2<CorefMatrix[cor1].length;cor2++)
		  {
			habitats=discourse_annotations.allhabitatsOf(("T"+CorefMatrix[cor1][cor2]));
		    for (int h=0;h<habitats.length;h++){
			   for (int corelist=0; corelist<CorefMatrix[cor1].length;corelist++)
			     if (corelist!= cor2)	   
		        	if (no_relation(("T"+CorefMatrix[cor1][corelist]),habitats[h])){
			         {
			        	 FEx_BIONLP_BB_PerSentence_train_test_MoreGlobalBMC.statis.Loc_cand++;
						discourse_annotations.add_new_relation();
						discourse_annotations.relations.lastElement().id1="T"+CorefMatrix[cor1][corelist];//bacteriumList.get(Bindex);
					    discourse_annotations.relations.lastElement().id2=habitats[h];//habitatList.get(Hindex);
					    discourse_annotations.relations.lastElement().relationship="LocalizationCoref";
		             }	
		      }
		  }
	 }
	} //end for one line of coreferences
	}

	 int[] readCoreferences(String textline){
		Pattern p1=Pattern.compile(" ");
		int[] CorefChain={};
		String[] Entities=p1.split(textline);
		for (int i=1;i<Entities.length;i++)
		 	
		{
			int  id1=Integer.parseInt(Entities[i].substring(Entities[i].indexOf("T")+1));
			 CorefChain=Arrays.copyOf(CorefChain, CorefChain.length+1);
			 CorefChain[CorefChain.length-1]=id1;
		} 
	    Arrays.sort(CorefChain);		 
		return CorefChain;      
		
  }
	boolean no_relation(String B_id, String H_id)
	{   
		boolean flag=true;
		for (int r=0;r< discourse_annotations.relations.size();r++){
			if (discourse_annotations.relations.elementAt(r).id1.equals(B_id) && discourse_annotations.relations.elementAt(r).id2.equals(H_id) )
				{
				flag=false;
			    return flag;
			    }
		}
		return flag;
	}
	
	
	
	
	public void assign_lth_f(Vector<Vector<String>> lth_m, BufferedReader sspliter, BufferedReader cocoa) throws IOException{
		BIOsentence BIOsentence1=new BIOsentence();
		int Boff=0; int Eoff=-1;
		int discoursechar=-1;
		String textline="";
		while (textline.equals("")){
		 textline=sspliter.readLine();
		 discoursechar++; 
		 }
		BIOsentence1.content=textline;
		String lastCocoa=null;
		for (int i=0; i<lth_m.size();i++){
			if (lth_m.elementAt(i).elementAt(0).equals("1") && i!=0)
			   {
				allsentences.addElement(new BIOsentence(BIOsentence1));
			    BIOsentence1=new BIOsentence(); 
			    discoursechar=discoursechar+textline.length();
			    do{
			    discoursechar++; 	
			    textline=sspliter.readLine();
			    }
			    while(textline.length()==0);
			    BIOsentence1.content=textline;
			    Boff=0;
			    }
			
			ling_features f=new ling_features();
			f.words.word=lth_m.elementAt(i).elementAt(1);
			Boff=textline.indexOf(f.words.word,Boff);
			Eoff=Boff+f.words.word.length();
			//f.words.wordOffset=new offsets();
			f.words.wordOffset.set(discoursechar+Boff,Eoff+discoursechar);
			//f.words.wordOffset.B=discoursechar+Boff;
			//f.words.wordOffset.E=Eoff+discoursechar;
			Boff=Eoff;
			
			lastCocoa=readCocoa(f.words.wordOffset,cocoa,lastCocoa);
			if (lastCocoa!=null)
				 if (!lastCocoa.startsWith("T"))
			       f.words.cocoa=lastCocoa;
			// 0:ID 1:FORM 2:LEMMA 3:CPOSTAG 4:POSTAG 5:FEATS 6:HEAD 7:DEPRL 8:PHEAD 9:PDEPREL 10:BIOLEMMA
			//lastCocoa=f.words.cocoa;
		//	f.words.lemma=lth_m.elementAt(i).elementAt(2);
			f.words.pos=lth_m.elementAt(i).elementAt(3);
			if (f.words.pos.equals('_'))
			  f.words.pos=lth_m.elementAt(i).elementAt(4);
			
			f.words.head=lth_m.elementAt(i).elementAt(6);
			if (f.words.head.equals('_'))
				f.words.head=lth_m.elementAt(i).elementAt(8);
			f.words.DPRL=lth_m.elementAt(i).elementAt(7);
			//f.words.SRL=build_RelDB.SRL(i,lth_m);
			if (f.words.DPRL.equals('_'))
			 f.words.DPRL=lth_m.elementAt(i).elementAt(9);
			f.words.lemma=lth_m.elementAt(i).elementAt(10);
			
			BIOsentence1.sentence_feat.addElement(f);
			
			if (i==lth_m.size()-1)
			{allsentences.addElement(BIOsentence1);}
			//lastElement().sentence_feat.addElement(f);
			//allsentences.addElement(BIOsentence1); 		
		}
		
	}
	public void rewrite_lth(BufferedReader r, PrintWriter w, BioLemmatizer bioLemmatizer) throws IOException{
        while (r.ready()){
                String l=r.readLine();
                Pattern s=Pattern.compile("\t");
                String[] toks=s.split(l);
                if (toks.length<10){
                        w.println();
                        continue;
                }
                String spelling =toks[1];// "radiolabeled";
                //String expectedLemma = "radiolabel";
                String partOfSpeech = toks[3];//"VBZ";
                if (partOfSpeech.equals("_"))
                {
                        partOfSpeech=toks[4];
                }
        //Set<String> expectedLemmas = new HashSet<String>();
        //expectedLemmas.add(expectedLemma);

                LemmataEntry lemmata = bioLemmatizer.lemmatizeByLexiconAndRules(spelling, partOfSpeech);
                        Set<String> results=new HashSet<String>(lemmata.lemmasAndCategories.values());
                        for (String str:results){
                                System.out.println(str);
                        }

                l=l.concat("\t"+results.toArray()[0]);
                w.println(l);
        }

}
	public void rewrite_a2_task2(BufferedReader r, PrintWriter w) throws IOException{
        // Here remove the partof annotations from a2 files
		while (r.ready()){
                String l=r.readLine();
                if (!l.contains("PartOf"))
                w.println(l);
        }

}


	public void assign_trees_f(Vector<TreeNode> char_trees, Vector<Vector<String>> lth_matrix){
	  int lth_begin=0;
	  int lth_end=0;
      for(int i=0;i<char_trees.size();i++){
    	TreeNode char_tree=char_trees.elementAt(i);
    	allsentences.elementAt(i).parseTree=char_trees.elementAt(i);
    	if (allsentences.elementAt(i).sentence_feat.lastElement().words.word.equals(".")||allsentences.elementAt(i).sentence_feat.lastElement().words.word.equals("]"))
    		allsentences.elementAt(i).title=false;
    	else 
    		allsentences.elementAt(i).title=true;
    	
		Vector<TreeNode> Nodes_= char_tree.getAllNodesOfTree(); 
		Vector <TreeNode> Sentence=char_tree.getLeafNodes();
		//BIOsentence BIOsentence1=new BIOsentence();
		int wordNum=Sentence.size();	
		int nodNum = char_tree.getNumberOfNodesInTree();
		int[] Tree_indexes=new int[wordNum];
		lth_begin= lth_end;
	    lth_end= lth_begin+wordNum;
	    Vector<Vector<String>> lth_submatrix=new Vector<Vector<String>>();
	    for (int it=lth_begin;it<lth_end;it++)
	    {lth_submatrix.addElement(lth_matrix.elementAt(it));}
	    
		Tree_indexes=build_RelDB.find_tree_indexes(Nodes_, lth_submatrix);
		for (int w=0;w<wordNum;w++){
			String wordsubcat="";
			TreeNode tem=Nodes_.elementAt(Tree_indexes[w]).getParent();
			if(tem!=null){
				TreeNode[] childeren1 =tem.getChildren();
				for(int i1=0; i1<childeren1.length;i1++)
					wordsubcat= wordsubcat+childeren1[i1].getLabel()+"_"; 
			 }// end if parent is not null
			allsentences.elementAt(i).sentence_feat.elementAt(w).words.subcategorization=wordsubcat;
			for (int w_2=0;w_2<wordNum;w_2++){
			allsentences.elementAt(i).sentence_feat.elementAt(w).word_word.path.addElement(Nodes_.elementAt(Tree_indexes[w]).getPath(Nodes_.elementAt(Tree_indexes[w_2]))); 
				int distance=TreeNode.getPathDistance(Nodes_.elementAt(Tree_indexes[w_2]), Nodes_.elementAt(Tree_indexes[w]));
				allsentences.elementAt(i).sentence_feat.elementAt(w).word_word.distance.addElement(distance);
				if (distance!=0)
					distance= (nodNum)/distance;
				allsentences.elementAt(i).sentence_feat.elementAt(w).word_word.deppath.addElement(allsentences.elementAt(i).depPath(w,w_2));
			}
		}//end for every word find subcategoriazation
      }// end for all trees
	}
	
	public boolean inVerbPhrase(Integer [] entity,TreeNode top){ 
//		top.getNodeWithSpan(entity[0], entity[entity.length-1]+1); 
		//top.getLeafNodes().elementAt(entity[0]);
		//top.getLeafNodes().elementAt(entity[entity.length-1]);
		String S=top.getCommonParent(top.getLeafNodes().elementAt(entity[0]), top.getLeafNodes().elementAt(entity[entity.length-1])).getLabel();
		if (S.charAt(0)=='V')  
			return true;
		else
			return false;
		}
	
		///////////////////////////
	public void fetch_ling_features(BufferedReader lth_file, BufferedReader charniak_file, BufferedReader sspliter, BufferedReader cocoa) throws IOException{
		// System.out.println(content);content=content.trim();
	
		Vector <Vector<String>> lth_matrix=fetch_lth_matrix(lth_file);
		Vector <TreeNode> char_tree=fetch_chars(charniak_file);
		assign_lth_f(lth_matrix,sspliter,cocoa);
		assign_trees_f(char_tree,lth_matrix);
		//readContent(sspliter);
		//fetch_spatial_pivots();
		lth_matrix=null;
		char_tree=null;
		System.gc();
		//check_sourcefiles(lth_file, charniak_file);
	}
	//}

	
	
	public void collect_RelCandidatesForSOP(Vector <Vector<String>> arff){
	    for (int it=0;it<allsentences.size();it++)
	    {
	    	
		  for (int w=0;w<allsentences.elementAt(it).sentence_feat.size();w++){
			
		  	 //int lm_index=allPosRelCandids.elementAt(r).lm,tr_index=allPosRelCandids.elementAt(r).tr,sp_index=allPosRelCandids.elementAt(r).sp;
			  //if (relations.elementAt(r).wh_tr.head_index!=-1)
			  //**********************trajector features  
		//	 if (sp_candidates.contains(w) || lm_candidates.contains(w)) {
			    /* arff is to collect the features in a structured way, arff-0 is to have the possible word forms
			     * arff-1 is to collect the possible pos tags, arff-2 is to collect subcategorization, arff-3 is to 
			     * collect dprl-features and arff-4 is for srl-features
			     */
				  
			      	//****************spatial indicator features
				    if (!arff.elementAt(0).contains(allsentences.elementAt(it).constant_correction(allsentences.elementAt(it).sentence_feat.elementAt(w).words.word)))
						   arff.elementAt(0).addElement(allsentences.elementAt(it).constant_correction(allsentences.elementAt(it).sentence_feat.elementAt(w).words.word));
				 	
				    if (!arff.elementAt(1).contains(allsentences.elementAt(it).constant_correction(allsentences.elementAt(it).sentence_feat.elementAt(w).words.pos)))
						   arff.elementAt(1).addElement(allsentences.elementAt(it).constant_correction(allsentences.elementAt(it).sentence_feat.elementAt(w).words.pos));
					 
					  if (!arff.elementAt(2).contains(allsentences.elementAt(it).constant_correction(allsentences.elementAt(it).sentence_feat.elementAt(w).words.subcategorization)))
							   arff.elementAt(2).addElement(allsentences.elementAt(it).constant_correction(allsentences.elementAt(it).sentence_feat.elementAt(w).words.subcategorization));
						
					 
					  if (!arff.elementAt(3).contains(allsentences.elementAt(it).constant_correction(allsentences.elementAt(it).sentence_feat.elementAt(w).words.DPRL)))
						   arff.elementAt(3).addElement(allsentences.elementAt(it).constant_correction(allsentences.elementAt(it).sentence_feat.elementAt(w).words.DPRL));

					  if (!arff.elementAt(4).contains(allsentences.elementAt(it).constant_correction(allsentences.elementAt(it).sentence_feat.elementAt(w).words.SRL)))
						   arff.elementAt(4).addElement(allsentences.elementAt(it).constant_correction(allsentences.elementAt(it).sentence_feat.elementAt(w).words.SRL));
					  
				  /////////*****************************************************//////////////////////////////
//					  
//					  if (!arff.contains("nsp_h_"+constant_correction(sentence_feat.elementAt(w).words.word)))
//						   arff.addElement("nsp_h_"+constant_correction(sentence_feat.elementAt(w).words.word));
//				 	
//					  if (!arff.contains("nsp_sub_"+constant_correction(sentence_feat.elementAt(w).words.subcategorization)))
//							   arff.addElement("nsp_sub_"+constant_correction(sentence_feat.elementAt(w).words.subcategorization));
//						
//					  if (!arff.contains("nsp_pos_"+constant_correction(sentence_feat.elementAt(w).words.pos)))
//						   arff.addElement("nsp_pos_"+constant_correction(sentence_feat.elementAt(w).words.pos));
//					  
//					  if (!arff.contains("nsp_dp_"+constant_correction(sentence_feat.elementAt(w).words.DPRL)))
//						   arff.addElement("nsp_dp_"+constant_correction(sentence_feat.elementAt(w).words.DPRL));
//
//					  if (!arff.contains("nsp_SRL_"+constant_correction(sentence_feat.elementAt(w).words.SRL)))
//						   arff.addElement("nsp_SRL_"+constant_correction(sentence_feat.elementAt(w).words.SRL));}
//				
//		///////////////////////////////////////////////////	    	
//			    	
//			  if (lm_candidates.contains(w))
//				 {
//					 for(int sp_pair=0; sp_pair<sp_candidates.size();sp_pair++){
//						 int sp_index=sp_candidates.elementAt(sp_pair);
//					 
//			 ////********** Features of  roles		
//				     if (w==sentence_feat.size()){
//						 featList.addElement(atts.indexOf("tr_h_undefined_"+constant_correction(sentence_feat.elementAt(sp_index).words.word)));
//						 featList.addElement(atts.indexOf("lm_h_undefined_"+constant_correction(sentence_feat.elementAt(sp_index).words.word)));
//						 featList.addElement(atts.indexOf("none_h_undefined"+constant_correction(sentence_feat.elementAt(sp_index).words.word)));
//					    }
//					 else{
//						 featList.addElement(atts.indexOf("tr_h_"+constant_correction(sentence_feat.elementAt(w).words.word)+constant_correction(sentence_feat.elementAt(sp_index).words.word)));
//					     featList.addElement(atts.indexOf("tr_dp_"+constant_correction(sentence_feat.elementAt(w).words.DPRL)+constant_correction(sentence_feat.elementAt(sp_index).words.word)));
//					     featList.addElement(atts.indexOf("tr_pos_"+constant_correction(sentence_feat.elementAt(w).words.pos)+constant_correction(sentence_feat.elementAt(sp_index).words.word)));
//					     featList.addElement(atts.indexOf("tr_SRL_"+constant_correction(sentence_feat.elementAt(w).words.SRL)+constant_correction(sentence_feat.elementAt(sp_index).words.word)));
//					     featList.addElement(atts.indexOf("tr_sub_"+constant_correction(sentence_feat.elementAt(w).words.subcategorization)+constant_correction(sentence_feat.elementAt(sp_index).words.word)));
//					     featList.addElement(atts.indexOf("tr_distanc_"+constant_correction(sentence_feat.elementAt(w).words.subcategorization)+constant_correction(sentence_feat.elementAt(sp_index).words.word)));
//					     featList.addElement(atts.indexOf("tr_path_"+constant_correction(sentence_feat.elementAt(w).words.subcategorization)+constant_correction(sentence_feat.elementAt(sp_index).words.word)));
//						 	
//					     
//						 featList.addElement(atts.indexOf("lm_h_"+constant_correction(sentence_feat.elementAt(w).words.word)+constant_correction(sentence_feat.elementAt(sp_index).words.word)));
//					     featList.addElement(atts.indexOf("lm_dp_"+constant_correction(sentence_feat.elementAt(w).words.DPRL)+constant_correction(sentence_feat.elementAt(sp_index).words.word)));
//					     featList.addElement(atts.indexOf("lm_pos_"+constant_correction(sentence_feat.elementAt(w).words.pos)+constant_correction(sentence_feat.elementAt(sp_index).words.word)));
//					     featList.addElement(atts.indexOf("lm_SRL_"+constant_correction(sentence_feat.elementAt(w).words.SRL)+constant_correction(sentence_feat.elementAt(sp_index).words.word)));
//					     featList.addElement(atts.indexOf("lm_sub_"+constant_correction(sentence_feat.elementAt(w).words.subcategorization)+constant_correction(sentence_feat.elementAt(sp_index).words.word)));
//					
//					 
//						 featList.addElement(atts.indexOf("non_h_"+constant_correction(sentence_feat.elementAt(w).words.word)+constant_correction(sentence_feat.elementAt(sp_index).words.word)));
//					     featList.addElement(atts.indexOf("non_dp_"+constant_correction(sentence_feat.elementAt(w).words.DPRL)+constant_correction(sentence_feat.elementAt(sp_index).words.word)));
//					     featList.addElement(atts.indexOf("non_pos_"+constant_correction(sentence_feat.elementAt(w).words.pos)+constant_correction(sentence_feat.elementAt(sp_index).words.word)));
//					     featList.addElement(atts.indexOf("non_SRL_"+constant_correction(sentence_feat.elementAt(w).words.SRL)+constant_correction(sentence_feat.elementAt(sp_index).words.word)));
//					     featList.addElement(atts.indexOf("non_sub_"+constant_correction(sentence_feat.elementAt(w).words.subcategorization)+constant_correction(sentence_feat.elementAt(sp_index).words.word)));
//					     
//			  //////////////khar khari az inja 
//			  
//			    	if (!arff.contains("tr_h_undefined"))
//						   arff.addElement("tr_h_undefined");
//			    	
//			    }
//			    else {
//			    	
//					if (!arff.contains("tr_h_"+constant_correction(sentence_feat.elementAt(tr_index).words.word)))
//						   arff.addElement("tr_h_"+constant_correction(sentence_feat.elementAt(tr_index).words.word));
//					
//					if (!arff.contains("tr_dp_"+constant_correction(sentence_feat.elementAt(tr_index).words.DPRL)))
//						   arff.addElement("tr_dp_"+constant_correction(sentence_feat.elementAt(tr_index).words.DPRL));
//					
//					if (!arff.contains("tr_pos_"+constant_correction(sentence_feat.elementAt(tr_index).words.pos)))
//						   arff.addElement("tr_pos_"+constant_correction(sentence_feat.elementAt(tr_index).words.pos));
//					
//					if (!arff.contains("tr_SRL_"+constant_correction(sentence_feat.elementAt(tr_index).words.SRL)))
//						   arff.addElement("tr_SRL_"+constant_correction(sentence_feat.elementAt(tr_index).words.SRL));
//					
//					
//					if (!arff.contains("tr_sub_"+constant_correction(sentence_feat.elementAt(tr_index).words.subcategorization)))
//						   arff.addElement("tr_sub_"+constant_correction(sentence_feat.elementAt(tr_index).words.subcategorization));
//			    }	
//			 	//****************landmark features	
//			    if (lm_index==sentence_feat.size()){
//			    	if (!arff.contains("lm_h_undefined"))
//						   arff.addElement("lm_h_undefined");
//			    }
//			    else{
//					if (!arff.contains("lm_h_"+constant_correction(sentence_feat.elementAt(lm_index).words.word)))
//						   arff.addElement("lm_h_"+constant_correction(sentence_feat.elementAt(lm_index).words.word));
//					
//					if (!arff.contains("lm_dp_"+constant_correction(sentence_feat.elementAt(lm_index).words.DPRL)))
//						   arff.addElement("lm_dp_"+constant_correction(sentence_feat.elementAt(lm_index).words.DPRL));
//					
//					if (!arff.contains("lm_pos_"+constant_correction(sentence_feat.elementAt(lm_index).words.pos)))
//						   arff.addElement("lm_pos_"+constant_correction(sentence_feat.elementAt(lm_index).words.pos));
//					
//					if (!arff.contains("lm_SRL_"+constant_correction(sentence_feat.elementAt(lm_index).words.SRL)))
//						   arff.addElement("lm_SRL_"+constant_correction(sentence_feat.elementAt(lm_index).words.SRL));
//					
//					
//					if (!arff.contains("lm_sub_"+constant_correction(sentence_feat.elementAt(lm_index).words.subcategorization)))
//						   arff.addElement("lm_sub_"+constant_correction(sentence_feat.elementAt(lm_index).words.subcategorization));
//			    }	
//					//**************relational features 
//				  // adding distance features 
//				  int distt=-1;
//				  if(tr_index!=sentence_feat.size())
//				    distt= sentence_feat.elementAt(sp_index).word_word.distance.elementAt(tr_index);
//				  if(!arff.contains("d_sp_tr_"+distt))
//					 arff.addElement("d_sp_tr_"+distt);  
//				  int distl=-1;
//				  if(lm_index!=sentence_feat.size())
//					 distl= sentence_feat.elementAt(sp_index).word_word.distance.elementAt(lm_index);
//				  if(!arff.contains("d_sp_lm_"+distl))
//					  arff.addElement("d_sp_lm_"+distl);
//				  int disttl=-1;
//				  if ((distl!=-1 & distt!=-1))
//					  disttl=sentence_feat.elementAt(tr_index).word_word.distance.elementAt(lm_index);
//				  if(!arff.contains("d_tr_lm_"+disttl))
//					  arff.addElement("d_tr_lm_"+disttl);
//				  // adding path features
//
//				}// end of for relations 
//		  // for(int r=0;r<Math.min (nonrelations.size(),relations.size()+1);r++)
//			   for(int r=0;r<allnonrel.size();r++){
//						
//				
//				int r1=selectedNegatives.elementAt(r);  
//			    int lm_index=allnonrel.elementAt(r1).lm,tr_index=allnonrel.elementAt(r1).tr,sp_index=allnonrel.elementAt(r1).sp;
//			  //if (relations.elementAt(r).wh_tr.head_index!=-1)
//			
//			    //**********************trajector features  
//			    if (tr_index==sentence_feat.size()){
//			    	if (!arff.contains("tr_h_undefined"))
//						   arff.addElement("tr_h_undefined");
//			    }
//			    else{
//					if (!arff.contains("tr_h_"+constant_correction(sentence_feat.elementAt(tr_index).words.word)))
//						   arff.addElement("tr_h_"+constant_correction(sentence_feat.elementAt(tr_index).words.word));
//					
//					if (!arff.contains("tr_dp_"+constant_correction(sentence_feat.elementAt(tr_index).words.DPRL)))
//						   arff.addElement("tr_dp_"+constant_correction(sentence_feat.elementAt(tr_index).words.DPRL));
//					
//					if (!arff.contains("tr_pos_"+constant_correction(sentence_feat.elementAt(tr_index).words.pos)))
//						   arff.addElement("tr_pos_"+constant_correction(sentence_feat.elementAt(tr_index).words.pos));
//					
//					if (!arff.contains("tr_SRL_"+constant_correction(sentence_feat.elementAt(tr_index).words.SRL)))
//						   arff.addElement("tr_SRL_"+constant_correction(sentence_feat.elementAt(tr_index).words.SRL));
//					
//					
//					if (!arff.contains("tr_sub_"+constant_correction(sentence_feat.elementAt(tr_index).words.subcategorization)))
//						   arff.addElement("tr_sub_"+constant_correction(sentence_feat.elementAt(tr_index).words.subcategorization));
//			    }
//			 	//****************landmark features	
//			    if (lm_index==sentence_feat.size()){
//			    	if (!arff.contains("lm_h_undefined"))
//						   arff.addElement("lm_h_undefined");
//			    }
//			    else{
//					
//					if (!arff.contains("lm_h_"+constant_correction(sentence_feat.elementAt(lm_index).words.word)))
//						   arff.addElement("lm_h_"+constant_correction(sentence_feat.elementAt(lm_index).words.word));
//					
//					if (!arff.contains("lm_dp_"+constant_correction(sentence_feat.elementAt(lm_index).words.DPRL)))
//						   arff.addElement("lm_dp_"+constant_correction(sentence_feat.elementAt(lm_index).words.DPRL));
//					
//					if (!arff.contains("lm_pos_"+constant_correction(sentence_feat.elementAt(lm_index).words.pos)))
//						   arff.addElement("lm_pos_"+constant_correction(sentence_feat.elementAt(lm_index).words.pos));
//					
//					if (!arff.contains("lm_SRL_"+constant_correction(sentence_feat.elementAt(lm_index).words.SRL)))
//						   arff.addElement("lm_SRL_"+constant_correction(sentence_feat.elementAt(lm_index).words.SRL));
//					
//					
//					if (!arff.contains("lm_sub_"+constant_correction(sentence_feat.elementAt(lm_index).words.subcategorization)))
//						   arff.addElement("lm_sub_"+constant_correction(sentence_feat.elementAt(lm_index).words.subcategorization));
//			    }	
//				//****************spatial indicator features
//				  if (!arff.contains("sp_h_"+constant_correction(sentence_feat.elementAt(sp_index).words.word)))
//						   arff.addElement("sp_h_"+constant_correction(sentence_feat.elementAt(sp_index).words.word));
//				  if (!arff.contains("sp_sub_"+constant_correction(sentence_feat.elementAt(sp_index).words.subcategorization)))
//						   arff.addElement("sp_sub_"+constant_correction(sentence_feat.elementAt(sp_index).words.subcategorization));
//					
//				  if (!arff.contains("sp_pos_"+constant_correction(sentence_feat.elementAt(sp_index).words.pos)))
//					   arff.addElement("sp_pos_"+constant_correction(sentence_feat.elementAt(sp_index).words.pos));
//				  
//				  if (!arff.contains("sp_dp_"+constant_correction(sentence_feat.elementAt(sp_index).words.DPRL)))
//					   arff.addElement("sp_dp_"+constant_correction(sentence_feat.elementAt(sp_index).words.DPRL));
//
//				  if (!arff.contains("sp_SRL_"+constant_correction(sentence_feat.elementAt(sp_index).words.SRL)))
//					   arff.addElement("sp_SRL_"+constant_correction(sentence_feat.elementAt(sp_index).words.SRL));
//				//**************relational features 
//				  // adding distance features 
//				  int distt=-1;
//				  if(tr_index!=sentence_feat.size())
//				    distt= sentence_feat.elementAt(sp_index).word_word.distance.elementAt(tr_index);
//				  if(!arff.contains("d_sp_tr_"+distt))
//					 arff.addElement("d_sp_tr_"+distt);  
//				  int distl=-1;
//				  if(lm_index!=sentence_feat.size())
//					 distl= sentence_feat.elementAt(sp_index).word_word.distance.elementAt(lm_index);
//				  if(!arff.contains("d_sp_lm_"+distl))
//					  arff.addElement("d_sp_lm_"+distl);
//				  int disttl=-1;
//				  if ((distl!=-1 & distt!=-1))
//					  disttl=sentence_feat.elementAt(tr_index).word_word.distance.elementAt(lm_index);
//				  if(!arff.contains("d_tr_lm_"+disttl))
//					  arff.addElement("d_tr_lm_"+disttl);
//				  // adding path features
//	    }// end of for allnonrel 
	//	 }//if candidate	
	   }//for words in the sentence
		 
	  }//for all sentences in the discourse
	 }//end procedure to collect the relational features
	
	public void collect_featureLexicon(Vector<String> att){
	for (int i=0; i< allsentences.size();i++)
		{
			for (int c=0;c<allsentences.elementAt(i).lm_candidates.size();c++){
     	    iEntity c1=allsentences.elementAt(i).lm_candidates.get(c);
     	    for (int f=0;f<c1.features.size();f++)
     	       if (!att.contains(c1.features.get(f)))
			              att.addElement(c1.features.get(f));
	    	}
		}
	}
	
	public void build_all_word_candidatesLmOBO(){
		
//		OBORepresentation obRep=new OBORepresentation();
//		OBOParser objParser=new OBOParser("./data sets/BIONLP/OntoBiotope_BioNLP-ST13.obo");
//		try {
//			obRep= objParser.parseFile();
//			System.out.println("number of elements" + obRep.getNumberOfObo());
//			ArrayList<String>  objExactSyns=  obRep.getExactAllSynonyms();
//			ArrayList<String>  objRelatedSyns=  obRep.getRelatedAllSynonyms();
//			ArrayList<String>  objNames=  obRep.getAllNames();
//			
//			
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		
	
		for (int it=0;it<allsentences.size();it++){
			
			//for (int w=0; w<allsentences.elementAt(it).sentence_feat.size();w++){
		   Integer[] indices=FEx_BIONLP_BB_PerSentence_train_test_MoreGlobalBMC.obRep.getIndexesOfExistingWords(allsentences.elementAt(it).sentence_feat);
	           if (indices.length!=0)
	        	 for(int entitylist=0;entitylist<indices.length;entitylist++)  
	              {  
	        		 iEntity e1=new iEntity();
	                 Integer[] listindic={indices[entitylist]};
	        		 e1.add_features(allsentences.elementAt(it),listindic);
	                 allsentences.elementAt(it).lm_candidates.add(e1);}
		
		}
	}
	public void check_OBO(OBORepresentation obRep, NCBIRepresentation NCBIrep, PrintWriter matchInfo ){
		// This function counts the numbe of gth bacterium and gth habitats that are found in the 
		// OBO and NCBI ontologies. And the number of the ones that are not found, counting the repeated ones also. 
		for (int i=0; i<this.discourse_annotations.entities.size();i++)

		{
			phrase_annotations e=discourse_annotations.entities.elementAt(i);

			//String phrase="";
			String[] S1={};
			for (int w=0; w<e.wordIndexes.length;w++)
			{  
				S1=Arrays.copyOf(S1,S1.length+1);
				S1[S1.length-1]=PreProcessing.BacteriaToken(allsentences.elementAt(e.sentence_id).sentence_feat.elementAt(e.wordIndexes[w]).words.lemma);

			}
			S1=PreProcessing.BacteriafullName(S1);

			
				if (e.role.startsWith("H"))
					if (obRep.exists_Habitat_inOBO(S1))
						FEx_BIONLP_BB_PerSentence_train_test_MoreGlobalBMC.statis.EntityInObo++;
					else
						FEx_BIONLP_BB_PerSentence_train_test_MoreGlobalBMC.statis.EntityNotInObo++;

				////// If the role is Bacteria then do some preprocess and check for the exact match.

				/*if (e.role.startsWith("B"))

				{	
					if (NCBIrep.exists_Bactriun_NCBI(S1))

						FEx_BIONLP_BB_PerSentence_train_test.statis.EntityInNCBI++;
					else
					{
						FEx_BIONLP_BB_PerSentence_train_test.statis.EntityNotInNCBI++;
						for (int kk=0;kk<S1.length;kk++)
						matchInfo.print(S1[kk]+" ");
						matchInfo.println();
					}

				}*/
				if (e.role.isEmpty()){
					
					if (NCBIrep.exists_Bactriun_NCBI(S1))
						FEx_BIONLP_BB_PerSentence_train_test_MoreGlobalBMC.statis.EntityInNCBI_Fp++;
					if (obRep.exists_Habitat_inOBO(S1))
						FEx_BIONLP_BB_PerSentence_train_test_MoreGlobalBMC.statis.EntityInObo_Fp++;
					
				}
			
		}
    	}
	
/////////////////
public void build_all_chunk_candidatesLmNCBI_Obo(){
		// This function check all the entities built based on the sentence chunks and adds the ones who have an overlap with the ontology of bacterium as the candidates for trajectors.
        // and adds those which have overlap with obo to lanmark candidates.  
		for (int i=0; i<discourse_annotations.entities.size();i++){ 
		 phrase_annotations e=discourse_annotations.entities.elementAt(i);
            String[] S1={};
			for (int w=0; w<e.wordIndexes.length;w++)
			{  
				S1=Arrays.copyOf(S1,S1.length+1);
				S1[S1.length-1]=PreProcessing.BacteriaToken(allsentences.elementAt(e.sentence_id).sentence_feat.elementAt(e.wordIndexes[w]).words.lemma);

			}
			S1=PreProcessing.BacteriafullName(S1);

			
				if (e.role.isEmpty())
					if (FEx_BIONLP_BB_PerSentence_train_test_MoreGlobalBMC.obRep.exists_Habitat_inOBO(S1))
						 lm_candidates.add(e);
					else
						if (FEx_BIONLP_BB_PerSentence_train_test_MoreGlobalBMC.NCBIRep.exists_Bactriun_NCBI(S1))
							 tr_candidates.add(e);
						else 
							FEx_BIONLP_BB_PerSentence_train_test_MoreGlobalBMC.statis.EntitiesNoOverlapAnyOnto++;
	  
		}
	
}// end function build_all_chunk_...
///////////////////////
	
	
	
	
public void build_all_word_candidatesLmNCBI(){
		
//		OBORepresentation obRep=new OBORepresentation();
//		OBOParser objParser=new OBOParser("./data sets/BIONLP/OntoBiotope_BioNLP-ST13.obo");
//		try {
//			obRep= objParser.parseFile();
//			System.out.println("number of elements" + obRep.getNumberOfObo());
//			ArrayList<String>  objExactSyns=  obRep.getExactAllSynonyms();
//			ArrayList<String>  objRelatedSyns=  obRep.getRelatedAllSynonyms();
//			ArrayList<String>  objNames=  obRep.getAllNames();
//			
//			
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		
	
		for (int it=0;it<allsentences.size();it++){
		   //for (int w=0; w<allsentences.elementAt(it).sentence_feat.size();w++){
		   Integer[] indices=FEx_BIONLP_BB_PerSentence_train_test_MoreGlobalBMC.NCBIRep.getIndexesOfExistingWords(allsentences.elementAt(it).sentence_feat);
	       if (indices.length!=0)
	        	 for(int entitylist=0;entitylist<indices.length;entitylist++)  
	              {  
	        		 iEntity e1=new iEntity();
	                 Integer[] listindic={indices[entitylist]};
	        		 e1.add_features(allsentences.elementAt(it),listindic);
	                 allsentences.elementAt(it).tr_candidates.add(e1);}
		
		}
	}
	public void paragraph_info(BufferedReader br) throws IOException{
		paragraph_num=0;
		int sen_num=0;
		if (br!=null)
		while (br.ready())
		{
			String par=br.readLine();
			if ((par.endsWith(".")|| par.endsWith("]")))
				paragraph_num++;
			for (int i=sen_num;i<allsentences.size();i++){
			    if (par.contains(allsentences.elementAt(i).content))
			    	{ 
			    	 if (!(allsentences.elementAt(i).content.endsWith(".")|| allsentences.elementAt(i).content.endsWith("]")))
			    	
				  		{
			    		allsentences.elementAt(i).title=true;
			    		allsentences.elementAt(i).description_number=paragraph_num+1;//.description_number=paragraph_num;
				  		}
			    	else 
			    	    allsentences.elementAt(i).description_number=paragraph_num;
			    	if (par.endsWith(allsentences.elementAt(i).content))
			          { sen_num=i+1;break;  } // the whole paragraph is parsed and matched with sentences
			      }
			}
		}
//		for (int i=1;i<allsentences.size();i++)
//			//if (allsentences.elementAt(i-1).content.equalsIgnoreCase("Description"))
//			{	
//				if (allsentences.elementAt(i).title==true  && allsentences.elementAt(i-1).title==false)
//					D++;
//				allsentences.elementAt(i).description_number=D;
//			}
	  }
	
public void candidate_features(Vector <Vector<String>> hashTable){
		
	for (int e=0;e<discourse_annotations.entities.size();e++){ 
		
		int sen_id=discourse_annotations.entities.elementAt(e).sentence_id;
		//produce the features of each entity
		BIOsentence s=new BIOsentence(allsentences.elementAt(sen_id));
		if (e==27)
			System.out.print("");
		 
		phrase_annotations e1=discourse_annotations.entities.elementAt(e);
		e1.head_index= FEx_BIONLP_BB_PerSentence_train_test_MoreGlobalBMC.HeadW_with_index(e1.wordIndexes, s.sentence_feat);
	    discourse_annotations.add_Efeatures(e,s);
	    int hashIndex=0;
	    if (e1.role.equals("Sp_candidate")) {hashIndex=2;}
	    for (int f=0;f<e1.features.size();f++)
  	       if (!hashTable.elementAt(hashIndex).contains(e1.features.get(f)))
			    hashTable.elementAt(hashIndex).addElement(e1.features.get(f));
	    }
	
	for (int r=0; r< discourse_annotations.relations.size();r++){
		//produce the features of each relation
	   
	   relation_annotations r1=discourse_annotations.relations.elementAt(r);
	   int e1=find_entity(r1.id1);
	   int e2=find_entity(r1.id2);
	   { 
		 discourse_annotations.add_Rfeatrues(e1,e2,r,allsentences,paragraph_num,CorefMatrix);//,sen_id1,sen_id2)
	     for (int f=0;f<r1.features.size();f++)
		   if (!hashTable.elementAt(1).contains(r1.features.get(f)))
			    hashTable.elementAt(1).addElement(r1.features.get(f)); 
	     }  
	}
	hashTable.elementAt(2).addElement("same_T"); //this feature is between two relations. 
}
//
//public void candidate_LocLoc(){
//	for (int i=0;i<lm_candidates.size();i++)
//	{ 
//	  for (int j=i+1;j<lm_candidates.size();j++){
//	  	  if(lm_candidates.get(i).sentence_id==lm_candidates.get(j).sentence_id){
//		  simTriplet t=new simTriplet();
//		  t.id1=lm_candidates.get(i).id;
//		  t.id2=lm_candidates.get(j).id;
//		  System.out.print(lm_candidates.get(i).id);
//		  System.out.print(lm_candidates.get(j).id);
//		  LocLocMatrix.add(t);
//		  
//	  }
//	  }
//				
//	}
//}
public int sentence_level_partner(int e1,int e2){
	
   if (discourse_annotations.entities.elementAt(e1).sentence_id!=discourse_annotations.entities.elementAt(e2).sentence_id)
	  if (discourse_annotations.entities.elementAt(e1).coreferences!=null)
	      for (int c=0;c<discourse_annotations.entities.elementAt(e1).coreferences.length;c++)
		     if(discourse_annotations.entities.elementAt(discourse_annotations.entities.elementAt(e1).coreferences[c]-1).sentence_id==discourse_annotations.entities.elementAt(e2).sentence_id)
		       {
		    	e1= discourse_annotations.entities.elementAt(e1).coreferences[c]-1;
		       }
return e1;
}

public void EntitySimilarity(){
	// Compute Jaccard similarity for the bacterium entities
	Similarity_Matrix=new double[discourse_annotations.entities.size()][discourse_annotations.entities.size()];
	
	for (int i=0;i<discourse_annotations.entities.size();i++)
	 {
	   
	   phrase_annotations e1=discourse_annotations.entities.elementAt(i);
	   if (!e1.role.equals("Sp_candidate"))
	    { 
		   String[] S1={};
	       for (int w=0; w<e1.wordIndexes.length;w++)
	       {  
	         S1=Arrays.copyOf(S1,S1.length+1);
	    	 S1[S1.length-1]=PreProcessing.BacteriaToken(allsentences.elementAt(e1.sentence_id).sentence_feat.elementAt(e1.wordIndexes[w]).words.word);
	      
	       }
	     S1=PreProcessing.BacteriafullName(S1);
	     for (int j=0; j<discourse_annotations.entities.size();j++)
		  {
		     phrase_annotations e2=discourse_annotations.entities.elementAt(j);
		     String[] S2={};  
		     for (int w=0; w<e2.wordIndexes.length;w++)
		      { 	 
		    	 S2=Arrays.copyOf(S2,S2.length+1);
		    	 S2[w]=PreProcessing.BacteriaToken(allsentences.elementAt(e2.sentence_id).sentence_feat.elementAt(e2.wordIndexes[w]).words.word);
			      
		      }
		     S2=PreProcessing.BacteriafullName(S2);
		     Similarity_Matrix[i][j]=StringSimilarity.Jaccard(S1,S2);
		
	    }
	   }
	 }	
 }
public int find_entity(String idstr){
	int id=-1;
	for (int i=0;i<discourse_annotations.entities.size();i++)
		if (discourse_annotations.entities.elementAt(i).id.equals(idstr)) 
		    return(i);
	System.out.print("Entity is not found in the whole discourse!"+idstr);
	return id;
}
public void writeDiscourseXML(PrintWriter spxml,int doc_num,int a1Flag,int a2Flag){
	// a1 and a2 are annotation flags that are writtent in the files if the flags are true. 
	//Vector <Integer> tr =new Vector <Integer>();
	//Vector <Integer> lm =new Vector <Integer>();
	//Vector <Integer> sp= new Vector <Integer>();
	spxml.println("<DOCUMENT id=\""+doc_num +"\">");
	int sentence_num=0;
	for (int i=0;i<allsentences.size();i++){ 
	 sentence_num=sentence_num+1;
	 spxml.println("  <SENTENCE id=\""+sentence_num+"\">");
	 spxml.print("    <CONTENT>");spxml.print(allsentences.elementAt(i).content);spxml.println("</CONTENT>");
	 spxml.println("    <TOKENS>");
	 for (int j=0;j<allsentences.elementAt(i).sentence_feat.size();j++)
	  {
		spxml.println("      <TOKEN id=\""+(j+1)+"\">");
		spxml.println("\t<WORD>"+allsentences.elementAt(i).sentence_feat.elementAt(j).words.word+"</WORD>");
		spxml.println("\t<COCOA>"+allsentences.elementAt(i).sentence_feat.elementAt(j).words.cocoa+"</COCOA>");
		spxml.println("\t<LEMMA>"+allsentences.elementAt(i).sentence_feat.elementAt(j).words.lemma+"</LEMMA>");
		spxml.println("\t<CharacterOffsetBegin>"+allsentences.elementAt(i).sentence_feat.elementAt(j).words.wordOffset.B+"</CharacterOffsetBegin>");
		spxml.println("\t<CharacterOffsetEnd>"+allsentences.elementAt(i).sentence_feat.elementAt(j).words.wordOffset.E+"</CharacterOffsetEnd>");
		spxml.println("\t<POS>"+allsentences.elementAt(i).sentence_feat.elementAt(j).words.pos+"</POS>");
		spxml.println("\t<DPRL>"+allsentences.elementAt(i).sentence_feat.elementAt(j).words.DPRL+"</DPRL>");
		spxml.println("\t<SUBCATEGORIZATION>"+allsentences.elementAt(i).sentence_feat.elementAt(j).words.subcategorization+"</SUBCATEGORIZATION>");
		//spxml.println("\t<a2>"+allsentences.elementAt(i).sentence_feat.elementAt(j).words.a2+"</a2>");
		
		spxml.println("      </TOKEN>");
		}	
	 spxml.println("        </TOKENS>");
	
	//for(int i=0;i<relations.size();i++){
	// relation t=relations.elementAt(i);
	 //if(!tr.contains(t.wh_tr.head_index)){
      //tr.addElement(t.wh_tr.head_index);
	  //spxml.println("<TRAJECTOR id=\'tw"+t.wh_tr.head_index+"\'>"+ t.wh_tr.ph_head+"</TRAJECTOR>");}
	 //if (!lm.contains(t.wh_lm.head_index)){
	   //lm.addElement(t.wh_lm.head_index);	 
	   //spxml.println("<LANDMARK id=\'lw"+t.wh_lm.head_index+"\'>"+ t.wh_lm.ph_head+"</LANDMARK>");}
	 //if(!sp.contains(t.wh_sp.head_index)){
	   //sp.addElement(t.wh_sp.head_index);	 
	   //spxml.print("<SPATIAL_INDICATOR id=\'sw"+t.wh_sp.head_index+"\'>");
	// for(int j=0;j<t.wh_sp.ph.size();j++)
	 //spxml.print( t.wh_sp.ph.elementAt(j)+" ");
	 //spxml.println("</SPATIAL_INDICATOR>");}
	 //String tempg=t.calc_type.general.elementAt(0).toLowerCase();
	 //tempg=tempg.replaceAll("\"","");
	 //tempg=tempg.trim();
	 //if (!(tempg.equals("distance")|| tempg.equals("region")|| tempg.equals("direction")))
		// System.out.print("Stop");
	 
//	 spxml.println("<RELATION id=\'r"+(i)+"\'\tsp=\'sw"+t.wh_sp.head_index+"\'\ttr=\'tw"+t.wh_tr.head_index+"\'\tlm=\'lw"+t.wh_lm.head_index+"\'"+"\tgeneral_type=\'"+tempg+"\'/>");
	// }//end for relation
	spxml.println("  </SENTENCE>");
	
	//spxml.println();
  }
  spxml.println("<ANNOTATIONS>");
  for (int i=0;i<discourse_annotations.entities.size();i++){
	  spxml.println(" <PHRASE>");
	  for (int j=0;j<discourse_annotations.entities.elementAt(i).wordOffsets.size();j++)
	   {  
		   spxml.print("  <TOKEN>");
		   spxml.print("   <CharacterOffsetBegin>");
		   spxml.print(discourse_annotations.entities.elementAt(i).wordOffsets.elementAt(j).B);
		   spxml.print("   </CharacterOffsetBegin>");
		   spxml.print("   <CharacterOffsetEnd>");
		   spxml.print(discourse_annotations.entities.elementAt(i).wordOffsets.elementAt(j).E);
		   spxml.print("   </CharacterOffsetEnd>");
		   spxml.print("  </TOKEN>");
	   }
	  spxml.println();
	  spxml.print("  <ANNOTATION>");
   	  spxml.print(discourse_annotations.entities.elementAt(i).label);
   	  spxml.println("  </ANNOTATION>");
   	  spxml.println(" </PHRASE>");
   	  
   	 	}
  spxml.println("</ANNOTATIONS>");
    spxml.println("</DOCUMENT>");
	 }
public Vector<String> FindAnn(Vector<offsets> o, int sentenceInd){
	Vector <String> t=new Vector <String>();
	for (int i=0; i<discourse_annotations.entities.size() ;i++){
	 if (o.elementAt(0).E>this.discourse_annotations.entities.elementAt(i).wordOffsets.elementAt(0).B){
      if (this.discourse_annotations.entities.elementAt(i).wordOffsets.equals(o))
       { 
    	t=this.discourse_annotations.entities.elementAt(i).label;
    	break;
    	}
       }
	else 	
		break;
		
	 }
	return t;
 }
//public void writeRelationalFeatureSO(Vector <Vector<String>> hashTable, PrintParameters pw){
//	 /* hashTable is to collect the features in a structured way, hashTable-0 is the entity features and hashTable-1 are the relationship feature*/
//    int Sp_count=0;
//    int Sp_indexes[]={};
//	for (int e=0; e<this.discourse_annotations.entities.size();e++)
//		{  
//		        Vector <Integer>  featList=new Vector <Integer>();
//		        int hashIndex=0; // 0 for entities and 1 for Sp candidates
//			    String s="";
//			    if (discourse_annotations.entities.elementAt(e).role.equals("Sp_candidate"))
//			    	 hashIndex=2;
//			    for (int f=0;f<discourse_annotations.entities.elementAt(e).features.size();f++)
//			      {
//				     s=discourse_annotations.entities.elementAt(e).features.get(f);
//				     featList.addElement(hashTable.elementAt(hashIndex).indexOf(s)+1);
//			         pw.inputpredicates.print("entity("+(e+1)+","+s+").");
//			       }
//			     String label= discourse_annotations.entities.elementAt(e).role;
//			     if (label==null)
//			    	 featList.addElement(0);
//			     else
//			        {
//			         if (label.startsWith("G"))
//			        		 {
//			        	      featList.addElement(1);
//			        	      pw.outputpredicates.print("Geographical("+(e+1)+").");
//			        	      
//			        		 }
//			         if (label.startsWith("H"))
//			             {
//			        	  featList.addElement(2);
//			        	  pw.outputpredicates.print("Habitat("+(e+1)+")."); 
//			             }
//			         
//			         if (label.startsWith("B"))
//			             {
//			        	     featList.addElement(3);
//			        	     pw.outputpredicates.print("Bacteria("+(e+1)+").");
//			             }
//			         if (label.startsWith("Sp"))
//		             {
//		        	     featList.addElement(4);
//		        	     pw.outputpredicates.print("Sp_candid("+(Sp_count+1)+").");
//		             }
//			         
//			         }
//			     pw.inputpredicates.println();
//			     pw.outputpredicates.println();
//			     
//			     ///////*******//////
//		        
//		         for(int t1=0; t1<featList.size()-1;t1++){
//		        	 if (hashIndex==0)
//		        	  pw.roleFeatures.println((e+1)+"\t"+featList.elementAt(t1)+"\t"+"1");
//		        	 else
//		        	     {
//		        	      pw.SpLabels.println((Sp_count+1)+"\t"+featList.elementAt(t1)+"\t"+"1");
//		        	        }
//    		      // pwE.print(featList.elementAt(t1)+", ");
//    		       
//			     }// end for each feature
////		         if (hashIndex==0)
////	    		    pw.entityLabels.println(featList.elementAt(featList.size()-1));
////		         else
////		           {
//////		        	 Sp_indexes=Arrays.copyOf(Sp_indexes, Sp_indexes.length+1);
//////       	             Sp_indexes[Sp_count]=Integer.parseInt(discourse_annotations.entities.elementAt(e).id.replaceAll("[a-zA-Z]",""));
////        	             Sp_count++;
////		            }
//		         //else
//		        //	 pw.SpCandidatefile.println(featList.elementAt(featList.size()-1));
//	    			
//	    		     //for (int t2=0;t2<atts.element));
//	    		}// end for all entities 
//	//pwE.println();
//	
//	int r_counter=0;
//	int TS_counter=0;
//	int LS_counter=0;
//	
//	for (int r=0; r<this.discourse_annotations.relations.size();r++)
//	  {  
//          int Insertflag=0; 
//          int e1=find_entity(discourse_annotations.relations.elementAt(r).id1);
//    	  int e2=find_entity(discourse_annotations.relations.elementAt(r).id2);
//    	  if (pw.Globality==1) // this means we function per sentence
//    		{
//    		 if (discourse_annotations.entities.elementAt(e1).sentence_id==discourse_annotations.entities.elementAt(e2).sentence_id)
//		     Insertflag=1;
//    		 }
//    	  else 
//    		  Insertflag=1; 
//    	 
//    	 if (Insertflag==1){
//    		 
//		   Vector <Integer>  RfeatList=new Vector <Integer>();
//	       String s="";
//	       if (discourse_annotations.relations.elementAt(r).relationship!=null)
//	         if (discourse_annotations.relations.elementAt(r).relationship.toLowerCase().equals("localization")||discourse_annotations.relations.elementAt(r).relationship.toLowerCase().equals("localizationcoref"))
//	          FEx_BIONLP_BB_PerSentence_train_test_MoreGlobalBMC.statis.testPositiveLoc++;
//	         else 
//	    	  FEx_BIONLP_BB_PerSentence_train_test.statis.testNgativeLoc++;
//	       for (int f=0;f<discourse_annotations.relations.elementAt(r).features.size();f++)
//	         {  
//	    	  s=discourse_annotations.relations.elementAt(r).features.get(f);
//	          RfeatList.addElement(hashTable.elementAt(1).indexOf(s)+1);
//              pw.inputpredicates.print("relation("+(r_counter+1)+","+discourse_annotations.relations.elementAt(r).id1+","+discourse_annotations.relations.elementAt(r).id2+","+s+").");
//	         }
//             String label= discourse_annotations.relations.elementAt(r).relationship;
//             if (label==null)
//              {
//        	  RfeatList.addElement(0);
//        	  pw.outputpredicates.println("Non("+(r_counter+1)+","+discourse_annotations.relations.elementAt(r).id1+","+discourse_annotations.relations.elementAt(r).id2+").");
//    	      }
//             else
//              if (label.toLowerCase().equals("localization"))
//               {
//        		 RfeatList.addElement(1);
//        		 pw.outputpredicates.println("Location("+(r_counter+1)+","+discourse_annotations.relations.elementAt(r).id1+","+discourse_annotations.relations.elementAt(r).id2+").");
//      	       }
//            else
//               if (label.startsWith("P"))
//               {   
//            	   pw.outputpredicates.println("PartOf("+(r_counter+1)+","+discourse_annotations.relations.elementAt(r).id1+","+discourse_annotations.relations.elementAt(r).id2+").");
//        	       RfeatList.addElement(2);
//        	    }
//             else 
//            	 if (label.toLowerCase().equals("localizationcoref") )
//            	  {
//            	    pw.outputpredicates.println("LocationCoref("+(r_counter+1)+","+discourse_annotations.relations.elementAt(r).id1+","+discourse_annotations.relations.elementAt(r).id2+").");
//        	        RfeatList.addElement(3);
//            	   }
//            else
//            	 if (label.equals("Tr_Sp"))
//            	 {
//            		 pw.outputpredicates.println("Tr_Sp("+(TS_counter+1)+","+discourse_annotations.relations.elementAt(r).id1+","+discourse_annotations.relations.elementAt(r).id2+").");
//         	         RfeatList.addElement(4); 
//            	 }
//            	 else
//                  if (label.equals("Lm_Sp"))
//        	      {
//        		   pw.outputpredicates.println("Lm_Sp("+(LS_counter+1)+","+discourse_annotations.relations.elementAt(r).id1+","+discourse_annotations.relations.elementAt(r).id2+").");
//     	           RfeatList.addElement(5); 
//        	      }
//            	   
//        for(int t1=0; t1<RfeatList.size()-1;t1++)
//         {
//          if (label==null)
//        	  pw.pairFeatures.println((r_counter+1)+"\t"+RfeatList.elementAt(t1)+"\t"+"1");
//          else
//            if (label.equals("Tr_Sp"))
//        	  pw.pairFeatures.println((TS_counter+1)+"\t"+RfeatList.elementAt(t1)+"\t"+"1");
//            else 
//        	  if (label.equals("Lm_Sp"))
//        	    pw.pairFeatures.println((LS_counter+1)+"\t"+RfeatList.elementAt(t1)+"\t"+"1");
//        	  else 
//        		pw.pairFeatures.println((r_counter+1)+"\t"+RfeatList.elementAt(t1)+"\t"+"1");
//	     } // end for each feature
//        // pwR.println();
//		
//		 String id1=discourse_annotations.relations.elementAt(r).id1;
//		 String id2=discourse_annotations.relations.elementAt(r).id2;
//		 if (label==null)
//		  {
//			 pw.pairFeatures.println(RfeatList.elementAt(RfeatList.size()-1));
//	       //  pw.relationKeyfile.println((Integer.parseInt(id1.substring(id1.indexOf("T")+1)))+"\t"+(Integer.parseInt(id2.substring(id2.indexOf("T")+1)))+"\t"+(r_counter+1));
//	         r_counter++;
//		   }
//		 else
//		 if (label.equals("Tr_Sp"))
//			 {
//			    pw.pairFeatures.println((RfeatList.elementAt(RfeatList.size()-1)));
//			    pw.relationLabelsTr_Sp.println((Integer.parseInt(id1.substring(id1.indexOf("T")+1)))+"\t"+(Integer.parseInt(id2.substring(id2.indexOf("p")+1)))+"\t"+(TS_counter+1));
//	            TS_counter++;
//			 }
//		 else 
//			 if (label.equals("Lm_Sp"))	 
//			 {
//				 pw.relationLabelsLm_Sp.println(RfeatList.elementAt(RfeatList.size()-1));
//			     pw.relationLabelsLm_Sp.println((Integer.parseInt(id1.substring(id1.indexOf("T")+1)))+"\t"+(Integer.parseInt(id2.substring(id2.indexOf("p")+1)))+"\t"+(LS_counter+1));
//			     LS_counter++;
//			 }
//			 else
//			 { 
//				 pw.pairFeatures.println(RfeatList.elementAt(RfeatList.size()-1));
//				// pw.relationKeyfile.println((Integer.parseInt(id1.substring(id1.indexOf("T")+1)))+"\t"+(Integer.parseInt(id2.substring(id2.indexOf("T")+1)))+"\t"+(r_counter+1));
//				 r_counter++;
//			 }
//		 
//		 pw.inputpredicates.println();	 
//		 
//		 }//Insertflag ==1
//     }// end for all relations 
////pwE.println();
//
// }// end write Entity/Relationship features prosedure


public void writeRelationalFeatureSONew(Vector <Vector<String>> hashTable, PrintParameters pw){
	/* hashTable is to collect the features in a structured way, hashTable-0 is the entity features and hashTable-1 are the relationship feature*/
	int Sp_count=0;
	int Tr_count=0;
	int Lm_count=0;
	int Sp_indexes[]={};
	for (int e=0; e<this.discourse_annotations.entities.size();e++)
	{   
		if (e==42)
			System.out.println();
		phrase_annotations Ent=discourse_annotations.entities.elementAt(e);
		pw.entitySpans.println((e+1)+"\t"+Ent.span(allsentences.elementAt(Ent.sentence_id))+"\t"+Ent.content(allsentences.elementAt(Ent.sentence_id)).trim());
	//	System.out.println    ((e+1)+"\t"+Ent.span(allsentences.elementAt(Ent.sentence_id))+"\t"+ Ent.content(allsentences.elementAt(Ent.sentence_id)));
		Vector <Integer>  featList=new Vector <Integer>();
		int hashIndex=0;  // 0 for entities and 1 for Sp candidates
		String s="";
		//  if isSpCand(discourse_annotations.entities.elementAt(e)||isTR || isLm)//.role.equals("Sp_candidate"))
		//	 hashIndex=2;
		// {
		for (int f=0;f<Ent.features.size();f++)
		{
			s=Ent.features.get(f);
			if (hashTable.elementAt(0).indexOf(s)+1==0)
				System.out.print("");
			
			
			featList.addElement(hashTable.elementAt(0).indexOf(s)+1);
			pw.inputpredicates.print("entity("+(e+1)+","+s+").");
			if (featList.elementAt(f)!=0)
			  pw.roleFeatures.println((e+1)+"\t"+featList.elementAt(f)+"\t"+"1");
		}

		String label= Ent.role;
		if (label.startsWith("G"))

		{
			pw.outputpredicates.println("Geographical("+(e+1)+")."); 
			pw.LmLabels.println((e+1)+"\t1\t"+(Lm_count+1));
			Lm_count++;
		}
		if (label.startsWith("H"))
		{
			//featList.addElement(2);
			pw.outputpredicates.println("Habitat("+(e+1)+")."); 
			pw.LmLabels.println((e+1)+"\t1\t"+(Lm_count+1));
		//	pw.entitySpans.println((e+1)+"\t"+Ent.phrase_offset.B+"\t"+Ent.phrase_offset.E+"\t"+ Ent.phrase_content);
			Lm_count++;
		}

		if (label.startsWith("B"))
		{
			//  featList.addElement(3);
			pw.outputpredicates.println("Bacteria("+(e+1)+").");
			pw.TrLabels.println((e+1)+"\t1\t"+(Tr_count+1));
			Tr_count++;
		}
		if (label.startsWith("Sp"))
		{
			//featList.addElement(4);
			pw.outputpredicates.println("Sp_candid("+(Sp_count+1)+").");
			pw.SpLabels.println((e+1)+"\t0\t"+(Sp_count+1));
			Sp_count++;
		}
		if (label.isEmpty())
		{
			pw.TrLabels.println((e+1)+"\t0\t"+(Tr_count+1));	
			Tr_count++;
			pw.LmLabels.println((e+1)+"\t0\t"+(Lm_count+1));
			Lm_count++;
		}

		pw.inputpredicates.println();
		pw.outputpredicates.println();

		///////*******//////

		//for(int t1=0; t1<featList.size()-1;t1++) // it was not clear why I go through the feature list here, so it is removed!
			


	}// end for all entities 
	
	//int r_counter=0;
	//int TS_counter=0;
	//int LS_counter=0;
     int rr_count=0; //this counter is to write the serial number of the loc-loc pairs
	for (int r=0; r<this.discourse_annotations.relations.size();r++)
	{  
		int Insertflag=0; 
		int e1=find_entity(discourse_annotations.relations.elementAt(r).id1);
		int e2=find_entity(discourse_annotations.relations.elementAt(r).id2);
		if (pw.Globality==1) // this means we function per sentence
		{
			if (discourse_annotations.entities.elementAt(e1).sentence_id==discourse_annotations.entities.elementAt(e2).sentence_id)
				Insertflag=1;
		}
		else 
			Insertflag=1; 

		if (Insertflag==1){

			Vector <Integer>  RfeatList=new Vector <Integer>();
			String s="";
			if (discourse_annotations.relations.elementAt(r).relationship!="")
				if (discourse_annotations.relations.elementAt(r).relationship.toLowerCase().equals("localization")||discourse_annotations.relations.elementAt(r).relationship.toLowerCase().equals("localizationcoref"))
					FEx_BIONLP_BB_PerSentence_train_test_MoreGlobalBMC.statis.testPositiveLoc++;
				else 
					FEx_BIONLP_BB_PerSentence_train_test_MoreGlobalBMC.statis.testNgativeLoc++;
			for (int f=0;f<discourse_annotations.relations.elementAt(r).features.size();f++)
			{  
				s=discourse_annotations.relations.elementAt(r).features.get(f);
				RfeatList.addElement(hashTable.elementAt(1).indexOf(s)+1);
				pw.inputpredicates.print("relation("+(r+1)+","+discourse_annotations.relations.elementAt(r).id1+","+discourse_annotations.relations.elementAt(r).id2+","+s+").");
				if(RfeatList.elementAt(f)!=0)
				 pw.pairFeatures.println((r+1)+"\t"+RfeatList.elementAt(f)+"\t"+"1"); // here is fiiling the feature file for the relation extraction. 
				//in this table r_ counter is the global relation counter \t feature index\t  1. 
			}
			
			String label= discourse_annotations.relations.elementAt(r).relationship;
			if (label=="")
			{
				//RfeatList.addElement(0);// I don't know why I ahve added this line here, so commetned for the moment. 
				pw.outputpredicates.println("Non("+(r+1)+","+discourse_annotations.relations.elementAt(r).id1+","+discourse_annotations.relations.elementAt(r).id2+").");
			    pw.relationLabelsTr_Lm.println((r+1)+"\t0"+"\t"+(e1+1)+"\t"+(e2+1));
				
			 }
			else
				if (label.toLowerCase().equals("localization"))
				{
					//RfeatList.addElement(1); // I don't know why I ahve added this line here, so commetned for the moment. 
					pw.outputpredicates.println("Location("+(r+1)+","+discourse_annotations.relations.elementAt(r).id1+","+discourse_annotations.relations.elementAt(r).id2+").");
					pw.relationLabelsTr_Lm.println((r+1)+"\t1"+"\t"+(e1+1)+"\t"+(e2+1));
				}
				else
					if (label.startsWith("P"))
					{   
						pw.outputpredicates.println("PartOf("+(r+1)+","+discourse_annotations.relations.elementAt(r).id1+","+discourse_annotations.relations.elementAt(r).id2+").");
						//RfeatList.addElement(2);// I don't know why I ahve added this line here, so commetned for the moment. 
						if (Util.overlap_tok_index(discourse_annotations.entities.elementAt(e1).wordIndexes,discourse_annotations.entities.elementAt(e2).wordIndexes))
							FEx_BIONLP_BB_PerSentence_train_test_MoreGlobalBMC.statis.PartOfEntityOverlap++;
							
						
					}
					else 
						if (label.toLowerCase().equals("localizationcoref") )
						{
							pw.outputpredicates.println("LocationCoref("+(r+1)+","+discourse_annotations.relations.elementAt(r).id1+","+discourse_annotations.relations.elementAt(r).id2+").");
							pw.relationLabelsTr_Lm.println((r+1)+"\t0"+"\t"+(e1+1)+"\t"+(e2+1));
							//RfeatList.addElement(3);// I don't know why I ahve added this line here, so commetned for the moment. 
						}
						else
							if (label.equals("Tr_Sp"))
							{
								pw.outputpredicates.println("Tr_Sp("+(r+1)+","+discourse_annotations.relations.elementAt(r).id1+","+discourse_annotations.relations.elementAt(r).id2+").");
								pw.relationLabelsTr_Sp.println((r+1)+"\t0"+"\t"+(e1+1)+"\t"+(e2+1));
								//RfeatList.addElement(4); // I don't know why I ahve added this line here, so commetned for the moment. 
							}
							else
								if (label.equals("Lm_Sp"))
								{
									pw.outputpredicates.println("Lm_Sp("+(r+1)+","+discourse_annotations.relations.elementAt(r).id1+","+discourse_annotations.relations.elementAt(r).id2+").");
									pw.relationLabelsLm_Sp.println((r+1)+"\t0"+"\t"+(e1+1)+"\t"+(e2+1));
									//RfeatList.addElement(5); // I don't know why I ahve added this line here, so commetned for the moment. 
								}

		/*	for(int t1=0; t1<RfeatList.size()-1;t1++) //this part is confusing I am not sure why I have written this, so removed.  
			{
				if (label==null)
					pw.pairFeatures.println((r_counter+1)+"\t"+RfeatList.elementAt(t1)+"\t"+"1");
				else
					if (label.equals("Tr_Sp"))
						pw.pairFeatures.println((TS_counter+1)+"\t"+RfeatList.elementAt(t1)+"\t"+"1");
					else 
						if (label.equals("Lm_Sp"))
							pw.pairFeatures.println((LS_counter+1)+"\t"+RfeatList.elementAt(t1)+"\t"+"1");
						else 
							pw.pairFeatures.println((r_counter+1)+"\t"+RfeatList.elementAt(t1)+"\t"+"1");
			} // end for each feature
			// pwR.println();
			 * 
			 * I am not sure why I wrote the below code here, I just remove it now with a whole block comments. 
*/
		/*	String id1=discourse_annotations.relations.elementAt(r).id1;
			String id2=discourse_annotations.relations.elementAt(r).id2;
			if (label==null)
			{
				//pw.pairFeatures.println(RfeatList.elementAt(RfeatList.size()-1)); 
				//   pw.relationKeyfile.println((Integer.parseInt(id1.substring(id1.indexOf("T")+1)))+"\t"+(Integer.parseInt(id2.substring(id2.indexOf("T")+1)))+"\t"+(r_counter+1));
				r_counter++;
			}
			else
				if (label.equals("Tr_Sp"))
				{
					pw.pairFeatures.println((RfeatList.elementAt(RfeatList.size()-1)));
					pw.relationLabelsTr_Sp.println((Integer.parseInt(id1.substring(id1.indexOf("T")+1)))+"\t"+(Integer.parseInt(id2.substring(id2.indexOf("p")+1)))+"\t"+(TS_counter+1));
					TS_counter++;
				}
				else 
					if (label.equals("Lm_Sp"))	 
					{
						pw.pairFeatures.println(RfeatList.elementAt(RfeatList.size()-1));
						pw.relationLabelsLm_Sp.println((Integer.parseInt(id1.substring(id1.indexOf("T")+1)))+"\t"+(Integer.parseInt(id2.substring(id2.indexOf("p")+1)))+"\t"+(LS_counter+1));
						LS_counter++;
					}
					else
					{ 
						pw.pairFeatures.println(RfeatList.elementAt(RfeatList.size()-1));
						// pw.relationKeyfile.println((Integer.parseInt(id1.substring(id1.indexOf("T")+1)))+"\t"+(Integer.parseInt(id2.substring(id2.indexOf("T")+1)))+"\t"+(r_counter+1));
						r_counter++;
					}*/

			pw.inputpredicates.println();	 

		}//Insertflag ==1
		
	for (int r2=r+1; r2<this.discourse_annotations.relations.size();r2++){
		int e3=find_entity(discourse_annotations.relations.elementAt(r2).id2);
		int e4=find_entity(discourse_annotations.relations.elementAt(r2).id1);
	    int ll=0; 
		if (discourse_annotations.entities.elementAt(e2).sentence_id==discourse_annotations.entities.elementAt(e3).sentence_id)
		{
			System.out.println(discourse_annotations.relations.elementAt(r2).relationship+ rr_count+ ";");
			System.out.println( discourse_annotations.relations.elementAt(r).relationship);
			rr_count++;
			if (discourse_annotations.relations.elementAt(r2).relationship.equalsIgnoreCase("localization") & discourse_annotations.relations.elementAt(r).relationship.equalsIgnoreCase("localization"))
						ll=1;
			
			pw.Loc_LocLabels.println((rr_count)+"\t"+ll+"\t"+(r+1)+"\t"+(r2+1));
			if (e1==e4)
			 pw.R_RFeatures.println((rr_count)+"\t"+1+"\t"+"1"); // here is fiiling the feature file for the relation extraction. 
			//pw.Loc_LocLabels.println((rr_count)+"\t"+ll+"\t"+(r+1)+"\t"+(r2+1)+"\t"+discourse_annotations.relations.elementAt(r).relationship+e1+" "+e2+"\t"+discourse_annotations.relations.elementAt(r2).relationship+e4+" "+e3);
			pw.outputpredicates.println("LocLoc("+(r+1)+","+(r2+1)+","+ll+").");
		}
	}	
	}// end for all relations 
	//pwE.println();

}// end write Entity/Relationship features prosedure


public void writeCoreferences( PrintWriter Coreferences){
	int coref_index=1;
	for (int i=0; i<CorefMatrix.length;i++)
		for (int k=0;k<CorefMatrix[i].length;k++)
			for (int j=k+1;j<CorefMatrix[i].length;j++)
			{
				Coreferences.println(CorefMatrix[i][k]+"\t"+CorefMatrix[i][j]+"\t"+coref_index);
				coref_index++;
			}

}
public void writeEntitySimilarity(PrintWriter Esimilarity){
	for (int i=0; i<Similarity_Matrix.length; i++)
		for (int j=0;j<Similarity_Matrix[i].length;j++)
			if (Similarity_Matrix[i][j]!=0)
				Esimilarity.println((i+1)+"\t"+(j+1)+"\t"+ Similarity_Matrix[i][j]);
}
public void chunk() {

	for (int i=0;i<allsentences.size();i++){
		// String[] a={" "};
		Vector<phrase_annotations> outch=new Vector<phrase_annotations>();
		int word_count=-1;
		int prev_word_count=0;
		int word_form_count=-1;
		int ch_num=0;



		//a[0]=allsentences.elementAt(i).content;
		String[] a={allsentences.elementAt(i).content};
		Chunker chunker = new Chunker();
		String temp="";
		Parser parser =
				new PlainToTokenParser(
						new WordSplitter(new SentenceSplitter(a)));
		String previous = "";

		for (Word w = (Word) parser.next(); w != null; w = (Word) parser.next()) 
		{


			//if (i==33 && word_count==1)
			//System.out.println("@@sentence number: "+i+" "+word_count);

			String prediction = chunker.discreteValue(w);
			if (prediction.startsWith("B-")|| prediction.startsWith("I-") && !previous.endsWith(prediction.substring(2)))
			{
				System.out.print("[" + prediction.substring(2) + " ");
				if (ch_num>=outch.size()-1)
				{ outch.add(new phrase_annotations()); temp=w.form;}
				//outch[outch.length-1]=""; 
			}
			if (word_count==-1&& prediction.startsWith("O")&& word_count!=prev_word_count){ //for the case that a sentence starts with a token which is not chunked.
				outch.add(new phrase_annotations()); temp=w.form;
			}
			prev_word_count=word_count;

			System.out.print("(" + w.partOfSpeech + " " + w.form + ") ");
			word_count++;
			word_form_count++;
			if (!w.form.trim().equalsIgnoreCase(allsentences.elementAt(i).sentence_feat.elementAt(word_count).words.word.trim()))
			{ System.out.print("Tokenization mismatch with the chunker."); }

			//  if (outch.lastElement()!=null)
			outch.lastElement().phrase_content=outch.lastElement().phrase_content+w.form + " "; 
			outch.lastElement().wordIndexes=Arrays.copyOf(outch.lastElement().wordIndexes, outch.lastElement().wordIndexes.length+1);
			outch.lastElement().wordIndexes[outch.lastElement().wordIndexes.length-1]=new Integer(word_count);

			if (!w.form.equalsIgnoreCase(allsentences.elementAt(i).sentence_feat.elementAt(word_count).words.word))
			{
				if (w.form.length()>allsentences.elementAt(i).sentence_feat.elementAt(word_count).words.word.length())
				{
					if (word_count<allsentences.elementAt(i).sentence_feat.size()-1)
						if (w.form.contains(allsentences.elementAt(i).sentence_feat.elementAt(word_count).words.word+allsentences.elementAt(i).sentence_feat.elementAt(word_count+1).words.word))
							word_count++;
				}

				else
					if (allsentences.elementAt(i).sentence_feat.elementAt(word_count).words.word.replaceAll(" ","").contains(w.form))
					{
						word_count--;
						temp=temp.trim()+w.form.trim();
					}
					else 
						if (!allsentences.elementAt(i).sentence_feat.elementAt(word_count).words.word.contains(w.form))
							System.out.print("Deep mistmach between the word indexes of the chuncker and the tokenizer!");



				System.out.print("mistmach between the word indexes of the chuncker and the tokenizer!");


			}

			//  else 
			// 	  outch.lastElement().phrase_content=w.form + " ";
			if (!prediction.equals("O")
					&& (w.next == null
					|| chunker.discreteValue(w.next).equals("O")
					|| chunker.discreteValue(w.next).startsWith("B-")
					|| !chunker.discreteValue(w.next)
					.endsWith(prediction.substring(2))))
			{ 
				System.out.print("] ");
				//if (word_count!=prev_word_count)
				outch.add(new phrase_annotations()); temp="";
				ch_num++;
			}
			if (w.next == null) 
			{
				System.out.println();
				if (!outch.lastElement().phrase_content.isEmpty()) // there might be a redundant empty element here 
				{                                   // which we should remove, because we add a new element after closing a bracket.
					outch.lastElement().phrase_content=outch.lastElement().phrase_content.trim();
					//allsentences.elementAt(i).chunks=Arrays.copyOf(allsentences.elementAt(i).chunks,outch.length);
				}

				else 
				{
					outch.removeElementAt(outch.size()-1); 
					outch.lastElement().phrase_content=outch.lastElement().phrase_content.trim();

					//outch[outch.length-2]=outch[outch.length-2].trim();
					//    allsentences.elementAt(i).chunks=Arrays.copyOf(allsentences.elementAt(i).chunks,outch.length-1);
				}
			}
			previous = prediction;
		}
		int lookahead=0;
		// allsentences.elementAt(i).chunks=Arrays.copyOf(allsentences.elementAt(i).chunks,outch.length-1);
		for (int kk=0;kk<outch.size();kk++){
			if(!outch.elementAt(kk).phrase_content.isEmpty())  	
			{	
				allsentences.elementAt(i).sen_chuncks.add(new phrase_annotations());
				allsentences.elementAt(i).sen_chuncks.lastElement().id="ch"+kk;
				allsentences.elementAt(i).sen_chuncks.lastElement().sentence_id=i;
				outch.elementAt(kk).wordIndexes=clean_chunk(outch.elementAt(kk).wordIndexes);
				allsentences.elementAt(i).sen_chuncks.lastElement().wordIndexes=outch.elementAt(kk).wordIndexes;
				allsentences.elementAt(i).sen_chuncks.lastElement().phrase_content=outch.elementAt(kk).content(allsentences.elementAt(i));
			}
		}
	}
}



////////
public void chunkBIO() {

	for (int i=0;i<allsentences.size();i++){
		
		Vector<phrase_annotations> outch=new Vector<phrase_annotations>();
		int word_count=0;
		boolean flag=false;
		String[] a={allsentences.elementAt(i).content};
		Chunker chunker = new Chunker();
		Parser parser =
				new PlainToTokenParser(
						new WordSplitter(new SentenceSplitter(a)));
		String previous = "";
        
		
		for (Word w = (Word) parser.next(); w != null; w = (Word) parser.next()) {
		      String prediction = chunker.discreteValue(w);
		      if (prediction.startsWith("B-")
		          || prediction.startsWith("I-")
		             && !previous.endsWith(prediction.substring(2)))
		        {
		    	  System.out.print("[" + prediction.substring(2) + " ");
		    	  outch.add(new phrase_annotations());
		    	  flag=true;
		    	 }
		      
		      
		      System.out.print("(" + w.partOfSpeech + " " + w.form + ") ");
		      if (flag==true & word_count<allsentences.elementAt(i).sentence_feat.size()) // this means the words are not out of chunks
		      {
		    	  outch.lastElement().phrase_content=outch.lastElement().phrase_content+w.form + " "; 
		    	  if (!w.form.trim().replaceAll("[^a-zA-Z0-9\\s_]", "").replaceAll(" ", "").equalsIgnoreCase(allsentences.elementAt(i).sentence_feat.elementAt(word_count).words.word.trim().replaceAll("[^a-zA-Z0-9\\s_]", "").replaceAll(" ", "")))
					{ System.out.print("Tokenization mismatch with the chunker.");
					 word_count=find_word_index(w.form,word_count,i);}
				  outch.lastElement().wordIndexes=Arrays.copyOf(outch.lastElement().wordIndexes, outch.lastElement().wordIndexes.length+1);
				  outch.lastElement().wordIndexes[outch.lastElement().wordIndexes.length-1]=new Integer(word_count);
				

			
		      }
		      
		      if (!prediction.equals("O")
		          && (w.next == null
		              || chunker.discreteValue(w.next).equals("O")
		              || chunker.discreteValue(w.next).startsWith("B-")
		              || !chunker.discreteValue(w.next)
		                  .endsWith(prediction.substring(2))))
		        {
		    	  System.out.print("] ");
		    	  flag=false;
		        }
		      
		      
		      if (w.next == null) System.out.println();
		      previous = prediction;
		      word_count++;
		      
		    }
		
		for (int kk=0;kk<outch.size();kk++){
			if(!outch.elementAt(kk).phrase_content.isEmpty())  	
			{	
				allsentences.elementAt(i).sen_chuncks.add(new phrase_annotations());
				allsentences.elementAt(i).sen_chuncks.lastElement().id="ch"+kk;
				allsentences.elementAt(i).sen_chuncks.lastElement().sentence_id=i;
				outch.elementAt(kk).wordIndexes=clean_chunk(outch.elementAt(kk).wordIndexes);
				allsentences.elementAt(i).sen_chuncks.lastElement().wordIndexes=outch.elementAt(kk).wordIndexes;
				allsentences.elementAt(i).sen_chuncks.lastElement().phrase_content=outch.elementAt(kk).content(allsentences.elementAt(i));
			}
		}
	}
}

private int find_word_index(String form, int word_count, int sen_id) {
	String b=form.trim().replaceAll("[^a-zA-Z0-9\\s_]", "").replaceAll(" ", "");
	String a="";
	for (int i=Math.max(0,word_count-2);i<Math.min(word_count+2,allsentences.elementAt(sen_id).sentence_feat.size());i++)
	{
		a=allsentences.elementAt(sen_id).sentence_feat.elementAt(i).words.word.trim().replaceAll("[^a-zA-Z0-9\\s_]", "").replaceAll(" ", "");
		if (b.equalsIgnoreCase(a))
	      {
			if (!a.isEmpty())
			 return(i);
			 }
	}
	a=allsentences.elementAt(sen_id).sentence_feat.elementAt(word_count-1).words.word.trim().replaceAll("[^a-zA-Z0-9\\s_]", "").replaceAll(" ", "");
	
	if (a.contains(b))
	   return(word_count-1); // when the parser connects two words that are separated by the chunker.
	System.out.print("word form not found!");
	return word_count;
}
///////
public void chunker_missEntities(PrintWriter misedChunker_Entity) {
	
	// TODO Auto-generated method stub
	//int sen_id=0;
	for (int i=0; i<discourse_annotations.entities.size();i++)
	 {
		phrase_annotations E=discourse_annotations.entities.elementAt(i);
		int s_id=E.sentence_id;
		String p1=E.content(allsentences.elementAt(s_id));
		p1=p1.replaceAll("[^a-zA-Z0-9\\s_]", "");
		p1=p1.replaceAll(" ", "");
		
	    boolean f=find_in_chunksString(s_id,p1);
	    
	    		//find_in_chunks(s_id,E.wordIndexes);
	    if (f==false)
	    	{
	    	misedChunker_Entity.println(E.content(allsentences.elementAt(s_id)));
	    	for (int ch=0; ch<allsentences.elementAt(s_id).sen_chuncks.size(); ch++){
	    		misedChunker_Entity.print(allsentences.elementAt(s_id).sen_chuncks.elementAt(ch).content(allsentences.elementAt(s_id))+" & ");
	    	}
	    	misedChunker_Entity.println("-------------------------------");
	    	if (E.role.startsWith("B"))
	    		 FEx_BIONLP_BB_PerSentence_train_test_MoreGlobalBMC.statis.missed_B_chunks++;
	    	if (E.role.startsWith("H"))
	    		 FEx_BIONLP_BB_PerSentence_train_test_MoreGlobalBMC.statis.missed_H_chunks++;
	    	if (E.role.startsWith("G"))
	    		 FEx_BIONLP_BB_PerSentence_train_test_MoreGlobalBMC.statis.missed_G_chunks++;
	    		
	    	}
	   	}
	
}
/////////
private boolean find_in_chunksString(int s_id, String p1) {
	 boolean flag=false;
		for (int i=0; i<allsentences.elementAt(s_id).sen_chuncks.size();i++){
			String p2=allsentences.elementAt(s_id).sen_chuncks.elementAt(i).content(allsentences.elementAt(s_id)).replaceAll("[^a-zA-Z0-9\\s_]", "");
			p2=p2.replaceAll(" ","");
			if (p1.equalsIgnoreCase(p2))
				{flag=true;
				return flag;}
		}
		return flag;
}
private boolean find_in_chunks(int s_id, Integer[] wordInd) {
	 boolean flag=false;
	for (int i=0; i<allsentences.elementAt(s_id).sen_chuncks.size();i++){
		if (Arrays.deepEquals(wordInd,allsentences.elementAt(s_id).sen_chuncks.elementAt(i).wordIndexes))
			{flag=true;
			return flag;}
	}
	return flag;
}

private Integer[] clean_chunk(Integer[] wordIndexes) {
	// TODO Auto-generated method stub
	Integer[] target={wordIndexes[0]};
	int j=1;
	//target[0]=new Integer(wordIndexes[0]);
	for (int i=1;i<wordIndexes.length;i++){
		if (wordIndexes[i]!=wordIndexes[i-1])
		 { 	
			target=Arrays.copyOf(target,target.length+1);
			target[j]=wordIndexes[i]; j++;
			}
		else{
			System.out.print("Stop");
		}
	}
	return target;
}
public void AddNegative_Relations() {
 for (int i=0; i<tr_candidates.size();i++){	
	for (int j=0;j<lm_candidates.size();j++){
		{		
			    FEx_BIONLP_BB_PerSentence_train_test_MoreGlobalBMC.statis.negLoc_negRoles++;
				discourse_annotations.add_new_relation();
				discourse_annotations.relations.lastElement().id1=tr_candidates.get(i).id;//bacteriumList.get(Bindex);
			    discourse_annotations.relations.lastElement().id2=lm_candidates.get(j).id;//habitatList.get(Hindex);
			    discourse_annotations.relations.lastElement().relationship="";
		      }
			}
		
	 }
  }
/////////////////////////////

public void writeChunckerTraining(PrintWriter pw){
	for (int i=0;i<allsentences.size();i++ )
	{
	for (int j=0;j< allsentences.elementAt(i).sentence_feat.size();j++)
	{
		pw.println(allsentences.elementAt(i).sentence_feat.elementAt(j).words.word+" "+allsentences.elementAt(i).sentence_feat.elementAt(j).words.pos+" "+ find_annotation(i,j));
	}
	pw.println();
		
		}
	}
private String find_annotation(int sen_ind, int tok_ind) {
	String ann="";
	
	for (int i=0;i<discourse_annotations.entities.size();i++){
	  phrase_annotations e=	discourse_annotations.entities.elementAt(i);
	  if (e.sentence_id==sen_ind)
	  {
	    if (e.wordIndexes[0]==tok_ind)
	    	ann="B-"+e.role.charAt(0);
	    else
	    	if (Utility.Util.contains_int(e.wordIndexes, tok_ind))
	    	  ann="I-"+e.role.charAt(0);	
	    
	    	
	  }
	}
	if (ann.isEmpty())
		ann="O";
	return ann;
}


}// end of class BIOdiscourse