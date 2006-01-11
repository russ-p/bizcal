package bizcal.util;

import java.util.Locale;
import java.util.ResourceBundle;

/**
 * @author Fredrik Bertilsson
 *
 */
public class TextUtil 
{
	public static String formatCase(String txt)
	{
	    if (txt == null)
	        return null;
		if (txt.toUpperCase().equals(txt))
			txt = txt.toLowerCase();
		return txt.substring(0,1).toUpperCase() + txt.substring(1);
	}
	
	public static String translate(String txt)
		throws Exception
	{
		if (txt == null)
			return null;
		return ResourceBundle.getBundle("bizcal.common.Bundle", Locale.getDefault()).getString(txt);
	}

}
