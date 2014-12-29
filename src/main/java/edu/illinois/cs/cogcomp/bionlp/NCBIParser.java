package edu.illinois.cs.cogcomp.bionlp;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.regex.Pattern;


public class NCBIParser {

	
	private String filePath;
	
	
	public NCBIParser(String filePath) {
		
		this.filePath = filePath;
	}


	
	
	/* handles an NCBI file, an example entity is below
	 [Term]
	 id: MBTO:00001967
	 name: soybean
	 exact_synonym: "Glycine hispida" [TyDI:23279]
	 exact_synonym: "Glycine max" [TyDI:23280]
	 is_a: MBTO:00000071 ! Leguminosae
	 */
	 
	public NCBIRepresentation parseFile() throws IOException{
		NCBIRepresentation NCBIRepr=new NCBIRepresentation();
		NCBIElement obElem=new NCBIElement();// defines an NCBI element like the example above
		boolean existElement=false;
		int lineNumber=0;
		String[] lineElements;
		Pattern p1 = Pattern.compile(" ");
		Pattern p2 = Pattern.compile("\"");
		Pattern p3 = Pattern.compile("\t");
		Pattern p4 = Pattern.compile("\'");
		String currentNCBIID="";
		
		//training
		BufferedReader in = new BufferedReader(new FileReader(filePath));
		String str="";
		while ((str = in.readLine()) != null) { ///per line	
			lineNumber++;
			
			existElement=true;
			if (str.startsWith("\"")) {
				obElem=new NCBIElement();
				lineElements=p2.split(str);
				obElem.setName(lineElements[1].trim());
				lineElements=p3.split(str);
				obElem.setIdElement(lineElements[lineElements.length-2].trim());
				NCBIRepr.addNCBIElement(obElem);
			}
			
			else if (str.startsWith("\'")) {
				obElem=new NCBIElement();				
				lineElements=p4.split(str);
				obElem.setName(lineElements[1].trim());
				lineElements=p3.split(str);
				obElem.setIdElement(lineElements[lineElements.length-2].trim());
				NCBIRepr.addNCBIElement(obElem);
						
			 }
			else
			{ 
				obElem=new NCBIElement();
				lineElements=p3.split(str);
				obElem.setIdElement(lineElements[0].trim());
				obElem.setName(lineElements[lineElements.length-2].trim());
				NCBIRepr.addNCBIElement(obElem);
			}
	    }
		in.close();
		return NCBIRepr;
		
	}
	
}
