package edu.illinois.cs.cogcomp.bionlp;


import edu.illinois.cs.cogcomp.bionlp.Utility.PreProcessing;
import edu.illinois.cs.cogcomp.bionlp.Utility.StringSimilarity;
import edu.illinois.cs.cogcomp.bionlp.Utility.Util;

import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Vector;


public class PrintParameters {
    public PrintWriter roleFeatures;
    public PrintWriter pairFeatures;

//public PrintWriter entityfileinputs;
//public PrintWriter entityLabels;

//public PrintWriter relationfileinputs;
//public PrintWriter relationKeyfile;

    public PrintWriter relations;
    public PrintWriter ontologyLables;

    public PrintWriter inputpredicates;
    public PrintWriter outputpredicates;

    public PrintWriter CorefFeatures;
    public int Globality=0; // 0 means discourse layer | 1 means sentence layer
    //public PrintWriter SpCandidatefile;
    public PrintWriter SpLabels;
    public PrintWriter TrLabels;
    public PrintWriter LmLabels;
//public PrintWriter relationKeyfileTr_Sp;
//public PrintWriter relationFeaturesTr_Sp;

    public PrintWriter relationLabelsTr_Sp;
    public PrintWriter relationLabelsLm_Sp;
    //public PrintWriter relationKeyfileLm_Sp;
//public PrintWriter relationFeaturesLm_Sp;
    public PrintWriter relationLabelsTr_Lm;
    public PrintWriter entitySpans;
    public PrintWriter Loc_LocLabels;
    public PrintWriter R_RFeatures;



}
