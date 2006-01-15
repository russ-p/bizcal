package bizcal.swing;

import java.awt.Cursor;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.LayoutManager;
import java.awt.Point;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JPopupMenu;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;

import bizcal.common.CalendarModel;
import bizcal.common.CalendarViewConfig;
import bizcal.common.Event;
import bizcal.swing.util.ErrorHandler;
import bizcal.swing.util.FrameArea;
import bizcal.swing.util.LassoArea;
import bizcal.util.BizcalException;
import bizcal.util.DateInterval;
import bizcal.util.DateUtil;
import bizcal.util.LocaleBroker;
import bizcal.util.TextUtil;
import bizcal.util.TimeOfDay;

public abstract class CalendarView 
{
	//protected TableLayoutPanel rootPanel;
	protected JComponent calPanel;
	public CalendarModel broker;
	protected CalendarListener listener;
	protected List bottomCategories = new ArrayList();
	protected PopupMenuCallback popupMenuCallback;
	private boolean visible = false;
	private Map _frameAreaMap = new HashMap();
	private Map _eventMap = new HashMap();
	private List _selectedEvents = new ArrayList();
	protected Font font;
	private LassoArea _lassoArea;
	private FrameArea _newEventArea;
	private JComponent _dragArea;
	private CalendarViewConfig desc;
	private JScrollPane scrollPane;
	
	public CalendarView(CalendarViewConfig desc)
		throws Exception
	{
		this.desc = desc;
		font = desc.getFont();
		/*rootPanel = new TableLayoutPanel();
        rootPanel.setBackground(Color.WHITE);
        rootPanel.createColumn(TableLayoutPanel.FILL);
        Row row = rootPanel.createRow();
                
		row = rootPanel.createRow(TableLayoutPanel.FILL);*/
        calPanel = createCalendarPanel();
        LayoutManager layout = getLayout();
        if (layout != null)
        	calPanel.setLayout(layout);
        //calPanel.setPreferredSize(new Dimension(600, 400));
        scrollPane = 
        	new JScrollPane(calPanel,
        			JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
					JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setCursor(Cursor.getDefaultCursor());
		scrollPane.getVerticalScrollBar().setUnitIncrement(15);
        ThisMouseListener mouseListener = new ThisMouseListener();
        ThisKeyListener keyListener = new ThisKeyListener();
        calPanel.addMouseListener(mouseListener);
        calPanel.addMouseMotionListener(mouseListener);
        calPanel.addKeyListener(keyListener);
               
        //row.createCell(scrollPane, TableLayoutPanel.FULL, TableLayoutPanel.FULL);
    }
	
	protected LayoutManager getLayout()
	{
		return null;
	}
	
	public final void refresh() throws Exception
	{
        scrollPane.setCorner(JScrollPane.UPPER_LEFT_CORNER, createCorner(true, true));
        scrollPane.setCorner(JScrollPane.LOWER_LEFT_CORNER, createCorner(true, false));
        scrollPane.setCorner(JScrollPane.UPPER_RIGHT_CORNER, createCorner(false, true));
        scrollPane.revalidate();
				
		_frameAreaMap.clear();
		_eventMap.clear();
		refresh0();
        scrollPane.setColumnHeaderView(getColumnHeader());
        scrollPane.setRowHeaderView(getRowHeader());
		initScroll();
		// Hack to make to init scroll work
		JScrollBar scrollBar = scrollPane.getVerticalScrollBar();
		scrollBar.setValue(scrollBar.getValue()-1);
	}
	
	public abstract void refresh0() throws Exception;
	
	public JComponent getComponent() throws Exception
    {
        //return rootPanel;
        return scrollPane;
    }
			
	public void setBroker(CalendarModel broker)
	throws Exception
	{
		this.broker = broker;		
	}
	
	public void setModel(CalendarModel model)
	{
		this.broker = model;
	}
	
	public void addListener(CalendarListener listener)
	{
		this.listener = listener;
	}
	
	/**
	 * Adds a category defined as an bottom category. Theese categories
	 * will appear "at the bottom" of the screen.
	 * 
	 * Example: "schema"
	 * 
	 * @param aCategory
	 */
	public void addBottomCategory(String aCategory)
	{
		bottomCategories.add(aCategory);
	}
	
	protected void fireDateChanged(Date date)
		throws Exception
	{
		listener.dateChanged(date);
	}
	
	protected DateInterval incDay(DateInterval day)
	throws Exception
	{
	return new DateInterval(new Date(day.getStartDate().getTime() + 24*3600*1000),
    	new Date(day.getEndDate().getTime() + 24*3600*1000));		
	}

	protected void fireDateSelected(Date date) throws Exception {
		listener.dateSelected(date);
	}
						
	protected FrameArea createFrameArea(Object calId, Event event)
		throws Exception
	{
		FrameArea area = new FrameArea();        
        String summary = event.getSummary();
        if (summary != null)
        	area.setDescription(summary);

        DateFormat format = DateFormat.getTimeInstance(DateFormat.SHORT, Locale.getDefault());
        if (event.isShowTime()) {
     		area.setHeadLine(format.format(event.getStart())
                    + "-"
                    + format.format(event.getEnd()));
        }
        area.setBackground(event.getColor());
        area.setBorder(event.isFrame());
        area.setRoundedRectangle(event.isRoundedCorner());
        area.setAlphaValue(event.isFrame() ? 0.7f : 0.4f);
        if (event.isBackground())
        	area.setAlphaValue(1.0f);
        if (event.isEditable()) {
	        FrameAreaMouseListener mouseListener = new FrameAreaMouseListener(area, calId, event);
	        area.addMouseListener(mouseListener);
	        area.addMouseMotionListener(mouseListener);
	        area.addKeyListener(new FrameAreaKeyListener(event));
        }
        if(event.isEditable()) {
            String tip = event.getToolTip();
            area.setToolTipText(tip);
        }
        area.setIcon(event.getIcon());
		//area.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        area.setSelected(isSelected(event));
        register(calId, event, area);        
        return area;
	}
	
	protected void showEventpopup(MouseEvent e, Object calId, Event event)
		throws Exception
	{
		if (popupMenuCallback == null)
			return;
		JPopupMenu popup = popupMenuCallback.getEventPopupMenu(calId, event);
		popup.show(e.getComponent(), e.getX(), e.getY());		
	}
	
	protected void showEmptyPopup(MouseEvent e, Object calId)
			throws Exception {
		if (popupMenuCallback == null)
			return;
		Date date = getDate(e.getPoint().x, e.getPoint().y);
		JPopupMenu popup = popupMenuCallback.getEmptyPopupMenu(calId, date);
		if (popup != null)
			popup.show(e.getComponent(), e.getX(), e.getY());
	}	

	public void setPopupMenuCallback(PopupMenuCallback popupMenuCallback) {
		this.popupMenuCallback = popupMenuCallback;
	}
	
	private class FrameAreaMouseListener
		extends MouseAdapter
		implements MouseMotionListener
	{
		private Point _startDrag;
		private FrameArea _frameArea;
		private Object _calId;
		private Event _event;

		public FrameAreaMouseListener(FrameArea frameArea, Object calId, Event event)
		{
			_frameArea = frameArea;
			_calId = calId;
			_event = event;
		}
		
		public void mousePressed(MouseEvent e) 
		{
			_startDrag = e.getPoint();
			maybeShowPopup(e);	
		}

	    public void mouseReleased(MouseEvent e) 
	    {
	    	try {	    
	    		if (listener != null)
	    			listener.moved(_event, _calId, _event.getStart(), _calId, getDate(e.getPoint().x, e.getPoint().y));
				maybeShowPopup(e);
		    } catch (Exception exc) {
	    		ErrorHandler.handleError(exc);
		    }	    	
	    }

	    public void mouseEntered(MouseEvent e) 
	    {
	    	try {
	    		scrollPane.setCursor(new Cursor(Cursor.HAND_CURSOR));
				_frameArea.setAlphaValue(_frameArea.getAlphaValue()+0.2f);
				//_frameArea.setBorder(true);
				_frameArea.repaint();	    		
		    } catch (Exception exc) {
	    		ErrorHandler.handleError(exc);
		    }	    	
	    }

	    public void mouseExited(MouseEvent e) 
	    {
	    	try {
	    		scrollPane.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
				_frameArea.setAlphaValue(_frameArea.getAlphaValue()-0.2f);
				//_frameArea.setBorder(false);
				_frameArea.repaint();
	    	} catch (Exception exc) {
	    		ErrorHandler.handleError(exc);
	    	}
	    }
	      
	    public void mouseClicked(MouseEvent e) 
	    {
	    	try {
	    		if (e.getClickCount() == 1) {
	    			if (_event.isSelectable()) {
		    			FrameArea area = getFrameArea(_calId, _event);
		    			boolean isSelected = area.isSelected(); 
						if ((e.getModifiersEx() & InputEvent.CTRL_DOWN_MASK) == 0)
		    				deselect();
		    			select(_calId, _event, !isSelected);
	    			}
	    		} else if (e.getClickCount() == 2) {
		    		if (listener != null)
		    			listener.showEvent(_calId, _event);
	    		}
	    	} catch (Exception exc) {
	    		ErrorHandler.handleError(exc);
	    	}
	    }
	    	    
    	private void maybeShowPopup(MouseEvent e) {
	    	try {
				if (e.isPopupTrigger()) {
					FrameArea area = getFrameArea(_calId, _event);
					if (_event.isSelectable()) {
						if(!area.isSelected())
							deselect();
						select(_calId, _event, true);
					}
					showEventpopup(e, _calId, _event);
				}
	    	} catch (Exception exc) {
	    		ErrorHandler.handleError(exc);
	    	}
		}
    	
		public void mouseDragged(MouseEvent e)
		{
			/*int dx = e.getPoint().x - _startDrag.x;
			int dy = e.getPoint().y - _startDrag.y;
			int x = _frameArea.getBounds().x;
			int y = _frameArea.getBounds().y;
			int width = _frameArea.getBounds().width;
			int height = _frameArea.getBounds().height;
			int newX = x + dx;
			int newY = y + dy;
			if (newX < getXOffset() || newY < getCaptionRowHeight()) {
				e.consume();
				return;
			}
			_frameArea.setBounds(newX, newY, width, height);*/
		}
		
		public void mouseMoved(MouseEvent e)
		{			
		}
	}
	
	private class FrameAreaKeyListener
		extends KeyAdapter
	{
		private Event _event;
		
		public FrameAreaKeyListener(Event event)
		{
			_event = event;
		}
		
		public void keyTyped(KeyEvent event)
		{
			try {
				if (event.getKeyCode() == (KeyEvent.CTRL_MASK | KeyEvent.VK_C)) {
					listener.copy(Collections.nCopies(1, _event));
			}
	    	} catch (Exception exc) {
	    		throw BizcalException.create(exc);
	    	}
		}
		
		public void keyPressed(KeyEvent event)
		{
		}
		
		public void keyReleased(KeyEvent event)
		{
		}		
	}
	
	protected class ThisKeyListener extends KeyAdapter 
	{
		private int SHIFT = 16;
		private int CTRL = 17;
		
		public void keyPressed(KeyEvent event) 
		{
			if(event.getKeyCode() == SHIFT)
			{
				calPanel.setCursor(new Cursor(Cursor.CROSSHAIR_CURSOR));				
			}
			
			if(event.getKeyCode() == CTRL && _selectedEvents.size()>0)
			{
				//calPanel.setCursor(addCuror));
			}
		}
	
		public void keyReleased(KeyEvent event) 
		{
			try 
			{
				if(event.getKeyCode() == SHIFT ||event.getKeyCode() == CTRL)
					calPanel.setCursor(null);
				
			} catch (Exception exc) 
			{
				ErrorHandler.handleError(exc);
			}
		}
	}
		
	protected abstract Date getDate(int xPos, int yPos)
		throws Exception;	
	
	
	protected class ThisMouseListener 
		extends MouseAdapter
		implements MouseMotionListener
	{
		private Point _startDrag;
		private boolean _dragging = false;
		private boolean _lasso = true;
		private Object _dragCalId = null;
		
		public void mouseClicked(MouseEvent e) {
			try {
				if (e.getClickCount() < 2)
					return;
				Date date = getDate(e.getPoint().x, e.getPoint().y);
				Object id = getCalendarId(e.getPoint().x, e.getPoint().y);
	    		if (listener != null)
	    			listener.newEvent(id, date);
			} catch (Exception exc) {
				ErrorHandler.handleError(exc);
			}
						
		}

    	private void maybeShowPopup(MouseEvent e) {
	    	try {
				if (e.isPopupTrigger()) {
					Object id = getCalendarId(e.getPoint().x, e.getPoint().y);
					showEmptyPopup(e, id);
				}
	    	} catch (Exception exc) {
	    		throw BizcalException.create(exc);
	    	}
		}
    	
		public void mousePressed(MouseEvent e) 
		{
			try {
				_startDrag = e.getPoint();
				_dragCalId = getCalendarId(e.getPoint().x, e.getPoint().y);
				_lasso = (e.getModifiersEx() & InputEvent.SHIFT_DOWN_MASK) != 0;
				maybeShowPopup(e);			
	    	} catch (Exception exc) {
	    		throw BizcalException.create(exc);
	    	}
		}

	    public void mouseReleased(MouseEvent e) 
	    {
	    	try {
				maybeShowPopup(e);
				_dragging = false;
				if (_dragArea == null)
					return;
				if (e.getPoint().y - _startDrag.y > 10) {
					Object id = getCalendarId(e.getPoint().x, e.getPoint().y);
					Date date1 = getDate(_startDrag.x, _startDrag.y);
					Date date2 = getDate(_dragArea.getLocation().x + _dragArea.getWidth(), _dragArea.getLocation().y + _dragArea.getHeight());
					if (_lasso) {
						lasso(id, date1, date2);
					}
					if (!_lasso && (date1.before(date2)))
			    		if (listener != null)
			    			listener.newEvent(_dragCalId, new DateInterval(date1, date2));
				}
				_dragArea.setVisible(false);
				//getCalenderArea().remove(_dragArea);
				_lasso = false;
				_dragArea = null;
				_dragCalId = null;
	    	} catch (Exception exc) {
	    		throw BizcalException.create(exc);
	    	}
	    }

		public void mouseDragged(MouseEvent e)
		{
			try {
				if (!_dragging) {
					_dragging = true;
					Object id = getCalendarId(e.getPoint().x, e.getPoint().y);
					Date date = getDate(e.getPoint().x, e.getPoint().y);
					if (!broker.isInsertable(id, date)) {
						return;
					}
					if (_lasso) {
						_dragArea = _lassoArea;
					} else {
						_dragArea = _newEventArea;
					}
					_dragArea.setVisible(true);
					_dragArea.setBounds(e.getPoint().x, e.getPoint().y, 100, 100);
					//getCalenderArea().add(_dragArea);
					getCalenderArea().revalidate();
				}
				if (_dragArea == null)
					return;
				Object calId = getCalendarId(e.getPoint().x, e.getPoint().y);
				if (!calId.equals(_dragCalId)) {
					e.consume();
					return;
				}
				int dx = e.getPoint().x - _startDrag.x;
				int dy = e.getPoint().y - _startDrag.y;
				int x = _dragArea.getBounds().x;
				int y = _dragArea.getBounds().y;
				_dragArea.setBounds(x, y, dx, dy);
	    	} catch (Exception exc) {
	    		throw BizcalException.create(exc);
	    	}
		}
		
		public void mouseMoved(MouseEvent e)
		{
			calPanel.requestFocusInWindow();		
		}		
	}
	
	protected Object getCalendarId(int x, int y)
		throws Exception 
	{
		return null;
	}
	
	public boolean isVisible() {
		return visible;
	}

	public void setVisible(boolean visible)
		throws Exception
	{
		this.visible = visible;
		if (visible)
			refresh();
	}
	
	protected int getXOffset()
	{
		return 0;
	}
	
	protected int getCaptionRowHeight()
	{
		return 0;
	}

	public void setCursor(Cursor cursor)
	{
		//rootPanel.setCursor(cursor);
		scrollPane.setCursor(cursor);
	}
	
	protected void register(Object calId, Event event, FrameArea area)
	{
		_frameAreaMap.put("" + calId + event.getId(), area);
		List list = (List) _eventMap.get(calId);
		if (list == null) {
			list = new ArrayList();
			_eventMap.put(calId, list);
		}
		list.add(event);
	}
	
	protected FrameArea getFrameArea(Object calId, Event event)
	{
		return (FrameArea) _frameAreaMap.get("" + calId + event.getId());		
	}
	
	public void select(Object calId, Event event, boolean flag)
		throws Exception
	{
		FrameArea area = getFrameArea(calId, event); 
		if(area!=null)
			area.setSelected(flag);
		_selectedEvents.add(event);
		listener.eventsSelected(_selectedEvents);
	}
		
	public void deselect()
		throws Exception
	{
		_selectedEvents.clear();
		Iterator iCal = broker.getSelectedCalendars().iterator();
		while (iCal.hasNext()) {
			bizcal.common.Calendar cal = 
				(bizcal.common.Calendar) iCal.next();
			Object calId = cal.getId();
			List events = (List) _eventMap.get(calId);
			if (events == null)
				return;
			Iterator i = events.iterator();
			while (i.hasNext()) {
				Event event = (Event) i.next();
				FrameArea area = getFrameArea(calId, event); 
				area.setSelected(false); 							
			}
		}		
		listener.eventsSelected(_selectedEvents);
	}
	
	public void copy()
		throws Exception
	{
		listener.copy(_selectedEvents);
	}
			
	protected JComponent getCalenderArea() throws Exception {
		return calPanel;
	}
	
	protected JComponent createCalendarPanel()
		throws Exception
	{
		return new JLayeredPane();
	}
	
	protected boolean supportsDrag()
	{
		return true;
	}

	protected void addDraggingComponents()
		throws Exception
	{
		_lassoArea = new LassoArea();
		calPanel.add(_lassoArea, 1000);    
		_newEventArea = new FrameArea();
		_newEventArea.setVisible(false);
		calPanel.add(_newEventArea, new Integer(2));		
	}
	
	private void lasso(Object id, Date date1, Date date2)
		throws Exception
	{
		deselect();
		if (DateUtil.round2Day(date1).getTime() != DateUtil.round2Day(date2).getTime()) {
			TimeOfDay startTime = DateUtil.getTimeOfDay(date1);
			TimeOfDay endTime = DateUtil.getTimeOfDay(date2);
			Date date = date1;
			//date = DateUtil.getDiffDay(date, +1);
			while (true) {
				Date start = DateUtil.setTimeOfDate(date, startTime);
				Date end = DateUtil.setTimeOfDate(date, endTime);
				if (end.after(date2))
					break;
				_selectedEvents.addAll(getEditibleEvents(id, new DateInterval(start, end)));
				date = DateUtil.getDiffDay(date, +1);
			}
		} else
			_selectedEvents.addAll(getEditibleEvents(id, new DateInterval(date1, date2)));
		Iterator i = _selectedEvents.iterator();
		while (i.hasNext()) {
			Event event = (Event) i.next();
			FrameArea area = getFrameArea(id, event); 
			area.setSelected(true);
		}
		listener.eventsSelected(_selectedEvents);						
	}

	private List getEditibleEvents(Object calId, DateInterval interval)
		throws Exception
	{
		List result = new ArrayList();
		List events = (List) _eventMap.get(calId);
		Iterator i = events.iterator();
		while (i.hasNext()) {
			Event event = (Event) i.next();
	        if (event.isEditable()) {
				DateInterval eventInterval = 
					new DateInterval(event.getStart(), event.getEnd());
				boolean overlap = eventInterval.overlap(interval);
				if (overlap)
					result.add(event);
	        }
		}
		return result;		
	}
	
	private boolean isSelected(Event event)
	{
		Iterator i = _selectedEvents.iterator();
		while (i.hasNext()) {
			Event tmpEvent = (Event) i.next();
			if (tmpEvent.getId().equals(event.getId()))
					return true;
			
		}
		return false;
	}

	public void setDescriptor(CalendarViewConfig desc)
	{
		this.desc = desc;
	}
	
	public CalendarViewConfig getDescriptor()
	{
		return desc;
	}
	
	protected JComponent getColumnHeader()
		throws Exception
	{
		return null;
	}

	protected JComponent getRowHeader() 
		throws Exception 
	{
		return null;
	}
	
	protected JComponent createCorner(boolean left, boolean top) 
	throws Exception 
	{
		return null;
	}

	
	protected int getInitYPos()
	throws Exception
	{
		return 0;
	}
	
	public void initScroll()
		throws Exception
	{
		scrollPane.getViewport().setViewPosition(new Point(0, getInitYPos()));
	}

	protected class CalHeaderMouseListener extends MouseAdapter {
		private Object calId;

		public CalHeaderMouseListener(Object calId) {
			this.calId = calId;
		}

		public void mousePressed(MouseEvent e) {
			maybeShowPopup(e);
		}

		public void mouseReleased(MouseEvent e) {
			maybeShowPopup(e);
		}

		private void maybeShowPopup(MouseEvent e) {
			try {
				if (e.isPopupTrigger()) {
					JPopupMenu popup = 
						popupMenuCallback.getCalendarPopupMenu(calId);
					popup.show(e.getComponent(), e.getX(), e.getY());		
				}
			} catch (Exception exc) {
				throw BizcalException.create(exc);
			}
		}
		
	    public void mouseEntered(MouseEvent e) 
	    {
    		//rootPanel.setCursor(new Cursor(Cursor.HAND_CURSOR));
	    }

	    public void mouseExited(MouseEvent e) 
	    {
    		//rootPanel.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
	    }
	}
	
	protected class DateLabelGroup
		extends ComponentAdapter
	{
		private List labels = new ArrayList();
		private List dates = new ArrayList();
		private List patterns = new ArrayList();
		
		public void addLabel(JLabel label, Date date)
		{
			labels.add(label);
			dates.add(date);
		}
		
		public void addPattern(String pattern)
		{
			patterns.add(new SimpleDateFormat(pattern));
		}
		
		public void addFormat(DateFormat format)
		{
			patterns.add(format);
		}
				
		public void componentResized(ComponentEvent event)
		{
			try {
				if (patterns.size() == 0) {
					DateFormat format;
					Locale l = LocaleBroker.getLocale();
					patterns.add(DateFormat.getDateInstance(DateFormat.LONG, l));
					patterns.add(DateFormat.getDateInstance(DateFormat.MEDIUM, l));
					patterns.add(DateFormat.getDateInstance(DateFormat.SHORT, l));
				}
				int maxPatternIndex = 0;
				for (int i=0; i < labels.size(); i++) {
					JLabel label = (JLabel) labels.get(i);
					Date date = (Date) dates.get(i);
					for (int j=0; j < patterns.size(); j++) {
						DateFormat format = (DateFormat) patterns.get(j);
						FontMetrics metrics = 
							label.getFontMetrics(label.getFont());
						int width = metrics.stringWidth(format.format(date));
						if (width < event.getComponent().getWidth()) {
							if (j > maxPatternIndex)
								maxPatternIndex = j;
							break;
						}
						if (j == patterns.size()-1)
							maxPatternIndex = patterns.size()-1;
					}					
				}
				DateFormat format = (DateFormat) patterns.get(maxPatternIndex);
				//DateFormat format = (DateFormat) patterns.get(0);
				for (int i=0; i < labels.size(); i++) {
					JLabel label = (JLabel) labels.get(i);
					Date date = (Date) dates.get(i);
					label.setText(TextUtil.formatCase(format.format(date)));
				}
			} catch (Exception e) {
				ErrorHandler.handleError(e); 
			}
		}
			
	}
		
	protected List getSelectedCalendars()
		throws Exception
	{
		return broker.getSelectedCalendars();
	}
	
	protected DateInterval getInterval()
		throws Exception
	{
		return broker.getInterval();
	}
	
	public CalendarModel getModel()
	{
		return broker;
	}
}