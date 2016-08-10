package ufcg.ccc.sri.ranking;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import ufcg.ccc.sri.util.TokenCleaner;

public class IndiceTFIDF {
	
	private static Hashtable<String, Hashtable<String, Integer>> hashTable = new Hashtable<>();
	private static Hashtable<Hashtable<String, Double>, Hashtable<String, Integer>> tfIdfIndex = new Hashtable<>(); 
	private static Hashtable<Integer, String> allDocuments = new Hashtable<>();
	
	/**
	 * Lê o arquivo e adiciona os documentos em uma hashtable
	 * @param filePath
	 */
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
			//Criar indice
			createTFIDFIndex();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Cria o indice TFIDF
	 * @param filePath Caminho do arquivo com os dados
	 */
	private static void createTFIDFIndex() {
		System.out.println("Criando Indice... ");

		for (int i = 1; i <= allDocuments.size(); i++) {
			String doc = allDocuments.get(i);
			//adiciona os termos ao indice, calculando seu tf;
			addToTable(doc.split(" "), String.valueOf(i));				
		}
		//Criar indice com o idf de todos os termos
		addIdfToIndice();
		//escreve arquivo com o indice TFIDF
//		writeOutputFile();
		System.out.println("Indice criado com sucesso!");
	}
	
	/**
	 * Adiciona ao Indice as palavras e o número do document, calculando seu tf
	 * @param words lista de palavras do paragrafo referente à um documento
	 * @param docNumber Número do documento que contém as palavras
	 */
	private static void addToTable(String[] words, String docNumber) {
		for (String word : words) {
			//Adiciona uma nova palavra ao indice TFIDF, juntamente com o numero do documento
			if (hashTable.get(word) == null) {
				Hashtable<String, Integer> docs = new Hashtable<>();
				
				//Calcula o tf de cada termo
				int tf = tf(words, word);
				docs.put(docNumber, tf);
				hashTable.put(word, docs);
				
			} else {
				//Caso a palavra já exista, adiciona o documento e tf ao indice correspondente
				Hashtable<String, Integer> docs = hashTable.get(word);
				if (!docs.containsKey(docNumber)) {
					int tf = tf(words, word);
					docs.put(docNumber, tf);
				}
			}
		}
	}
	
	/**
	 * Calcula o tf do termo passado como parâmetro
	 * @param doc strings do documento
	 * @param term termo
	 * @return tf do termo
	 */
	private static int tf(String[] doc, String term) {
		int count = 0;
		//frequencia do termo "term" no documento "doc"
		for (String word : doc) {
			if (word.equals(term)) {
				count++;
			}
		}
		return count;
	}
	
	/**
	 * Calcula idf do termo passado como paramêtro
	 * @param term termo
	 * @return idf
	 */
	private static Double idf(String term) {
		return ((double)1 / hashTable.get(term).size());
	}
	
	/**
	 * Adiciona o idf de cada termo ao indice TFIDF
	 * @return
	 */
	private static Hashtable<Hashtable<String, Double>, Hashtable<String, Integer>> addIdfToIndice() {
		Hashtable<Hashtable<String, Double>, Hashtable<String, Integer>> dict = new Hashtable<>();
		Set<String> keys = hashTable.keySet();
		//Para cada termo, calcula seu idf e o adiciona ao indiceTFIDF
		for (String term : keys) {
			Hashtable<String, Integer> docs = hashTable.get(term);
			Hashtable<String, Double> newKey = new Hashtable<>();
			newKey.put(term, idf(term));
			dict.put(newKey, docs);
		}
		tfIdfIndex = dict;
		return dict;
	}
	
	/**
	 * Escreve o indice TFIDF no arquivo indiceTFIDF.txt
	 */
	private static void writeOutputFile() {
		FileWriter fw;
		try {
			fw = new FileWriter(new File("files/indiceTFIDF.txt"));
			BufferedWriter bw = new BufferedWriter(fw);
			
			//indice no formato:
			// {termo=idf}={documento=tf, documento2=tf2, ...}
			Set<Hashtable<String, Double>> keys = tfIdfIndex.keySet();
			for (Hashtable<String, Double> key : keys) {
				bw.write(key + "=" + tfIdfIndex.get(key));
				bw.newLine();
			}
			bw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Retorna o top 5 documentos resultantes da pesquisa
	 * @param search Consulta
	 * @return Lista de documentos
	 */
	public static ArrayList<Entry<String, Double>> search(String search) {
		System.out.println("Consulta: " + search);
		Hashtable<String, Double> scores = new Hashtable<>();
		String[] queryTerms = search.toLowerCase().split(" ");
		
		//Para cada termo da pesquisa
		for (String term : queryTerms) {
			 Hashtable<String, Integer> docsOfTerm = hashTable.get(term);
			 
			 //Para cada documento que contém esse termo
			 for (String doc : docsOfTerm.keySet()) {
				 
				 //calcula score BM25 para documento
				 Double score = bm25Rank(8, docsOfTerm.get(doc), idf(term));
				 
				 //Caso o documento já esteja na lista, incrementa seu score
				 if (scores.containsKey(doc)) {
					 Double pastScore = scores.get(doc);
					 scores.put(doc, pastScore + score);
				 } else {
					 //Se não, adiciona o documento e seu score na lista
					 scores.put(doc, score);
				 }
			 }
		}
		//Faz o ranking da lista de documentos resultante
		return sortRank(scores, 5);
	}
	
	/**
	 * Calcula o valor BM25 para cada documento. O score resultante servirá para fazer o ranking dos documentos mais importantes para a consulta
	 * @param k 
	 * @param tf
	 * @param idf
	 * @return score pro documento
	 */
	private static Double bm25Rank(Integer k, Integer tf, Double idf) {
		return (((k + 1) * tf) / tf + k) * (Math.log(hashTable.size()+1) * idf) ;
	}
	
	/**
	 * Irá ordenar a lista de resultados da consulta e retornará apenas a quantidade desejada de documentos
	 * @param documents Resultado geral da pesquisa
	 * @param topResults Tamanho do Ranking desejado
	 * @return Top documentos que mais se adequam a consulta
	 */
	private static ArrayList<Entry<String, Double>> sortRank(Hashtable<String,Double> documents, int topResults) {
		ArrayList<Map.Entry<String, Double>> result = new ArrayList<>();
		ArrayList<Map.Entry<String, Double>> docsOrdered = new ArrayList<Entry<String, Double>>(documents.entrySet());
		
		//Ordena o array de entries <String, Double>
		Collections.sort(docsOrdered, new Comparator<Map.Entry<String, Double>>(){

			@Override
			public int compare(Map.Entry<String, Double> o1, Map.Entry<String, Double> o2) {
				return o2.getValue().compareTo(o1.getValue());
			}});

		//Adiciona ao resultado apenas a quantidade de documentos desejada para o ranking
		for (int i = 0; i < topResults; i++) {
			result.add(docsOrdered.get(i));
		}
		return result;
	}
	
	/**
	 * Consultas de teste do ranking
	 */
	public static void testQueries() {
		
		String[] queries = {"primeira guerra mundial", "espaço e tempo", "minha terra tem palmeiras onde canta o sabiá", "grupo raça negra"};
		for (String query : queries) {
			System.out.println("---------------------------");
			ArrayList<Entry<String, Double>> result = search(query);
			for (int i = 0; i < result.size(); i++) {
				System.out.println((i+1) + ". Documento " + result.get(i).getKey());
			}
		}
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
		readDocuments("files/ptwiki-v2.trec");
//		userInputQuery(); //Descomentar essa linha caso queira o promp para digitar uma busca
		testQueries();
	}

}
