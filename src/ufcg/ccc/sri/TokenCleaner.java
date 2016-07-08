package ufcg.ccc.sri;

public class TokenCleaner {
	
	public static String clean(String str){
		String aux;
		str = str.toLowerCase();
		str = str.replaceAll("&.{2,4};", " ");
		str = str.replaceAll("\\{\\{!\\}\\}", " ");

		int start, end;
		start = str.indexOf("{{");
		while(start != -1){
			end = str.indexOf("}}");
			if (start > end) {
				break;
			}
			aux = str.substring(start, end+2);
			str = str.replace(aux, " ");
			start = str.indexOf("{{");
		}

		str = str.replaceAll("[^a-z0-9çáéíóúàãõâêô-]", " ");
		
		return(str);
	}
}
