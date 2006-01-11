package bizcal.common;

import java.util.*;

/**
 * English localization data used by swing components.
 *
 * @author Fredrik Bertilsson
 */
public class Bundle
        extends ListResourceBundle
{
    private Object[][] _contents = new Object[0][0];


    public Object[][] getContents()
    {
        return _contents;
    }

    public static String translate(String txt) throws Exception
    {
    	if (txt == null)
    		return null;
    	try {
	        return ResourceBundle.getBundle("bizcal.sheet.common.Bundle",
	                Locale.getDefault()).getString(txt);
    	} catch (MissingResourceException e) {
    		return txt;
    	}
    }
    
}
