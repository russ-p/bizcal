package bizcal.util;

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;


/**
 * @author Fredrik Bertilsson
 */
public class TimeOfDay
	implements Comparable
{
    private long _time;
    
    public TimeOfDay(long time)
    {
        _time = time;
    }
    
    public TimeOfDay(int hours, int minutes)
    	throws Exception
    {
    	_time = hours*3600*1000 + minutes*60*1000;	
    }
    
    public String toString()
    {
        return "" + _time;
    }
    
    public long getValue()
    {
        return _time;
    }
    
    public int getHour()
    	throws Exception
    {
    	return getCalendar().get(Calendar.HOUR_OF_DAY);
    }

    public int getMinute()
    	throws Exception
    {
    	return getCalendar().get(Calendar.MINUTE);
    }
    
    private Calendar getCalendar()
    	throws Exception
    {
    	Calendar cal = Calendar.getInstance(Locale.getDefault());
    	cal.setTimeZone(TimeZone.getTimeZone("GMT"));
    	cal.setTime(new Date(_time));
    	return cal;
    }
    
    public Date getDate(Date date)
    	throws Exception
    {
    	Calendar cal = Calendar.getInstance(Locale.getDefault());
    	cal.setTimeZone(TimeZone.getDefault());
    	cal.setTime(date);
    	cal.set(Calendar.HOUR_OF_DAY, getHour());
    	cal.set(Calendar.MINUTE, getMinute());
    	cal.set(Calendar.SECOND, 0);
    	cal.set(Calendar.MILLISECOND, 0);
    	return cal.getTime();
    }
    
    public int compareTo(Object other)
    {
        if (other == null)
            return -1;
    	TimeOfDay o = (TimeOfDay) other;    	
    	return (int) (_time - o.getValue());
    }
    
    public boolean equals(Object other)
    {
        return compareTo(other) == 0;
    }
}
