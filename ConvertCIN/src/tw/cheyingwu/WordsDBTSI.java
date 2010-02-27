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
import java.util.HashMap;
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

	private List<WordRow> wordRowList;

	HashMap<Character, String> codeTable;
	HashMap<Character, Integer> frequencyTable;

	public WordsDBTSI() {
		this.wordsTablePath = "res/tsi.src";
		this.wordsDBPath = "ZhuYin.db";

		this.wordRowList = new ArrayList<WordRow>();

		this.PrepareCodeTable();
	}

	public void make() {
		this.ConvertCIN();
		this.createWordsDB();
		//this.splitDB();
	}
	
	public void PrepareCodeTable() {
		this.codeTable = new HashMap<Character, String>();
		codeTable.put('ㄝ', "12573");
		codeTable.put('ㄦ', "12582");
		codeTable.put('ㄡ', "12577");
		codeTable.put('ㄥ', "12581");
		codeTable.put('ㄢ', "12578");
		codeTable.put('ㄅ', "12549");
		codeTable.put('ㄉ', "12553");
		codeTable.put('ˇ', "711");
		codeTable.put('ˋ', "715");
		codeTable.put('ㄓ', "12563");
		codeTable.put('ˊ', "714");
		codeTable.put('˙', "729");
		codeTable.put('ㄚ', "12570");
		codeTable.put('ㄞ', "12574");
		codeTable.put('ㄤ', "12580");
		codeTable.put('ㄇ', "12551");
		codeTable.put('ㄖ', "12566");
		codeTable.put('ㄏ', "12559");
		codeTable.put('ㄎ', "12558");
		codeTable.put('ㄍ', "12557");
		codeTable.put('ㄑ', "12561");
		codeTable.put('ㄕ', "12565");
		codeTable.put('ㄘ', "12568");
		codeTable.put('ㄛ', "12571");
		codeTable.put('ㄨ', "12584");
		codeTable.put('ㄜ', "12572");
		codeTable.put('ㄠ', "12576");
		codeTable.put('ㄩ', "12585");
		codeTable.put('ㄙ', "12569");
		codeTable.put('ㄟ', "12575");
		codeTable.put('ㄣ', "12579");
		codeTable.put('ㄆ', "12550");
		codeTable.put('ㄐ', "12560");
		codeTable.put('ㄋ', "12555");
		codeTable.put('ㄔ', "12564");
		codeTable.put('ㄧ', "12583");
		codeTable.put('ㄒ', "12562");
		codeTable.put('ㄊ', "12554");
		codeTable.put('ㄌ', "12556");
		codeTable.put('ㄗ', "12567");
		codeTable.put('ㄈ', "12552");
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
		
		File f = new File(this.wordsDBPath);
		if(f.exists())
		 f.delete();
		
		try {
			Class.forName("org.sqlite.JDBC");
			Connection conn = DriverManager.getConnection("jdbc:sqlite:" + this.wordsDBPath);
			Statement stat = conn.createStatement();
			stat.executeUpdate("DROP TABLE IF EXISTS words;");
			stat.executeUpdate("CREATE TABLE words (code VARCHAR, word VARCHAR, frequency INTEGER, use INTEGER not null);");
			stat.executeUpdate("CREATE INDEX idx_words_code ON words (code);");
			stat.executeUpdate("CREATE TABLE phrases (code VARCHAR, word VARCHAR, frequency INTEGER, use INTEGER not null);");
			stat.executeUpdate("CREATE INDEX idx_phrases_code ON phrases (code);");

			
			PreparedStatement prep_words = conn.prepareStatement("INSERT INTO words VALUES (?, ?, ?, 0);");
			PreparedStatement prep_phrases = conn.prepareStatement("INSERT INTO phrases VALUES (?, ?, ?, 0);");

			
			for (WordRow wr : this.wordRowList) {
			 // Filter words
			 if(wr.frequency>2000 || (wr.word.length()==1 && wr.frequency>100)) {
				 if(wr.word.length()==1) {
					 prep_words.setString(1, wr.code);
					 prep_words.setString(2, wr.word);
					 prep_words.setInt(3, wr.frequency);
					 prep_words.addBatch();
				 }
				 else {
					 prep_phrases.setString(1, wr.code);
					 prep_phrases.setString(2, wr.word);
					 prep_phrases.setInt(3, wr.frequency);
					 prep_phrases.addBatch();
				 }				 
			 }
			}

			conn.setAutoCommit(false);
			prep_words.executeBatch();
			prep_phrases.executeBatch();
			conn.setAutoCommit(true);

			// Create view
			//stat.executeUpdate("DROP VIEW IF EXISTS vwords;");
			//stat.executeUpdate("CREATE VIEW vwords AS SELECT * from words where frequency>1000 or (length(word)=1 and frequency>1);");
			
			// Create android_metadata
			stat.executeUpdate("DROP TABLE IF EXISTS android_metadata;");
			stat.executeUpdate("CREATE TABLE android_metadata (locale VARCHAR);");
			stat.executeUpdate("INSERT INTO android_metadata VALUES ('zh_TW');");
			
			conn.close();

		} catch (ClassNotFoundException e) {
			System.out.println("找不到JDBC");
			e.printStackTrace();
		} catch (SQLException e) {
			System.out.println("SQL錯誤");
			e.printStackTrace();
		}

	}
	
	public void splitDB(){
		try {
			
	    FileInputStream fis = new FileInputStream(this.wordsDBPath);
	    int size = 1048576; // 1MB
	    byte buffer[] = new byte[size];

	    int count = 0;
	    while (true) {
	      int i = fis.read(buffer, 0, size);
	      if (i == -1)
	        break;

	      String filename = this.wordsDBPath + count;
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
}
