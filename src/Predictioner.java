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
	public static LinkedList<RNAPalindrome> bracketToPalin(String bracket){
		int bracketLength = bracket.length();
		RNAPalindrome currPalin = null;
		//same trick: find palindromes
		char[][] bracketMatrix = new char[bracketLength][bracketLength];
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

}
