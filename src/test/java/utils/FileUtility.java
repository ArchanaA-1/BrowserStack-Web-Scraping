package utils;

import java.io.FileReader;
import java.util.Properties;

public class FileUtility {
	public String getPropertyKeyValue(String key) throws Throwable {
		FileReader fis = new FileReader(IConstant.commonDataFilePath);
		Properties pobj = new Properties();
		pobj.load(fis);
		String value = pobj.getProperty(key);
		return value;
	}

}
