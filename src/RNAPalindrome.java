
public class RNAPalindrome {
	//... C           G            G     ...      C        C        G...
	//....startPos    tmpEndPos   endPos        endMatch          startMatch
	//HASH: startPos, startMatch
	private int startPos;
	private int startMatch;
	private int tmpEndPos;
	private int endPos;
	private int endMatch;
	private int length;
	private int hashPalin;
	public RNAPalindrome(int startPos, int startMatch){
		this.startPos = startPos;
		this.startMatch = startMatch;
		this.tmpEndPos = startMatch;
		this.endPos = 0;
		this.endMatch = 0;
		length = 0;
	}
	/**
	 * for the bracket file
	 * @param startPos
	 * @param endPos
	 * @param ended
	 */
	public RNAPalindrome(int startPos, int endPos, boolean ended){
		this.startPos = startPos;
		this.endPos = endPos;
	}
	public void hash(){
		hashPalin = (startPos + ", " + startMatch).hashCode();
	}
	public void setTmpEndPos(int tmp){
		this.tmpEndPos = tmp;
	}
	public int getTmpEndPos(){
		return this.tmpEndPos;
	}
	public void setEnds(int endPos, int endMatch){
		this.endPos = endPos;
		this.endMatch = endMatch;
	}
	public int getEndPos(){
		return this.endPos;
	}
	
}
