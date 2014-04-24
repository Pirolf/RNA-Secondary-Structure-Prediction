import java.io.File;
import java.io.IOException;
import java.util.Scanner;


public class Predictioner {

	public static void main(String[] args) {
		File rnaSeqFile = new File("ecoli16sRNA_J01695");
		Scanner s = null;
		try{
			String rnaSeq = "";
			s = new Scanner(rnaSeqFile);
			if(s.hasNextLine()){
				rnaSeq = s.nextLine();
				RNASeqInstance seqInstance = new RNASeqInstance(rnaSeq.length(), rnaSeq);
			}
		}catch(IOException e){
			System.out.println(e.getMessage());
		}finally{
			s.close();
		}
		

	}

}
