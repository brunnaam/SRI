package ufcg.ccc.sri.lucene;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.lucene.analysis.br.BrazilianAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.similarities.BM25Similarity;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import ufcg.ccc.sri.util.TokenCleaner;

import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.Query;
import org.apache.lucene.document.TextField;

public class Lucene {

	private IndexWriter index;
	private Hashtable<Integer, String> allDocuments = new Hashtable<>();
	
	public Lucene() throws IOException{
		Directory dir = new RAMDirectory();
		IndexWriterConfig config = new IndexWriterConfig(new BrazilianAnalyzer());
		config.setSimilarity(new BM25Similarity());
		index = new IndexWriter(dir, config);
	}

	public void readDocs(String filePath) throws IOException{
		readDocsAux(filePath);
		for (Integer docNumber : allDocuments.keySet()) {
			String text = allDocuments.get(docNumber);
			Document doc = new Document();
			doc.add(new StringField("nomearquivo", docNumber.toString(), Field.Store.YES));
			doc.add(new TextField("texto", text, Field.Store.YES));
			index.addDocument(doc);
		}
	}
	
	private void readDocsAux(String filePath) {
				
		try {
			//Lendo XML
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			org.w3c.dom.Document file = dBuilder.parse(new File(filePath));

			file.getDocumentElement().normalize();

			//Pega todos os n�s "DOC"
			NodeList allDocs = file.getElementsByTagName("DOC");
			
			//Itera sobre a lista de DOCS
			for (int i = 0; i < allDocs.getLength(); i++) {

				Node doc = allDocs.item(i);

				if (doc.getNodeType() == Node.ELEMENT_NODE) {

					Element eElement = (Element) doc;
					//Pega o valor do documento pela tag "DOCNO"
					String stringDoc = eElement.getElementsByTagName("DOCNO").item(0).getTextContent();
					Integer docNumber = Integer.parseInt(stringDoc);
					
					//Pega o paragrafo do doc pela tag "P"
					String docText = eElement.getElementsByTagName("P").item(0).getTextContent();
					
					//Limpa o texto retirando citacoes, caracteres n�o alfa-numericos e espacos desnecess�rios,
					//colocando todo o texto em caixa baixa
					String cleanText = TokenCleaner.clean(docText);
					
					allDocuments.put(docNumber, cleanText);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void luceneSearch(String busca, int n) throws IOException, ParseException{
		QueryParser qp = new QueryParser("texto", new BrazilianAnalyzer());
		IndexSearcher searcher = new IndexSearcher(DirectoryReader.open(index)); 
		searcher.setSimilarity(new BM25Similarity());
		Query query = qp.parse(busca);
		TopDocs topDocs = searcher.search(query, n);
		ScoreDoc[] hits = topDocs.scoreDocs;
		for (int i = 0; i < hits.length; i++) {
			Document doc = searcher.doc(hits[i].doc);
			System.out.println((i+1) + " - " +doc.get("nomearquivo"));
		}
	}	
	
}
