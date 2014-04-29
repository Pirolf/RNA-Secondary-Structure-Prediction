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
		for(int i = 0; i < predMatrix.length; i++){
			for(int j = 0; j < predMatrix.length; j++){
				
					predMatrix[i][j] = new BaseCell(i, j, 0);

			}


		}
		for(int i= 0; i < predMatrix.length - 1; i++){
			for(int j = 0; j < predMatrix.length - i - 1; j++){
				BaseCell currBaseCell = predMatrix[i][j];
				if(getOpposite(revSeq.charAt(i)) == seq.charAt(j)){
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

	public void findPredResult(){
		for(int i = 0; i < predMatrix.length; i++){
			for(int j = 0; j < predMatrix.length - i - 1; j++){
				BaseCell currCell = predMatrix[i][j];
				if(currCell == null){
					System.out.println("currCell at "+ i + ", " + j + " is null");
				}
				if(currCell != null){
					//System.out.println("currCell at "+ i + ", " + j + " is not null");
					if(currCell.getTail() != null){
						int start = currCell.getCol();
						int end = predMatrix.length - 1 - currCell.getRow();
						RNAPalindrome secStruct = new RNAPalindrome(start, end);
						palinSeq.add(secStruct);
					}
				}

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
			System.out.println("(" + start + ", " + end + ")" + ": " + seq.substring(start, end + 1));

		}
		System.out.println("Total predicted secondary structures: " + palinSeq.size());
	}


}
