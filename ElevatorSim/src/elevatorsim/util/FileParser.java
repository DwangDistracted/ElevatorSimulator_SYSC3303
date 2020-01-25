package util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import org.json.JSONException;
import org.json.JSONObject;

public class FileParser {
	
	public static String getJSONStringFromFile(String path) throws FileNotFoundException {
		File file = new File(path);
		if(file.isFile()) {
			Scanner scanner = new Scanner(new File(path));
			String json = scanner.useDelimiter("\\Z").next();
			scanner.close();
			
			return json;
		}
		
		return null;
	}
	
	public static JSONObject getJSONObjectFromFile(String path) throws JSONException, FileNotFoundException {
		return new JSONObject(getJSONStringFromFile(path));
	}
	
	public void getFile(String filePath) {
        BufferedReader br = null;
        String line = "";
        String cvsSplitBy = ",";

        try {
            br = new BufferedReader(new FileReader(filePath));
            while ((line = br.readLine()) != null) {
                String[] input = line.split(cvsSplitBy);
                
                try {
                	//InputInfo user = new InputInfo(Floor.valueOf(input[0]), Direction.valueOf(input[1]), Floor.valueOf(input[2]));
                } catch (Exception ex) {
                	System.out.println("Invalid Input - File Ignored");
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
	}
	
	public static List<JSONObject> parseFiles(String directory) {
		List<JSONObject> input = null;
		for(File file :  new File(directory).listFiles()) {
			if(file.getName().endsWith(".json")) {
				JSONObject jsonObject;
				
				try {
					jsonObject = getJSONObjectFromFile(file.getAbsolutePath());
				} catch (Exception e) {
					e.printStackTrace();
					appendToFileName(file, ".invalid");
					continue;
				}
				
				if(input == null) {
					input = new ArrayList<JSONObject>(); 
				}
				
				input.add(jsonObject);
				appendToFileName(file, ".parsed");
			}
		}
		
		return input;
	}
	
	public static void appendToFileName(File file, String value) {
		File file2 = new File(file.getName() + value);
		boolean success = file.renameTo(file2);
	}
	
}
