package bizcal.util;

import java.util.Calendar;

public interface CalendarFactory 
{
	public Calendar newCalendar()
		throws Exception;
}
