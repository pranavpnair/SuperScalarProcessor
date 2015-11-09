import java.util.Comparator;
import java.util.TreeSet;


public class Complete {
	int k;
	int halt;
	TreeSet<Info> tmpbuffer,tmp;
	Complete(Comparator<Info> ins){
		k=1;
		halt = 0;
		tmpbuffer = new TreeSet<Info>(ins);
		tmp = new TreeSet<Info>(ins);
	}
	
	public void completetmp(TreeSet<Info> reorderbuffer){
		if(!reorderbuffer.isEmpty())
			tmpbuffer.addAll(reorderbuffer);
		reorderbuffer.clear();
	}
	
	public int completeperm(int[] registerFile, int[] busy, int halt1){
		for(Info i:tmpbuffer){
			int l = i.instno;
			if(l == k){
				k++;
				System.out.println("Complete Stage instruction no: " + String.valueOf(tmpbuffer.first().instno));
				switch(i.instype){
				case 0:
				case 1:
				case 2: 
				case 3: registerFile[i.dest] = i.aluoutput;
						busy[i.dest] = 0 ;break;
				}
			}
			else{
				tmp.add(i);
			}
		}
		tmpbuffer.clear();
		tmpbuffer.addAll(tmp);
		tmp.clear();
		if(halt1 == 1)
			return 1;
		return 0;
	}
}
