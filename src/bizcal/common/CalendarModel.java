package bizcal.common;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import bizcal.util.BizcalException;
import bizcal.util.DateInterval;
import bizcal.util.DateUtil;
import bizcal.util.TimeOfDay;

public interface CalendarModel
{	
	public List getEvents(Object calId)
		throws Exception;
	
	public List getCalendars()
		throws Exception;

	public List getSelectedCalendars()
		throws Exception;
	
	public void refresh()
		throws Exception;
	
	public void deleteCalendar(Object id)
		throws Exception;
	
	public long getResolution()
		throws Exception;
	
	public DateInterval getInterval()
		throws Exception;
	
	public List getColorDescriptions()
		throws Exception;
		
	public boolean isInsertable(Object id, Date date) 
		throws Exception;
	
	public boolean isRedDay(Date date)
		throws Exception;
	
	public TimeOfDay getViewStart()
		throws Exception;
	
	public TimeOfDay getViewEnd()
		throws Exception;

	public abstract class BaseImpl
		implements CalendarModel
	{
		private int sunday = Calendar.SUNDAY;
		private TimeOfDay viewStart;
		private TimeOfDay viewEnd;
		private DateInterval interval;
		
		public BaseImpl()
		{
			try {
				viewStart = new TimeOfDay(7, 0);
				viewEnd = new TimeOfDay(18, 0);
			} catch (Exception e) {
				throw BizcalException.create(e);
			}
		}
		
		public List getCalendars() throws Exception
		{
			return new ArrayList();
		}
		
		public List getSelectedCalendars()
			throws Exception
		{
			return new ArrayList();			
		}
		

		public void refresh() throws Exception
		{			
		}

		public void deleteCalendar(Object id) throws Exception
		{			
		}
		
		public long getResolution()
		{
			return 15 * 60 * 1000;
		}
		
		public List getColorDescriptions() throws Exception

		{
			return new ArrayList();
		}

		public boolean isInsertable(Object id, Date date) 
		throws Exception
		{
			return true;
		}

		public boolean isRedDay(Date date)
		throws Exception
		{
			return DateUtil.getDayOfWeek(date) == sunday;
		}

		public TimeOfDay getViewStart()
		throws Exception
		{
			return viewStart;
		}
	
		public TimeOfDay getViewEnd()
		throws Exception
		{
			return viewEnd;			
		}
		
		public void setViewStart(TimeOfDay value)
		{
			viewStart = value;
		}

		public void setViewEnd(TimeOfDay value)
		{
			viewEnd = value;
		}

		public DateInterval getInterval() {
			return interval;
		}

		public void setInterval(DateInterval interval) {
			this.interval = interval;
		}
		
	}
		
}
