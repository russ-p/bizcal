package bizcal.swing.util;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.EventListener;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JComponent;

import bizcal.common.Event;
import bizcal.util.DateUtil;



public class FrameArea
	extends JComponent
{
	private static final long serialVersionUID = 1L;

	private String itsHeadLine;
	private String itsDescription;
	private Color fontColor;
	private Shape itsShape;
	private List listeners = new ArrayList();
	private boolean border;
	private boolean roundedRectangle;
	private boolean selected;
	private float alphaValue;
	private ImageIcon icon;
	//private Color selectionColor = new Color(196, 0, 0);
	private Color selectionColor = Color.BLACK;

	private Date endTime = null;

	private Date startTime = null;

	private boolean isMoving = false;

	private String movingString = "";

	private Date moveDate;

	private Event event = null;
	private List<FrameArea> children = null;


	public static final DateFormat timeFormat = new SimpleDateFormat("HH:mm");//,;
//			Translatrix.getLocale());


	public FrameArea()
	throws Exception
	{
		this.setFont(new Font("Verdana", Font.PLAIN, 10));
		// change color for drag
		this.setBackground(new Color(100,100,245));
		this.fontColor = Color.WHITE;
		this.alphaValue = 0.7f;
		this.border = true;
		this.roundedRectangle = true;
		this.alphaValue = 0.7f;
		this.selected = false;
	}


	public void setEvent(Event event) {
		/* ================================================== */
		this.event = event;
		/* ================================================== */
	}

	public Event getEvent() {
		/* ================================================== */
		return this.event;
		/* ================================================== */
	}


	public void addChild(FrameArea fa) {
		/* ================================================== */
		if (this.children == null)
			this.children = new ArrayList<FrameArea>();
		/* ------------------------------------------------------- */
		children.add(fa);
		/* ================================================== */
	}

	public void removeChild(FrameArea fa) {
		/* ================================================== */
		if (this.children != null)
			this.children.remove(fa);
		/* ================================================== */
	}

	public List<FrameArea> getChildren() {
		/* ================================================== */
		if (this.children == null)
			return new ArrayList<FrameArea>();
		else
			return this.children;
		/* ================================================== */
	}


	public void setIsMoving(boolean b) {
		/* ================================================== */
		this.isMoving  = b;
		/* ================================================== */
	}

	/**
	 * Sets the string that is displayed while moving
	 *
	 * @param str
	 */
	public void setMovingTimeString(Date moveDate, Date endDate) {
		/* ================================================== */
		this.moveDate = moveDate;
		this.movingString = timeFormat.format(moveDate)
			+ " - " +
			timeFormat.format(new Date(endDate.getTime() + DateUtil.getDiffDay(endDate, moveDate)));
		/* ================================================== */
	}

	public void setEndTime(Date endTime) {
		/* ================================================== */
		this.endTime = endTime;
		/* ================================================== */
	}

	public void setStartTime(Date startTime) {
		/* ================================================== */
		this.startTime = startTime;
		/* ================================================== */
	}

	public void setAlphaValue(float aValue)
	{
		if (aValue > 1.0f)
			aValue = 1.0f;
		this.alphaValue = aValue;
	}
	public float getAlphaValue()
	{
		return this.alphaValue;
	}

	//The toolkit will invoke this method when it's time to paint
	public void paint(Graphics g)
	{
		Graphics2D g2 = (Graphics2D) g;
		//makes the graphics smoother
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		int width = (int) getWidth();
		int height = (int) getHeight();
		BufferedImage buffImg = null;
		try {
			buffImg = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println("W: " + width + " H: " + height);

		}
        Graphics2D gbi = buffImg.createGraphics();
        gbi.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        gbi.setStroke(new BasicStroke(1.0f));

		if(this.roundedRectangle)
		{
			AlphaComposite ac = AlphaComposite.getInstance(AlphaComposite.DST_OVER, alphaValue);
			gbi.setComposite(ac);
			gbi.setPaint(this.getBackground());
			gbi.fill(new RoundRectangle2D.Double(0, 0,
	              width,
				  height,
	              20, 20));
			if(this.border)
			{
				gbi.setPaint(Color.black);
				gbi.draw(new RoundRectangle2D.Double(1, 1, width-2, height-2, 17,17));
			}
		}
		else
		{
			AlphaComposite ac = null;
			ac = AlphaComposite.getInstance(AlphaComposite.DST_OVER, alphaValue);
			gbi.setComposite(ac);
			gbi.setPaint(this.getBackground());
			gbi.fill(new Rectangle2D.Double(0, 0,
		              width,
					  height));
			if(this.border)
			{
				gbi.setPaint(Color.black);
				gbi.draw(new Rectangle2D.Double(1, 1, width-2, height-2));
			}
		}

		g2.drawImage(buffImg, null, 0, 0);
		/* ------------------------------------------------------- */
		// create darker header
		BufferedImage buffImgHeader = new BufferedImage(width, 20, BufferedImage.TYPE_INT_ARGB);
        Graphics2D gbiHeader = buffImgHeader.createGraphics();
        gbiHeader.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        gbiHeader.setStroke(new BasicStroke(1.0f));


//        AlphaComposite ac = null;
//		ac = AlphaComposite.getInstance(AlphaComposite.DST_OVER, alphaValue);
//		gbiHeader.setComposite(ac);

        gbiHeader.setPaint(this.getBackground());
        gbiHeader.fill(new RoundRectangle2D.Double(0, 0,
	              width,
				  20,
	              20, 20));


        g2.drawImage(buffImgHeader, null, 0, 0);

		/* ------------------------------------------------------- */
		int xpos = 5;
		if (icon != null) {
			g2.drawImage(icon.getImage(), xpos, 3, this);
			xpos += icon.getIconWidth() + 3;
		}
		//actions below this point will be placed on top of the "colored glass"
		Font timeFont = this.getFont().deriveFont(Font.BOLD);
		g2.setPaint(fontColor);
		g2.setFont(timeFont);
		int ypos = 15;

		if (itsHeadLine != null) {
			g2.drawString(itsHeadLine, xpos, ypos);
			ypos += 15;
			xpos = 5;
		}
		Font descriptionFont = this.getFont();
		g2.setPaint(fontColor);
		g2.setFont(descriptionFont);
		if (itsDescription != null)
			g2.drawString(itsDescription, xpos, ypos);


		/* ------------------------------------------------------- */
		// draw end time at the bottom
		if (this.endTime != null) {
			/* ------------------------------------------------------- */
			g2.setFont(timeFont);
			g2.drawString(timeFormat.format(endTime)
//					+ " "+
//					Translatrix.getTranslationString("bizcal.clock"),
					,xpos + this.getBounds().width - 75, ypos + this.getBounds().height - 20);
			/* ------------------------------------------------------- */
		}
		/* ------------------------------------------------------- */


		/* ------------------------------------------------------- */
		// draw start time at the top
		if (this.startTime != null) {
			/* ------------------------------------------------------- */
			g2.setFont(timeFont);
			g2.drawString(timeFormat.format(startTime)
//					+ " "+
//					Translatrix.getTranslationString("bizcal.clock"),
					,xpos, ypos);
			/* ------------------------------------------------------- */
		}
		/* ------------------------------------------------------- */
		/* ------------------------------------------------------- */
		// if an event is moving, draw the new time period at the bottom
		if (this.isMoving) {
			/* ------------------------------------------------------- */

			g2.setFont(timeFont);
			g2.drawString(movingString,
					xpos + this.getBounds().width - 85, ypos + this.getBounds().height - 35);
			/* ------------------------------------------------------- */
		}



		if(this.selected)
		{

			float dash1[] = {1.0f};
			BasicStroke dashed = new BasicStroke(10.0f, BasicStroke.CAP_BUTT,
													BasicStroke.JOIN_BEVEL,
			 										10.0f, dash1, 0.0f);
			g2.setPaint(selectionColor);
			g2.setStroke(new BasicStroke(1.5f));
			if(this.roundedRectangle)
				g2.draw(new RoundRectangle2D.Double(1, 1, width-2, height-2, 17,17));
			else
				g2.draw(new Rectangle2D.Double(1, 1, width-2, height-2));
		}
		super.paint(gbiHeader);
		super.paint(g2);
		super.paint(gbi);


//		System.out.println("Start: " + startTime + " - End: "+ endTime);
	}

	/**
	 * @return Returns the border.
	 */
	public boolean isBorder() {
		return border;
	}
	/**
	 * @param border The border to set.
	 */
	public void setBorder(boolean border) {
		this.border = border;
	}

	public void setRoundedRectangle(boolean rounded)
	{
		this.roundedRectangle = rounded;
	}

	public boolean isRoundedRectangle()
	{
		return this.roundedRectangle;
	}

	/**
	 * Label text placed on the first line in the FrameArea.
	 * Example value: "08:00-11.30"
	 * @param aHeadLine
	 */
	public void setHeadLine(String aHeadLine)
	{
		itsHeadLine = aHeadLine;
		if(aHeadLine == null)
			itsHeadLine = "";
	}

	/**
	 * Label text placed below HeadLine in the FrameArea.
	 * Example value: "Meeting with group C"
	 * @param aDescription
	 */
	public void setDescription(String aDescription)
	{
		itsDescription = aDescription;
		if (aDescription == null)
			itsDescription = "";
	}

	public void setFontColor(Color aColor)
	{
		fontColor = aColor;
	}

	public Color getFontColor()
	{
		return fontColor;
	}


	public void addListener(Listener listener)
	{
		listeners.add(listener);
	}

	public static interface Listener
	extends EventListener
	{
		public void selected(FrameArea source)
		 	throws Exception;

		public void mouseOver(FrameArea source)
			throws Exception;

		public void mouseOut(FrameArea source)
			throws Exception;

		public void popupMenu(FrameArea source)
			throws Exception;

		public void moved(Point pos1, Point pos2)
			throws Exception;
	}

	public void setSelected(boolean flag)
	{
		this.selected = flag;
		repaint();
	}

	public boolean isSelected()
	{
		return this.selected;
	}
	public ImageIcon getIcon() {
		return icon;
	}
	public void setIcon(ImageIcon icon) {
		this.icon = icon;
	}

}
