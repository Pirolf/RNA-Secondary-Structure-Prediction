import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Scanner;


public class Predictioner {

	public static void main(String[] args) {
		File rnaSeqFile = new File("src/ecoli16sRNA_J01695");
		File rnaSecStructFile = new File("src/ecoli16sRNA_J01695_bracket");
		Scanner s = null;
		Scanner sbracket = null;
		try{
			String rnaSeq = "";
			String bracketStr = "";
			s = new Scanner(rnaSeqFile);
			sbracket = new Scanner(rnaSecStructFile);
			if(s.hasNextLine()){
				rnaSeq = s.nextLine();
				System.out.println(rnaSeq.length());
				//run prediction
				RNASeqInstance seqInstance = new RNASeqInstance(rnaSeq.length(), rnaSeq);
				seqInstance.findPredResult();
				seqInstance.printAllSecStruct();
				
				
				
				while(sbracket.hasNextLine()){
					bracketStr += sbracket.nextLine();
				}
			
				bracketStr = "((((....))).(..)).(.)";

				LinkedList<RNAPalindrome> palinsOfBracket = new LinkedList<RNAPalindrome>();
				RNAPalindrome p1 = new RNAPalindrome(1, 8, true);
				RNAPalindrome p2 = new RNAPalindrome(2, 7, true);
				RNAPalindrome p3 = new RNAPalindrome(10, 13, true);
				RNAPalindrome p5 = new RNAPalindrome(15, 18, true);
				RNAPalindrome p4 = new RNAPalindrome(0, 14, true);
				RNAPalindrome p6 = new RNAPalindrome(3, 6, true);
				RNAPalindrome p7 = new RNAPalindrome(20, 22, true);
				palinsOfBracket.add(p7);	
				palinsOfBracket.add(p6);
				palinsOfBracket.add(p4);
				palinsOfBracket.add(p1);
				palinsOfBracket.add(p5);
				palinsOfBracket.add(p3);
				palinsOfBracket.add(p2);						
				
				//palinsOfBracket.add(p3);
				LinkedList<RNAPalindrome> rp = findStemLoops(palinsOfBracket);
				/*
				for(int i = 0; i < palinsOfBracket.size(); i++){
					System.out.print(" ");
					palinsOfBracket.get(i).printPalin();
				}*/
				for(int i = 0; i < rp.size(); i++){
					System.out.print(" ");
					rp.get(i).printPalin();
				}
				

			}
		}catch(IOException e){
			System.out.println(e.getMessage());
		}finally{
			s.close();
			sbracket.close();
		}


	}
	/**
	 * TODO: have issues here
	 * Convert secondary structures from bracket to RNAPalin
	 * Duplicate stems are also produced,
	 * we can remove duplicates afterwards (eg. merge (4, 8) and (3, 11) by using distance)
	 * @param bracket
	 * @param startSub: starting left paren's position in original string
	 * @param posOffset:  position of this char in the original sequence
	 * @return
	 */
	//public static int posOffset = 0; 
	//public static int endPos;
	public static LinkedList<RNAPalindrome> bracketToPalin(LinkedList<RNAPalindrome> rp,
			String originalBracket, String bracket,
			int startSub, int posOffset){
		char currChar = bracket.charAt(0);
		System.out.print("currChar: " + currChar);
		System.out.print(posOffset + ", ");
		if(currChar == '('){
			System.out.print("lparen, ");
			startSub = posOffset;//start from current position of (
			posOffset ++;
			currChar = bracket.charAt(0);
			bracketToPalin(rp, originalBracket, bracket.substring(1), startSub, posOffset);	
			
			 //endPos =  posOffset;
			
		}else if(currChar == '.'){
			System.out.print("dot, ");
			posOffset ++;
			//startSub = posOffset;
			bracketToPalin(rp, originalBracket, bracket.substring(1), startSub, posOffset);
		}else if (currChar ==')'){
		// ')'  ......((...(((....))).....))... 
		//calc positions, and add palindrome to the list
			System.out.print("rparen, ");
		int endPos =  posOffset;
		posOffset++;
		rp.add(new RNAPalindrome(startSub, endPos, true));
		boolean currIsRParen = true;
		int currPos = posOffset;
		while(currIsRParen){
			if(originalBracket.charAt(currPos) == ')'){
				rp.add(new RNAPalindrome(startSub-1, endPos+1, true));
				startSub--; endPos++;
			}else{
				currIsRParen = false;
			}
			currPos++;
		}
		//return rp;
		}
		return rp;
	}

	// ((.(((....)))..(....)....)...)
	// (((...))..)
	public static LinkedList<RNAPalindrome> markParensWithMultipleChildren(LinkedList<RNAPalindrome> rp){
		for(int i = 0; i < rp.size(); i++){
			RNAPalindrome curr1 = rp.get(i);
			int start1 = curr1.getStartPos();
			int end1 = curr1.getEndPos();
			int children = 0;
			int endOfLastChild = 0;
			int startOfLastChild = end1;
			for(int j = 0; j < rp.size() && children < 2; j++){
				if(j!=i){
					RNAPalindrome curr2 = rp.get(j);
					
					int start2 = curr2.getStartPos();
					int end2 = curr2.getEndPos();
					System.out.println("start2, end2 " + start2 + ","+ end2 + "; start1, end1 " + start1 + ", " + end1);
					if(start2 > start1 && end2 < end1){
						//curr2.setBelongsToStemLoop(false);
						if(start2 > endOfLastChild || end2 < startOfLastChild){
							children ++;
							System.out.println("endOfLastChild: " + end2+ ", startOfLastChild: " + start2);
							System.out.println("children of (" + start1 + ", " + end1 + "): " + children);
							endOfLastChild = end2;
							startOfLastChild = start2;	
						}
					}
				}				
			}
			if(children >= 2){
				curr1.setBelongsToStemLoop(false);
				System.out.println("(" + start1 + ", " + end1 + "): belongsToStemLoop: " + curr1.getBelongsToStemLoop());
			}		
		}
		return rp;	
	}
	public static LinkedList<RNAPalindrome> findStemLoops(LinkedList<RNAPalindrome> rp){
		rp = markParensWithMultipleChildren(rp);
		RNAPalindrome[] stemLoops = new RNAPalindrome[rp.size()];//(RNAPalindrome[])(rp.toArray());
		for(int i = 0; i < stemLoops.length; i++){
			stemLoops[i] = rp.get(i);
		}
		for(int i = 0; i < rp.size(); i++){
			RNAPalindrome curr1 = rp.get(i);
			if(curr1.getBelongsToStemLoop()){
				int minStart = curr1.getStartPos();
				int maxEnd = curr1.getEndPos();
				for(int j = 0; j < rp.size(); j++){
					RNAPalindrome curr2 = rp.get(j);
					int currStart2 = curr2.getStartPos();
					int currEnd2 = curr2.getEndPos();
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
							maxEnd = curr2.getEndPos();
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
