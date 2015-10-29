/*******************************************************************************
 * Bizcal is a component library for calendar widgets written in java using swing.
 * Copyright (C) 2007  Frederik Bertilsson 
 * Contributors:       Martin Heinemann martin.heinemann(at)tudor.lu
 * 
 * http://sourceforge.net/projects/bizcal/
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 * 
 * [Java is a trademark or registered trademark of Sun Microsystems, Inc. 
 * in the United States and other countries.]
 * 
 *******************************************************************************/
package bizcal.demo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.swing.JFrame;

import bizcal.common.Calendar;
import bizcal.common.CalendarModel;
import bizcal.common.DayViewConfig;
import bizcal.common.Event;
import bizcal.swing.DayView;
import bizcal.util.DateInterval;
import bizcal.util.DateUtil;

public class BizcalDemo
{
	public static void main(String[] args)
		throws Exception
	{
		// Create a new config. It can be used to change the display behaviour.
		// E.g. you can change the initial position of the scrollbar
		DayViewConfig config = new DayViewConfig();
		
		// The DayView class creates the GUI. Use the getComponent() method to
		// retrieve the Swing component to embed it into a frame.
		DayView dayView = new DayView(config);
		
		// MyEventModel is an implementation of the CalendarModel interface.
		// It will be used by Bizcal to fetch data like the events or number of
		// days to show.
		MyEventModel model = new MyEventModel();
		dayView.setModel(model);
		
		// Set all active calendars. In this case there is only one calendar.
		// Events are only displayed if their calendars are active.
		dayView.setActiveCalendars(model.getSelectedCalendars());
		
		// Create a container for the DayView.
		JFrame frame = new JFrame("Bizcal Demo");
		dayView.refresh();
		frame.setContentPane(dayView.getComponent());
		frame.setSize(800, 600);
		frame.setVisible(true);
	}

	private static class MyEventModel
		extends CalendarModel.BaseImpl
	{
		private List<Event> events = new ArrayList<Event>();
		private DateInterval interval;
		private Calendar cal;

		public MyEventModel()
			throws Exception
		{
			Date date = DateUtil.round2Week(new Date());
			date = new Date(date.getTime() + 8*60*60*1000);
			
			for (int i=0; i < 7; i++) {
				Event event = new Event();
				event.setStart(date);
				event.setEnd(new Date(date.getTime() + 90*60*1000));
				event.setSummary("Summary " + i);
				event.setDescription("Description " + i);
				event.setToolTip("Tooltip " + i);
				
				events.add(event);
				if (i % 2 == 0) {
					// Add the event again to show how multiple events at the
					// same time look like.
					events.add(event.copy());
				}
				
				date = DateUtil.getDiffDay(date, +1);
				date = new Date(date.getTime() + 60*60*1000);
			}
			
			// Show a full week
			Date start = DateUtil.round2Week(new Date());
			Date end = DateUtil.getDiffDay(start, 7);
			interval = new DateInterval(start, end);
			
			cal = new Calendar();
			cal.setId(1);
			cal.setSummary("Peter");
		}

		public List<Event> getEvents(Object calId)
		throws Exception
		{
			return events;
		}

		public List getSelectedCalendars()
		throws Exception
		{
			return Collections.nCopies(1, cal);
		}

		public DateInterval getInterval()
		{
			return interval;
		}


	}
}
