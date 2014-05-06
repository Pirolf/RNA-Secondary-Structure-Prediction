import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Scanner;


public class Predictioner {
	public static void main(String[] args) {
		File rnaSeqFile = new File("src/ecoli16sRNA_J01695");
		File rnaSecStructFile = new File("src/ecoli16sRNA_J01695_bracket");
		//File rnaSecStructFile = new File("src/d.16.b.A.globiformis");
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
				//bracketStr = bracketStr.substring(368, 393);
				//bracketStr = bracketStr.substring(406, 435);
				//bracketStr = bracketStr.substring(135, 223);
				//bracketStr = "((...((((..))))....))";
				//bracketStr = "(((.(((......)))...(((....)))...(((......))).)))..";
				bracketStr = "(((.(((...)))...((((.((..)).)))).(((.((...((.(...)..)).))..(..(((...))).).))))))..";
				//store substrings of a sec struct, each substring contains at most 1 stem loop
				LinkedList<RNAPalindrome> subSecStruct = new LinkedList<RNAPalindrome>();

				//test breakToStemLoops;
				subSecStruct = breakToUnitSegs(bracketStr, subSecStruct, 0, bracketStr.length());
				LinkedList<RNAPalindrome> stemLoops = locateStemLoops(bracketStr, subSecStruct);

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
	/**
	 * Find position of the stem loop in a unit segment if there is one
	 * @param bracketStr: original bracket string
	 * @param subSecStruct: list of unit segments
	 * @return list of stem loops with positions in original bracket string
	 */
	public static LinkedList<RNAPalindrome> locateStemLoops(String bracketStr, LinkedList<RNAPalindrome> subSecStruct){
		LinkedList<RNAPalindrome> stemLoops = new LinkedList<RNAPalindrome>();

		for(int i = 0; i < subSecStruct.size(); i++){
			RNAPalindrome currPalin = subSecStruct.get(i); //current unit segment
			int numRparens = 0; //total number of right parens in current unit segment
			int matchingLparens = 0;//number of left parens found
			int startPos = 0;//correct start position of the stem loop in original bracket string
			boolean leftFound = false;
			//make sure we only count right parens to the right of the last left paren
			//eg. ))).((...))

			/**
			 *Read from right to left, because unit segment list returned by breakToUnitSegs
			 *has correct end positions in original bracket string
			 *but start position may be left by 0 or more positions
			 * 
			 */
			for(int j = currPalin.getStartMatch(); j >= currPalin.getStartPos() && !leftFound; j--){
				char currChar = bracketStr.charAt(j);
				if(currChar == ')'){
					numRparens ++;
				}else if(currChar == '('){
					leftFound = true;
				}
			}
			for(int j = currPalin.getStartMatch(); j >= currPalin.getStartPos() && matchingLparens < numRparens; j--){
				char currChar = bracketStr.charAt(j);
				if(currChar == '('){
					matchingLparens ++;
				}
				if(matchingLparens == numRparens){
					startPos = j;
					//adjust start position of the unit seg, now it is a stem loop
					currPalin.setEnds(startPos, currPalin.getStartMatch());
					//add the stem loop to the list
					stemLoops.add(currPalin);
				}
			}
		}

		return stemLoops;

	}
	/**
	 * Break a secondary structure into unit segments, such that each 
	 * unit segment contains at most 1 stem loop.
	 * This may generate segments such as ...(((...))), (.(..), ((...)), .)..)))
	 * 
	 * @param str: bracket string of current segment
	 * @param subStructList: list of unit segments, passed onto recursive calls
	 * to add new unit segment to
	 * @param totalOffset: start position of the current segment in the original bracket string,
	 * keeps track of the position of start and end tokens in the bracket string
	 * @param initLength: the length of the original bracket string, make sure every
	 * unit segment to be added to the segment list is within valid range of the original string
	 * @return list of unit segments
	 */
	public static LinkedList<RNAPalindrome> breakToUnitSegs(String str,
			LinkedList<RNAPalindrome> subStructList, int totalOffset, int initLength){
		LinkedList<RNAPalindrome> sl = subStructList;
		//(((..(...)...))) => no
		//(((..(...)..((.)).......((...(..(..)...)..))..........))) => no
		//bracketStr = "(((.(((...)))...((((....)))).(((.((...((.(...)..)).))..(..).))))))..";
		int i = 0;
		char prevSym = str.charAt(0); //preceding char of the current char
		//start and end position (relative to current string)of the substring of the current string to be passed on the the recursive call
		int startOfSubs = 0;
		int endOfSubs = 1; 
		int numLparens = 0;
		while(i < str.length()){
			char currSym = str.charAt(i);
			if(currSym == '.' && i != 0){
				if(prevSym == '('){
					startOfSubs = i - 1;//startOfSubs is always in the most inner (, eg, the third ( in ..(((...)))
					endOfSubs++;
				}else if(prevSym == ')'){
					endOfSubs = i - 1;
					// go one level down
					String substrOfCurrStr = str.substring(startOfSubs, endOfSubs + 1);
					subStructList = breakToUnitSegs(substrOfCurrStr, sl, totalOffset + i, initLength);
					startOfSubs = endOfSubs +1;

				}else{
					endOfSubs++;
				}
			}else if(currSym == '('){
				numLparens ++;
			}
			prevSym = currSym;
			i++;
		}
		//make sure the start and end positions of the stem loop is in valid range
		if(totalOffset - 1 < initLength && totalOffset - i - numLparens >=0){
			System.out.print("numparens :" + numLparens + ", totalOffset: " + totalOffset + ", i: " + i);
			//totalOffset - 1: end position of the stemLoop in original bracket str,
			//totalOffset - 1 - i: position of the most inner ( in the unit seg, eg. the third ( in ...(((...)))
			//totalOffset - 1 - i - numLparens - 1: position of the first ( in the unit seg, or to off by some positions to the left of the first (
			/**We only make sure the unit seg contains at most 1 stem loop, locating exact start and end positions 
			*of the stem loop is handled by locateStemLoop() 
			*/
			RNAPalindrome unitSubStruct = new RNAPalindrome(totalOffset - 1 - i - numLparens -1, totalOffset - 1, true);
			unitSubStruct.printPalin();
			System.out.println();
			sl.add(unitSubStruct);
		}

		return sl;
	}

}
