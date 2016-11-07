package com.ilkengin.airdrum;

/* authors :
 * Ahmet Özkul - ahmet.ozkul@itu.edu.tr
 * Rabia Yorgancı - rabia.yorganci@itu.edu.tr
 * 
 * This project is developed a part of study for IROS 2014
 * */

import java.io.File;
import java.io.FileOutputStream;

import android.os.Environment;

public class FileOperations {

	public static String resultFileName = "tempName.txt";

	public boolean isExternalStorageWritable() {
		String state = Environment.getExternalStorageState();
		if (Environment.MEDIA_MOUNTED.equals(state)) {
			return true;
		}
		return false;
	}

	static void writeToFile(String strAction, String strActionResult,
			long timeOfOperations) {
		String result = strAction + "\n" + strActionResult + "\n"
				+ timeOfOperations + " ms\n";
		String root = Environment.getExternalStorageDirectory().toString();
		File myDir = new File(root + "/Air_Drum");
		if (!myDir.isDirectory()) {
			myDir.mkdirs();
		}
		File file = new File(myDir, resultFileName);

		try {
			FileOutputStream out = new FileOutputStream(file, true);
			out.write(result.getBytes());
			out.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
