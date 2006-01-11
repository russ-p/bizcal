package bizcal.util;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * @author Fredrik Bertilsson
 */
public class LocalizedText
{
    private String _bundleBaseName;
    private String _text;
    private LocalizedText fallback;
    private String defaultValue;
    
    public LocalizedText(String text, String bundleBaseName)
    {
        _text = text;
        _bundleBaseName = bundleBaseName;
        defaultValue = text;
    }
    
    public LocalizedText(String text)
    {
        this(text, null);
    }
    
    public String toString()
    {
        try {
            if (_bundleBaseName == null)
                return _text;
            ResourceBundle bundle = 
                ResourceBundle.getBundle(_bundleBaseName, LocaleBroker.getLocale());
            try {
                return bundle.getString(_text);
            } catch (MissingResourceException mre) {
                if (fallback != null)
                    return fallback.toString();
                return defaultValue;
            }
        } catch (Exception e) {
            throw BizcalException.create(e);
        }
    }
    
    public String getKey()
    {
        return _text;
    }
    
    public String getDefaultValue()
    {
        return defaultValue;
    }
    public void setDefaultValue(String defaultValue)
    {
        this.defaultValue = defaultValue;
    }
    
    public void setFallback(LocalizedText fallback)
    {
        this.fallback = fallback;
    }
    
    
    public static class Factory
    {
        private String _bundleBaseName;
        
        public Factory(String bundleBaseName)
        {
            _bundleBaseName = bundleBaseName;
        }
        
        public LocalizedText createText(String text)
        {
            return new LocalizedText(text, _bundleBaseName);
        }
    }
    
}
