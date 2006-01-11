package bizcal.common;

import bizcal.util.TimeOfDay;

public class DayViewConfig
	extends CalendarViewConfig
{
	private int dayCount = 1;
	private boolean showDateEditor = false;
	private boolean summedDay = false;
	private boolean summedWeek = false;
	private boolean summedTotal = false;
	private String statusBarDayHeader = "Day";
	private String statusBarWeekHeader = "Week";
	private String statusTotalHeader = "Total";
	
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
	
	public boolean isShowDateEditor() {
		return showDateEditor;
	}
	public void setShowDateEditor(boolean showDateEditor) {
		this.showDateEditor = showDateEditor;
	}
	
	public boolean isSummedDay() {
		return summedDay;
	}
	public void setSummedDay(boolean summedDay) {
		this.summedDay = summedDay;
	}
	public boolean isSummedTotal() {
		return summedTotal;
	}
	public void setSummedTotal(boolean summedTotal) {
		this.summedTotal = summedTotal;
	}
	public boolean isSummedWeek() {
		return summedWeek;
	}
	public void setSummedWeek(boolean summedWeek) {
		this.summedWeek = summedWeek;
	}
	
	public String getStatusBarDayHeader() {
		return statusBarDayHeader;
	}
	public void setStatusBarDayHeader(String statusBarDayHeader) {
		this.statusBarDayHeader = statusBarDayHeader;
	}
	public String getStatusBarWeekHeader() {
		return statusBarWeekHeader;
	}
	public void setStatusBarWeekHeader(String statusBarWeekHeader) {
		this.statusBarWeekHeader = statusBarWeekHeader;
	}
	public String getStatusBarTotalHeader() {
		return statusTotalHeader;
	}
	public void setStatusBarTotalHeader(String statusTotalWeekHeader) {
		this.statusTotalHeader = statusTotalWeekHeader;
	}
}
