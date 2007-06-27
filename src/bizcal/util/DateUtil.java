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
package bizcal.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;

/**
 * @author Fredrik Bertilsson
 */
public class DateUtil
{
	private static CalendarFactory calFactory =
		new DefaultCalendarFactory();

	public static Date round2Day(Date date)
		throws Exception
	{
		Calendar cal = newCalendar();
		cal.setTime(date);
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		return cal.getTime();
	}

	/**
	 * Rounds the date to the given hour. Minutes and seconds are also 0.
	 *
	 * @param date
	 * @param hour
	 * @return
	 */
	public static Date round2Hour(Date date, int hour)
			throws Exception
	{
		/* ================================================== */
		Calendar cal = newCalendar();
		cal.setTime(date);
		if (hour > -1 && hour < 25) {
			cal.set(Calendar.HOUR_OF_DAY, hour);
			cal.set(Calendar.MINUTE, 0);
			cal.set(Calendar.SECOND, 0);
			cal.set(Calendar.MILLISECOND, 0);
		} else
			throw new Exception("Bad hour " + hour);

		return cal.getTime();
		/* ================================================== */
	}


	public static Date round2Minute(Date date)
		throws Exception
	{
		Calendar cal = newCalendar();
		cal.setTime(date);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
	    return cal.getTime();
	}

	/**
	 * Returns the day of the week as int
	 * 
	 * @param date
	 * @return
	 */
	public static int getDayOfWeek(Date date)
	{
		Calendar cal = newCalendar();
		cal.setTime(date);
		return cal.get(Calendar.DAY_OF_WEEK);

	}

	public static String getWeekday(Date date)
		throws Exception
	{
	    DateFormat format = new SimpleDateFormat("EEEEE", Locale.getDefault());
        format.setTimeZone(TimeZone.getDefault());
	    return format.format(date);
	}
	
	/**
	 * returns the current hour of the date
	 * 
	 * @param date
	 * @return
	 */
	public static int getHourOfDay(Date date) {
		/* ================================================== */
		Calendar cal = newCalendar();
		cal.setTime(date);
		return cal.get(Calendar.HOUR_OF_DAY);
		/* ================================================== */
	}
	

	public static TimeOfDay getTimeOfDay(Date date)
		throws Exception
	{
		return new TimeOfDay(date.getTime() - round2Day(date).getTime());
	}

	public static Date getStartOfWeek(Date date)
	{
		Calendar cal = newCalendar();
		cal.setTime(date);
		cal.set(Calendar.DAY_OF_WEEK, cal.getFirstDayOfWeek());
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		return cal.getTime();
	}

	/**
	 * Sets the weekday to the current date
	 * 
	 * @param date
	 * @param weekday
	 * @return
	 */
	public static Date setDayOfWeek(Date date, int weekday) {
		/* ================================================== */
		Calendar cal = newCalendar();
		cal.setTime(date);
		cal.set(Calendar.DAY_OF_WEEK, weekday);
		
		return cal.getTime();
		/* ================================================== */
	}
	
	
	public static int getYear(Date date) throws Exception
    {
		Calendar cal = newCalendar();
        cal.setTime(date);
        return cal.get(Calendar.YEAR);

    }

	/**
	 * @param date
	 * @return
	 * @throws Exception
	 */
	public static int getMonth(Date date) throws Exception
    {
		Calendar cal = newCalendar();
        cal.setTime(date);
        return cal.get(Calendar.MONTH);
    }

	/**
	 * @param date
	 * @return
	 * @throws Exception
	 */
	public static int getDayOfMonth(Date date) throws Exception
    {
		Calendar cal = newCalendar();
        cal.setTime(date);
        return cal.get(Calendar.DAY_OF_MONTH);
    }

	/**
	 * Returns the date in distance to the given one, according to the offset (diff) given in days.<br/>
	 * Example:<br/> <strong>date =</strong> 2007-03-23 (Friday)<br/>
	 * <strong>diff = </strong>3<br>
	 * <strong>getDiffDay = </strong> 2007-03-20 (Tuesday)
	 *
	 * @param date
	 * @param diff
	 * @return
	 * @throws Exception
	 */
	public static Date getDiffDay(Date date, int diff)
		throws Exception
	{
		Calendar cal = newCalendar();
        cal.setTime(date);
		cal.add(Calendar.DAY_OF_WEEK, diff);
		return cal.getTime();
	}

	/**
	 * Returns the diff of the two dates
	 *
	 * @param oldDate
	 * @param newDate
	 * @return
	 */
	public static long getDiffDay(Date oldDate, Date newDate) {
		/* ================================================== */
		long diff = 0;
			// if moved to a later date
			if (newDate.getTime() > oldDate.getTime()) {
				/* ------------------------------------------------------- */
				diff = newDate.getTime() - oldDate.getTime();

				/* ------------------------------------------------------- */
			} else {
				/* ------------------------------------------------------- */
				diff = (-1)*(oldDate.getTime() - newDate.getTime());
				/* ------------------------------------------------------- */
			}
			return diff;
		/* ================================================== */
	}

	
	/**
	 * Returns the number of days that are between the two.
	 * [startDay,endDay]
	 * week days.
	 * 
	 * @param startDay
	 * @param endDay
	 * @return
	 */
	public static int getDiffDay(int startDay, int endDay) {
		/* ================================================== */
		// if the startday is greater than the end day
		// add 7 days to the end plus one to inlcude the end day
		if (startDay > endDay)
			endDay = endDay + 7;
		/* ------------------------------------------------------- */
		return endDay - startDay + 1;
		/* ================================================== */
	}
	
	
	public static boolean isSameDay(Date d1, Date d2) {
		GregorianCalendar cal1 = new GregorianCalendar();
		cal1.setTime(d1);
		GregorianCalendar cal2 = new GregorianCalendar();
		cal2.setTime(d2);
		if (cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR))
			return true;
		return false;
	}



	public static int getDateDiff(Date date2, Date date1) throws Exception {
		return (int) ((date2.getTime() - date1.getTime()) / 24 / 3600 / 1000);
	}

	public static Date setTimeOfDate(Date date, TimeOfDay time)
			throws Exception {
		Calendar cal = newCalendar();
		cal.setTime(date);
		cal.set(Calendar.HOUR_OF_DAY, time.getHour());
		cal.set(Calendar.MINUTE, time.getMinute());
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		return cal.getTime();

	}

	public static Date round2Week(Date date) throws Exception {
		Calendar cal = newCalendar();
		cal.setTime(date);
		cal.set(Calendar.DAY_OF_WEEK, cal.getFirstDayOfWeek());
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		return cal.getTime();
	}

	public static Date round2Month(Date date) throws Exception {
		Calendar cal = newCalendar();
		cal.setTime(date);
		cal.set(Calendar.DAY_OF_MONTH, 1);
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		return cal.getTime();
	}


	/**
	 * Creates an array of dates in distance of the minutes offset
	 * @param minOffset
	 * @return
	 */
	public static Date[] createDateList(int minOffset) {
		/* ================================================== */
		if (minOffset < 0)
			minOffset = 0;
		if (minOffset > 60)
			minOffset = 60;
		/* ------------------------------------------------------- */
		try {
			/* ------------------------------------------------------- */
			Calendar cal = newCalendar();
			cal.set(Calendar.DAY_OF_MONTH, 0);
			cal.set(Calendar.MONTH, 0);
			cal.set(Calendar.HOUR_OF_DAY, 0);
			cal.set(Calendar.MINUTE, 0);
			cal.set(Calendar.SECOND, 0);
			cal.set(Calendar.MILLISECOND, 0);
			
			int capacity = 60*24 / minOffset;
			
			Date[] dates = new Date[capacity];

			int limit = 60 / minOffset;
			
			int count = 1;
			for (int i = 0; i < capacity; i++) {
				/* ------------------------------------------------------- */
				dates[i] = cal.getTime();
				
				cal.roll(Calendar.MINUTE, minOffset);
				if (count == limit) {
					cal.roll(Calendar.HOUR_OF_DAY, 1);
					count = 0;
				}
				count++;
				
				/* ------------------------------------------------------- */
			}
			return dates;
			/* ------------------------------------------------------- */
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		/* ================================================== */
	}
	
	/**
	 * Creates a Date object with the current local time
	 * and the given type and value. 
	 * 
	 * @param value
	 * @return
	 */
	public static Date createDate(int type, int value) {
		/* ================================================== */
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.DAY_OF_MONTH, 0);
		cal.set(Calendar.MONTH, 0);
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		
		cal.set(type, value);
		return cal.getTime();
		/* ================================================== */
	}
	
	
	public static Calendar newCalendar()
	{
		return calFactory.newCalendar();
	}

	public static void setCalendarFactory(CalendarFactory factory)
	{
		calFactory = factory;
	}

	private static class DefaultCalendarFactory
		implements CalendarFactory
	{
		public Calendar newCalendar()
		{
			Calendar cal = Calendar.getInstance(LocaleBroker.getLocale());
	        cal.setTimeZone(TimeZoneBroker.getTimeZone());
			return cal;
		}
	}


}
