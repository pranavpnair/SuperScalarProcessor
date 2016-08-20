import java.util.Comparator;
import java.util.TreeSet;


public class Retire {
	TreeSet<Info> tmpbuffer;
	
	Retire(Comparator<Info> ins){
		tmpbuffer = new TreeSet<Info>(ins);
	}
	
	public void retiretmp(TreeSet<Info> storebuffer){
		if(!storebuffer.isEmpty())
			tmpbuffer.addAll(storebuffer);
		storebuffer.clear();
	}
	
	public int retireperm(int[] Data , int halt){
		int returnvalue;
		if(halt == 1)
			 returnvalue = 1;
		else{
			returnvalue = 0;
		}
		Info i;
		while(!tmpbuffer.isEmpty()){
			System.out.println("Retire Stage instruction no: " + String.valueOf(tmpbuffer.first().instno));
			i = tmpbuffer.pollFirst();
			Data[i.aluoutput] = i.op2;
		}
		return returnvalue;
	}
}
