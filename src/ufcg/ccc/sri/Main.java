package ufcg.ccc.sri;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

public class Main {

	public static void main(String[] args) {
		
		try {
			BufferedReader reader = new BufferedReader(new FileReader(new File("C:\\Users\\Brunna\\Downloads\\ptwiki-v2.trec")));
			
			String line = "";
//			String text = "";
			int docNumber = 1;
			
			while ((line = reader.readLine()) != null) {
				if (line.contains("<DOC>")) {
					System.out.println("------------- Inicio do Documento " + docNumber + " ------------ ");
					System.out.println(line);
				} else if (line.contains("</DOC>")) {
					System.out.println(line);
					System.out.println("------------- Fim do Documento " + docNumber + " ------------ ");
//					System.out.println(text);
//					String cleanText = TokenCleaner.clean(text);
//					System.out.println(cleanText);
					docNumber++;
//					text = "";
				} else if (line.contains("<P>") || line.contains("</P>")){
					System.out.println(line);
					String cleanText = TokenCleaner.clean(line);
					System.out.println(cleanText);
//					text += line + " ";
				}
			}       
		} catch (Exception e) {
			e.printStackTrace();
		}
		 
	}

}
