package edu.illinois.cs.cogcomp.bionlp;/*This is the last program that adds the relations to the examples and produces the inputs of Nfoil to classify each example as sr-non-sr*/
//package GeneralData_preprocess;

//import GeneralData_preprocess.PrintParameters;
//import SemEval2013.FileFilter;
//import Utility.Statistics;

import edu.illinois.cs.cogcomp.bionlp.Utility.Statistics;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Vector;

//import java.io.FilenameFilter;
//import java.util.ArrayList;
//import java.util.HashSet;
//import java.util.Set;
//import edu.ucdenver.ccp.nlp.biolemmatizer.BioLemmatizer;
//import GeneralData_preprocess.PrintParameters;
//import RLpackage.TreeNode;
//import GeneralData_preprocess.sentence.ling_features;

public class FEx_BIONLP_BB_PerSentence_train_test_MoreGlobalBMC {
static Statistics statis=new Statistics();
static String[] StopWords;
static NCBIRepresentation NCBIRep=new NCBIRepresentation();
static OBORepresentation obRep=new OBORepresentation();
public static int HeadW_with_index(Integer[] wordindexes,Vector<ling_features> words){
		
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
	
	
	///////////////////////////////////////////////////////
	public static String SRL(int index, Vector<Vector <String>> M)
	{
		for(int i=10;i<M.elementAt(index).size();i++)
		  {
			if(!M.elementAt(index).elementAt(i).equals("_"))
			return(M.elementAt(index).elementAt(i));
			}
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
	//private static BioLemmatizer bioLemmatizer;
	
	
	public static void main(String[] args) throws Exception{
		
		Vector <Vector<String>> arffAttRel =new Vector<Vector<String>>();
	    arffAttRel.add(0,new Vector<String>());
	    arffAttRel.add(1,new Vector<String>());
	    arffAttRel.add(2,new Vector<String>());
	    arffAttRel.add(3,new Vector<String>());
	    arffAttRel.add(4,new Vector<String>());
	    arffAttRel.add(5,new Vector<String>());// this is for the features of a compound not a word only
	      
	    String[] strDirOut={"./DataSets/TablesTrain","./DataSets/TablesTest"};
	    String[] strDirIn={"./DataSets/BioNLP-ST-2013_Bacteria_Biotopes_train_dev","./DataSets/BioNLP-ST-2013_Bacteria_Biotopes_test"};
	    String[] strTask={"task_1","task_3"};
	    String[] strTr_Ts={"train","test"};
	    String[] strCocoa={"./DataSets/BioNLP-ST-2013_Bacteria_Biotopes_cocoa/","./DataSets/BioNLP-ST-2013_Bacteria_Biotopes_test/BioNLP-2013_Bacteria_Biotopes_test_cocoa/task_3/"};
	    String strDirectoryout ="./DataSets/Statistics"; // The directory of the output database files
	    BufferedReader brt2a1 = null;
		BufferedReader brt2a2=null;
		BufferedReader brTxt=null;
    	(new File(strDirectoryout)).mkdirs();
    	PrintWriter MatchInfo=new PrintWriter(new BufferedWriter (new FileWriter(strDirectoryout+"/Matching.txt")));
    	PrintWriter misedChunker_Entity=new PrintWriter(new BufferedWriter(new FileWriter(strDirectoryout+"/MissingEntitiesByChunker")));
    	//PrintWriter chunkerTrainingFile=new PrintWriter(new BufferedWriter(new FileWriter(strDirectoryout+"/chunkerTrain.txt")));
	    for (int TT=0;TT<=1;TT++){
	    PrintParameters parameters=new PrintParameters();
		parameters.Globality=0;	
	    (new File(strDirOut[TT])).mkdirs();
	    (new File(strDirOut[TT]+"/entitySpans")).mkdirs(); 
	    (new File(strDirOut[TT]+"/roleFeatures")).mkdirs(); 
	    (new File(strDirOut[TT]+"/TrLabels")).mkdirs(); 
	    (new File(strDirOut[TT]+"/LmLabels")).mkdirs(); 
	    (new File(strDirOut[TT]+"/SpLabels")).mkdirs(); 
	    (new File(strDirOut[TT]+"/pairFeatures")).mkdirs(); 
	    (new File(strDirOut[TT]+"/SpTrLabels")).mkdirs(); 
	    (new File(strDirOut[TT]+"/SpLmLabels")).mkdirs(); 
	    (new File(strDirOut[TT]+"/TrLmLabels")).mkdirs();
	    (new File(strDirOut[TT]+"/Loc_LocLabels")).mkdirs();
	    (new File(strDirOut[TT]+"/R_RFeatures")).mkdirs();
	    
	   // (new File(strDirOut[TT]+"/relations")).mkdirs(); 
	   // (new File(strDirOut[TT]+"/entityLabels")).mkdirs();
	    (new File(strDirOut[TT]+"/outputPredicates")).mkdirs();
	    //(new File(strDirOut[TT]+"/inputSparseRel")).mkdirs();
	    (new File(strDirOut[TT]+"/inputPredicates")).mkdirs();         
	    //(new File(strDirOut[TT]+"/relationLabels")).mkdirs();
	    (new File(strDirOut[TT]+"/Coreferences")).mkdirs();
	    (new File(strDirOut[TT]+"/CorefFeatures")).mkdirs();
	    (new File(strDirOut[TT]+"/EntitySimilarity")).mkdirs();
	    
	    Vector <PrintWriter> roleFeatures=new Vector <PrintWriter>();
   	    Vector <PrintWriter> SpLabels=new Vector<PrintWriter>();
   	    Vector <PrintWriter> TrLabels=new Vector<PrintWriter>();
   	    Vector <PrintWriter> LmLabels=new Vector<PrintWriter>();
   	    
   		Vector <PrintWriter> pairFeatures=new Vector <PrintWriter>();

		Vector <PrintWriter> relationLabelsTr_Sp=new Vector <PrintWriter>();
		Vector <PrintWriter> relationLabelsLm_Sp=new Vector <PrintWriter>();
		Vector <PrintWriter> relationLabelsTr_Lm=new Vector <PrintWriter>();
		
		Vector <PrintWriter> R_RFeatures=new Vector<PrintWriter>();
		Vector <PrintWriter> Loc_LocLabels=new Vector<PrintWriter>();

   	
   	// Vector <PrintWriter> relationSparsefile=new Vector<PrintWriter>();
   	 //Vector <PrintWriter> relationfileinputs=new Vector<PrintWriter>();
   	// Vector <PrintWriter> relationLabels=new Vector<PrintWriter>();
        Vector <PrintWriter> Coreferences=new Vector<PrintWriter>();
   	    Vector <PrintWriter> CorefFeatures=new Vector<PrintWriter>();
   	    Vector <PrintWriter> Esimilarity=new Vector<PrintWriter>();
   	 
   	
    	Vector <PrintWriter> inputpredicates=new Vector <PrintWriter>();
        Vector <PrintWriter> outputpredicates=new Vector <PrintWriter>();
        
        Vector <PrintWriter> entitySpans=new Vector<PrintWriter>();
        
	    String strDirectoryin =strDirIn[TT]+"/"+strTask[TT]+"/";//"./DataSets/BioNLP-ST-2013_Bacteria_Biotopes_train_dev/task_1/"; // The directory of the output database files
		(new File(strDirectoryin)).mkdirs();
		
		File f = new File(strDirIn[TT]+"/BioNLP-ST-2013_Bacteria_Biotopes_"+strTr_Ts[TT]+"_mcccj/"+strTask[TT]+"/");
    	File fsource=new File(strDirIn[TT]+"/"+strTask[TT]+"/");
		File splitsource=new File(strDirIn[TT]+"/"+"BioNLP-ST-2013_Bacteria_Biotopes_"+strTr_Ts[TT]+"ssplit/"+strTask[TT]+"/");
    	//File t1a2=new File("./DataSets/BioNLP-ST-2013_Bacteria_Biotopes_train_dev/task_1/");
    	
		File Cocoa=new File(strCocoa[TT]); 
    	
    	File t2a1=new File("./DataSets/BioNLP-ST-2013_Bacteria_Biotopes_train_dev/task_2/");
    	File t2a2=new File("./DataSets/BioNLP-ST-2013_Bacteria_Biotopes_train_dev/task_2/");
    	

		FileFilter conllFilter = new FileFilter(".connlx2","");
		//FileFilter cocoAFilter = new FileFilter(".ann","");
		//FileFilter ptbFilter = new FileFilter(".ptb","");
		//FileFilter sdepFilter = new FileFilter(".sdep","");
		FileFilter txtFilter    = new FileFilter(".txt","");
		//FileFilter ssFilter = new FileFilter(".ss",""); 
        // Vector <PrintWriter> examplefileoutputs=new Vector <PrintWriter>(); 
    	//File OntoBiotope=new File("/DataSets/OntoBiotope_BioNLP-ST13.obo");
		//FileFilter a2Filter = new FileFilter(".a2","");
		//FileFilter a1Filter = new FileFilter(".a1","");
       
		File[] conllfiles = f.listFiles(conllFilter);
		//File[] ptbfiles = f.listFiles(ptbFilter);
		//File[] sdepfiles = f.listFiles(sdepFilter);
		File[] txtfiles = fsource.listFiles(txtFilter);
		//File[] ssfiles= splitsource.listFiles(ssFilter);
		//File[] t1a2files= t1a2.listFiles(a2Filter);
		//File[] CocoaFiles= Cocoa.listFiles(cocoAFilter);
		//File[] t2a1files=t2a1.listFiles(a1Filter);
		//File[] t2a2files=t2a2.listFiles(a2Filter);
	    int sentence_num=0;
 
      //  ArrayList <BIOdiscourse> training=new ArrayList<BIOdiscourse>();
        StopWords=ReadStopWords();
      NCBIParser objParser=new NCBIParser("./DataSets/taxonomie_ncbi.txt");
  	  try {
  		    NCBIRep= objParser.parseFile();
    	} catch (IOException e) {
  		// TODO Auto-generated catch block
  		e.printStackTrace();
  	    }
    	OBOParser oboParser=new OBOParser("./DataSets/OntoBiotope_BioNLP-ST13.obo");
    	try {
    		obRep= oboParser.parseFile();
    		System.out.println("number of elements" + obRep.getNumberOfObo());
    		ArrayList<String>  objExactSyns=  obRep.getExactAllSynonyms();
    		ArrayList<String>  objRelatedSyns=  obRep.getRelatedAllSynonyms();
    		ArrayList<String>  objNames=  obRep.getAllNames();
    		
    		
    	} catch (IOException e) {
    		// TODO Auto-generated catch block
    		e.printStackTrace();
    	}
    	
    	
       for (int i=0;i<txtfiles.length ;i++)
       {
    	    BIOdiscourse discourse1=new BIOdiscourse();
    	    
    	    String file_name=conllfiles[i].getName().substring(0,conllfiles[i].getName().indexOf("."));
         	discourse1.name=file_name;
    	    BufferedReader brconnl = new BufferedReader(new FileReader(f.getPath()+"/"+file_name+".connlx2"));
    		BufferedReader brptb = new BufferedReader(new FileReader(f.getPath()+"/"+file_name+".ptb"));
    		BufferedReader brssplit= new BufferedReader(new FileReader(splitsource.getPath()+"/"+file_name+".ss"));
    		//BufferedReader brt1a2=new BufferedReader(new FileReader(t1a2.getPath()+"/"+file_name+".a2"));
    		BufferedReader brCocoa=new BufferedReader(new FileReader(Cocoa.getPath()+"/"+file_name+".ann"));
    		if (TT==0){
    		  brt2a1=new BufferedReader(new FileReader(t2a1.getPath()+"/"+file_name+".a1"));
    		  brt2a2=new BufferedReader(new FileReader(t2a2.getPath()+"/"+file_name+".a2"));
    		  }
    		  brTxt=new BufferedReader(new FileReader(strDirIn[TT]+"/"+strTask[TT]+"/"+file_name+".txt"));
    		//BufferedReader bioOnto = new BufferedReader(new FileReader(OntoBiotope));
    		if (file_name.contains("30002"))
   			System.out.print(file_name);
    		PrintWriter discoursefile=new PrintWriter(new BufferedWriter (new FileWriter(strDirectoryin+file_name+".xml")));
    		sentence_num++;
    		
    		discourse1.fetch_ling_features(brconnl,brptb,brssplit, brCocoa);
    		discourse1.chunkBIO();
    		discourse1.paragraph_info(brTxt);
    		
    		discourse1.fetch_t2a1a2_annotationsAndCand(brt2a1,brt2a2);
    	//	discourse1.writeChunckerTraining(chunkerTrainingFile);
    		
    		discourse1.build_all_chunk_candidatesLmNCBI_Obo();
    		discourse1.AddNegative_Relations();
    		//discourse1.build_all_candidatesLmOBO();
    		
        	//	discourse1.fetch_t1a1_annotations(brt1a2);
    		//discourse1.check_OBO(obRep,NCBIRep,MatchInfo);
    		discourse1.chunker_missEntities(misedChunker_Entity);
    		
    		//discourse1.addNegRelCand();
    		discourse1.candidate_features(arffAttRel);
    		//discourse1.candidate_LocLoc();
    	//	discourse1.EntitySimilarity();
    	  
    		//  discourse1.collect_featureLexicon(arffAttRel.elementAt(0));
           // discourse1.collect_RelCandidatesForSOP(arffAttRel);
            //discourse1.writeDiscourseXML(discoursefile, sentence_num,0,1);
    		 if (i==15) 
    			System.out.print("Stop for check!");
             roleFeatures.addElement(new PrintWriter(new BufferedWriter(new FileWriter(strDirOut[TT]+"/roleFeatures/"+file_name+".data"))));
    		 SpLabels.addElement(new PrintWriter(new BufferedWriter(new FileWriter(strDirOut[TT]+"/SpLabels/"+file_name+".data"))));
    		 TrLabels.addElement(new PrintWriter(new BufferedWriter(new FileWriter(strDirOut[TT]+"/TrLabels/"+file_name+".data"))));
    		 LmLabels.addElement(new PrintWriter(new BufferedWriter(new FileWriter(strDirOut[TT]+"/LmLabels/"+file_name+".data"))));
             
    		 // examplefileoutputs.addElement(new PrintWriter(new BufferedWriter(new FileWriter(strDirectoryout+"/outputs/"+file_name+(i+1)+".data"))));
             inputpredicates.addElement(new PrintWriter(new BufferedWriter(new FileWriter(strDirOut[TT]+"/inputPredicates/"+file_name+".data"))));
             outputpredicates.addElement(new PrintWriter(new BufferedWriter(new FileWriter(strDirOut[TT]+"/outputPredicates/"+file_name+".data"))));
             entitySpans.addElement(new PrintWriter(new BufferedWriter(new FileWriter(strDirOut[TT]+"/entitySpans/"+file_name+".data"))));
             pairFeatures.addElement(new PrintWriter(new BufferedWriter(new FileWriter(strDirOut[TT]+"/pairFeatures/"+file_name+".data"))));
    		 relationLabelsTr_Sp.addElement(new PrintWriter(new BufferedWriter(new FileWriter(strDirOut[TT]+"/SpTrLabels/"+file_name+".data"))));
             relationLabelsLm_Sp.addElement(new PrintWriter(new BufferedWriter(new FileWriter(strDirOut[TT]+"/SpLmLabels/"+file_name+".data"))));
             relationLabelsTr_Lm.addElement(new PrintWriter(new BufferedWriter(new FileWriter(strDirOut[TT]+"/TrLmLabels/"+file_name+".data"))));
             R_RFeatures.addElement(new PrintWriter(new BufferedWriter(new FileWriter(strDirOut[TT]+"/R_RFeatures/"+file_name+".data"))));
             Loc_LocLabels.addElement(new PrintWriter(new BufferedWriter(new FileWriter(strDirOut[TT]+"/Loc_LocLabels/"+file_name+".data"))));
             Coreferences.addElement(new PrintWriter(new BufferedWriter(new FileWriter(strDirOut[TT]+"/Coreferences/"+file_name+".data"))));
             CorefFeatures.addElement(new PrintWriter(new BufferedWriter(new FileWriter(strDirOut[TT]+"/CorefFeatures/"+file_name+".data"))));
             Esimilarity.addElement(new PrintWriter(new BufferedWriter(new FileWriter(strDirOut[TT]+"/EntitySimilarity/"+file_name+".data"))));
             
             parameters.roleFeatures=roleFeatures.lastElement();
             parameters.SpLabels=SpLabels.lastElement();
             parameters.TrLabels=TrLabels.lastElement();
             parameters.LmLabels=LmLabels.lastElement();
             parameters.entitySpans=entitySpans.lastElement();
             
             parameters.pairFeatures=pairFeatures.lastElement();
             parameters.relationLabelsTr_Sp=relationLabelsTr_Sp.lastElement();
             parameters.relationLabelsLm_Sp=relationLabelsLm_Sp.lastElement();
             parameters.relationLabelsTr_Lm=relationLabelsTr_Lm.lastElement();
             
             parameters.R_RFeatures=R_RFeatures.lastElement();
             parameters.Loc_LocLabels=Loc_LocLabels.lastElement();
             
            // parameters.relationLabels=relationLabels.lastElement();
            // parameters.relationKeyfile=relationSparsefile.lastElement();
             parameters.inputpredicates=inputpredicates.lastElement();
             parameters.outputpredicates=outputpredicates.lastElement();
             parameters.CorefFeatures=CorefFeatures.lastElement();
             
             //%%%%%%%%%%%%%%%%%%%%%%
             discourse1.writeRelationalFeatureSONew(arffAttRel, parameters);
             discourse1.writeCoreferences(Coreferences.lastElement());
             discourse1.writeEntitySimilarity(Esimilarity.lastElement());
             //training.add(discourse1);
             discoursefile.close();
           
         	 roleFeatures.lastElement().close();
         	 //examplefileoutputs.elementAt(filecounter).close();
         	  
         	 SpLabels.lastElement().close();
         	 TrLabels.lastElement().close();
         	 LmLabels.lastElement().close();
         	 
         	 entitySpans.lastElement().close();
         	 pairFeatures.lastElement().close();
         	 
         	 relationLabelsTr_Sp.lastElement().close();
         	 relationLabelsLm_Sp.lastElement().close();
         	 relationLabelsTr_Lm.lastElement().close();
         	 
         	 Loc_LocLabels.lastElement().close();
         	 R_RFeatures.lastElement().close();
         	
         	 inputpredicates.lastElement().close();
        	 outputpredicates.lastElement().close();
        	
         	 //relationfileinputs.elementAt(filecounter).close();
         	 //relationSparsefile.elementAt(filecounter).close();
         	 //entityLabels.elementAt(filecounter).close();
         	 //relationLabels.elementAt(filecounter).close();
         	 Coreferences.lastElement().close();
         	 CorefFeatures.lastElement().close();
         	 Esimilarity.lastElement().close();
         }
	    }
          make_feature_header(arffAttRel);
          writeStatistics();
          MatchInfo.close();
          misedChunker_Entity.close();
        //  chunkerTrainingFile.close();
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
	stat.println("Num negative entity candidates:\t"+statis.Entity_neg_num);
	stat.println("Num Sp candidates:\t"+statis.Sp_candidate);

	stat.println("Num Localization relations annotated\t"+statis.Loc_annotated);
	stat.println("Num negative loc relations using positive roles:\t"+statis.Loc_neg);
	stat.println("Num of HB patterns:\t"+statis.HB);
	stat.println("Num of annotated sentence level localization relations:\t"+statis.SentenceLocRelations);
	stat.println("Num of annotated discourse level localization relations:\t"+statis.discourseLocRelations);
	stat.println("Num of localazation without annotation but via coref:\t"+statis.Loc_cand);
	stat.println("Num of localazation in the same sentence not directly annotated but via coref:\t"+statis.SentenceCorefLocRelations);
	stat.println("Test Num of localazation examples in the same sentence:\t"+statis.testPositiveLoc);
	stat.println("Test Num of non-localazation examples in the same sentence:\t"+statis.testNgativeLoc);
	stat.println("Num of Bacteria with exact match in NCBI:\t"+statis.EntityInNCBI);
	stat.println("Num of Bacteria does not exist in NCBI:\t"+statis.EntityNotInNCBI);
	stat.println("Num of Habitat with exact match in obo (names/synonyms/related words):\t"+statis.EntityInObo);
	stat.println("Num of Habitat do not exist in obo (names/synonyms/related words):\t"+statis.EntityNotInObo);
	stat.println("Num of no-rol exist in obo (names/synonyms/related words):\t"+statis.EntityInObo_Fp);
	stat.println("Num of no-rol exist in NCBI:\t"+statis.EntityInNCBI_Fp);
	stat.println("Num of misssed bacterium chunks:\t"+ statis.missed_B_chunks);
	stat.println("Num of misssed habitat chunks:\t"+ statis.missed_H_chunks);
	stat.println("Num of misssed geograqphical chunks:\t"+ statis.missed_G_chunks);
	stat.println("Num of candidates in the list of entities which do not have overlap with obo and NCBI:\t"+statis.EntitiesNoOverlapAnyOnto);
	stat.println("Num of candidate relations composed of negative roles:\t"+statis.negLoc_negRoles);
	stat.println("Num of nested mentions:\t"+statis.nestedMentions);
	stat.println("Num of discountinous mentions:\t"+statis.discontinuousMentions);
	stat.println("Num of part of relations:\t"+statis.PartOf_annotated);
	stat.println("Num of part of relations which are overlapping entities:\t"+statis.PartOfEntityOverlap);
	
	stat.close();
	}
	
}//end class

