package edu.illinois.cs.cogcomp.bionlp;

/*
 [Term]
id: MBTO:00001257
name: marine environment
related_synonym: "marine" [TyDI:24359]
related_synonym: "sea" [TyDI:24909]
related_synonym: "ocean" [TyDI:24998]
related_synonym: "marine area" [TyDI:24361]
related_synonym: "oceanic" [TyDI:24999]
is_a: MBTO:00000643 ! aquatic environment
xref: ENVO:00000016 ! sea
 */


public class NCBIElement {

	
	private String idElement;
	private String name;
	//private ArrayList<String> exact_synonym=new ArrayList<String>();
	//private ArrayList<String> related_synonym=new ArrayList<String>();
	//private ArrayList<String> is_a=new ArrayList<String>();
	//private ArrayList<String> xref=new ArrayList<String>();
	
	public NCBIElement(){
		
	}
	
	public NCBIElement(String idElement, String name) {
		this.idElement = idElement;
		this.name = name;	
	}
	
	
	
	public String getIdElement() {
		return idElement;
	}

	public void setIdElement(String idElement) {
		this.idElement = idElement;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	
//	
//	public void addRelatedSynonym(String synID){
//		related_synonym.add(synID);
//	}
//	
//	public void addISA(String is_a_ID){
//		is_a.add(is_a_ID);
//	}
//	public void addXREF(String xrefID){
//		xref.add(xrefID);
//	}
//
//	public ArrayList<String> getExact_synonym() {
//		return exact_synonym;
//	}
//
//
//	public ArrayList<String> getIs_a() {
//		return is_a;
//	}
//
//
//	public ArrayList<String> getRelated_synonym() {
//		return related_synonym;
//	}
//
//	public ArrayList<String> getXref() {
//		return xref;
//	}

}

