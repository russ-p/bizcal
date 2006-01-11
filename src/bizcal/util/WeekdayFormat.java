package bizcal.util;

import java.text.DateFormat;
import java.text.FieldPosition;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;

public class WeekdayFormat
	extends DateFormat
{
	private static final long serialVersionUID = 1L;
	
	private int length;
	private SimpleDateFormat format;
	
	public WeekdayFormat(int length)
	{
		format = new SimpleDateFormat("EEEEE");
		this.length = length;
	}
	
	public StringBuffer format(Date date,
            StringBuffer toAppendTo,
            FieldPosition fieldPosition)
	{
		StringBuffer str = format.format(date, toAppendTo, fieldPosition);
		if (str.length() > length) {
			str = new StringBuffer(str.substring(0, length));		
		}
		return str;
	}
	
	public Date parse(String source,
            ParsePosition pos)
	{
		return format.parse(source, pos); 
	}
}
