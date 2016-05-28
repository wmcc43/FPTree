
public class FPTreeDemo {

	public static void main(String[] args) {
		if(args.length!=3){
			System.out.println("Usage:\n\tjava FPTreeDemo FILEPATH SPLITER(regex) MINSUPPORT");
			System.exit(-1);
		}
		long start = System.currentTimeMillis();
		FPTree tree = new FPTree();
		tree.setDataPath(args[0]);
		tree.setSplitter(args[1]);
		tree.setSupportCount(Integer.parseInt(args[2]));
		tree.startBuildTree();
		tree.showFrequentPattern();
		long end = System.currentTimeMillis();
		System.out.println("total execution time : "+(end-start)+" ms");
		System.out.println("total used memory : "+(Runtime.getRuntime().totalMemory()-Runtime.getRuntime().freeMemory())/(1024*1024)+" MB");
	}
}
