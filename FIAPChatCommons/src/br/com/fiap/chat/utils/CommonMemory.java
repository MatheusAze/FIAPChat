package br.com.fiap.chat.utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;

public class CommonMemory {

	private static final String FILE_NAME;
	static {
		String filePath = System.getProperty("user.home") + File.separator + "commonMemory-chat.fiap";
		File file = new File(filePath);
		if (!file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException e) {
				System.err.println("ERRO AO CRIAR ARQUIVO COMPARTILHADO!");
			}
		}

		FILE_NAME = filePath;
	}

	public static void writeInfo(String info) {
		Writer writer = null;

		try {
			writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(FILE_NAME), "utf-8"));
			writer.write(info);
		} catch (IOException ex) {
			// report
		} finally {
			try {
				writer.close();
			} catch (Exception ex) {
			}
		}

	}

	public static String readInfo() throws FileNotFoundException {
		BufferedReader br = new BufferedReader(new FileReader(FILE_NAME));
		String info = null;
		try {
			StringBuilder sb = new StringBuilder();
			String line = null;

			while ((line = br.readLine()) != null) {
				sb.append(line);
			}
			info = sb.toString();
		} catch (IOException e) {
			System.err.println("Erro Inesperado ao acessar memória compartilhada!");
			System.exit(-1);
		} finally {
			try {
				br.close();
			} catch (IOException e) { /* ignore */
			}
		}

		return info;
	}

}
