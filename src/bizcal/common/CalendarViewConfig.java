package bizcal.common;

import java.awt.Color;
import java.awt.Font;

public class CalendarViewConfig 
{
	private CalendarModel callback;
	private boolean showTopHeader = true;
	private Font font = new Font("Verdana", Font.PLAIN, 10);
	private Color primaryColor = new Color(182,202,184);
	private Color secondaryColor = new Color(255,255,255);
	private String caption;
	private Color lineColor = Color.LIGHT_GRAY;
	
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
}
