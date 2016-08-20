import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Comparator;
import java.util.TreeSet;
import java.util.Vector;


public class Pipeline {
	int halt;
	int flush;
	int halt1;
	int halt2;
	Vector<Integer> instructions;
	int Data[];
	int n,pc;
	int resvnsize[];
	TreeSet<Info>[] resvnstations = new TreeSet[3]; 
	int reordbuffsize;
	int busy[];
	int RegisterFile[];
	int storebuffsize;
	int latency[];
	TreeSet<Info> decodebuffer;
	TreeSet<Info> dispatchbuffer;
	TreeSet<Info> reorderbuffer;
	TreeSet<Info> storebuffer;
	Comparator<Info> ins;
	Fetch fetch;
	Decode decode;
	Dispatch dispatch;
	Execute execute;
	Complete complete;
	Retire retire;
	Pipeline(){
		flush = 0;
		ins = new InstructionComparator();
		Data = new int[1024];
		busy = new int[16];
		latency = new int[3];
		RegisterFile = new int[16];
		for(int i=0;i<16;i++)
			busy[i] = 0;
		pc = 0;
		halt = 0;
		instructions = new Vector<Integer>();
		resvnsize = new int[3];
		for(int i=0;i<3;i++)
			resvnstations[i] = new TreeSet<Info>(ins);
		decodebuffer = new TreeSet<Info>(ins);
		dispatchbuffer = new TreeSet<Info>(ins);
		reorderbuffer = new TreeSet<Info>(ins);
		storebuffer = new TreeSet<Info>(ins);
		
	}
	public boolean terminateprogram(int cycles){
		if(cycles == 0 || flush == 1)
			return false;
		if(decodebuffer.isEmpty() && dispatchbuffer.isEmpty() && reorderbuffer.isEmpty() && storebuffer.isEmpty())
			return true;
		return false;
	}
	
	
	public void initPipeline(){
		fetch = new Fetch(n,ins);
		decode = new Decode(ins);
		dispatch = new Dispatch(ins);
		execute = new Execute(latency,ins);
		complete = new Complete(ins);
		retire = new Retire(ins);
	}
	
	public void updatePC(int newpc){
		if(newpc == -1)
			halt1 = 1;
		else
			pc = newpc;
	}
	
	public void flushpipeline(int instno){
		fetch.flush(decodebuffer, instno);
		decode.flush(dispatchbuffer, instno);
		dispatch.flush(resvnstations, instno);
		execute.flush(reorderbuffer, instno);
		flush = 1;
	}
	
	
	public void update() {
		int l = fetch.fetchperm(decodebuffer, pc, instructions.size(),dispatch, execute);
		updatePC(l);
		decode.decodeperm(dispatchbuffer, n, decodebuffer);
		dispatch.dispatchperm(dispatchbuffer,resvnstations, resvnsize,busy);
		int k = execute.executeperm(reorderbuffer, storebuffer);
		if( k!= -1)
			flushpipeline(k);
		halt2 = complete.completeperm(RegisterFile, busy,halt1);
		halt = retire.retireperm(Data, halt2);
	}
	
	public void updatetmp() {
		fetch.fetchtmp(instructions, pc);
		decode.decodetmp(decodebuffer, n);
		dispatch.dispatchtmp(dispatchbuffer, resvnsize, busy, RegisterFile, n);
		updatePC(execute.executetmp(resvnstations, storebuffer, Data,pc));
		complete.completetmp(reorderbuffer);
		retire.retiretmp(storebuffer);
	}
	
	public void readInstructions(String filepath) {
		BufferedReader br = null;
		try {
		String sCurrentLine;
		br = new BufferedReader(new FileReader(filepath));

		while ((sCurrentLine = br.readLine()) != null) {
			instructions.add(Integer.parseInt(sCurrentLine.trim(), 2));
			//check this.
		}

	} catch (IOException e) {
		e.printStackTrace();
	} finally {
		try {
			if (br != null)br.close();
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}
		
	}
	
}
