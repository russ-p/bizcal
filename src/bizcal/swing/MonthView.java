package bizcal.swing;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
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

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.plaf.basic.BasicBorders;

import bizcal.common.CalendarViewConfig;
import bizcal.common.Event;
import bizcal.swing.util.ErrorHandler;
import bizcal.swing.util.TableLayoutPanel;
import bizcal.swing.util.TableLayoutPanel.Cell;
import bizcal.swing.util.TableLayoutPanel.Row;
import bizcal.util.BizcalException;
import bizcal.util.DateUtil;
import bizcal.util.TextUtil;

public class MonthView
	extends CalendarView
{
	private static int WEEKDAY_ROW_HEIGHT = 20;

	private int width;
	private TableLayoutPanel _panel;
	
	public MonthView(CalendarViewConfig desc)
		throws Exception
	{
		super(desc);
	}
			
	public void refresh0()
		throws Exception
	{
		_panel.deleteRows();
		_panel.deleteColumns();
		_panel.clear();
		
		_panel.setBackground(Color.WHITE);

		for (int i=0; i < 7; i++)
			_panel.createColumn(TableLayoutPanel.FILL);
				
		Calendar cal = Calendar.getInstance(Locale.getDefault());
		cal.setTime(getInterval().getStartDate());
						
		Row headerRow = _panel.createRow(WEEKDAY_ROW_HEIGHT);
		int month = cal.get(Calendar.MONTH);
		int weekday = cal.getFirstDayOfWeek();
		DateFormat format = new SimpleDateFormat("EEE", Locale.getDefault());
		for (int i=0; i < 7; i++) {			
			cal.set(Calendar.DAY_OF_WEEK, weekday);
			
			String formatedString = StringLengthFormater.formatDateString(cal.getTime(), font, (width-10)/7, null);
			JLabel label = new JLabel(formatedString, JLabel.CENTER);
			label.setForeground(Color.BLACK);
			label.setBounds(0, 0, (int)(width)/7,WEEKDAY_ROW_HEIGHT);
			label.setFont(font.deriveFont(Font.BOLD));
			headerRow.createCell(label);
			
			weekday++;
			if (weekday >= 8)
				weekday -= 7;
		}
		
		cal.set(Calendar.DAY_OF_MONTH, 1);
        int col = cal.get(Calendar.DAY_OF_WEEK); 
    	col -= cal.getFirstDayOfWeek();
    	if (col < 0)
    		col += 7;

    	int lastDayOfWeek = cal.getFirstDayOfWeek();
    	lastDayOfWeek--;
    	if (lastDayOfWeek < 1)
    		lastDayOfWeek += 7;
    	
    	Map eventMap = createEventsPerDay();
    	
    	Row row = _panel.createRow(TableLayoutPanel.FILL);
    	for (int i=0; i < col; i++) {
    		row.createCell();
        }
        while (cal.get(Calendar.MONTH) == month) {
        	int day = cal.get(Calendar.DAY_OF_MONTH);
        	Cell dayCell;
        	dayCell = row.createCell(createDayCell(cal, eventMap), TableLayoutPanel.FULL, TableLayoutPanel.FULL);
            if (cal.get(Calendar.DAY_OF_WEEK) == lastDayOfWeek) {
            	row = _panel.createRow(TableLayoutPanel.FILL);
            }
            cal.add(Calendar.DAY_OF_MONTH, 1);
        }
                   
        _panel.updateUI();
	}
	
	private JComponent createDayCell(Calendar cal, Map eventMap)
		throws Exception
	{ 	
		Font eventFont = this.font.deriveFont(Font.BOLD);
		TableLayoutPanel panel = new TableLayoutPanel();
		panel.setBackground(Color.WHITE);
		panel.createColumn(TableLayoutPanel.FILL);
		//panel.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.RAISED));
		panel.setBorder(BasicBorders.getRadioButtonBorder());
		int dayno = cal.get(Calendar.DAY_OF_MONTH);
		String text = "" + dayno;
		Row row = panel.createRow();
		JLabel label = new JLabel(text);
		label.setOpaque(true);
		label.setBackground(getDescriptor().getPrimaryColor());
		label.setForeground(Color.black);
		label.setFont(font);
		label.addMouseListener(new DayMouseListener());
		row.createCell(label, 3, 2);
		
		DateFormat format = DateFormat.getTimeInstance(DateFormat.SHORT, Locale.getDefault());
		List events = (List) eventMap.get(DateUtil.round2Day(cal.getTime()));
		
		if(events != null) {
			Iterator i = events.iterator();
			while (i.hasNext()) {
				Event event = (Event) i.next();
				row = panel.createRow();
				String time = format.format(event.getStart());
				String summary = "";
				if (event.getSummary() != null)
					summary = event.getSummary();
				JLabel eventLabel = new JLabel(time + " " + summary);
				eventLabel.setFont(eventFont);
				time += "-" + format.format(event.getEnd());
				eventLabel.setToolTipText(time + " " + summary);
				EventMouseListener listener = new EventMouseListener();
				listener.label = eventLabel;
				listener.event = event;
				eventLabel.addMouseListener(listener);
				row.createCell(eventLabel);							
			}
		}
		JScrollPane scrollPanel = new JScrollPane(panel, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		scrollPanel.setPreferredSize(new Dimension(100,100));
		return scrollPanel;
	}

	
	private class EventMouseListener
		extends MouseAdapter
	{
		public JLabel label;
		public Event event;
		
		public void mouseEntered(MouseEvent e)
		{
			label = (JLabel) e.getSource();
			
			label.setCursor(new Cursor(Cursor.HAND_CURSOR));
			label.setBackground(label.getBackground().darker());
			label.setForeground(Color.LIGHT_GRAY);
			
		}
		
		public void mouseExited(MouseEvent e)
		{
			label = (JLabel) e.getSource();
			label.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
			label.setBackground(label.getBackground().brighter());
			label.setForeground(Color.BLACK);
		}
		
		public void mouseClicked(MouseEvent e)
		{
			try
			{				
				//put this code in a separate method
				TableLayoutPanel borderPanel = new TableLayoutPanel();
				borderPanel.createColumn(5);
				borderPanel.createColumn(TableLayoutPanel.FILL);
				borderPanel.createColumn(5);
				Row row = borderPanel.createRow(5);
				row = borderPanel.createRow(TableLayoutPanel.FILL);
				row.createCell();
				JTabbedPane tab = new JTabbedPane();
				tab.add("Information", new JLabel("Information"));
				tab.add("Deltagare", new JLabel("Deltagare"));
				tab.setPreferredSize(new Dimension(300,300));
				row.createCell(tab, 2, 2);
				
				row = borderPanel.createRow(5);
				
				row = borderPanel.createRow();
				row.createCell();
				row.createCell(new JButton("Ok"), 2, 3);
				row = borderPanel.createRow(5);
							
				JDialog dia = new JDialog();
				dia.setModal(true);
				dia.setLocation(20, 20);
				
				DateFormat format = DateFormat.getTimeInstance(DateFormat.SHORT, Locale.getDefault());
							
				dia.setTitle(format.format(event.getStart())+ " - "+ format.format(event.getEnd()));
				dia.setContentPane(borderPanel);
				dia.pack();
				dia.setVisible(true);
				System.out.println("clicked");
				_panel.updateUI();
				_panel.repaint();
				
			}
			catch (Exception ex)
			{
				throw BizcalException.create(ex);
			}				
		}

	}
	
	private class DayMouseListener
	extends MouseAdapter
	{
		public JLabel label;
		public void mouseEntered(MouseEvent e)
		{
			label = (JLabel) e.getSource();
			label.setCursor(new Cursor(Cursor.HAND_CURSOR));
			label.setBackground(label.getBackground().darker());
			label.setForeground(Color.LIGHT_GRAY);			
		}
		
		public void mouseExited(MouseEvent e)
		{
			label = (JLabel) e.getSource();
			label.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
			label.setBackground(label.getBackground().brighter());
			label.setForeground(Color.BLACK);
		}
		
		public void mouseClicked(MouseEvent e)
		{
			try
			{
				label = (JLabel) e.getSource();
			}
		
			catch (Exception ex)
			{
				throw BizcalException.create(ex);
			}
		}
	}
	
	private class ThisComponentListener
	extends ComponentAdapter
{
	
	public void componentResized(ComponentEvent e)
	{
		try {
			
			width = _panel.getWidth();
			refresh();
		
		} catch (Exception exc) {
			exc.printStackTrace();
			ErrorHandler.handleError(exc);
		}
	}		
}
	
	protected Date getDate(int xPos, int yPos) throws Exception {
		return null;
	}
	
	public long getTimeInterval()
	throws Exception
	{
		return 24*3600*1000*30;
	}
	
	protected String getHeaderText() throws Exception {
		Calendar cal = Calendar.getInstance(Locale.getDefault());
		cal.setTime(getInterval().getStartDate());
		DateFormat format = new SimpleDateFormat("MMMM yyyy", Locale.getDefault());
		return TextUtil.formatCase(format.format(cal.getTime()));
	}
	
	protected JComponent createCalendarPanel()
	throws Exception
	{
		_panel = new TableLayoutPanel();
		_panel.addComponentListener(new ThisComponentListener());
		return _panel;
	}
	
	protected boolean supportsDrag()
	{
		return false;
	}
	
	private Map createEventsPerDay()
		throws Exception
	{
		Map map = new HashMap();
		Iterator j = getModel().getSelectedCalendars().iterator();
		while (j.hasNext()) {
			bizcal.common.Calendar cal = (bizcal.common.Calendar) j.next();
			Iterator i = getModel().getEvents(cal.getId()).iterator();
			while (i.hasNext()) {
				Event event = (Event) i.next();
				Date date = DateUtil.round2Day(event.getStart());
				List events = (List) map.get(date);
				if (events == null) {
					events = new ArrayList();
					map.put(date, events);
				}
				events.add(event);
			}
		}
		return map;
	}

	
}
