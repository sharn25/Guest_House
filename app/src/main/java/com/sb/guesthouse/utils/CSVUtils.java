package com.sb.guesthouse.utils;

import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

/**
 * CSVUtils create  a csv output file for importing in excel
 * @author sharn25
 * @since 01-02-2021
 * @version 0.0
 */
public class CSVUtils {
	private final String TAG = "CSVUtils";
	private final String CSV_NAME = "ExcelCSV";
	private final String fileExt = ".csv";
	private File csvFile;
	private FileOutputStream fos;
	private Writer out;
	
	/**
	 * Initial Constructor
	 */
	public CSVUtils(String filePath){
		String outputFile = filePath;
		csvFile = new File(outputFile);
		if(csvFile.exists()){
			csvFile.delete();
		}
		LogUtil.i(TAG, "Outfile: " + csvFile.getAbsolutePath(), true);
		try {
			fos = new FileOutputStream(csvFile);
			out = new BufferedWriter(new OutputStreamWriter
					(fos, StandardCharsets.UTF_8));

			LogUtil.i(TAG, "File Opened Successfully.", true);
		} catch (FileNotFoundException e) {
			LogUtil.e(TAG, "Unable to open result file.", true);
		}
	}
	
	public void writeln(String msg){
		if(fos!=null){
			try {
				//fos.write((msg + "\r\n").getBytes(StandardCharsets.UTF_8));
				out.append(msg +  "\r\n");
				out.flush();
			} catch (IOException e) {
				LogUtil.e(TAG, "Unable to write data to file: " + msg, true);
			}
		}
	}
	
	public void write(String msg){
		if(fos!=null){
			try {
				//fos.write((msg + ",").getBytes(StandardCharsets.UTF_8));
				out.append(msg +  ",");
				out.flush();
			} catch (IOException e) {
				LogUtil.e(TAG, "Unable to write data to file: " + msg, true);
			}
		}
	}
	
	public void close(){
		try {
			out.close();
			LogUtil.i(TAG, "File Closed Successfully.", true);
		} catch (IOException e) {
			LogUtil.e(TAG, "Unable to close result file.", true);
		}
	}
}
