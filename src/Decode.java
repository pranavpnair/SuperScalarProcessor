import java.util.Comparator;
import java.util.TreeSet;


public class Decode {
	TreeSet<Info> tmpbuffer;
	TreeSet<Info> poppedInfo;
	TreeSet<Info> tmp;
	Decode(Comparator<Info> ins){
		tmpbuffer = new TreeSet<Info>(ins);
		poppedInfo = new TreeSet<Info>(ins);
		tmp = new TreeSet<Info>(ins);
	}
	public void flush(TreeSet<Info> dispatchbuffer, int instno)
	{
		tmpbuffer.clear();
		for(Info i: dispatchbuffer){
			if(i.instno <= instno)
				tmp.add(i);
		}
		dispatchbuffer.clear();
		dispatchbuffer.addAll(tmp);
		tmp.clear();
	}
	
	public void decodetmp(TreeSet<Info> decodebuffer, int n){
		while(tmpbuffer.size()<=n && decodebuffer.size() > 0 ){
			Info i = decodebuffer.pollFirst();
			i.instype = i.getbits(i.instruction, 13, 15);
			i.immbit = i.getbits(i.instruction, 12, 12);
			switch(i.instype){
				case 0: 
				case 1:
				case 2: i.src1 = i.getbits(i.instruction, 4, 7);
						i.dest = i.getbits(i.instruction, 8, 11); 
						if(i.immbit == 1)
							i.immvalue = i.getbits(i.instruction, 0, 3);
						else
							i.src2 = i.getbits(i.instruction, 0, 3);
						break;
				case 3: i.src1 = i.getbits(i.instruction, 4, 7);
						i.dest = i.getbits(i.instruction, 8, 11);
						break;
				case 4:	i.src2 = i.getbits(i.instruction, 4, 7);
						i.src1 = i.getbits(i.instruction, 8, 11);
						break;
				case 5: i.label = i.getbits(i.instruction, 4, 11);
						break;
				case 6: i.src1 = i.getbits(i.instruction, 8, 11);
						i.label = i.getbits(i.instruction, 0, 7);
						break;
				case 7: break;
			}
			tmpbuffer.add(i);
		}	
	}
	
	public void decodeperm(TreeSet<Info> dispatchbuffer, int n, TreeSet<Info> decodebuffer){
		while(tmpbuffer.size() > 0 && dispatchbuffer.size() < n){
			System.out.println("Decode Stage instruction no: " + String.valueOf(tmpbuffer.first().instno));
			dispatchbuffer.add(tmpbuffer.pollFirst());
		}
		if(tmpbuffer.size()!=0){
			decodebuffer.addAll(tmpbuffer);
			tmpbuffer.clear();
		}
	}
}
