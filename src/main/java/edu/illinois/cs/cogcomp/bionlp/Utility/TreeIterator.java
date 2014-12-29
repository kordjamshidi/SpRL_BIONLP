package edu.illinois.cs.cogcomp.bionlp.Utility;


import java.util.Iterator;


//Pre-order, depth-first, left-to-right tree iterator.

public class TreeIterator implements Iterator<TreeNode> {

	private TreeNode curr;

	public TreeIterator(TreeNode top) {
		curr = top;
	}

	public boolean hasNext() {
		return curr != null;
	}

	public TreeNode next() {
		TreeNode result = curr;
		// First look for children
		if (curr.getChildren().length!=0)
			curr = curr.getChildren()[0];
		else {
			// Then go up
			boolean found = false;
			while (!found && curr!=null) {
				if (curr.getParent()!=null && curr.getParent().getNextChild(curr) != null) {
					curr = curr.getParent().getNextChild(curr);
					found = true;
				} else
					curr = curr.getParent();
			}
		}
		return result;
	}

	public void remove() {
		throw new IllegalStateException("This method is not implemented...");
	}

}
