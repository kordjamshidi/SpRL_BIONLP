package edu.illinois.cs.cogcomp.bionlp;//package GeneralData_preprocess;

//import java.util.Vector;

  public class offsets{
	    	int B=0;
	    	int E=0;
	    	public void set(int b,int e){
	    		
	    		B=b;
	    		E=e;
	    	}
	public boolean contains(offsets x){
		boolean flag=false;
		if (B<=x.B && E>=x.E)
			flag=true;
		return flag;
	}
	public boolean equals(offsets x)
	{
	boolean flag=false;
	if (B==x.B && E==x.E)
		flag=true;
	return flag;
	}
}
