package ufcg.ccc.sri;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Scanner;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class Ranking {
	
	private static Hashtable<String, List<String>> hashTable = new Hashtable<>();
	private static Hashtable<Integer, String> allDocuments = new Hashtable<>();
	
	/**
	 * Adiciona ao dicionário as palavras e o número do document
	 * @param words lista de palavras do paragrafo referente à um documento
	 * @param docNumber Número do documento que contém as palavras
	 */
	private static void addToTable(String[] words, String docNumber) {
		for (String word : words) {
			//Adiciona uma nova palavra ao dicionario, juntamente com o numero do documento
			if (hashTable.get(word) == null) {
				List<String> docs = new ArrayList<String>();
				//Calcula o tf de cada termo
				int tf = tf(words, word);
				docs.add(docNumber + ":" + tf);
				hashTable.put(word, docs);
			} else {
				//Caso a palavra já exista, adiciona o numero do documento à lista 
				List<String> docs = hashTable.get(word);
				int tf = tf(words, word);
				if (!docs.contains(docNumber + ":" + tf)) {
					docs.add(docNumber + ":" + tf);
				}
			}
		}
	}	
	
	public static void readDocuments(String filePath) {
		try {
			//Lendo XML
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document file = dBuilder.parse(new File(filePath));

			file.getDocumentElement().normalize();

			//Pega todos os nós "DOC"
			NodeList allDocs = file.getElementsByTagName("DOC");
			
			//Itera sobre a lista de DOCS
			for (int i = 0; i < allDocs.getLength(); i++) {

				Node doc = allDocs.item(i);

				if (doc.getNodeType() == Node.ELEMENT_NODE) {

					Element eElement = (Element) doc;
					//Pega o valor do documento pela tag "DOCNO"
					String docNumber = eElement.getElementsByTagName("DOCNO").item(0).getTextContent();
					
					//Pega o paragrafo do doc pela tag "P"
					String docText = eElement.getElementsByTagName("P").item(0).getTextContent();
					
					//Limpa o texto retirando citacoes, caracteres não alfa-numericos e espacos desnecessários,
					//colocando todo o texto em caixa baixa
					String cleanText = TokenCleaner.clean(docText);
					allDocuments.put(Integer.parseInt(docNumber), cleanText);
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Cria o dicionário
	 * @param filePath Caminho do arquivo com os dados
	 */
	public static void fillDictionary(String filePath) {
			readDocuments(filePath);
			System.out.println("Criando dicionário... ");

			for (int i = 1; i <= allDocuments.size(); i++) {
				String doc = allDocuments.get(i);
				addToTable(doc.split(" "), String.valueOf(i));				
			}
			
	}
	
	private static int tf(String[] doc, String term) {
		int count = 0;
		for (String word : doc) {
			if (word.equals(term)) {
				count++;
			}
		}
		return count;
	}
	
	private static double idf(String term) {
		int numberOfDocsWithTerm = 0;
		double idf = 0.0;
		for (int i = 1; i <= allDocuments.size(); i++) {
			String doc = allDocuments.get(i);
			if (doc.contains(term)) {
				numberOfDocsWithTerm++;
			}
		}
		idf = Math.log10(allDocuments.size()+1/numberOfDocsWithTerm);
		return idf;
	}
	
	/**
	 * Escreve o indice invertido no arquivo indiceInvertido.txt
	 */
	private static void writeOutputFile() {
		FileWriter fw;
		try {
			fw = new FileWriter(new File("files/indiceInvertido2.txt"));
			BufferedWriter bw = new BufferedWriter(fw);
			
			//indice no formato:
			//    chave: [<lista_de_documentos>]
			for (String s : hashTable.keySet()) {
				bw.write(s + ": " + hashTable.get(s).toString());
				bw.newLine();
			}
			bw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Imprime na tela os documentos nos quais a busca desejada aparece
	 * @param search
	 */
	public static void search(String search) {
		
	}
	
	
	/**
	 * Consultas de teste do indice invertido
	 */
	public static void testQueries() {
		
	}
	
	/**
	 * Pede que a consulta seja digitada pelo usuário. Prompt fica em loop até receber a mensagem exit
	 */
	@SuppressWarnings("resource")
	public static void userInputQuery() {
		Scanner userInput = new Scanner(System.in);
		String search = "";
		
		while (!search.equals("exit")) {
			System.out.println("Digite sua busca: ");
			search = userInput.nextLine();
			search(search);
		}
	}

	public static void main(String[] args) {
		fillDictionary("files/ptwiki-v2.trec");
//		userInputQuery(); //Descomentar essa linha caso queira o promp para digitar uma busca
//		testQueries();
	}

}
