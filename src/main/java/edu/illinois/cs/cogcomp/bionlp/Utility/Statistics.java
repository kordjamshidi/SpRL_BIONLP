package edu.illinois.cs.cogcomp.bionlp.Utility;

public class Statistics {
  public int Bacteria_num=0;
  public int Habitat_num=0;
  public int Geographical_num=0;
  public int Bacteria_neg_num=0;
  public int Habitat_neg_num=0;
  public int Entity_neg_num;
  public int Loc_neg=0;
 // public int partOf_neg=0;
  public int Loc_cand=0;
  public int Loc_annotated=0;
  //public int PartOf_cand=0;
  //public int missed_Bacteria=0;
 // public int missed_Habitat=0;
  public int HB=0;
  int same_sen=0;
  int same_par=0;
  public int SentenceLocRelations=0;// The number of the localization relations in the sentence level
  public int discourseLocRelations=0;
  public int SentenceCorefLocRelations=0;
  public int testPositiveLoc=0;
  public int testNgativeLoc=0;
  public int Sp_candidate=0;
  public int EntityInObo=0;
  public int EntityInNCBI=0;
  public int EntityNotInObo=0;
  public int EntityNotInNCBI=0;
  public int EntityInNCBI_Fp=0;
  public int EntityInObo_Fp=0;
  public int missed_H_chunks=0;
  public int missed_B_chunks=0;
  public int missed_G_chunks=0;
  public int EntitiesNoOverlapAnyOnto=0;
  public int partOf_neg=0;
  public int negLoc_negRoles=0;
  public int nestedMentions=0;
  public int  discontinuousMentions=0;
  public int PartOf_annotated=0;
  public int PartOfEntityOverlap=0;

} 
