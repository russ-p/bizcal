package bizcal.util;

import java.util.Locale;

/**
 * @author Fredrik Bertilsson
 */
public class LocaleBroker
{
    private static LocaleCallback callback = new LocaleCallback.DefaultImpl();
    
    
    public static LocaleCallback getCallback()
    {
        return callback;
    }
    
    public static void setCallback(LocaleCallback callback)
    {
        LocaleBroker.callback = callback;
    }
    
    public static Locale getLocale()
    	throws Exception
    {
    	Locale locale = callback.getLocale();
        return locale;
    }
    
    public static void setLocale(Locale locale)
    {
        callback = new LocaleCallback.DefaultImpl(locale);
    }
    
    
}
