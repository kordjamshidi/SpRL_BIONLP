package edu.illinois.cs.cogcomp.bionlp;

import java.io.File;
import java.io.FilenameFilter;

public class FileFilter implements FilenameFilter{

private String fileExtension;
private String nameSub;
	public FileFilter (String fileExtension,String sub){
		this.fileExtension=fileExtension;
		this.nameSub=sub;
	}
	
	public boolean accept(File directory, String fileName){
		return (fileName.endsWith(this.fileExtension)&& fileName.contains(this.nameSub));
	}
} 
