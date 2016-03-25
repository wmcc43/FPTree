import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
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
		root.setParent(null);
		desireData.forEach((ID, itemList)->{
			itemList.forEach(item->{
				
			});
		});
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
		private FPNode parent;
		private FPNode sameIDSideNode;
		private HashMap<String,FPNode> childs;
		private int occurCount;
		private String ID;
		
		public void setParent(FPNode parent){
			this.parent = parent;
		}
		
		public FPNode getParent(){
			return parent;
		}
		
		public void setSameIDSideNode(FPNode sameIDSideNode){
			this.sameIDSideNode = sameIDSideNode;
		}
		
		public FPNode getSameIDSideNode(){
			return sameIDSideNode;
		}
		
		public FPNode getChild(String ID){
			if(childs.containsKey(ID))
				return childs.get(ID);
			else
				return null;
		}
		
		public void addChild(FPNode child){
			childs.put(child.ID, child);
		}
		
		public void setOccurCount(int occurCount){
			this.occurCount = occurCount;
		}
		
		public int getOccurCount(){
			return occurCount;
		}
		
		public void setID(String ID){
			this.ID = ID;
		}
		
		public String getID(){
			return ID;
		}
	}
}
