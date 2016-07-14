package ufcg.ccc.sri;

public class TokenCleaner {
	
	public static String clean(String str){
		str = str.toLowerCase();
		str = str.replaceAll("&.{2,4};", " ");
		str = str.replaceAll("\\{\\{!\\}\\}", " ");
		str = str.replaceAll("\\{\\{.*?\\}\\}", " ");
		str = str.replaceAll("[^a-z0-9çáéíóúàãõâêô-]", " ");
		str = str.replaceAll("[ ]{2,}", " ");
		
		return(str);
	}

}
