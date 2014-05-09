import java.util.Iterator;
import java.util.LinkedList;
//import java.sql.*;
public class RNASeqInstance {
	private String seq;
	public BaseCell[][] predMatrix;
	private LinkedList<RNAPalindrome> palinSeq;
	public RNASeqInstance(int seqLength, String seq){
		palinSeq = new LinkedList<RNAPalindrome>();
		this.seq = seq;
		predMatrix = new BaseCell[seqLength][seqLength];
		String revSeq = reverseSeq(seq);

		/**
		 * 	A T T C G T A A C C T T G C T A C
		 * 
		 * 
		 * 
		 */
		//Init matrix
		for(int i = 0; i < predMatrix.length; i++){
			for(int j = 0; j < predMatrix.length; j++){
				predMatrix[i][j] = new BaseCell(i, j, 0);
			}
		}
		for(int i= 0; i < predMatrix.length - 1; i++){
			for(int j = 0; j < predMatrix.length - i - 1; j++){
				BaseCell currBaseCell = predMatrix[i][j];
				if(getOpposite(revSeq.charAt(i), 1, 50) == seq.charAt(j)){
					(predMatrix[i][j]).setHead();
					if(i >= 1 && j >= 1){
						BaseCell leftTop = predMatrix[i-1][j-1];
						if(leftTop.checkHead()){
							currBaseCell.linkHead(leftTop);
						}
					}
					//so we will have head<-head<-head
					//if a cell is head but its head is null, than this cell is the start of the sec struct
					//then we can get its tail as the ending position

				}else{
					if(i >= 1 && j >= 1){
						BaseCell leftTop = predMatrix[i-1][j-1];
						int currRow = i-1;
						boolean linking = true;
						BaseCell currNode = leftTop;
						while(currRow >= 1 && linking){					
							if(currNode.getHead() == null){
								linking = false;
							}else{
								currNode = currNode.getHead();
								currRow --;
							}						
						}
						if(currRow < i - 1){
							currNode.linkTail(leftTop);
						}
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
			reverse += currChar;
		}
		return reverse;
	}

	public static char getOpposite(char c){
		switch(c){
		case 'A': return 'U';
		case 'U': return 'A';
		case 'G': return 'C';
		case 'C': return 'G';
		default:return 'A';
		}
	}
	/**
	 * For heurisitic functions for G-U pairs, with probability of 0.05 being G-U
	 * @param c
	 * @param heuristicNum
	 * @return
	 */
	public static char getOpposite(char c, int heuristicNum){
		double probGU = Math.random();
		if(c == 'A'){
			return 'U';
		}
		if(c == 'U'){
			return 'A';
		}
		if(probGU > 0.95){
			if(c == 'G'){return 'U';}
			if(c == 'U'){return 'G';}
		}
		if(c == 'G'){return 'C';}

		return 'G';

	}
	
	public static char getOpposite(char c, int heuristicNum, int dist){
		int distLimit = 50;
		double probGU = Math.random();
		if(c == 'A'){
			return 'U';
		}
		if(c == 'U'){
			return 'A';
		}
		if(probGU > 0.95 && dist < distLimit){
			if(c == 'G'){return 'U';}
			if(c == 'U'){return 'G';}
		}
		if(c == 'G'){return 'C';}

		return 'G';

	}

	public void findPredResult(){
		for(int i = 0; i < predMatrix.length; i++){
			int maxBP = 0;
			int startOfMaxBP = 0;
			int endOfMaxBP = 0;
			//Be greedy, we only accept secondary structures with the max base pairs
			for(int j = predMatrix.length - i - 1 - 1; j >= 0; j--){
			//for(int j = 0; j < predMatrix.length - i - 1; j++){
				BaseCell currCell = predMatrix[i][j];
				if(currCell != null){
					if(currCell.getTail() != null){
						int start = currCell.getCol();
						int end = predMatrix.length - 1 - currCell.getRow();
						int numBP = Math.abs(currCell.getCol()-currCell.getTail().getCol()) + 1;
						if(numBP > maxBP){
							maxBP = numBP;
							startOfMaxBP = start;
							endOfMaxBP = end;
						}
						
					}
				}

			}//end of inner for
			if(maxBP != 0){
				RNAPalindrome secStruct = new RNAPalindrome(startOfMaxBP, endOfMaxBP, maxBP);
				palinSeq.add(secStruct);
			}
		}
	}
	public void printAllSecStruct(){
		Iterator<RNAPalindrome> itr = palinSeq.iterator();
		int counter = 0;
		while(itr.hasNext()){
			RNAPalindrome currPalin = itr.next();
			int start = currPalin.getStartPos();
			int end = currPalin.getStartMatch();
			counter ++;
			System.out.println("(" + start + ", " + end + ")" + ": "  + currPalin.getBasePairs() + ", "+ seq.substring(start, end + 1));

		}
		System.out.println("Total predicted secondary structures: " + palinSeq.size());
	}


}
