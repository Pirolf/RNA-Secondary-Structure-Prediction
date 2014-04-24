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
	 * @param bracket
	 * @return
	 */
	public static LinkedList<RNAPalindrome> bracketToPalin(String bracket, int matchingLength){
		int bracketLength = bracket.length();
		RNAPalindrome currPalin = null;
		//same trick: find palindromes->NO
		//char[][] bracketMatrix = new char[bracketLength][bracketLength];
		char currChar = ' ';
		//find farthest matching parens
		for(int i = 0; i < bracket.length(); i++){
			currChar = bracket.charAt(i);
			if(currChar == '('){
				int contiLParenLength = continuousParenLength('(', bracket);
			}
		}
		//call recursively on substring
		//base case: substring length < 4
		return null;
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
