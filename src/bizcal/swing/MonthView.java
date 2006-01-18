package bizcal.swing;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.LayoutManager;
import java.awt.Rectangle;
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
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.plaf.basic.BasicBorders;

import bizcal.common.CalendarViewConfig;
import bizcal.common.Event;
import bizcal.swing.util.ErrorHandler;
import bizcal.swing.util.FrameArea;
import bizcal.swing.util.TableLayoutPanel;
import bizcal.swing.util.TableLayoutPanel.Row;
import bizcal.util.BizcalException;
import bizcal.util.DateInterval;
import bizcal.util.DateUtil;
import bizcal.util.TextUtil;
import bizcal.util.Tuple;

public class MonthView
	extends CalendarView
{
	private int width;
	private JPanel _panel;
	private ColumnHeaderPanel columnHeader;
	private List cells = new ArrayList();
	private List hLines = new ArrayList();
	private List vLines = new ArrayList();
	
	public MonthView(CalendarViewConfig desc)
		throws Exception
	{
		super(desc);
	}
			
	public void refresh0()
		throws Exception
	{
		_panel.removeAll();
		cells.clear();
		hLines.clear();
		vLines.clear();
						
		Calendar cal = Calendar.getInstance(Locale.getDefault());
		cal.setTime(getInterval().getStartDate());
						
		int month = cal.get(Calendar.MONTH);
		
		cal.set(Calendar.DAY_OF_MONTH, 1);		
		cal.set(Calendar.DAY_OF_WEEK, cal.getFirstDayOfWeek());

    	int lastDayOfWeek = cal.getFirstDayOfWeek();
    	lastDayOfWeek--;
    	if (lastDayOfWeek < 1)
    		lastDayOfWeek += 7;
    	
    	Map eventMap = createEventsPerDay();
    	List row = new ArrayList();
    	cells.add(row);
        while (true) {
        	JComponent cell = createDayCell(cal, eventMap, month);
        	_panel.add(cell);
        	row.add(cell);
            if (cal.get(Calendar.DAY_OF_WEEK) == lastDayOfWeek) {
            	if (cal.get(Calendar.MONTH) != month)
            		break;
            	row = new ArrayList();
            	cells.add(row);
            }
            cal.add(Calendar.DAY_OF_MONTH, 1);
        }

        int colCount = getModel().getSelectedCalendars().size()*7;
        for (int i=0; i < colCount-1; i++) {
			JLabel line = new JLabel();
			line.setBackground(Color.LIGHT_GRAY);
			line.setOpaque(true);
			_panel.add(line);     
			vLines.add(line);
        }
        
        int rowCount = cells.size()-1;
        for (int i=0; i < rowCount; i++) {
			JLabel line = new JLabel();
			line.setBackground(Color.LIGHT_GRAY);
			line.setOpaque(true);
			_panel.add(line);
			hLines.add(line);
        }
        
		columnHeader.setModel(getModel());
		columnHeader.setPopupMenuCallback(popupMenuCallback);
		columnHeader.refresh();       
	}
	
	
	private JComponent createDayCell(Calendar cal, Map eventMap, int month)
		throws Exception
	{ 	
		Font eventFont = this.font;
		TableLayoutPanel panel = new TableLayoutPanel();
		if (cal.get(Calendar.MONTH) == month) { 
			panel.setBackground(Color.WHITE);
			panel.setOpaque(false);
		} else  
			panel.setBackground(new Color(230, 230, 230));
		panel.createColumn(TableLayoutPanel.FILL);
		//panel.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.RAISED));
		//panel.setBorder(BasicBorders.getRadioButtonBorder());
		int dayno = cal.get(Calendar.DAY_OF_MONTH);
		String text = "" + dayno;
		Row row = panel.createRow();
		JLabel label = new JLabel(text);
		//label.setOpaque(true);
		//label.setBackground(getDescriptor().getPrimaryColor());
		//label.setForeground(Color.black);
		label.setFont(font.deriveFont(Font.BOLD));
		label.addMouseListener(new DayMouseListener());
		row.createCell(label);
		panel.createRow(TableLayoutPanel.FILL);
		
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
				eventLabel.setOpaque(true);
				eventLabel.setBackground(event.getColor());
				EventMouseListener listener = new EventMouseListener();
				listener.label = eventLabel;
				listener.event = event;
				eventLabel.addMouseListener(listener);
				row.createCell(eventLabel, TableLayoutPanel.TOP, TableLayoutPanel.FULL);							
			}
		}
		JScrollPane scrollPanel = new JScrollPane(panel, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		scrollPanel.setPreferredSize(new Dimension(100,100));
		//return scrollPanel;
		return panel;
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
			
			columnHeader.setWidth(_panel.getWidth());
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
		_panel = new JPanel();
		_panel.setLayout(new Layout());
		_panel.setBackground(Color.WHITE);
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
	
	public JComponent getColumnHeader() throws Exception {
		if (columnHeader == null)
			columnHeader = new ColumnHeaderPanel(7);			
		return columnHeader.getComponent();
	}
	

	private class Layout implements LayoutManager {
		public void addLayoutComponent(String name, Component comp) {
		}

		public void removeLayoutComponent(Component comp) {
		}

		public Dimension preferredLayoutSize(Container parent) {
			try {
				int width = 7 * getModel().getSelectedCalendars().size() * DayView.PREFERRED_DAY_WIDTH;
				return new Dimension(width, getPreferredHeight());
			} catch (Exception e) {
				throw BizcalException.create(e);
			}
		}

		public Dimension minimumLayoutSize(Container parent) {
			return new Dimension(50, 100);
		}

		public void layoutContainer(Container parent) 
		{
			try {
				double width = parent.getWidth();
				width = width / getModel().getSelectedCalendars().size();
				width = width / 7;
				double height = parent.getHeight() / cells.size();
				for (int row=0; row < cells.size(); row++) {
					List rowList = (List) cells.get(row);
					for (int col=0; col < rowList.size(); col++) {
						JComponent cell = (JComponent) rowList.get(col);
						cell.setBounds((int) (col*width),
								(int) (row*height),
								(int) width,
								(int) height);
					}
				}
				
		        int colCount = getModel().getSelectedCalendars().size()*7;
		        for (int i=0; i < colCount-1; i++) {		        	
					JLabel line = (JLabel) vLines.get(i);
					line.setBounds((int) ((i+1)*width), 
							0,
							1,
							parent.getHeight());
					System.err.println("MonthView: " + (colCount+1)*width + ", " + 0 + ", " + 1 + ", " + parent.getHeight());
		        }
		        int rowCount = cells.size()-1;
		        for (int i=0; i < rowCount; i++) {
					JLabel line = (JLabel) hLines.get(i);
					line.setBounds(0, 
							(int) ((i+1)*height),
							parent.getWidth(),
							1);
		        }
				
			} catch (Exception e) {
				throw BizcalException.create(e);
			}
		}
	}
	
	private int getPreferredHeight()
	{
		return cells.size() * 100;		
	}
	
}
