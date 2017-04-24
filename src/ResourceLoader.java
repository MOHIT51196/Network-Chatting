import java.util.ResourceBundle;

public interface ResourceLoader {
	
	final static ResourceBundle bundle = ResourceBundle.getBundle("config");
	
	public static String getProperty(String key){
		
		return bundle.getString(key);
	}
}
