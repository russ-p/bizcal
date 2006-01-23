package bizcal.common;

public class DayViewConfig
	extends CalendarViewConfig
{
	private int dayCount = 1;
	
	public DayViewConfig()
	{
		setCaption("Calendar");
	}
	
	public DayViewConfig(CalendarViewConfig calViewD)
	{
		copy(calViewD);
	}

	public int getDayCount() {
		return dayCount;
	}
	public void setDayCount(int dayCount) {
		this.dayCount = dayCount;
	}
	
}
