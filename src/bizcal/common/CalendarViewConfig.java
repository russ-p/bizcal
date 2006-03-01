package bizcal.common;

import java.awt.Color;
import java.awt.Font;

import bizcal.util.TimeOfDay;

public class CalendarViewConfig 
{
	private CalendarModel callback;
	private boolean showTopHeader = true;
	private Font font = new Font("Verdana", Font.PLAIN, 10);
	private Color primaryColor = new Color(182,202,184);
	private Color secondaryColor = new Color(255,255,255);
	private String caption;
	private Color lineColor = new Color(200, 200, 200);
	private Color lineColor2 = new Color(150, 150, 150);
	private Color lineColor3 = new Color(100, 100, 100);
	private TimeOfDay startView;
	private TimeOfDay endView;
	
	public CalendarViewConfig()
		throws Exception
	{
		startView = new TimeOfDay(6, 0);
		endView = new TimeOfDay(18, 0);
	}
	
	public String getCaption() {
		return caption;
	}
	public void setCaption(String caption) {
		this.caption = caption;
	}
	public CalendarModel getCallback() {
		return callback;
	}
	public void setCallback(CalendarModel callback) {
		this.callback = callback;
	}

	public boolean isShowTopHeader() {
		return showTopHeader;
	}
	public void setShowTopHeader(boolean showTopHeader) {
		this.showTopHeader = showTopHeader;
	}
	public Font getFont() {
		return font;
	}
	public void setFont(Font font) {
		this.font = font;
	}
	
	public void copy(CalendarViewConfig other)
	{
		this.callback = other.callback;
		this.font = other.font;
		this.showTopHeader = other.showTopHeader;
		this.startView = other.startView;
		this.endView = other.endView;
	}
	
	public Color getPrimaryColor() {
		return primaryColor;
	}
	public void setPrimaryColor(Color primaryColor) {
		this.primaryColor = primaryColor;
	}
	public Color getSecondaryColor() {
		return secondaryColor;
	}
	public void setSecondaryColor(Color secondaryColor) {
		this.secondaryColor = secondaryColor;
	}
	public Color getLineColor() {
		return lineColor;
	}
	public void setLineColor(Color lineColor) {
		this.lineColor = lineColor;
	}
	public TimeOfDay getEndView() {
		return endView;
	}
	public void setEndView(TimeOfDay endView) {
		this.endView = endView;
	}
	public TimeOfDay getStartView() {
		return startView;
	}
	public void setStartView(TimeOfDay startView) {
		this.startView = startView;
	}

	public Color getLineColor2() {
		return lineColor2;
	}

	public void setLineColor2(Color lineColor2) {
		this.lineColor2 = lineColor2;
	}

	public Color getLineColor3() {
		return lineColor3;
	}

	public void setLineColor3(Color lineColor3) {
		this.lineColor3 = lineColor3;
	}
}
