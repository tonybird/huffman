package a1;

import java.util.ArrayList;
import java.util.Map;

public class Node implements Comparable<Node> {
	public Node l, r;
	public int s;
	public int count = 0;
	
	public Node() {}
	
	public Node getLeft() { return l; }
	public Node getRight() { return r; }
	public int getSymbol() { return s; }
	
	public boolean isLeaf() {
		return l == null && r == null;
	}
	
	public boolean isFull() {
		if (isLeaf()) return true;
		if (l == null || r == null) return false;
		return l.isFull() && r.isFull();
	}
	
	public void insert(int symbol, int depth) {
		if (depth == 0) {
			this.s = symbol;
		} else if (l == null) {
			l = new Node();
			l.insert(symbol, depth-1);
		} else if (!l.isFull()) {
			l.insert(symbol, depth-1);
		} else if (r == null) {
			r = new Node();
			r.insert(symbol, depth-1);
		} else if (!r.isFull()) {
			r.insert(symbol, depth-1);
		} else {
			throw new RuntimeException("Tree is already full");
		}
	}
	
	public String toString() {
		if (!isLeaf()) return "Internal node";
		return "Leaf: " + s;
	}
	
	public int getCount() {
		if (isLeaf()) return count;
		int freq = 0;
		if (l != null) freq += l.getCount();
		if (r != null) freq += r.getCount();
		return freq;
	}
	
	public int getHeight() {
		if (isLeaf()) {
			return 0;
		} else {
			int lHeight = l.getHeight();
			int rHeight = r.getHeight();
			if (lHeight > rHeight) {
				return lHeight+1;
			} else {
				return rHeight+1;
			}
		}
	}

	public int compareTo(Node o) {
		if (getCount() != o.getCount()) {
			return getCount() - o.getCount();
		} else {
			return getHeight() - o.getHeight();
		}
	}
	
	public void buildCodeStrings(Map<Integer, String> cmap, String code, int c) {
		if (s == c && isLeaf()) cmap.put((int) c, code);
		else {
			if (l != null) l.buildCodeStrings(cmap, code+"0", c);
			if (r != null) r.buildCodeStrings(cmap, code+"1", c);
		}
	}
	
}
