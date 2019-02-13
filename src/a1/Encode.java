package a1;
import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Queue;

import io.OutputStreamBitSink;

public class Encode {
	
	private static Map<Integer, String> _code_map;

	public static void main(String[] args) throws IOException {
		String outputFile = "data/recompressed.txt";
		String inputFile = "data/uncompressed.txt";
		File file = new File(inputFile); 
		BufferedReader br = new BufferedReader(new FileReader(file)); 

		// count frequency of each symbol
		ArrayList<Symbol> symbols = new ArrayList<Symbol>();
		for (int i = 0; i<256; i++) {
			symbols.add(new Symbol((char) i, 0));
		}
		int c;
		int totalChars = 0;
		while ((c = br.read()) != -1) {
			symbols.get(c).count++;
			totalChars++;
		}
		totalChars += 30;
		
		// create Node object for each symbol, sort by frequency
		ArrayList<Node> nodes = new ArrayList<Node>();
		for (Symbol s : symbols) {
			Node n = new Node();
			n.count = s.count;
			n.s = s.s;
			nodes.add(n);
		}
		Collections.sort(nodes);

		// create minimum variance tree
		while (nodes.size() > 1) {
			Node parent = new Node();
			parent.l = nodes.remove(0);
			parent.r = nodes.remove(0);
			nodes.add(parent);
			Collections.sort(nodes);
		}
		Node root = nodes.get(0);
		
		// build code strings for minimum variance tree
		Map<Integer, String> cmap = new HashMap<Integer, String>();
		for (Symbol s: symbols) {
			root.buildCodeStrings(cmap, "", s.s);
		}
		
		// calculate code lengths
		ArrayList<Symbol> sym_with_length = new ArrayList<Symbol>();
		for (Entry<Integer, String> e : cmap.entrySet()) {
			int codeLength = e.getValue().length();
			int symbol = e.getKey();			
			Symbol s = new Symbol((char) symbol, codeLength);
			sym_with_length.add(s);
		}
		Collections.sort(sym_with_length);

		// create canonical Huffman tree from code lengths
		Node canonical_root = new Node();
		for (Symbol s : sym_with_length) {
			canonical_root.insert(s.s, s.length);
		}
		assert canonical_root.isFull();
		
		// build code strings for canonical Huffman tree
		_code_map = new HashMap<Integer, String>();
		for (Symbol s: symbols) {
			canonical_root.buildCodeStrings(_code_map, "", s.s);
		}
				
		// Open output stream
		FileOutputStream fos = new FileOutputStream(outputFile);
		OutputStreamBitSink bit_sink = new OutputStreamBitSink(fos);

		// Write out code lengths for each symbol as 8 bit value to output file		
		for (int i = 0; i<256; i++) {
			bit_sink.write(_code_map.get(i).length(), 8);
		}
				
		// Write out total number of symbols as 32 bit value
		// System.out.println(totalChars);
		bit_sink.write(totalChars, 32);

		// Reopen input file
		FileInputStream fis = new FileInputStream(inputFile);

		// Go through input, read each symbol, look up code string and write to output
		for (int i = 0; i<totalChars; i++) {
			c = fis.read();
			bit_sink.write(_code_map.get(c));
		}

		// Pad output to next word
		bit_sink.padToWord();

		// Close files
		fis.close();
		fos.close();
		
		theoreticalEntropy(symbols, sym_with_length, totalChars);

	}
	
	public static void theoreticalEntropy(ArrayList<Symbol> symbols, ArrayList<Symbol> lengths, int totalChars) {
		double theoreticalEntropy = 0;
		double actualEntropy = 0;
		for (Symbol s : lengths) {
			symbols.get(s.s).length = s.length;
		}
		for (Symbol s : symbols) {
			double probability = s.count/(double)totalChars;
			double logbase2 = 0;
			int length = s.length;
			if (probability > 0) {
				logbase2 = Math.log(probability)/Math.log(2);
			}
			theoreticalEntropy += -probability * logbase2;
			actualEntropy += probability * length;
		}
		System.out.println("Theoretical entropy: "+theoreticalEntropy);
		System.out.println("Actual entropy: "+actualEntropy);

	}

}
