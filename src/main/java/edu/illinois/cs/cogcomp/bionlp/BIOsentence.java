package edu.illinois.cs.cogcomp.bionlp;//package GeneralData_preprocess;
import edu.illinois.cs.cogcomp.bionlp.Utility.TreeNode;
import edu.illinois.cs.cogcomp.bionlp.Utility.TreeRepresentation;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collections;
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.ArrayList;

import static edu.illinois.cs.cogcomp.bionlp.BIOdiscourse.isInteger;
import static edu.illinois.cs.cogcomp.bionlp.Utility.Util.find_tree_indexes;


public class BIOsentence {
	////////////////////
//    public boolean  check_sourcefiles(BufferedReader lth_file, BufferedReader charniak_file) throws IOException{
//    	boolean a=true;
//    	System.out.println(content);
//    	while(lth_file.ready())
//    		System.out.println(lth_file.readLine());
//    	while (charniak_file.ready())System.out.println(charniak_file.readLine());
//    	charniak_file.close();
//    	lth_file.close();
//    	return a;
//    };
	public BIOsentence(BIOsentence x){
		sentence_feat=x.sentence_feat;  
		content=x.content;
		parseTree=x.parseTree;
		description_number=x.description_number;
		title=x.title;
	}
	public BIOsentence(){}
	public boolean isInrelations(nonrelation x){
		for(int i=0; i<relations.size();i++){
		if (relations.elementAt(i).wh_tr.head_index==-1) relations.elementAt(i).wh_tr.head_index=sentence_feat.size();
		if (relations.elementAt(i).wh_lm.head_index==-1) relations.elementAt(i).wh_lm.head_index=sentence_feat.size();
	 	
		 if (x.lm==relations.elementAt(i).wh_lm.head_index && x.tr==relations.elementAt(i).wh_tr.head_index&&x.sp==relations.elementAt(i).wh_sp.head_index)
		   return true;}
		
		return false;
	}
	
	public int[][] make_similarity_matrix(){
		if (relations.size()!=0 & allnonrel.size()!=0){
		int[][] sim= new int[relations.size()][allnonrel.size()];
		for (int i=0; i<relations.size();i++)
			for(int j=0; j<allnonrel.size();j++)
			{
			 if (relations.elementAt(i).wh_sp.head_index==allnonrel.elementAt(j).sp){sim[i][j]=sim[i][j]+2;}
			 if (relations.elementAt(i).wh_lm.head_index==allnonrel.elementAt(j).lm){sim[i][j]=sim[i][j]+2;}
			 if (relations.elementAt(i).wh_tr.head_index==allnonrel.elementAt(j).tr){sim[i][j]=sim[i][j]+2;}
			 if (relations.elementAt(i).wh_tr.head_index==allnonrel.elementAt(j).lm){sim[i][j]=sim[i][j]+1;}
			 if (relations.elementAt(i).wh_lm.head_index==allnonrel.elementAt(j).tr){sim[i][j]=sim[i][j]+1;}
			}
		
		return sim;
		}
		else
	   	return null;
	}
	
//	int maxrow(int [] a){
//		int maxi=-1;
//		for (int i=0;i<a.length;i++)
//		{ 
//			if (a[i]>maxi)
//			maxi=a[i];}
//            return maxi;}
	
	int[] maxim(int [][] a, int n){ //return the index of n maximum numbers of array a
		int [] b=new int[a[0].length];
		for(int j=0;j<a[0].length;j++)
		{	
		 int	max=-1;
		for (int i=0;i<a.length;i++)
		{ if (a[i][j]<max)
			b[j]=max;
		else {b[j]=a[i][j]; max=b[j];}
		}}
		int [] m=new int[n+1];
		//n=n-1;
		if (b.length!=0){
		int max=0;
		int index=0;
		for (int j=0;j<n;j++){
			max=-1;
		for (int i=0;i<b.length;i++){
			if (b[i]>max)
				{
				 max=b[i];
			     index =i;
			     }
				}
				m[j]=index; 
				b[index]=-1;
		     }}
		if (n!=0 & b.length!=0)
		   m[n]=-1;
		//else 
	//		m[0]=-1;
		return m;
	  }
	public void selective_negatives(){ 
		int[][] sim_matrix=make_similarity_matrix();// this computes the similarity between negatives and all positives
		if (sim_matrix!=null){
		  //for (int j=0;j<sim_matrix.length;j++) 
			 // {
				int [] m=maxim(sim_matrix,sim_matrix[0].length);// the relation which has the maximum similarity with one of the positives has been chosen
		        for (int mind=0; m[mind]!=-1;mind++)// 
		    	selectedNegatives.addElement(m[mind]);// the index of negatives are added to the selected negatives 
		        //in the begining this was written to select negatives but now it just ranks negatives based on their similarity to positives
		       //} 
		   }
		else 
		{
			if (allnonrel.size()>0) selectedNegatives.addElement(0);
		  }
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
			if (Rows.size()>=10)
				Feature_Matrix.addElement(Rows);
		}
		return Feature_Matrix;}
	////////////////// End of building a matrix  over the information given by  lth semantic role labeler////
	public TreeNode fetch_char(BufferedReader brChar) throws IOException{
		TreeNode top=null;
		String sen1=brChar.readLine();
		System.out.println(sen1);
		if (sen1.toUpperCase()==sen1){sen1=brChar.readLine();}
		if (sen1.toUpperCase() != sen1) {
			top = TreeRepresentation.readTree(sen1);
		}
		return top;
	}
	////////////// End of reading the charniak tree ////////////////////

	public Vector <String> type_tok( String s,String c){
		StringTokenizer st = new StringTokenizer(s,c);
		Vector<String> t=new Vector <String>();
		while (st.hasMoreTokens())
		{
			String S = st.nextToken();
			t.addElement(S);

		}
		return t;
	}// end of tokenization public method
	///////////////////////
	public class phrase{
		Vector <String> ph=new Vector<String>();
		String ph_head=null;
		int head_index=0;
        
		public int index_tune(int wrongindex, String input){
			int offset=0;
        	String temp=input.trim();
        	//temp=temp.replaceAll("[\\s][\\s]*"," ");
        	
        	Vector <String> t=type_tok(temp," ");
        	if(ph.elementAt(0).trim().toLowerCase().equals("others")){
        		System.out.print("stop");
        	}
        	for (int wt=0; wt<=(wrongindex+offset); wt++){
        		if(t.elementAt(wt).contains(",")|| t.elementAt(wt).contains("?")||t.elementAt(wt).contains("(")||t.elementAt(wt).contains(")")) offset++;
        	}
        		
        	if (!t.elementAt(wrongindex+offset).trim().toLowerCase().equals(ph_head.trim().toLowerCase())){
        		System.out.print(ph);
        	}
        	return (wrongindex+offset);
          }
		public void set_ph(String p,String h){
			h=h.replaceAll("\"", "");
			if (h.replaceAll(" ","").equals("_")){
			ph_head="undefined";
			head_index=-1;
			}
			else{
			ph_head=h;
			p=p.trim();
			p=p.replaceAll("[\\s][\\s]*"," ");
			p=p.replaceAll("\"", "");
			ph=type_tok(p," ");
			String ind="";
			if(h.contains("_")){
				ph_head=h.substring(0,h.indexOf("_"));
				ind=h.substring(h.indexOf("_")+1);}
			ind=ind.replaceAll( "[^\\d]", "" );
			if(isInteger(ind))	{
				head_index=Integer.parseInt(ind)-1;
				head_index=index_tune(head_index, content);}
			else System.out.println("warning: no index determined");

		}// end else
			}// end set phrase
	}// end definition of phrase class
	////////////////////////////////////
	public class calculi{
		Vector <String> general=new Vector<String>();
		Vector <String> specific=new Vector<String>();
		Vector <String> sp_value=new Vector<String>();
         int[] flat_output=new int[16]; 
		public void set_calc(String g,String s, String v){
			general=type_tok(g,"/");
			specific=type_tok(s,"/");
			sp_value=type_tok(v,"/");
			
		}
	}
	////////////////end definition calculi class
	public class nonrelation{
		int tr=0; int lm=0; int sp=0;
	}
	public class relation {
		phrase wh_tr=new phrase();
		phrase wh_lm=new phrase(); 
		phrase wh_sp=new phrase();
		phrase wh_motion=new phrase();
		boolean dynamic=false;
		String path=null;
		String FrORe=null;
		calculi calc_type=new calculi();
		String GUM_mod=null;
		
		////////////////////////// read  one relation's elements ///////////////
		public void  read_rel_map(Vector<String> row){
			wh_tr.set_ph(row.elementAt(7), row.elementAt(7));
			wh_lm.set_ph(row.elementAt(17), row.elementAt(17));	
			wh_sp.set_ph(row.elementAt(15),row.elementAt(15));
			wh_motion.set_ph(row.elementAt(9),row.elementAt(9));
			calc_type.set_calc("_","_",row.elementAt(0));
			//if (row.elementAt(12).toLowerCase().contains("static")) dynamic=false; else dynamic=true;
			//path=row.elementAt(13);
			//FrORe=row.elementAt(14);
			GUM_mod=row.elementAt(14);
		}
//////////////////////////read  one relation's elements ///////////////
		public void  read_rel(Vector<String> row){
			wh_tr.set_ph(row.elementAt(1), row.elementAt(5));
			wh_lm.set_ph(row.elementAt(2), row.elementAt(6));	
			wh_sp.set_ph(row.elementAt(3),row.elementAt(7));
			wh_motion.set_ph(row.elementAt(4),row.elementAt(8));
			calc_type.set_calc(row.elementAt(9),row.elementAt(10),row.elementAt(11));
			flatten_sp(calc_type,1);
			if (row.elementAt(12).toLowerCase().contains("static")) dynamic=false; else dynamic=true;
			path=row.elementAt(13);
			FrORe=row.elementAt(14);
			GUM_mod=row.elementAt(15);
			 }
		public void  read_rel_Room(Vector<String> row){
			wh_tr.set_ph(row.elementAt(3), row.elementAt(3));
			wh_lm.set_ph(row.elementAt(6), row.elementAt(6));	
			wh_sp.set_ph(row.elementAt(5),row.elementAt(5));
			//wh_motion.set_ph(row.elementAt(9),row.elementAt(9));
			calc_type.set_calc("_","_",row.elementAt(2));
			//if (row.elementAt(12).toLowerCase().contains("static")) dynamic=false; else dynamic=true;
			//path=row.elementAt(13);
			//FrORe=row.elementAt(14);
			GUM_mod=row.elementAt(4);
		}
  }

	///////////////end definition relation class
	
	/////////////////// begin of potential pivot class definition/////////////
	public class potential_pivots_class{
		Vector <phrase> tpp_prepositions=new Vector <phrase>();
		Vector <phrase> non_tpp_prepositions= new Vector <phrase>();
		Vector <phrase> motion_verbs=new Vector <phrase>();
		Vector <phrase> spatial_nouns = new Vector<phrase>();
	}
	///////////////////end of potential pivot class definition //////////////

	public String content;
	public Vector<phrase_annotations> sen_chuncks=new Vector<phrase_annotations>();
	public String[] chunks={};
	public boolean title; 
	public TreeNode parseTree;
	public Vector <relation> relations= new Vector<relation>();
	public Vector<nonrelation> nonrelations=new Vector<nonrelation>();
	public Vector <Integer> selectedNegatives=new Vector <Integer>();
	public Vector <nonrelation> allnonrel=new Vector<nonrelation>();
	public Vector <nonrelation> allPosRelCandids=new Vector<nonrelation>();
	Vector<ling_features> sentence_feat=new Vector<ling_features>();
	int description_number=0;
	

//	annotations sentence_t1a1_ann=new annotations(); 
	//Vector<Vector<String>> lth_matrix=new Vector<Vector<String>>();
	//TreeNode char_tree=null;
	potential_pivots_class potential_pivots=new potential_pivots_class();
	Vector <Integer> non_SP_tpps=new Vector<Integer>();
	Vector <Integer> non_tr=new Vector<Integer>();
	Vector <Integer> non_lm=new Vector<Integer>();
	ArrayList <iEntity> tr_candidates=new ArrayList<iEntity>();
	ArrayList <iEntity> lm_candidates=new ArrayList<iEntity>();
    Vector <Integer> sp_candidates=new Vector<Integer>();
     
	///////////////////// produce negative relations/////////////
//	public void fetch_allnonrel(){
//		 Vector <nonrelation> candidates= new Vector<nonrelation>();
//		 for (int i=0; i<sp_candidates.size();i++)
//			 for(int j=0;j<tr_candidates.size();j++)
//				 for (int k=0;k<lm_candidates.size();k++){
//					      nonrelation temp=new nonrelation();
//						  temp.sp=sp_candidates.elementAt(i);
//					      temp.tr=tr_candidates.elementAt(j);
//					      temp.lm=lm_candidates.elementAt(k);
//					      if (!isInrelations(temp) & temp.lm!=temp.tr& temp.lm!=temp.sp&temp.tr!=temp.sp){candidates.addElement(temp);}
//					 }
//		 for(int j=0;j<candidates.size();j++)
//			{
//			allnonrel.addElement(candidates.elementAt(j));}
//		    selective_negatives();
//	    }
//	public void fetch_nonrel(){
//	    Vector <nonrelation> candidates= new Vector<nonrelation>();
//		
//		for(int a=0;a<non_SP_tpps.size();a++)
//			for (int b=0;b<non_tr.size();b++)
//				for (int c=0; c<non_lm.size();c++)
//				   if((non_SP_tpps.elementAt(a)!=non_tr.elementAt(b)) && (non_tr.elementAt(b)!=non_lm.elementAt(c))&& (non_SP_tpps.elementAt(a)!=non_lm.elementAt(c) ))
//				     {
//					  nonrelation temp=new nonrelation();
//					  temp.sp=non_SP_tpps.elementAt(a);
//				      temp.tr=non_tr.elementAt(b);
//				      temp.lm=non_lm.elementAt(c);
//				      candidates.addElement(temp);
//				      }
//		//int j=0;
//		for(int j=0;j<candidates.size();j++)
//			{
//			 nonrelations.addElement(candidates.elementAt(j));
//			 }
//		   // selective_negatives();
//			 
//	 }
	
	public void fetch_prunedrel(int count){
		int p=0;
		for (int i=0;i<nonrelations.size();i++)
		  {
			
			count=count+ p;
		   }
		}
	
  public void prun_empty_relations(){
	 
	 for (int r=0; r<relations.size();){
		 if(relations.elementAt(r).wh_sp.head_index==-1)
			 relations.removeElementAt(r);
		 else r++;}
	 
 }
  //**********************Get-positive relations that the arguments are candidates*******
  public void fetch_allPosRelCandids(int j){//, PrintWriter p){
	  for (int i=0;i<relations.size();i++){
		  if(relations.elementAt(i).wh_lm.head_index==-1)
			  relations.elementAt(i).wh_lm.head_index=sentence_feat.size();
		  if (relations.elementAt(i).wh_tr.head_index==-1)
			  relations.elementAt(i).wh_tr.head_index=sentence_feat.size();
		  if ( lm_candidates.contains(relations.elementAt(i).wh_lm.head_index)& tr_candidates.contains(relations.elementAt(i).wh_tr.head_index)& sp_candidates.contains(relations.elementAt(i).wh_sp.head_index))
		    {
			  nonrelation temp=new nonrelation();
			  temp.sp=relations.elementAt(i).wh_sp.head_index;
		      temp.tr=relations.elementAt(i).wh_tr.head_index;
		      temp.lm=relations.elementAt(i).wh_lm.head_index;
		      allPosRelCandids.addElement(temp);
		      }
			// else 
           //  if(relations.elementAt(i).wh_sp.head_index!=-1)
			//	 p.println(this.content+"\\sentence"+j+"\\lm "+relations.elementAt(i).wh_lm.head_index+relations.elementAt(i).wh_lm.ph_head+"\\tr "+ relations.elementAt(i).wh_tr.head_index+"\\"+relations.elementAt(i).wh_tr.ph_head+"\\sp"+relations.elementAt(i).wh_sp.head_index+"\\"+relations.elementAt(i).wh_sp.ph_head);
		    }
        }
  
  
	//***********************Get-relations*******************************//
	public void fetch_rel(Vector<Vector<String>> Rows, int i){
		/* This method extracts one sentence and its relations from the list of all the sentences.*/
		if (Rows.elementAt(i).size()<15) {System.out.println("warning: this row"+i+" does not contain a full sentence!!");}
		else{
			content=Rows.elementAt(i).elementAt(0);
			content=content.trim();
			content=content.replace(","," , ");// make comma counted as a  word
			content=content.replace("("," ( ");
			content=content.replace("?"," ? ");    
			   
			content=content.replace(")"," ) ");
			content=content.replaceAll("[\\s][\\s]*", " ");
			content=content.replaceAll("\"", "");
			if (content.contains("a wooden house with green window frames") ){
				System.out.print("Stop");
			}
			do{
				relation r=new relation();
				if (Rows.elementAt(i).size()==15)
					Rows.elementAt(i).add(0, " ");
				r.read_rel(Rows.elementAt(i));
			//	if (r.wh_sp.head_index!=-1)
				  relations.addElement(r);	
				i++;	
			}while(Rows.elementAt(i).size()!=16&& !Rows.elementAt(i).elementAt(0).contains("*"));
		}//end else
	}// end fetch
	
	
	
	//***********************************************************************//
	//*******************extract the linguistic features of the sentence****//
	public void assign_lth_f(Vector<Vector<String>> lth_m){
		for (int i=0; i<lth_m.size();i++ ){
			ling_features f=new ling_features();
			f.words.word=lth_m.elementAt(i).elementAt(1);
			f.words.lemma=lth_m.elementAt(i).elementAt(2);
		//	f.words.Cpos=lth_m.elementAt(i).elementAt(3);
			f.words.pos=lth_m.elementAt(i).elementAt(4);
			f.words.DPRL=lth_m.elementAt(i).elementAt(7);
			//f.words.SRL=build_RelDB.SRL(i,lth_m);
		//	f.words.PDPRL=lth_m.elementAt(i).elementAt(9);
			sentence_feat.addElement(f);  		
		}
	}
	///////////////////////////

	public void assign_tree_f(TreeNode charniak, Vector<Vector<String>> lth_matrix){

		Vector<TreeNode> Nodes_ = charniak.getAllNodesOfTree(); 
		Vector <TreeNode> Sentence=charniak.getLeafNodes();
		int wordNum=Sentence.size();	
		int nodNum = charniak.getNumberOfNodesInTree();
		int[] Tree_indexes=new int[wordNum];
		Tree_indexes=find_tree_indexes(Nodes_, lth_matrix);
		for (int w=0;w<wordNum;w++){
			String wordsubcat="";
			TreeNode tem=Nodes_.elementAt(Tree_indexes[w]).getParent();
			if(tem!=null){
				TreeNode[] childeren1 =tem.getChildren();
				for(int i1=0; i1<childeren1.length;i1++)
					wordsubcat= wordsubcat+childeren1[i1].getLabel()+"_"; 
			}// end if parent is not null
			sentence_feat.elementAt(w).words.subcategorization=wordsubcat;
			for (int w_2=0;w_2<wordNum;w_2++){
				sentence_feat.elementAt(w).word_word.path.addElement(Nodes_.elementAt(Tree_indexes[w]).getPath(Nodes_.elementAt(Tree_indexes[w_2]))); 
				int distance=TreeNode.getPathDistance(Nodes_.elementAt(Tree_indexes[w_2]), Nodes_.elementAt(Tree_indexes[w]));
				sentence_feat.elementAt(w).word_word.distance.addElement(distance);
				if (distance!=0)
					distance= (nodNum)/distance;
				sentence_feat.elementAt(w).word_word.deppath.addElement(depPath(w,w_2));
			}
		}//end for every word find subcategoriazation
	}
	
	/////////////////////////
	public  boolean sp_annotated(int i){
		boolean flag=false;
		for(int r=0;r<relations.size();r++)
			if (relations.elementAt(r).wh_sp.head_index==i) 
			 {flag=true;break;}
		return flag;
		}
	
	public void build_nonTR_LM(){ 	// This procedure builds the list of none trajector and none landmarks among the words // with NN and NNS part of speech tags.  
		for (int w=0; w<sentence_feat.size();w++)
		 {  
			boolean flag1=false, flag2=false;
			 for(int r=0;r<relations.size();r++)
			    {
			     if (relations.elementAt(r).wh_tr.head_index==w) 
			          flag1=true; 
			     if (relations.elementAt(r).wh_lm.head_index==w) 
				     flag2=true;
				 }
		if (sentence_feat.elementAt(w).words.pos.toLowerCase().contains("nn")|| sentence_feat.elementAt(w).words.pos.toLowerCase().contains("nns")||sentence_feat.elementAt(w).words.pos.toLowerCase().contains("prp"))
		{	
		 if (!flag1) non_tr.add(w); 
		 if (!flag2) non_lm.add(w);
		 }// end if noun then add 
		   }// for each word
       }
    /////////End of building non role words for the three roles of trajector, landmark //////
	
	/////////////////////////////////////////////
	/*public  Vector <phrase> tpp_prepositions_index(String input) 
		{  
        
	//input=input.replace("\"", "");
		//input=input.trim();
		String T[]=input.split(" ");
		Vector <phrase> a1=new Vector<phrase>();
		
		for(int i=0;i<T.length;i++)
		{
			if (build_RelDB.find_(build_RelDB.Prepositions,T[i]))
			{ 
				if (!sp_annotated(i)) non_SP_tpps.add(i);
				phrase t=new phrase();
				t.head_index=i;
				t.ph_head=T[i].toLowerCase();
				t.ph.addElement(T[i].toLowerCase());
				a1.add(t);
			}
		}
		return a1;
	}*/
	
	
	
	public String depPath(int w1,int w2){
		String result ="";
		if (w1==w2) return result;
		if (sentence_feat.elementAt(w1).words.head.equals("0"))
			  result=(depPath(w2,w1));
		else
		  if (!sentence_feat.elementAt(w1).words.head.equals(Integer.toString(w2+1)))
		     result=(depPath(Integer.parseInt(sentence_feat.elementAt(w1).words.head)-1,w2));
			
	   result+="_"+(sentence_feat.elementAt(w1).words.DPRL);	
		return result;
	}

	//////////////////////////////////////
	public void fetch_ling_features(BufferedReader lth_file, BufferedReader charniak_file) throws IOException{
		// System.out.println(content);content=content.trim();
		Vector<Vector<String>> lth_matrix=fetch_lth_matrix(lth_file);
		TreeNode char_tree=fetch_char(charniak_file);
		assign_lth_f(lth_matrix);
		assign_tree_f(char_tree,lth_matrix);
		//fetch_spatial_pivots();
		lth_matrix=null;
		char_tree=null;
		System.gc();
		//check_sourcefiles(lth_file, charniak_file);
	}
	/*public void fetch_spatial_pivots(){
		potential_pivots.tpp_prepositions=tpp_prepositions_index(content);
	}*/
	public String constant_correction(String input){
		    
		   if (input!=null){ input=input.replaceAll("��","x");
			input=input.replaceAll("��","0");
			input=input.replaceAll("-", "_");
			input=input.replaceAll(":","_");
			input = input.replaceAll("[^a-zA-Z0-9\\s_]", "");
			input=input.replaceAll("[\\s][\\s]*","");
			input=input.toLowerCase().trim();
			if (input.toUpperCase().contains("_LRB"))
		    	System.out.print("Stop");
			if (input.startsWith("_")) 
				{
				input=input.replaceFirst("[_][_]*","");}
			if (input.equals("_")) input="0";
			if (input.equals("")) input="0";}
		return input;
	}
	public void writeInput_features(PrintWriter p, int i){
		for(int w=0;w< sentence_feat.size();w++){
			p.println("dprl("+i+","+w+","+constant_correction(sentence_feat.elementAt(w).words.DPRL)+").");
			p.println("lemma("+i+","+constant_correction(sentence_feat.elementAt(w).words.lemma)+").");
			p.println("pos("+i+","+constant_correction(sentence_feat.elementAt(w).words.pos)+").");
			p.println("srl("+i+","+constant_correction(sentence_feat.elementAt(w).words.SRL)+").");
			p.println("subcategorization("+i+","+constant_correction(sentence_feat.elementAt(w).words.subcategorization)+").");
			p.println("word("+i+","+sentence_feat.elementAt(w).words.word+").");
			for(int w_2=0;w_2<sentence_feat.size();w_2++){
			    p.println("distance("+i+","+w+","+w_2+","+sentence_feat.elementAt(w).word_word.distance.elementAt(w_2)+").");
			    p.println("path("+i+","+w+","+w_2+","+constant_correction(sentence_feat.elementAt(w).word_word.path.elementAt(w_2))+").");
			
			}
		}// end write all input linguistic features
	}
	////End of  extraction of linguistic features///////// 
	
	public void writeInputRelations(PrintWriter p, int i){
		for(int r=0; r<relations.size(); r++){
			p.println("sentence(s"+i+","+"r"+(i+r)+").");
	        p.println("sr(r"+(i+r)+","+relations.elementAt(r).wh_sp.head_index+","+relations.elementAt(r).wh_tr.head_index+","+ relations.elementAt(r).wh_lm.head_index+").");
	        p.println("motion(r"+(i+r)+","+relations.elementAt(r).wh_motion.head_index+").");
	        p.println("rpath(r"+(i+r)+","+relations.elementAt(r).path+").");
	        p.println("frRef(r"+(i+r)+","+relations.elementAt(r).FrORe+").");
	        p.println("dynamic(r"+(i+r)+","+relations.elementAt(r).dynamic+").");
	    	}
		}
	public void writecalculi(PrintWriter p, int i){
		for(int r=0; r<relations.size(); r++){
			
		  for (int t=0;t<relations.elementAt(r).calc_type.general.size();t++)
		  {
		   p.println("gen_calc(r"+(i+r)+","+"t"+t+","+relations.elementAt(r).calc_type.general.elementAt(t)+").");
		   }
		  for (int t=0;t<relations.elementAt(r).calc_type.sp_value.size();t++){
		  p.println("specific_calc(r"+(i+r)+","+"t"+t+","+relations.elementAt(r).calc_type.sp_value.elementAt(t)+").");
		  }
		  for(int t=0;t<relations.elementAt(r).calc_type.sp_value.size();t++){
		  p.println("sp_value(r"+(i+r)+","+"t"+t+","+relations.elementAt(r).calc_type.sp_value.elementAt(t)+").");
		  }
		  } 
		
	}
	///////////////////////////////////////////////
	public boolean word_word_needed(int w1, int w2){
		//if trjectors or landmarks contain w2 and SPs contain w1 then add the features
		boolean flagw1=false;
		boolean flagw2=false;
		for (int r=0; r< relations.size();r++)
		  {
		   if(relations.elementAt(r).wh_lm.head_index==w2 || relations.elementAt(r).wh_tr.head_index==w2)
			   flagw2=true;
		   if(relations.elementAt(r).wh_sp.head_index==w1)
			   flagw1=true;
		   }// end check spatial relations
		for (int nr=0; nr<nonrelations.size();nr++)
			{
			 if(nonrelations.elementAt(nr).lm==w2 || nonrelations.elementAt(nr).tr==w2)
				   flagw2=true;
			   if(nonrelations.elementAt(nr).sp==w1)
				   flagw1=true;
			   }// end check non spatial relations
		
		if (flagw1==true && flagw2==true)
		    return true;
		else 
			return false;
	 }
	///////////////////////// end of (if a word appears in a either spatail or non spatial relation ////////
	public void writeInp_featkLog(PrintWriter p, int i){ //here it inputs all the properties of the words in one table
		p.println("interpretation(i"+i+", sentence(s"+i+")).");
		for(int w=0;w< sentence_feat.size();w++){
			p.println("interpretation(i"+i+", hasword(s"+i+", w"+w+")).");
			p.println("interpretation(i"+i+", word(w"+w+","+constant_correction(sentence_feat.elementAt(w).words.word)+","+constant_correction(sentence_feat.elementAt(w).words.pos)+","+constant_correction(sentence_feat.elementAt(w).words.DPRL)+","+constant_correction(sentence_feat.elementAt(w).words.SRL)+","+constant_correction(sentence_feat.elementAt(w).words.subcategorization)+")).");
			//p.println("lemma("+i+","++").");
			//p.println("pos("+i+","++").");
			//p.println("srl("+i+","++").");
			//p.println("subcategorization("+i+","++").");
			
			for(int w_2=0;w_2<sentence_feat.size();w_2++){
			 if (word_word_needed(w,w_2))	
			  { 
				int position=0;
				if (w<w_2) position=1; 
			   p.println("interpretation(i"+i+", word_word(w"+w+", w"+w_2+","+constant_correction(sentence_feat.elementAt(w).word_word.path.elementAt(w_2))+","+sentence_feat.elementAt(w).word_word.distance.elementAt(w_2)+","+position+")).");
			  }
			 }
		}// end write all input linguistic features in kLog format
		p.println("interpretation(i"+i+", hasword(s"+i+", w"+sentence_feat.size()+")).");
		p.println("interpretation(i"+i+", word(w"+sentence_feat.size()+", 0,0,0,0,0)).");
		
	}
		
	////////////////////////////////////////////
	
	public void writeInput_featureskLog(PrintWriter p, int i){// it inputs each property in one collumn
		
		if (i==10)
		System.out.print(i);
		p.println("interpretation(i"+i+", sentence(s"+i+")).");
		for(int w=0;w< sentence_feat.size();w++){
			p.println("interpretation(i"+i+", hasword(s"+i+", w"+w+")).");
			
			p.println("interpretation(i"+i+", word(w"+w+","+constant_correction(sentence_feat.elementAt(w).words.word)+")).");
			p.println("interpretation(i"+i+",pos(w"+w+","+constant_correction(sentence_feat.elementAt(w).words.pos)+")).");
			p.println("interpretation(i"+i+",dprl(w"+w+","+constant_correction(sentence_feat.elementAt(w).words.DPRL)+")).");
			p.println("interpretation(i"+i+",srl(w"+w+","+constant_correction(sentence_feat.elementAt(w).words.SRL)+")).");
			p.println("interpretation(i"+i+",subcat(w"+w+","+constant_correction(sentence_feat.elementAt(w).words.subcategorization)+")).");
		    for(int localindex=0;localindex<potential_pivots.tpp_prepositions.size();localindex++)
			if (potential_pivots.tpp_prepositions.elementAt(localindex).head_index==w)
		    p.println("interpretation(i"+i+",tpp(w"+w+")).");
		   
			
			//p.println("lemma("+i+","++").");
			//p.println("pos("+i+","++").");
			//p.println("srl("+i+","++").");
			//p.println("subcategorization("+i+","++").");
			
			for(int w_2=0;w_2<sentence_feat.size();w_2++){
			 if (word_word_needed(w,w_2))	
			  { 
			   int position=0;
			   if (w<w_2) position=1; 
			   p.println("interpretation(i"+i+", word_word_path_ext(w"+w+", w"+w_2+","+constant_correction(sentence_feat.elementAt(w).word_word.path.elementAt(w_2))+")).");
			   p.println("interpretation(i"+i+", word_word_dis_ext(w"+w+", w"+w_2+","+sentence_feat.elementAt(w).word_word.distance.elementAt(w_2)+")).");
			   p.println("interpretation(i"+i+", word_word_befor_ext(w"+w+", w"+w_2+","+position+")).");	
			  }
			 }
		}// end write all input linguistic features in kLog format
		p.println("interpretation(i"+i+", hasword(s"+i+", w"+sentence_feat.size()+")).");
		p.println("interpretation(i"+i+", word(w"+sentence_feat.size()+", undef0)).");
		p.println("interpretation(i"+i+",pos(w"+sentence_feat.size()+", 0)).");
		p.println("interpretation(i"+i+",dprl(w"+sentence_feat.size()+", 0)).");
		p.println("interpretation(i"+i+",srl(w"+sentence_feat.size()+", 0)).");
		p.println("interpretation(i"+i+",subcat(w"+sentence_feat.size()+", 0)).");
	
	}
	
	////End of  extraction of linguistic features for kLog///////// 
	
	   public void writeInputRelationskLog(PrintWriter p, int i){
		   int u=sentence_feat.size();
		for(int r=0; r<relations.size(); r++){
			
		    if (relations.elementAt(r).wh_sp.head_index==-1)  relations.elementAt(r).wh_sp.head_index=u;
		    if (relations.elementAt(r).wh_lm.head_index==-1)  relations.elementAt(r).wh_lm.head_index=u;
		    if (relations.elementAt(r).wh_tr.head_index==-1)  relations.elementAt(r).wh_tr.head_index=u;
		        
		    p.println("interpretation(i"+i+", rel(r"+r+", w"+relations.elementAt(r).wh_sp.head_index+", w"+relations.elementAt(r).wh_tr.head_index+", w"+ relations.elementAt(r).wh_lm.head_index+")).");
	        p.println("interpretation(i"+i+", sprel(r"+r+")).");
	        p.println("interpretation(i"+i+", goodsprel(r"+r+")).");
	        // p.println("interpretation(i"+i+", sprel(r"+r+")).");
	        //  p.println("motion(r"+(i+r)+","+relations.elementAt(r).wh_motion.head_index+").");
	       // p.println("rpath(r"+(i+r)+","+relations.elementAt(r).path+").");
	       // p.println("frRef(r"+(i+r)+","+relations.elementAt(r).FrORe+").");
	       // p.println("dynamic(r"+(i+r)+","+relations.elementAt(r).dynamic+").");
	    	}
		} // end extraction of relations for kLog

	   public void writeInputnonRelationskLog(PrintWriter p, int i, int ont, PrintWriter Np){
		    int nr=relations.size();
		    for(int r=0; r<nonrelations.size(); r++){ // changed to a balanced number of negative relations
			if (r<=(nr+1)){
			p.println("interpretation(i"+i+", rel(r"+(r+nr)+", w"+nonrelations.elementAt(r).sp+", w"+nonrelations.elementAt(r).tr+", w"+ nonrelations.elementAt(r).lm+")).");
			p.println("interpretation(i"+i+", sprel(r"+(r+nr)+")).");
			if (ont==1)
			p.println("interpretation(i"+i+", ontoSeed"+ont+"(r"+(r+nr)+")).");}
			else{
				Np.println("interpretation(i"+i+", rel(r"+(r+nr)+", w"+nonrelations.elementAt(r).sp+", w"+nonrelations.elementAt(r).tr+", w"+ nonrelations.elementAt(r).lm+")).");
				Np.println("interpretation(i"+i+", sprel(r"+(r+nr)+")).");
				Np.println("interpretation(i"+i+", spreltest(r"+(r+nr)+")).");
				}
			
			//p.println("interpretation(i"+i+", nonsprel(r"+(r+nr)+")).");
	        //  p.println("motion(r"+(i+r)+","+relations.elementAt(r).wh_motion.head_index+").");
	       // p.println("rpath(r"+(i+r)+","+relations.elementAt(r).path+").");
	       // p.println("frRef(r"+(i+r)+","+relations.elementAt(r).FrORe+").");
	       // p.println("dynamic(r"+(i+r)+","+relations.elementAt(r).dynamic+").");
	    	}
		}
	   
	// Here we produce the negatives that should be selected   
	   public void writeInputnonRelationskLogSelectiveSam(PrintWriter p, int i, int ont, PrintWriter Np){
		    int nr=relations.size();
		    for(int r=0; r<nonrelations.size(); r++){ // changed to a balanced number of negative relations
			if (r<=(nr+1)){
				p.println("interpretation(i"+i+", selected_triplet"+"( w"+nonrelations.elementAt(r).sp+", w"+nonrelations.elementAt(r).tr+", w"+ nonrelations.elementAt(r).lm+")).");}
		       	if (ont==1)
			{p.println("interpretation(i"+i+", ontoSeed"+ont+"(w"+relations.elementAt(r).wh_sp.head_index+", w"+relations.elementAt(r).wh_tr.head_index+", w"+ relations.elementAt(r).wh_lm.head_index+")).");
	       	}
			//p.println("interpretation(i"+i+", ontoSeed"+ont+"(r"+(r+nr)+")).");}
			else{
				Np.println("interpretation(i"+i+", rel(r"+(r+nr)+", w"+nonrelations.elementAt(r).sp+", w"+nonrelations.elementAt(r).tr+", w"+ nonrelations.elementAt(r).lm+")).");
				Np.println("interpretation(i"+i+", sprel(r"+(r+nr)+")).");
				Np.println("interpretation(i"+i+", spreltest(r"+(r+nr)+")).");
				
			 }
		  }
		}
	   //////////////////////////////Write the selected nagatives nbased on their similarity to positives
	   public void writeInputnonRelationskLogsimilaritySam(PrintWriter p, int i){
	   for (int j=0; j<selectedNegatives.size();j++){
		   int r=selectedNegatives.elementAt(j);
		   p.println("interpretation(i"+i+", selected_triplet"+"( w"+allnonrel.elementAt(r).sp+", w"+allnonrel.elementAt(r).tr+", w"+ allnonrel.elementAt(r).lm+")).");}
	    }
	 
	   
	   public void writeInputnonRelkLogsimGradualSam(PrintWriter p, int i){
		   
		   for (int j=0; j<selectedNegatives.size();j++){
			   if (i==570)
			   {
				   System.out.println("Stop!");
			   }
			   int r=selectedNegatives.elementAt(j);
			   p.println("interpretation(i"+i+", selected_triplet( w"+allnonrel.elementAt(r).sp+", w"+allnonrel.elementAt(r).tr+", w"+ allnonrel.elementAt(r).lm+","+j+")).");}
		       
		   }
	   
	   
	   
	   public void write_G_calculikLog(PrintWriter p, int i){
		   for(int r=0; r<relations.size(); r++){
				
		        p.println("interpretation(i"+i+", rel(r"+r+", w"+relations.elementAt(r).wh_sp.head_index+", w"+relations.elementAt(r).wh_tr.head_index+", w"+ relations.elementAt(r).wh_lm.head_index+")).");
		        p.println("interpretation(i"+i+", sprel(r"+r+")).");
		        for(int t=0;t<relations.elementAt(r).calc_type.general.size();t++){
		        String tg=	relations.elementAt(r).calc_type.general.elementAt(t).toLowerCase();
		        tg=tg.replaceAll(" ","");
		        tg=tg.replaceAll("\"", "");
		       // if (relations.elementAt(r).calc_type.specific.elementAt(0).toLowerCase().contains("rcc")){
		        p.println("interpretation(i"+i+", rel_g_type(r"+r+","+tg+")).");}
		        // p.println("interpretation(i"+i+", sprel(r"+r+")).");
		        //  p.println("motion(r"+(i+r)+","+relations.elementAt(r).wh_motion.head_index+").");
		       // p.println("rpath(r"+(i+r)+","+relations.elementAt(r).path+").");
		       // p.println("frRef(r"+(i+r)+","+relations.elementAt(r).FrORe+").");
		       // p.println("dynamic(r"+(i+r)+","+relations.elementAt(r).dynamic+").");
		    	} 
		   
	   }
	   public int index_Of(String [] s, String t){
		   int ind=-1;
		   if (t.contains("pp")) t="pp";
		   for (int i=0; i < s.length;i++){
			 if (s[i].equals(t))
			 {ind=i;
			 break;}
			 }
		   return ind;
	   }
	   public void flatten_sp(calculi x, int sp){
		   String[]  labels={"region", "direction", "distance", "eq", "dc","ec", "po","pp", "below","left", "right", "behind", "front", "above"};
		   int j=1;
		   x.flat_output[0]=sp;
		   x.flat_output[1]=1-sp;
		   for (int i=0;i<x.general.size();i++){
			   if (i>0)
				   System.out.print("Stop");
			   j=index_Of(labels,x.general.elementAt(i).toLowerCase().trim());
			   if(j!=-1)
			     x.flat_output[j+2]=1;
			  // j=index_Of(labels,x.specific.elementAt(i).toLowerCase().trim());
			  // if (j!=-1)
			   // x.flat_output[j+2]=1;
			   j= index_Of(labels,x.sp_value.elementAt(i).toLowerCase().trim());
			   if (j!=-1)
			    x.flat_output[j+2]=1; 
			  }
		    }
	   
	   public void writekHierarchyout(PrintWriter p,int i,int ontSeed){
		   for(int r=0; r<relations.size(); r++){
				
		        p.println("interpretation(i"+i+", rel(r"+r+", w"+relations.elementAt(r).wh_sp.head_index+", w"+relations.elementAt(r).wh_tr.head_index+", w"+ relations.elementAt(r).wh_lm.head_index+")).");
		        p.println("interpretation(i"+i+", sprel(r"+r+")).");
		          
		        //for(int t=0;t<relations.elementAt(r).calc_type.general.size();t++){
		          if (relations.elementAt(r).calc_type.flat_output[ontSeed]==1){
		          /*String tg=	relations.elementAt(r).calc_type.general.elementAt(t).toLowerCase();
		          tg=tg.replaceAll(" ","");
		          tg=tg.replaceAll("\"", "");*/
		       // if (relations.elementAt(r).calc_type.specific.elementAt(0).toLowerCase().contains("rcc")){
		          p.println("interpretation(i"+i+", ontoSeed"+ontSeed+"(r"+r+")).");}}
		        // p.println("interpretation(i"+i+", sprel(r"+r+")).");
		        //  p.println("motion(r"+(i+r)+","+relations.elementAt(r).wh_motion.head_index+").");
		       // p.println("rpath(r"+(i+r)+","+relations.elementAt(r).path+").");
		       // p.println("frRef(r"+(i+r)+","+relations.elementAt(r).FrORe+").");
		       // p.println("dynamic(r"+(i+r)+","+relations.elementAt(r).dynamic+").");
		    	} 
	   public void writekHierarchyout_intensional(PrintWriter p,int i,int ontSeed){
		   for(int r=0; r<relations.size(); r++){
			   // p.println("interpretation(i"+i+", rel(w"+relations.elementAt(r).wh_sp.head_index+", w"+relations.elementAt(r).wh_tr.head_index+", w"+ relations.elementAt(r).wh_lm.head_index+")).");
		       // p.println("interpretation(i"+i+", sprel(r"+r+")).");
		        
		       // p.println("interpretation(i"+i+", rel(w"+relations.elementAt(r).wh_sp.head_index+", w"+relations.elementAt(r).wh_tr.head_index+", w"+ relations.elementAt(r).wh_lm.head_index+")).");
		        //p.println("interpretation(i"+i+", sprel(r"+r+")).");
		      //  if (relations.elementAt(r).calc_type.flat_output[ontSeed]==1){
		        	
		        	//p.println("interpretation(i"+i+", ontoSeed"+ontSeed+"(r"+r+")).");}}
		      
		         p.println("interpretation(i"+i+", ontoSeed"+ontSeed+"(w"+relations.elementAt(r).wh_sp.head_index+", w"+relations.elementAt(r).wh_tr.head_index+", w"+ relations.elementAt(r).wh_lm.head_index+")).");}//}
		        } 
	   
	   void replacelm(int r){
		   String directions[]={"left","right","front","back","center","middle","top","down"};
		    
		   String find=null;
		   for (int i=0; i<directions.length;i++){
			   if (relations.elementAt(r).wh_sp.ph.contains(directions[i])) {find=directions[i]; break;}}
		   int hind=-1,dirind=-1;
		   if (find!=null){
		   for (int kk=0;kk<relations.elementAt(r).wh_sp.ph.size();kk++)
			   {
			    if(relations.elementAt(r).wh_sp.ph_head.replaceAll(" ","").equalsIgnoreCase(relations.elementAt(r).wh_sp.ph.elementAt(kk)))
				   hind=kk;
		        if(find.equalsIgnoreCase(relations.elementAt(r).wh_sp.ph.elementAt(kk)))
			   dirind=kk;
			   }
		   relations.elementAt(r).wh_lm.ph_head=find;
		   relations.elementAt(r).wh_lm.head_index=relations.elementAt(r).wh_sp.head_index+dirind-hind;
		 
		   }
		   }
	   public void write_intensional_changeLm(PrintWriter p,int i,int ontSeed){
		   
		   for(int r=0; r<relations.size(); r++){
			   // p.println("interpretation(i"+i+", rel(w"+relations.elementAt(r).wh_sp.head_index+", w"+relations.elementAt(r).wh_tr.head_index+", w"+ relations.elementAt(r).wh_lm.head_index+")).");
		       // p.println("interpretation(i"+i+", sprel(r"+r+")).");
		        
		       // p.println("interpretation(i"+i+", rel(w"+relations.elementAt(r).wh_sp.head_index+", w"+relations.elementAt(r).wh_tr.head_index+", w"+ relations.elementAt(r).wh_lm.head_index+")).");
		        //p.println("interpretation(i"+i+", sprel(r"+r+")).");
		      //  if (relations.elementAt(r).calc_type.flat_output[ontSeed]==1){
		        	
		        	//p.println("interpretation(i"+i+", ontoSeed"+ontSeed+"(r"+r+")).");}}
			  // if (this.sentence_feat.elementAt(0).words.word.contains("a")&& this.sentence_feat.elementAt(1).words.word.contains("larg"))
			   //{System.out.println("Stop");}
			   
		         if (relations.elementAt(r).wh_lm.ph_head.equalsIgnoreCase("undefined"))
		        	 replacelm(r);
		          p.println("interpretation(i"+i+", ontoSeed"+ontSeed+"(w"+relations.elementAt(r).wh_sp.head_index+", w"+relations.elementAt(r).wh_tr.head_index+", w"+ relations.elementAt(r).wh_lm.head_index+")).");}//}
		        }
	   
	   public void dataStatistics(PrintWriter p,int i, int ar[]){
		   int n=relations.size();
		   p.println(i+"\t\t"+n);
		   ar[n]++;
	   }
	   
	   // end extraction of relations for kLog
	   public void writecalculikLog(PrintWriter p, int i){
	     
	   for(int r=0; r<relations.size(); r++){
			
	        p.println("interpretation(i"+i+", rel(r"+r+", w"+relations.elementAt(r).wh_sp.head_index+", w"+relations.elementAt(r).wh_tr.head_index+", w"+ relations.elementAt(r).wh_lm.head_index+")).");
	        p.println("interpretation(i"+i+", sprel(r"+r+")).");
	        if (relations.elementAt(r).calc_type.specific.elementAt(0).toLowerCase().contains("rcc")){
	        p.println("interpretation(i"+i+", rccsprel(r"+r+")).");}
	        // p.println("interpretation(i"+i+", sprel(r"+r+")).");
	        //  p.println("motion(r"+(i+r)+","+relations.elementAt(r).wh_motion.head_index+").");
	       // p.println("rpath(r"+(i+r)+","+relations.elementAt(r).path+").");
	       // p.println("frRef(r"+(i+r)+","+relations.elementAt(r).FrORe+").");
	       // p.println("dynamic(r"+(i+r)+","+relations.elementAt(r).dynamic+").");
	    	}
	   }
	   public void  writekLogIndividualRoles(PrintWriter p, int i){
		   for(int r=0; r<relations.size(); r++){
			    p.println("interpretation(i"+i+",trajector(w"+relations.elementAt(r).wh_tr.head_index+")).");
		        p.println("interpretation(i"+i+",landmark(w"+relations.elementAt(r).wh_lm.head_index+")).");
		        p.println("interpretation(i"+i+",indicator(w"+relations.elementAt(r).wh_sp.head_index+")).");
		    	}
	         }
	   public Vector<Integer> gather_rels_of_a_seq(int x, int[] flag){
		  Vector <Integer> rs=new Vector <Integer>();
		// rs contains the index of the relations that should be integrated
		 // rs.addElement(x);
		   for(int r=0; r<relations.size(); r++){
			   if (relations.elementAt(r).wh_sp.head_index==relations.elementAt(x).wh_sp.head_index)
				   {
				   flag[r]=1;
				   rs.addElement(r);
				   }
			   }
		   return rs;
		   }
	   
	   /////////////////
	   public String label_of_w(int w, Vector <Integer> rs){
		   String S=null;
		   for(int r=0;r<rs.size();r++)
		   {
			   if (relations.elementAt(rs.elementAt(r)).wh_lm.head_index==w) S=" Landmark";
			   if (relations.elementAt(rs.elementAt(r)).wh_tr.head_index==w) S= " Trajector";
			   if (relations.elementAt(rs.elementAt(r)).wh_sp.head_index==w) S=" Indicator";
		   } 
		  if (S==null) S=" None";
		   return S;
	   }
	   public String is_pivot(int w,int w2){
		  
		   String S=null;
		   if (w==w2) S=" pivot";
		   else S=" none";
		   return S;
	   }
	  public void writePositives_perSp(PrintWriter p,PrintWriter pgrmm, int i , Vector<Integer> rs){
		// rs contains the index of the relations that should be integrated their sp is the same
		  int w2=relations.elementAt(rs.elementAt(0)).wh_sp.head_index;
		  boolean position=true;
		  for(int w=0;w<sentence_feat.size();w++){
			   String label=label_of_w(w,rs);
			   String pivot=is_pivot(w,w2);
			   if (w<w2) 
				   position=false;
			   p.println(sentence_feat.elementAt(w).words.word+" "+sentence_feat.elementAt(w2).words.word+" "+sentence_feat.elementAt(w2).words.subcategorization+" "+sentence_feat.elementAt(w).words.pos+" "+sentence_feat.elementAt(w).words.DPRL+" "+sentence_feat.elementAt(w).words.SRL+" "+sentence_feat.elementAt(w).word_word.path.elementAt(w2)+" "+sentence_feat.elementAt(w).word_word.distance.elementAt(w2)+" "+" wordsubcat_"+sentence_feat.elementAt(w).words.subcategorization+" "+position+pivot+label);
			   pgrmm.println(label.trim()+" ---- "+sentence_feat.elementAt(w).words.word+" Prep="+sentence_feat.elementAt(w2).words.word+" Psub="+sentence_feat.elementAt(w2).words.subcategorization+" "+sentence_feat.elementAt(w).words.pos+" "+sentence_feat.elementAt(w).words.DPRL+" "+sentence_feat.elementAt(w).words.SRL+" Pat="+sentence_feat.elementAt(w).word_word.path.elementAt(w2)+" dis="+sentence_feat.elementAt(w).word_word.distance.elementAt(w2)+" "+" wordsubcat_"+sentence_feat.elementAt(w).words.subcategorization+" pos="+position+pivot);
			  //   pwMallet.println("None ---- "+headword+" "+lemma+" "+"Prep="+spatialPivot1+" "+"Psub="+subcategorization+" "+phrasetype+" "+headwordPOS+" "+headwordDPRL+" "+headwordSRL+" "+"Pat="+path+" "+"dis="+distance+" "+"wordsubcat_"+wordsubcat+" "+"pos="+position+" pivot")   
		  }
		 
	  }
	 // %%%%%%%%%%%%%%% Producing positive interpretations with sequential structure for stupid kLog
	  public void writekLogPositives_perSp(PrintWriter kLogSeqs, int i , Vector<Integer> rs){
			// rs contains the index of the relations that should be integrated their sp is the same
			  int w2=relations.elementAt(rs.elementAt(0)).wh_sp.head_index;
			  boolean position=true;
		
			  for(int w=0;w<sentence_feat.size();w++){
				   String label=label_of_w(w,rs).toLowerCase();
				   String pivot=is_pivot(w,w2);
				   if (w<w2) 
					   position=false;
				   kLogSeqs.println("interpretation(i"+i+", word(w"+w+","+constant_correction(sentence_feat.elementAt(w).words.word)+")).");
				   kLogSeqs.println("interpretation(i"+i+",pos(w"+w+","+constant_correction(sentence_feat.elementAt(w).words.pos)+")).");
				   kLogSeqs.println("interpretation(i"+i+",dprl(w"+w+","+constant_correction(sentence_feat.elementAt(w).words.DPRL)+")).");
				   kLogSeqs.println("interpretation(i"+i+",srl(w"+w+","+constant_correction(sentence_feat.elementAt(w).words.SRL)+")).");
				   kLogSeqs.println("interpretation(i"+i+",subcat(w"+w+","+constant_correction(sentence_feat.elementAt(w).words.subcategorization)+")).");
				    //kLogSeqs.println("interpretation	  " Psub="+sentence_feat.elementAt(w2).words.subcategorization+" "+
				   kLogSeqs.println("interpretation(i"+i+", word_word_path_ext(w"+w+", w"+w2+","+constant_correction(sentence_feat.elementAt(w).word_word.path.elementAt(w2))+")).");
				   kLogSeqs.println("interpretation(i"+i+", word_word_dis_ext(w"+w+", w"+w2+","+sentence_feat.elementAt(w).word_word.distance.elementAt(w2)+")).");
				   kLogSeqs.println("interpretation(i"+i+", word_word_befor_ext(w"+w+", w"+w2+","+position+")).");		   
				   kLogSeqs.println	("interpretation(i"+i+",role(w"+w+","+label.trim()+")).");	
				  }
			  kLogSeqs.println("interpretation(i"+i+",pivot(w"+w2+","+ constant_correction(sentence_feat.elementAt(w2).words.word)+")).");
			  
		     }
	  
	// %%%%%%%%%%%%%%% Producing negative interpretations with sequential structure for stupid kLog that can not produce input specific examples
		
	  public void  writekLogNegatives_perSp_pivprop(PrintWriter kLogSeqs,int i,int w2) {
		  boolean position=true;
		  for(int w=0;w<sentence_feat.size();w++){
			   String label=" none";
			   String pivot=is_pivot(w,w2);
			   if (w<w2) 
				   position=false;
			   kLogSeqs.println("interpretation(i"+i+", word(w"+w+","+constant_correction(sentence_feat.elementAt(w).words.word)+")).");
			   kLogSeqs.println("interpretation(i"+i+",pos(w"+w+","+constant_correction(sentence_feat.elementAt(w).words.pos)+")).");
			   kLogSeqs.println("interpretation(i"+i+",dprl(w"+w+","+constant_correction(sentence_feat.elementAt(w).words.DPRL)+")).");
			   kLogSeqs.println("interpretation(i"+i+",srl(w"+w+","+constant_correction(sentence_feat.elementAt(w).words.SRL)+")).");
			   kLogSeqs.println("interpretation(i"+i+",subcat(w"+w+","+constant_correction(sentence_feat.elementAt(w).words.subcategorization)+")).");
			   kLogSeqs.println("interpretation(i"+i+",pivot(w"+w+","+ constant_correction(sentence_feat.elementAt(w2).words.word)+")).");
			   kLogSeqs.println("interpretation(i"+i+",pivosubcat(w"+w+","+constant_correction(sentence_feat.elementAt(w2).words.subcategorization)+")).");
			   	   
			   //kLogSeqs.println("interpretation	  " Psub="+sentence_feat.elementAt(w2).words.subcategorization+" "+
			   kLogSeqs.println("interpretation(i"+i+", word_word_path_ext(w"+w+","+constant_correction(sentence_feat.elementAt(w).word_word.path.elementAt(w2))+")).");
			   kLogSeqs.println("interpretation(i"+i+", word_word_dis_ext(w"+w+","+sentence_feat.elementAt(w).word_word.distance.elementAt(w2)+")).");
			   kLogSeqs.println("interpretation(i"+i+", word_word_befor_ext(w"+w+","+position+")).");		   
			   kLogSeqs.println	("interpretation(i"+i+",role(w"+w+","+"w"+w2+","+label.trim()+")).");	
			   }
		     kLogSeqs.println("interpretation(i"+i+",pivotEntity(w"+w2+")).");   
		  
		      }
	  
	 	  
	  // %%%%%%%%%%%%%%% Producing positive interpretations with sequential structure for stupid kLog
	  public void writekLogPositives_perSp_pivprop(PrintWriter kLogSeqs, int i , Vector<Integer> rs){
			// rs contains the index of the relations that should be integrated their sp is the same
			  int w2=relations.elementAt(rs.elementAt(0)).wh_sp.head_index;
			  boolean position=true;
		
			  for(int w=0;w<sentence_feat.size();w++){
				   String label=label_of_w(w,rs).toLowerCase();
				   String pivot=is_pivot(w,w2);
				   if (w<w2) 
					   position=false;
				   kLogSeqs.println("interpretation(i"+i+", word(w"+w+","+constant_correction(sentence_feat.elementAt(w).words.word)+")).");
				   kLogSeqs.println("interpretation(i"+i+",pos(w"+w+","+constant_correction(sentence_feat.elementAt(w).words.pos)+")).");
				   kLogSeqs.println("interpretation(i"+i+",dprl(w"+w+","+constant_correction(sentence_feat.elementAt(w).words.DPRL)+")).");
				   kLogSeqs.println("interpretation(i"+i+",srl(w"+w+","+constant_correction(sentence_feat.elementAt(w).words.SRL)+")).");
				   kLogSeqs.println("interpretation(i"+i+",subcat(w"+w+","+constant_correction(sentence_feat.elementAt(w).words.subcategorization)+")).");
				   kLogSeqs.println("interpretation(i"+i+",pivot(w"+w+","+ constant_correction(sentence_feat.elementAt(w2).words.word)+")).");
				   kLogSeqs.println("interpretation(i"+i+",pivosubcat(w"+w+","+constant_correction(sentence_feat.elementAt(w2).words.subcategorization)+")).");
				   //kLogSeqs.println("interpretation	  " Psub="+sentence_feat.elementAt(w2).words.subcategorization+" "+
				   kLogSeqs.println("interpretation(i"+i+", word_word_path_ext(w"+w+","+constant_correction(sentence_feat.elementAt(w).word_word.path.elementAt(w2))+")).");
				   kLogSeqs.println("interpretation(i"+i+", word_word_dis_ext(w"+w+","+sentence_feat.elementAt(w).word_word.distance.elementAt(w2)+")).");
				   kLogSeqs.println("interpretation(i"+i+", word_word_befor_ext(w"+w+","+position+")).");		   
				   kLogSeqs.println	("interpretation(i"+i+",role(w"+w+",w"+w2+","+label.trim()+")).");	
				   }
			  kLogSeqs.println("interpretation(i"+i+",pivotEntity(w"+w2+")).");   
			   
		     }
	  
	// %%%%%%%%%%%%%%% Producing negative interpretations with sequential structure for stupid kLog that can not produce input specific examples
		
	  public void  writekLogNegatives_perSp(PrintWriter kLogSeqs,int i,int w2) {
		  boolean position=true;
		  for(int w=0;w<sentence_feat.size();w++){
			   String label=" none";
			   String pivot=is_pivot(w,w2);
			   if (w<w2) 
				   position=false;
			   kLogSeqs.println("interpretation(i"+i+", word(w"+w+","+constant_correction(sentence_feat.elementAt(w).words.word)+")).");
			   kLogSeqs.println("interpretation(i"+i+",pos(w"+w+","+constant_correction(sentence_feat.elementAt(w).words.pos)+")).");
			   kLogSeqs.println("interpretation(i"+i+",dprl(w"+w+","+constant_correction(sentence_feat.elementAt(w).words.DPRL)+")).");
			   kLogSeqs.println("interpretation(i"+i+",srl(w"+w+","+constant_correction(sentence_feat.elementAt(w).words.SRL)+")).");
			   kLogSeqs.println("interpretation(i"+i+",subcat(w"+w+","+constant_correction(sentence_feat.elementAt(w).words.subcategorization)+")).");
			    //kLogSeqs.println("interpretation	  " Psub="+sentence_feat.elementAt(w2).words.subcategorization+" "+
			   kLogSeqs.println("interpretation(i"+i+", word_word_path_ext(w"+w+", w"+w2+","+constant_correction(sentence_feat.elementAt(w).word_word.path.elementAt(w2))+")).");
			   kLogSeqs.println("interpretation(i"+i+", word_word_dis_ext(w"+w+", w"+w2+","+sentence_feat.elementAt(w).word_word.distance.elementAt(w2)+")).");
			   kLogSeqs.println("interpretation(i"+i+", word_word_befor_ext(w"+w+", w"+w2+","+position+")).");		   
			   kLogSeqs.println	("interpretation(i"+i+",role(w"+w+","+label.trim()+")).");	
			   }
		       kLogSeqs.println("interpretation(i"+i+",pivot(w"+w2+","+ constant_correction(sentence_feat.elementAt(w2).words.word)+")).");
	     }
	  //%%%%%%%%%%%%%%%%%
	  
	  
	  
	  //////////
	  public void  writeNegatives_perSp(PrintWriter p,PrintWriter pgrmm,int w2) {
		  boolean position=true;
		  for(int w=0;w<sentence_feat.size();w++){
			   String label=" None";
			   String pivot=is_pivot(w,w2);
			   if (w<w2) 
				   position=false;
			   p.println(sentence_feat.elementAt(w).words.word+" "+sentence_feat.elementAt(w2).words.word+" "+sentence_feat.elementAt(w2).words.subcategorization+" "+sentence_feat.elementAt(w).words.pos+" "+sentence_feat.elementAt(w).words.DPRL+" "+sentence_feat.elementAt(w).words.SRL+" "+sentence_feat.elementAt(w).word_word.path.elementAt(w2)+" "+sentence_feat.elementAt(w).word_word.distance.elementAt(w2)+" "+" wordsubcat_"+sentence_feat.elementAt(w).words.subcategorization+" "+position+pivot+label);
			   pgrmm.println(label.trim()+" ---- "+sentence_feat.elementAt(w).words.word+" Prep="+sentence_feat.elementAt(w2).words.word+" Psub="+sentence_feat.elementAt(w2).words.subcategorization+" "+sentence_feat.elementAt(w).words.pos+" "+sentence_feat.elementAt(w).words.DPRL+" "+sentence_feat.elementAt(w).words.SRL+" Pat="+sentence_feat.elementAt(w).word_word.path.elementAt(w2)+" dis="+sentence_feat.elementAt(w).word_word.distance.elementAt(w2)+" "+" wordsubcat_"+sentence_feat.elementAt(w).words.subcategorization+" pos="+position+pivot);
				 
		  }
		  
	  }
	  // public void writeNoneSeq(PrintWriter p, int i){}
	   public void writeMalletSeq(PrintWriter p,PrintWriter pgrmm, int i){
		   
		   int[] flags=new int[relations.size()];// each relation has a flag which will be  1 when the relation already has been used
		   for(int r=0;r<relations.size();r++){ 
			   if (flags[r]!=1){
		             Vector <Integer> rs=gather_rels_of_a_seq(r,flags);// this finds the relations that should be integrated and set their flags
		             writePositives_perSp(p,pgrmm, i,rs); // rs contains the index of the relations that should be integrated
		             p.println();
		             pgrmm.println();}
		   }
		   for (int r=0; r<non_SP_tpps.size();r++){
			   writeNegatives_perSp(p,pgrmm,non_SP_tpps.elementAt(r));
			   p.println();
			   pgrmm.println();
		   }
		  // writeNoneSeq(p,i);
	   }
	   
	  // This procedure produces the sequential inputs for mallet and GRMM but first changes the many undefined landmarks 
public void  writeMalletSeq_modifUndefLm(PrintWriter p,PrintWriter pgrmm, int i){
		   
		   int[] flags=new int[relations.size()];// each relation has a flag which will be  1 when the relation already has been used
		   for(int r=0;r<relations.size();r++){ 
			   replacelm(r);
			   if (flags[r]!=1){
		             Vector <Integer> rs=gather_rels_of_a_seq(r,flags);// this finds the relations that should be integrated and set their flags
		             writePositives_perSp(p,pgrmm, i,rs); // rs contains the index of the relations that should be integrated
		             p.println();
		             pgrmm.println();}
		   }
		   for (int r=0; r<non_SP_tpps.size();r++){
			   writeNegatives_perSp(p,pgrmm,non_SP_tpps.elementAt(r));
			   p.println();
			   pgrmm.println();
		   }
		   
		  // writeNoneSeq(p,i);
	   }
	  

/*public void  writekLogSequences(PrintWriter kLogSeqs){
  
   int[] flags=new int[relations.size()];// each relation has a flag which will be  1 when the relation already has been used
   for(int r=0;r<relations.size();r++){ 
     replacelm(r);
     if (flags[r]!=1){
     Vector <Integer> rs=gather_rels_of_a_seq(r,flags);// this finds the relations that should be integrated and set their flags
     writekLogPositives_perSp_pivprop(kLogSeqs,(build_Sequences_SbyS.sequence_num),rs); // rs contains the index of the relations that should be integrated
     kLogSeqs.println();
     build_Sequences_SbyS.sequence_num++;
	         }
	       }
	 for (int r=0; r<non_SP_tpps.size();r++){
		  writekLogNegatives_perSp_pivprop(kLogSeqs,(build_Sequences_SbyS.sequence_num),non_SP_tpps.elementAt(r));
		   kLogSeqs.println();
		   build_Sequences_SbyS.sequence_num++;
	   }
 }*/

	   //////////////////
	   
	   public void write_roles_features( PrintWriter p ,int i,Vector <String> values){
		   
		 for (int r=0;r<relations.size();r++){
		  if (sentence_feat.elementAt(relations.elementAt(r).wh_tr.head_index).words.pos.contains("VB"))
			  p.print(sentence_feat.elementAt(relations.elementAt(r).wh_tr.head_index).words.word+"*VB*"+i+":");
		  if (sentence_feat.elementAt(relations.elementAt(r).wh_tr.head_index).words.pos.contains("PRP"))
			  p.print(sentence_feat.elementAt(relations.elementAt(r).wh_tr.head_index).words.word+"*PRP*"+i+":");
		 
		  if (sentence_feat.elementAt(relations.elementAt(r).wh_tr.head_index).words.pos.contains("CC"))
		  { //p.print("CC:"+i+":");
			  p.print(sentence_feat.elementAt(relations.elementAt(r).wh_tr.head_index).words.word+"*CC*");}
		  
		  if (sentence_feat.elementAt(relations.elementAt(r).wh_tr.head_index).words.pos.contains("JJ"))
		  {p.print("JJ:"+i+":"); p.print(sentence_feat.elementAt(relations.elementAt(r).wh_tr.head_index).words.word+"**");
		  }
		   if (!values.contains(sentence_feat.elementAt(relations.elementAt(r).wh_tr.head_index).words.pos))
			   values.addElement(sentence_feat.elementAt(relations.elementAt(r).wh_tr.head_index).words.pos);}
		 
		  }
	   ///////////////////////////////////////////////////
	   
	   ////////////////////Below; collect all the propositional attributes for SpRL arff file///////
	   public void collect_RelarffCandidates(Vector <String> arff, PrintWriter head1){
	   for(int r=0;r<allPosRelCandids.size();r++){
		 int lm_index=allPosRelCandids.elementAt(r).lm,tr_index=allPosRelCandids.elementAt(r).tr,sp_index=allPosRelCandids.elementAt(r).sp;
		  //if (relations.elementAt(r).wh_tr.head_index!=-1)
		  //**********************trajector features  
		    if (tr_index==sentence_feat.size()){
		    	if (!arff.contains("tr_h_undefined"))
					   arff.addElement("tr_h_undefined");
		    }
		    else {
		    	
				if (!arff.contains("tr_h_"+constant_correction(sentence_feat.elementAt(tr_index).words.word)))
					   arff.addElement("tr_h_"+constant_correction(sentence_feat.elementAt(tr_index).words.word));
				
				if (!arff.contains("tr_dp_"+constant_correction(sentence_feat.elementAt(tr_index).words.DPRL)))
					   arff.addElement("tr_dp_"+constant_correction(sentence_feat.elementAt(tr_index).words.DPRL));
				
				if (!arff.contains("tr_pos_"+constant_correction(sentence_feat.elementAt(tr_index).words.pos)))
					   arff.addElement("tr_pos_"+constant_correction(sentence_feat.elementAt(tr_index).words.pos));
				
				if (!arff.contains("tr_SRL_"+constant_correction(sentence_feat.elementAt(tr_index).words.SRL)))
					   arff.addElement("tr_SRL_"+constant_correction(sentence_feat.elementAt(tr_index).words.SRL));
				
				
				if (!arff.contains("tr_sub_"+constant_correction(sentence_feat.elementAt(tr_index).words.subcategorization)))
					   arff.addElement("tr_sub_"+constant_correction(sentence_feat.elementAt(tr_index).words.subcategorization));
		    }	
		 	//****************landmark features	
		    if (lm_index==sentence_feat.size()){
		    	if (!arff.contains("lm_h_undefined"))
					   arff.addElement("lm_h_undefined");
		    }
		    else{
				if (!arff.contains("lm_h_"+constant_correction(sentence_feat.elementAt(lm_index).words.word)))
					   arff.addElement("lm_h_"+constant_correction(sentence_feat.elementAt(lm_index).words.word));
				
				if (!arff.contains("lm_dp_"+constant_correction(sentence_feat.elementAt(lm_index).words.DPRL)))
					   arff.addElement("lm_dp_"+constant_correction(sentence_feat.elementAt(lm_index).words.DPRL));
				
				if (!arff.contains("lm_pos_"+constant_correction(sentence_feat.elementAt(lm_index).words.pos)))
					   arff.addElement("lm_pos_"+constant_correction(sentence_feat.elementAt(lm_index).words.pos));
				
				if (!arff.contains("lm_SRL_"+constant_correction(sentence_feat.elementAt(lm_index).words.SRL)))
					   arff.addElement("lm_SRL_"+constant_correction(sentence_feat.elementAt(lm_index).words.SRL));
				
				
				if (!arff.contains("lm_sub_"+constant_correction(sentence_feat.elementAt(lm_index).words.subcategorization)))
					   arff.addElement("lm_sub_"+constant_correction(sentence_feat.elementAt(lm_index).words.subcategorization));
		    }	
			//****************spatial indicator features
		    if (!arff.contains("sp_h_"+constant_correction(sentence_feat.elementAt(sp_index).words.word)))
				   arff.addElement("sp_h_"+constant_correction(sentence_feat.elementAt(sp_index).words.word));
		 	
			  if (!arff.contains("sp_sub_"+constant_correction(sentence_feat.elementAt(sp_index).words.subcategorization)))
					   arff.addElement("sp_sub_"+constant_correction(sentence_feat.elementAt(sp_index).words.subcategorization));
				
			  if (!arff.contains("sp_pos_"+constant_correction(sentence_feat.elementAt(sp_index).words.pos)))
				   arff.addElement("sp_pos_"+constant_correction(sentence_feat.elementAt(sp_index).words.pos));
			  
			  if (!arff.contains("sp_dp_"+constant_correction(sentence_feat.elementAt(sp_index).words.DPRL)))
				   arff.addElement("sp_dp_"+constant_correction(sentence_feat.elementAt(sp_index).words.DPRL));

			  if (!arff.contains("sp_SRL_"+constant_correction(sentence_feat.elementAt(sp_index).words.SRL)))
				   arff.addElement("sp_SRL_"+constant_correction(sentence_feat.elementAt(sp_index).words.SRL));
		//**************relational features 
			  // adding distance features 
			  int distt=-1;
			  if(tr_index!=sentence_feat.size())
			    distt= sentence_feat.elementAt(sp_index).word_word.distance.elementAt(tr_index);
			  if(!arff.contains("d_sp_tr_"+distt))
				 arff.addElement("d_sp_tr_"+distt);  
			  int distl=-1;
			  if(lm_index!=sentence_feat.size())
				 distl= sentence_feat.elementAt(sp_index).word_word.distance.elementAt(lm_index);
			  if(!arff.contains("d_sp_lm_"+distl))
				  arff.addElement("d_sp_lm_"+distl);
			  int disttl=-1;
			  if ((distl!=-1 & distt!=-1))
				  disttl=sentence_feat.elementAt(tr_index).word_word.distance.elementAt(lm_index);
			  if(!arff.contains("d_tr_lm_"+disttl))
				  arff.addElement("d_tr_lm_"+disttl);
			  // adding path features

			}// end of for relations 
	  // for(int r=0;r<Math.min (nonrelations.size(),relations.size()+1);r++)
		   for(int r=0;r<allnonrel.size();r++){
					
			
			int r1=selectedNegatives.elementAt(r);  
		    int lm_index=allnonrel.elementAt(r1).lm,tr_index=allnonrel.elementAt(r1).tr,sp_index=allnonrel.elementAt(r1).sp;
		  //if (relations.elementAt(r).wh_tr.head_index!=-1)
		
		    //**********************trajector features  
		    if (tr_index==sentence_feat.size()){
		    	if (!arff.contains("tr_h_undefined"))
					   arff.addElement("tr_h_undefined");
		    }
		    else{
				if (!arff.contains("tr_h_"+constant_correction(sentence_feat.elementAt(tr_index).words.word)))
					   arff.addElement("tr_h_"+constant_correction(sentence_feat.elementAt(tr_index).words.word));
				
				if (!arff.contains("tr_dp_"+constant_correction(sentence_feat.elementAt(tr_index).words.DPRL)))
					   arff.addElement("tr_dp_"+constant_correction(sentence_feat.elementAt(tr_index).words.DPRL));
				
				if (!arff.contains("tr_pos_"+constant_correction(sentence_feat.elementAt(tr_index).words.pos)))
					   arff.addElement("tr_pos_"+constant_correction(sentence_feat.elementAt(tr_index).words.pos));
				
				if (!arff.contains("tr_SRL_"+constant_correction(sentence_feat.elementAt(tr_index).words.SRL)))
					   arff.addElement("tr_SRL_"+constant_correction(sentence_feat.elementAt(tr_index).words.SRL));
				
				
				if (!arff.contains("tr_sub_"+constant_correction(sentence_feat.elementAt(tr_index).words.subcategorization)))
					   arff.addElement("tr_sub_"+constant_correction(sentence_feat.elementAt(tr_index).words.subcategorization));
		    }
		 	//****************landmark features	
		    if (lm_index==sentence_feat.size()){
		    	if (!arff.contains("lm_h_undefined"))
					   arff.addElement("lm_h_undefined");
		    }
		    else{
				
				if (!arff.contains("lm_h_"+constant_correction(sentence_feat.elementAt(lm_index).words.word)))
					   arff.addElement("lm_h_"+constant_correction(sentence_feat.elementAt(lm_index).words.word));
				
				if (!arff.contains("lm_dp_"+constant_correction(sentence_feat.elementAt(lm_index).words.DPRL)))
					   arff.addElement("lm_dp_"+constant_correction(sentence_feat.elementAt(lm_index).words.DPRL));
				
				if (!arff.contains("lm_pos_"+constant_correction(sentence_feat.elementAt(lm_index).words.pos)))
					   arff.addElement("lm_pos_"+constant_correction(sentence_feat.elementAt(lm_index).words.pos));
				
				if (!arff.contains("lm_SRL_"+constant_correction(sentence_feat.elementAt(lm_index).words.SRL)))
					   arff.addElement("lm_SRL_"+constant_correction(sentence_feat.elementAt(lm_index).words.SRL));
				
				
				if (!arff.contains("lm_sub_"+constant_correction(sentence_feat.elementAt(lm_index).words.subcategorization)))
					   arff.addElement("lm_sub_"+constant_correction(sentence_feat.elementAt(lm_index).words.subcategorization));
		    }	
			//****************spatial indicator features
			  if (!arff.contains("sp_h_"+constant_correction(sentence_feat.elementAt(sp_index).words.word)))
					   arff.addElement("sp_h_"+constant_correction(sentence_feat.elementAt(sp_index).words.word));
			  if (!arff.contains("sp_sub_"+constant_correction(sentence_feat.elementAt(sp_index).words.subcategorization)))
					   arff.addElement("sp_sub_"+constant_correction(sentence_feat.elementAt(sp_index).words.subcategorization));
				
			  if (!arff.contains("sp_pos_"+constant_correction(sentence_feat.elementAt(sp_index).words.pos)))
				   arff.addElement("sp_pos_"+constant_correction(sentence_feat.elementAt(sp_index).words.pos));
			  
			  if (!arff.contains("sp_dp_"+constant_correction(sentence_feat.elementAt(sp_index).words.DPRL)))
				   arff.addElement("sp_dp_"+constant_correction(sentence_feat.elementAt(sp_index).words.DPRL));

			  if (!arff.contains("sp_SRL_"+constant_correction(sentence_feat.elementAt(sp_index).words.SRL)))
				   arff.addElement("sp_SRL_"+constant_correction(sentence_feat.elementAt(sp_index).words.SRL));
			//**************relational features 
			  // adding distance features 
			  int distt=-1;
			  if(tr_index!=sentence_feat.size())
			    distt= sentence_feat.elementAt(sp_index).word_word.distance.elementAt(tr_index);
			  if(!arff.contains("d_sp_tr_"+distt))
				 arff.addElement("d_sp_tr_"+distt);  
			  int distl=-1;
			  if(lm_index!=sentence_feat.size())
				 distl= sentence_feat.elementAt(sp_index).word_word.distance.elementAt(lm_index);
			  if(!arff.contains("d_sp_lm_"+distl))
				  arff.addElement("d_sp_lm_"+distl);
			  int disttl=-1;
			  if ((distl!=-1 & distt!=-1))
				  disttl=sentence_feat.elementAt(tr_index).word_word.distance.elementAt(lm_index);
			  if(!arff.contains("d_tr_lm_"+disttl))
				  arff.addElement("d_tr_lm_"+disttl);
			  // adding path features
    }// end of for allnonrel 

    }
	   ///////////
	   
	   public void collect_Relarff_att(Vector <String> arff, PrintWriter head1){
		      
		  for(int r=0;r<relations.size();r++){
			    int lm_index=relations.elementAt(r).wh_lm.head_index,tr_index=relations.elementAt(r).wh_tr.head_index,sp_index=relations.elementAt(r).wh_sp.head_index;
			  //if (relations.elementAt(r).wh_tr.head_index!=-1)
			
			    //**********************trajector features  
			    if (tr_index==sentence_feat.size()){
			    	if (!arff.contains("tr_h_undefined"))
						   arff.addElement("tr_h_undefined");
			    }
			    else {
			    	
					if (!arff.contains("tr_h_"+constant_correction(sentence_feat.elementAt(tr_index).words.word)))
						   arff.addElement("tr_h_"+constant_correction(sentence_feat.elementAt(tr_index).words.word));
					
					if (!arff.contains("tr_dp_"+constant_correction(sentence_feat.elementAt(tr_index).words.DPRL)))
						   arff.addElement("tr_dp_"+constant_correction(sentence_feat.elementAt(tr_index).words.DPRL));
					
					if (!arff.contains("tr_pos_"+constant_correction(sentence_feat.elementAt(tr_index).words.pos)))
						   arff.addElement("tr_pos_"+constant_correction(sentence_feat.elementAt(tr_index).words.pos));
					
					if (!arff.contains("tr_SRL_"+constant_correction(sentence_feat.elementAt(tr_index).words.SRL)))
						   arff.addElement("tr_SRL_"+constant_correction(sentence_feat.elementAt(tr_index).words.SRL));
					
					
					if (!arff.contains("tr_sub_"+constant_correction(sentence_feat.elementAt(tr_index).words.subcategorization)))
						   arff.addElement("tr_sub_"+constant_correction(sentence_feat.elementAt(tr_index).words.subcategorization));
			    }	
			 	//****************landmark features	
			    if (lm_index==sentence_feat.size()){
			    	if (!arff.contains("lm_h_undefined"))
						   arff.addElement("lm_h_undefined");
			    }
			    else{
					if (!arff.contains("lm_h_"+constant_correction(sentence_feat.elementAt(lm_index).words.word)))
						   arff.addElement("lm_h_"+constant_correction(sentence_feat.elementAt(lm_index).words.word));
					
					if (!arff.contains("lm_dp_"+constant_correction(sentence_feat.elementAt(lm_index).words.DPRL)))
						   arff.addElement("lm_dp_"+constant_correction(sentence_feat.elementAt(lm_index).words.DPRL));
					
					if (!arff.contains("lm_pos_"+constant_correction(sentence_feat.elementAt(lm_index).words.pos)))
						   arff.addElement("lm_pos_"+constant_correction(sentence_feat.elementAt(lm_index).words.pos));
					
					if (!arff.contains("lm_SRL_"+constant_correction(sentence_feat.elementAt(lm_index).words.SRL)))
						   arff.addElement("lm_SRL_"+constant_correction(sentence_feat.elementAt(lm_index).words.SRL));
					
					
					if (!arff.contains("lm_sub_"+constant_correction(sentence_feat.elementAt(lm_index).words.subcategorization)))
						   arff.addElement("lm_sub_"+constant_correction(sentence_feat.elementAt(lm_index).words.subcategorization));
			    }	
				//****************spatial indicator features
			    if (!arff.contains("sp_h_"+constant_correction(sentence_feat.elementAt(sp_index).words.word)))
					   arff.addElement("sp_h_"+constant_correction(sentence_feat.elementAt(sp_index).words.word));
			 	
				  if (!arff.contains("sp_sub_"+constant_correction(sentence_feat.elementAt(sp_index).words.subcategorization)))
						   arff.addElement("sp_sub_"+constant_correction(sentence_feat.elementAt(sp_index).words.subcategorization));
					
				  if (!arff.contains("sp_pos_"+constant_correction(sentence_feat.elementAt(sp_index).words.pos)))
					   arff.addElement("sp_pos_"+constant_correction(sentence_feat.elementAt(sp_index).words.pos));
				  
				  if (!arff.contains("sp_dp_"+constant_correction(sentence_feat.elementAt(sp_index).words.DPRL)))
					   arff.addElement("sp_dp_"+constant_correction(sentence_feat.elementAt(sp_index).words.DPRL));

				  if (!arff.contains("sp_SRL_"+constant_correction(sentence_feat.elementAt(sp_index).words.SRL)))
					   arff.addElement("sp_SRL_"+constant_correction(sentence_feat.elementAt(sp_index).words.SRL));
			//**************relational features 
				  // adding distance features 
				  int distt=-1;
				  if(tr_index!=sentence_feat.size())
				    distt= sentence_feat.elementAt(sp_index).word_word.distance.elementAt(tr_index);
				  if(!arff.contains("d_sp_tr_"+distt))
					 arff.addElement("d_sp_tr_"+distt);  
				  int distl=-1;
				  if(lm_index!=sentence_feat.size())
					 distl= sentence_feat.elementAt(sp_index).word_word.distance.elementAt(lm_index);
				  if(!arff.contains("d_sp_lm_"+distl))
					  arff.addElement("d_sp_lm_"+distl);
				  int disttl=-1;
				  if ((distl!=-1 & distt!=-1))
					  disttl=sentence_feat.elementAt(tr_index).word_word.distance.elementAt(lm_index);
				  if(!arff.contains("d_tr_lm_"+disttl))
					  arff.addElement("d_tr_lm_"+disttl);
				  // adding path features

				}// end of for relations 
		  // for(int r=0;r<Math.min (nonrelations.size(),relations.size()+1);r++)
			   for(int r=0;r<nonrelations.size();r++){
			    int lm_index=nonrelations.elementAt(r).lm,tr_index=nonrelations.elementAt(r).tr,sp_index=nonrelations.elementAt(r).sp;
			  //if (relations.elementAt(r).wh_tr.head_index!=-1)
			
			    //**********************trajector features  
					if (!arff.contains("tr_h_"+constant_correction(sentence_feat.elementAt(tr_index).words.word)))
						   arff.addElement("tr_h_"+constant_correction(sentence_feat.elementAt(tr_index).words.word));
					
					if (!arff.contains("tr_dp_"+constant_correction(sentence_feat.elementAt(tr_index).words.DPRL)))
						   arff.addElement("tr_dp_"+constant_correction(sentence_feat.elementAt(tr_index).words.DPRL));
					
					if (!arff.contains("tr_pos_"+constant_correction(sentence_feat.elementAt(tr_index).words.pos)))
						   arff.addElement("tr_pos_"+constant_correction(sentence_feat.elementAt(tr_index).words.pos));
					
					if (!arff.contains("tr_SRL_"+constant_correction(sentence_feat.elementAt(tr_index).words.SRL)))
						   arff.addElement("tr_SRL_"+constant_correction(sentence_feat.elementAt(tr_index).words.SRL));
					
					
					if (!arff.contains("tr_sub_"+constant_correction(sentence_feat.elementAt(tr_index).words.subcategorization)))
						   arff.addElement("tr_sub_"+constant_correction(sentence_feat.elementAt(tr_index).words.subcategorization));
					
			 	//****************landmark features	
					
					if (!arff.contains("lm_h_"+constant_correction(sentence_feat.elementAt(lm_index).words.word)))
						   arff.addElement("lm_h_"+constant_correction(sentence_feat.elementAt(lm_index).words.word));
					
					if (!arff.contains("lm_dp_"+constant_correction(sentence_feat.elementAt(lm_index).words.DPRL)))
						   arff.addElement("lm_dp_"+constant_correction(sentence_feat.elementAt(lm_index).words.DPRL));
					
					if (!arff.contains("lm_pos_"+constant_correction(sentence_feat.elementAt(lm_index).words.pos)))
						   arff.addElement("lm_pos_"+constant_correction(sentence_feat.elementAt(lm_index).words.pos));
					
					if (!arff.contains("lm_SRL_"+constant_correction(sentence_feat.elementAt(lm_index).words.SRL)))
						   arff.addElement("lm_SRL_"+constant_correction(sentence_feat.elementAt(lm_index).words.SRL));
					
					
					if (!arff.contains("lm_sub_"+constant_correction(sentence_feat.elementAt(lm_index).words.subcategorization)))
						   arff.addElement("lm_sub_"+constant_correction(sentence_feat.elementAt(lm_index).words.subcategorization));
					
				//****************spatial indicator features
				  if (!arff.contains("sp_h_"+constant_correction(sentence_feat.elementAt(sp_index).words.word)))
						   arff.addElement("sp_h_"+constant_correction(sentence_feat.elementAt(sp_index).words.word));
				  if (!arff.contains("sp_sub_"+constant_correction(sentence_feat.elementAt(sp_index).words.subcategorization)))
						   arff.addElement("sp_sub_"+constant_correction(sentence_feat.elementAt(sp_index).words.subcategorization));
					
				  if (!arff.contains("sp_pos_"+constant_correction(sentence_feat.elementAt(sp_index).words.pos)))
					   arff.addElement("sp_pos_"+constant_correction(sentence_feat.elementAt(sp_index).words.pos));
				  
				  if (!arff.contains("sp_dp_"+constant_correction(sentence_feat.elementAt(sp_index).words.DPRL)))
					   arff.addElement("sp_dp_"+constant_correction(sentence_feat.elementAt(sp_index).words.DPRL));

				  if (!arff.contains("sp_SRL_"+constant_correction(sentence_feat.elementAt(sp_index).words.SRL)))
					   arff.addElement("sp_SRL_"+constant_correction(sentence_feat.elementAt(sp_index).words.SRL));
					
	}// end of for nonrelations 
 
   }
	   /////////////////////////Below Collect the vector of all attributes for  constructing ARFF files
	   
	   public void collect_arff_att(Vector <String> arff, PrintWriter G_m)  // this module collects all the attributes in the data set to define in the header of arff file
	   {
		       
	       
		for (int r=0; r<relations.size();r++){
			
			//if (relations.elementAt(r).wh_tr.head_index!=-1)
			if (!arff.contains("tr_"+constant_correction(relations.elementAt(r).wh_tr.ph_head)))
				   arff.addElement("tr_"+constant_correction(relations.elementAt(r).wh_tr.ph_head));
			
			//if (relations.elementAt(r).wh_lm.head_index!=-1)
			if (!arff.contains("lm_"+constant_correction(relations.elementAt(r).wh_lm.ph_head)))
				   arff.addElement("lm_"+constant_correction(relations.elementAt(r).wh_lm.ph_head));
			if (!arff.contains("sp_"+constant_correction(relations.elementAt(r).wh_sp.ph_head)))
				   arff.addElement("sp_"+constant_correction(relations.elementAt(r).wh_sp.ph_head));
			if (!arff.contains("mo_"+constant_correction(relations.elementAt(r).wh_motion.ph_head)))
					arff.addElement("mo_"+constant_correction(relations.elementAt(r).wh_motion.ph_head)); 	
			if (!arff.contains("for_"+constant_correction(relations.elementAt(r).FrORe)))
				arff.addElement("for_"+constant_correction(relations.elementAt(r).FrORe)); 	
			if (!arff.contains("pa_"+constant_correction(relations.elementAt(r).path)))
				arff.addElement("pa_"+constant_correction(relations.elementAt(r).path)); 	
	        if (!arff.contains("dy_"+relations.elementAt(r).dynamic))
				arff.addElement("dy_"+relations.elementAt(r).dynamic); 	
	        if (!arff.contains("gum_"+constant_correction(relations.elementAt(r).GUM_mod)))
	        {arff.addElement("gum_"+constant_correction(relations.elementAt(r).GUM_mod)); 
//	        G_m.print(constant_correction(relations.elementAt(r).GUM_mod)+",");
	        }
	        for(int r1=0; r1<relations.elementAt(r).wh_sp.ph.size();r1++){
	    		  if (!constant_correction(relations.elementAt(r).wh_sp.ph.elementAt(r1)).equals(constant_correction(relations.elementAt(r).wh_sp.ph_head)))	
 	                 arff.addElement("sp1_"+constant_correction(relations.elementAt(r).wh_sp.ph.elementAt(r1)));}
	        
	        for(int r1=0; r1<relations.elementAt(r).wh_tr.ph.size();r1++){
	    		  if (!constant_correction(relations.elementAt(r).wh_tr.ph.elementAt(r1)).equals(constant_correction(relations.elementAt(r).wh_tr.ph_head)))	
	    		    if (!arff.contains("tr1_"+constant_correction(relations.elementAt(r).wh_tr.ph.elementAt(r1))))
	                 arff.addElement("tr1_"+constant_correction(relations.elementAt(r).wh_tr.ph.elementAt(r1)));}
	        
	        for(int r1=0; r1<relations.elementAt(r).wh_lm.ph.size();r1++){
	    		  if (!constant_correction(relations.elementAt(r).wh_lm.ph.elementAt(r1)).equals(constant_correction(relations.elementAt(r).wh_lm.ph_head)))	
	    		    if (!arff.contains("lm1_"+constant_correction(relations.elementAt(r).wh_lm.ph.elementAt(r1))))
	                 arff.addElement("lm1_"+constant_correction(relations.elementAt(r).wh_lm.ph.elementAt(r1)));}
		
		} 

	   }
	   public String hierachyindex(String s){//the structured output of RCC5+DIR+DIS
		   s=s.toLowerCase();
		   s=s.replaceAll(" ","");
		   s=s.replaceAll("\"","");
		   String Hind=null;
		   boolean flag= false;
		   if (s.contains("pp")) s="pp";
		  // String[] relat={"SR","NSR"};
		  // String[] general={"Region","Direction","Distance"};
		   String[]  Rcc5={"pp","ec","eq","dc","po"};
		   for(int i=0;i<Rcc5.length && flag==false;i++)
		   if (Rcc5[i].contains(s.toLowerCase()))
		       {
			   Hind="1/1/"+Integer.toString(i+1);
			   flag=true;
			   }
		   //String [] dir_specific={"relative","absolute"};
		   String [] dir_Rvalue={"below","left","right","behind","front","above"};
		   for(int i=0;i<dir_Rvalue.length&& flag==false;i++){
		   if (dir_Rvalue[i].contains(s)){
			   Hind="1/2/1/"+Integer.toString(i+1);
			   flag=true;
		     }
		   }
		   String [] dir_Avalue={"west","south","east","north","sw","nw","se","ne"};
		   for(int i=0;i<dir_Avalue.length&& flag==false;i++){
			   if (dir_Avalue[i].contains(s)){
				   Hind="1/2/2/"+Integer.toString(i+1);
				   flag=true;
			     } 
			   }
		   
		   if (!flag){Hind="1/3";}
		   if (Hind==null) System.out.print("Error#404: spatial type is missing!!");
		   return Hind;
		  
	      }
	   ///////////
	   //This module makes arff data lines for hierarchical classification RCC5+DIR(R,A)+Dis+NSR
	  public void add_arff_data_LangCalHierarch(Vector <String> atts , PrintWriter p,PrintWriter pN){
	   
	 
	   for (int r=0; r<relations.size();r++)
		{   
		 String TempyHierar="";
		 Vector <Integer> featList=new Vector <Integer>();
		 for (int r_t1=0; r_t1<1;r_t1++) ///temporary make one label and delete: relations.elementAt(r).calc_type.sp_value.size()
		 { 
		  if (r_t1!=0) TempyHierar=TempyHierar+"@";
			 TempyHierar=TempyHierar+hierachyindex(relations.elementAt(r).calc_type.sp_value.elementAt(r_t1));
		  }
		 
		  //String temty=relations.elementAt(r).calc_type.sp_value.elementAt(0).replaceAll("\"","").trim().toUpperCase();
   		//  if (!labels.contains(temty) ) temty="NONE";
   		 
   		
   		  int lm_index=relations.elementAt(r).wh_lm.head_index,tr_index=relations.elementAt(r).wh_tr.head_index,sp_index=relations.elementAt(r).wh_sp.head_index;
			 ////********** Features of  trajectors		
			 if (tr_index==sentence_feat.size()){
				 featList.addElement(atts.indexOf("tr_h_undefined"));
			    }
			 else{
				 featList.addElement(atts.indexOf("tr_h_"+constant_correction(sentence_feat.elementAt(tr_index).words.word)));
			     featList.addElement(atts.indexOf("tr_dp_"+constant_correction(sentence_feat.elementAt(tr_index).words.DPRL)));
			     featList.addElement(atts.indexOf("tr_pos_"+constant_correction(sentence_feat.elementAt(tr_index).words.pos)));
			     featList.addElement(atts.indexOf("tr_SRL_"+constant_correction(sentence_feat.elementAt(tr_index).words.SRL)));
			     featList.addElement(atts.indexOf("tr_sub_"+constant_correction(sentence_feat.elementAt(tr_index).words.subcategorization)));
			 }
	      	//****************landmark features	
			 if (lm_index==sentence_feat.size()){
				 featList.addElement(atts.indexOf("lm_h_undefined"));
			    }
			 else{
			     featList.addElement(atts.indexOf("lm_h_"+constant_correction(sentence_feat.elementAt(lm_index).words.word)));
			     featList.addElement(atts.indexOf("lm_dp_"+constant_correction(sentence_feat.elementAt(lm_index).words.DPRL)));
			     featList.addElement(atts.indexOf("lm_pos_"+constant_correction(sentence_feat.elementAt(lm_index).words.pos)));
			     featList.addElement(atts.indexOf("lm_SRL_"+constant_correction(sentence_feat.elementAt(lm_index).words.SRL)));
			     featList.addElement(atts.indexOf("lm_sub_"+constant_correction(sentence_feat.elementAt(lm_index).words.subcategorization)));
			 }
	    //*********** spatial indicator features 
			  
			     featList.addElement(atts.indexOf("sp_h_"+constant_correction(sentence_feat.elementAt(sp_index).words.word)));
			     featList.addElement(atts.indexOf("sp_dp_"+constant_correction(sentence_feat.elementAt(sp_index).words.DPRL)));
			     featList.addElement(atts.indexOf("sp_pos_"+constant_correction(sentence_feat.elementAt(sp_index).words.pos)));
			     featList.addElement(atts.indexOf("sp_SRL_"+constant_correction(sentence_feat.elementAt(sp_index).words.SRL)));
			     featList.addElement(atts.indexOf("sp_sub_"+constant_correction(sentence_feat.elementAt(sp_index).words.subcategorization)));
			
          Collections.sort(featList);
	    		p.print("{");
	    		for(int t1=0; t1<featList.size();t1++){
	    		if(featList.elementAt(t1)!=-1)
		    	p.print((featList.elementAt(t1)+1)+" 1,");}
	    		
	    		p.println("2907 "+TempyHierar+"}");
	    	 }
		   for (int r=0; r<Math.min(nonrelations.size(),relations.size()+1);r++)
			{  Vector <Integer>  featList=new Vector <Integer>();
			 int lm_index=nonrelations.elementAt(r).lm,tr_index=nonrelations.elementAt(r).tr,sp_index=nonrelations.elementAt(r).sp;
			 ////********** Features of  trajectors				
				 featList.addElement(atts.indexOf("tr_h_"+constant_correction(sentence_feat.elementAt(tr_index).words.word)));
			     featList.addElement(atts.indexOf("tr_dp_"+constant_correction(sentence_feat.elementAt(tr_index).words.DPRL)));
			     featList.addElement(atts.indexOf("tr_pos_"+constant_correction(sentence_feat.elementAt(tr_index).words.pos)));
			     featList.addElement(atts.indexOf("tr_SRL_"+constant_correction(sentence_feat.elementAt(tr_index).words.SRL)));
			     featList.addElement(atts.indexOf("tr_sub_"+constant_correction(sentence_feat.elementAt(tr_index).words.subcategorization)));
			
	      	//****************landmark features	
			
			     featList.addElement(atts.indexOf("lm_h_"+constant_correction(sentence_feat.elementAt(lm_index).words.word)));
			     featList.addElement(atts.indexOf("lm_dp_"+constant_correction(sentence_feat.elementAt(lm_index).words.DPRL)));
			     featList.addElement(atts.indexOf("lm_pos_"+constant_correction(sentence_feat.elementAt(lm_index).words.pos)));
			     featList.addElement(atts.indexOf("lm_SRL_"+constant_correction(sentence_feat.elementAt(lm_index).words.SRL)));
			     featList.addElement(atts.indexOf("lm_sub_"+constant_correction(sentence_feat.elementAt(lm_index).words.subcategorization)));
			
	    	//*********** spatial indicator features 
			  
			     featList.addElement(atts.indexOf("sp_h_"+constant_correction(sentence_feat.elementAt(sp_index).words.word)));
			     featList.addElement(atts.indexOf("sp_dp_"+constant_correction(sentence_feat.elementAt(sp_index).words.DPRL)));
			     featList.addElement(atts.indexOf("sp_pos_"+constant_correction(sentence_feat.elementAt(sp_index).words.pos)));
			     featList.addElement(atts.indexOf("sp_SRL_"+constant_correction(sentence_feat.elementAt(sp_index).words.SRL)));
			     featList.addElement(atts.indexOf("sp_sub_"+constant_correction(sentence_feat.elementAt(sp_index).words.subcategorization)));
			     
			     // adding distance features 
			     featList.addElement(atts.indexOf("d_sp_tr_"+sentence_feat.elementAt(sp_index).word_word.distance.elementAt(tr_index)));
				 featList.addElement(atts.indexOf("d_sp_lm_"+sentence_feat.elementAt(sp_index).word_word.distance.elementAt(lm_index)));
				 featList.addElement(atts.indexOf("d_tr_lm_"+sentence_feat.elementAt(tr_index).word_word.distance.elementAt(lm_index)));
				  // adding path features

			
       Collections.sort(featList);
	    		p.print("{");
	    		for(int t1=0; t1<featList.size();t1++){
	    		if(featList.elementAt(t1)!=-1)
		    	p.print((featList.elementAt(t1)+1)+" 1,");}
	    		p.println("2907 "+"2}");
		    }
		   for (int r=Math.min(nonrelations.size(),relations.size()+1);r<nonrelations.size();r++)
			{  Vector<Integer> featListNeg=new Vector <Integer>();
			 int lm_index=nonrelations.elementAt(r).lm,tr_index=nonrelations.elementAt(r).tr,sp_index=nonrelations.elementAt(r).sp;
			 ////********** Features of  trajectors				
				 featListNeg.addElement(atts.indexOf("tr_h_"+constant_correction(sentence_feat.elementAt(tr_index).words.word)));
			     featListNeg.addElement(atts.indexOf("tr_dp_"+constant_correction(sentence_feat.elementAt(tr_index).words.DPRL)));
			     featListNeg.addElement(atts.indexOf("tr_pos_"+constant_correction(sentence_feat.elementAt(tr_index).words.pos)));
			     featListNeg.addElement(atts.indexOf("tr_SRL_"+constant_correction(sentence_feat.elementAt(tr_index).words.SRL)));
			     featListNeg.addElement(atts.indexOf("tr_sub_"+constant_correction(sentence_feat.elementAt(tr_index).words.subcategorization)));
			
	      	//****************landmark features	
			
			     featListNeg.addElement(atts.indexOf("lm_h_"+constant_correction(sentence_feat.elementAt(lm_index).words.word)));
			     featListNeg.addElement(atts.indexOf("lm_dp_"+constant_correction(sentence_feat.elementAt(lm_index).words.DPRL)));
			     featListNeg.addElement(atts.indexOf("lm_pos_"+constant_correction(sentence_feat.elementAt(lm_index).words.pos)));
			     featListNeg.addElement(atts.indexOf("lm_SRL_"+constant_correction(sentence_feat.elementAt(lm_index).words.SRL)));
			     featListNeg.addElement(atts.indexOf("lm_sub_"+constant_correction(sentence_feat.elementAt(lm_index).words.subcategorization)));
			
	    	//*********** spatial indicator features 
			  
			     featListNeg.addElement(atts.indexOf("sp_h_"+constant_correction(sentence_feat.elementAt(sp_index).words.word)));
			     featListNeg.addElement(atts.indexOf("sp_dp_"+constant_correction(sentence_feat.elementAt(sp_index).words.DPRL)));
			     featListNeg.addElement(atts.indexOf("sp_pos_"+constant_correction(sentence_feat.elementAt(sp_index).words.pos)));
			     featListNeg.addElement(atts.indexOf("sp_SRL_"+constant_correction(sentence_feat.elementAt(sp_index).words.SRL)));
			     featListNeg.addElement(atts.indexOf("sp_sub_"+constant_correction(sentence_feat.elementAt(sp_index).words.subcategorization)));
			//******distance feature for negative examples
			     featListNeg.addElement(atts.indexOf("d_sp_tr_"+sentence_feat.elementAt(sp_index).word_word.distance.elementAt(tr_index)));
				 featListNeg.addElement(atts.indexOf("d_sp_lm_"+sentence_feat.elementAt(sp_index).word_word.distance.elementAt(lm_index)));
				 featListNeg.addElement(atts.indexOf("d_tr_lm_"+sentence_feat.elementAt(tr_index).word_word.distance.elementAt(lm_index)));
				 
      Collections.sort(featListNeg);
	    		pN.print("{");
	    		for(int t1=0; t1<featListNeg.size();t1++){
	    		if(featListNeg.elementAt(t1)!=-1)
	    			
		    	pN.print((featListNeg.elementAt(t1)+1)+" 1,");}
	    		pN.println("2907 "+"2}");
		    }
		   }
	   
	   
	   
	   
	   ////// 
	   public void add_arff_data_LangCal(Vector <String> atts , PrintWriter p,PrintWriter pN){ // this process makes arff files for direct mapping to RCC
		   String  labels="TTP EC EQ DC TPPI PO NTPP NTPPI";
		   for (int r=0; r<relations.size();r++)
			{  
			   Vector <Integer>  featList=new Vector <Integer>();
			   String temty=relations.elementAt(r).calc_type.sp_value.elementAt(0).replaceAll("\"","").trim().toUpperCase();
	    		if (!labels.contains(temty) ) temty="NONE";
	    		if (temty.contains("TPP")) temty="PP";
	    		int lm_index=relations.elementAt(r).wh_lm.head_index,tr_index=relations.elementAt(r).wh_tr.head_index,sp_index=relations.elementAt(r).wh_sp.head_index;
				 ////********** Features of  trajectors		
				 if (tr_index==sentence_feat.size()){
					 featList.addElement(atts.indexOf("tr_h_undefined"));
				    }
				 else{
					 featList.addElement(atts.indexOf("tr_h_"+constant_correction(sentence_feat.elementAt(tr_index).words.word)));
				     featList.addElement(atts.indexOf("tr_dp_"+constant_correction(sentence_feat.elementAt(tr_index).words.DPRL)));
				     featList.addElement(atts.indexOf("tr_pos_"+constant_correction(sentence_feat.elementAt(tr_index).words.pos)));
				     featList.addElement(atts.indexOf("tr_SRL_"+constant_correction(sentence_feat.elementAt(tr_index).words.SRL)));
				     featList.addElement(atts.indexOf("tr_sub_"+constant_correction(sentence_feat.elementAt(tr_index).words.subcategorization)));
				 }
		      	//****************landmark features	
				 if (lm_index==sentence_feat.size()){
					 featList.addElement(atts.indexOf("lm_h_undefined"));
				    }
				 else{
				     featList.addElement(atts.indexOf("lm_h_"+constant_correction(sentence_feat.elementAt(lm_index).words.word)));
				     featList.addElement(atts.indexOf("lm_dp_"+constant_correction(sentence_feat.elementAt(lm_index).words.DPRL)));
				     featList.addElement(atts.indexOf("lm_pos_"+constant_correction(sentence_feat.elementAt(lm_index).words.pos)));
				     featList.addElement(atts.indexOf("lm_SRL_"+constant_correction(sentence_feat.elementAt(lm_index).words.SRL)));
				     featList.addElement(atts.indexOf("lm_sub_"+constant_correction(sentence_feat.elementAt(lm_index).words.subcategorization)));
				 }
		    //*********** spatial indicator features 
				  
				     featList.addElement(atts.indexOf("sp_h_"+constant_correction(sentence_feat.elementAt(sp_index).words.word)));
				     featList.addElement(atts.indexOf("sp_dp_"+constant_correction(sentence_feat.elementAt(sp_index).words.DPRL)));
				     featList.addElement(atts.indexOf("sp_pos_"+constant_correction(sentence_feat.elementAt(sp_index).words.pos)));
				     featList.addElement(atts.indexOf("sp_SRL_"+constant_correction(sentence_feat.elementAt(sp_index).words.SRL)));
				     featList.addElement(atts.indexOf("sp_sub_"+constant_correction(sentence_feat.elementAt(sp_index).words.subcategorization)));
				
             Collections.sort(featList);
		    		p.print("{");
		    		for(int t1=0; t1<featList.size();t1++){
		    		if(featList.elementAt(t1)!=-1)
			    	p.print(featList.elementAt(t1)+" 1,");}
		    		p.println("2802 "+temty+"}");
		    	 }
			   for (int r=0; r<Math.min(nonrelations.size(),relations.size()+1);r++)
				{  Vector <Integer>  featList=new Vector <Integer>();
				 int lm_index=nonrelations.elementAt(r).lm,tr_index=nonrelations.elementAt(r).tr,sp_index=nonrelations.elementAt(r).sp;
				 ////********** Features of  trajectors				
					 featList.addElement(atts.indexOf("tr_h_"+constant_correction(sentence_feat.elementAt(tr_index).words.word)));
				     featList.addElement(atts.indexOf("tr_dp_"+constant_correction(sentence_feat.elementAt(tr_index).words.DPRL)));
				     featList.addElement(atts.indexOf("tr_pos_"+constant_correction(sentence_feat.elementAt(tr_index).words.pos)));
				     featList.addElement(atts.indexOf("tr_SRL_"+constant_correction(sentence_feat.elementAt(tr_index).words.SRL)));
				     featList.addElement(atts.indexOf("tr_sub_"+constant_correction(sentence_feat.elementAt(tr_index).words.subcategorization)));
				
		      	//****************landmark features	
				
				     featList.addElement(atts.indexOf("lm_h_"+constant_correction(sentence_feat.elementAt(lm_index).words.word)));
				     featList.addElement(atts.indexOf("lm_dp_"+constant_correction(sentence_feat.elementAt(lm_index).words.DPRL)));
				     featList.addElement(atts.indexOf("lm_pos_"+constant_correction(sentence_feat.elementAt(lm_index).words.pos)));
				     featList.addElement(atts.indexOf("lm_SRL_"+constant_correction(sentence_feat.elementAt(lm_index).words.SRL)));
				     featList.addElement(atts.indexOf("lm_sub_"+constant_correction(sentence_feat.elementAt(lm_index).words.subcategorization)));
				
		    	//*********** spatial indicator features 
				  
				     featList.addElement(atts.indexOf("sp_h_"+constant_correction(sentence_feat.elementAt(sp_index).words.word)));
				     featList.addElement(atts.indexOf("sp_dp_"+constant_correction(sentence_feat.elementAt(sp_index).words.DPRL)));
				     featList.addElement(atts.indexOf("sp_pos_"+constant_correction(sentence_feat.elementAt(sp_index).words.pos)));
				     featList.addElement(atts.indexOf("sp_SRL_"+constant_correction(sentence_feat.elementAt(sp_index).words.SRL)));
				     featList.addElement(atts.indexOf("sp_sub_"+constant_correction(sentence_feat.elementAt(sp_index).words.subcategorization)));
				     
				     // adding distance features 
				     featList.addElement(atts.indexOf("d_sp_tr_"+sentence_feat.elementAt(sp_index).word_word.distance.elementAt(tr_index)));
					 featList.addElement(atts.indexOf("d_sp_lm_"+sentence_feat.elementAt(sp_index).word_word.distance.elementAt(lm_index)));
					 featList.addElement(atts.indexOf("d_tr_lm_"+sentence_feat.elementAt(tr_index).word_word.distance.elementAt(lm_index)));
					  // adding path features

				
            Collections.sort(featList);
		    		p.print("{");
		    		for(int t1=0; t1<featList.size();t1++){
		    		if(featList.elementAt(t1)!=-1)
			    	p.print(featList.elementAt(t1)+" 1,");}
		    		p.println("2802 "+"NONE}");
			    }
			   for (int r=Math.min(nonrelations.size(),relations.size()+1);r<nonrelations.size();r++)
				{  Vector<Integer> featListNeg=new Vector <Integer>();
				 int lm_index=nonrelations.elementAt(r).lm,tr_index=nonrelations.elementAt(r).tr,sp_index=nonrelations.elementAt(r).sp;
				 ////********** Features of  trajectors				
					 featListNeg.addElement(atts.indexOf("tr_h_"+constant_correction(sentence_feat.elementAt(tr_index).words.word)));
				     featListNeg.addElement(atts.indexOf("tr_dp_"+constant_correction(sentence_feat.elementAt(tr_index).words.DPRL)));
				     featListNeg.addElement(atts.indexOf("tr_pos_"+constant_correction(sentence_feat.elementAt(tr_index).words.pos)));
				     featListNeg.addElement(atts.indexOf("tr_SRL_"+constant_correction(sentence_feat.elementAt(tr_index).words.SRL)));
				     featListNeg.addElement(atts.indexOf("tr_sub_"+constant_correction(sentence_feat.elementAt(tr_index).words.subcategorization)));
				
		      	//****************landmark features	
				
				     featListNeg.addElement(atts.indexOf("lm_h_"+constant_correction(sentence_feat.elementAt(lm_index).words.word)));
				     featListNeg.addElement(atts.indexOf("lm_dp_"+constant_correction(sentence_feat.elementAt(lm_index).words.DPRL)));
				     featListNeg.addElement(atts.indexOf("lm_pos_"+constant_correction(sentence_feat.elementAt(lm_index).words.pos)));
				     featListNeg.addElement(atts.indexOf("lm_SRL_"+constant_correction(sentence_feat.elementAt(lm_index).words.SRL)));
				     featListNeg.addElement(atts.indexOf("lm_sub_"+constant_correction(sentence_feat.elementAt(lm_index).words.subcategorization)));
				
		    	//*********** spatial indicator features 
				  
				     featListNeg.addElement(atts.indexOf("sp_h_"+constant_correction(sentence_feat.elementAt(sp_index).words.word)));
				     featListNeg.addElement(atts.indexOf("sp_dp_"+constant_correction(sentence_feat.elementAt(sp_index).words.DPRL)));
				     featListNeg.addElement(atts.indexOf("sp_pos_"+constant_correction(sentence_feat.elementAt(sp_index).words.pos)));
				     featListNeg.addElement(atts.indexOf("sp_SRL_"+constant_correction(sentence_feat.elementAt(sp_index).words.SRL)));
				     featListNeg.addElement(atts.indexOf("sp_sub_"+constant_correction(sentence_feat.elementAt(sp_index).words.subcategorization)));
				//******distance feature for negative examples
				     featListNeg.addElement(atts.indexOf("d_sp_tr_"+sentence_feat.elementAt(sp_index).word_word.distance.elementAt(tr_index)));
					 featListNeg.addElement(atts.indexOf("d_sp_lm_"+sentence_feat.elementAt(sp_index).word_word.distance.elementAt(lm_index)));
					 featListNeg.addElement(atts.indexOf("d_tr_lm_"+sentence_feat.elementAt(tr_index).word_word.distance.elementAt(lm_index)));
					 
           Collections.sort(featListNeg);
		    		pN.print("{");
		    		for(int t1=0; t1<featListNeg.size();t1++){
		    		if(featListNeg.elementAt(t1)!=-1)
			    	pN.print(featListNeg.elementAt(t1)+" 1,");}
		    		pN.println("2802 "+"NONE}");
			    }
		   }
	   public void writeSVMontology(PrintWriter p){
		   for (int r=0;r<relations.size();r++){
			   int i=0;
			   for (i=0;i<relations.elementAt(r).calc_type.flat_output.length-1;i++)
			     p.print(relations.elementAt(r).calc_type.flat_output[i]+","); 
		       p.println(relations.elementAt(r).calc_type.flat_output[i]);
	    }
	   }
	   //////
	  // mapping to a combination of calculi arff to make just a multi-class classifier for all the leaves  of the ontology
	   public void add_arff_data_LangMULTIcal(Vector <String> atts , PrintWriter p,PrintWriter pN){ // this process makes arff files for direct mapping to RCC
		   
		   String  labels="ttp ec eq dc tppi po ntpp ntppi below left right behind front above west south east north sw nw se ne";
		   
		   for (int r=0; r<relations.size();r++)
			{  
			   Vector <Integer>  featList=new Vector <Integer>();
			   String temty=relations.elementAt(r).calc_type.sp_value.elementAt(0).replaceAll("\"","").trim().toLowerCase();
	    		if (!labels.contains(temty) ) temty="dis";
	    		if (temty.contains("tpp")) temty="pp";
	    		int lm_index=relations.elementAt(r).wh_lm.head_index,tr_index=relations.elementAt(r).wh_tr.head_index,sp_index=relations.elementAt(r).wh_sp.head_index;
				 ////********** Features of  trajectors		
				 if (tr_index==sentence_feat.size()){
					 featList.addElement(atts.indexOf("tr_h_undefined"));
				    }
				 else{
					 featList.addElement(atts.indexOf("tr_h_"+constant_correction(sentence_feat.elementAt(tr_index).words.word)));
				     featList.addElement(atts.indexOf("tr_dp_"+constant_correction(sentence_feat.elementAt(tr_index).words.DPRL)));
				     featList.addElement(atts.indexOf("tr_pos_"+constant_correction(sentence_feat.elementAt(tr_index).words.pos)));
				     featList.addElement(atts.indexOf("tr_SRL_"+constant_correction(sentence_feat.elementAt(tr_index).words.SRL)));
				     featList.addElement(atts.indexOf("tr_sub_"+constant_correction(sentence_feat.elementAt(tr_index).words.subcategorization)));
				 }
		      	//****************landmark features	
				 if (lm_index==sentence_feat.size()){
					 featList.addElement(atts.indexOf("lm_h_undefined"));
				    }
				 else{
				     featList.addElement(atts.indexOf("lm_h_"+constant_correction(sentence_feat.elementAt(lm_index).words.word)));
				     featList.addElement(atts.indexOf("lm_dp_"+constant_correction(sentence_feat.elementAt(lm_index).words.DPRL)));
				     featList.addElement(atts.indexOf("lm_pos_"+constant_correction(sentence_feat.elementAt(lm_index).words.pos)));
				     featList.addElement(atts.indexOf("lm_SRL_"+constant_correction(sentence_feat.elementAt(lm_index).words.SRL)));
				     featList.addElement(atts.indexOf("lm_sub_"+constant_correction(sentence_feat.elementAt(lm_index).words.subcategorization)));
				 }
		    //*********** spatial indicator features 
				  
				     featList.addElement(atts.indexOf("sp_h_"+constant_correction(sentence_feat.elementAt(sp_index).words.word)));
				     featList.addElement(atts.indexOf("sp_dp_"+constant_correction(sentence_feat.elementAt(sp_index).words.DPRL)));
				     featList.addElement(atts.indexOf("sp_pos_"+constant_correction(sentence_feat.elementAt(sp_index).words.pos)));
				     featList.addElement(atts.indexOf("sp_SRL_"+constant_correction(sentence_feat.elementAt(sp_index).words.SRL)));
				     featList.addElement(atts.indexOf("sp_sub_"+constant_correction(sentence_feat.elementAt(sp_index).words.subcategorization)));
				
             Collections.sort(featList);
		    		p.print("{");
		    		for(int t1=0; t1<featList.size();t1++){
		    		if(featList.elementAt(t1)!=-1)
			    	p.print((featList.elementAt(t1))+" 1,");}
		    		p.println("2906 "+temty+"}");
		    	 }
			   for (int r=0; r<Math.min(nonrelations.size(),relations.size()+1);r++)
				{  Vector <Integer>  featList=new Vector <Integer>();
				 int lm_index=nonrelations.elementAt(r).lm,tr_index=nonrelations.elementAt(r).tr,sp_index=nonrelations.elementAt(r).sp;
				 ////********** Features of  trajectors				
					 featList.addElement(atts.indexOf("tr_h_"+constant_correction(sentence_feat.elementAt(tr_index).words.word)));
				     featList.addElement(atts.indexOf("tr_dp_"+constant_correction(sentence_feat.elementAt(tr_index).words.DPRL)));
				     featList.addElement(atts.indexOf("tr_pos_"+constant_correction(sentence_feat.elementAt(tr_index).words.pos)));
				     featList.addElement(atts.indexOf("tr_SRL_"+constant_correction(sentence_feat.elementAt(tr_index).words.SRL)));
				     featList.addElement(atts.indexOf("tr_sub_"+constant_correction(sentence_feat.elementAt(tr_index).words.subcategorization)));
				
		      	//****************landmark features	
				
				     featList.addElement(atts.indexOf("lm_h_"+constant_correction(sentence_feat.elementAt(lm_index).words.word)));
				     featList.addElement(atts.indexOf("lm_dp_"+constant_correction(sentence_feat.elementAt(lm_index).words.DPRL)));
				     featList.addElement(atts.indexOf("lm_pos_"+constant_correction(sentence_feat.elementAt(lm_index).words.pos)));
				     featList.addElement(atts.indexOf("lm_SRL_"+constant_correction(sentence_feat.elementAt(lm_index).words.SRL)));
				     featList.addElement(atts.indexOf("lm_sub_"+constant_correction(sentence_feat.elementAt(lm_index).words.subcategorization)));
				
		    	//*********** spatial indicator features 
				  
				     featList.addElement(atts.indexOf("sp_h_"+constant_correction(sentence_feat.elementAt(sp_index).words.word)));
				     featList.addElement(atts.indexOf("sp_dp_"+constant_correction(sentence_feat.elementAt(sp_index).words.DPRL)));
				     featList.addElement(atts.indexOf("sp_pos_"+constant_correction(sentence_feat.elementAt(sp_index).words.pos)));
				     featList.addElement(atts.indexOf("sp_SRL_"+constant_correction(sentence_feat.elementAt(sp_index).words.SRL)));
				     featList.addElement(atts.indexOf("sp_sub_"+constant_correction(sentence_feat.elementAt(sp_index).words.subcategorization)));
				     
				     // adding distance features 
				     featList.addElement(atts.indexOf("d_sp_tr_"+sentence_feat.elementAt(sp_index).word_word.distance.elementAt(tr_index)));
					 featList.addElement(atts.indexOf("d_sp_lm_"+sentence_feat.elementAt(sp_index).word_word.distance.elementAt(lm_index)));
					 featList.addElement(atts.indexOf("d_tr_lm_"+sentence_feat.elementAt(tr_index).word_word.distance.elementAt(lm_index)));
					  // adding path features

				
            Collections.sort(featList);
		    		p.print("{");
		    		for(int t1=0; t1<featList.size();t1++){
		    		if(featList.elementAt(t1)!=-1)
			    	p.print((featList.elementAt(t1))+" 1,");}
		    		p.println("2906 "+"NONE}");
			    }
			   for (int r=Math.min(nonrelations.size(),relations.size()+1);r<nonrelations.size();r++)
				{  Vector<Integer> featListNeg=new Vector <Integer>();
				 int lm_index=nonrelations.elementAt(r).lm,tr_index=nonrelations.elementAt(r).tr,sp_index=nonrelations.elementAt(r).sp;
				 ////********** Features of  trajectors				
					 featListNeg.addElement(atts.indexOf("tr_h_"+constant_correction(sentence_feat.elementAt(tr_index).words.word)));
				     featListNeg.addElement(atts.indexOf("tr_dp_"+constant_correction(sentence_feat.elementAt(tr_index).words.DPRL)));
				     featListNeg.addElement(atts.indexOf("tr_pos_"+constant_correction(sentence_feat.elementAt(tr_index).words.pos)));
				     featListNeg.addElement(atts.indexOf("tr_SRL_"+constant_correction(sentence_feat.elementAt(tr_index).words.SRL)));
				     featListNeg.addElement(atts.indexOf("tr_sub_"+constant_correction(sentence_feat.elementAt(tr_index).words.subcategorization)));
				
		      	//****************landmark features	
				
				     featListNeg.addElement(atts.indexOf("lm_h_"+constant_correction(sentence_feat.elementAt(lm_index).words.word)));
				     featListNeg.addElement(atts.indexOf("lm_dp_"+constant_correction(sentence_feat.elementAt(lm_index).words.DPRL)));
				     featListNeg.addElement(atts.indexOf("lm_pos_"+constant_correction(sentence_feat.elementAt(lm_index).words.pos)));
				     featListNeg.addElement(atts.indexOf("lm_SRL_"+constant_correction(sentence_feat.elementAt(lm_index).words.SRL)));
				     featListNeg.addElement(atts.indexOf("lm_sub_"+constant_correction(sentence_feat.elementAt(lm_index).words.subcategorization)));
				
		    	//*********** spatial indicator features 
				  
				     featListNeg.addElement(atts.indexOf("sp_h_"+constant_correction(sentence_feat.elementAt(sp_index).words.word)));
				     featListNeg.addElement(atts.indexOf("sp_dp_"+constant_correction(sentence_feat.elementAt(sp_index).words.DPRL)));
				     featListNeg.addElement(atts.indexOf("sp_pos_"+constant_correction(sentence_feat.elementAt(sp_index).words.pos)));
				     featListNeg.addElement(atts.indexOf("sp_SRL_"+constant_correction(sentence_feat.elementAt(sp_index).words.SRL)));
				     featListNeg.addElement(atts.indexOf("sp_sub_"+constant_correction(sentence_feat.elementAt(sp_index).words.subcategorization)));
				//******distance feature for negative examples
				     featListNeg.addElement(atts.indexOf("d_sp_tr_"+sentence_feat.elementAt(sp_index).word_word.distance.elementAt(tr_index)));
					 featListNeg.addElement(atts.indexOf("d_sp_lm_"+sentence_feat.elementAt(sp_index).word_word.distance.elementAt(lm_index)));
					 featListNeg.addElement(atts.indexOf("d_tr_lm_"+sentence_feat.elementAt(tr_index).word_word.distance.elementAt(lm_index)));
					 
           Collections.sort(featListNeg);
		    		pN.print("{");
		    		for(int t1=0; t1<featListNeg.size();t1++){
		    		if(featListNeg.elementAt(t1)!=-1)
			    	pN.print((featListNeg.elementAt(t1))+" 1,");}
		    		pN.println("2906 "+"NONE}");
			    }
		   }
	   
	   ////////
	   public void add_arff_data(Vector <String> atts , PrintWriter p){// this module makes the lines of the arff file data section  produced from the relations of one sentence
		  String  labels="TTP EC EQ DC TPPI PO NTPP NTPPI";
		   for (int r=0; r<relations.size();r++)
			{  Vector <Integer>  featList=new Vector <Integer>();
			   String temty=relations.elementAt(r).calc_type.sp_value.elementAt(0).replaceAll("\"","").trim().toUpperCase();
	    		if (!labels.contains(temty) ) temty="NONE";
	    	
	    		featList.addElement(atts.indexOf("tr_"+constant_correction(relations.elementAt(r).wh_tr.ph_head)));
	    		for(int r1=0; r1<relations.elementAt(r).wh_tr.ph.size();r1++){
		    		  if (!constant_correction(relations.elementAt(r).wh_tr.ph.elementAt(r1)).equals(constant_correction(relations.elementAt(r).wh_tr.ph_head)))	
		    		     featList.addElement(atts.indexOf("tr1_"+constant_correction(relations.elementAt(r).wh_tr.ph.elementAt(r1))));}
		    		
	    		
	    		featList.addElement(atts.indexOf("lm_"+constant_correction(relations.elementAt(r).wh_lm.ph_head)));
	    		for(int r1=0; r1<relations.elementAt(r).wh_lm.ph.size();r1++){
		    		  if (!constant_correction(relations.elementAt(r).wh_lm.ph.elementAt(r1)).equals(constant_correction(relations.elementAt(r).wh_lm.ph_head)))	
		    		     featList.addElement(atts.indexOf("lm1_"+constant_correction(relations.elementAt(r).wh_lm.ph.elementAt(r1))));}
		    		
	    		featList.addElement(atts.indexOf("sp_"+constant_correction(relations.elementAt(r).wh_sp.ph_head)));
	    		for(int r1=0; r1<relations.elementAt(r).wh_sp.ph.size();r1++){
	    		  if (!constant_correction(relations.elementAt(r).wh_sp.ph.elementAt(r1)).equals(constant_correction(relations.elementAt(r).wh_sp.ph_head)))	
	    		     featList.addElement(atts.indexOf("sp1_"+constant_correction(relations.elementAt(r).wh_sp.ph.elementAt(r1))));}
	    		
	    		featList.addElement(atts.indexOf("mo_"+constant_correction(relations.elementAt(r).wh_motion.ph_head)));
	    		featList.addElement(atts.indexOf("for_"+constant_correction(relations.elementAt(r).FrORe)));
	    		featList.addElement(atts.indexOf("pa_"+constant_correction(relations.elementAt(r).path)));
	    		featList.addElement(atts.indexOf("dy_"+relations.elementAt(r).dynamic));
	    		featList.addElement(atts.indexOf("gum_"+constant_correction(relations.elementAt(r).GUM_mod)));
	    		Collections.sort(featList);
	    		p.print("{");
	    		for(int t1=0; t1<featList.size();t1++){
	    		if(featList.elementAt(t1)!=-1)
		    	p.print(featList.elementAt(t1)+" 1,");}
	    		p.println("1098 "+temty+"}");
		    }
		   }
	   ///////////////////
//	   public void write_one_realtion_features(Vector <Integer>  featList, String prefix,int index, PrintWriter p){
//		   featList.addElement(atts.indexOf("tr_h_"+constant_correction(sentence_feat.elementAt(tr_index).words.word)));
//		     featList.addElement(atts.indexOf("tr_dp_"+constant_correction(sentence_feat.elementAt(tr_index).words.DPRL)));
//		     featList.addElement(atts.indexOf("tr_pos_"+constant_correction(sentence_feat.elementAt(tr_index).words.pos)));
//		     featList.addElement(atts.indexOf("tr_SRL_"+constant_correction(sentence_feat.elementAt(tr_index).words.SRL)));
//		     featList.addElement(atts.indexOf("tr_sub_"+constant_correction(sentence_feat.elementAt(tr_index).words.subcategorization)));
//		
//	   }
	   public void add_arff_ReldataCandidates(Vector <String> atts , PrintWriter p,PrintWriter pN){
		   // String  labels="SR NSR";
		   for (int r=0; r<allPosRelCandids.size();r++)
			{  
			 Vector <Integer>  featList=new Vector <Integer>();
			 int lm_index=allPosRelCandids.elementAt(r).lm,tr_index=allPosRelCandids.elementAt(r).tr,sp_index=allPosRelCandids.elementAt(r).sp;
			 ////********** Features of  trajectors		
			 if (tr_index==sentence_feat.size()){
				 featList.addElement(atts.indexOf("tr_h_undefined"));
			    }
			 else{
				 featList.addElement(atts.indexOf("tr_h_"+constant_correction(sentence_feat.elementAt(tr_index).words.word)));
			     featList.addElement(atts.indexOf("tr_dp_"+constant_correction(sentence_feat.elementAt(tr_index).words.DPRL)));
			     featList.addElement(atts.indexOf("tr_pos_"+constant_correction(sentence_feat.elementAt(tr_index).words.pos)));
			     featList.addElement(atts.indexOf("tr_SRL_"+constant_correction(sentence_feat.elementAt(tr_index).words.SRL)));
			     featList.addElement(atts.indexOf("tr_sub_"+constant_correction(sentence_feat.elementAt(tr_index).words.subcategorization)));
			 }
	      	//****************landmark features	
			 if (lm_index==sentence_feat.size()){
				 featList.addElement(atts.indexOf("lm_h_undefined"));
			    }
			 else{
			     featList.addElement(atts.indexOf("lm_h_"+constant_correction(sentence_feat.elementAt(lm_index).words.word)));
			     featList.addElement(atts.indexOf("lm_dp_"+constant_correction(sentence_feat.elementAt(lm_index).words.DPRL)));
			     featList.addElement(atts.indexOf("lm_pos_"+constant_correction(sentence_feat.elementAt(lm_index).words.pos)));
			     featList.addElement(atts.indexOf("lm_SRL_"+constant_correction(sentence_feat.elementAt(lm_index).words.SRL)));
			     featList.addElement(atts.indexOf("lm_sub_"+constant_correction(sentence_feat.elementAt(lm_index).words.subcategorization)));
			 }
	    //*********** spatial indicator features 
			  
			     featList.addElement(atts.indexOf("sp_h_"+constant_correction(sentence_feat.elementAt(sp_index).words.word)));
			     featList.addElement(atts.indexOf("sp_dp_"+constant_correction(sentence_feat.elementAt(sp_index).words.DPRL)));
			     featList.addElement(atts.indexOf("sp_pos_"+constant_correction(sentence_feat.elementAt(sp_index).words.pos)));
			     featList.addElement(atts.indexOf("sp_SRL_"+constant_correction(sentence_feat.elementAt(sp_index).words.SRL)));
			     featList.addElement(atts.indexOf("sp_sub_"+constant_correction(sentence_feat.elementAt(sp_index).words.subcategorization)));
	//**********			     // adding distance features 
			     
			  
			     
				  if(tr_index!=sentence_feat.size())
			        featList.addElement(atts.indexOf("d_sp_tr_"+sentence_feat.elementAt(sp_index).word_word.distance.elementAt(tr_index)));
				  if(lm_index!=sentence_feat.size())
				     featList.addElement(atts.indexOf("d_sp_lm_"+sentence_feat.elementAt(sp_index).word_word.distance.elementAt(lm_index)));
				  if(tr_index!=sentence_feat.size()& lm_index!=sentence_feat.size())
				  featList.addElement(atts.indexOf("d_tr_lm_"+sentence_feat.elementAt(tr_index).word_word.distance.elementAt(lm_index)));
				 
          Collections.sort(featList);
	    		p.print("{");
	    		for(int t1=0; t1<featList.size();t1++){
	    		if(featList.elementAt(t1)!=-1)
		    	p.print(featList.elementAt(t1)+" 1,");}
	    		p.println("212 "+"SR}");
	    		//p.println("2817 "+"SR}");
		    }
		   int neg=0;
		   //for (int r=0; r<Math.min(nonrelations.size(),relations.size()+1);r++)
		   for (int r=0; r<allnonrel.size();r++)
			 {
			  Vector <Integer>  featList=new Vector <Integer>();
			  int r1=selectedNegatives.elementAt(r);
			  int lm_index=allnonrel.elementAt(r1).lm,tr_index=allnonrel.elementAt(r1).tr,sp_index=allnonrel.elementAt(r1).sp;
			  
			  ////********** Features of  trajectors	
			  if (tr_index==sentence_feat.size()){
				 featList.addElement(atts.indexOf("tr_h_undefined"));
			    }
			  else{
				 featList.addElement(atts.indexOf("tr_h_"+constant_correction(sentence_feat.elementAt(tr_index).words.word)));
			     featList.addElement(atts.indexOf("tr_dp_"+constant_correction(sentence_feat.elementAt(tr_index).words.DPRL)));
			     featList.addElement(atts.indexOf("tr_pos_"+constant_correction(sentence_feat.elementAt(tr_index).words.pos)));
			     featList.addElement(atts.indexOf("tr_SRL_"+constant_correction(sentence_feat.elementAt(tr_index).words.SRL)));
			     featList.addElement(atts.indexOf("tr_sub_"+constant_correction(sentence_feat.elementAt(tr_index).words.subcategorization)));
			   }
	      	//****************landmark features	
			 if (lm_index==sentence_feat.size()){
				 featList.addElement(atts.indexOf("lm_h_undefined"));
			    }
			 else{
			
			     featList.addElement(atts.indexOf("lm_h_"+constant_correction(sentence_feat.elementAt(lm_index).words.word)));
			     featList.addElement(atts.indexOf("lm_dp_"+constant_correction(sentence_feat.elementAt(lm_index).words.DPRL)));
			     featList.addElement(atts.indexOf("lm_pos_"+constant_correction(sentence_feat.elementAt(lm_index).words.pos)));
			     featList.addElement(atts.indexOf("lm_SRL_"+constant_correction(sentence_feat.elementAt(lm_index).words.SRL)));
			     featList.addElement(atts.indexOf("lm_sub_"+constant_correction(sentence_feat.elementAt(lm_index).words.subcategorization)));
			 }
	    	//*********** spatial indicator features 
			  
			     featList.addElement(atts.indexOf("sp_h_"+constant_correction(sentence_feat.elementAt(sp_index).words.word)));
			     featList.addElement(atts.indexOf("sp_dp_"+constant_correction(sentence_feat.elementAt(sp_index).words.DPRL)));
			     featList.addElement(atts.indexOf("sp_pos_"+constant_correction(sentence_feat.elementAt(sp_index).words.pos)));
			     featList.addElement(atts.indexOf("sp_SRL_"+constant_correction(sentence_feat.elementAt(sp_index).words.SRL)));
			     featList.addElement(atts.indexOf("sp_sub_"+constant_correction(sentence_feat.elementAt(sp_index).words.subcategorization)));
			     // adding distance features 
			     
			     if(tr_index!=sentence_feat.size())
				    featList.addElement(atts.indexOf("d_sp_tr_"+sentence_feat.elementAt(sp_index).word_word.distance.elementAt(tr_index)));
				 if(lm_index!=sentence_feat.size())
				    featList.addElement(atts.indexOf("d_sp_lm_"+sentence_feat.elementAt(sp_index).word_word.distance.elementAt(lm_index)));
				 if(tr_index!=sentence_feat.size()& lm_index!=sentence_feat.size())
				    featList.addElement(atts.indexOf("d_tr_lm_"+sentence_feat.elementAt(tr_index).word_word.distance.elementAt(lm_index)));
					   // adding path features
                Collections.sort(featList);
	    		p.print("{");
	    		for(int t1=0; t1<featList.size();t1++){
	    		if(featList.elementAt(t1)!=-1)
		    	p.print(featList.elementAt(t1)+" 1,");}
	    		p.println("212 "+"NSR}");
	    		//p.println("2817 "+"NSR}");
	    		neg=r;
		    }
		   ////////////////
		   for (int r=neg+1;r<allnonrel.size();r++)
			{
			 int r1=selectedNegatives.elementAt(r);   
			 Vector<Integer> featList=new Vector <Integer>();
			 int lm_index=allnonrel.elementAt(r1).lm,tr_index=allnonrel.elementAt(r1).tr,sp_index=allnonrel.elementAt(r1).sp;
			 ////********** Features of  trajectors	
			 
			 if (tr_index==sentence_feat.size()){
				 featList.addElement(atts.indexOf("tr_h_undefined"));
			    }
			 else{
				 featList.addElement(atts.indexOf("tr_h_"+constant_correction(sentence_feat.elementAt(tr_index).words.word)));
			     featList.addElement(atts.indexOf("tr_dp_"+constant_correction(sentence_feat.elementAt(tr_index).words.DPRL)));
			     featList.addElement(atts.indexOf("tr_pos_"+constant_correction(sentence_feat.elementAt(tr_index).words.pos)));
			     featList.addElement(atts.indexOf("tr_SRL_"+constant_correction(sentence_feat.elementAt(tr_index).words.SRL)));
			     featList.addElement(atts.indexOf("tr_sub_"+constant_correction(sentence_feat.elementAt(tr_index).words.subcategorization)));
			 }
	      	//****************landmark features	
			 if (lm_index==sentence_feat.size()){
				 featList.addElement(atts.indexOf("lm_h_undefined"));
			    }
			 else{
			     featList.addElement(atts.indexOf("lm_h_"+constant_correction(sentence_feat.elementAt(lm_index).words.word)));
			     featList.addElement(atts.indexOf("lm_dp_"+constant_correction(sentence_feat.elementAt(lm_index).words.DPRL)));
			     featList.addElement(atts.indexOf("lm_pos_"+constant_correction(sentence_feat.elementAt(lm_index).words.pos)));
			     featList.addElement(atts.indexOf("lm_SRL_"+constant_correction(sentence_feat.elementAt(lm_index).words.SRL)));
			     featList.addElement(atts.indexOf("lm_sub_"+constant_correction(sentence_feat.elementAt(lm_index).words.subcategorization)));
			 }
	    	//*********** spatial indicator features 
			  
			     featList.addElement(atts.indexOf("sp_h_"+constant_correction(sentence_feat.elementAt(sp_index).words.word)));
			     featList.addElement(atts.indexOf("sp_dp_"+constant_correction(sentence_feat.elementAt(sp_index).words.DPRL)));
			     featList.addElement(atts.indexOf("sp_pos_"+constant_correction(sentence_feat.elementAt(sp_index).words.pos)));
			     featList.addElement(atts.indexOf("sp_SRL_"+constant_correction(sentence_feat.elementAt(sp_index).words.SRL)));
			     featList.addElement(atts.indexOf("sp_sub_"+constant_correction(sentence_feat.elementAt(sp_index).words.subcategorization)));
			//******distance feature for negative examples
			     
			     if(tr_index!=sentence_feat.size())
			     featList.addElement(atts.indexOf("d_sp_tr_"+sentence_feat.elementAt(sp_index).word_word.distance.elementAt(tr_index)));
			     if(lm_index!=sentence_feat.size())
			     featList.addElement(atts.indexOf("d_sp_lm_"+sentence_feat.elementAt(sp_index).word_word.distance.elementAt(lm_index)));
			     if(tr_index!=sentence_feat.size()& lm_index!=sentence_feat.size())
			     featList.addElement(atts.indexOf("d_tr_lm_"+sentence_feat.elementAt(tr_index).word_word.distance.elementAt(lm_index)));
				 
        Collections.sort(featList);
	    		pN.print("{");
	    		for(int t1=0; t1<featList.size();t1++){
	    		if(featList.elementAt(t1)!=-1)
		    	pN.print(featList.elementAt(t1)+" 1,");}
	    		pN.println("2817 "+"NSR}");
		    }	   
	   }
	   
	   
	   
	   public void add_arff_Reldata(Vector <String> atts , PrintWriter p,PrintWriter pN){// this module makes the lines of the arff file data section  produced from the relations of one sentence
			 // String  labels="SR NSR";
			   for (int r=0; r<relations.size();r++)
				{  Vector <Integer>  featList=new Vector <Integer>();
				   
				 int lm_index=relations.elementAt(r).wh_lm.head_index,tr_index=relations.elementAt(r).wh_tr.head_index,sp_index=relations.elementAt(r).wh_sp.head_index;
				 ////********** Features of  trajectors		
				 if (tr_index==sentence_feat.size()){
					 featList.addElement(atts.indexOf("tr_h_undefined"));
				    }
				 else{
					 featList.addElement(atts.indexOf("tr_h_"+constant_correction(sentence_feat.elementAt(tr_index).words.word)));
				     featList.addElement(atts.indexOf("tr_dp_"+constant_correction(sentence_feat.elementAt(tr_index).words.DPRL)));
				     featList.addElement(atts.indexOf("tr_pos_"+constant_correction(sentence_feat.elementAt(tr_index).words.pos)));
				     featList.addElement(atts.indexOf("tr_SRL_"+constant_correction(sentence_feat.elementAt(tr_index).words.SRL)));
				     featList.addElement(atts.indexOf("tr_sub_"+constant_correction(sentence_feat.elementAt(tr_index).words.subcategorization)));
				 }
		      	//****************landmark features	
				 if (lm_index==sentence_feat.size()){
					 featList.addElement(atts.indexOf("lm_h_undefined"));
				    }
				 else{
				     featList.addElement(atts.indexOf("lm_h_"+constant_correction(sentence_feat.elementAt(lm_index).words.word)));
				     featList.addElement(atts.indexOf("lm_dp_"+constant_correction(sentence_feat.elementAt(lm_index).words.DPRL)));
				     featList.addElement(atts.indexOf("lm_pos_"+constant_correction(sentence_feat.elementAt(lm_index).words.pos)));
				     featList.addElement(atts.indexOf("lm_SRL_"+constant_correction(sentence_feat.elementAt(lm_index).words.SRL)));
				     featList.addElement(atts.indexOf("lm_sub_"+constant_correction(sentence_feat.elementAt(lm_index).words.subcategorization)));
				 }
		    //*********** spatial indicator features 
				  
				     featList.addElement(atts.indexOf("sp_h_"+constant_correction(sentence_feat.elementAt(sp_index).words.word)));
				     featList.addElement(atts.indexOf("sp_dp_"+constant_correction(sentence_feat.elementAt(sp_index).words.DPRL)));
				     featList.addElement(atts.indexOf("sp_pos_"+constant_correction(sentence_feat.elementAt(sp_index).words.pos)));
				     featList.addElement(atts.indexOf("sp_SRL_"+constant_correction(sentence_feat.elementAt(sp_index).words.SRL)));
				     featList.addElement(atts.indexOf("sp_sub_"+constant_correction(sentence_feat.elementAt(sp_index).words.subcategorization)));
				
              Collections.sort(featList);
		    		p.print("{");
		    		for(int t1=0; t1<featList.size();t1++){
		    		if(featList.elementAt(t1)!=-1)
			    	p.print(featList.elementAt(t1)+" 1,");}
		    		p.println("2802 "+"SR}");
			    }
			   for (int r=0; r<Math.min(nonrelations.size(),relations.size()+1);r++)
				{  Vector <Integer>  featList=new Vector <Integer>();
				 int lm_index=nonrelations.elementAt(r).lm,tr_index=nonrelations.elementAt(r).tr,sp_index=nonrelations.elementAt(r).sp;
				 ////********** Features of  trajectors				
					 featList.addElement(atts.indexOf("tr_h_"+constant_correction(sentence_feat.elementAt(tr_index).words.word)));
				     featList.addElement(atts.indexOf("tr_dp_"+constant_correction(sentence_feat.elementAt(tr_index).words.DPRL)));
				     featList.addElement(atts.indexOf("tr_pos_"+constant_correction(sentence_feat.elementAt(tr_index).words.pos)));
				     featList.addElement(atts.indexOf("tr_SRL_"+constant_correction(sentence_feat.elementAt(tr_index).words.SRL)));
				     featList.addElement(atts.indexOf("tr_sub_"+constant_correction(sentence_feat.elementAt(tr_index).words.subcategorization)));
				
		      	//****************landmark features	
				
				     featList.addElement(atts.indexOf("lm_h_"+constant_correction(sentence_feat.elementAt(lm_index).words.word)));
				     featList.addElement(atts.indexOf("lm_dp_"+constant_correction(sentence_feat.elementAt(lm_index).words.DPRL)));
				     featList.addElement(atts.indexOf("lm_pos_"+constant_correction(sentence_feat.elementAt(lm_index).words.pos)));
				     featList.addElement(atts.indexOf("lm_SRL_"+constant_correction(sentence_feat.elementAt(lm_index).words.SRL)));
				     featList.addElement(atts.indexOf("lm_sub_"+constant_correction(sentence_feat.elementAt(lm_index).words.subcategorization)));
				
		    	//*********** spatial indicator features 
				  
				     featList.addElement(atts.indexOf("sp_h_"+constant_correction(sentence_feat.elementAt(sp_index).words.word)));
				     featList.addElement(atts.indexOf("sp_dp_"+constant_correction(sentence_feat.elementAt(sp_index).words.DPRL)));
				     featList.addElement(atts.indexOf("sp_pos_"+constant_correction(sentence_feat.elementAt(sp_index).words.pos)));
				     featList.addElement(atts.indexOf("sp_SRL_"+constant_correction(sentence_feat.elementAt(sp_index).words.SRL)));
				     featList.addElement(atts.indexOf("sp_sub_"+constant_correction(sentence_feat.elementAt(sp_index).words.subcategorization)));
				     
				     // adding distance features 
				     featList.addElement(atts.indexOf("d_sp_tr_"+sentence_feat.elementAt(sp_index).word_word.distance.elementAt(tr_index)));
					 featList.addElement(atts.indexOf("d_sp_lm_"+sentence_feat.elementAt(sp_index).word_word.distance.elementAt(lm_index)));
					 featList.addElement(atts.indexOf("d_tr_lm_"+sentence_feat.elementAt(tr_index).word_word.distance.elementAt(lm_index)));
					  // adding path features

				
             Collections.sort(featList);
		    		p.print("{");
		    		for(int t1=0; t1<featList.size();t1++){
		    		if(featList.elementAt(t1)!=-1)
			    	p.print(featList.elementAt(t1)+" 1,");}
		    		p.println("2802 "+"NSR}");
			    }
			   for (int r=Math.min(nonrelations.size(),relations.size()+1);r<nonrelations.size();r++)
				{  Vector<Integer> featListNeg=new Vector <Integer>();
				 int lm_index=nonrelations.elementAt(r).lm,tr_index=nonrelations.elementAt(r).tr,sp_index=nonrelations.elementAt(r).sp;
				 ////********** Features of  trajectors				
					 featListNeg.addElement(atts.indexOf("tr_h_"+constant_correction(sentence_feat.elementAt(tr_index).words.word)));
				     featListNeg.addElement(atts.indexOf("tr_dp_"+constant_correction(sentence_feat.elementAt(tr_index).words.DPRL)));
				     featListNeg.addElement(atts.indexOf("tr_pos_"+constant_correction(sentence_feat.elementAt(tr_index).words.pos)));
				     featListNeg.addElement(atts.indexOf("tr_SRL_"+constant_correction(sentence_feat.elementAt(tr_index).words.SRL)));
				     featListNeg.addElement(atts.indexOf("tr_sub_"+constant_correction(sentence_feat.elementAt(tr_index).words.subcategorization)));
				
		      	//****************landmark features	
				
				     featListNeg.addElement(atts.indexOf("lm_h_"+constant_correction(sentence_feat.elementAt(lm_index).words.word)));
				     featListNeg.addElement(atts.indexOf("lm_dp_"+constant_correction(sentence_feat.elementAt(lm_index).words.DPRL)));
				     featListNeg.addElement(atts.indexOf("lm_pos_"+constant_correction(sentence_feat.elementAt(lm_index).words.pos)));
				     featListNeg.addElement(atts.indexOf("lm_SRL_"+constant_correction(sentence_feat.elementAt(lm_index).words.SRL)));
				     featListNeg.addElement(atts.indexOf("lm_sub_"+constant_correction(sentence_feat.elementAt(lm_index).words.subcategorization)));
				
		    	//*********** spatial indicator features 
				  
				     featListNeg.addElement(atts.indexOf("sp_h_"+constant_correction(sentence_feat.elementAt(sp_index).words.word)));
				     featListNeg.addElement(atts.indexOf("sp_dp_"+constant_correction(sentence_feat.elementAt(sp_index).words.DPRL)));
				     featListNeg.addElement(atts.indexOf("sp_pos_"+constant_correction(sentence_feat.elementAt(sp_index).words.pos)));
				     featListNeg.addElement(atts.indexOf("sp_SRL_"+constant_correction(sentence_feat.elementAt(sp_index).words.SRL)));
				     featListNeg.addElement(atts.indexOf("sp_sub_"+constant_correction(sentence_feat.elementAt(sp_index).words.subcategorization)));
				//******distance feature for negative examples
				     featListNeg.addElement(atts.indexOf("d_sp_tr_"+sentence_feat.elementAt(sp_index).word_word.distance.elementAt(tr_index)));
					 featListNeg.addElement(atts.indexOf("d_sp_lm_"+sentence_feat.elementAt(sp_index).word_word.distance.elementAt(lm_index)));
					 featListNeg.addElement(atts.indexOf("d_tr_lm_"+sentence_feat.elementAt(tr_index).word_word.distance.elementAt(lm_index)));
					 
            Collections.sort(featListNeg);
		    		pN.print("{");
		    		for(int t1=0; t1<featListNeg.size();t1++){
		    		if(featListNeg.elementAt(t1)!=-1)
			    	pN.print(featListNeg.elementAt(t1)+" 1,");}
		    		pN.println("2802 "+"NSR}");
			    }
		  }
	   
	   
	   
	   ///////////////////////////Making arff files printing all negative and positives based on candidates and 
	   /// count howmany positives we miss here        ////////////////////////////////
	   
	   
	   public void addAllCandid_arff_Reldata(Vector <String> atts , PrintWriter p,PrintWriter pN){// this module makes the lines of the arff file data section  produced from the relations of one sentence
			 // String  labels="SR NSR";
			   for (int r=0; r<relations.size();r++)
				{  
				 
				 Vector <Integer>  featList=new Vector <Integer>();
				 int lm_index=relations.elementAt(r).wh_lm.head_index,tr_index=relations.elementAt(r).wh_tr.head_index,sp_index=relations.elementAt(r).wh_sp.head_index;
				 ////********** Features of  trajectors		
				 if (tr_index==sentence_feat.size()){
					 featList.addElement(atts.indexOf("tr_h_undefined"));
				    }
				 else{
					 featList.addElement(atts.indexOf("tr_h_"+constant_correction(sentence_feat.elementAt(tr_index).words.word)));
				     featList.addElement(atts.indexOf("tr_dp_"+constant_correction(sentence_feat.elementAt(tr_index).words.DPRL)));
				     featList.addElement(atts.indexOf("tr_pos_"+constant_correction(sentence_feat.elementAt(tr_index).words.pos)));
				     featList.addElement(atts.indexOf("tr_SRL_"+constant_correction(sentence_feat.elementAt(tr_index).words.SRL)));
				     featList.addElement(atts.indexOf("tr_sub_"+constant_correction(sentence_feat.elementAt(tr_index).words.subcategorization)));
				 }
		      	//****************landmark features	
				 if (lm_index==sentence_feat.size()){
					 featList.addElement(atts.indexOf("lm_h_undefined"));
				    }
				 else{
				     featList.addElement(atts.indexOf("lm_h_"+constant_correction(sentence_feat.elementAt(lm_index).words.word)));
				     featList.addElement(atts.indexOf("lm_dp_"+constant_correction(sentence_feat.elementAt(lm_index).words.DPRL)));
				     featList.addElement(atts.indexOf("lm_pos_"+constant_correction(sentence_feat.elementAt(lm_index).words.pos)));
				     featList.addElement(atts.indexOf("lm_SRL_"+constant_correction(sentence_feat.elementAt(lm_index).words.SRL)));
				     featList.addElement(atts.indexOf("lm_sub_"+constant_correction(sentence_feat.elementAt(lm_index).words.subcategorization)));
				 }
		    //*********** spatial indicator features 
				  
				     featList.addElement(atts.indexOf("sp_h_"+constant_correction(sentence_feat.elementAt(sp_index).words.word)));
				     featList.addElement(atts.indexOf("sp_dp_"+constant_correction(sentence_feat.elementAt(sp_index).words.DPRL)));
				     featList.addElement(atts.indexOf("sp_pos_"+constant_correction(sentence_feat.elementAt(sp_index).words.pos)));
				     featList.addElement(atts.indexOf("sp_SRL_"+constant_correction(sentence_feat.elementAt(sp_index).words.SRL)));
				     featList.addElement(atts.indexOf("sp_sub_"+constant_correction(sentence_feat.elementAt(sp_index).words.subcategorization)));
				
            Collections.sort(featList);
		    		p.print("{");
		    		for(int t1=0; t1<featList.size();t1++){
		    		if(featList.elementAt(t1)!=-1)
			    	p.print(featList.elementAt(t1)+" 1,");}
		    		p.println("2802 "+"SR}");
			    }
			   for (int r=0; r<Math.min(nonrelations.size(),relations.size()+1);r++)
				{  Vector <Integer>  featList=new Vector <Integer>();
				 int lm_index=nonrelations.elementAt(r).lm,tr_index=nonrelations.elementAt(r).tr,sp_index=nonrelations.elementAt(r).sp;
				 ////********** Features of  trajectors				
					 featList.addElement(atts.indexOf("tr_h_"+constant_correction(sentence_feat.elementAt(tr_index).words.word)));
				     featList.addElement(atts.indexOf("tr_dp_"+constant_correction(sentence_feat.elementAt(tr_index).words.DPRL)));
				     featList.addElement(atts.indexOf("tr_pos_"+constant_correction(sentence_feat.elementAt(tr_index).words.pos)));
				     featList.addElement(atts.indexOf("tr_SRL_"+constant_correction(sentence_feat.elementAt(tr_index).words.SRL)));
				     featList.addElement(atts.indexOf("tr_sub_"+constant_correction(sentence_feat.elementAt(tr_index).words.subcategorization)));
				
		      	//****************landmark features	
				
				     featList.addElement(atts.indexOf("lm_h_"+constant_correction(sentence_feat.elementAt(lm_index).words.word)));
				     featList.addElement(atts.indexOf("lm_dp_"+constant_correction(sentence_feat.elementAt(lm_index).words.DPRL)));
				     featList.addElement(atts.indexOf("lm_pos_"+constant_correction(sentence_feat.elementAt(lm_index).words.pos)));
				     featList.addElement(atts.indexOf("lm_SRL_"+constant_correction(sentence_feat.elementAt(lm_index).words.SRL)));
				     featList.addElement(atts.indexOf("lm_sub_"+constant_correction(sentence_feat.elementAt(lm_index).words.subcategorization)));
				
		    	//*********** spatial indicator features 
				  
				     featList.addElement(atts.indexOf("sp_h_"+constant_correction(sentence_feat.elementAt(sp_index).words.word)));
				     featList.addElement(atts.indexOf("sp_dp_"+constant_correction(sentence_feat.elementAt(sp_index).words.DPRL)));
				     featList.addElement(atts.indexOf("sp_pos_"+constant_correction(sentence_feat.elementAt(sp_index).words.pos)));
				     featList.addElement(atts.indexOf("sp_SRL_"+constant_correction(sentence_feat.elementAt(sp_index).words.SRL)));
				     featList.addElement(atts.indexOf("sp_sub_"+constant_correction(sentence_feat.elementAt(sp_index).words.subcategorization)));
				     
				     // adding distance features 
				     featList.addElement(atts.indexOf("d_sp_tr_"+sentence_feat.elementAt(sp_index).word_word.distance.elementAt(tr_index)));
					 featList.addElement(atts.indexOf("d_sp_lm_"+sentence_feat.elementAt(sp_index).word_word.distance.elementAt(lm_index)));
					 featList.addElement(atts.indexOf("d_tr_lm_"+sentence_feat.elementAt(tr_index).word_word.distance.elementAt(lm_index)));
					  // adding path features

				
           Collections.sort(featList);
		    		p.print("{");
		    		for(int t1=0; t1<featList.size();t1++){
		    		if(featList.elementAt(t1)!=-1)
			    	p.print(featList.elementAt(t1)+" 1,");}
		    		p.println("2802 "+"NSR}");
			    }
			   for (int r=Math.min(nonrelations.size(),relations.size()+1);r<nonrelations.size();r++)
				{  Vector<Integer> featListNeg=new Vector <Integer>();
				 int lm_index=nonrelations.elementAt(r).lm,tr_index=nonrelations.elementAt(r).tr,sp_index=nonrelations.elementAt(r).sp;
				 ////********** Features of  trajectors				
					 featListNeg.addElement(atts.indexOf("tr_h_"+constant_correction(sentence_feat.elementAt(tr_index).words.word)));
				     featListNeg.addElement(atts.indexOf("tr_dp_"+constant_correction(sentence_feat.elementAt(tr_index).words.DPRL)));
				     featListNeg.addElement(atts.indexOf("tr_pos_"+constant_correction(sentence_feat.elementAt(tr_index).words.pos)));
				     featListNeg.addElement(atts.indexOf("tr_SRL_"+constant_correction(sentence_feat.elementAt(tr_index).words.SRL)));
				     featListNeg.addElement(atts.indexOf("tr_sub_"+constant_correction(sentence_feat.elementAt(tr_index).words.subcategorization)));
				
		      	//****************landmark features	
				
				     featListNeg.addElement(atts.indexOf("lm_h_"+constant_correction(sentence_feat.elementAt(lm_index).words.word)));
				     featListNeg.addElement(atts.indexOf("lm_dp_"+constant_correction(sentence_feat.elementAt(lm_index).words.DPRL)));
				     featListNeg.addElement(atts.indexOf("lm_pos_"+constant_correction(sentence_feat.elementAt(lm_index).words.pos)));
				     featListNeg.addElement(atts.indexOf("lm_SRL_"+constant_correction(sentence_feat.elementAt(lm_index).words.SRL)));
				     featListNeg.addElement(atts.indexOf("lm_sub_"+constant_correction(sentence_feat.elementAt(lm_index).words.subcategorization)));
				
		    	//*********** spatial indicator features 
				  
				     featListNeg.addElement(atts.indexOf("sp_h_"+constant_correction(sentence_feat.elementAt(sp_index).words.word)));
				     featListNeg.addElement(atts.indexOf("sp_dp_"+constant_correction(sentence_feat.elementAt(sp_index).words.DPRL)));
				     featListNeg.addElement(atts.indexOf("sp_pos_"+constant_correction(sentence_feat.elementAt(sp_index).words.pos)));
				     featListNeg.addElement(atts.indexOf("sp_SRL_"+constant_correction(sentence_feat.elementAt(sp_index).words.SRL)));
				     featListNeg.addElement(atts.indexOf("sp_sub_"+constant_correction(sentence_feat.elementAt(sp_index).words.subcategorization)));
				//******distance feature for negative examples
				     featListNeg.addElement(atts.indexOf("d_sp_tr_"+sentence_feat.elementAt(sp_index).word_word.distance.elementAt(tr_index)));
					 featListNeg.addElement(atts.indexOf("d_sp_lm_"+sentence_feat.elementAt(sp_index).word_word.distance.elementAt(lm_index)));
					 featListNeg.addElement(atts.indexOf("d_tr_lm_"+sentence_feat.elementAt(tr_index).word_word.distance.elementAt(lm_index)));
					 
          Collections.sort(featListNeg);
		    		pN.print("{");
		    		for(int t1=0; t1<featListNeg.size();t1++){
		    		if(featListNeg.elementAt(t1)!=-1)
			    	pN.print(featListNeg.elementAt(t1)+" 1,");}
		    		pN.println("2802 "+"NSR}");
			    }
		  }
	   
	   
	   
	   
	   
	   
	   
	   public void add_maptask_arff_data(Vector <String> atts , PrintWriter p){// this module makes the lines of the arff file data section  produced from the relations of one sentence
			  String  labels="TTP EC EQ DC TPPI PO NTPP NTPPI";
			   for (int r=0; r<relations.size();r++)
				{  Vector <Integer>  featList=new Vector <Integer>();
				   String temty=relations.elementAt(r).calc_type.sp_value.elementAt(0).replaceAll("\"","").trim().toUpperCase();
		    		if (!labels.contains(temty) ) temty="NONE";
		    	
		    		//featList.addElement(atts.indexOf("tr_"+constant_correction(relations.elementAt(r).wh_tr.ph_head)));
		    		for(int r1=0; r1<relations.elementAt(r).wh_tr.ph.size();r1++){
			    		//  if (!constant_correction(relations.elementAt(r).wh_tr.ph.elementAt(r1)).equals(constant_correction(relations.elementAt(r).wh_tr.ph_head)))	
			    		     featList.addElement(atts.indexOf("tr_"+constant_correction(relations.elementAt(r).wh_tr.ph.elementAt(r1))));}
			    		
		    		
		    		//featList.addElement(atts.indexOf("lm_"+constant_correction(relations.elementAt(r).wh_lm.ph_head)));
		    		for(int r1=0; r1<relations.elementAt(r).wh_lm.ph.size();r1++){
			    		  //if (!constant_correction(relations.elementAt(r).wh_lm.ph.elementAt(r1)).equals(constant_correction(relations.elementAt(r).wh_lm.ph_head)))	
			    		     featList.addElement(atts.indexOf("lm_"+constant_correction(relations.elementAt(r).wh_lm.ph.elementAt(r1))));}
			    		
		    		//featList.addElement(atts.indexOf("sp_"+constant_correction(relations.elementAt(r).wh_sp.ph_head)));
		    		for(int r1=0; r1<relations.elementAt(r).wh_sp.ph.size();r1++){
		    		  //if (!constant_correction(relations.elementAt(r).wh_sp.ph.elementAt(r1)).equals(constant_correction(relations.elementAt(r).wh_sp.ph_head)))	
		    		     featList.addElement(atts.indexOf("sp_"+constant_correction(relations.elementAt(r).wh_sp.ph.elementAt(r1))));}
		    		
		    		//featList.addElement(atts.indexOf("mo_"+constant_correction(relations.elementAt(r).wh_motion.ph_head)));
		    		for(int r1=0; r1<relations.elementAt(r).wh_motion.ph.size();r1++){
			    		//  if (!constant_correction(relations.elementAt(r).wh_motion.ph.elementAt(r1)).equals(constant_correction(relations.elementAt(r).wh_motion.ph_head)))	
			    		     featList.addElement(atts.indexOf("mo_"+constant_correction(relations.elementAt(r).wh_motion.ph.elementAt(r1))));}
			    		
		    		featList.addElement(atts.indexOf("for_"+constant_correction(relations.elementAt(r).FrORe)));
		    		featList.addElement(atts.indexOf("pa_"+constant_correction(relations.elementAt(r).path)));
		    		featList.addElement(atts.indexOf("dy_"+relations.elementAt(r).dynamic)); 
		    		featList.addElement(atts.indexOf("gum_"+constant_correction(relations.elementAt(r).GUM_mod))); 
		    		Collections.sort(featList);
		    		p.print("{");
		    		for(int t1=0; t1<featList.size();t1++){
		    		if(featList.elementAt(t1)!=-1)
			    	p.print(featList.elementAt(t1)+" 1,");}
		    		p.println("1098 "+temty+"}");
			    }
			   }
	   //////////
	   public void add_Room_arff_data(Vector <String> atts , PrintWriter p){// this module makes the lines of the arff file data section  produced from the relations of one sentence
			  String  labels="TTP EC EQ DC TPPI PO NTPP NTPPI";
			   for (int r=0; r<relations.size();r++)
				{  Vector <Integer>  featList=new Vector <Integer>();
				   String temty=relations.elementAt(r).calc_type.sp_value.elementAt(0).replaceAll("\"","").trim().toUpperCase();
		    		if (!labels.contains(temty) ) temty="NONE";
		    	
		    		//featList.addElement(atts.indexOf("tr_"+constant_correction(relations.elementAt(r).wh_tr.ph_head)));
		    		for(int r1=0; r1<relations.elementAt(r).wh_tr.ph.size();r1++){
			    		 if ( !featList.contains(atts.indexOf("tr_"+constant_correction(relations.elementAt(r).wh_tr.ph.elementAt(r1)))))	
			    		     featList.addElement(atts.indexOf("tr_"+constant_correction(relations.elementAt(r).wh_tr.ph.elementAt(r1))));}
			    		
		    		
		    		//featList.addElement(atts.indexOf("lm_"+constant_correction(relations.elementAt(r).wh_lm.ph_head)));
		    		for(int r1=0; r1<relations.elementAt(r).wh_lm.ph.size();r1++){
			    		  //if (!constant_correction(relations.elementAt(r).wh_lm.ph.elementAt(r1)).equals(constant_correction(relations.elementAt(r).wh_lm.ph_head)))	
		    			 if ( !featList.contains(atts.indexOf("lm_"+constant_correction(relations.elementAt(r).wh_lm.ph.elementAt(r1)))))
			    		     featList.addElement(atts.indexOf("lm_"+constant_correction(relations.elementAt(r).wh_lm.ph.elementAt(r1))));}
			    		
		    		//featList.addElement(atts.indexOf("sp_"+constant_correction(relations.elementAt(r).wh_sp.ph_head)));
		    		for(int r1=0; r1<relations.elementAt(r).wh_sp.ph.size();r1++){
		    		  //if (!constant_correction(relations.elementAt(r).wh_sp.ph.elementAt(r1)).equals(constant_correction(relations.elementAt(r).wh_sp.ph_head)))	
		    			 if ( !featList.contains(atts.indexOf("sp_"+constant_correction(relations.elementAt(r).wh_sp.ph.elementAt(r1)))))
		    		     featList.addElement(atts.indexOf("sp_"+constant_correction(relations.elementAt(r).wh_sp.ph.elementAt(r1))));}
		    		
		    		//featList.addElement(atts.indexOf("mo_"+constant_correction(relations.elementAt(r).wh_motion.ph_head)));
		    	//	for(int r1=0; r1<relations.elementAt(r).wh_motion.ph.size();r1++){
			    		//  if (!constant_correction(relations.elementAt(r).wh_motion.ph.elementAt(r1)).equals(constant_correction(relations.elementAt(r).wh_motion.ph_head)))	
			    	//	     featList.addElement(atts.indexOf("mo_"+constant_correction(relations.elementAt(r).wh_motion.ph.elementAt(r1))));}
			    		
		    		//featList.addElement(atts.indexOf("for_"+constant_correction(relations.elementAt(r).FrORe)));
		    	//	featList.addElement(atts.indexOf("pa_"+constant_correction(relations.elementAt(r).path)));
		    		//featList.addElement(atts.indexOf("dy_"+relations.elementAt(r).dynamic));
		    		featList.addElement(atts.indexOf("gum_"+constant_correction(relations.elementAt(r).GUM_mod)));
		    		
		    		Collections.sort(featList);
		    		p.print("{");
		    		for(int t1=0; t1<featList.size();t1++){
		    		if(featList.elementAt(t1)!=-1)
			    	p.print(featList.elementAt(t1)+" 1,");}
		    		p.println("1098 "+temty+"}");
			    }
			   }
	   //////////////////////////////////
	 public void writeAlchemyOutputs(PrintWriter p, int wordcount){
		 Vector <Integer> t=new Vector<Integer>();
	      Vector <Integer> l=new Vector<Integer>();
	      Vector <Integer> sp=new Vector<Integer>();
		   for(int r=0; r<relations.size(); r++){
//				if (!t.contains(relations.elementAt(r).wh_tr.head_index)){
//		          p.println("trajector("+(wordcount+relations.elementAt(r).wh_tr.head_index)+")");
//		          t.addElement(relations.elementAt(r).wh_tr.head_index);
//		   	}
//				if (!l.contains(relations.elementAt(r).wh_lm.head_index)){
//			          p.println("landmark("+(wordcount+relations.elementAt(r).wh_lm.head_index)+")");
//			          l.addElement(relations.elementAt(r).wh_lm.head_index);
//			   	}
//				if (!sp.contains(relations.elementAt(r).wh_sp.head_index)){
//			          p.println("spindicator("+(wordcount+relations.elementAt(r).wh_sp.head_index)+")");
//			          sp.addElement(relations.elementAt(r).wh_sp.head_index);
//			   	}
		p.println("sr("+(relations.elementAt(r).wh_tr.head_index+wordcount)+","+(relations.elementAt(r).wh_lm.head_index+wordcount)+","+(relations.elementAt(r).wh_sp.head_index+wordcount)+")");
		   }	
	 }  
	 public void writeAlchemyInputs(PrintWriter p, int wordcount){
	 int len=sentence_feat.size();	  
	 for(int w=0;w< sentence_feat.size();w++){
		p.println("dprl("+(wordcount+w)+",Dp_"+constant_correction(sentence_feat.elementAt(w).words.DPRL).toUpperCase()+")");
		//p.println("lemma("+i+","+constant_correction(sentence_feat.elementAt(w).words.lemma)+").");
		p.println("pos("+(wordcount+w)+",Pos_"+constant_correction(sentence_feat.elementAt(w).words.pos).toUpperCase()+")");
		p.println("srl("+(wordcount+w)+","+constant_correction(sentence_feat.elementAt(w).words.SRL).toUpperCase()+")");
		p.println("subcat("+(wordcount+w)+",Sub_"+constant_correction(sentence_feat.elementAt(w).words.subcategorization).toUpperCase()+")");
		p.println("word("+(wordcount+w)+", \""+sentence_feat.elementAt(w).words.word.toUpperCase()+"\")");
		for(int w_2=0;w_2<sentence_feat.size();w_2++){
		    //p.println("distance("+(wordcount+w)+","+(wordcount+w_2)+","+sentence_feat.elementAt(w).word_word.distance.elementAt(w_2)+")");
		    //p.println("pat("+(wordcount+w)+","+(wordcount+w_2)+","+constant_correction(sentence_feat.elementAt(w).word_word.path.elementAt(w_2)).toUpperCase()+")");
		 }
		}
	 // write the features of undefined word
	 p.println("dprl("+(wordcount+len)+",Dp_0"+")");
		//p.println("lemma("+i+","+constant_correction(sentence_feat.elementAt(w).words.lemma)+").");
		p.println("pos("+(wordcount+len)+",Pos_0"+")");
		p.println("srl("+(wordcount+len)+",0"+")");
		p.println("subcat("+(wordcount+len)+",Sub_0"+")");
		p.println("word("+(wordcount+len)+", \"undef\")");
	 // end write all input linguistic features
	     
	     // writeAlchemyOutputs(p,wordcount);
	   
	 //wordcount=wordcount+sentence_feat.size();
	 }

	/*//************************************MAPtask-Specific-FileReading*******************  
	 public void fetch_Maptask_rel(Vector<Vector<String>> Rows, int i){
			 This method extracts one sentence and its relations from the list of all the sentences.
			if (Rows.elementAt(i).size()<15) 
			{System.out.println("warning: this row"+i+" does not contain a full sentence!!");}
			else{
				content=Rows.elementAt(i).elementAt(2);
				content=content.trim();
				content=content.replace(","," , ");// make comma counted as a  word
				content=content.replace("("," ( ");
				content=content.replace(")"," ) ");
				content=content.replaceAll("[\\s][\\s]*", " ");
				content=content.replaceAll("\"", "");
				if (content.contains("a wooden house with green window frames") ){
					System.out.print("Stop");
				}
				do{
					relation r=new relation();
					if (Rows.elementAt(i).size()==14)
						Rows.elementAt(i).add(0, " ");
					r.read_rel_map(Rows.elementAt(i));
				//	if (r.wh_sp.head_index!=-1)
					  relations.addElement(r);	
					i++;	
				}while(!build_RelDB_SbySEdition4arff_Maptask_test.isInteger(Rows.elementAt(i).elementAt(1))&& !Rows.elementAt(i).elementAt(0).contains("*"));
			}//end else
		}// end fetch
	 
	 /////////////////////////// Room description specific file reading ///////////////////
	 public void fetch_Room_rel(Vector<Vector<String>> Rows, int i){
			 This method extracts one sentence and its relations from the list of all the sentences.
			if (Rows.elementAt(i).elementAt(1).contains("+") || Rows.elementAt(i).elementAt(0).contains("*")) 
			{System.out.println("warning: this row"+i+" does not contain a full sentence!!");}
			else{
				content=Rows.elementAt(i).elementAt(0);
				content=content.trim();
				content=content.replace(","," , ");// make comma counted as a  word
				content=content.replace("("," ( ");
				content=content.replace(")"," ) ");
				content=content.replaceAll("[\\s][\\s]*", " ");
				content=content.replaceAll("\"", "");
				if (content.contains("a wooden house with green window frames")){
					System.out.print("Stop");
				}
				//do{
					relation r=new relation();
					//if (Rows.elementAt(i).size()==14)
					//	Rows.elementAt(i).add(0, " ");
					r.read_rel_Room(Rows.elementAt(i));
				//	if (r.wh_sp.head_index!=-1)
					  relations.addElement(r);	
					i++;	
			//	}while(!build_RelDB_SbySEdition4arff_Maptask_test.isInteger(Rows.elementAt(i).elementAt(1))&& !Rows.elementAt(i).elementAt(0).contains("*"));
			}//end else
		}// end fetch
	 public void add_arff_dataRcc5(Vector <String> atts , PrintWriter p){// this module makes the lines of the arff file data section  produced from the relations of one sentence
		  String  labels="TTP EC EQ DC TPPI PO NTPP NTPPI";
		   for (int r=0; r<relations.size();r++)
			{  Vector <Integer>  featList=new Vector <Integer>();
			   String temty=relations.elementAt(r).calc_type.sp_value.elementAt(0).replaceAll("\"","").trim().toUpperCase();
	    		if (!labels.contains(temty) ) temty="NONE";
	            if (temty.contains("TPP")) temty="PP";	
	    		featList.addElement(atts.indexOf("tr_"+constant_correction(relations.elementAt(r).wh_tr.ph_head)));
	    		for(int r1=0; r1<relations.elementAt(r).wh_tr.ph.size();r1++){
		    		  if (!constant_correction(relations.elementAt(r).wh_tr.ph.elementAt(r1)).equals(constant_correction(relations.elementAt(r).wh_tr.ph_head)))	
		    		     featList.addElement(atts.indexOf("tr1_"+constant_correction(relations.elementAt(r).wh_tr.ph.elementAt(r1))));}
		    		
	    		
	    		featList.addElement(atts.indexOf("lm_"+constant_correction(relations.elementAt(r).wh_lm.ph_head)));
	    		for(int r1=0; r1<relations.elementAt(r).wh_lm.ph.size();r1++){
		    		  if (!constant_correction(relations.elementAt(r).wh_lm.ph.elementAt(r1)).equals(constant_correction(relations.elementAt(r).wh_lm.ph_head)))	
		    		     featList.addElement(atts.indexOf("lm1_"+constant_correction(relations.elementAt(r).wh_lm.ph.elementAt(r1))));}
		    		
	    		featList.addElement(atts.indexOf("sp_"+constant_correction(relations.elementAt(r).wh_sp.ph_head)));
	    		for(int r1=0; r1<relations.elementAt(r).wh_sp.ph.size();r1++){
	    		  if (!constant_correction(relations.elementAt(r).wh_sp.ph.elementAt(r1)).equals(constant_correction(relations.elementAt(r).wh_sp.ph_head)))	
	    		     featList.addElement(atts.indexOf("sp1_"+constant_correction(relations.elementAt(r).wh_sp.ph.elementAt(r1))));}
	    		
	    		featList.addElement(atts.indexOf("mo_"+constant_correction(relations.elementAt(r).wh_motion.ph_head)));
	    		featList.addElement(atts.indexOf("for_"+constant_correction(relations.elementAt(r).FrORe)));
	    		featList.addElement(atts.indexOf("pa_"+constant_correction(relations.elementAt(r).path)));
	    		featList.addElement(atts.indexOf("dy_"+relations.elementAt(r).dynamic));
	    		featList.addElement(atts.indexOf("gum_"+constant_correction(relations.elementAt(r).GUM_mod)));
	    		
	    		Collections.sort(featList);
	    		p.print("{");
	    		for(int t1=0; t1<featList.size();t1++){
	    		if(featList.elementAt(t1)!=-1)
		    	p.print(featList.elementAt(t1)+" 1,");}
	    		p.println("1098 "+temty+"}");
		    }
		   }
	 public void add_arff_GeneralSP_data(Vector <String> atts , PrintWriter p){// this module makes the lines of the arff file data section  produced from the relations of one sentence
		  String  labels="REGION DIRECTION DISTANCE";
		   for (int r=0; r<relations.size();r++)
			{  Vector <Integer>  featList=new Vector <Integer>();
			   String temty=relations.elementAt(r).calc_type.general.elementAt(0).replaceAll("\"","").trim().toUpperCase();
	    		if (!labels.contains(temty) ) temty="NONE";
	    	
	    		featList.addElement(atts.indexOf("tr_"+constant_correction(relations.elementAt(r).wh_tr.ph_head)));
	    		for(int r1=0; r1<relations.elementAt(r).wh_tr.ph.size();r1++){
		    		  if (!constant_correction(relations.elementAt(r).wh_tr.ph.elementAt(r1)).equals(constant_correction(relations.elementAt(r).wh_tr.ph_head)))	
		    		     featList.addElement(atts.indexOf("tr1_"+constant_correction(relations.elementAt(r).wh_tr.ph.elementAt(r1))));}
		    		
	    		
	    		featList.addElement(atts.indexOf("lm_"+constant_correction(relations.elementAt(r).wh_lm.ph_head)));
	    		for(int r1=0; r1<relations.elementAt(r).wh_lm.ph.size();r1++){
		    		  if (!constant_correction(relations.elementAt(r).wh_lm.ph.elementAt(r1)).equals(constant_correction(relations.elementAt(r).wh_lm.ph_head)))	
		    		     featList.addElement(atts.indexOf("lm1_"+constant_correction(relations.elementAt(r).wh_lm.ph.elementAt(r1))));}
		    		
	    		featList.addElement(atts.indexOf("sp_"+constant_correction(relations.elementAt(r).wh_sp.ph_head)));
	    		for(int r1=0; r1<relations.elementAt(r).wh_sp.ph.size();r1++){
	    		  if (!constant_correction(relations.elementAt(r).wh_sp.ph.elementAt(r1)).equals(constant_correction(relations.elementAt(r).wh_sp.ph_head)))	
	    		     featList.addElement(atts.indexOf("sp1_"+constant_correction(relations.elementAt(r).wh_sp.ph.elementAt(r1))));}
	    		
	    		featList.addElement(atts.indexOf("mo_"+constant_correction(relations.elementAt(r).wh_motion.ph_head)));
	    		featList.addElement(atts.indexOf("for_"+constant_correction(relations.elementAt(r).FrORe)));
	    		featList.addElement(atts.indexOf("pa_"+constant_correction(relations.elementAt(r).path)));
	    		featList.addElement(atts.indexOf("dy_"+relations.elementAt(r).dynamic));
	    		featList.addElement(atts.indexOf("gum_"+constant_correction(relations.elementAt(r).GUM_mod)));
	    		Collections.sort(featList);
	    		p.print("{");
	    		for(int t1=0; t1<featList.size();t1++){
	    		if(featList.elementAt(t1)!=-1)
		    	p.print(featList.elementAt(t1)+" 1,");}
	    		p.println("1098 "+temty+"}");
		    }
		   }
	 
	 public void add_arff_GUM_data(Vector <String> atts , PrintWriter p){// this module makes the lines of the arff file data section  produced from the relations of one sentence
		  String  labels="REGION DIRECTION DISTANCE";
		   for (int r=0; r<relations.size();r++)
			{  Vector <Integer>  featList=new Vector <Integer>();
			   String temty=relations.elementAt(r).calc_type.general.elementAt(0).replaceAll("\"","").trim().toUpperCase();
	    		if (!labels.contains(temty) ) temty="NONE";
	    	
	    		featList.addElement(atts.indexOf("tr_"+constant_correction(relations.elementAt(r).wh_tr.ph_head)));
	    		for(int r1=0; r1<relations.elementAt(r).wh_tr.ph.size();r1++){
		    		  if (!constant_correction(relations.elementAt(r).wh_tr.ph.elementAt(r1)).equals(constant_correction(relations.elementAt(r).wh_tr.ph_head)))	
		    		     featList.addElement(atts.indexOf("tr1_"+constant_correction(relations.elementAt(r).wh_tr.ph.elementAt(r1))));}
		    		
	    		
	    		featList.addElement(atts.indexOf("lm_"+constant_correction(relations.elementAt(r).wh_lm.ph_head)));
	    		for(int r1=0; r1<relations.elementAt(r).wh_lm.ph.size();r1++){
		    		  if (!constant_correction(relations.elementAt(r).wh_lm.ph.elementAt(r1)).equals(constant_correction(relations.elementAt(r).wh_lm.ph_head)))	
		    		     featList.addElement(atts.indexOf("lm1_"+constant_correction(relations.elementAt(r).wh_lm.ph.elementAt(r1))));}
		    		
	    		featList.addElement(atts.indexOf("sp_"+constant_correction(relations.elementAt(r).wh_sp.ph_head)));
	    		for(int r1=0; r1<relations.elementAt(r).wh_sp.ph.size();r1++){
	    		  if (!constant_correction(relations.elementAt(r).wh_sp.ph.elementAt(r1)).equals(constant_correction(relations.elementAt(r).wh_sp.ph_head)))	
	    		     featList.addElement(atts.indexOf("sp1_"+constant_correction(relations.elementAt(r).wh_sp.ph.elementAt(r1))));}
	    		
	    		featList.addElement(atts.indexOf("mo_"+constant_correction(relations.elementAt(r).wh_motion.ph_head)));
	    		featList.addElement(atts.indexOf("for_"+constant_correction(relations.elementAt(r).FrORe)));
	    		featList.addElement(atts.indexOf("pa_"+constant_correction(relations.elementAt(r).path)));
	    		featList.addElement(atts.indexOf("dy_"+relations.elementAt(r).dynamic));
	    		//featList.addElement(atts.indexOf("gum_"+constant_correction(relations.elementAt(r).GUM_mod)));
	    		Collections.sort(featList);
	    		p.print("{");
	    		for(int t1=0; t1<featList.size();t1++){
	    		if(featList.elementAt(t1)!=-1)
		    	p.print(featList.elementAt(t1)+" 1,");}
	    		p.println("1059 "+constant_correction(relations.elementAt(r).GUM_mod)+"}");
		    }
		   }
	 public void add_arff_dataRcc5standard(Vector <String> atts , PrintWriter p){// this module makes the lines of the arff file data section  produced from the relations of one sentence
		  String  labels="TTP EC EQ DC TPPI PO NTPP NTPPI";
		   for (int r=0; r<relations.size();r++)
			{ Vector <Integer>  featList=new Vector <Integer>();
			  String temty=relations.elementAt(r).calc_type.sp_value.elementAt(0).replaceAll("\"","").trim().toUpperCase();
	    		if (!labels.contains(temty) ) temty="NONE";
	            if (temty.endsWith("TPP")) temty="PP";	
	            if(temty.endsWith("TPPI")) temty="PPI";
	            if(temty.contains("EC")) temty="DC";
	            if(temty.contains("DC")) temty="DR";
	    		featList.addElement(atts.indexOf("tr_"+constant_correction(relations.elementAt(r).wh_tr.ph_head)));
	    		for(int r1=0; r1<relations.elementAt(r).wh_tr.ph.size();r1++){
		    		  if (!constant_correction(relations.elementAt(r).wh_tr.ph.elementAt(r1)).equals(constant_correction(relations.elementAt(r).wh_tr.ph_head)))	
		    		     featList.addElement(atts.indexOf("tr1_"+constant_correction(relations.elementAt(r).wh_tr.ph.elementAt(r1))));}
		    		
	    		
	    		featList.addElement(atts.indexOf("lm_"+constant_correction(relations.elementAt(r).wh_lm.ph_head)));
	    		for(int r1=0; r1<relations.elementAt(r).wh_lm.ph.size();r1++){
		    		  if (!constant_correction(relations.elementAt(r).wh_lm.ph.elementAt(r1)).equals(constant_correction(relations.elementAt(r).wh_lm.ph_head)))	
		    		     featList.addElement(atts.indexOf("lm1_"+constant_correction(relations.elementAt(r).wh_lm.ph.elementAt(r1))));}
		    		
	    		featList.addElement(atts.indexOf("sp_"+constant_correction(relations.elementAt(r).wh_sp.ph_head)));
	    		for(int r1=0; r1<relations.elementAt(r).wh_sp.ph.size();r1++){
	    		  if (!constant_correction(relations.elementAt(r).wh_sp.ph.elementAt(r1)).equals(constant_correction(relations.elementAt(r).wh_sp.ph_head)))	
	    		     featList.addElement(atts.indexOf("sp1_"+constant_correction(relations.elementAt(r).wh_sp.ph.elementAt(r1))));}
	    		
	    		featList.addElement(atts.indexOf("mo_"+constant_correction(relations.elementAt(r).wh_motion.ph_head)));
	    		featList.addElement(atts.indexOf("for_"+constant_correction(relations.elementAt(r).FrORe)));
	    		featList.addElement(atts.indexOf("pa_"+constant_correction(relations.elementAt(r).path)));
	    		featList.addElement(atts.indexOf("dy_"+relations.elementAt(r).dynamic));
	    		featList.addElement(atts.indexOf("gum_"+constant_correction(relations.elementAt(r).GUM_mod)));
	    		
	    		Collections.sort(featList);
	    		p.print("{");
	    		for(int t1=0; t1<featList.size();t1++){
	    		if(featList.elementAt(t1)!=-1)
		    	p.print(featList.elementAt(t1)+" 1,");}
	    		p.println("1098 "+temty+"}");
		    }
		   }*/
	public void writeXMLtest(PrintWriter spxml,int sentence_num){
		//Vector <Integer> tr =new Vector <Integer>();
		//Vector <Integer> lm =new Vector <Integer>();
		spxml.println("<SENTENCE id=\'s"+sentence_num+"\'>");
		spxml.print("<CONTENT>");spxml.print(content);spxml.println("</CONTENT>");
		//for(int i=0;i<relations.size();i++){
		 //relation t=relations.elementAt(i);
		 //if(!tr.contains(t.wh_tr.head_index)){
	      //tr.addElement(t.wh_tr.head_index);
		  //spxml.println("<TRAJECTOR id=\'tw"+t.wh_tr.head_index+"\'>"+ t.wh_tr.ph_head+"</TRAJECTOR>");}
		 //if (!lm.contains(t.wh_lm.head_index)){
		  // lm.addElement(t.wh_lm.head_index);	 
		   //spxml.println("<LANDMARK id=\'lw"+t.wh_lm.head_index+"\'>"+ t.wh_lm.ph_head+"</LANDMARK>");}
		 //spxml.print("<SPATIAL_INDICATOR id=\'sw"+t.wh_sp.head_index+"\'>");
		 //for(int j=0;j<t.wh_sp.ph.size();j++)
		 //spxml.print( t.wh_sp.ph.elementAt(j)+" ");
		 //spxml.println("</SPATIAL_INDICATOR>");
		 //String tempg=t.calc_type.general.elementAt(0).toLowerCase();
		 //tempg=tempg.replaceAll("\"","");
		 //tempg=tempg.trim();
		// if (!(tempg.equals("distance")|| tempg.equals("region")|| tempg.equals("direction")))
			// System.out.print("Stop");
		 
		// spxml.println("<RELATION id=\'r"+(i)+"\'\tsp=\'sw"+t.wh_sp.head_index+"\'\ttr=\'tw"+t.wh_tr.head_index+"\'\tlm=\'lw"+t.wh_lm.head_index+"\'"+"\tgeneral_type=\'"+tempg+"\'/>");
		// }//end for relation
		spxml.println("</SENTENCE>");
		spxml.println();
		
	}
	public void writeXML(PrintWriter spxml,int sentence_num){
		Vector <Integer> tr =new Vector <Integer>();
		Vector <Integer> lm =new Vector <Integer>();
		Vector <Integer> sp= new Vector <Integer>();
		spxml.println("<SENTENCE id=\'s"+sentence_num+"\'>");
		spxml.print("<CONTENT>");spxml.print(content);spxml.println("</CONTENT>");
		for(int i=0;i<relations.size();i++){
		 relation t=relations.elementAt(i);
		 if(!tr.contains(t.wh_tr.head_index)){
	      tr.addElement(t.wh_tr.head_index);
		  spxml.println("<TRAJECTOR id=\'tw"+t.wh_tr.head_index+"\'>"+ t.wh_tr.ph_head+"</TRAJECTOR>");}
		 if (!lm.contains(t.wh_lm.head_index)){
		   lm.addElement(t.wh_lm.head_index);	 
		   spxml.println("<LANDMARK id=\'lw"+t.wh_lm.head_index+"\'>"+ t.wh_lm.ph_head+"</LANDMARK>");}
		 if(!sp.contains(t.wh_sp.head_index)){
		   sp.addElement(t.wh_sp.head_index);	 
		   spxml.print("<SPATIAL_INDICATOR id=\'sw"+t.wh_sp.head_index+"\'>");
		 for(int j=0;j<t.wh_sp.ph.size();j++)
		 spxml.print( t.wh_sp.ph.elementAt(j)+" ");
		 spxml.println("</SPATIAL_INDICATOR>");}
		 String tempg=t.calc_type.general.elementAt(0).toLowerCase();
		 tempg=tempg.replaceAll("\"","");
		 tempg=tempg.trim();
		 if (!(tempg.equals("distance")|| tempg.equals("region")|| tempg.equals("direction")))
			 System.out.print("Stop");
		 
		 spxml.println("<RELATION id=\'r"+(i)+"\'\tsp=\'sw"+t.wh_sp.head_index+"\'\ttr=\'tw"+t.wh_tr.head_index+"\'\tlm=\'lw"+t.wh_lm.head_index+"\'"+"\tgeneral_type=\'"+tempg+"\'/>");
		 }//end for relation
		spxml.println("</SENTENCE>");
		spxml.println();
	}
	public void writeXMLphrase(PrintWriter spxml,int sentence_num){
		Vector <Integer> tr =new Vector <Integer>();
		Vector <Integer> lm =new Vector <Integer>();
		Vector <Integer> sp= new Vector <Integer>();
		spxml.println("<SENTENCE id=\'s"+sentence_num+"\'>");
		spxml.print("<CONTENT>");spxml.print(content);spxml.println("</CONTENT>");
		for(int i=0;i<relations.size();i++){
		 relation t=relations.elementAt(i);
		 if(!tr.contains(t.wh_tr.head_index)){
	      tr.addElement(t.wh_tr.head_index);
		  spxml.println("<TRAJECTOR id=\'tw"+t.wh_tr.head_index+"\'>");
		  for(int j=0;j<t.wh_tr.ph.size();j++)
				 spxml.print( t.wh_tr.ph.elementAt(j)+" ");
				 spxml.println("</TRAJECTOR>");}
		 
		 if (!lm.contains(t.wh_lm.head_index)){
		   lm.addElement(t.wh_lm.head_index);	
		   spxml.println("<LANDMARK id=\'lw"+t.wh_lm.head_index+"\'>");
		   for(int j=0;j<t.wh_lm.ph.size();j++)
				 spxml.print( t.wh_lm.ph.elementAt(j)+" ");
				 spxml.println("</LANDMARK>");}
		   
		  // + t.wh_lm.ph_head+"</LANDMARK>");}
		 if(!sp.contains(t.wh_sp.head_index)){
		   sp.addElement(t.wh_sp.head_index);	 
		   spxml.print("<SPATIAL_INDICATOR id=\'sw"+t.wh_sp.head_index+"\'>");
		 for(int j=0;j<t.wh_sp.ph.size();j++)
		 spxml.print( t.wh_sp.ph.elementAt(j)+" ");
		 spxml.println("</SPATIAL_INDICATOR>");}
		 String tempg=t.calc_type.general.elementAt(0).toLowerCase();
		 tempg=tempg.replaceAll("\"","");
		 tempg=tempg.trim();
		 if (!(tempg.equals("distance")|| tempg.equals("region")|| tempg.equals("direction")))
			 System.out.print("Stop");
		 
		 spxml.println("<RELATION id=\'r"+(i)+"\'\tsp=\'sw"+t.wh_sp.head_index+"\'\ttr=\'tw"+t.wh_tr.head_index+"\'\tlm=\'lw"+t.wh_lm.head_index+"\'"+"\tgeneral_type=\'"+tempg+"\'/>");
		 }//end for relation
		spxml.println("</SENTENCE>");
		spxml.println();
	}
	public int svm_struct_output(int w){
		   int S=-1;
		   for(int r=0;r<relations.size();r++)
		   {
			   if (relations.elementAt(r).wh_lm.head_index==w) S=1;
			   if (relations.elementAt(r).wh_tr.head_index==w) S=2;
			   if (relations.elementAt(r).wh_sp.head_index==w) S=3;
		   } 
		  if (S==-1) S=0;
		   return S;
	   }
	
	
	//This below precedure is going to produce all input relational features. 
	public void writeRelationalFeatureSvmStruct(Vector <Vector<String>> atts,PrintWriter pw, PrintWriter pwPredicates){
		 /* arff is to collect the features in a structured way, arff-0 is to have the possible word forms
	     * arff-1 is to collect the possible pos tags, arff-2 is to collect subcategorization, arff-3 is to 
	     * collect dprl-features and arff-4 is for srl-features
	     */
		 

		for (int w=0; w<this.sentence_feat.size();w++)
				{  
			      if (this.content.toLowerCase().contains("blackboard and teacher"))
			    	  System.out.print("Stop");
			      Vector <Integer>  featList=new Vector <Integer>();
    			 
    			     String s="";
    			     s=constant_correction(sentence_feat.elementAt(w).words.word);
			         featList.addElement(atts.elementAt(0).indexOf(s));
			         pwPredicates.print("word("+(w+1)+","+s+").");
			         
		             s=constant_correction(sentence_feat.elementAt(w).words.pos);
    			     featList.addElement(atts.elementAt(1).indexOf(s));
    			     pwPredicates.print("pos("+(w+1)+","+s+").");
  	               
    			     s=constant_correction(sentence_feat.elementAt(w).words.subcategorization);
				     featList.addElement(atts.elementAt(2).indexOf(s));
				     pwPredicates.print("subcat("+(w+1)+","+s+").");
		               
				     s=constant_correction(sentence_feat.elementAt(w).words.DPRL);
				     featList.addElement(atts.elementAt(3).indexOf(s));
				     pwPredicates.print("dprl("+(w+1)+","+s+").");
		                
				     s=constant_correction(sentence_feat.elementAt(w).words.SRL);
				     featList.addElement(atts.elementAt(4).indexOf(s));
				     pwPredicates.println("srl("+(w+1)+","+s+").");
			         
				     featList.addElement(svm_struct_output(w));
				     
			           
				     
				     
		    		 for(int t1=0; t1<featList.size()-1;t1++){
 	    		     //for (int t2=0;t2<atts.elementAt(t1).size();t2++)
		    			//if(featList.elementAt(t1)!=-1)
  			            	pw.print(featList.elementAt(t1)+", ");
  			           //  else 
  			            // 	pw.print("0 ");
 	    		     }// end for each feature
		    		pw.println(featList.elementAt(featList.size()-1));
		    		 	
	 	    		     //for (int t2=0;t2<atts.element));
		    		}// end for all words 
		pw.println();
	
	}// end write features prosedure
	
	
	
	public void writeRelationalOutputSvmStruct(PrintWriter pw,PrintWriter pwPredicates, int num[]){
		
		
		int j=0;
		for(int i=0;i<relations.size();i++){
	    		
	     
		pw.print(relations.elementAt(i).wh_sp.head_index+1+","); j=relations.elementAt(i).wh_sp.head_index-relations.elementAt(i).wh_tr.head_index;  if (j<0) j=j*(-1); if (j<32) num[j]++;  
		pw.print(relations.elementAt(i).wh_tr.head_index+1+","); j=relations.elementAt(i).wh_tr.head_index-relations.elementAt(i).wh_lm.head_index; if (j<0) j=j*(-1); if (j<32) num[j]++;
		pw.println(relations.elementAt(i).wh_lm.head_index+1);   j=relations.elementAt(i).wh_sp.head_index-relations.elementAt(i).wh_lm.head_index;if (j<0)  j=j*(-1); if (j<32) num[j]++;
		pwPredicates.print(relations.elementAt(i).wh_sp.ph_head+","); 
		pwPredicates.print(relations.elementAt(i).wh_tr.ph_head+",");
		pwPredicates.println(relations.elementAt(i).wh_lm.ph_head+",");
		}
		pw.println();
		pwPredicates.println();
	}
	public int distance(int i,int j){
		int dis=0;
		if (i>j)
		{
			int t=j;
			j=i;
			i=t;
		}
		if (j>=sentence_feat.size())
			{dis=33; return dis;}
		for(int iter=i; iter<=j;iter++)
		{
		String S=sentence_feat.elementAt(iter).words.pos.toLowerCase();	
		if (S.contains("nn") ||	S.contains("nns") || S.contains("prp")|| S.contains("in")||S.contains("on")|| S.contains("to"))
		{ 
			dis++; }
		}
		return dis;
	}
	//to be filled in
public void writeRelationalOutputSvmStruct_Statistics(PrintWriter pw,PrintWriter pwPredicates, int num[],int numdis[],int numneg[],int numnegdis[]){
	    
		//num[0]=num[0]+this.nonrelations.size();
	//	num[1]=num[1]+this.relations.size();
		int d1=0,d2=0,d3=0;
		int dideal=0;
		int j=0;
		for(int i=0;i<relations.size();i++){
	    d1=relations.elementAt(i).wh_sp.head_index-relations.elementAt(i).wh_tr.head_index;  if (d1<0) d1=d1*(-1);// if (j<32) num[j]++;  
		 d2=relations.elementAt(i).wh_tr.head_index-relations.elementAt(i).wh_lm.head_index; if (d2<0) d2=d2*(-1); //if (j<32) num[j]++;
	  d3=relations.elementAt(i).wh_sp.head_index-relations.elementAt(i).wh_lm.head_index;if (d3<0)  d3=d3*(-1); //if (j<32) num[j]++;
		if (d1>d2)
		 dideal=d1;
		else 
			dideal=d2;
		if (d3 >dideal)
		dideal=d3;
		
		if (dideal<0) dideal=dideal*(-1); if (dideal<32) num[dideal]++; 
		if (dideal>=9)
		{  pw.print(relations.elementAt(i).wh_sp.head_index+1+",");
		   pw.print(relations.elementAt(i).wh_tr.head_index+1+",");
		   pw.println(relations.elementAt(i).wh_lm.head_index+1); 
		   pw.println(content+"\t"+dideal);}
		j=distance(relations.elementAt(i).wh_sp.head_index,relations.elementAt(i).wh_tr.head_index);if (j<0) j=j*(-1); if (j<32) numdis[j]++;
		j=distance(relations.elementAt(i).wh_tr.head_index,relations.elementAt(i).wh_lm.head_index);if (j<0) j=j*(-1); if (j<32) numdis[j]++;
		j=distance(relations.elementAt(i).wh_sp.head_index,relations.elementAt(i).wh_lm.head_index);if (j<0) j=j*(-1); if (j<32) numdis[j]++;
		
		pwPredicates.print(relations.elementAt(i).wh_sp.ph_head+","); 
		pwPredicates.print(relations.elementAt(i).wh_tr.ph_head+",");
		pwPredicates.println(relations.elementAt(i).wh_lm.ph_head+",");
		}
		pw.println();
		pwPredicates.println();
		
	  for (int i=0; i<allnonrel.size();i++)
	  {
	   d1=allnonrel.elementAt(i).sp-allnonrel.elementAt(i).tr;  if (d1<0) d1=d1*(-1); //if (j<32) numneg[j]++;  
	   d2=allnonrel.elementAt(i).tr-allnonrel.elementAt(i).lm; if (d2<0) d2=d2*(-1);// if (j<32) numneg[j]++;
	   d3=allnonrel.elementAt(i).sp-allnonrel.elementAt(i).lm;if (j<0)  d3=d3*(-1); //if (j<32) numneg[j]++;
	   
	   if (d1>d2)
			 dideal=d1;
			else 
				dideal=d2;
			if (d3 >dideal)
			dideal=d3;
			if (dideal<0) dideal=dideal*(-1); if (dideal<32) numneg[dideal]++;  
			
	   
	   
	   j=distance(allnonrel.elementAt(i).sp,allnonrel.elementAt(i).tr);  if (j<0) j=j*(-1); if (j<32) numnegdis[j]++;  
	   j=distance(allnonrel.elementAt(i).tr,allnonrel.elementAt(i).lm); if (j<0) j=j*(-1); if (j<32) numnegdis[j]++;
	   j=distance(allnonrel.elementAt(i).sp,allnonrel.elementAt(i).lm);if (j<0)  j=j*(-1); if (j<32) numnegdis[j]++;
	  }
	}
	
	public void writeComplexRelationalFSvmStruct(Vector <String> atts , PrintWriter p)
	{
		for (int w=0; w<this.sentence_feat.size();w++)
		{  }
		
	}
	public void collect_RelCandidatesForSvm(Vector <Vector<String>> arff){
		    
		  for(int w=0;w<this.sentence_feat.size();w++){
			
		  	 //int lm_index=allPosRelCandids.elementAt(r).lm,tr_index=allPosRelCandids.elementAt(r).tr,sp_index=allPosRelCandids.elementAt(r).sp;
			  //if (relations.elementAt(r).wh_tr.head_index!=-1)
			  //**********************trajector features  
		//	 if (sp_candidates.contains(w) || lm_candidates.contains(w)) {
			    /* arff is to collect the features in a structured way, arff-0 is to have the possible word forms
			     * arff-1 is to collect the possible pos tags, arff-2 is to collect subcategorization, arff-3 is to 
			     * collect dprl-features and arff-4 is for srl-features
			     */
				  
			      	//****************spatial indicator features
				    if (!arff.elementAt(0).contains(constant_correction(sentence_feat.elementAt(w).words.word)))
						   arff.elementAt(0).addElement(constant_correction(sentence_feat.elementAt(w).words.word));
				 	
				    if (!arff.elementAt(1).contains(constant_correction(sentence_feat.elementAt(w).words.pos)))
						   arff.elementAt(1).addElement(constant_correction(sentence_feat.elementAt(w).words.pos));
					 
					  if (!arff.elementAt(2).contains(constant_correction(sentence_feat.elementAt(w).words.subcategorization)))
							   arff.elementAt(2).addElement(constant_correction(sentence_feat.elementAt(w).words.subcategorization));
						
					 
					  if (!arff.elementAt(3).contains(constant_correction(sentence_feat.elementAt(w).words.DPRL)))
						   arff.elementAt(3).addElement(constant_correction(sentence_feat.elementAt(w).words.DPRL));

					  if (!arff.elementAt(4).contains(constant_correction(sentence_feat.elementAt(w).words.SRL)))
						   arff.elementAt(4).addElement(constant_correction(sentence_feat.elementAt(w).words.SRL));
					  
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
// 			 ////********** Features of  roles		
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
	 }//end procedure to collect the relational features
//	ArrayList <ArrayList<Integer>> matchOntoBio(BIOsentence s){
//		ArrayList <ArrayList <Integer>> L=new ArrayList<ArrayList<Integer>>();
//		
//		for (int i=0;i<s.sentence_feat.size();i++)
//		{	
//			ArrayList<Integer>l1=new ArrayList<Integer>();
//			l1.add(i);
//		}
//		L.add(l1);
//		return L;
//	}
//	
	
	
	
	void generate_candidate_features(Vector <String> atts){
			 /* arff is to collect the features in a structured way, arff-0 is to have the possible word forms
		     * arff-1 is to collect the possible pos tags, arff-2 is to collect subcategorization, arff-3 is to 
		     * collect dprl-features and arff-4 is for srl-features
		     */
	  //  ArrayList <iEntity> candids=new ArrayList<iEntity>(); 
         //	candids=matchOntoBio(this);
	
		for (int c=0; c<this.lm_candidates.size();c++)
					{  
			           iEntity e1=this.lm_candidates.get(c);
			           
			           for (int f=0;f<e1.features.size();f++){
			        	e1.initialize(atts.size());
			        	int index= atts.elementAt(0).indexOf(e1.features.get(f));
			           //e1.wordOffsets.add(sentence_feat.elementAt(w).words.wordOffset);
			           // Vector <Integer>  featList=new Vector <Integer>();
	    			 //  String s="";
	    			   //s=constant_correction(sentence_feat.elementAt(c).words.word);
				       if (index!=-1)
			        	e1.featureMap[index]=1;
				       }
				      /* e1.features.add(s);
				         //pwPredicates.print("word("+(w+1)+","+s+").");
				       s=constant_correction(sentence_feat.elementAt(c).words.pos);
	    			   e1.featureMap.add(atts.elementAt(1).indexOf(s));
	    			   e1.features.add(s);
	    			     //pwPredicates.print("pos("+(w+1)+","+s+").");
	  	               
	    			   s=constant_correction(sentence_feat.elementAt(c).words.subcategorization);
					   e1.featureMap.add(atts.elementAt(2).indexOf(s));
					   e1.features.add(s);
					     //pwPredicates.print("subcat("+(w+1)+","+s+").");
			               
					    s=constant_correction(sentence_feat.elementAt(c).words.DPRL);
					    e1.featureMap.add(atts.elementAt(3).indexOf(s));
					     //pwPredicates.print("dprl("+(w+1)+","+s+").");
					    e1.features.add(s);
					    s=constant_correction(sentence_feat.elementAt(c).words.SRL);
					    e1.featureMap.add(atts.elementAt(4).indexOf(s));
					    e1.features.add(s);
					     //pwPredicates.println("srl("+(w+1)+","+s+").");
				         //featList.addElement(svm_struct_output(w));
					    // for(int t1=0; t1<featList.size()-1;t1++){
	 	    		     // 	pw.print(featList.elementAt(t1)+", ");
	  			          // }// end for each feature
			    		//pw.println(featList.elementAt(featList.size()-1));
			    	//	 candids.add(e1);}// end for all words }
*/					}
		//	pw.println();
			// return (this.lm_candidates);
		}// end write features prosedure
	
	
}// end of class sentence
