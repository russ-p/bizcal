package bizcal.common;

public class DayViewConfig
	extends CalendarViewConfig
{
	private int dayCount = 1;
	private boolean showExtraDateHeaders = false;
	
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
	
	public boolean isShowExtraDateHeaders() {
		return showExtraDateHeaders;
	}

	public void setShowExtraDateHeaders(boolean showExtraDateHeaders) {
		this.showExtraDateHeaders = showExtraDateHeaders;
	}
	
}
