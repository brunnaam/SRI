package ufcg.ccc.sri.lucene;

import java.io.IOException;

import org.apache.lucene.queryparser.classic.ParseException;

public class Main {
	
	public static void main(String[] args) throws IOException, ParseException {
		Lucene l = new Lucene();
		
		l.readDocs("files/ptwiki-v2.trec");
		l.luceneSearch("escritor inglês", 23);
		
	}

}
