import java.util.LinkedList;
//import java.sql.*;
public class RNASeqInstance {
	private int seqLength;
	private String seq;
	public int[][] seqMatrix;
	private LinkedList<RNAPalindrome> palinSeq;
	public RNASeqInstance(int seqLength, String seq){
		palinSeq = new LinkedList<RNAPalindrome>();
		this.seqLength = seqLength;
		this.seq = seq;
		seqMatrix = new int[seqLength + 1][seqLength + 1];
		String revSeq = reverseSeq(seq);
		//init matrix: row for original seq, column for rev
		for(int i = 0; i < seqMatrix.length; i++){
			seqMatrix[i][0] = i;
			seqMatrix[0][i] = i;
		}
		/**
		 * 	A T T C G T A A C C T T G C T A C
		 * 
		 * 
		 * 
		 */
		RNAPalindrome currPalin = null;
		for(int i = 1; i < seqMatrix.length - 4 - 2; i++){//4: no sharp turn, 
			//length - i - 4 >= 2
			//length - i - 4 >= j
			for(int j = 1; j < seqMatrix.length - i - 4; j++){
				if(revSeq.charAt(i) == seq.charAt(j)){
					seqMatrix[i][j] = seqMatrix[i-1][j-1];
					//check 
					if(currPalin == null){
						currPalin = new RNAPalindrome(j, seqMatrix.length - i + 1);
					}else{
						if(currPalin.getEndPos() != 0){
							//already ended
							
						}else{
							currPalin.setTmpEndPos(j);
						}				
					}
				}else{
					if(currPalin != null){
						if(currPalin.getTmpEndPos() == j - 1 && currPalin.getEndPos() == 0){
							currPalin.setEnds(j, seqMatrix.length - i);
							currPalin = new RNAPalindrome(j, seqMatrix.length - i + 1);
							palinSeq.add(currPalin);
						}
					}
					if(seqMatrix[i-1][j-1] < seqMatrix[i-1][j] 
							&& seqMatrix[i-1][j-1] < seqMatrix[i][j-1]){
						seqMatrix[i][j] = seqMatrix[i-1][j-1] + 1;
					}else{
						seqMatrix[i][j] = Math.min(seqMatrix[i-1][j], seqMatrix[i][j-1]);
					}
				}
			}

		}
	}

	public LinkedList<RNAPalindrome> getPalinList(){
		return palinSeq;
	}
	/**
	 * Reverses an RNA seq
	 * @return
	 */
	public static String reverseSeq(String seq){
		//ATTTCCCGA
		String reverse = "";
		for(int i = seq.length() - 1; i >= 0; i--){
			char currChar = seq.charAt(i);
			switch(currChar){
			case 'A': reverse += 'U';break;
			case 'U': reverse += 'A';break;
			case 'G': reverse += 'C';break;
			case 'C': reverse += 'G';break;
			default:break;
			}

		}
		return reverse;
	}

}
