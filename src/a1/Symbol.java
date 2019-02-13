package a1;

public class Symbol implements Comparable<Symbol> {
	public int s;
	public int length = 0;
	public int count = 0;
	public double probability = 0;
	
	public Symbol(int symbol, int length) {
		this.s = symbol;
		this.length = length;
	}

	@Override
	public int compareTo(Symbol o) {
		if (o.count > this.count) return -1;
		if (o.count < this.count) return 1;
		
		if (o.length > this.length) return -1;
		if (o.length < this.length) return 1;
		return 0;
	}
	
	public String toString() {
		return  "symbol:"+s+" length:"+length;
	}

}
