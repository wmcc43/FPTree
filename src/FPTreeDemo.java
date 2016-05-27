
public class FPTreeDemo {

	public static void main(String[] args) {
		long start = System.currentTimeMillis();
		FPTree tree = new FPTree();
		tree.setDataPath("./t2.txt");
		tree.setSplitter("[:,]+");
		tree.setSupportCount(15);
		tree.startBuildTree();
		tree.showFrequentPattern();
		long end = System.currentTimeMillis();
		System.out.println("total execution time : "+(end-start)+" ms");
		System.out.println("total used memory : "+(Runtime.getRuntime().totalMemory()-Runtime.getRuntime().freeMemory())/(1024*1024)+" MB");
	}
}
