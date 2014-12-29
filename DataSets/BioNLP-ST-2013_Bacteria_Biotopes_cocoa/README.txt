Entity annotation by the Cocoa annotator for the BioNLP13 Shared Tasks.
----------------------------------------------------------------------

The entity annotations in the *cocoa files are made by the Cocoa annotator, which is also available as a WebAPI. The annotations provided are the BioNLP13 are "extended" annotations, where nested terms are also annotated (e.g., 'liver' in 'liver cancer'); please see:

http://npjoint.com/AboutCocoa.html

as well as

http://npjoint.com/CocoaAnnX.html

The annotations can be retrieved by a WebAPI call, such as:

curl -d "mode=minform&outputFormat=b1&code=utf&apikey=1234&text=A smorgasbord: Liver cancer, chromatophores and tigers." http://npjoint.com/Cocoa/api/

where the outputFormat parameter should be set to "b1" to get the same annotations as provided in the supporting resources for the Bionlp13 tasks. The value of the "text" parameter is the input text which is to be annotated (URI-encoded data). Details on the parameters for the WebAPI call are provided at:

http://npjoint.com/CocoaApi.html

There is exactly one ".ann" Cocoa-annotated file corresponding to each input file (.txt) in each task of the BioNLP13 dataset. The format of the ".ann" file is exactly the same as that of the "a1" files in the Shared Task dataset.


Tags provided by the Cocoa annotator
------------------------------------

The annotation tags provided by the annotator map more-or-less straightforwardly to the tags in the BioNLP13 tasks. One note is that Cocoa marks up certain entities with tag of "Molecule" when it cannot decide if the entity is a chemical or a protein. Practically, we have noticed that the tag "Molecule" maps >90% of the time to "Protein" (which is the "Gene_or_gene_product" tag in the CG task, as an example). Terms marked up as "Complex" by Cocoa (e.g. "Axin signaling complex") may also be considered as "Protein"s in some of the tasks. It may be relevant that there is a grey area in the Gene ontology classification where, for example, homomeric proteins are called "Gene products" whereas heteromeric proteins are marked as "Cellular component" but Cocoa does not (mostly) make these distinctions.

The fine-grained classification for anatomical entities is only moderately reliable, apart from the "Cell" subcategory, where performance is reasonable. However, the overall detection of anatomic entities without finer distinctions is very reasonable.

The annotation for "Habitat" is relatively recent and recall is likely to be low, also geographical and political entities are also marked up as "Habitat" for now.

Contact:

S. V. Ramanan:	ramanan@npjoint.com

for more information or clarification.

