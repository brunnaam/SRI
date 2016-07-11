package ufcg.ccc.sri;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

public class Main {
	
	private static BTree<String, List<Integer>> btree = new BTree<String, List<Integer>>();
	
	private static void addToTree(String[] words, Integer docNumber) {
		for (String word : words) {
			if (btree.get(word) == null) {
				List<Integer> docs = new ArrayList<Integer>();
				docs.add(docNumber);
				btree.put(word, docs);
			} else {
				List<Integer> docs = btree.get(word);
				if (!docs.contains(docNumber)) {
					docs.add(docNumber);
				}
			}
		}
	}	

	@SuppressWarnings("resource")
	public static void fillDictionary(String filePath) {
		try {
			BufferedReader reader = new BufferedReader(new FileReader(new File(filePath)));
			
			String line = "";
			int docNumber = 1;
			
			while ((line = reader.readLine()) != null) {
				if (line.contains("</DOC>")) {
					docNumber++;
				} else if (line.contains("</P>")){
					String docLine = line.replaceAll("</P>", "");
					String cleanText = TokenCleaner.clean(docLine);
					String[] words = cleanText.split(" ");
					addToTree(words, docNumber);
				}
			}   
			
			System.out.println(btree);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void search(String search) {
		//TODO
		String[] words = search.split(" ");
	}
	
	public static void main(String[] args) {
		
		fillDictionary("C:\\Users\\Brunna\\Downloads\\ptwiki-v2.trec");
				 
	}

}
