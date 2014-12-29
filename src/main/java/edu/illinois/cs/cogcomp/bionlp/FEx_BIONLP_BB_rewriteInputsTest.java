package edu.illinois.cs.cogcomp.bionlp;/*This is the last program that adds the relations to the examples and produces the inputs of Nfoil to classify each example as sr-non-sr*/
//package GeneralData_preprocess;

import edu.illinois.cs.cogcomp.bionlp.Utility.Statistics;
import edu.illinois.cs.cogcomp.bionlp.Utility.TreeNode;
import edu.ucdenver.ccp.nlp.biolemmatizer.BioLemmatizer;
import edu.ucdenver.ccp.nlp.biolemmatizer.LemmataEntry;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Vector;

public class FEx_BIONLP_BB_rewriteInputsTest {
 static Statistics statis=new Statistics();
 //static String[] StopWords;
 //static NCBIRepresentation NCBIRep=new NCBIRepresentation();
 //static OBORepresentation obRep=new OBORepresentation();
	
	public static int HeadW_with_index(TreeNode t,TreeNode x,Vector<Vector <String>> M){
		Vector<TreeNode> xs=x.getLeafNodes();
		Vector <TreeNode> ch=t.getLeafNodes();
		
		int index1=-1;
		for(int i=0;i<ch.size(); i++)
		{ 
			TreeNode xtemp=xs.elementAt(0);
			TreeNode TreeTemp=ch.elementAt(i);
			while ( xtemp!=null & TreeTemp!=null ){
				if(xtemp.equals(TreeTemp)) {
				xtemp=xtemp.getParent();
				TreeTemp=TreeTemp.getParent();
				if (xtemp==null & TreeTemp==null){ index1=i; break;}
				}
				else break;}
			if (index1!=-1) break;
		}
		Vector <String> cons=x.getAllWords();
		int consarray[][];
		consarray = new int[cons.size()][2]; 
		 for (int i2=0;i2<cons.size();i2++){
					consarray[i2][0]=Integer.parseInt(M.elementAt(index1).elementAt(0));
					consarray[i2][1]=Integer.parseInt(M.elementAt(index1).elementAt(8));
					index1++;
					}
       
		if (cons.size()==1){
			return consarray[0][0]-1;}
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
			index=consarray[i1][0]-1;  break;}
		}// for i1
		return index;
	}
	///////////////////////////////////////////////////////
	public static String SRL(int index, Vector<Vector <String>> M)
	{
		for(int i=10;i<M.elementAt(index).size();i++)
		{
			if(!M.elementAt(index).elementAt(i).equals("_"))
			return(M.elementAt(index).elementAt(i));}
		return("");
	}
	/////////////////////////////////////////////////////
	public static  String[] sortIntegerStrings(String[] a) {
		String[] orderedchars=new String[a.length];
    	int[] intchars=new int[a.length];
		
		for(int u=0;u<a.length;u++){
			intchars[u]=Integer.parseInt(a[u].replaceAll( "[^\\d]", "" ));
	     }
		Arrays.sort(intchars);
		for (int u=0;u<intchars.length;u++){
			for(int u2=0; u2<a.length;u2++){
		     if (a[u2].replaceAll( "[^\\d]", "" ).equals(Integer.toString(intchars[u])))
		    	 {orderedchars[u]=a[u2];System.out.println(u2);break;}}
		}
		return orderedchars;}
	
	public static void sort_check(String[] a, String[] b){
		if (a.length!=b.length)
			System.out.print("the number of files does not match!!");
		else{
			
		 }
	}
	public static void make_feature_header(Vector<Vector<String>> Att) throws IOException
	{
		String strDirectoyout ="./DataSets/collected-features_bio"; // The directory of the output database files
		(new File(strDirectoyout)).mkdirs();
		for(int i=0;i<Att.size();i++)
		{ 
		 	
		  Collections.sort(Att.elementAt(i));	
		  PrintWriter local_features=new PrintWriter(new BufferedWriter (new FileWriter(strDirectoyout+"/phi_"+i+".txt")));
		  for (int j=0; j<Att.elementAt(i).size();j++)
	      local_features.print(j+"\t"+Att.elementAt(i).elementAt(j)+"\n");
		  local_features.close();
	    }
	}// end make header files
	private static BioLemmatizer bioLemmatizer;	
	
	public static void main(String[] args) throws Exception{
	    
	bioLemmatizer=new BioLemmatizer(); 
    
	LemmataEntry l=new LemmataEntry(null,null);
	l.lemmasToString();
	//l.lemmatizeByLexicon("bacteria", "NNS");
//String v=l.lemmasToString();
		// test the string similarity
		/*String S1="kitting are good";
		String S2="settings are bad no";
		int d=StringSimilarity.editDistance(S1, S2);
		Pattern p=Pattern.compile(" ");
	    String[] s1=p.split(S1);
	    String[] s2=p.split(S2);
	    double a=StringSimilarity.Jaccard(s1,s2);*/
	    
	    String strDirectoryout="./DataSets/structuredFVBIO";
	  //  String strDirectoryout1="./DataSets/BB_task_2_train_dev";
	    (new File(strDirectoryout)).mkdirs();
	   // (new File(strDirectoryout1)).mkdirs();
	    (new File(strDirectoryout+"/entities")).mkdirs(); 
	    (new File(strDirectoryout+"/relations")).mkdirs(); 
	    (new File(strDirectoryout+"/entityLabels")).mkdirs();
	    (new File(strDirectoryout+"/outputPredicates")).mkdirs();
	    (new File(strDirectoryout+"/inputSparseRel")).mkdirs();
	    (new File(strDirectoryout+"/inputPredicates")).mkdirs();
	    (new File(strDirectoryout+"/relationLabels")).mkdirs();
	    (new File(strDirectoryout+"/Coreferences")).mkdirs();
	    (new File(strDirectoryout+"/CorefFeatures")).mkdirs();
	    (new File(strDirectoryout+"/EntitySimilarity")).mkdirs();
	    
	    String strDirectoryin ="./DataSets/BioNLP-ST-2013_Bacteria_Biotopes_test/task_3/"; // The directory of the output database files
		(new File(strDirectoryin)).mkdirs();
		File f = new File("./DataSets/BioNLP-ST-2013_Bacteria_Biotopes_test/BioNLP-ST-2013_Bacteria_Biotopes_test_mcccj/task_3/");
    	File fsource=new File("./DataSets/BioNLP-ST-2013_Bacteria_Biotopes_test/task_3/");
		File splitsource=new File("./DataSets/BioNLP-ST-2013_Bacteria_Biotopes_test/BioNLP-ST-2013_Bacteria_Biotopes_testssplit/task_3/");
    //	File t1a2=new File("./DataSets/BioNLP-ST-2013_Bacteria_Biotopes_train_dev/task_1/");
    	File Cocoa=new File("./DataSets/BioNLP-ST-2013_Bacteria_Biotopes_test/BioNLP-2013_Bacteria_Biotopes_test_cocoa/task_3/"); 
    //	File t2a1=new File("./DataSets/BioNLP-ST-2013_Bacteria_Biotopes_train_dev/task_2/");
    //	File t2a2=new File("./DataSets/BioNLP-ST-2013_Bacteria_Biotopes_train_dev/task_2/");
    	
    	 Vector <PrintWriter> entityfileinputs=new Vector <PrintWriter>();
    	 Vector <PrintWriter> entityLabels=new Vector<PrintWriter>();
    	 Vector <PrintWriter> relationSparsefile=new Vector<PrintWriter>();
    	 Vector <PrintWriter> relationfileinputs=new Vector<PrintWriter>();
    	 Vector <PrintWriter> relationLabels=new Vector<PrintWriter>();
    	 Vector <PrintWriter> Coreferences=new Vector<PrintWriter>();
    	 Vector <PrintWriter> CorefFeatures=new Vector<PrintWriter>();
    	 Vector <PrintWriter> Esimilarity=new Vector<PrintWriter>();
    	 Vector <PrintWriter> ConllX2=new Vector<PrintWriter>();
    	// Vector<PrintWriter> a2_task2_train=new Vector<PrintWriter>();
    	
     	 Vector <PrintWriter> inputpredicates=new Vector <PrintWriter>();
         Vector <PrintWriter> outputpredicates=new Vector <PrintWriter>();
        
       
        // Vector <PrintWriter> examplefileoutputs=new Vector <PrintWriter>(); 
    	//File OntoBiotope=new File("/DataSets/OntoBiotope_BioNLP-ST13.obo");
    	
		FilenameFilter conllFilter = new FilenameFilter() {
			public boolean accept(File dir, String name) {
				String lowercaseName = name.toLowerCase();
				if (lowercaseName.endsWith(".connlx")) {
					return true;
				} else {
					return false;
				}
			}
		};
		FilenameFilter cocoAFilter = new FilenameFilter() {
			public boolean accept(File dir, String name) {
				String lowercaseName = name.toLowerCase();
				if (lowercaseName.endsWith(".ann")) {
					return true;
				} else {
					return false;
				}
			}
		};
		FilenameFilter ptbFilter = new FilenameFilter() {
			public boolean accept(File dir, String name) {
				String lowercaseName = name.toLowerCase();
				if (lowercaseName.endsWith(".ptb")) {
					return true;
				} else {
					return false;
				}
			}
		};
		FilenameFilter sdepFilter = new FilenameFilter() {
			public boolean accept(File dir, String name) {
				String lowercaseName = name.toLowerCase();
				if (lowercaseName.endsWith(".sdep")) {
					return true;
				} else {
					return false;
				}
			}
		};
		FilenameFilter txtFilter = new FilenameFilter() {
			public boolean accept(File dir, String name) {
				String lowercaseName = name.toLowerCase();
				if (lowercaseName.endsWith(".txt")) {
					return true;
				} else {
					return false;
				}
			}
		};
		FilenameFilter ssFilter = new FilenameFilter() {
			public boolean accept(File dir, String name) {
				String lowercaseName = name.toLowerCase();
				if (lowercaseName.endsWith(".ss")) {
					return true;
				} else {
					return false;
				}
			}
		};
		FilenameFilter a2Filter = new FilenameFilter() {
			public boolean accept(File dir, String name) {
				String lowercaseName = name.toLowerCase();
				if (lowercaseName.endsWith(".a2")) {
					return true;
				} else {
					return false;
				}
			}
		};
		FilenameFilter a1Filter = new FilenameFilter() {
			public boolean accept(File dir, String name) {
				String lowercaseName = name.toLowerCase();
				if (lowercaseName.endsWith(".a1")) {
					return true;
				} else {
					return false;
				}
			}
		};
		File[] conllfiles = f.listFiles(conllFilter);
		File[] ptbfiles = f.listFiles(ptbFilter);
		File[] sdepfiles = f.listFiles(sdepFilter);
		File[] txtfiles = fsource.listFiles(txtFilter);
		File[] ssfiles= splitsource.listFiles(ssFilter);
	//	File[] t1a2files= t1a2.listFiles(a2Filter);
		File[] CocoaFiles= Cocoa.listFiles(cocoAFilter);
		//File[] t2a1files=t2a1.listFiles(a1Filter);
	//	File[] t2a2files=t2a2.listFiles(a2Filter);
	    int sentence_num=0;
 Vector <Vector<String>> arffAttRel =new Vector<Vector<String>>();
      arffAttRel.add(0,new Vector<String>());
      arffAttRel.add(1,new Vector<String>());
      arffAttRel.add(2,new Vector<String>());
      arffAttRel.add(3,new Vector<String>());
      arffAttRel.add(4,new Vector<String>());
      arffAttRel.add(5,new Vector<String>());// this is for the features of a compound not a word only
      ArrayList <BIOdiscourse> training=new ArrayList<BIOdiscourse>();
     // StopWords=ReadStopWords();
//      NCBIParser objParser=new NCBIParser("./DataSets/taxonomie_ncbi.txt");
//  	  try {
//  		    NCBIRep= objParser.parseFile();
//    	} catch (IOException e) {
//  		// TODO Auto-generated catch block
//  		e.printStackTrace();
//  	    }
//    	OBOParser oboParser=new OBOParser("./DataSets/OntoBiotope_BioNLP-ST13.obo");
//    	try {
//    		obRep= oboParser.parseFile();
//    		System.out.println("number of elements" + obRep.getNumberOfObo());
//    	//	ArrayList<String>  objExactSyns=  obRep.getExactAllSynonyms();
//    	//	ArrayList<String>  objRelatedSyns=  obRep.getRelatedAllSynonyms();
//    		ArrayList<String>  objNames=  obRep.getAllNames();
//    		
//    		
//    	} catch (IOException e) {
//    		// TODO Auto-generated catch block
//    		e.printStackTrace();
//    	}
      
      for (int i=0;i<txtfiles.length ;i++)
       {
    	    BIOdiscourse discourse1=new BIOdiscourse();
    	    String file_name=conllfiles[i].getName().substring(0,conllfiles[i].getName().indexOf("."));
         	BufferedReader brconnl = new BufferedReader(new FileReader(f.getPath()+"/"+file_name+".connlx"));
    		BufferedReader brptb = new BufferedReader(new FileReader(f.getPath()+"/"+file_name+".ptb"));
    		BufferedReader brssplit= new BufferedReader(new FileReader(splitsource.getPath()+"/"+file_name+".ss"));
    	//	BufferedReader brt1a2=new BufferedReader(new FileReader(t1a2.getPath()+"/"+file_name+".a2"));
    		BufferedReader brCocoa=new BufferedReader(new FileReader(Cocoa.getPath()+"/"+file_name+".ann"));
    	//	BufferedReader brt2a1=new BufferedReader(new FileReader(t2a1.getPath()+"/"+file_name+".a1"));
    	//	BufferedReader brt2a2=new BufferedReader(new FileReader(t2a2.getPath()+"/"+file_name+".a2"));
    		//BufferedReader bioOnto = new BufferedReader(new FileReader(OntoBiotope));
    		if (file_name.contains("10090"))
   			System.out.print(file_name);
    		PrintWriter discoursefile=new PrintWriter(new BufferedWriter (new FileWriter(strDirectoryin+file_name+".xml")));
    		sentence_num++;
    		ConllX2.addElement(new PrintWriter(new BufferedWriter(new FileWriter(f.getPath()+"/"+file_name+".connlx2"))));
    		//discourse1.fetch_ling_features(brconnl,brptb,brssplit, brCocoa);
    		
    		//a2_task2_train.addElement(new PrintWriter(new BufferedWriter(new FileWriter(strDirectoryout1+"/"+file_name+".a2"))));
    		//discourse1.rewrite_a2_task2(brt2a2, a2_task2_train.lastElement()); 
     		discourse1.rewrite_lth(brconnl,ConllX2.lastElement(), bioLemmatizer);
    		//***discourse1.paragraph_info();
    		//discourse1.build_all_candidates();
    		//discourse1.fetch_t1a1_annotations(brt1a2);

    		///***discourse1.fetch_t2a1a2_annotationsAndCand(brt2a1,brt2a2);
    		//discourse1.addNegRelCand();
    		//****discourse1.candidate_features(arffAttRel);
    		//***discourse1.EntitySimilarity();
    	  
    		//  discourse1.collect_featureLexicon(arffAttRel.elementAt(0));
           // discourse1.collect_RelCandidatesForSOP(arffAttRel);
            //discourse1.writeDiscourseXML(discoursefile, sentence_num,0,1);
    		if (i==15) 
    			System.out.print("Stop for check!");
    		 entityfileinputs.addElement(new PrintWriter(new BufferedWriter(new FileWriter(strDirectoryout+"/entities/"+file_name+".data"))));
    		 entityLabels.addElement(new PrintWriter(new BufferedWriter(new FileWriter(strDirectoryout+"/entityLabels/"+file_name+".data"))));
            // examplefileoutputs.addElement(new PrintWriter(new BufferedWriter(new FileWriter(strDirectoryout+"/outputs/"+file_name+(i+1)+".data"))));
             inputpredicates.addElement(new PrintWriter(new BufferedWriter(new FileWriter(strDirectoryout+"/inputPredicates/"+file_name+".data"))));
             outputpredicates.addElement(new PrintWriter(new BufferedWriter(new FileWriter(strDirectoryout+"/outputPredicates/"+file_name+".data"))));
             relationfileinputs.addElement(new PrintWriter(new BufferedWriter(new FileWriter(strDirectoryout+"/relations/"+file_name+".data"))));
             relationSparsefile.addElement(new PrintWriter(new BufferedWriter(new FileWriter(strDirectoryout+"/inputSparseRel/"+file_name+".data"))));
             relationLabels.addElement(new PrintWriter(new BufferedWriter(new FileWriter(strDirectoryout+"/relationLabels/"+file_name+".data"))));
             Coreferences.addElement(new PrintWriter(new BufferedWriter(new FileWriter(strDirectoryout+"/Coreferences/"+file_name+".data"))));
             CorefFeatures.addElement(new PrintWriter(new BufferedWriter(new FileWriter(strDirectoryout+"/CorefFeatures/"+file_name+".data"))));
             Esimilarity.addElement(new PrintWriter(new BufferedWriter(new FileWriter(strDirectoryout+"/EntitySimilarity/"+file_name+".data"))));
             //%%%%%%%%%%%%%%%%%%%%%%
            //*** discourse1.writeRelationalFeatureSO(arffAttRel,entityfileinputs.lastElement(),entityLabels.lastElement(),relationfileinputs.lastElement(),relationLabels.lastElement(),relationSparsefile.lastElement(),inputpredicates.lastElement(), outputpredicates.lastElement(),Coreferences.lastElement(),CorefFeatures.lastElement());
             //***discourse1.writeEntitySimilarity(Esimilarity.lastElement());
             //training.add(discourse1);
             discoursefile.close();
          }
          make_feature_header(arffAttRel);
          writeStatistics();
          
          /*ArrayList <iEntity> trainingEx=new ArrayList<iEntity>();
          for (int i=0; i<training.size();i++)
           {
        	  BIOdiscourse di1=training.get(i);
        	  for (int j=0; j<di1.allsentences.size();j++)
        	    { 
        		  BIOsentence sen1=di1.allsentences.elementAt(j);
        	//	  sen1.buildEntities();
        		//  sen1.buildRelationships();
        		  //sen1.updateLexicon(arffAttRel);
        		
        		  sen1.generate_candidate_features(arffAttRel.elementAt(0));
        		  
        		  ArrayList <iEntity> temp= di1.allsentences.elementAt(j).lm_candidates;

        		  for (int ind=0; ind<temp.size(); ind++)
        		   { 
        			  temp.get(ind).label=di1.FindAnn(temp.get(ind).wordOffsets,i);
        		   }
        		  if (!temp.isEmpty())
        		  trainingEx.addAll(temp);
        		
       		  }
           }*/
         for(int filecounter=0;filecounter<entityfileinputs.size();filecounter++)
     	 {
     	 entityfileinputs.elementAt(filecounter).close();
     	 //examplefileoutputs.elementAt(filecounter).close();
     	 inputpredicates.elementAt(filecounter).close();
     	 outputpredicates.elementAt(filecounter).close();
     	 relationfileinputs.elementAt(filecounter).close();
     	 relationSparsefile.elementAt(filecounter).close();
     	 entityLabels.elementAt(filecounter).close();
     	 relationLabels.elementAt(filecounter).close();
     	 Coreferences.elementAt(filecounter).close();
     	 CorefFeatures.elementAt(filecounter).close();
     	 Esimilarity.elementAt(filecounter).close();
     	 ConllX2.elementAt(filecounter).close();
     // a2_task2_train.elementAt(filecounter).close();
     	 }
          
//           int numOfattributes=trainingEx.get(0).getFeatureDimension();
//           FastVector structAttributes = new FastVector(numOfattributes+1);
//	       for (int i = 0 ; i < numOfattributes; i++) {
//	       structAttributes.addElement(new Attribute(Integer.toString(i) ));
//	        }        
//	        ///add here for loop with all classes
//	        // Add class attribute.
//	        final FastVector classValues = new FastVector(2);
//	        classValues.addElement("Yes");
//	        classValues.addElement("No");
//
//	        structAttributes.addElement(new Attribute("Class", classValues));
//	        Instances structuredData; 
//     	    Instances teststructuredData;
//     	    FeatureRepresentation objFeature = new FeatureRepresentation();	
//    	 // LabelRepresentation  objLabel=new LabelRepresentation();
//            System.out.println("prepareStructuredData started");
//          // BufferedReader bufReader;
//            String lineStr=""; //this is the string for a given line i.e. a given instance of the training example
//            structuredData= new Instances("Data1", structAttributes, 4000);
//            teststructuredData=new Instances("Data1", structAttributes, 4000);//allocates the dataset for a 1000 of instances
//            structuredData.setClassIndex(structuredData.numAttributes() - 1);
//            teststructuredData.setClassIndex(structuredData.numAttributes() - 1); //sets the class vector, I set it as the last dimension of my vector
//            int[] indices=new int[structuredData.numAttributes()]; ///indice vector, used later on for indexing
//            for (int i=0;i<indices.length;i++){
//              indices[i]=i;          
//            }
//                for (int i=0; i<trainingEx.size()/2; i++){
//                 double[] feats=trainingEx.get(i).doubleFeatureMatrix();//gets the feature vector as double array
//                    
//                 ///so we pass in parameters feature vector and indice vector
//                 SparseInstance instance= new  SparseInstance(1, feats,  indices, indices.length);
//                    // Add instance to training data.
//                structuredData.add(instance);//this is the whole dataset
//                if (!trainingEx.get(i).label.isEmpty())
//                  structuredData.instance(i).setClassValue(1);
//                else 
//                 structuredData.instance(i).setClassValue(0);
//                }  
//                
//                ///////
//                
//                for (int i=trainingEx.size()/2; i<trainingEx.size(); i++){
//                    double[] feats=trainingEx.get(i).doubleFeatureMatrix();//gets the feature vector as double array
//                       
//                    ///so we pass in parameters feature vector and indice vector
//                    SparseInstance instance= new  SparseInstance(1, feats,  indices, indices.length);
//                       // Add instance to training data.
//                   teststructuredData.add(instance);//this is the whole dataset
//                   if (!trainingEx.get(i).label.isEmpty())
//                     teststructuredData.instance(i-trainingEx.size()/2).setClassValue(1);
//                   else 
//                    teststructuredData.instance(i-trainingEx.size()/2).setClassValue(0);
//                   }  
//                //use it to set the label vector below for this particular instance
//                               
//               /* SMO smoClassifier = new SMO();
//     	        Classifier classifier; 
//     	       // String[] options = {"-M"};
//     	        PolyKernel polyK = new PolyKernel();
//     	        polyK.setUseLowerOrder(false);
//     	        polyK.setExponent(1.0);
//     	        smoClassifier.setKernel(polyK);
//     	       // smoClassifier.setOptions(options);
//     	        classifier=smoClassifier;
//     	        */
//     		  
//     		  MultilayerPerceptron objPerc= new MultilayerPerceptron();
//     		  objPerc.setHiddenLayers("0");
//     		
//     		  Classifier classifier=objPerc;
//     		  String[] options = {"-N", "100"};
//     	       classifier.setOptions(options);
//     	       int truepos=0;
//     	       classifier.buildClassifier(structuredData);
//     	       for(int ex=0;ex<teststructuredData.numInstances();ex++){
//     	    	 double tmpval=classifier.classifyInstance(teststructuredData.instance(ex));   
//     	    	 if (tmpval==teststructuredData.instance(ex).classValue())
//     	    		 truepos++;
//     	    		 
//     	       }
//               
//               
         System.out.print("finished!");
//                
	}//end main
public static String[] ReadStopWords() throws IOException{
	String[] S={""};
	BufferedReader in = new BufferedReader(new FileReader("./DataSets/StopWords.txt"));
	int i=0;
	while (in.ready()){
		S= Arrays.copyOf(S, S.length + 1);
		S[S.length-1]=in.readLine(); i++;
	  }
	return S;
 }	
public static boolean find_(String [] S,String x){ // This finds a string  among an array of string
	for(int i=0;i<S.length;i++){
		if (S[i].toLowerCase().equals(x.toLowerCase())){
			return true;
		}
	}
return false;
}

public static void writeStatistics() throws IOException{
	String strDirectoryout ="./DataSets/Statistics"; // The directory of the output database files
	(new File(strDirectoryout)).mkdirs();
	PrintWriter stat=new PrintWriter(new BufferedWriter (new FileWriter(strDirectoryout+"/Statistics.txt")));
	stat.println("Num Bacteria:\t"+statis.Bacteria_num);
	stat.println("Num Habitat:\t"+statis.Habitat_num);
	stat.println("Num Localization relations\t"+statis.Loc_cand);
	stat.println("Num negative loc relations:\t"+statis.Loc_neg);
	stat.println("Num of HB patterns:\t"+statis.HB);
	stat.close();
	}
	
}//end class

