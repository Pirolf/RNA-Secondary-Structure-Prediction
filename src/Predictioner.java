import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Scanner;


public class Predictioner {
	public static int absCurrChar = -1;
	public static LinkedList<RNAPalindrome> rp = new LinkedList<RNAPalindrome>();
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
				//run prediction
				
				RNASeqInstance seqInstance = new RNASeqInstance(rnaSeq.length(), rnaSeq);
				seqInstance.findPredResult();
				seqInstance.printAllSecStruct();
			
				 

				
				//translate brackets
				
				findAllPairs(-1, bracketStr);
				Iterator<RNAPalindrome> itr = rp.iterator();
				LinkedList<RNAPalindrome> pairs = new LinkedList<RNAPalindrome>();
				while(itr.hasNext()){

					RNAPalindrome currBasePair = itr.next();

					int start = currBasePair.getStartPos();
					int startMatch = currBasePair.getStartMatch();
					if(bracketStr.charAt(start) != '.' && bracketStr.charAt(startMatch) != '(' && bracketStr.charAt(startMatch) != '.'){
						int numLparen = 0; int numRparen = 0;
						String currStr = bracketStr.substring(start, startMatch + 1);
						for(int i = 0; i < currStr.length(); i++){
							if(currStr.charAt(i) == '('){
								numLparen ++;
							}else if(currStr.charAt(i) == ')'){
								numRparen ++;
							}
						}
						if(numLparen == numRparen){
							pairs.add(currBasePair);
							//System.out.println("(" + start + ", " + startMatch + "):" + bracketStr.substring(start, startMatch + 1));	
						}
						
					}
				}
				System.out.println("stemloops:");
				LinkedList<RNAPalindrome> sll = findStemLoops(pairs);
				for(int i = 0; i < sll.size(); i++){
					sll.get(i).printPalin();
					System.out.println();
				}
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
	public static void findAllPairs(int absLparenPos, String subseq){

		absCurrChar = findMatchingBasePairs(absLparenPos, subseq);
		//System.out.println("currPos: " + currPos);
		while(absCurrChar < subseq.length() - 1){
			//System.out.println("currPos: " + currPos);
			absCurrChar = findMatchingBasePairs(absCurrChar, subseq.substring(absCurrChar + 1));
		}

	}
	public static boolean isLeftPart = true;
	public static int findMatchingBasePairs(int absLparenPos, String subseq){
		char firstChar = subseq.charAt(0);
		if (firstChar == '.'){
			if(isLeftPart){
				absCurrChar ++;
				//proceed
				int matchingRparen = findMatchingBasePairs(absLparenPos + 1, subseq.substring(1));
				RNAPalindrome basePair = new RNAPalindrome(absLparenPos + 1, matchingRparen, true);
				rp.add(basePair);
				return matchingRparen;
			}else{
				int i = 0;
				absCurrChar ++;
				char c = '.';
				while(i < subseq.length() && c == '.'){
					c = subseq.charAt(i);
					absCurrChar ++;
					i++;
				}
				if(c == ')'){
					isLeftPart = false;		

				}else if(c == '('){
					isLeftPart = true;

				}
				if(isLeftPart){
					//return findMatchingBasePairs(absLparenPos + 1, subseq.substring(1));
					return absCurrChar + i;
				}else{
					return absCurrChar + i;
				}


			}

		}else if(firstChar == '('){
			absCurrChar ++;
			//call resursively
			int matchingRparen = findMatchingBasePairs(absLparenPos + 1, subseq.substring(1));
			//add to list
			//System.out.println(matchingRparen);
			RNAPalindrome basePair = new RNAPalindrome(absLparenPos + 1, matchingRparen, true);
			rp.add(basePair);
			isLeftPart = true;
		}
		isLeftPart = false;
		absCurrChar ++;

		return absCurrChar;
		//return rparen's absolute position


	}


	// ((.(((....)))..(....)....)...)
	// (((...))..)
	public static LinkedList<RNAPalindrome> markParensWithMultipleChildren(LinkedList<RNAPalindrome> r){
		for(int i = 0; i < r.size(); i++){
			RNAPalindrome curr1 = r.get(i);
			int start1 = curr1.getStartPos();
			int end1 = curr1.getStartMatch();
			int children = 0;
			int endOfLastChild = 0;
			int startOfLastChild = end1;
			for(int j = 0; j < r.size() && children < 2; j++){
				if(j!=i){
					RNAPalindrome curr2 = r.get(j);

					int start2 = curr2.getStartPos();
					int end2 = curr2.getStartMatch();
				//	System.out.println("start2, end2 " + start2 + ","+ end2 + "; start1, end1 " + start1 + ", " + end1);
					if(start2 > start1 && end2 < end1){
						//curr2.setBelongsToStemLoop(false);
						if(start2 > endOfLastChild || end2 < startOfLastChild){
							children ++;
						//	System.out.println("endOfLastChild: " + end2+ ", startOfLastChild: " + start2);
						//	System.out.println("children of (" + start1 + ", " + end1 + "): " + children);
							endOfLastChild = end2;
							startOfLastChild = start2;	
						}
					}
				}				
			}
			if(children >= 2){
				curr1.setBelongsToStemLoop(false);
				//System.out.println("(" + start1 + ", " + end1 + "): belongsToStemLoop: " + curr1.getBelongsToStemLoop());
			}		
		}
		return r;	
	}
	public static LinkedList<RNAPalindrome> findStemLoops(LinkedList<RNAPalindrome> r){
		r = markParensWithMultipleChildren(r);
		RNAPalindrome[] stemLoops = new RNAPalindrome[r.size()];//(RNAPalindrome[])(rp.toArray());
		for(int i = 0; i < stemLoops.length; i++){
			stemLoops[i] = r.get(i);
		}
		for(int i = 0; i < r.size(); i++){
			RNAPalindrome curr1 = r.get(i);
			if(curr1.getBelongsToStemLoop()){
				int minStart = curr1.getStartPos();
				int maxEnd = curr1.getStartMatch();
				for(int j = 0; j < r.size(); j++){
					RNAPalindrome curr2 = r.get(j);
					int currStart2 = curr2.getStartPos();
					int currEnd2 = curr2.getStartMatch();
					if(curr2.getBelongsToStemLoop()){
						if(currStart2 > minStart && currEnd2 < maxEnd){
							//i enloses j
							stemLoops[j] = null;
						}else if(currStart2 < minStart && currEnd2 > maxEnd){
							//  currStart2 minStart maxEnd currEnd2
							//      j         i
							stemLoops[i] = null;
							//so that we can continue to remove any parens within i even if it's not a stem loop
							minStart = curr2.getStartPos();
							maxEnd = curr2.getStartPos();
						}						
					}
				}//end inner for
			}//end if
		}
		LinkedList<RNAPalindrome> stemLoopList = new LinkedList<RNAPalindrome>();
		for(int i = 0; i < stemLoops.length; i++){
			if(stemLoops[i] != null){
				if(stemLoops[i].getBelongsToStemLoop())
					stemLoopList.add(stemLoops[i]);
			}
		}
		return stemLoopList;
	}



}
