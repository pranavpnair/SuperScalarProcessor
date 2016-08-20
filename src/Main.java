
import java.util.Scanner;



public class Main {
	
	public static void main(String[] args){
		int cycles = 0;
		Pipeline pipe = new Pipeline();
		Scanner in = new Scanner(System.in);
		System.out.println("Enter the value of n:");
		pipe.n = in.nextInt();
		System.out.println("Enter the reservation station sizes:");
		for(int i=0;i<3;i++){
			pipe.resvnsize[i] = in.nextInt();
		}
		System.out.println("Enter the reorder buffer size:");
		pipe.reordbuffsize = in.nextInt();
		System.out.println("Enter the store buffer size:");
		pipe.storebuffsize = in.nextInt();
		System.out.println("Enter the latencies of ALU unit:");
		for(int i=0;i<3;i++)
			pipe.latency[i] = in.nextInt();
		String test;
		System.out.println("Enter the test case:");
		test = in.next();
	//	System.out.println("Enter filepath containing instructions: ");
		pipe.readInstructions("C:/Users/pranav_2/Desktop/tc_assignment_5/" + test);
		in.close();
		pipe.initPipeline();
		while(!pipe.terminateprogram(cycles)){
			if(pipe.flush == 1)
				pipe.flush = 0;
			cycles++;
			System.out.println("Cycle no: " + String.valueOf(cycles));
			pipe.updatetmp();
			pipe.update();
		}
		System.out.println("No. of cycles = " + String.valueOf(cycles));
		System.out.println("CPI = " + String.valueOf(pipe.instructions.size()*1.0/cycles));
	}
}
