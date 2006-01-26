package bizcal.swing;

import java.util.Date;
import java.util.List;

import bizcal.common.Event;
import bizcal.util.DateInterval;

/**
 * @author Fredrik Bertilsson
 */
public interface CalendarListener 
{
	public void dateSelected(Date date) throws Exception;

	public void dateChanged(Date date) throws Exception;		
	
	public void eventsSelected(List list) throws Exception;
	
	public void showEvent(Object id, Event event) throws Exception;

	public void newEvent(Object id, Date date) throws Exception;
	
	public void newEvent(Object id, DateInterval interval) throws Exception;
	
	public void copy(List list) throws Exception;
	
	public void paste(Object calId, Date date) throws Exception;

	public void moved(Event event, Object orgCalId, Date orgDate, Object newCalId, Date newDate)
		throws Exception;
	
	public void newCalendar()
		throws Exception;
	
	public void deleteEvent(Event event)
		throws Exception;
	
	public void closeCalendar(Object calId)
		throws Exception;

	public static class BaseImpl implements CalendarListener {
		public void dateSelected(Date date) throws Exception {
		}

		public void dateChanged(Date date) throws Exception {
		}

		public void eventsSelected(List list) throws Exception {	}
		
		public void showEvent(Object id, Event event) throws Exception
		{			
		}

		public void newEvent(Object id, Date date) throws Exception {
		}

		public void newEvent(Object id, DateInterval interval) throws Exception {
		}

		public void selected(Object id, DateInterval interval) throws Exception
		{ }

		public void copy(List list) throws Exception {
		}

		public void moved(Event event, Object orgCalId, Date orgDate,
				Object newCalId, Date newDate) throws Exception {
		}
		
		public void newCalendar()
			throws Exception
		{		
		}
		
		public void deleteEvent(Event event)
		throws Exception
		{		
		}
		
		public void paste(Object calId, Date date) throws Exception
		{			
		}
		
		public void closeCalendar(Object calId)
			throws Exception
		{			
		}
		
	}	
	
}
