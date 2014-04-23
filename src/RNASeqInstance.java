import java.util.LinkedList;
//import java.sql.*;
public class RNASeqInstance {
	public static final char GAP = 'e';
	
	private int instanceID;
	private int instanceOrder;
	private int seqLength;
	private String seq;
	public int[][] seqMatrix;
	private LinkedList<RNAPalindrome> palinSeq;
	public RNASeqInstance(int instanceID, int instanceOrder, int seqLength, String seq){
		this.instanceID = instanceID;
		this.instanceOrder = instanceOrder;
		this.seqLength = seqLength;
		this.seq = seq;
		seqMatrix = new int[seqLength + 1][seqLength + 1];
		String revSeq = reverseSeq(seq);
		//init matrix: row for originral seq, column for rev
		for(int i = 0; i < seqMatrix.length; i++){
			seqMatrix[i][0] = i;
			seqMatrix[0][i] = i;
		}
		for(int i = 1; i < seqMatrix.length; i++){
			for(int j = 1; j < seqMatrix.length; j++){
				if(seq.charAt(i) == seq.charAt(j)){
					seqMatrix[i][j] = seqMatrix[i-1][j-1];
					//TODO: store the info
				}else{
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
			reverse += seq.charAt(i);
		}
		return reverse;
	}
	
}
