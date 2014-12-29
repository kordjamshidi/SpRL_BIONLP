package edu.illinois.cs.cogcomp.bionlp;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.regex.Pattern;


public class OBOParser {

	
	private String filePath;
	
	
	public OBOParser(String filePath) {
		
		this.filePath = filePath;
	}


	
	
	/* handles an obo file, an example entity is below
	 [Term]
	 id: MBTO:00001967
	 name: soybean
	 exact_synonym: "Glycine hispida" [TyDI:23279]
	 exact_synonym: "Glycine max" [TyDI:23280]
	 is_a: MBTO:00000071 ! Leguminosae
	 */
	 
	public OBORepresentation parseFile() throws IOException{
		OBORepresentation oboRepr=new OBORepresentation();
		OBOElement obElem=new OBOElement();;// defines an obo element like the example above
		boolean existElement=false;
		int lineNumber=0;
		String[] lineElements;
		Pattern p1 = Pattern.compile(" ");
		Pattern p2 = Pattern.compile(":");
		String currentOBOID="";
		
		//training
		BufferedReader in = new BufferedReader(new FileReader(filePath));
		String str="";
		while ((str = in.readLine()) != null) { ///per line	
			lineNumber++;
			if (lineNumber<10) continue;
			
			
			if (str.equals("[Term]")) {
				obElem=new OBOElement();
				existElement=true;
			}
			else if (str.trim().equals("")) {
				if (existElement) oboRepr.addOBOElement(currentOBOID, obElem);
				continue;
			}
			else{
								
					lineElements=p1.split(str);
		
					//id: MBTO:00001967
					if (lineElements[0].contains("id:")) {
						
						//MBTO:00001967
						String[] tmpStr=p2.split(lineElements[1]);
						obElem.setIdOBOElement(tmpStr[1].trim());
						currentOBOID=tmpStr[1].trim();
						
					}
					
					else if (lineElements[0].contains("name:")) {
						//name: soybean
						String name="";
						for (int i=1;i<lineElements.length;i++)
						   name=name+" "+lineElements[i];
						obElem.setName(name.trim());		
					     }
					else if (lineElements[0].contains("exact_synonym")) {
						//exact_synonym: "Glycine hispida" [TyDI:23279]
						//int lastElem=lineElements.length-1;
						//String tmpStr=lineElements[lastElem];
						//[TyDI:23279]
						//String tyDI=tmpStr.substring(6,tmpStr.length()-1);
						int startIDx,endIDx;
						startIDx=str.indexOf("\"");
						endIDx=str.lastIndexOf("\"");
						String exactSyn=str.substring(startIDx+1, endIDx);
						
						obElem.addExactSynonym(exactSyn);;	
					}
					else if (lineElements[0].contains("related_synonym")) {
						//related_synonym: "marine" [TyDI:24359]
						int startIDx,endIDx;
						startIDx=str.indexOf("\"");
						endIDx=str.lastIndexOf("\"");
						String relatedSyn=str.substring(startIDx+1, endIDx);
						obElem.addRelatedSynonym(relatedSyn);	
					}		
					else if (lineElements[0].contains("is_a:")) {
						//is_a: MBTO:00000643 ! aquatic environment
						String[] tmpStr=p2.split(lineElements[1]);
						String mbto=tmpStr[1];
						obElem.addISA(mbto);	
					}		
					else if (lineElements[0].contains("xref:")) {
						//xref: ENVO:00000016 ! sea
						String[] tmpStr=p2.split(lineElements[1]);
						String envo=tmpStr[1];
						obElem.addXREF(envo);	
					}	
					
			
			}
			
			
			
				    	 				    
		}
		in.close();
		return oboRepr;
		
	}
	
}
