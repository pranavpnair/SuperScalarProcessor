import java.util.Comparator;
import java.util.TreeSet;
import java.util.Vector;


public class Fetch {
	int newpc;
	int oldpc;
	int globalinstno;
	TreeSet<Info> tmpbuffer;
	int n;
	Fetch(int k, Comparator<Info> ins)
	{
		newpc = oldpc = 0;
		globalinstno = 1;
		n = k;
		tmpbuffer = new TreeSet<Info>(ins);
	}
	
	public void flush(TreeSet<Info> decodebuffer, int instno)
	{
		tmpbuffer.clear();
		TreeSet<Info> tmp = new TreeSet<Info>();
		for(Info i: decodebuffer){
			if(i.instno <= instno)
				tmp.add(i);
		}
		decodebuffer.clear();
		decodebuffer.addAll(tmp);
		globalinstno = instno+1;
	}
	
	public void fetchtmp(Vector<Integer> instructions, int pc){
		
		newpc = pc;
		oldpc = pc;
		while(tmpbuffer.size() < n && newpc < instructions.size() && pc>=0 ){
			Info i = new Info();
			i.instruction = instructions.get(newpc);
			i.pc = newpc;
			newpc++;
			i.instno = globalinstno;
			tmpbuffer.add(i);
			globalinstno++;
			//check if globalinstno implementation is right
		}
	}
	public int fetchperm(TreeSet<Info> decodebuffer, int pc, int size, Dispatch dispatch,Execute execute){
		if(oldpc != pc || execute.branched == 1){
			System.out.println("branch instruction encountered and pc changed.");
			dispatch.hazard = 0;
			execute.branched = 0;
			return pc;
		}
		else{
			while(tmpbuffer.size() > 0 && decodebuffer.size() < n - dispatch.hazard && pc < size ){
				System.out.println("Fetch Stage instruction no: " + String.valueOf(tmpbuffer.first().instno) + "   INS TYPE: " + String.valueOf(tmpbuffer.first().getbits(tmpbuffer.first().instruction, 13, 15)) + "   PC: "+pc);
				decodebuffer.add(tmpbuffer.pollFirst());
			}
			pc = newpc - tmpbuffer.size();
			globalinstno -= tmpbuffer.size(); 
			tmpbuffer.clear();
			if(dispatch.hazard!= 0)
				dispatch.hazard = 0;
			//System.out.println("No. of instructions fetched: " + Integer.toString(pc - oldpc) + "  PC:" + Integer.toString(pc) );
			return pc;
		}
	}
}
