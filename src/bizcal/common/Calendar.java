package bizcal.common;

import java.awt.Color;
import java.awt.image.BufferedImage;

/**
 * @author Fredrik Bertilsson
 */
public class Calendar
{
	private Object id;
	private String summary;	
	private Color color = Color.WHITE;
	private BufferedImage image;
	private boolean enabled = true;
	private boolean blankIsAvailible = true;
	
	public Object getId() {
		return id;
	}
	public void setId(Object id) {
		this.id = id;
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
	
	public BufferedImage getImage() {
		return image;
	}
	public void setImage(BufferedImage image) {
		this.image = image;
	}
	
	public boolean isEnabled() {
		return enabled;
	}
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}
	
	public boolean isBlankIsAvailible() {
		return blankIsAvailible;
	}
	public void setBlankIsAvailible(boolean blankIsAvailible) {
		this.blankIsAvailible = blankIsAvailible;
	}
}
