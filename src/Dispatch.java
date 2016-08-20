
import java.util.Comparator;
import java.util.TreeSet;


public class Dispatch {
	TreeSet<Info>[] tmpstations;
	TreeSet<Info> poppedInfo;
	TreeSet<Info> tmp;
	int hazard;
	int flag1,flag2;
	int tmpbusy[];
	Dispatch(Comparator<Info> ins){
		tmpstations = new TreeSet[3];
		tmp = new TreeSet<Info>(ins);
		hazard = 0;
		poppedInfo = new TreeSet<Info>(ins);
		tmpbusy = new int[16];
		for(int i=0;i<3;i++)
			tmpstations[i] = new TreeSet<Info>(ins);
	}
	
	public void flush(TreeSet<Info>[] resvnstations, int instno){
		for(int k=0;k<3;k++){
			tmpstations[k].clear();
			for (Info i : resvnstations[k]){
				if(i.instno > instno)
					resvnstations[k].remove(i);
			}
		}	
	}
	
	public void dispatchtmp(TreeSet<Info> dispatchbuffer, int resvnsize[], int busy[], int[] registerFile,int n){
		flag1 = flag2 = 0;
		for(int i=0; i<16;i++)
			tmpbusy[i] = busy[i];
		for(Info i: dispatchbuffer){
			if(flag2 == 1)
				break;
			switch(i.instype){
				case 0:
				case 1:
				case 2: if(tmpstations[0].size() <= resvnsize[0])
						{
							tmp.add(i);
							if(i.immbit == 0){
								if((tmpbusy[i.src1] == 2 || tmpbusy[i.src2] == 2) || ((tmpbusy[i.dest] == 1 || tmpbusy[i.dest] == 2)&& n!=1)){
									if(tmpbusy[i.src1] != 2)
										tmpbusy[i.src1] = 1;
									if(tmpbusy[i.src2] != 2)
										tmpbusy[i.src2] = 1;
									tmpbusy[i.dest] = 2;
									poppedInfo.add(i);
									hazard += 1;
									if(tmpbusy[i.src1] == 2 || tmpbusy[i.src2] == 2)
										System.out.println("RAW Hazard at instruction: " + i.instno);
									else if(tmpbusy[i.dest] == 1)
										System.out.println("WAR Hazard at instruction: " + i.instno);
									else 
										System.out.println("WAW Hazard at instruction: " + i.instno);
								}
								else{
									i.op1 = registerFile[i.src1];
									i.op2 = registerFile[i.src2];								
									tmpbusy[i.dest] = 2;
									tmpstations[0].add(i);
								}
							}
							else{
								if(tmpbusy[i.src1] == 2 || ((tmpbusy[i.dest] == 1 || tmpbusy[i.dest] == 2)&& n!=1)){
									if(tmpbusy[i.src1] != 2)
										tmpbusy[i.src1] = 1;
									tmpbusy[i.dest] = 2;
									poppedInfo.add(i);
									hazard += 1;
									if(tmpbusy[i.src1] == 2)
										System.out.println("RAW Hazard at instruction: " + i.instno);
									else if(tmpbusy[i.dest] == 1)
										System.out.println("WAR Hazard at instruction: " + i.instno);
									else
										System.out.println("WAW Hazard at instruction: " + i.instno);
								}
								else{
									i.op1 = registerFile[i.src1];
									i.op2 = i.immvalue;								
									tmpbusy[i.dest] = 2;
									tmpstations[0].add(i);
								}
							}
						}
					break;
				case 3: if(tmpstations[1].size() < resvnsize[1]){
						tmp.add(i);
					
					if(((tmpbusy[i.dest] == 1 || tmpbusy[i.dest] == 2)&& n!=1) || tmpbusy[i.src1] == 2){
						flag1 = 1;
						if(tmpbusy[i.src1]!= 2)
							tmpbusy[i.src1] = 1;
						tmpbusy[i.dest] = 2;
						poppedInfo.add(i);
						hazard += 1;
						if(tmpbusy[i.src1] == 2)
							System.out.println("RAW Hazard at instruction: " + i.instno);
						else if(tmpbusy[i.dest] == 1)
							System.out.println("WAR Hazard at instruction: " + i.instno);
						else
							System.out.println("WAW Hazard at instruction: " + i.instno);
					}
					else{
						i.op2 = registerFile[i.src1];
						tmpbusy[i.dest] = 2;
						if(flag1 == 0 )
							tmpstations[1].add(i);
					}
				}
					break;
				case 4: if(tmpstations[1].size() < resvnsize[1]){
					tmp.add(i);
					if(tmpbusy[i.src1] == 2 || tmpbusy[i.src2] == 2){
						flag1 = 1;
						if(tmpbusy[i.src1]!= 2)
							tmpbusy[i.src1] = 1;
						if(tmpbusy[i.src2]!= 2)
							tmpbusy[i.src2] = 1;
						poppedInfo.add(i);
						hazard += 1;
						System.out.println("RAW Hazard at instruction: " + i.instno);
					}
					else{
						i.op1 = registerFile[i.src1];
						i.op2 = registerFile[i.src2];
						if(flag1 == 0)
							tmpstations[1].add(i);
					}
				}
					break;
				case 5: if(tmpstations[2].size() < resvnsize[2]){
					tmp.add(i);
					tmpstations[2].add(i);
				}
					break;
				case 6: if(tmpstations[2].size() < resvnsize[2]){
					tmp.add(i);
					if(tmpbusy[i.src1] != 2){
						tmpbusy[i.src1] = 1;
						tmpstations[2].add(i);
					}
					else{
						i.op1 = registerFile[i.src1];
						flag2 = 1;
						poppedInfo.add(i);
						hazard += 1;
						System.out.println("RAW Hazard at instruction: " + i.instno);
					}
				}
					break;
				case 7: tmp.add(i);
						tmpstations[2].add(i);
			}
		}
		
		for(Info i :tmp){
			dispatchbuffer.remove(i);
		}
		tmp.clear();
		if(!poppedInfo.isEmpty()){
			dispatchbuffer.addAll(poppedInfo);
			poppedInfo.clear();
		}
		
	}
	
	public void dispatchperm(TreeSet<Info> dispatchbuffer,TreeSet<Info>[] resvnstations,int resvnsize[],int busy[]){
		while(resvnstations[0].size() < resvnsize[0] && tmpstations[0].size()>0){
			System.out.println("Dispatch Stage instruction no: " + String.valueOf(tmpstations[0].first().instno));
			dispatchbuffer.remove(tmpstations[0].first());
			busy[tmpstations[0].first().dest] = 2;
			resvnstations[0].add(tmpstations[0].pollFirst());
		}
		while(resvnstations[1].size() < resvnsize[1] && tmpstations[1].size()>0){
			System.out.println("Dispatch Stage instruction no: " + String.valueOf(tmpstations[1].first().instno));
			dispatchbuffer.remove(tmpstations[1].first());
			if(tmpstations[1].first().instype == 3)
				busy[tmpstations[1].first().dest] = 2;
			resvnstations[1].add(tmpstations[1].pollFirst());
		}
		while(resvnstations[2].size() < resvnsize[2] && tmpstations[2].size()>0){
			System.out.println("Dispatch Stage instruction no: " + String.valueOf(tmpstations[2].first().instno));
			dispatchbuffer.remove(tmpstations[2].first());
			resvnstations[2].add(tmpstations[2].pollFirst());
		}
		if(!tmpstations[0].isEmpty()){
			dispatchbuffer.addAll(tmpstations[0]);
		}
		if(!tmpstations[1].isEmpty()){
			dispatchbuffer.addAll(tmpstations[1]);
		}
		if(!tmpstations[1].isEmpty()){
			dispatchbuffer.addAll(tmpstations[1]);
		}
		tmpstations[0].clear();
		tmpstations[1].clear();
		tmpstations[2].clear();
		poppedInfo.clear();
	}
}
