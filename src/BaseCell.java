//represents a single cell in the matrix
public class BaseCell {
	private int row;
	private int col;
	private int editDistance;
	private BaseCell head;//as a back pointer
	private BaseCell tail;
	private boolean isTail;
	private boolean isHead;
	public BaseCell(int r, int c, int ed){
		row = r;
		col = c;
		editDistance = 0;
		head = null;
		tail = null;
		isHead = false;
		isTail = false;
	}
	public void setHead(BaseCell bc){
		head = bc;
	}
	public void incrEditDistance(){
		editDistance ++;
	}
	public BaseCell getHead(){
		return head;
	}
	public void setHead(){
		isHead = true;
	}
	public boolean checkHead(){
		return isHead;
	}
	public void linkHead(BaseCell h){
		head = h;
	}
	public void setTail(){
		isTail = true;
	}
	public void linkTail(BaseCell t){
		tail = t;
	}
	public BaseCell getTail(){
		return tail;
	}
	public int getRow(){
		return row;
	}
	public int getCol(){
		return col;
	}
}
