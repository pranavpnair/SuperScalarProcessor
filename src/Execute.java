import java.util.Comparator;
import java.util.LinkedList;
import java.util.Queue;
import java.util.TreeSet;



public class Execute {
	TreeSet<Info> tmpreordbuffer;
	Queue<Info> alu,ldstr,branch;
	int branched,flag1,flag2,flag3;
	
	Execute(int[] latency, Comparator<Info> ins){
		branched = flag1 = flag2= flag3 =0;
		tmpreordbuffer = new TreeSet<Info>(ins);
		alu = new LinkedList<Info>();
		ldstr = new LinkedList<Info>();
		branch = new LinkedList<Info>();
		for(int i=1;i<latency[0];i++)
			alu.add(null);
		for(int i=1;i<latency[1];i++)
			ldstr.add(null);
		for(int i=1;i<latency[2];i++)
			branch.add(null);
	}
	
	//do flush
	
	public int executetmp(TreeSet<Info>[] resvnstations, TreeSet<Info> storebuffer, int[] Data, int pc){
		if(resvnstations[0].size() > 0){
			alu.add(resvnstations[0].pollFirst());
			flag1= 1;
		}
		if(!alu.isEmpty() && flag1 == 1){
			Info i = alu.peek();
			if(i!=null){
				switch(i.instype){
				case 0: i.aluoutput = i.op1 + i.op2;break;
				case 1: i.aluoutput = i.op1 - i.op2;break;
				case 2: i.aluoutput = i.op1* i.op2; break;
				}
				tmpreordbuffer.add(alu.poll());
			}
			else{
				alu.poll();
			}
		}
		if(resvnstations[1].size() > 0){
			ldstr.add(resvnstations[1].pollFirst());
			flag2= 1;
		}
		if(!ldstr.isEmpty() && flag2 ==1){
			Info i = ldstr.peek();
			if(i!=null){
				switch(i.instype){
				case 3: i.aluoutput = i.op2;break;
				case 4:	i.aluoutput = i.op1;
						break;
				}
				tmpreordbuffer.add(ldstr.poll());
			}
			else{
				ldstr.poll();
			}
		}
		if(resvnstations[2].size() > 0){
			branch.add(resvnstations[2].pollFirst());
			flag3= 1;
		}
		if(!branch.isEmpty() && flag3 ==1){
			Info i = branch.peek();
			if(i!=null){
				switch(i.instype){
				case 5: i.aluoutput = i.pc + (i.label<<1)-2 ;
						branched = 1;
						break;
				case 6: if(i.op1 == 0){
							i.aluoutput = i.pc + (i.label<<1)-2;
							branched = 1;
						}
						break;
				case 7: return -1;
				}
				tmpreordbuffer.add(branch.poll());
				return i.aluoutput -1;
			}
			else{
				branch.poll();
			}
		}
		return pc;
	}
	
	public int executeperm(TreeSet<Info> reordbuffer,TreeSet<Info> storebuffer){
		int instno;
		while(!tmpreordbuffer.isEmpty()){	
			if(tmpreordbuffer.first().instype == 4){
				System.out.println("Execute Stage instruction no: " + String.valueOf(tmpreordbuffer.first().instno));
				storebuffer.add(tmpreordbuffer.first());
				reordbuffer.add(tmpreordbuffer.pollFirst());
			}
			else if(tmpreordbuffer.first().instype == 5 || (tmpreordbuffer.first().instype == 6 && tmpreordbuffer.first().op1 ==0)){
				System.out.println("Execute Stage instruction no: " + String.valueOf(tmpreordbuffer.first().instno));
				reordbuffer.add(tmpreordbuffer.first());
				instno = tmpreordbuffer.pollFirst().instno;
				return instno;
			}
			else{
				System.out.println("Execute Stage instruction no: " + String.valueOf(tmpreordbuffer.first().instno));
				reordbuffer.add(tmpreordbuffer.pollFirst());
			}
		}
		return -1;
	}
	
	public void flush(TreeSet<Info> reorderbuffer, int instno){
		tmpreordbuffer.clear();
		for(Info i: reorderbuffer){
			if(i.instno > instno)
			reorderbuffer.remove(i);
		}
	}
}
