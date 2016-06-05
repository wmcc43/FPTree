import java.util.Scanner;

public class FPTreeDemo {

	public static void main(String[] args) {
		Scanner stdin = new Scanner(System.in);
		String arg[] = new String[4];
		if(args.length>0 && args.length!=4){
			System.out.println("Usage:\n\tjava FPTreeDemo FILEPATH SPLITTER(regex) MINSUPPORT(0.0~1.0) LoopWithChangeMinSup(y/n)");
			System.exit(0);
		}
		else if(args.length == 0){
			System.out.print("input file path:");
			arg[0] = stdin.next();
			System.out.print("file splitter(can use regex):");
			arg[1] = stdin.next();
			System.out.print("minimum support(0.0~1.0):");
			arg[2] = stdin.next();
			System.out.print("Wanna loop run with change minimum support?(y/n)");
			arg[3] = stdin.next();
		}
		else if(args.length==4){
			arg[0] = args[0];
			arg[1] = args[1];
			arg[2] = args[2];
			arg[3] = args[3];
		}
		run(arg);
		String choose;
		while(arg[3].toLowerCase().equals("y") || arg[3].toLowerCase().equals("Y")){
			System.out.print("Wanna change minimum support?(y/n)");
			choose = stdin.next();
			if(choose.toLowerCase().equals("y") || choose.toLowerCase().equals("yes")){
				System.out.print("input minimum support(0.0~1.0):");
				arg[2] = stdin.next();
				run(arg);
			}
			else
				break;
		}
	}
	
	static void run(String[] arg){
		long start = System.currentTimeMillis();
		FPTree tree = new FPTree();
		tree.setDataPath(arg[0]);
		tree.setSplitter(arg[1]);
		tree.setMinSupport(Double.parseDouble(arg[2]));
		tree.startBuildTree();
		tree.showFrequentPattern();
		long end = System.currentTimeMillis();
		System.out.println("total execution time : "+(end-start)+" ms");
		System.out.println("total used memory : "+(Runtime.getRuntime().totalMemory()-Runtime.getRuntime().freeMemory())/(1024*1024)+" MB");
	}
}
