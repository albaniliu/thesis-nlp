=================================================
(C) Le Hong Phuong, phuonglh@gmail.com,
	2008-2009,
	Hanoi University of Science,  
	Vietnam National University, Hanoi, Vietnam
==================================================

vnTagger version 4.0.0, released 11/2009.
==================================================

I. GENERAL INFORMATION

	vnTagger is a highly accurate tagger for Vietnamese texts. Its 
	precision and recall ratios are about 96%. It uses as additional libraries 
	vnTokenizer (a tokenizer for Vietnamese texts) and Stanford Maxent Tagger.
	These libraries are provided in the jars file of the lib directory. 
	
	The tagset in use contains 17 main lexical tags:
		
		1.  Np - Proper noun
		2.  Nc - Classifier
		3.  Nu - Unit noun
		4.  N - Common noun
		5.  V - Verb
		6.  A - Adjective
		7.  P - Pronoun
		8.  R - Adverbe
		9.  L - Determiner
		10. M - Numeral
		11. E - Preposition
		12. C - Subordinating conjunction
		13. CC - Coordinating conjunction
		14. I - Interjection
		15. T - Auxiliary, modal words
		16. Y - Abbreviation
		17. Z - Bound morphemes
		18. X - Unknown
			
	There are also tags for delimiters and punctuations.
	
	
II. REQUIREMENT
 
	You need a JRE version 6.0 or above installed on your system. You may 
	download a JRE from the Java website of Sun Microsystems (http://java.sun.com/).
	
	
III. HOW TO RUN

	- Under Unix/Linux, use the provided script named "vnTagger.sh" 
		to run the program.
	
	- This program is a core tagger for Vietnamese texts, it has no graphical
		 user interface (GUI). You should provide two arguments for the program:
		 an input text file to be tagged (with argument option -i) and an output file for the 
		 program to write result to (with argument option -o).
		 For example:
		  
		  vnTagger.sh -i samples/sample0.txt  -o samples/sample0.tagged.xml
	 	
	 	Note that "sample0.txt" must exist and contain some Vietnamese text encoded in UTF-8
	 	encoding. The result file "sample0.tagged.xml" is a text file (A simple XML format) created by 
	 	the program and it is always encoded in UTF-8 encoding.
	 	
	 	The program supports the use of the underscore _ for separating syllables in words with option -u, 
	 	and use of plain text output instead of the default XML output with option -p.
	 	
	 	Thus, the command
	 	
	 		vnTagger.sh -i samples/sample0.txt  -o samples/sample0.tagged.xml -u
	 		
	 	will produce output with syllables separated by underscore characters.
	 	
	 	The command 
	 	
	 		vnTagger.sh -i samples/sample0.txt  -o samples/sample0.tagged.txt -u -p 
	 		
	 	will produce output with syllables separated by underscore characters and use a plain text
		output file instead of an XML file.
	 	
	- Under Microsoft Windows, use the provided script "vnTagger.bat" instead of "vnTagger.sh"  
		to run the program. All the options are similars as described above.

	- If you want to use a GUI version of the tagger, you should download vnToolkit, an 
		Eclipse Rich Client application. vnTagger, its GUI and other tools are bundled 
		as plug-ins of vnToolkit. NOTE: vnToolkit may not contain the lastest version of vnTagger.
		 

IV. HOW TO USE THE API
	
	The main class of the tagger is vn.hus.nlp.tagger.VietnameseMaxentTagger. This class provides
	three methods to tag text:
		
		+ public String tagText(String text)
		
			Tag a text and return a tagged string.
			
		+ public void tagFile(String inputFile, String outputFile, IOutputer outputer)
		
			Tag an input text file and write the result to an output file, using an outputer.
			
		+ public void tagFile(String inputFile, String outputFile)
		
			Tag an input text file and write the result to an output file, using a default plain outputer.
    			
V. LICENSE
  
  		See the LICENSE file.
  		
 VI. CONTACT
 
	The program and this short documentation for the tagger is written by Le Hong Phuong (phuonglh@gmail.com), 
	Any comments, questions and discussions will be appreciated.   
 	 