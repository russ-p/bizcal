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
		DayView dayView = new DayView(new DayViewConfig());
		dayView.setModel(new ThisModel());
		JFrame frame = new JFrame("Bizcal Demo");
		dayView.refresh();
		frame.setContentPane(dayView.getComponent());
		frame.setSize(800, 600);
		frame.setVisible(true);
	}
	
	private static class ThisModel
		extends CalendarModel.BaseImpl
	{
		private List events = new ArrayList();
		private DateInterval interval;
		private Calendar cal;
		
		public ThisModel()
			throws Exception
		{
			Date date = DateUtil.round2Week(new Date());
			date = new Date(date.getTime() + 8*60*60*1000);
			for (int i=0; i < 7; i++) {
				Event event = new Event();
				event.setStart(date);
				event.setEnd(new Date(date.getTime() + 90*60*1000));
				event.setSummary("Test " + i);
				events.add(event);
				date = DateUtil.getDiffDay(date, +1);
				date = new Date(date.getTime() + 60*60*1000);
			}
			Date start = DateUtil.round2Week(new Date());
			Date end = DateUtil.getDiffDay(start, +7);
			interval = new DateInterval(start, end);
			cal = new Calendar();
			cal.setId(1);
			cal.setSummary("Peter");
		}
		
		public List getEvents(Object calId)
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
