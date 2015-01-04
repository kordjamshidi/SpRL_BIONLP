package edu.illinois.cs.cogcomp.bionlp.Utility;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class StringSimilarity {

	
	public static double editDistance(String s, String t){
	    int m=s.length();
	    int n=t.length();
	    int[][]d=new int[m+1][n+1];
	    for(int i=0;i<=m;i++){
	      d[i][0]=i;
	    }
	    for(int j=0;j<=n;j++){
	      d[0][j]=j;
	    }
	    for(int j=1;j<=n;j++){
	      for(int i=1;i<=m;i++){
	        if(s.charAt(i-1)==t.charAt(j-1)){
	          d[i][j]=d[i-1][j-1];
	        }
	        else{
	          d[i][j]=min((d[i-1][j]+1),(d[i][j-1]+1),(d[i-1][j-1]+1));
	        }
	      }
	    }
        int l=Math.max(1,Math.max(m,n));
	    return(((double) d[m][n])/l);
	  }
	  public static int min(int a,int b,int c){
	    return(Math.min(Math.min(a,b),c));
	  }


/**
 * Calculates Jaccard coefficient for two sets of items. 
 * 
 */
 public static double Jaccard(String[] x, String[] y) {
        double sim=0.0d;
        if ( (x!=null && y!=null) && (x.length>0 || y.length>0)) {
                        sim = similarity(Arrays.asList(x), Arrays.asList(y)); 
        } else {
                throw new IllegalArgumentException("The arguments x and y must be not NULL and either x or y must be non-empty.");
        }
        return sim;
    }
    
    private static double similarity(List<String> x, List<String> y) {
        
        if( x.size() == 0 || y.size() == 0 ) {
            return 0.0;
        }
        
        Set<String> unionXY = new HashSet<String>(x);
        unionXY.addAll(y);
        
        Set<String> intersectionXY = new HashSet<String>(x);
        intersectionXY.retainAll(y);

        return (double) intersectionXY.size() / (double) unionXY.size(); 
    }
    
}
