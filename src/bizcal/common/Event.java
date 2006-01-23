package bizcal.common;

import java.awt.Color;
import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import javax.swing.ImageIcon;

import bizcal.util.LocaleBroker;

/**
 * @author Fredrik Bertilsson
 */
public class Event 
{
	private Object id;
	private String summary;
	private String description;
	private Date start;
	private Date end;	
	private int level = 0;
	private Color color = Color.LIGHT_GRAY;
	private boolean frame = true;
	private boolean roundedCorner = true;
	private boolean editable = true;
	private boolean showTime = true;
	private String toolTip = null;
	private Map props = new HashMap();
	private boolean background = false;
	private boolean selectable = true;
	private ImageIcon icon = null;
	private Object orgEvent;
	
	public Object getId() {
		return id;
	}
	public void setId(Object id) {
		this.id = id;
	}
	
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public Date getEnd() {
		return end;
	}
	public void setEnd(Date end) {
		this.end = end;
	}
	public Date getStart() {
		return start;
	}
	public void setStart(Date start) {
		this.start = start;
	}
	public String getSummary() {
		return summary;
	}
	public void setSummary(String summary) {
		this.summary = summary;
	}

	public Color getColor() {
		return color;
	}
	public void setColor(Color color) {
		this.color = color;
	}
	public boolean isFrame() {
		return frame;
	}
	public void setFrame(boolean frame) {
		this.frame = frame;
	}
	public int getLevel() {
		return level;
	}
	public void setLevel(int level) {
		this.level = level;
	}
	public boolean isRoundedCorner() {
		return roundedCorner;
	}
	public void setRoundedCorner(boolean roundedCorner) {
		this.roundedCorner = roundedCorner;
	}
	public boolean isEditable() {
		return editable;
	}
	public void setEditable(boolean editable) {
		this.editable = editable;
	}
	
	public boolean equals(Event event)
	{
		return getId().equals(event.getId());
	}
	
	public void set(String property, Object value)
	{
		props.put(property, value);
	}
	
	public Object get(String property)
	{
		return props.get(property);
	}

	public boolean isShowTime() {
		return showTime;
	}
	public void setShowTime(boolean showTime) {
		this.showTime = showTime;
	}
	
	public String getToolTip()
		throws Exception
	{
		if (toolTip != null)
			return toolTip;
		DateFormat format = DateFormat.getTimeInstance(DateFormat.SHORT, LocaleBroker.getLocale());
		return "[" + format.format(getStart()) + 
			"-" + format.format(getEnd()) + "] " + 
			summary;
	}
	public void setToolTip(String toolTip) {
		this.toolTip = toolTip;
	}
	
	public boolean isBackground() {
		return background;
	}
	public void setBackground(boolean background) {
		this.background = background;
	}
	
	public boolean isSelectable() {
		return selectable;
	}
	public void setSelectable(boolean selectable) {
		this.selectable = selectable;
	}	
	
	public String toString()
	{
		DateFormat format = 
			DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT, new Locale("sv")); 
		format.setTimeZone(TimeZone.getTimeZone("CET"));
		return format.format(start) + " - " + format.format(end) + " : " + summary; 
	}
	
	public ImageIcon getIcon() {
		return icon;
	}
	public void setIcon(ImageIcon icon) {
		this.icon = icon;
	}
	
	public Object getOrgEvent() {
		return orgEvent;
	}
	public void setOrgEvent(Object orgEvent) {
		this.orgEvent = orgEvent;
	}
}
