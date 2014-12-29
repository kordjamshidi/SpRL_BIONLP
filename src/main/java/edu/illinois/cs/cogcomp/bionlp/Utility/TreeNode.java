package edu.illinois.cs.cogcomp.bionlp.Utility;


import java.util.Collections;
import java.util.Vector;

public class TreeNode implements Comparable<TreeNode> {

	private String label;

	private String word;

	private TreeNode parent;

	private TreeNode[] children;

	public TreeNode(String label, String word) {
		this.label = label;
		this.word = word;
		children = new TreeNode[0];
	}

	public TreeNode(String label, TreeNode... children) {
		this.label = label;
		this.children = children;
		for (int i = 0; i < children.length; i++)
			children[i].setParent(this);
	}

	protected void setParent(TreeNode parent) {
		this.parent = parent;
	}

	public int getNumberOfNodesInTree() {
		int sum = 1;
		for (int i = 0; i < getChildren().length; i++)
			sum += getChildren()[i].getNumberOfNodesInTree();
		return sum;
	}

	public int getNumberOfAscendants() {
		int sum = getChildren().length;
		for (int i = 0; i < getChildren().length; i++)
			sum += getChildren()[i].getNumberOfAscendants();
		return sum;
	}

	public int getNumOfLeafNodes() {
		if (isLeafNode())
			return 1;
		else {
			int total = 0;
			for (TreeNode child : children)
				total += child.getNumOfLeafNodes();
			return total;
		}
	}

	public Vector<TreeNode> getLeafNodes() {
		Vector<TreeNode> result = new Vector<TreeNode>();
		if (isLeafNode()) {
			result.add(this);
		} else {
			for (TreeNode child : children)
				result.addAll(child.getLeafNodes());
		}
		return result;
	}

	private boolean isLeafNode() {
		return children.length == 0;
	}

	public int getDepth() {
		if (getParent() == null)
			return 0;
		else
			return getParent().getDepth() + 1;
	}

	public int getLength() {
		return getNumOfLeafNodes();
	}

	public TreeNode getNodeWithSpan(int start, int end) {
		Pair<Integer, Integer> coords = getSpan();
		if (coords.getFirst() == start && coords.getSecond() == end)
			return this;
		else {
			for (int i = 0; i < children.length; i++) {
				Pair<Integer, Integer> childCo = children[i].getSpan();
				if (start >= childCo.getFirst() && end <= childCo.getSecond())
					return children[i].getNodeWithSpan(start, end);
			}
			throw new IllegalArgumentException("No child found for span " + start + " - " + end);
		}

	}

	public Pair<Integer, Integer> getSpan() {
		if (getParent() == null)
			return new Pair<Integer, Integer>(0, getLength());
		else {
			int before = getParent().getSpan().getFirst();
			TreeNode[] children = getParent().getChildren();
			int i = 0;
			while (i < children.length && children[i] != this) {
				before += children[i].getNumOfLeafNodes();
				i++;
			}
			return new Pair<Integer, Integer>(before, before + getLength());
		}
	}

	public TreeNode getNextChild(TreeNode currChild) {
		int index = 0;
		while (index < getChildren().length && getChildren()[index] != currChild)
			index++;
		if (index >= getChildren().length - 1)
			return null;
		else
			return getChildren()[index + 1];
	}

	public TreeNode getPrevChild(TreeNode currChild) {
		int index = 0;
		while (index < getChildren().length && getChildren()[index] != currChild)
			index++;
		if (index == 0)
			return null;
		else
			return getChildren()[index - 1];
	}

	public void addChild(int pos, TreeNode child) {
		TreeNode[] newChildren = new TreeNode[children.length + 1];
		for (int i = 0; i < pos; i++)
			newChildren[i] = children[i];
		newChildren[pos] = child;
		child.setParent(this);
		for (int i = pos + 1; i < newChildren.length; i++)
			newChildren[i] = children[i - 1];
		children = newChildren;
	}

	// private HashMap<TreeNode,String> pathCache ;

	public void getPath(TreeNode end, Vector<String> parts) {
		getPath(this, end, parts);
	}

	public String getPath(TreeNode end) {
		String result = "";
		Vector<String> parts = new Vector<String>();
		getPath(end, parts);
		for (String part : parts)
			result += part;
		return result;
	}

	// public void removeCache() {
	// pathCache = null ;
	// }

	public static void getPath(TreeNode start, TreeNode end, Vector<String> parts) {
		TreeNode commonParent = TreeNode.getCommonParent(start, end);
		TreeNode curr = end;
		while (curr != commonParent) {
			parts.add("↓" + curr.getLabel());
			curr = curr.getParent();
		}
		String middle = "↑" + commonParent.getLabel() + "↓";
		parts.add(middle);
		curr = start;
		while (curr != commonParent) {
			parts.add(curr.getLabel() + "↑");
			curr = curr.getParent();
		}
		Collections.reverse(parts);
	}

	// public String getPath(TreeNode start, TreeNode end) {
	// // assert (hasChild(start) || start==this);
	// // assert (hasChild(end) || end==this);
	// // assert(!start.hasChild(end)) ;
	//
	// TreeNode commonParent = TreeNode.getCommonParent(start, end);
	// if (commonParent != this)
	// return commonParent.getPath(start, end);
	//
	// boolean diffChildren = true;
	// for (int i = 0; i < children.length && diffChildren; i++) {
	// TreeNode child = children[i];
	// if (child.hasChild(start) && child.hasChild(end)) {
	// diffChildren = false;
	// }
	// }
	// if (diffChildren) {
	// String startPath = "";
	// String endPath = "";
	// for (int i = 0; i < children.length; i++) {
	// TreeNode child = children[i];
	// if (child.hasChild(start) || start == child)
	// startPath = child.getPath(start, child) + "↑";
	// if (child.hasChild(end) || end == child)
	// endPath = "↓" + child.getPath(child, end);
	// diffChildren = false;
	//
	// }
	// String middle = getLabel();
	// return startPath + middle + endPath;
	// } else {
	// for (int i = 0; i < children.length; i++) {
	// TreeNode child = children[i];
	// if (child.hasChild(start) && child.hasChild(end)) {
	// return child.getPath(start, end);
	// }
	// }
	// }
	// throw new IllegalStateException("");
	// }

	public String getPath(TreeNode start, TreeNode end, boolean orderInfo) {
		String path = start.getPath(end);
		if (orderInfo)
			return comesBefore(start, end) + " " + path;
		else
			return path;
	}

	public boolean hasChild(TreeNode child) {
		for (int i = 0; i < children.length; i++) {
			TreeNode curr = children[i];
			if (curr == child)
				return true;
			else if (curr.hasChild(child))
				return true;
		}
		return false;
	}

	public TreeNode findMaximumNode(String words) {
		TreeIterator it = new TreeIterator(this);
		while (it.hasNext()) {
			TreeNode curr = it.next();
			if (curr.getWords().equals(words))
				return curr;
		}
		System.err.println("Node : " + words);
		System.err.println("Not found in tree : " + this);
		throw new IllegalArgumentException("No node found for string \"" + words + "\"");
	}

	public TreeNode[] getChildren() {
		return children;
	}

	public String getLabel() {
		return label;
	}

	public TreeNode getParent() {
		return parent;
	}

	public String getWord() {
		return word;
	}

	public void setWord(String word) {
		this.word = word;
	}

	public String toString() {
		return toString("");
	}

	protected String toString(String start) {
		String result = start + " + ";
		result += label;
		if (getWord() != null) {
			result += " \"" + getWord() + "\"\n";
		} else {
			result += "\n";
			for (TreeNode child : getChildren()) {
				result += child.toString(start + " |");
			}
		}
		return result;
	}

	public Vector<String> getAllWords() {
		Vector<String> result = new Vector<String>();
		if (getChildren().length == 0)
			result.add(getWord());
		else {
			for (int i = 0; i < getChildren().length; i++) {
				TreeNode child = getChildren()[i];
				result.addAll(child.getAllWords());
			}
		}
		return result;
	}

	public String getWords() {
		if (getChildren().length == 0)
			return getWord();
		else {
			String result = "";
			for (int i = 0; i < getChildren().length; i++) {
				TreeNode child = getChildren()[i];
				result += child.getWords();
				if (i < getChildren().length - 1)
					result += " ";
			}
			return result.trim();
		}
	}

	public Vector<TreeNode> getAllNodesOfTree() {
		Vector<TreeNode> result = new Vector<TreeNode>();
		result.add(this);
		for (int i = 0; i < getChildren().length; i++) {
			result.addAll(getChildren()[i].getAllNodesOfTree());
		}
		return result;
	}

	public boolean hasWord() {
		return word != null;
	}

	public int compareTo(TreeNode o) {
		assert (o != null);
		if (label.equals(o.getLabel())) {
			if (hasWord() && !o.hasWord()) {
				return 1;
			} else if (!hasWord() && o.hasWord())
				return -1;
			else {
				int comp = getChildren().length - o.getChildren().length;
				if (comp != 0)
					return comp;
				else {
					for (int i = 0; i < getChildren().length && comp == 0; i++)
						comp = getChildren()[i].compareTo(o.getChildren()[i]);
					return comp;
				}
			}
		} else
			return label.compareTo(o.getLabel());
	}

	public boolean comesBefore(TreeNode other) {
		return getTopOfTree().comesBefore(this, other) > 0;
	}

	public TreeNode getTopOfTree() {
		if (getParent() == null)
			return this;
		else
			return getParent().getTopOfTree();
	}

	public int comesBefore(TreeNode first, TreeNode second) {
		int posFirst = -1;
		int posSecond = -1;
		for (int i = 0; i < children.length; i++) {
			TreeNode child = children[i];
			if (child == first || child.hasChild(first))
				posFirst = i;
			if (child == second || child.hasChild(second))
				posSecond = i;
		}
		if (posFirst == -1 || posSecond == -1)
			return 0;
		if (posFirst < posSecond)
			return 1;
		else if (posFirst == posSecond)
			return children[posFirst].comesBefore(first, second);
		else
			return -1;
	}

	public boolean equals(TreeNode other) {
		if (!other.getLabel().equals(getLabel()))
			return false;
		if (hasWord()) {
			if (!other.hasWord())
				return false;
			else
				return getWord().equals(other.getWord());
		}
		if (getChildren().length != other.getChildren().length)
			return false;
		for (int i = 0; i < getChildren().length; i++)
			if (!getChildren()[i].equals(other.getChildren()[i]))
				return false;
		return true;
	}
	
    public boolean hasVPAncestor()
    { // indicates whether a node has a parent of 
    	if (getParent()!=null)
    	  {
    	   if (getParent().label.startsWith("V"))
    		 return true;
    	   else 
    	     return getParent().hasVPAncestor();}
    	 else 
    		return false;
   }
    
   public boolean hasAncestor(TreeNode ancestor) {
		if (getParent() == ancestor)
			return true;
		else if (getParent() == null)
			return false;
		else
			return getParent().hasAncestor(ancestor);
	}

	public boolean hasDescendant(TreeNode descendant) {
		for (TreeNode child : getChildren())
			if (child == descendant || child.hasDescendant(descendant))
				return true;
		return false;
	}

	public static String getCombinedLabel(Vector<TreeNode> nodes) {
		String curr = "";
		for (int i = 0; i < nodes.size(); i++) {
			curr += nodes.get(i).getLabel();
			if (i != nodes.size() - 1)
				curr += "_";
		}
		return curr;
	}

	public static TreeNode getCommonParent(TreeNode node1, TreeNode node2) {
		TreeNode parent = node1;
		while (parent != null) {
			if (node2 == parent || node2.hasAncestor(parent))
				return parent;
			parent = parent.getParent();
		}
		throw new IllegalStateException("Nodes " + node1 + " and " + node2 + " do not have a common ancestor!");
	}

	public static TreeNode getCommonParent(Vector<TreeNode> nodes) {
		TreeNode parent = nodes.get(0);
		for (int i = 1; i < nodes.size() && parent != null; i++) {
			TreeNode node = nodes.get(i);
			while (parent != null && node != parent && !node.hasAncestor(parent))
				parent = parent.getParent();
		}
		return parent;
	}

	public static boolean isVerbPhrase(TreeNode node) {
		return node.getLabel().charAt(0) == 'V';
	}

	public static boolean isNounPhrase(TreeNode node) {
		return node.getLabel().charAt(0) == 'N';
	}

	public static void getSequentialPath(TreeNode startNode, TreeNode endNode, Vector<String> path) {
		Vector<TreeNode> allLeafs = startNode.getTopOfTree().getLeafNodes();
		boolean normalOrder = startNode.comesBefore(endNode);
		TreeNode first = normalOrder ? startNode : endNode;
		TreeNode last = normalOrder ? endNode : startNode;
		for (int i = 0; i < allLeafs.size(); i++) {
			TreeNode curr = allLeafs.get(i);
			if (!curr.comesBefore(first) && !last.comesBefore(curr))
				path.add(curr.getLabel());
		}
		if (!normalOrder)
			Collections.reverse(path);
	}

	public static int getPathDistance(TreeNode start, TreeNode end) {
		int dist = 0 ; 
		TreeNode commonParent = TreeNode.getCommonParent(start, end);
		TreeNode curr = end;
		while (curr != commonParent) {
			dist ++ ; 
			curr = curr.getParent();
		}
		curr = start;
		while (curr != commonParent) {
			dist ++ ; 
			curr = curr.getParent();
		}
		return dist ; 
	}

}
