package ufcg.ccc.sri.indiceInvertido;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;
import java.util.List;
import java.util.Scanner;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import ufcg.ccc.sri.util.TokenCleaner;


public class IndiceInvertido {

	private static Hashtable<String, List<Integer>> hashTable = new Hashtable<>();
	
	/**
	 * Adiciona ao dicionário as palavras e o número do document
	 * @param words lista de palavras do paragrafo referente à um documento
	 * @param docNumber Número do documento que contém as palavras
	 */
	private static void addToTable(String[] words, Integer docNumber) {
		for (String word : words) {
			//Adiciona uma nova palavra ao dicionario, juntamente com o numero do documento
			if (hashTable.get(word) == null) {
				List<Integer> docs = new ArrayList<Integer>();
				docs.add(docNumber);
				hashTable.put(word, docs);
			} else {
				//Caso a palavra já exista, adiciona o numero do documento à lista 
				List<Integer> docs = hashTable.get(word);
				if (!docs.contains(docNumber)) {
					docs.add(docNumber);
				}
			}
		}
	}	
	
	/**
	 * Cria o dicionário
	 * @param filePath Caminho do arquivo com os dados
	 */
	public static void fillDictionary(String filePath) {
		try {

			System.out.println("Criando dicionário... ");

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
					String[] words = cleanText.split(" ");
					Integer docNum = Integer.parseInt(docNumber);

					//Adiciona as palavras ao dicionario
					addToTable(words, docNum);

				}
			}
			//escreve o dicionario em um arquivo
			writeOutputFile();
			System.out.println("Dicionário Criado!");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Escreve o indice invertido no arquivo indiceInvertido.txt
	 */
	private static void writeOutputFile() {
		FileWriter fw;
		try {
			fw = new FileWriter(new File("files/indiceInvertido.txt"));
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
		search = search.toLowerCase();
		//Caso a busca seja um AND
		if (search.contains(" and ") ) {
			String formattedSearch = search.replaceAll(" and ", " ");
			String[] words = formattedSearch.split(" ");
			System.out.println("Resultado: " + andSearch(words[0], words[1]));
		} else if (search.contains(" or ")) {
			//Caso a busca seja um OR
			String formattedSearch = search.replaceAll(" or ", " ");
			String[] words = formattedSearch.split(" ");
			System.out.println("Resultado: " + orSearch(words));
		} else {
			//Caso a busca nao siga os padroes
			if (!search.equals("exit")) System.out.println("Pesquisa deve ser do tipo: <termo> <operador> <termo>, onde operador pode ser 'and' ou 'or'");
		}
	}
	
	/**
	 * Faz um AND entre os elementos da busca, ex: nomes and biblicos
	 * @param p1 palavra 1
	 * @param p2 palavra 2
	 * @return lista de documentos que contém as duas palavras 
	 */
	private static List<Integer> andSearch(String p1, String p2) {
		List<Integer> answer = new ArrayList<Integer>();
		
		List<Integer> postingsP1 = hashTable.get(p1);
		List<Integer> postingsP2 = hashTable.get(p2);
		
		if (postingsP1 == null || postingsP2 == null) return null;
		
		int indexP1 = 0;
		int indexP2 = 0;
		
		while (indexP1 < postingsP1.size() && indexP2 < postingsP2.size()) {
			if (postingsP1.get(indexP1) == postingsP2.get(indexP2)) {
				answer.add(postingsP1.get(indexP1));
				indexP1++;
				indexP2++;
			} else if (postingsP1.get(indexP1) < postingsP2.get(indexP2)) {
				indexP1++;
			} else {
				indexP2++;
			}
		}
		return answer;
	}
	
	/**
	 * Faz um OR entre os elementos da busca, ex: nomes or biblicos
	 * @param words 
	 * @return lista de documentos que contém as palavra 1 ou a palavra 2 
	 */
	private static List<Integer> orSearch(String[] words) {
		List<Integer> docs = new ArrayList<Integer>();
		for (String word : words) {
			List<Integer> wordDocs = hashTable.get(word);
			if (wordDocs != null) {
				for (Integer i : wordDocs) {
					if (!docs.contains(i)) {
						docs.add(i);
					}
				}
			}
		}
		Collections.sort(docs);
		return docs;
	}
	
	/**
	 * Consultas de teste do indice invertido
	 */
	public static void testQueries() {
		String search = "Winston or Churchill";
		System.out.println("\n" + search);
		search(search);
		
		search = "Winston and Churchill";
		System.out.println("\n" + search);
		search(search);
		
		search = "Estados or Unidos";
		System.out.println("\n" + search);
		search(search);
		
		search = "Estados and Unidos";
		System.out.println("\n" + search);
		search(search);
		
		search = "nomes or bíblicos";
		System.out.println("\n" + search);
		search(search);
		
		search = "nomes and bíblicos";
		System.out.println("\n" + search);
		search(search);
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
		testQueries();
	}
	
}
