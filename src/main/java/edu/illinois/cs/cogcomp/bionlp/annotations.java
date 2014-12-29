package edu.illinois.cs.cogcomp.bionlp;

import edu.illinois.cs.cogcomp.bionlp.Utility.TreeNode;
import edu.illinois.cs.cogcomp.bionlp.Utility.Util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Vector;

//import com.sun.org.apache.xpath.internal.operations.And;

public class annotations {

	//	public class phrase_annotations{
	//	   Vector <offsets> wordOffsets=new Vector<offsets>();	
	//	   String label=null;
	//	 }
	//public void add_annotations(phrase_annotations x){
	//discourse_ann.addElement(x);
	//	}
	public void add_new_Entity()
	{
		entities.addElement(new phrase_annotations());
	}
	public void add_new_relation(){
		relations.addElement(new relation_annotations());
	}
	Vector<phrase_annotations> entities=new Vector<phrase_annotations>();
	Vector <relation_annotations> relations=new Vector<relation_annotations>();
	//	public ArrayList<String> get_Efeatures(int entity_index){
	//		return entities.elementAt(entity_index).features;
	//	}
	public void add_Efeatures(int entity_index, BIOsentence s){
		{
			String whole_phrase="phrase_";
			String phrase="";
			String pre="";
			phrase_annotations e1=entities.elementAt(entity_index);
			Integer[] toks=e1.wordIndexes;	
			int head= e1.head_index;//FEx_BIONLP_BB_PerSentence_train_test.HeadW_with_index(toks, s.sentence_feat);
			for (int i=0;i< toks.length;i++)
			 {	
				    if (toks[i]==head)  pre="head_";
				//	if (e1.role.equals("Sp_candidate")){ 
					e1.features.add(pre+"txt_"+s.sentence_feat.elementAt(toks[i]).words.word);
					//e1.features.add("head_"+s.sentence_feat.elementAt(i).words.head);
					e1.features.add(pre+"pos_"+s.sentence_feat.elementAt(toks[i]).words.pos);
					e1.features.add(pre+"dprl_"+s.sentence_feat.elementAt(toks[i]).words.DPRL);
					e1.features.add(pre+"sub_"+s.sentence_feat.elementAt(toks[i]).words.subcategorization);
					e1.features.add(pre+"lemma_"+s.sentence_feat.elementAt(toks[i]).words.lemma);
					e1.features.add(pre+"cocoa_"+s.sentence_feat.elementAt(toks[i]).words.cocoa);
			    //   }
				//else
				//{
				//	    	if (i==0)
				//	    		 {
				//	    			e1.features.add("B_"+s.sentence_feat.elementAt(i).words.word);
				//	    		 	e1.features.add("B_"+s.sentence_feat.elementAt(i).words.pos);
				//	    		    e1.features.add("B_"+s.sentence_feat.elementAt(i).words.DPRL);
				//	    		    e1.features.add("B_"+s.sentence_feat.elementAt(i).words.subcategorization);
				//	    		    e1.features.add("B_"+s.sentence_feat.elementAt(i).words.SRL);
				//	    		    e1.features.add("B_"+s.sentence_feat.elementAt(i).words.lemma);
				//	    		    e1.features.add("B_P_"+s.sentence_feat.elementAt(i).words.PDPRL);
				//	    		    }
				//	    		else
				//	    			
				//	    			if (i==toks.length-1)
				//	    				{
				//	    					e1.features.add("E_"+s.sentence_feat.elementAt(i).words.word);
				//	    					e1.features.add("E_"+s.sentence_feat.elementAt(i).words.word);
				//	    	    	    	e1.features.add("E_"+s.sentence_feat.elementAt(i).words.pos);
				//	    	    		    e1.features.add("E_"+s.sentence_feat.elementAt(i).words.DPRL);
				//	    	    		    e1.features.add("E_"+s.sentence_feat.elementAt(i).words.subcategorization);
				//	    	    		    e1.features.add("E_"+s.sentence_feat.elementAt(i).words.SRL);
				//	    	    		    e1.features.add("E_"+s.sentence_feat.elementAt(i).words.lemma);
				//	    	    		    e1.features.add("E_P_"+s.sentence_feat.elementAt(i).words.PDPRL);
				//	    				}
				//	    	    		else
				//	    	    			{
				//	    	    			  e1.features.add("M_"+s.sentence_feat.elementAt(i).words.word);
				//	    	    			  e1.features.add("M_"+s.sentence_feat.elementAt(i).words.word);
				//		    	    	      e1.features.add("M_"+s.sentence_feat.elementAt(i).words.pos);
				//		    		          e1.features.add("M_"+s.sentence_feat.elementAt(i).words.subcategorization);
				//		    		          e1.features.add("M_"+s.sentence_feat.elementAt(i).words.SRL);
				//		    		          e1.features.add("M_"+s.sentence_feat.elementAt(i).words.lemma);
				//		    		          e1.features.add("M_P_"+s.sentence_feat.elementAt(i).words.PDPRL);
				//		    		          }
			//	e1.features.add("txt_"+s.sentence_feat.elementAt(toks[i]).words.word); 
			//	e1.features.add("cocoa_"+s.sentence_feat.elementAt(toks[i]).words.cocoa);
				whole_phrase=whole_phrase+"_"+s.sentence_feat.elementAt(toks[i]).words.word;
			//	phrase=phrase+" "+s.sentence_feat.elementAt(toks[i]).words.word;
			//	}
			}
				      if (toks.length>1)  e1.features.add(whole_phrase); 
				      String pos=Phrase_Pos(e1.wordIndexes,s.parseTree);
				      e1.features.add("phPos_"+pos);
			          boolean[] a=match_NCBI(phrase);
				      e1.features.add("NCBIM_"+a[0]);
				      e1.features.add("NCBIO_"+a[1]);
				      a=match_OBO(phrase);
				      e1.features.add("OBOM_"+a[0]);
				      e1.features.add("OBOO_"+a[1]);
		}
	}
////////////
//  Add Relation Features
/////////////
	public void add_Rfeatrues(int e1Index,int e2Index,int relIndex, Vector<BIOsentence> alls, int par_num, int[][] Coref){
		//String before="";

		relation_annotations r=relations.elementAt(relIndex);
		phrase_annotations e1=entities.elementAt(e1Index);
		phrase_annotations e2= entities.elementAt(e2Index);
		///
		int sen_id1=entities.elementAt(e1Index).sentence_id;
		BIOsentence s1=new BIOsentence(alls.elementAt(sen_id1));
		int sen_id2=entities.elementAt(e2Index).sentence_id;
		BIOsentence s2=new BIOsentence(alls.elementAt(sen_id2));
		TreeNode p1=Phrase_node(e1.wordIndexes,s1.parseTree);
		TreeNode p2=Phrase_node(e2.wordIndexes,s2.parseTree);


		///////////////////////////////
		//    Bacterium head features
		//////////////////////////////

		//int B_head= HeadW_with_index(e1.wordIndexes,s1.sentence_feat);
		//r.fe6atures.add("B_head_txt_"+ s1.sentence_feat.elementAt(B_head).words.word);
		//r.features.add("B_head_lem_"+ s1.sentence_feat.elementAt(B_head).words.lemma);
		//r.features.add("B_head_dprl_"+ s1.sentence_feat.elementAt(B_head).words.DPRL);
		//r.features.add("B_head_pos_"+ s1.sentence_feat.elementAt(B_head).words.pos);
		//r.features.add("B_head_coco_"+ s1.sentence_feat.elementAt(B_head).words.cocoa);
		///////////////////////////////
		//    Habibtat head features
		//////////////////////////////
		//int H_head= HeadW_with_index(e2.wordIndexes,s2.sentence_feat);
		//r.features.add("H_head_txt_"+ s2.sentence_feat.elementAt(H_head).words.word);
		//r.features.add("H_head_lem_"+ s2.sentence_feat.elementAt(H_head).words.lemma);
		//r.features.add("H_head_dprl_"+ s2.sentence_feat.elementAt(H_head).words.DPRL);
		//r.features.add("H_head_pos_"+ s2.sentence_feat.elementAt(H_head).words.pos);
		//r.features.add("H_head_coco_"+ s2.sentence_feat.elementAt(H_head).words.cocoa);
		//int parDistance=0;
		//if (s2.description_number!=s1.description_number)
		//if (r.relationship!=null)
		//if (r.relationship.equals("Tr_Sp")||r.relationship.equals("Lm_Sp"))
		// {
		//int B_head= HeadW_with_index(e1.wordIndexes,s1.sentence_feat);
		//r.features.add("B_head_txt_"+ s1.sentence_feat.elementAt(B_head).words.word);
		//r.features.add("B_head_lem_"+ s1.sentence_feat.elementAt(B_head).words.lemma);
		//r.features.add("B_head_dprl_"+ s1.sentence_feat.elementAt(B_head).words.DPRL);
		//r.features.add("B_head_pos_"+ s1.sentence_feat.elementAt(B_head).words.pos);

		//r.features.add("B_head_coco_"+ s1.sentence_feat.elementAt(B_head).words.cocoa);
		//	if (sen_id1==sen_id2){
		//	    r.features.add("path_"+p1.getPath(p2));
		//		if (p1.getPathDistance(p1, p2)!=0)
		//		  r.features.add("dist_"+Integer.toString(alls.elementAt(sen_id1).parseTree.getNumberOfNodesInTree()/p1.getPathDistance(p1, p2)));	
		//		}	    
		//	return;
		// }



		int parDistance=(s2.description_number-s1.description_number);
		r.features.add("parDistance_"+parDistance);
		if (Util.contains_int_int(Coref, e1Index + 1))
			r.features.add("B_has_Coref");
		for (int i=0;i<Coref.length;i++)
		{
			if (Coref[i][0]==e1Index+1)
			{ 
				r.features.add("B_first_mention");
				break;
			}
		}

		///

		if (e1.sentence_id==0)
			r.features.add("title_B");
		if ((e1.content(s1).toLowerCase().trim()).contains(e2.content(s2).toLowerCase().trim())) // content is also computed here if it has not before.
			r.features.add("contains_1");
		if (e1.sentence_id<e2.sentence_id)
			r.features.add("before_1");
		if (s1.title==true)
		{
			r.features.add("title_B");	
		}
		if (s1.title==true &s1.description_number==1){
			r.features.add("title_B0"+s2.description_number);
		}

		if (s2.description_number==s1.description_number & s1.title==true)
			r.features.add("Same_Par_title_B");
		boolean Capital=Character.isUpperCase(s1.sentence_feat.elementAt(e1.wordIndexes[0]).words.word.charAt(0));
		r.features.add("Cap_"+Capital);
		//			String lex="Bphtex";
		//			String pos="Bphpos";
		//			for (int i=0;i<e1.wordIndexes.length;i++)
		//			{
		//				String txt=s1.sentence_feat.elementAt(e1.wordIndexes[i]).words.lemma;
		//				lex=lex+"_"+txt;//word.toLowerCase();
		//				r.features.add("Btxt_"+txt);
		//
		//				String wpos=s1.sentence_feat.elementAt(e1.wordIndexes[i]).words.pos;
		//				pos=pos+"_"+wpos;
		//				r.features.add("Bwpos_"+wpos);
		//
		//				String dprl="B_Dprl_"+s1.sentence_feat.elementAt(e1.wordIndexes[i]).words.DPRL;
		//				r.features.add(dprl);
		//
		//				String subcat="B_subcat_"+s1.sentence_feat.elementAt(e1.wordIndexes[i]).words.subcategorization;
		//				r.features.add(subcat);
		//			}
		//			lex=lex+"_H";
		//			pos=pos+"_H";
		//			for (int i=0;i<e2.wordIndexes.length;i++){
		//				String txt=s2.sentence_feat.elementAt(e2.wordIndexes[i]).words.lemma;
		//				lex=lex+"_"+txt;//.word.toLowerCase();
		//				r.features.add("Htxt_"+txt);
		//
		//				String wpos=s2.sentence_feat.elementAt(e2.wordIndexes[i]).words.pos;
		//				pos=pos+"_"+wpos;
		//				r.features.add("Hwpos_"+wpos);
		//
		//				String dprl="H_Dprl_"+s2.sentence_feat.elementAt(e2.wordIndexes[i]).words.DPRL;
		//				r.features.add(dprl);
		//				String subcat="H_subcat_"+s2.sentence_feat.elementAt(e2.wordIndexes[i]).words.subcategorization;
		//				r.features.add(subcat);
		//
		//			}
		//			r.features.add(lex);
		//			r.features.add(pos);

		///////
		String t1=p1.getLabel();
		String t2=p2.getLabel();
		r.features.add("BPos_"+t1+"_HPos_"+t2);
		r.features.add("Hdlem_"+s1.sentence_feat.elementAt(e1.head_index).words.lemma+"_Hdlem_"+s2.sentence_feat.elementAt(e2.head_index).words.lemma);
		r.features.add("H_"+e1.phrase_content.trim());
		r.features.add("B_"+e2.phrase_content.trim());
		///////
		if (r.relationship!=null)   
		{
			if (r.relationship.toLowerCase().equals("localization"))

			{
				if (e1.sentence_id==e2.sentence_id)
					FEx_BIONLP_BB_PerSentence_train_test_MoreGlobalBMC.statis.SentenceLocRelations++; 
				else 
					FEx_BIONLP_BB_PerSentence_train_test_MoreGlobalBMC.statis.discourseLocRelations++; 

				if (e1.coreferences!=null)
					for (int c=0;c<e1.coreferences.length;c++)
						if( entities.elementAt(e1.coreferences[c]-1).sentence_id==sen_id2)
							FEx_BIONLP_BB_PerSentence_train_test_MoreGlobalBMC.statis.SentenceCorefLocRelations++; 
				//A bacterium co-reference which is in the same sentence with the habitat of the annotated localization.
			}
		}
////////////////////// Sentence Level features 
		
		if (e1.sentence_id==e2.sentence_id)
		{   

			r.features.add("same_senId");
			for (int i=e2.wordIndexes[0];i>0;i--)
			{ 
				ling_features.word_features w =s1.sentence_feat.elementAt(i).words;
				if (w.pos.contains("IN")| w.pos.contains("IN"))
				{
					r.features.add("P_"+w.lemma);//.word);
				}
				if (w.pos.contains("VB")| w.pos.contains("VB")){
					r.features.add("V_"+w.lemma);//.word);
					r.features.add("V_"+w.cocoa);
					break;
				}
			}
			r.features.add("path_"+p1.getPath(p2));
			if (p1.getPathDistance(p1, p2)!=0)
				r.features.add("dist_"+Integer.toString(alls.elementAt(sen_id1).parseTree.getNumberOfNodesInTree()/p1.getPathDistance(p1, p2)));		    
			r.features.add("depPat_"+s1.sentence_feat.elementAt(e1.head_index).word_word.deppath.elementAt(e2.head_index));
			// if (e1.wordIndexes[0]<e2.wordIndexes[0])
			//  r.features.add("before_1");
		} /// end sentence level features 

		//else
		//r.features.add("diff_senId");


		//Integer[] toks=e1.wordIndexes;	
		//if e1 happens before e2.

		/*	if e1 is described e2 happens in the description of e1.

		if e1 comes after the description that contains e2.
		if e1 is-a title entity.
		e1 And e2 are in the same sentence.
		connecting verb between e1 and e2. 
		connectin preposition between e1 and e2.
		e2 is in the description of e1.
		e2 is in the last description and e1 is the main entity.

		relations.elementAt(relation_index).id1
		relations.elementAt(relation_index).id2
		relations.elementAt(relation_index).features.add()
		 */
	}

	public boolean[] match_NCBI(String phrase){
		
		
			
		return (FEx_BIONLP_BB_PerSentence_train_test_MoreGlobalBMC.NCBIRep.match_Bacteria(phrase));
		
		}
	
	public boolean[] match_OBO(String phrase){
		
		/*for (int it=0;it<allsentences.size();it++){
			
			//for (int w=0; w<allsentences.elementAt(it).sentence_feat.size();w++){
		   Integer[] indices=obRep.getIndexesOfExistingWords(allsentences.elementAt(it).sentence_feat);
	           if (indices.length!=0)
	        	 for(int entitylist=0;entitylist<indices.length;entitylist++)  
	              {  
	        		 iEntity e1=new iEntity();
	                 Integer[] listindic={indices[entitylist]};
	        		 e1.add_features(allsentences.elementAt(it),listindic);
	                 allsentences.elementAt(it).lm_candidates.add(e1);}*/
		
		//}
		return (FEx_BIONLP_BB_PerSentence_train_test_MoreGlobalBMC.obRep.match_Habitat(phrase));
	}
   public static String Phrase_Pos(Integer[] entity, TreeNode top)
		  {   
			return Phrase_node(entity,top).getLabel();
		   }
	public static TreeNode Phrase_node(Integer[]entity, TreeNode top){
		Vector<TreeNode> e=new Vector<TreeNode>();  
		TreeNode p=null;
		for (int i=0; i<entity.length;i++)
		{	
			e.add(top.getLeafNodes().elementAt(entity[i]));
		}
		p=top.getCommonParent(e);  
		return p;
	}
	public String[] allhabitatsOf(String id) {
		// TODO Auto-generated method stub
		String[] habitats={};
		for (int j=0; j<relations.size();j++)
		{
			if (relations.elementAt(j).id1.equals(id)&& relationType(id,relations.elementAt(j).id2).equals("Localization")){
				habitats=Arrays.copyOf(habitats, habitats.length+1);
				habitats[habitats.length-1]=relations.elementAt(j).id2;
			}
		}
		return habitats;
	}
	public String relationType(String id1, String id2)
	{
		for (int j=0; j<relations.size();j++)
			if (relations.elementAt(j).id1.equals(id1)&& relations.elementAt(j).id2.equals(id2))
				return relations.elementAt(j).relationship;

		return null;
	}
	public static int HeadW_with_index(Integer[] wordindexes,Vector<ling_features> words){
		///Vector<TreeNode> xs=x.getLeafNodes();
		//Vector <TreeNode> ch=t.getLeafNodes();

		//int index1=-1;
		//		for(int i=0;i<ch.size(); i++)
		//		{ 
		//			TreeNode xtemp=xs.elementAt(0);
		//			TreeNode TreeTemp=ch.elementAt(i);
		//			while ( xtemp!=null & TreeTemp!=null ){
		//				if(xtemp.equals(TreeTemp)) {
		//				xtemp=xtemp.getParent();
		//				TreeTemp=TreeTemp.getParent();
		//				if (xtemp==null & TreeTemp==null){ index1=i; break;}
		//				}
		//				else break;}
		//			if (index1!=-1) break;
		//		}

		//		Vector <String> cons=x.getAllWords();
		int consarray[][];
		consarray = new int[wordindexes.length][2]; 
		for (int i2=0;i2<wordindexes.length;i2++){
			consarray[i2][0]=wordindexes[i2];
			consarray[i2][1]=Integer.parseInt(words.elementAt(wordindexes[i2]).words.head)-1;
		}
		//       
		if (wordindexes.length==1){
			return consarray[0][0];}
		boolean flag=true;
		int index=0;

		for(int i1=0;i1<consarray.length;i1++)
		{   flag=true;
		for(int i2=0;i2<consarray.length  ;i2++)

		{  
			if(i1!=i2){
				if (consarray[i1][1]==consarray[i2][0])
				{flag=false;break;}
			}

		}// for i2
		if (flag==true)
		{
			index=consarray[i1][0];  break;}
		}// for i1
		return index;
	}
}// 
 

 