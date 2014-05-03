
public class RNAPalindrome {
	//... (C           G            G)    ...      (C        C        G)...
	//....startPos    tmpEndPos   endPos        endMatch          startMatch
	//HASH: startPos, startMatch
	private int startPos;
	private int startMatch;
	private int tmpEndPos;
	private int endPos;
	private int numBasePairs = 0;
	private boolean belongsToStemLoop = true;
	public RNAPalindrome(int startPos, int startMatch, int numBasePairs){
		this.startPos = startPos;
		this.startMatch = startMatch;
		this.tmpEndPos = startMatch;
		this.endPos = 0;
		this.numBasePairs = numBasePairs;
	}
	/**
	 * for the bracket file
	 * @param startPos
	 * @param endPos
	 * @param ended
	 */
	public RNAPalindrome(int startPos, int startMatch, boolean ended){
		this.startPos = startPos;
		this.startMatch= startMatch;
	}
	public int getBasePairs(){
		return numBasePairs;
	}
	public int getStartMatch(){
		return startMatch;
	}
	public void setTmpEndPos(int tmp){
		this.tmpEndPos = tmp;
	}
	public boolean getBelongsToStemLoop(){
		return belongsToStemLoop;
	}
	public void setBelongsToStemLoop(boolean sl){
		belongsToStemLoop = sl;
	}
	public int getTmpEndPos(){
		return this.tmpEndPos;
	}
	public void setEnds(int startPos, int startMatch){
		this.startPos = startPos;
		this.startMatch = startMatch;
	}
	public int getEndPos(){
		return this.endPos;
	}
	public int getStartPos(){
		return this.startPos;
	}
	
	public void printPalin(){
		System.out.print("(" + startPos + ", " + startMatch + ")");
	}
	
}
