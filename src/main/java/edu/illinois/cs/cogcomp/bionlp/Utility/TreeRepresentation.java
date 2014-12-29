package edu.illinois.cs.cogcomp.bionlp.Utility;


import java.util.Vector;

/**
 * You might want to read the information about MappedTreeNode in the file MappedTreeNode.java
 * 
 */

public class TreeRepresentation {

	private static boolean redoSpecialCharacters ;
	
	public static TreeNode readTree(String tree) {
		if (tree.charAt(0) != '(')
			throw new IllegalArgumentException("Malformed parse tree : " + tree);
		int endOfLabel = tree.indexOf(' ');
		String label = tree.substring(1, endOfLabel);
		int end = getPosOfClosingBracket(tree);
		String remainder = tree.substring(endOfLabel + 1, end);
		if (remainder.charAt(0) == '(') {
			boolean finished = false;
			Vector<TreeNode> children = new Vector<TreeNode>();
			while (!finished) {
				int end2 = getPosOfClosingBracket(remainder);
				String child = remainder.substring(0, end2 + 1);
				children.add(readTree(child));
				if (end2 < remainder.length() - 1)
					remainder = remainder.substring(end2 + 2);
				else
					finished = true;
			}
			TreeNode[] childs = new TreeNode[children.size()];
			children.toArray(childs);
			return new TreeNode(label, childs);
		} else {
			String word = remainder;
			if (redoSpecialCharacters)
				word = replaceSpecialCharacters(word);
			return new TreeNode(label, word);
		}
	}
	private static String replaceSpecialCharacters(String word) {
		String lrb = new String("(");
		String rrb = new String(")");
		String quot = new String("\"");
		if (word.equalsIgnoreCase("-lrb-"))
			return lrb;
		else if (word.equalsIgnoreCase("-rrb-"))
			return rrb;
		else if (word.equals("``"))
			return quot;
		else if (word.equals("''"))
			return quot;
		else
			return word;
	}

	private static String putSpecialCharacters(String word) {
		String lrb = new String("-lrb-");
		String rrb = new String("-rrb-");
		String quot = new String("``");
		if (word.equalsIgnoreCase("("))
			return lrb;
		else if (word.equalsIgnoreCase(")"))
			return rrb;
		else if (word.equals("\""))
			return quot;
		else
			return word;

	}

	private static int getPosOfClosingBracket(String input) {
		int pos = 0;
		int numOfBrackets = 0;
		while (pos < input.length()) {
			char curr = input.charAt(pos);
			if (curr == '(')
				numOfBrackets++;
			else if (curr == ')')
				numOfBrackets--;
			if (numOfBrackets == 0)
				return pos;
			pos++;
		}
		return -1;
	}

	
	public static String transformToText(TreeNode node) {
		if (node.hasWord()) {
			return "(" + node.getLabel() + " " + putSpecialCharacters(node.getWord()) + ")";
		} else {
			String result = "(" + node.getLabel() + " ";
			for (int i = 0; i < node.getChildren().length; i++) {
				result += transformToText(node.getChildren()[i]);
				if (i != node.getChildren().length - 1)
					result += " ";
			}
			result += ")";
			return result;
		}
	}

}


