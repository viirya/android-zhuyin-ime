package tw.cheyingwu;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;

public class ConvertCIN {

	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
		HashMap<Character, String> codeTable = new HashMap<Character, String>();
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

		BufferedReader bufReader = new BufferedReader(new FileReader("res/bpmf.cin"));
		String stringLine = null;
		
		while ((stringLine = bufReader.readLine()) != null) {
			String[] splitedString = stringLine.split(" ");
			char[] code = splitedString[0].toCharArray();
			String fullCode="";
			for(char c:code){
				fullCode += codeTable.get(c);
			}
			System.out.println(fullCode+","+splitedString[1]);
		}
		
	}

}
