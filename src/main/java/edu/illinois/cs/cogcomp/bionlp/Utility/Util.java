package edu.illinois.cs.cogcomp.bionlp.Utility;

import java.util.Arrays;

public class Util {
public static boolean contains_int(int[] A, int x){
	for (int i=0;i< A.length;i++)
		if (A[i]==x)
			return true;
	return false;
}
public static boolean contains_int_int(int[][] A,int x){
	for (int i=0;i< A.length;i++)
		
		if (contains_int(A[i],x))
			return true;
	return false;
}
public static boolean contains_str(String [] S,String x){ // This findes a string  among an array of string
	for(int i=0;i<S.length;i++){
		if (S[i].toLowerCase().equals(x.toLowerCase())){
			return true;
		}
	}
return false;
}

public static boolean d2Int_d2_Int(Integer[][] sen, Integer[] wordIndexes) {
	// TODO Auto-generated method stub
	if (sen!=null) 
	for (int i=0;i<sen.length;i++)
	{
		if (Arrays.deepEquals(sen[i],wordIndexes))
				return true;
		
	}
	return false;
 }
public static boolean contains_int(Integer[] A, int x) {
	for (int i=0;i< A.length;i++)
	if (A[i]==x)
		return true;
return false;
}
public static boolean overlap_tok_index(Integer a[],Integer b[]){
	for (int i=0;i<a.length;i++)
		for (int j=0;j<b.length;j++)
			if (a[i]==b[j])
				return true;
 return false;	
}
}
