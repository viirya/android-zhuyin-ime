package tw.cheyingwu;

import java.io.BufferedReader;
import java.io.File;
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

class WordRow {
	String code;
	String word;
	Integer frequency;
}

public class WordsDB {

	private String wordsTablePath;
	private String wordsDBPath;

	private List<WordRow> wordRowList;

	HashMap<Character, String> codeTable;
	HashMap<Character, Integer> frequencyTable;

	public WordsDB() {
		this.wordsTablePath = "res/bpmf.cin";
		this.wordsDBPath = "res/ZhuYin.db";

		this.wordRowList = new ArrayList<WordRow>();

		this.PrepareCodeTable();
		this.PrepareFrequencyTable();
	}

	public void make() {
		this.ConvertCIN();
		this.createWordsDB();
	}

	public void PrepareCodeTable() {
		this.codeTable = new HashMap<Character, String>();
		codeTable.put(',', "12573");
		codeTable.put('-', "12582");
		codeTable.put('.', "12577");
		codeTable.put('/', "12581");
		codeTable.put('0', "12578");
		codeTable.put('1', "12549");
		codeTable.put('2', "12553");
		codeTable.put('3', "711");
		codeTable.put('4', "715");
		codeTable.put('5', "12563");
		codeTable.put('6', "714");
		codeTable.put('7', "729");
		codeTable.put('8', "12570");
		codeTable.put('9', "12574");
		codeTable.put(';', "12580");
		codeTable.put('a', "12551");
		codeTable.put('b', "12566");
		codeTable.put('c', "12559");
		codeTable.put('d', "12558");
		codeTable.put('e', "12557");
		codeTable.put('f', "12561");
		codeTable.put('g', "12565");
		codeTable.put('h', "12568");
		codeTable.put('i', "12571");
		codeTable.put('j', "12584");
		codeTable.put('k', "12572");
		codeTable.put('l', "12576");
		codeTable.put('m', "12585");
		codeTable.put('n', "12569");
		codeTable.put('o', "12575");
		codeTable.put('p', "12579");
		codeTable.put('q', "12550");
		codeTable.put('r', "12560");
		codeTable.put('s', "12555");
		codeTable.put('t', "12564");
		codeTable.put('u', "12583");
		codeTable.put('v', "12562");
		codeTable.put('w', "12554");
		codeTable.put('x', "12556");
		codeTable.put('y', "12567");
		codeTable.put('z', "12552");
	}

	public void PrepareFrequencyTable() {

		this.frequencyTable = new HashMap<Character, Integer>();
		File file = new File("res/docs");
		File[] files = file.listFiles();
		for (File f : files) {
			// System.out.println(f.toString());
			try {
				BufferedReader bufReader = new BufferedReader(new FileReader(f.toString()));
				String stringLine = null;
				while ((stringLine = bufReader.readLine()) != null) {
					//System.out.println(stringLine);
					char[] words = stringLine.toCharArray();
					for (char word : words) {
						Integer fy = this.frequencyTable.get(word);
						if (fy == null) {
							this.frequencyTable.put(word, 1);
						} else {
							//System.out.println(word+" "+fy);
							this.frequencyTable.put(word, fy + 1);
						}
						//System.out.println(word);
					}
					
				}
			} catch (FileNotFoundException e) {
				System.out.println("檔案不存在");
				e.printStackTrace();
			} catch (IOException e) {
				System.out.println("檔案讀取錯誤");
				e.printStackTrace();
			}
		}

	}

	public void ConvertCIN() {
		try {
			BufferedReader bufReader = new BufferedReader(new FileReader(this.wordsTablePath));
			String stringLine = null;
			while ((stringLine = bufReader.readLine()) != null) {
				String[] splitedString = stringLine.split(" ");
				char[] code = splitedString[0].toCharArray();
				String fullCode = "";
				for (char c : code) {
					fullCode += codeTable.get(c);
				}

				WordRow wr = new WordRow();
				wr.code = fullCode;
				wr.word = splitedString[1];

				Integer f = this.frequencyTable.get(splitedString[1].toCharArray()[0]);
				if (f == null) {
					wr.frequency = 0;
				} else {
					//System.out.println(splitedString[1]+" "+f);
					wr.frequency = f;
				}

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
		try {
			Class.forName("org.sqlite.JDBC");
			Connection conn = DriverManager.getConnection("jdbc:sqlite:" + this.wordsDBPath);
			Statement stat = conn.createStatement();
			stat.executeUpdate("DROP TABLE IF EXISTS words;");
			stat.executeUpdate("CREATE TABLE words (code VARCHAR, word VARCHAR, frequency INTEGER);");

			PreparedStatement prep = conn.prepareStatement("INSERT INTO words VALUES (?, ?, ?);");

			for (WordRow wr : this.wordRowList) {
				prep.setString(1, wr.code);
				prep.setString(2, wr.word);
				prep.setInt(3, wr.frequency);
				prep.addBatch();
			}

			conn.setAutoCommit(false);
			prep.executeBatch();
			conn.setAutoCommit(true);

			conn.close();

		} catch (ClassNotFoundException e) {
			System.out.println("找不到JDBC");
			e.printStackTrace();
		} catch (SQLException e) {
			System.out.println("SQL錯誤");
			e.printStackTrace();
		}

	}
}
