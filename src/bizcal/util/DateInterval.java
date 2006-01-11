package bizcal.util;

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * @author Fredrik Bertilsson
 */
public class DateInterval
	extends Interval
{
	private long _duration;
	
    public DateInterval(Interval interval)
    	throws Exception
    {
        setStart(interval.getStart());
        setEndDate((Date) interval.getEnd());
        setIncludeStart(interval.isIncludeStart());
        setIncludeEnd(interval.isIncludeEnd());
    }
    
    public DateInterval()
    {    	
    }
    
    public DateInterval(Date start, Date end)
    	throws Exception
    {
    	setStartDate(start);
    	setEndDate(end);
    }
    
    public DateInterval(Date day)
    	throws Exception
    {
    	Date start = DateUtil.round2Day(day);
    	setStartDate(start);
    	Date end = DateUtil.getDiffDay(start, +1);
    	setEndDate(end);
    }
    
    public DateInterval(Date day, int type) throws Exception {
		setStartDate(day);
		Calendar cal = 
			Calendar.getInstance(TimeZone.getDefault(), Locale.getDefault());
		cal.setTime(day);
		cal.add(type, +1);
		setEndDate(cal.getTime());
	}
    
    public DateInterval(Date day, int type, int count) throws Exception {
		setStartDate(day);
		Calendar cal = 
			Calendar.getInstance(TimeZone.getDefault(), Locale.getDefault());
		cal.setTime(day);
		cal.add(type, count);
		setEndDate(cal.getTime());
	}    

    public DateInterval(Date start, long duration) throws Exception {
		this(start, new Date(start.getTime() + duration));
	}    
    
    public Date getStartDate() throws Exception {
		return (Date) getStart();
	}

	public void setStartDate(Date start) throws Exception {
		setStart(start);		
	}

	public Date getEndDate() throws Exception {
		return (Date) getEnd();
	}

	public void setEndDate(Date end) throws Exception {
		setEnd(end);
		if (getStartDate() == null)
			return;
		if (end == null)
		    return;
		long diff = end.getTime() - getStartDate().getTime();
		_duration = (int) diff;
	}
		
	public void setDuration(long duration)
		throws Exception
	{
		_duration = duration;
		setEnd(new Date(getStartDate().getTime() + duration));
	}
	
	public long getDuration()
	{
		return _duration;
	}
	
	public DateInterval intersection(DateInterval interval)
		throws Exception
	{
		Interval result = intersection((Interval) interval);
		return new DateInterval((Date) result.getStart(), (Date) result.getEnd());
	}
    
}
