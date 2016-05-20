
public class FPTreeDemo {

	public static void main(String[] args) {
		FPTree tree = new FPTree();
		tree.setDataPath("./FPTreeDemoData.txt");
		tree.setSplitter("\t");
		tree.setSupportCount(2);
		tree.startBuildTree();
		tree.showFrequentPattern();
	}

}
