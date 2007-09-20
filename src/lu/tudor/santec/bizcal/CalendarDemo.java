/*******************************************************************************
 * Copyright (c) 2007 by CRP Henri TUDOR - SANTEC LUXEMBOURG 
 * check http://www.santec.tudor.lu for more information
 *  
 * Contributor(s):
 * Johannes Hermen  johannes.hermen(at)tudor.lu                            
 * Martin Heinemann martin.heinemann(at)tudor.lu  
 *  
 * This library is free software; you can redistribute it and/or modify it  
 * under the terms of the GNU Lesser General Public License (version 2.1)
 * as published by the Free Software Foundation.
 * 
 * This software is distributed in the hope that it will be useful, but     
 * WITHOUT ANY WARRANTY; without even the implied warranty of               
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU        
 * Lesser General Public License for more details.                          
 * 
 * You should have received a copy of the GNU Lesser General Public         
 * License along with this library; if not, write to the Free Software      
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA 
 *******************************************************************************/
package lu.tudor.santec.bizcal;

import java.awt.Color;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.swing.JFrame;

import lu.tudor.santec.bizcal.listeners.NamedCalendarListener;
import lu.tudor.santec.bizcal.util.ObservableEventList;
import lu.tudor.santec.bizcal.views.DayViewPanel;
import lu.tudor.santec.bizcal.views.ListViewPanel;
import lu.tudor.santec.bizcal.views.MonthViewPanel;
import bizcal.common.Event;
import bizcal.swing.CalendarListener;
import bizcal.swing.util.FrameArea;
import bizcal.util.DateInterval;

public class CalendarDemo extends JFrame{

	private static final long serialVersionUID = 1L;
	
	
	public static final String CALENDAR_ID = "calendarId";
	
	
	private ObservableEventList eventDataList;

	private DayViewPanel dayViewPanel;

	private DayViewPanel weekViewPanel;

	private MonthViewPanel monthViewPanel;

	private ListViewPanel listViewPanel;

	private CalendarPanel calendarPanel;

	public CalendarDemo() {
		super("Calendar Demo");

		this.calendarPanel = new CalendarPanel();
		
		this.eventDataList = new ObservableEventList();
		
		EventModel dayModel = new EventModel(eventDataList, EventModel.TYPE_DAY);
		EventModel weekModel = new EventModel(eventDataList, EventModel.TYPE_WEEK);
		EventModel monthModel = new EventModel(eventDataList, EventModel.TYPE_MONTH);
		EventModel listModel = new EventModel(eventDataList, EventModel.TYPE_MONTH);


		
		/* ------------------------------------------------------- */
		this.dayViewPanel = new DayViewPanel(dayModel);
		this.weekViewPanel = new DayViewPanel(weekModel);
		this.monthViewPanel = new MonthViewPanel(monthModel);
		this.listViewPanel = new ListViewPanel(listModel);
		/* ------------------------------------------------------- */
		
		DemoCalendarListener calListener = new DemoCalendarListener();
		
		
		dayViewPanel.addCalendarListener(calListener);
		weekViewPanel.addCalendarListener(calListener);
		monthViewPanel.addCalendarListener(calListener);
		listViewPanel.addCalendarListener(calListener);

		/* ------------------------------------------------------- */
		calendarPanel.addCalendarView(dayViewPanel);
		calendarPanel.addCalendarView(weekViewPanel);
		calendarPanel.addCalendarView(monthViewPanel);
		calendarPanel.addCalendarView(listViewPanel);
		/* ------------------------------------------------------- */
		

		
		
		
		
		calendarPanel.addNamedCalendar(new TestNamedCalendar("Peter", "dem Peter seiner", Color.RED));
		calendarPanel.addNamedCalendar(new TestNamedCalendar("Max", "dem Max seiner", Color.BLUE));
		calendarPanel.addNamedCalendar(new TestNamedCalendar("Office", "allen ihrer", Color.GRAY));


		calendarPanel.addNamedCalendarListener(new NamedCalendarListener() {

			public void activeCalendarsChanged(Collection<NamedCalendar> calendars) {
				/* ====================================================== */
//				 if no calendar is active, remove all events
				/* ================================================== */
				if (calendars == null || calendars.size() < 1) {
					eventDataList.clear();
					return;
				}
				/* ------------------------------------------------------- */
				/* ================================================== */
				// fetch the appointments of the active calendars
				/* ================================================== */
				updateEventsForActiveCalendars();
				/* ====================================================== */
			}

			public void selectedCalendarChanged(NamedCalendar selectedCalendar) {
				/* ====================================================== */
				
				/* ====================================================== */
			}

		});




		this.add(calendarPanel);

		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//		this.pack();
		this.setSize(1000, 700);
		this.setVisible(true);

	}

	@SuppressWarnings("unchecked")
	private synchronized void updateEventsForActiveCalendars() {
		/* ================================================== */
		// add all events from active calendars
		List<Event> allActiveEvents = new ArrayList<Event>();

		for (NamedCalendar nc : calendarPanel.getCalendars()) {
			if (nc.isActive())
				// this is just for demonstration. You can define a time periode to 
				// get events from the calendar
				allActiveEvents.addAll(nc.getEvents(null, null));
		}

		Collections.sort(allActiveEvents);

		eventDataList.clear();
		eventDataList.addAll(allActiveEvents);
		/* ================================================== */
	}

	
	
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		new CalendarDemo();
	}
	
	
	class DemoCalendarListener implements CalendarListener {

		public void closeCalendar(Object calId) throws Exception {
			/* ====================================================== */
			// TODO Auto-generated method stub
			/* ====================================================== */
		}

		public void copy(List<Event> list) throws Exception {
			/* ====================================================== */
			// TODO Auto-generated method stub
			/* ====================================================== */
		}

		public void dateChanged(Date date) throws Exception {
			/* ====================================================== */
			// TODO Auto-generated method stub
			/* ====================================================== */
		}

		public void dateSelected(Date date) throws Exception {
			/* ====================================================== */
			// TODO Auto-generated method stub
			/* ====================================================== */
		}

		public void deleteEvent(Event event) throws Exception {
			/* ====================================================== */
			eventDataList.remove(event);
			/* ====================================================== */
		}

		public void deleteEvents(List<Event> events) {
			/* ====================================================== */
			eventDataList.removeAll(events);
			/* ====================================================== */
		}

		public void eventClicked(Object id, Event _event, FrameArea area, MouseEvent e) {
			/* ====================================================== */
			// TODO Auto-generated method stub
			/* ====================================================== */
		}

		public void eventDoubleClick(Object id, Event event, MouseEvent mouseEvent) {
			/* ====================================================== */
			// TODO Auto-generated method stub
			/* ====================================================== */
		}

		public void eventSelected(Object id, Event event) throws Exception {
			/* ====================================================== */
//			 try to find the calendar by its id
			if (calendarPanel.getCalendars() == null)
				return;
			/* ------------------------------------------------------- */
			for (NamedCalendar nc : calendarPanel.getCalendars()) {
				if (nc.getId().equals(event.get(CALENDAR_ID))) {
					/* ------------------------------------------------------- */
					calendarPanel.setSelectedCalendar(nc);
					return;
					/* ------------------------------------------------------- */
				}
			}
			/* ====================================================== */
		}

		public void eventsSelected(List<Event> list) throws Exception {
			/* ====================================================== */
			// TODO Auto-generated method stub
			/* ====================================================== */
		}

		public void moved(Event event, Object orgCalId, Date orgDate, Object newCalId, Date newDate) throws Exception {
			/* ====================================================== */
			event.move(newDate);
			eventDataList.trigger();
			/* ====================================================== */
		}

		public void newCalendar() throws Exception {
			/* ====================================================== */
			// TODO Auto-generated method stub
			/* ====================================================== */
		}

		public void newEvent(Object id, Date date) throws Exception {
			/* ====================================================== */
			// create a normal appointment lasting 15 min
			DateInterval interval = new DateInterval(date, new Date(date.getTime()+900000));
			newEvent(id, interval);
			/* ====================================================== */
		}

		public void newEvent(Object id, DateInterval interval) throws Exception {
			/* ====================================================== */

			// create an Event object
//			Event newEvent = appointment2Event(ap);

			NamedCalendar nc = calendarPanel.getSelectedCalendar();
			/* ------------------------------------------------------- */
			if (nc == null)
				return;
			/* ------------------------------------------------------- */
			Event event = new Event();
			event.setStart(interval.getStartDate());
			event.setEnd(interval.getEndDate());
			event.setId(id);
			
			nc.addEvent("clientXXX", event);
			
			/* ====================================================== */
		}

		public void paste(Object calId, Date date) throws Exception {
			/* ====================================================== */
			// TODO Auto-generated method stub
			/* ====================================================== */
		}

		public void resized(Event event, Object orgCalId, Date orgEndDate, Date newEndDate) throws Exception {
			/* ====================================================== */
			NamedCalendar nc = calendarPanel.getSelectedCalendar();
			/* ------------------------------------------------------- */
			if (nc == null)
				return;
			/* ------------------------------------------------------- */
			event.setEnd(newEndDate);

			nc.saveEvent("clientXXX", event, false);
			/* ====================================================== */
		}

		public void selectionReset() throws Exception {
			/* ====================================================== */
			// TODO Auto-generated method stub
			/* ====================================================== */
		}

		public void showEvent(Object id, Event event) throws Exception {
			/* ====================================================== */
			// TODO Auto-generated method stub
			/* ====================================================== */
		}
		
	}
	
	
	
	

	/**
	 * @author martin.heinemann@tudor.lu
	 * 27.06.2007
	 * 11:53:49
	 *
	 *
	 * @version
	 * <br>$Log: CalendarDemo.java,v $
	 * <br>Revision 1.2  2007/09/20 07:23:16  heine_
	 * <br>new version commit
	 * <br>
	 * <br>Revision 1.17  2007/06/27 14:59:55  heinemann
	 * <br>*** empty log message ***
	 * <br>
	 * <br>Revision 1.16  2007/06/27 11:59:42  heinemann
	 * <br>*** empty log message ***
	 * <br>
	 *   
	 */
	class TestNamedCalendar extends NamedCalendar {
		
		
		private List<Event> calendarEvents = new ArrayList<Event>();
		
		public TestNamedCalendar(String name, String description, Color color) {
			super(name, description, color);
			
			this.setId(this.hashCode());
			
		}

		@Override
		public List<Event> getEvents(Date from, Date to) {
			return calendarEvents;
		}

		@Override
		public void deleteEvent(String clientId, Event event) {
			/* ====================================================== */
			calendarEvents.remove(event);
			eventDataList.remove(event);
			/* ====================================================== */
		}

		@Override
		public List<Event> addEvent(String clientId, Event event) {
			/* ====================================================== */
			event.set(CALENDAR_ID, this.getId());
			event.setColor(this.getColor());
			
			eventDataList.add(event);
			calendarEvents.add(event);
			return null;
			/* ====================================================== */
		}

		@Override
		public List<Event> saveEvent(String clientId, Event event, boolean userInteraction) {
			/* ====================================================== */
			// TODO Auto-generated method stub
			return null;
			/* ====================================================== */
		}

	

	}


}
