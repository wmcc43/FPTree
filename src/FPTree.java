import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.TreeMap;

public class FPTree {
	TreeMap<String, LinkedList<String>> originData;
	TreeMap<String, LinkedList<itemTableElement>> desireData;
	HashMap<String, FPNode> headTable;
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
		eliminateLowerCountItem();
		buildTree();
		buildHeadTable();
		originData=null;
		desireData=null;
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
		bf.close();
		input.close();
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
	}
	
	private void buildTree(){
		root = new FPNode();
		root.childs = new HashMap<>();
		desireData.forEach((ID, itemList)->{
			buildChilds(root, itemList);
		});
	}
	
	private void buildChilds(FPNode root, LinkedList<itemTableElement> itemList){
		itemTableElement item = itemList.remove(0);
		FPNode itemNode;
		if(root.childs!=null && root.childs.containsKey(item.itemName)){
			itemNode = root.childs.get(item.itemName);
			itemNode.occurCount++;
			if(itemList.isEmpty())
				return;
			buildChilds(itemNode, itemList);
		}
		else{
			itemNode = new FPNode();
			itemNode.itemName = item.itemName;
			itemNode.occurCount++;
			itemNode.parent = root;
			if(root.childs==null)
				root.childs = new HashMap<>();
			root.childs.put(item.itemName,itemNode);
			if(itemList.isEmpty())
				return;
			buildChilds(itemNode, itemList);
		}
	}
	
	private void buildHeadTable(){
		root.childs.forEach((itemName, FPNode)->{
			traverseTree(FPNode);
		});
	}
	
	private void traverseTree(FPNode fpnode){
		if(headTable.containsKey(fpnode.itemName)){
			FPNode t = headTable.get(fpnode.itemName);
			while(t.sameIDSideNode!=null){
				t = t.sameIDSideNode;
			}
			t.sameIDSideNode=fpnode;
		}
		else{
			headTable.put(fpnode.itemName, fpnode);
		}
		if(fpnode.childs!=null){
			fpnode.childs.forEach((itemName, FPNode)->{
				traverseTree(FPNode);
			});
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
			occurCount = 0;
			itemName = null;
		}
		
		@Override
		public String toString(){
			return "["+itemName+":"+occurCount+"]";
		}
	}
}
