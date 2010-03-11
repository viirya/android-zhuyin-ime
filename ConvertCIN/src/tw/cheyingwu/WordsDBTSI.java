package tw.cheyingwu;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

/*
class WordRow {
	String code;
	String word;
	Integer frequency;
}
*/

public class WordsDBTSI {

	private String wordsTablePath;
	private String wordsDBPath;
	private String phrasesDBPath;

	private List<WordRow> wordRowList;

	HashMap<Character, String> codeTable;
	HashMap<Character, Integer> frequencyTable;

	public WordsDBTSI() {
		this.wordsTablePath = "res/tsi.src";
		this.wordsDBPath = "ZhuYinWords.db";
		this.phrasesDBPath = "ZhuYinPhrases.db";

		this.wordRowList = new ArrayList<WordRow>();

		this.PrepareCodeTable();
	}

	public void make() {
		this.ConvertCIN();
		this.createWordsDB();
		this.splitDB();
	}
	
	public void PrepareCodeTable() {
		this.codeTable = new HashMap<Character, String>();
		codeTable.put('ㄅ', "10");
		codeTable.put('ㄆ', "11");
		codeTable.put('ㄇ', "12");
		codeTable.put('ㄈ', "13");
		codeTable.put('ㄉ', "14");
		codeTable.put('ㄊ', "15");
		codeTable.put('ㄋ', "16");
		codeTable.put('ㄌ', "17");
		codeTable.put('ㄍ', "18");
		codeTable.put('ㄎ', "19");
		codeTable.put('ㄏ', "1A");
		codeTable.put('ㄐ', "1B");
		codeTable.put('ㄑ', "1C");
		codeTable.put('ㄒ', "1D");
		codeTable.put('ㄓ', "1E");
		codeTable.put('ㄔ', "1F");
		codeTable.put('ㄕ', "1G");
		codeTable.put('ㄖ', "1H");
		codeTable.put('ㄗ', "1I");
		codeTable.put('ㄘ', "1J");
		codeTable.put('ㄙ', "1K");
		codeTable.put('ㄚ', "20");
		codeTable.put('ㄛ', "21");
		codeTable.put('ㄜ', "22");
		codeTable.put('ㄝ', "23");
		codeTable.put('ㄞ', "24");
		codeTable.put('ㄟ', "25");
		codeTable.put('ㄠ', "26");
		codeTable.put('ㄡ', "27");
		codeTable.put('ㄢ', "28");
		codeTable.put('ㄣ', "29");
		codeTable.put('ㄤ', "2A");
		codeTable.put('ㄥ', "2B");
		codeTable.put('ㄦ', "2C");
		codeTable.put('ㄧ', "30");
		codeTable.put('ㄨ', "31");
		codeTable.put('ㄩ', "32");
		codeTable.put('˙',  "40");
		codeTable.put('ˊ',  "41");		
		codeTable.put('ˇ',  "42");
		codeTable.put('ˋ',  "43");
	}

	public void ConvertCIN() {
		try {
			BufferedReader bufReader = new BufferedReader(new FileReader(this.wordsTablePath));
			String stringLine = null;
			while ((stringLine = bufReader.readLine()) != null) {
				System.out.println(stringLine);
				String[] splitedString = stringLine.split(" ");				
				String fullCode = "";
		
				for (int i = 2; i < splitedString.length; i++) {
 				 char[] code = splitedString[i].toCharArray();
 				 for (char c : code) {
					fullCode += codeTable.get(c);
				 }	
				}
				
				WordRow wr = new WordRow();
				wr.code = fullCode;
				wr.word = splitedString[0];
				wr.frequency = Integer.parseInt(splitedString[1]);
				
				this.wordRowList.add(wr);
			}
		} catch (FileNotFoundException e) {
			System.out.println("檔案不存在");
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println("檔案讀取錯誤");
			e.printStackTrace();
		}
		
	}

	public void createWordsDB() {
		
		// Clean ZhuYin.db* files
		File folder = new File(".");
		File[] listOfFiles = folder.listFiles();
		
		if(listOfFiles.length > 0) {
			for (int i = 0; i < listOfFiles.length; i++) {
				if(listOfFiles[i].getName().startsWith(this.wordsDBPath))
					listOfFiles[i].delete();
			}
			for (int i = 0; i < listOfFiles.length; i++) {
				if(listOfFiles[i].getName().startsWith(this.phrasesDBPath))
					listOfFiles[i].delete();
			}			
		}
	
		try {
			Class.forName("org.sqlite.JDBC");
			Connection connWords = DriverManager.getConnection("jdbc:sqlite:" + this.wordsDBPath);
			Connection connPhrases = DriverManager.getConnection("jdbc:sqlite:" + this.phrasesDBPath);
			Statement statWords = connWords.createStatement();
			Statement statPhrases = connPhrases.createStatement();

	        Collection collection = codeTable.values();
	        Iterator iterator = collection.iterator();
	        while(iterator.hasNext()) {
	        	String codeStr = iterator.next().toString();
	        	statWords.executeUpdate("CREATE TABLE words_" + codeStr + " (code VARCHAR, word VARCHAR, frequency INTEGER, use INTEGER not null);");
				//statWords.executeUpdate("CREATE INDEX idx_words_" + codeStr + "_code ON words_" + codeStr + " (code, word, frequency, use);");
	        	//statWords.executeUpdate("CREATE INDEX idx_words_" + codeStr + "_code ON words_" + codeStr + " (code);");
	        	statPhrases.executeUpdate("CREATE TABLE phrases_" + codeStr + " (code VARCHAR, word VARCHAR, frequency INTEGER, use INTEGER not null);");
				//statPhrases.executeUpdate("CREATE INDEX idx_phrases_" + codeStr + "_code ON phrases_" + codeStr + " (code, word, frequency, use);");
	        	//statPhrases.executeUpdate("CREATE INDEX idx_phrases_" + codeStr + "_code ON phrases_" + codeStr + " (code);");
	        }
	            			
			String insertSQL;
			Integer iWord=0, iPhrase=0;
			
			for (WordRow wr : this.wordRowList) {
			 // Filter words
			 if(wr.frequency>400 || (wr.word.length()==1 && wr.frequency>1)) {
				 
				 if(wr.word.length()==1) {
					 insertSQL = "INSERT INTO words_";
					 insertSQL +=  wr.code.substring(0, 2) + " VALUES ('" + wr.code + "', '" + wr.word + "', " + wr.frequency + ", 0);";
					 statWords.addBatch(insertSQL);
					 iWord++;
				 }else {
					 insertSQL = "INSERT INTO phrases_";
					 insertSQL +=  wr.code.substring(0, 2) + " VALUES ('" + wr.code + "', '" + wr.word + "', " + wr.frequency + ", 0);";
					 statPhrases.addBatch(insertSQL);					 
					 iPhrase++;
				 }
				 
			 }
			}

			connWords.setAutoCommit(false);
			statWords.executeBatch();
			connWords.setAutoCommit(true);
			
			connPhrases.setAutoCommit(false);
			statPhrases.executeBatch();
			connPhrases.setAutoCommit(true);
			
			// Create android_metadata
			statWords.executeUpdate("CREATE TABLE android_metadata (locale VARCHAR);");
			statWords.executeUpdate("INSERT INTO android_metadata VALUES ('zh_TW');");

			statPhrases.executeUpdate("CREATE TABLE android_metadata (locale VARCHAR);");
			statPhrases.executeUpdate("INSERT INTO android_metadata VALUES ('zh_TW');");

			connWords.close();
			connPhrases.close();
			
			System.out.println("總共: " + iWord + " 字, " + iPhrase + " 詞");

		} catch (ClassNotFoundException e) {
			System.out.println("找不到JDBC");
			e.printStackTrace();
		} catch (SQLException e) {
			System.out.println("SQL錯誤");
			e.printStackTrace();
		}

	}
	
	private void splitDataBase(String DB_Name){
		try {
			
	    FileInputStream fis = new FileInputStream(DB_Name);
	    int size = 921600; // 900KB, Android assets input file has 1MB limitaion.
	    byte buffer[] = new byte[size];

	    int count = 0;
	    while (true) {
	      int i = fis.read(buffer, 0, size);
	      if (i == -1)
	        break;

	      String filename = DB_Name + count;

	      FileOutputStream fos = new FileOutputStream(filename);
	      fos.write(buffer, 0, i);
	      fos.flush();
	      fos.close();

	      ++count;
	    }
		} catch (IOException e){
			e.printStackTrace();
		}
	}
	
	public void splitDB(){
		splitDataBase(this.wordsDBPath);
		splitDataBase(this.phrasesDBPath);
	}
}
