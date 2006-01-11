package bizcal.swing;

import java.util.Date;

import javax.swing.JPopupMenu;

import bizcal.common.Event;

/**
 * @author Fredrik Bertilsson
 */
public interface PopupMenuCallback
{
	public JPopupMenu getEventPopupMenu(Object calId, Event event)
		throws Exception;
	
	public JPopupMenu getCalendarPopupMenu(Object calId)
		throws Exception;
	
	public JPopupMenu getProjectPopupMenu(Object calId)
	throws Exception;

	public JPopupMenu getEmptyPopupMenu(Object calId, Date date)
		throws Exception;
	
	public static class BaseImpl
		implements PopupMenuCallback
	{
		public JPopupMenu getEventPopupMenu(Object calId, Event event)
		throws Exception
		{
			return null;
		}
	
		public JPopupMenu getCalendarPopupMenu(Object calId)
		throws Exception
		{
			return null;
		}
	
		public JPopupMenu getProjectPopupMenu(Object calId)
		throws Exception
		{
			return null;
		}

		public JPopupMenu getEmptyPopupMenu(Object calId, Date date)
		throws Exception
		{
			return null;
		}
		
	}
}
