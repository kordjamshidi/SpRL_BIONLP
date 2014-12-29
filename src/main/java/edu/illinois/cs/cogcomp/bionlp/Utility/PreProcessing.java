package edu.illinois.cs.cogcomp.bionlp.Utility;

import java.util.ArrayList;
import java.util.Collections;

//import com.sun.tools.javac.util.List;

public class PreProcessing {
	public static String BacteriaToken(String s){
		s=s.toLowerCase().trim();
		s = s.replaceAll( "\\p{Punct}$", "" );
        s = s.replaceAll( "^\\p{Punct}", "" );
        return(s);
	}
	public static String[] BacteriafullName(String[] s){
		String[] commonList={"str.","str","spp.","spp","strain","sp.","sp","subsp"};
		for (int i=0;i<s.length;i++)
	     { 
			if (Util.contains_str(commonList,s[i]))
			 {
	           ArrayList<String> list =  new ArrayList<String>();
	           Collections.addAll(list, s); 
	           list.remove(i);
	           s = list.toArray(new String[list.size()]);
	          }
	    }
		return s;
	  }
}
