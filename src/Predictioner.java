import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Scanner;


public class Predictioner {
	public static void main(String[] args) {
		File rnaSeqFile = new File("src/ecoli16sRNA_J01695");
		//File rnaSecStructFile = new File("src/ecoli16sRNA_J01695_bracket");
		File rnaSecStructFile = new File("src/d.16.b.A.globiformis");
		//File rnaSecStructFile = new File("src/d.16.b.B.subtilis");
		Scanner s = null;
		Scanner sbracket = null;
		try{
			String rnaSeq = "";
			String bracketStr = "";
			s = new Scanner(rnaSeqFile);
			sbracket = new Scanner(rnaSecStructFile);
			if(s.hasNextLine()){
				rnaSeq = s.nextLine();
				while(sbracket.hasNextLine()){
					bracketStr += sbracket.nextLine();
				}
				//bracketStr = bracketStr.substring(366, 393);
				//bracketStr = bracketStr.substring(404, 435);
				//bracketStr = bracketStr.substring(135, 223);
				bracketStr = "(((.(((......)))...(((....)))...(((......))).)))..";
				bracketStr = "(((.(((......)))...(((....))).(((.((..))...))))))..";
				//store substrings of a sec struct, each substring contains at most 1 stem loop
				LinkedList<RNAPalindrome> subSecStruct = new LinkedList<RNAPalindrome>();
				LinkedList<RNAPalindrome> stemLoops = new LinkedList<RNAPalindrome>();
				
				//break down secondary structure 
				int k = 0; 
				int lastEnd = 0;
				boolean rparenFound = false;
				while(k < bracketStr.length()){
					char currChar = bracketStr.charAt(k);
					if(rparenFound){
						if((currChar == '.'&& lastEnd < bracketStr.length() - 1) || 
								currChar == ')' && k == bracketStr.length() - 1){
							RNAPalindrome sub = new RNAPalindrome(lastEnd+1, k, true);
							lastEnd = k;
							subSecStruct.add(sub);
							rparenFound = false;
						}
					}else if(currChar == ')'){
						rparenFound = true;
					}
					k++;				
				}
				for(int i = 0; i < subSecStruct.size(); i++){
					subSecStruct.get(i).printPalin();
					// (((.(((......))).
					// find num of rparens
					RNAPalindrome currPalin = subSecStruct.get(i);
					String currPalinStr = bracketStr.substring(currPalin.getStartPos(),
							currPalin.getStartMatch() + 1);
					
					//check if numLParens >= numRParens or <
					int totalLParens = 0; int totalRParens = 0;
					for(int m = 0; m < currPalinStr.length(); m++){
						char currChar = currPalinStr.charAt(m);
						if(currChar == '('){
							totalLParens ++;
						}else if(currChar == ')'){
							totalRParens ++;
						}
					}
					int numRParens = -1;
					int numLParens = 0; 
					int firstLParen = 0;
					int lastRParen = 0;
					if(totalLParens >= totalRParens){
						numRParens = 0;
						numLParens = -1; 
						int j = currPalinStr.length() - 1;
						while(j >= 0 && numLParens < numRParens){
							char currToken = currPalinStr.charAt(j);
							if(currToken == ')'){
								if(lastRParen == 0){
									lastRParen = j;
								}
								numRParens++;
								numLParens ++;
							}else if(currToken == '('){
								numLParens ++;
							}
							j--;
						}
						if(numLParens == numRParens && numLParens > 0){
							firstLParen = j;
							int startPosInSecStruct = currPalin.getStartPos() + firstLParen - 1;
							int endPosInSecStruct = currPalin.getStartPos() + lastRParen; 
							//System.out.println(endPosInSecStruct);
							System.out.println("(" + firstLParen + ", " + lastRParen + ")"
							+ currPalinStr.substring(firstLParen - 1, lastRParen + 1));
							RNAPalindrome currStemLoop =
									new RNAPalindrome(startPosInSecStruct, endPosInSecStruct, true);
							stemLoops.add(currStemLoop);
						}
					}else{
						//if num of rparens > lparens, read from left
						numRParens = -1;
						numLParens = 0; 
						int j = 0;
						while(j < currPalinStr.length() && numLParens > numRParens){
							char currToken = currPalinStr.charAt(j);
							if(currToken == '('){
								if(firstLParen == 0){
									firstLParen = j;
								}
								numRParens++;
								numLParens ++;
							}else if(currToken == ')'){
								numRParens ++;
							}
							j++;
						}
						if(numLParens == numRParens && numLParens > 0){
							lastRParen = j;
							int startPosInSecStruct = currPalin.getStartPos() + firstLParen - 1;
							int endPosInSecStruct = currPalin.getStartPos() + lastRParen + 1; 
							//System.out.println(endPosInSecStruct);
							System.out.println("(" + firstLParen + ", " + lastRParen + ")"
							+ currPalinStr.substring(firstLParen - 1, lastRParen + 1));
							RNAPalindrome currStemLoop =
									new RNAPalindrome(startPosInSecStruct, endPosInSecStruct, true);
							stemLoops.add(currStemLoop);
						}
					}
					
					
				}
				
				System.out.println(bracketStr);
				System.out.println("Stem Loops: ");
				for(int i = 0; i < stemLoops.size(); i++){
					RNAPalindrome currStemLoop = stemLoops.get(i);
					currStemLoop.printPalin();
					System.out.println(bracketStr.substring(currStemLoop.getStartPos(), currStemLoop.getStartMatch() + 1));
				}
				//run prediction
				/*
				RNASeqInstance seqInstance = new RNASeqInstance(rnaSeq.length(), rnaSeq);
				seqInstance.findPredResult();
				seqInstance.printAllSecStruct();
			*/
			}
		}catch(IOException e){
			System.out.println(e.getMessage());
		}finally{
			s.close();
			sbracket.close();
		}


	}

	public static char getOppositeParen(char p){
		if(p == '('){
			return ')';
		}
		if(p == ')'){
			return '(';
		}
		return p;

	}
	
	public static LinkedList<RNAPalindrome> findStemLoops(LinkedList<RNAPalindrome> sl){
		LinkedList<RNAPalindrome> stemLoops = sl;
		
		return stemLoops;
	}

	

}
