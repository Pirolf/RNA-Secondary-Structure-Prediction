import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Scanner;


public class Predictioner {

	public static void main(String[] args) {
		File rnaSeqFile = new File("ecoli16sRNA_J01695");
		File rnaSecStructFile = new File("ecoli16sRNA_J01695_bracket");
		Scanner s = null;
		Scanner sbracket = null;
		try{
			String rnaSeq = "";
			s = new Scanner(rnaSeqFile);
			sbracket = new Scanner(rnaSecStructFile);
			if(s.hasNextLine()){
				rnaSeq = s.nextLine();
				//run prediction
				RNASeqInstance seqInstance = new RNASeqInstance(rnaSeq.length(), rnaSeq);
				//get prediction result
				LinkedList<RNAPalindrome> predResult = seqInstance.getPalinList();
			}
		}catch(IOException e){
			System.out.println(e.getMessage());
		}finally{
			s.close();
			sbracket.close();
		}


	}
	/**
	 * Convert secondary structures from bracket to RNAPalin
	 * Duplicate stems are also produced, but as long as we can find a match, then it's ok
	 * or we can remove duplicates afterwards (eg. remove (4, 8) from (3, 9))
	 * @param bracket
	 * @param startSub: starting left paren's position in original string
	 * @param posOffset:  position of this char in the original sequence
	 * @return
	 */
	public static int posOffset = 0; 
	public static LinkedList<RNAPalindrome> bracketToPalin(LinkedList<RNAPalindrome> rp, String bracket,
			int startSub){
		char currChar = bracket.charAt(0);
		if(currChar == '('){
			startSub = posOffset;//start from current position of (
			posOffset ++;
			bracketToPalin(rp, bracket.substring(posOffset), startSub);	
		}else if(currChar == '.'){
			posOffset ++;
		}else{
			// ')'  ......((...(((....))).....))... 
			//calc positions, and add palindrome to the list
			int endPos =  posOffset;
			rp.add(new RNAPalindrome(startSub, endPos, true));
			posOffset++;
			return rp;
		}
		return rp;
	}

	public static String revBracket(String originalBracket){
		String revBracket = "";
		for(int i = 0; i < originalBracket.length(); i++){
			char currChar = originalBracket.charAt(i);
			if(currChar == '('){
				revBracket += ")";
			}else if(currChar == ')'){
				revBracket += "(";
			}else if(currChar == '.'){
				revBracket += ".";
			}
		}
		return revBracket;
	}

	public static int continuousParenLength(char paren, String str){
		char currChar = ' ';
		int currPos = 1;
		int contiLength = 1;
		while(currPos < str.length()){
			currChar = str.charAt(currPos);
			if(currChar == paren){
				contiLength++;
			}else{
				return contiLength;
			}
		}
		return contiLength;
	}

}
