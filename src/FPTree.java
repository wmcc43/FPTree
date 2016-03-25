import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.TreeMap;

public class FPTree {
	TreeMap<String, LinkedList<String>> originData;
	TreeMap<String, LinkedList<itemTableElement>> desireData;
	LinkedList<itemTableElement> itemTable;
	HashMap<String, LinkedList<FPNode>> headTable;
	FPNode root;
	int supportCount;
	String dataPath;
	String splitter;
	
	public FPTree(){
		originData = new TreeMap<>();
		headTable = new HashMap<>();
	}
	
	public void startBuildTree(){
		try{
			readData();
		}
		catch(IOException e){
			e.printStackTrace();
		}
		System.out.println(originData.toString());
		eliminateLowerCountItem();
		System.out.println(originData.toString());
		System.out.println(itemTable.toString());
		buildTree();
		System.out.println(root);
	}
	
	private void readData() throws IOException {
		FileReader input= new FileReader(dataPath);
		BufferedReader bf = new BufferedReader(input);
		String temp = bf.readLine();
		while(temp!=null){
			String s[] = temp.split(splitter);
			LinkedList<String> item = new LinkedList<>();
			for(int i=1;i<s.length;i++)
				item.add(s[i]);
			originData.put(s[0], item);
			temp=bf.readLine();
		}
	}
	
	private void eliminateLowerCountItem(){
		TreeMap<String, itemTableElement> temp = new TreeMap<>();
		originData.forEach((ID,itemList)->{
			itemList.forEach(item->{
				if(temp.containsKey(item)){
					temp.get(item).count++;
				}
				else{
					temp.put(item, new itemTableElement(item, 1));
				}
			});
		});
		
		temp.forEach((item, element)->{
			if(element.count<supportCount){
				originData.forEach((ID, itemList)->{
					itemList.remove(item);
				});
			}
		});
		
		desireData = new TreeMap<>();
		originData.forEach((ID, itemList)->{
			LinkedList<itemTableElement> t = new LinkedList<>();
			itemList.forEach(item -> t.add(new itemTableElement(item, temp.get(item).count)));
			t.sort(null);
			desireData.put(ID, t);
		});
		
		System.out.println(desireData);
		
		itemTable = new LinkedList<>();
		temp.forEach((item, element) -> {
			if(element.count>=supportCount)
				itemTable.add(element);
		});
		itemTable.sort(null);
	}
	
	private void buildTree(){
		root = new FPNode();
		desireData.forEach((ID, itemList)->{
			buildChilds(root, itemList);
		});
	}
	
	private void buildChilds(FPNode root, List<itemTableElement> itemList){
		itemTableElement item = itemList.get(0);
		if(root.childs.containsKey(item.itemName)){
			FPNode itemNode = root.childs.get(item.itemName);
			itemNode.occurCount++;
			List<itemTableElement> nextList = itemList.subList(itemList.indexOf(item)+1, itemList.size());
			if(nextList.isEmpty())
				return;
			buildChilds(itemNode, nextList);
		}
		else{
			FPNode t = new FPNode();
			t.itemName = item.itemName;
			t.occurCount++;
			t.parent = root;
			root.childs.put(item.itemName,t);
			List<itemTableElement> nextList = itemList.subList(itemList.indexOf(item)+1, itemList.size());
			if(nextList.isEmpty())
				return;
			buildChilds(t, nextList);
		}
	}
	
	public void setSupportCount(int supportCount){
		this.supportCount = supportCount;
	}
	
	public void setDataPath(String dataPath){
		this.dataPath = dataPath;
	}
	
	public void setSplitter(String splitter){
		this.splitter = splitter;
	}
	
	private class itemTableElement implements Comparable<itemTableElement>{
		String itemName;
		int count;
		public itemTableElement(String item,int count){
			this.itemName = item;
			this.count = count;
		}
		
		@Override
		public int compareTo(itemTableElement o) {
			if(this.count < o.count)
				return 1;
			else if(this.count > o.count)
				return -1;
			else
				return 0;
		}
		
		@Override
		public String toString(){
			return "["+itemName+":"+count+"]";
		}
	}
	
	private class FPNode{
		FPNode parent;
		FPNode sameIDSideNode;
		HashMap<String,FPNode> childs;
		int occurCount;
		String itemName;
		
		FPNode(){
			parent = null;
			sameIDSideNode = null;
			childs = new HashMap<>();
			occurCount = 0;
			itemName = null;
		}
	}
}
