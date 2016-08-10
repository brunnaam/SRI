package ufcg.ccc.sri.lucene;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

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
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.Query;
import org.apache.lucene.document.TextField;

public class Lucene {

	private IndexWriter index;
	
	public Lucene() throws IOException{
		Directory dir = new RAMDirectory();
		IndexWriterConfig config = new IndexWriterConfig(new BrazilianAnalyzer());
		index = new IndexWriter(dir, config);
	}

	public void lerDocs(String pasta) throws IOException{
		File arquivos[];
		File diretorio = new File(pasta);
		arquivos = diretorio.listFiles();
		for(int i = 0; i < arquivos.length; i++){
			String texto = new String(Files.readAllBytes(arquivos[i].toPath()));
			Document doc = new Document();
			doc.add(new StringField("nomearq", arquivos[i].getName(), Field.Store.YES));
			doc.add(new TextField("texto", texto, Field.Store.YES));
			index.addDocument(doc);
		}
	}
	
	public void luceneTesteBusca(String busca, int n) throws IOException, ParseException{
		QueryParser qp = new QueryParser("texto", new BrazilianAnalyzer());
		IndexSearcher searcher = new IndexSearcher(DirectoryReader.open(index)); 
		Query query = qp.parse(busca);
		TopDocs topDocs = searcher.search(query, n);
		ScoreDoc[] hits = topDocs.scoreDocs;
		for (int i = 0; i < hits.length; i++) {
			Document doc = searcher.doc(hits[i].doc);
			System.out.println(doc.get("nomearq"));
		}
	}	
	
}
