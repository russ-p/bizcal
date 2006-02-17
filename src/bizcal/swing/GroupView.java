package bizcal.swing;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
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
import java.util.Map;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JScrollPane;

import bizcal.common.CalendarModel;
import bizcal.common.CalendarViewConfig;
import bizcal.common.Event;
import bizcal.swing.util.FrameArea;
import bizcal.util.BizcalException;
import bizcal.util.DateInterval;
import bizcal.util.DateUtil;
import bizcal.util.LocaleBroker;
import bizcal.util.TextUtil;
import bizcal.util.TimeOfDay;

/**
 * @author Fredrik Bertilsson
 */
public class GroupView
	extends CalendarView
{
	private static final int LABEL_COL_WIDTH = 70;
	private static final int HOUR_RESOLUTION = 2;
	private static final int PREFERRED_HOUR_WIDTH = 10;
	public static final int PREFERRED_ROW_HEIGHT = 20;
	
	private List frameAreaRows = new ArrayList();
	private List eventRows = new ArrayList();
	private Map vLines = new HashMap();
	private List hLines = new ArrayList();
	private JLayeredPane calPanel;
	private JScrollPane scrollPane;
	
	public GroupView(CalendarViewConfig config, CalendarModel model) throws Exception	
    {
		super(config);
		setModel(model);
		font = new Font("Verdana", Font.PLAIN, 10);	 
		calPanel = new JLayeredPane();
		calPanel.setLayout(new Layout());
        scrollPane = 
        	new JScrollPane(calPanel,
        			JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
					JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setCursor(Cursor.getDefaultCursor());
		scrollPane.getVerticalScrollBar().setUnitIncrement(15);
        scrollPane.setCorner(JScrollPane.UPPER_LEFT_CORNER, createCorner(true, true));
        scrollPane.setCorner(JScrollPane.LOWER_LEFT_CORNER, createCorner(true, false));
        scrollPane.setCorner(JScrollPane.UPPER_RIGHT_CORNER, createCorner(false, true));
		DaysHoursHeaderPanel columnHeader = new DaysHoursHeaderPanel(config, model);	
        scrollPane.setColumnHeaderView(columnHeader.getComponent());
		CalendarRowHeader rowHeader = new CalendarRowHeader(model);
		rowHeader.setFooterHeight(0);
        scrollPane.setRowHeaderView(rowHeader.getComponent());
		
    }
	
	public void refresh0() throws Exception
    {		
		calPanel.removeAll();
        calPanel.setBackground(Color.WHITE);
               
        frameAreaRows.clear();
        eventRows.clear();
        hLines.clear();
        vLines.clear();

        addDraggingComponents(calPanel);
        
        Iterator i = getModel().getSelectedCalendars().iterator();
        while (i.hasNext()) {
        	bizcal.common.Calendar cal = (bizcal.common.Calendar) i.next();
        	Object calId = cal.getId();
        	String calHeader = cal.getSummary();
        	calHeader = StringLengthFormater.formatNameString(calHeader, font, LABEL_COL_WIDTH-5);
                    	            	            
            JLabel hLine = new JLabel();
            hLine.setBackground(getDescriptor().getLineColor());
            hLine.setOpaque(true);
            calPanel.add(hLine, new Integer(1));
            hLines.add(hLine);

            List frameAreas = new ArrayList();
            frameAreaRows.add(frameAreas);

            List events = getModel().getEvents(calId);
             
            eventRows.add(events);
            Iterator j = events.iterator();
            while (j.hasNext()) 
            {
                Event event = (Event) j.next();
                FrameArea area = createFrameArea(calId, event);
                frameAreas.add(area);
               	calPanel.add(area, new Integer(event.getLevel()));
            }           
        }		

        Calendar cal = Calendar.getInstance(LocaleBroker.getLocale());
        cal.setTime(getInterval().getStartDate());
        while (cal.getTime().getTime() < getInterval().getEndDate().getTime()) {
            Date date = cal.getTime();

            // Day line
            JLabel line = new JLabel();
            line.setBackground(new Color(200, 200, 200));
            line.setOpaque(true);
            calPanel.add(line, new Integer(1));
            vLines.put(date, line);
            
            TimeOfDay startTime = getDescriptor().getStartView();
            cal.set(Calendar.HOUR_OF_DAY, startTime.getHour());
            cal.set(Calendar.MINUTE, startTime.getMinute());
            cal.set(Calendar.SECOND, 0);
            cal.set(Calendar.MILLISECOND, 0);
            TimeOfDay endTime = getDescriptor().getEndView();
            while (true) {
                line = new JLabel();
                line.setBackground(getDescriptor().getLineColor());
                line.setOpaque(true);
                calPanel.add(line, new Integer(1));
                vLines.put(cal.getTime(), line);
            	
            	cal.add(Calendar.HOUR, +1 * HOUR_RESOLUTION);
            	TimeOfDay timeOfDay = new TimeOfDay(cal.getTime());
            	if (timeOfDay.getValue() >= endTime.getValue())
            		break;
            }
            
            cal.add(Calendar.DAY_OF_YEAR, 1);
            cal.set(Calendar.HOUR_OF_DAY, 0);
            cal.set(Calendar.MINUTE, 0);
            cal.set(Calendar.SECOND, 0);
            cal.set(Calendar.MILLISECOND, 0);
            //pos += 24 * 3600 * 1000;
            
        }
       
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
		//return CAPTION_ROW_HEIGHT0 * 2;
		return 0;
	}	
	
	protected int getXOffset()
	{
		//return LABEL_COL_WIDTH;
		return 0;
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
		return getTimeHeight() / getModel().getSelectedCalendars().size();
	}
	
	private int getXPos(Date date)
		throws Exception
	{
		TimeOfDay time = new TimeOfDay(date);		
		long x = time.getValue() - getDescriptor().getStartView().getValue();
		if (x < 0)
			x = 0;
		long dayViewDuration = getDescriptor().getEndView().getValue() -
			getDescriptor().getStartView().getValue();
		double ratio = ((double) x) / ((double) dayViewDuration);
		int dayWidth = getDayWidth();
		int datediff = DateUtil.getDateDiff(date, getInterval().getStartDate());
		return (int) (getXOffset() + datediff*dayWidth + ratio * dayWidth);
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
        	try {
	        	DateInterval interval = getModel().getInterval();
	        	int dayCount = 
	        		DateUtil.getDateDiff(interval.getEndDate(), interval.getStartDate());
	        	int width = dayCount * getHourCount() * PREFERRED_HOUR_WIDTH; 
	        	int height = getModel().getSelectedCalendars().size() * PREFERRED_ROW_HEIGHT;
	            return new Dimension(width, height);
        	} catch (Exception e) {
        		throw BizcalException.create(e);
        	}
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

                int yPos = yoffset;
                for (int i = 0; i < eventRows.size(); i++) {
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
                
                int captionHeight = getCaptionRowHeight() / 2;
                //long pos = getInterval().getStartDate().getTime();
                Calendar cal = Calendar.getInstance(LocaleBroker.getLocale());
                cal.setTime(getInterval().getStartDate());
                while (cal.getTime().getTime() < getInterval().getEndDate().getTime()) {
                    Date date = cal.getTime();
                    int xpos = getXPos(date);
                    
                    JLabel line = (JLabel) vLines.get(date);
                    line.setBounds(xpos, 0, 1, height);
                    if(cal.get(Calendar.DAY_OF_WEEK) == Calendar.MONDAY)
                    	line.setBackground(Color.LIGHT_GRAY);
                    
                    TimeOfDay startTime = getDescriptor().getStartView();
                    cal.set(Calendar.HOUR_OF_DAY, startTime.getHour());
                    cal.set(Calendar.MINUTE, startTime.getMinute());
                    cal.set(Calendar.SECOND, 0);
                    cal.set(Calendar.MILLISECOND, 0);
                    TimeOfDay endTime = getDescriptor().getEndView();
                    while (true) {
                        xpos = getXPos(cal.getTime());
                        line = (JLabel) vLines.get(cal.getTime());
                        line.setBounds(xpos, captionHeight, 1, height-captionHeight);                        
                    	
                    	cal.add(Calendar.HOUR, +1 * HOUR_RESOLUTION);
                    	TimeOfDay timeOfDay = new TimeOfDay(cal.getTime());
                    	if (timeOfDay.getValue() >= endTime.getValue())
                    		break;
                    }
                    
                    
                    cal.add(Calendar.DAY_OF_MONTH, 1);
                    cal.set(Calendar.HOUR_OF_DAY, 0);
                    cal.set(Calendar.MINUTE, 0);
                    cal.set(Calendar.SECOND, 0);
                    cal.set(Calendar.MILLISECOND, 0);
                }
            } catch (Exception e) {
                throw BizcalException.create(e);
            }
        }
    }
	
	public JComponent getComponent()
	{
		return scrollPane;
	}
	
	protected Date getDate(int xPos, int yPos)
		throws Exception
	{
		int daywidth = getDayWidth();
		xPos -= getXOffset();
		int dayno = xPos / daywidth;
		xPos -= dayno*daywidth;		
		double ratio = (double) xPos / (double) daywidth;
		Date date = getInterval().getStartDate();
		date = DateUtil.getDiffDay(date, dayno);
		long dayViewDuration = getDescriptor().getEndView().getValue() -
			getDescriptor().getStartView().getValue();
		long startTime = getDescriptor().getStartView().getValue();
		long passedTime = (long) (ratio*dayViewDuration);
		TimeOfDay timeOfDay = new TimeOfDay(startTime+passedTime);
		date = timeOfDay.getDate(date);
		return date;
	}
	
	protected Object getCalendarId(int x, int y) throws Exception {
		int height = getHeight() - getCaptionRowHeight();
		double ratio = (double) y / (double) height;
		int pos = (int) (ratio * (getSelectedCalendars().size()));
		bizcal.common.Calendar cal = 
			(bizcal.common.Calendar) getSelectedCalendars().get(pos);
		return cal.getId();
	}
	
	protected String getHeaderText()
		throws Exception
	{		
		Date from = getInterval().getStartDate();
		Calendar date = Calendar.getInstance(LocaleBroker.getLocale());
		date.setTime(getInterval().getEndDate());
		date.add(Calendar.DATE, -1);
		DateFormat format = new SimpleDateFormat("MMMM yyyy", LocaleBroker.getLocale());
		return TextUtil.formatCase(format.format(from));
	}
	
	private int getHourCount()
		throws Exception
	{
		return getDescriptor().getEndView().getHour() - 
			getDescriptor().getStartView().getHour();
	}
		
}
