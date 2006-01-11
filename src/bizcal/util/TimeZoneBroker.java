package bizcal.util;

import java.util.TimeZone;

/**
 * @author Fredrik Bertilsson
 */
public class TimeZoneBroker
{
    private static Callback callback = new DefaultCallback();
    
    public static interface Callback
    {
        public TimeZone getTimeZone() throws Exception;
    }
    
    public static class DefaultCallback
    	implements Callback
    {
        private TimeZone tz = TimeZone.getDefault();
        
        public DefaultCallback(TimeZone tz)
        {
            this.tz = tz;
        }
        
        public DefaultCallback()
        {            
        }
        
        public TimeZone getTimeZone() throws Exception
        {
            return tz;
        }
        
    }
    
    public static Callback getCallback()
    {
        return callback;
    }
    
    public static void setCallback(Callback callback)
    {
        TimeZoneBroker.callback = callback;
    }
    
    public static TimeZone getTimeZone()
    	throws Exception
    {
    	TimeZone timeZone = callback.getTimeZone();
        return timeZone;
    }
    
    public static void setTimeZone(TimeZone timeZone)
    {
        callback = new DefaultCallback(timeZone);
    }
}
