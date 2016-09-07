package ufcg.ccc.sri.subtitle.checkpoint1;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class CleanSubtitle {
	
	public static void readSubtitles(String folder) throws IOException{
		File subtitles[];
		File diretorio = new File(folder);
		File resultDir = new File(folder + "Final");
		if (!resultDir.exists()) {
			resultDir.mkdir();
		}
		FileWriter fw;
		BufferedWriter bw;
		subtitles = diretorio.listFiles();
		for(int i = 0; i < subtitles.length; i++){
			String movieName = subtitles[i].getName();
//			String text = new String(Files.readAllBytes(subtitles[i].toPath()));
			fw = new FileWriter(new File(resultDir + "/" + movieName));
			bw = new BufferedWriter(fw);
			String cleanText = clean(subtitles[i]);
			bw.write(cleanText);
		}
	}
	
	private static String clean(File subtitle) {
		FileReader fileReader;
		String result = "";
		try {
			fileReader = new FileReader(subtitle);
			BufferedReader br = new BufferedReader(fileReader);
			
			String row = br.readLine();
			while ((row = br.readLine()) != null) {
				if (row.equals("9999")) {
					break;
				} if (row.equals("")) {
					row = br.readLine();
					row = br.readLine();
				} else if (!row.equals("") && !row.contains("-->")) {
					if (row.contains("<i>")) {
						row = row.replace("<i>", "");
					}
					if (row.contains("</i>")) {
						row = row.replace("</i>", "");
					}
					result += row + "\n";
				}
			}
			result = result.replaceAll("[-?()!@#$%&*.,:;\"]", "");
			fileReader.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return result;
	}
	
	public static void main(String[] args) {
		try {
			System.out.println("Limpando legendas...");
			readSubtitles("C:\\Users\\Brunna\\Desktop\\legendas");
			System.out.println("Done!");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
