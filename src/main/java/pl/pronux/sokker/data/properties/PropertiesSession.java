package pl.pronux.sokker.data.properties;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

import pl.pronux.sokker.exceptions.SVException;

public class PropertiesSession {
	
	
	private Properties properties;
	private String filename;
	
	public Properties init(String filename) throws FileNotFoundException, IOException {
		properties = new Properties();
		properties.load(new FileInputStream(filename));
		this.filename = filename;
		return properties; 
	}
	
	public Properties getProperties() throws SVException{
		if(properties != null) {
			return properties;
		} else {
			throw new SVException("There is no such properties"); //$NON-NLS-1$
		}
	}
	
	public void save(Properties properties, String filename) throws FileNotFoundException, IOException {
		if(filename != null) {
			properties.store(new FileOutputStream(new File(filename)), "");	 //$NON-NLS-1$
		}
	}
	
	public void save() throws FileNotFoundException, IOException {
		save(properties, filename);
	}
	
	public String getProperty(String key) {
		return properties.getProperty(key);
	}
	
	public void setProperty(String key, String value) {
		this.properties.setProperty(key, value);
	}
}
