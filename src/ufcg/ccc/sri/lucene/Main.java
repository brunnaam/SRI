package ufcg.ccc.sri.lucene;

import java.io.IOException;
import java.util.Scanner;

import org.apache.lucene.queryparser.classic.ParseException;

public class Main {
	
	private static Lucene l;
	
	public static void main(String[] args) {
		
		try {
			l = new Lucene();
			l.readDocs("files/ptwiki-v2.trec");
			
			testQuery();
//			userInputQuery();
			
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
	}
	
	public static void testQuery() throws IOException, ParseException {
		System.out.println("Query: Escritor ingl�s \n ---- Top 10 results: ---- ");
		l.luceneSearch("Escritor ingl�s", 10);
	}
	
	@SuppressWarnings("resource")
	public static void userInputQuery() throws IOException, ParseException {
		
		Scanner userInput = new Scanner(System.in);
		String search = "";
		
		while (!search.equals("exit")) {
			System.out.println("Digite sua busca: ");
			search = userInput.nextLine();
			if (search.equals("exit")) {
				break;
			}
			System.out.println("Query: " + search + "\n ---- Top 10 results: ----") ;
			l.luceneSearch(search, 10);
		}
	}

}
