
public class Info {
	int instruction;
	int pc;
	int instype;
	int immbit;
	int  immvalue;
	int  dest;
	int  src1;
	int  src2;
	int  op1;
	int  op2;
	int opdest;
	int instno;
	int label;
	int aluoutput;
	public int  getbits(int n, int start, int end){
		int tempValue = n << (31 - end);
		return (tempValue >>> (31 - end + start));
	}
}
