package bizcal.util;

/**
 * Misc null-safe operations.
 *
 * @author Fredrik Bertilsson
 */
public class NullSafe
{
    public static String toString(Object obj)
    {
        if (obj == null)
            return null;
        return obj.toString();
    }

    public static String toString(Object obj, String defaultValue)
    {
        if (obj == null)
            return defaultValue;
        return obj.toString();
    }

    public static String trim(String str)
    {
        if (str == null)
            return null;
        return str.trim();
    }

    public static boolean equals(Object a, Object b)
    {
        if (a == null && b != null)
            return false;
        if (a == null && b == null)
            return true;
        return a.equals(b);
    }
    
    public static int compareTo(Comparable a, Comparable b)
    {
    	if (a == null) {
    		if (b != null)
    			return -1;
    		return 0;
    	}
    	if (b == null)
    		return 1;
    	return a.compareTo(b);
    }
    
    public static int length(String str)
    {
        if (str == null)
            return 0;
        return str.length();
    }

}