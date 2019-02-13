package a1;
import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import io.*;

public class Decode {

	public static void main(String[] args) throws IOException, InsufficientBitsLeftException {
		
		InputStreamBitSource bits = new InputStreamBitSource(new FileInputStream("data/compressed.dat"));
		ArrayList<Symbol> symbols = new ArrayList<Symbol>();
		for (int i = 0; i<256; i++) {
			symbols.add(new Symbol((char) i, bits.next(8)));
		}
		providedEntropy(symbols);
		Collections.sort(symbols);
		int numsymbols = bits.next(32);
		
		// create canonical Huffman tree from lengths
		Node root = new Node();
		for (Symbol s : symbols) {
			root.insert(s.s, s.length);
		}
		
		// decode remaining bits and write to file
		PrintWriter out = new PrintWriter("data/uncompressed.txt");
		for (int i = 0; i<numsymbols; i++) {
			Node curr = root;
			while (!curr.isLeaf()) {
				if (bits.next(1) == 0) curr = curr.getLeft();
				else curr = curr.getRight();
			}
				int sym = curr.getSymbol();
				symbols.get(sym).count++;
				out.print((char) sym);
			
		}
		out.close();
		
		

	}
	
	public static void providedEntropy(ArrayList<Symbol> symbols) throws IOException {
		String inputFile = "data/uncompressed.txt";
		File file = new File(inputFile); 
		BufferedReader br = new BufferedReader(new FileReader(file)); 

		// count frequency of each symbol
		ArrayList<Symbol> symbol_freqs = new ArrayList<Symbol>();
		for (int i = 0; i<256; i++) {
			symbol_freqs.add(new Symbol((char) i, 0));
		}
		int c;
		int totalChars = 0;
		while ((c = br.read()) != -1) {
			symbol_freqs.get(c).count++;
			totalChars++;
		}

		double entropy = 0;
		for (Symbol s : symbols) {
			double probability = symbol_freqs.get(s.s).count/(double)totalChars;
			entropy += probability * s.length;

		}
		System.out.println("Entropy of provided symbol lengths: "+entropy);
	}
}