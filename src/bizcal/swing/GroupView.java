package bizcal.swing;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.LayoutManager;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.swing.JComponent;
import javax.swing.JLabel;

import net.fortuna.ical4j.model.component.VEvent;
import bizcal.common.CalendarViewConfig;
import bizcal.common.Event;
import bizcal.swing.util.FrameArea;
import bizcal.swing.util.GradientArea;
import bizcal.util.BizcalException;
import bizcal.util.DateInterval;
import bizcal.util.DateUtil;
import bizcal.util.TextUtil;

/**
 * @author Fredrik Bertilsson
 */
public class GroupView
	extends CalendarView
{
	private static final int CAPTION_ROW_HEIGHT0 = 20;
	private static final int LABEL_COL_WIDTH = 70;
	
	private GradientArea topGradientArea;
	private GradientArea leftGradientArea;
	private List frameAreaRows = new ArrayList();
	private List eventRows = new ArrayList();
	private Map vLines = new HashMap();
	private List hLines = new ArrayList();
	private Map dayLabels = new HashMap();
	private List calLabels = new ArrayList();
	
	public GroupView(CalendarViewConfig desc) throws Exception	
    {
		super(desc);
		font = new Font("Verdana", Font.PLAIN, 10);
	    
		Date start = new Date();
		start = DateUtil.round2Month(start);
    }
	
	public void refresh0() throws Exception
    {		
		if(calPanel==null)
			return;
		calPanel.removeAll();
        calPanel.setBackground(Color.WHITE);
               
        frameAreaRows.clear();
        eventRows.clear();
        hLines.clear();
        dayLabels.clear();
        calLabels.clear();
        vLines.clear();

        Iterator i = getSelectedCalendars().iterator();
        while (i.hasNext()) {
        	bizcal.common.Calendar cal = 
        		(bizcal.common.Calendar) i.next();
        	Object calId = cal.getId();
        	String calHeader = cal.getSummary();
        	calHeader = StringLengthFormater.formatNameString(calHeader, font, LABEL_COL_WIDTH-5);
            
           	JLabel header = new JLabel(" " + calHeader);
           	
           	header.setAlignmentY(2);
            header.setFont(font);
            calLabels.add(header);
            calPanel.add(header);
        	            	            
            JLabel hLine = new JLabel();
            Color lineColor = new Color(240, 240, 240);
            hLine.setBackground(lineColor);
            hLine.setOpaque(true);
            calPanel.add(hLine);
            hLines.add(hLine);

            List frameAreas = new ArrayList();
            frameAreaRows.add(frameAreas);

            List events = broker.getEvents(calId);
             
            eventRows.add(events);
            Iterator j = events.iterator();
            while (j.hasNext()) 
            {
                Event event = (Event) j.next();
                FrameArea area = createFrameArea(calId, event);
                frameAreas.add(area);
               	calPanel.add(area);
            }           
        }		

        DateFormat dateFormat = new SimpleDateFormat("dd");

        Calendar cal = Calendar.getInstance(Locale.getDefault());
        cal.setTime(getInterval().getStartDate());
        while (cal.getTime().getTime() < getInterval().getEndDate().getTime()) {
            Date date = cal.getTime();
            String timeTxt = dateFormat.format(date);
            JLabel timeLabel = new JLabel(timeTxt, JLabel.CENTER);
            timeLabel.setFont(font);
            calPanel.add(timeLabel);
            dayLabels.put(date, timeLabel);

            // Day line
            JLabel line = new JLabel();
            line.setBackground(new Color(240, 240, 240));
            line.setOpaque(true);
            calPanel.add(line);
            vLines.put(date, line);

            cal.add(Calendar.DAY_OF_MONTH, 1);
            //pos += 24 * 3600 * 1000;
            
        }

        topGradientArea = new GradientArea(GradientArea.TOP_BOTTOM, new Color(
                255, 255, 255), new Color(245, 245, 245));
        topGradientArea.setOpaque(true);
        calPanel.add(topGradientArea);
        
        leftGradientArea = new GradientArea(GradientArea.LEFT_RIGHT, new Color(
                255, 255, 255), new Color(245, 245, 245));
        leftGradientArea.setOpaque(true);
        calPanel.add(leftGradientArea);
       
        calPanel.validate();
        calPanel.repaint();
    }
	
	private int getWidth()
	{
		return calPanel.getWidth();
	}
	
	private int getHeight()
	{
		return calPanel.getHeight();
	}
	
	protected int getCaptionRowHeight()
	{
		return CAPTION_ROW_HEIGHT0;		
	}	
	
	protected int getXOffset()
	{
		return LABEL_COL_WIDTH;
	}
	
	private int getTimeHeight()
	{
		return getHeight() - getCaptionRowHeight();
	}
	
	private int getTimeWidth()
	{
		return getWidth() - getXOffset();
	}
	
	private int getRowHeight()
	throws Exception
	{
		return getTimeHeight() / getSelectedCalendars().size();
	}
	
	private int getXPos(Date date)
		throws Exception
	{
		long duration = getInterval().getDuration();
		long time = date.getTime() - getInterval().getStartDate().getTime();
		double ratio = ((double) time) / ((double) duration);
		int width = getTimeWidth();		
		return (int) (getXOffset() + ratio * width);
	}
	
	private int getDayWidth()
		throws Exception
	{
		long duration = getInterval().getDuration();
		duration = duration / 24 / 3600 / 1000;
		return (int) (getTimeWidth() / duration);
	}
	
	protected LayoutManager getLayout()
	{
		return new Layout();
	}
	
	private class Layout implements LayoutManager
    {
        public void addLayoutComponent(String name, Component comp)
        {
        }

        public void removeLayoutComponent(Component comp)
        {
        }

        public Dimension preferredLayoutSize(Container parent)
        {
            return parent.getSize();
        }

        public Dimension minimumLayoutSize(Container parent)
        {
            return new Dimension(50, 100);
        }

        public void layoutContainer(Container parent0)
        {
            try {
                int width = getWidth();
                int height = getHeight();
                int yoffset = getCaptionRowHeight();
                int rowHeight = getRowHeight();
                DateInterval day = getInterval();
                
                int xoffset = getXOffset();
                double panelHeight = getTimeHeight();

                topGradientArea.setBounds(0, 0, width, yoffset);
                leftGradientArea.setBounds(0, yoffset, xoffset, height
                        - yoffset);

                int yPos = yoffset;
                for (int i = 0; i < eventRows.size(); i++) {
                	JLabel calLabel = 
                		(JLabel) calLabels.get(i);
                	calLabel.setBounds(0, yPos, LABEL_COL_WIDTH, rowHeight);
                	
                	List areas = (List) frameAreaRows.get(i);
                	List events = (List) eventRows.get(i);
                	for (int j=0; j < areas.size(); j++) {
                		FrameArea area = (FrameArea) areas.get(j);
                		Event event = (Event) events.get(j);
                		int x1 = getXPos(event.getStart());
                		int x2 = getXPos(event.getEnd());
                		area.setBounds(x1, 
                				yPos, 
								x2-x1, 
								rowHeight);                		
                	}
                	
                	JLabel hLine = (JLabel) hLines.get(i);
                	hLine.setBounds(0, yPos + rowHeight, width, 1);
                	
					yPos += rowHeight;
                }
                
                int dayWidth = getDayWidth(); 
                //long pos = getInterval().getStartDate().getTime();
                Calendar cal = Calendar.getInstance(Locale.getDefault());
                cal.setTime(getInterval().getStartDate());
                while (cal.getTime().getTime() < getInterval().getEndDate().getTime()) {
                    Date date = cal.getTime();
                    JLabel timeLabel = (JLabel) dayLabels.get(date);
                    int xpos = getXPos(date);
                    timeLabel.setBounds(xpos, 0, dayWidth, getCaptionRowHeight());
                    
                    JLabel line = (JLabel) vLines.get(date);
                    line.setBounds(xpos, 0, 1, height);
                    if(cal.get(Calendar.DAY_OF_WEEK) == Calendar.MONDAY)
                    	line.setBackground(Color.LIGHT_GRAY);
                    
                    cal.add(Calendar.DAY_OF_MONTH, 1);
                    //pos += 24 * 3600 * 1000;
                }
            } catch (Exception e) {
                throw BizcalException.create(e);
            }
        }
    }
		
	protected Date getDate(int xPos, int yPos)
		throws Exception
	{
		int width = getWidth() - getXOffset();
		xPos -= getXOffset();
		double ratio = (double) xPos / (double) width;
		long time = (long) (getInterval().getStartDate().getTime() + ratio * getInterval().getDuration());
		return new Date(time);
	}
	
	protected Object getCalendarId(int x, int y) throws Exception {
		int height = getHeight() - getCaptionRowHeight();
		double ratio = (double) y / (double) height;
		int pos = (int) (ratio * (getSelectedCalendars().size()-1));
		return (String) getSelectedCalendars().get(pos);
	}
	
	public long getTimeInterval()
	throws Exception
	{
		return 24*3600*1000*30;
	}
		
	protected String getHeaderText()
		throws Exception
	{		
		Date from = getInterval().getStartDate();
		Calendar date = Calendar.getInstance(Locale.getDefault());
		date.setTime(getInterval().getEndDate());
		date.add(Calendar.DATE, -1);
		Date tom = date.getTime();
		DateFormat format = new SimpleDateFormat("MMMM yyyy", Locale.getDefault());
		return TextUtil.formatCase(format.format(from));
	}
}
