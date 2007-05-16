package bizcal.common;

import bizcal.util.TimeOfDay;

public class DayViewConfig
	extends CalendarViewConfig
{
	private int dayCount = 1;
	private boolean showExtraDateHeaders = true;
	private boolean showDateFooter = false;
	private TimeOfDay startView;
	private TimeOfDay endView;

	public DayViewConfig()
		throws Exception
	{
		startView = new TimeOfDay(7, 0);
		endView = new TimeOfDay(12, 0);

		setCaption("Calendar");
	}

	public DayViewConfig(CalendarViewConfig calViewD)
		throws Exception
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

	public void setShowDateFooter(boolean showDateFooter) {
		this.showDateFooter = showDateFooter;
	}

	public boolean isShowDateFooter() {
		return showDateFooter;
	}

	@Override
	public TimeOfDay getEndView() {
		return endView;
	}
	@Override
	public void setEndView(TimeOfDay endView) {
		this.endView = endView;
	}



}
