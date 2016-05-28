import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.TreeMap;

public class FPTree {
	TreeMap<String, LinkedList<String>> originData;
	TreeMap<String, LinkedList<itemTableElement>> desireData;
	HashMap<String, FPNode> headTable;
	HashMap<String, HashMap<String, FPNodeCount>> pattern;
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
		generatePattern();
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
			if(!itemList.isEmpty())
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
	
	private void generatePattern(){
		pattern = new HashMap<String, HashMap<String, FPNodeCount>>();
		headTable.forEach((item, fpnode)->{
			FPNode t = fpnode;
			FPNode current = fpnode;
			HashMap<String, FPNodeCount> itemPattern = new HashMap<>();
			HashMap<FPNode, FPNodeCount> conditionalFPTree = new HashMap<>();
			while(t!=null){
				while(t.parent!=root){
					if(conditionalFPTree.containsKey(t.parent))
						conditionalFPTree.get(t.parent).count += current.occurCount;
					else
						conditionalFPTree.put(t.parent, new FPNodeCount(t.parent, current.occurCount));
					t = t.parent;
				}
				t = current.sameIDSideNode;
				current = t;
			}
			
			t = fpnode;
			current = fpnode;
			ArrayList<FPNodeCount> path = new ArrayList<>();
			while(t!=null){
				path.removeAll(path);
				while(t.parent!=root){
					FPNodeCount count = conditionalFPTree.get(t.parent);
					if(count.count>=supportCount)
						path.add(count);
					if(t.parent.parent==root){
						itemPattern = minePattern(path, current, itemPattern);
					}
					t = t.parent;
				}
				t = current.sameIDSideNode;
				current = t;
			}
			pattern.put(item, itemPattern);
		});
	}
	
	private HashMap<String, FPNodeCount> minePattern(ArrayList<FPNodeCount> path, FPNode current, HashMap<String, FPNodeCount> itemPattern){
		long maxCombination = (long)Math.pow(2.0, (double)(path.size()));
		long chooser;
		StringBuilder patternNameBuilder = new StringBuilder();
		String patternName;
		for(long i=1; i < maxCombination; i++){
			patternNameBuilder.delete(0, patternNameBuilder.length());
			chooser = 1;
			while(chooser < maxCombination){
				if((chooser & i) != 0){
					int flag = (int)(Math.log((double)chooser)/Math.log(2.0));
					patternNameBuilder.append(path.get(flag).fpnode.itemName).append(",");
				}
				chooser = chooser << 1;
				if(chooser <= 0){
					System.out.println("chooser = "+chooser);
				}
			}
			patternNameBuilder.append(current.itemName);
			patternName = patternNameBuilder.toString();
			if(!itemPattern.containsKey(patternName))
				itemPattern.put(patternName, new FPNodeCount(current, current.occurCount));
			else{
				FPNodeCount count = itemPattern.get(patternName);
				count.count += current.occurCount;
			}
		}
		return itemPattern;
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
	
	public void showFrequentPattern(){
		pattern.forEach((itemName, p)->{
			if(!p.isEmpty()){
				System.out.print(itemName+"{");
				p.forEach((name,count) -> System.out.print("<"+name+":"+count.count+"> "));
				System.out.println("}");
			}
		});
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
	
	private class FPNodeCount{
		FPNode fpnode;
		int count;
		
		public FPNodeCount(FPNode fpnode, int count){
			this.fpnode = fpnode;
			this.count = count;
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
