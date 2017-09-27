/*******************************************************************************
 * Bizcal is a component library for calendar widgets written in java using swing.
 * Copyright (C) 2007  Frederik Bertilsson 
 * Contributors:       Martin Heinemann martin.heinemann(at)tudor.lu
 * 
 * http://sourceforge.net/projects/bizcal/
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 * 
 * [Java is a trademark or registered trademark of Sun Microsystems, Inc. 
 * in the United States and other countries.]
 * 
 *******************************************************************************/
package bizcal.swing;

import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.LayoutManager;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.InputEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.text.DateFormat;
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
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;

import lu.tudor.santec.bizcal.NamedCalendar;
import bizcal.common.CalendarModel;
import bizcal.common.CalendarViewConfig;
import bizcal.common.Event;
import bizcal.swing.util.ErrorHandler;
import bizcal.swing.util.FrameArea;
import bizcal.swing.util.GradientArea;
import bizcal.swing.util.LassoArea;
import bizcal.util.BizcalException;
import bizcal.util.DateInterval;
import bizcal.util.DateUtil;
import bizcal.util.TimeOfDay;

/**
 * 11.06.2007 14:47:25
 * 
 * 
 * @version <br>
 *          $Log: CalendarView.java,v $
 *          Revision 1.60  2015/01/15 10:21:48  schorsch262
 *          printlines removed
 *
 *          Revision 1.59  2015/01/06 15:12:54  schorsch262
 *          calendar selection automatically deselects the former selected calendar
 *
 *          Revision 1.58  2012/06/19 16:16:24  thorstenroth
 *          remove system.out.println.
 *
 *          Revision 1.57  2012/06/19 16:09:17  thorstenroth
 *          Bug fix: double click on Appointment has not work.
 *
 *          Revision 1.56  2012/03/14 13:06:47  thorstenroth
 *          Bug fix:  Popup menu open now again if there is a right click in the CalendarView.
 *
 *          Revision 1.55  2012/01/30 16:06:43  thorstenroth
 *          Bug fix: right click on a event was broken.
 *
 *          Revision 1.54  2011/09/15 16:18:51  thorstenroth
 *          Fix Bug - Now the line breaks in the FrameArea and tootips are are visible.
 * Revision 1.53 2011/08/01 14:59:40
 *          thorstenroth Implement a new function that scale the resize region
 *          when resizing a appointment.
 * 
 *          Revision 1.52 2011/07/28 08:37:28 thorstenroth fix bugs: - better
 *          movement by drag and resize a appointment. - current time line are
 *          now not repaint when a appointment is create, dragged or resized. -
 *          the shift-key which allows a pixel accuracy dragging and resizeing
 *          of appointments, can now pressed any time not only at the beginning
 *          of drag or resize action.
 * 
 *          Revision 1.51 2011/07/26 15:12:17 thorstenroth code clearup
 * 
 *          Revision 1.50 2011/07/26 13:46:26 thorstenroth Fix Ticket #887.
 * 
 *          Revision 1.49 2011/07/15 15:33:00 thorstenroth fixed Ticket #416
 * 
 *          Revision 1.48 2011/07/14 11:43:14 thorstenroth Upgrading the
 *          selection of appointments.
 * 
 *          Revision 1.47 2011/07/06 13:55:50 thorstenroth fix the deadlock in
 *          class DayView in Line 660 when try to get a date form empty hashmap.
 * 
 *          Revision 1.46 2011/06/14 14:49:58 thorstenroth fix Bug #842
 * 
 *          Revision 1.45 2011/05/18 12:56:45 thorstenroth New final
 *          implementation of the FrameArea Paint method.
 * 
 *          Revision 1.44 2011/05/17 15:22:46 thorstenroth 1. fix bugs that
 *          compute a wrong end time when resize the appointment. it only
 *          happens if appointment are recurrence. 2. New implementation of the
 *          FrameArea Paint method (it is not final).
 * 
 *          Revision 1.43 2011/03/04 15:32:07 thorstenroth Little redesign of
 *          the frame area show now the start and end time of a event in the
 *          footer too.
 * 
 *          Revision 1.42 2011/03/04 12:45:35 thorstenroth 1. Improvement of the
 *          mouse controls when event gets resize and move in the calendar. 2.
 *          Bug Fix: The position of the current timeline is now correct and
 *          only shown ar the current day. 3. Bug Fix: Because of the bug the
 *          view can not difference between Events form different calendars
 *          which have the same start and end time so sometimes by resize or
 *          move a event there are side effects when drawing the events.
 * 
 *          Revision 1.41 2011/02/22 15:10:26 thorstenroth Comment one bug in
 *          code
 * 
 *          Revision 1.40 2011/02/22 14:59:32 thorstenroth 1. Add a new layout
 *          for the day view. This layout split the day column into a number of
 *          lines which is equal to the number of calendars which are active.
 *          The events of one calendar are now shown in one line, one below the
 *          other.
 * 
 *          2. Add a new horizontal line to the day view to represent the
 *          current time.
 * 
 *          Revision 1.39 2010/04/21 08:28:28 hermenj fixed annoying bug when
 *          trying to show popup in some cases
 * 
 *          Revision 1.38 2009/04/28 14:11:19 heine_ some dst fixes. Not yet
 *          finished but better than before...
 * 
 *          Revision 1.37 2008/10/30 10:42:52 heine_ *** empty log message ***
 * 
 *          Revision 1.36 2008/10/21 15:08:31 heine_ *** empty log message ***
 * 
 *          Revision 1.35 2008/10/09 12:33:09 heine_ shows now the summary of an
 *          event in the header of a FrameArea and the the description is now in
 *          the body.
 * 
 *          Revision 1.34 2008/08/12 12:47:27 heine_ fixed some bugs and made
 *          code improvements
 * 
 *          Revision 1.33 2008/06/19 12:20:00 heine_ *** empty log message ***
 * 
 *          Revision 1.32 2008/06/10 13:16:36 heine_ *** empty log message ***
 * 
 *          Revision 1.31 2008/06/09 14:10:09 heine_ *** empty log message ***
 * 
 *          Revision 1.30 2008/05/30 11:36:47 heine_ *** empty log message ***
 * 
 *          Revision 1.29 2008/05/26 08:15:31 heine_ removed MainThread locking
 *          by swing worker thread
 * 
 *          Revision 1.28 2008/04/24 14:17:37 heine_ Improved timeslot search
 *          when clicking and moving
 * 
 *          Revision 1.27 2008/04/08 13:17:53 heine_ *** empty log message ***
 * 
 *          Revision 1.26 2008/03/28 08:45:12 heine_ *** empty log message ***
 * 
 *          Revision 1.25 2008/03/21 15:02:35 heine_ fixed problem when
 *          selecting lasso area in a region that was in the bottom of the
 *          panel.
 * 
 *          Removed all the evil getBounds() statements. Should run fast now and
 *          use lesser heap.
 * 
 *          Revision 1.24 2008/01/21 14:13:26 heine_ *** empty log message ***
 * 
 *          Revision 1.37 2008-01-21 14:06:22 heinemann code cleanup and java
 *          doc
 * 
 *          Revision 1.36 2007-09-11 16:14:41 heinemann speed up
 * 
 * 
 *          Revision 1.26 2007/06/19 09:01:36 heinemann exception fixed <br>
 *          Revision 1.24 2007/06/18 11:41:32 heinemann <br>
 *          bug fixes and alpha optimations <br>
 * <br>
 * <br>
 *          Revision 1.20 2007/06/12 13:47:50 heinemann <br>
 *          fixed nullpointer <br>
 * <br>
 * 
 * 
 */
public abstract class CalendarView {

	public CalendarModel broker;

	protected CalendarListener listener;

	protected List<String> bottomCategories = new ArrayList<String>();

	protected PopupMenuCallback popupMenuCallback;

	private boolean visible = false;

	private Map<String, FrameArea> _frameAreaMap = new HashMap<String, FrameArea>();

	private Map<Object, List<Object>> _eventMap = new HashMap<Object, List<Object>>();

	private List<Event> _selectedEvents = new ArrayList<Event>();

	protected Font font;

	private LassoArea _lassoArea;

	private FrameArea _newEventArea;

	private JComponent _dragArea;

	private CalendarViewConfig desc;

	private static boolean draggingEnabled = true;

	// drag status of CalendarView is set if appointment is dragged
	private boolean isDragging = false;

	// create status of CalendarView is set if appointment is in creation
	private boolean isCreating = false;

	// resize status of CalendarView is set if appointment is resized
	private boolean isResizeing = false;

	/**
	 * Member to store the original clicked FrameArea in a dragging event.
	 */
	private static FrameArea originalClickedFrameArea = null;

	/**
	 * Offset to compute positions next to a vertical line
	 */
	private static final int LINE_OFFSET = 5;

	/**
	 * Static member to store if a mouse button was pressed. Used to avoid
	 * cursor checking in mouseMoved method of FrameAreas
	 */
	private static boolean isMousePressed = false;

	/**
	 * Member to store the state if a frameArea is resizable
	 */
	private static boolean isResizeable = false;

	private List<JLabel> vLines = new ArrayList<JLabel>();

	private List<JLabel> hLines = new ArrayList<JLabel>();

	Map<Event, FrameArea> frameAreaHash = Collections
			.synchronizedMap(new HashMap<Event, FrameArea>());

	private JComponent calPanel;

	private Date selectionDate;

	private NamedCalendar selectedCalendar;

	/**
	 * @param desc
	 * @throws Exception
	 */
	public CalendarView(CalendarViewConfig desc) throws Exception {
		this.desc = desc;
		font = desc.getFont();
	}

	protected LayoutManager getLayout() {
		return null;
	}

	public final void refresh() throws Exception {
		/* ================================================== */
		// SwingUtilities.invokeLater(new Runnable() {
		// public void run() {
		// /* ================================================== */
		_frameAreaMap.clear();
		_eventMap.clear();
		try {
			/* --------------------------------------------- */
			refresh0();
			/* --------------------------------------------- */
		} catch (Exception e) {
			e.printStackTrace();
		}
		/* ================================================== */
		// }
		// });
		/* ================================================== */
	}

	public abstract void refresh0() throws Exception;

	/**
	 * @param broker
	 * @throws Exception
	 */
	public void setBroker(CalendarModel broker) throws Exception {
		this.broker = broker;
	}

	/**
	 * Same method as setBroker
	 * 
	 * @param model
	 */
	public void setModel(CalendarModel model) {
		this.broker = model;
	}

	public void addListener(CalendarListener listener) {
		this.listener = listener;
	}

	/**
	 * Adds a category defined as an bottom category. Theese categories will
	 * appear "at the bottom" of the screen.
	 * 
	 * Example: "schema"
	 * 
	 * @param aCategory
	 */
	public void addBottomCategory(String aCategory) {
		bottomCategories.add(aCategory);
	}

	protected void fireDateChanged(Date date) throws Exception {
		if (listener != null)
			listener.dateChanged(date);
	}

	/**
	 * Increases the given DateInterval by one day.
	 * 
	 * @param day
	 * @return
	 * @throws Exception
	 */
	protected DateInterval incDay(DateInterval day) throws Exception {
		/* ================================================== */
		// DateInterval di = new DateInterval(new Date(
		// day.getStartDate().getTime() + 24 * 3600 * 1000), new Date(day
		// .getEndDate().getTime() + 24 * 3600 * 1000));
		DateInterval di = new DateInterval(
				DateUtil.move(day.getStartDate(), 1), DateUtil.move(day
						.getEndDate(), 1));

		return di;
		/* ================================================== */
	}

	protected void fireDateSelected(Date date) throws Exception {
		if (listener != null)
			listener.dateSelected(date);
	}

	protected FrameArea createFrameArea(Object calId, Event event)
			throws Exception {
		FrameArea area = new FrameArea();

		area.setEvent(event);

		String summary = event.getSummary();
		if (summary != null)
			area.setDescription(summary);

		/* ------------------------------------------------------- */
		// construct the headline of the date and the summary
		/* ------------------------------------------------------- */
		DateFormat formatter = DateFormat.getTimeInstance(DateFormat.SHORT,
				Locale.getDefault());
		/* ------------------------------------------------------- */
		StringBuffer headLineBuff = new StringBuffer();
		/* ------------------------------------------------------- */
		// add the date if activated
		/* ------------------------------------------------------- */
		if (event.isShowTime()) {
			/* ------------------------------------------------------- */
			headLineBuff.append(formatter.format(event.getStart()) + "-"
					+ formatter.format(event.getEnd()) + " ");
			/* ------------------------------------------------------- */
		}
		/* ------------------------------------------------------- */
		// add the summary to the headline
		/* ------------------------------------------------------- */
		if (event.getSummary() != null)
			headLineBuff.append(event.getSummary());
		/* ------------------------------------------------------- */
		area.setHeadLine(headLineBuff.toString());
		area.setStartTime(event.getStart());
		area.setEndTime(event.getEnd());
		/* ------------------------------------------------------- */
		// add the description to the description of the framearea
		/* ------------------------------------------------------- */
		if (event.getDescription() != null)
			area.setDescription(event.getDescription());

		// if (event.isShowTime()) {
		// area.setHeadLine(format.format(event.getStart()) + "-"
		// + format.format(event.getEnd()));
		// }
		area.setBackground(event.getColor());
		area.setBorder(event.isFrame());
		area.setBordercolor(event.getBordercolor());
		area.setRoundedRectangle(event.isRoundedCorner());
		area.showHeader(event.showHeader());
		// area.setAlphaValue(event.isFrame() ? 0.4f : 0.3f);
		// area.setAlphaValue(event.isFrame() ? 0.3f : 0.3f);
		// if (event.isBackground())
		// area.setAlphaValue(0.3f);
		if (event.isEditable()) {
			FrameAreaMouseListener mouseListener = new FrameAreaMouseListener(
					area, calId, event);
			area.addMouseListener(mouseListener);
			area.addMouseMotionListener(mouseListener);
			area.addKeyListener(new FrameAreaKeyListener(event));
		}

		area.setToolTipText(event.getToolTip());

			/* ------------------------------------------------------- */
		// set icons
		area.setIcon(event.getIcon());
		area.setUpperRightIcon(event.getUpperRightIcon());
		/* ------------------------------------------------------- */
		area.setCursor(new Cursor(Cursor.HAND_CURSOR));
		/* ------------------------------------------------------- */
		// set the line distance
		// if (event.get(Event.LINE_DISTANCE) != null)
		try {
			// TODO this call always throws null pointer exception why ?
			// print stack trace you will see.
			area.setLineDistance((Integer) event.get(Event.LINE_DISTANCE));
		} catch (Exception e) {
			// e.printStackTrace();
		}
		/* ------------------------------------------------------- */
		// System.out.println("area select ?: "+ isSelected(event));
		area.setSelected(isSelected(event));
		register(calId, event, area);
		return area;
	}

	protected void showEventpopup(MouseEvent e, Object calId, Event event)
			throws Exception {
		if (popupMenuCallback == null)
			return;

		JPopupMenu popup = popupMenuCallback
				.getEventPopupMenu(calId, event);

		if (popup == null)
			return;
		
		popup.show(e.getComponent(), e.getX(), e.getY());
		popup.setVisible(true);
	}

	protected void showEmptyPopup(MouseEvent e, Object calId) throws Exception {
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

	/**
	 * Returns the estimated height for a time slot
	 * 
	 * @return
	 */
	public int getTimeSlotHeight() {
		/* ================================================== */
		if (hLines != null) {
			// find two lines with a y
			Integer y1 = null;
			Integer y2 = null;
			/* ------------------------------------------------------- */
			for (JLabel l : hLines) {
				if (l.getY() > 0) {
					if (y1 == null)
						y1 = l.getY();
					else {
						y2 = l.getY();
						break;
					}
				}
			}
			/* ------------------------------------------------------- */
			// compute gap
			if (y1 != null && y2 != null)
				return (-1) * (y1 - y2);
		}
		return -1;

		/* ================================================== */
	}

	/**
	 * Get the default column width
	 * 
	 * @return
	 */
	private int getColumnWidth() {
		/* ================================================== */
		if (this.vLines != null) {
			/* ------------------------------------------------------- */
			if (this.vLines.size() > 0)
				return vLines.get(0).getX();

			return calPanel.getWidth();
			/* ------------------------------------------------------- */
		}
		return -1;
		/* ================================================== */
	}

	/**
	 * Returns the date that was selected by the lasso. if a framearea is
	 * selcted, the date will be the start of the event. Mostly used for copy
	 * paste.
	 * 
	 * @return
	 */
	public Date getSelectionDate() {
		/* ================================================== */
		return this.selectionDate;
		/* ================================================== */
	}

	/**
	 * Computes the selection date according the coordinate values
	 * 
	 * @param x
	 * @param y
	 */
	private void setSelectionDate(int x, int y) {
		/* ================================================== */
		try {
			setSelectionDate(getDate(x, y));
		} catch (Exception e) {
			e.printStackTrace();
		}
		/* ================================================== */
	}

	private void setSelectionDate(Date d) {
		/* ================================================== */
		this.selectionDate = d;
		try {
			fireDateSelected(this.selectionDate);
		} catch (Exception e) {
		}
		/* ================================================== */
	}

	private class FrameAreaMouseListener extends MouseAdapter implements
			MouseMotionListener {

		private Point _startDrag;

		private FrameArea _frameArea;

		private Object _calId;

		private Event _event;

		private Cursor resizeCursor = new Cursor(Cursor.S_RESIZE_CURSOR);

		private Cursor handCursor = new Cursor(Cursor.HAND_CURSOR);

		private FrameArea lastCreatedFrameArea = null;

		private Integer mouseXold = -1;

		private HashMap<Integer, FrameArea> additionalFrames = new HashMap<Integer, FrameArea>();

		private List<FrameArea> deletedFrameAreas = Collections
				.synchronizedList(new ArrayList<FrameArea>());

		private boolean _shiftKey = false;

		private boolean dragged;

		private long lastEventTime = 0;

		private FrameArea baseArea;
		// define max y size of resize region
		private static final int DEFAULT_SIZE_Y_RESIZE_REGION = 6;
		// set resize region to max y
		private int resizeRegionY = DEFAULT_SIZE_Y_RESIZE_REGION;
		
		private boolean isPopupTrigger = false;

		// private Integer lastCreatedKey;

		public FrameAreaMouseListener(FrameArea frameArea, Object calId,
				Event event) {
			/* ================================================== */
			_frameArea = frameArea;
			_calId = calId;
			_event = event;
			/* ================================================== */
		}
		
		
		public void mousePressed (MouseEvent e)
		{
			if (e.isPopupTrigger())
				isPopupTrigger = true;
			
			CalendarView.isMousePressed = true;
			this.dragged = false;
			/* ------------------------------------------------------- */
			// store the clicked FrameArea
			/* ------------------------------------------------------- */
			if (originalClickedFrameArea == null)
				originalClickedFrameArea = _frameArea;
			/* ------------------------------------------------------- */
			try
			{
				/* ------------------------------------------------------- */
				// select the event
				/* ------------------------------------------------------- */
				if (e.getClickCount() == 1 && _event.isSelectable())
				{
					/* ------------------------------------------------------- */
					// FrameArea area = getFrameArea(_calId, _event);
					FrameArea area = getBaseArea();
					
					if ((e.getModifiersEx() & Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()) != 0)
					{
						if (area.isSelected())
							select(_calId, _event, false);
						else
							select(_calId, _event, true);
					}
					else
					{
						deselect();
						select(_calId, _event, true);
					}
					
					_lassoArea.setVisible(false);
					_frameArea.requestFocus();
					
					if (listener != null)
						listener.eventClicked(_calId, _event, area, e);
					/* ------------------------------------------------------- */
				}
				
			}
			catch (Exception e1)
			{
				e1.printStackTrace();
			}
			
			// get FrameArea
			FrameArea baseFrameArea = getBaseArea();
			// set position value for dragging
			_startDrag = e.getPoint();
			
			// fill additional frames
			additionalFrames.clear();
			if (baseFrameArea.getChildren() != null)
				for (FrameArea fa : baseFrameArea.getChildren())
				{
					additionalFrames.put(fa.getX(), fa);
				}
			lastCreatedFrameArea = findLastFrameArea(baseFrameArea);
			/* ------------------------------------------------------- */
			calPanel.repaint();
		}

		/**
		 * Removes the frame area from the additionalFrames hashmap
		 * 
		 * @param fa
		 */
		private void removeAdditionalArea(FrameArea fa) {
			/* ================================================== */
			List<Integer> keys = new ArrayList<Integer>();
			for (Integer key : additionalFrames.keySet())
				if (additionalFrames.get(key).equals(fa))
					keys.add(key);
			for (Integer k : keys) {
				additionalFrames.remove(k);
			}
			/* ================================================== */
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * java.awt.event.MouseAdapter#mouseReleased(java.awt.event.MouseEvent)
		 */
		public void mouseReleased (MouseEvent e)
		{
			if (isPopupTrigger || e.isPopupTrigger())
			{
				showPopup(e);
			}
			else
			{
				isDragging = false;
				isResizeing = false;
				FrameArea baseFrameArea = getBaseArea();
				if (!baseFrameArea.equals(_frameArea))
				{
					baseFrameArea.getMouseListeners()[0].mouseReleased(e);
					return;
				}
				/* ------------------------------------------------------- */
				// clear the deleted
				/* ------------------------------------------------------- */
				for (FrameArea fa : deletedFrameAreas)
				{
					fa.setVisible(false);
					calPanel.remove(fa);
					removeAdditionalArea(fa);
				}
				deletedFrameAreas.clear();
				getComponent().revalidate();
				try
				{
					if (listener != null)
					{
						
						if (isResizeable)
						{
							FrameArea fa = findLastFrameArea(baseFrameArea);
							if (fa == null)
								fa = baseFrameArea;
							
							Date movDate = getDate(fa.getX() + 5, fa.getY() + fa.getHeight());
							
							if (!movDate.equals(_event.getStart()))
							{
								listener.resized(_event, _calId, _event.getEnd(), getDate(fa.getX() + 5, fa.getY() + fa.getHeight()));
							}
						}
						else
						{
							
							// if the date has not changed, do nothing
							Date eventDateNew = getDate(baseFrameArea.getX() + 5, baseFrameArea.getY());
							// =============================================================
							// cut the seconds from both dates, they can differ
							// but
							// are not significant for us because we create a
							// calendar
							// and not a scientific timetable
							// =============================================================
							if (dragged
									&& !(DateUtil.round2Minute(eventDateNew)
											.equals(DateUtil.round2Minute(_event.getStart()))))
							{
								// move
								listener.moved(_event, _calId, _event.getStart(), _calId, eventDateNew);
							}
							
							// mouse click
							if (e.getClickCount() == 1 && _event.isSelectable())
							{
								
								FrameArea area = getFrameArea(_calId, _event);
								listener.eventClicked(_calId, _event, area, e);
							}
							
							// mouse double click
							if (e.getClickCount() == 2 && _event.isSelectable())
							{
								select(_calId, _event, true);
								CalendarView.isMousePressed = false;
								listener.eventDoubleClick(_calId, _event, e);
								return;
							}
						}
					}
					
				}
				catch (Exception exc)
				{
					ErrorHandler.handleError(exc);
				}
				
				_frameArea.setIsMoving(false);
				
				// reset the original frameArea
				originalClickedFrameArea = null;
				
				CalendarView.isMousePressed = false;
//				try {
//					refresh();
//				} catch (Exception e1) {
//					e1.printStackTrace();
//				}
			}
		}

		// TODO FIX ME in this mouse action there are sometimes null pointer
		// exceptions
		public void mouseEntered(MouseEvent e) {
			/* ================================================== */
			FrameArea baseFrameArea = getBaseArea();
			if (!baseFrameArea.equals(_frameArea)) {
				baseFrameArea.getMouseListeners()[0].mouseEntered(e);
				return;
			}
			if (!CalendarView.isMousePressed)
				CalendarView.isResizeable = false;
			else
				return;
			/* ------------------------------------------------------- */
			try {
				if (!_event.isSelectable())
					return;
				if (!_frameArea.isSelected()) {
					// float alpha = _frameArea.getAlphaValue()+0.2f;

					if (_frameArea.getChildren() != null)
						for (FrameArea fa : _frameArea.getChildren()) {
							fa.setBrightness(true);
							fa.setBorder(true);
						}
					_frameArea.setBrightness(true);
					_frameArea.setBorder(true);

					calPanel.repaint();
				}

			} catch (Exception exc) {
				ErrorHandler.handleError(exc);
			}
			/* ================================================== */
		}

		public void mouseExited(MouseEvent e) {
			/* ================================================== */
			FrameArea baseFrameArea = getBaseArea();

			if (!baseFrameArea.equals(_frameArea)) {
				baseFrameArea.getMouseListeners()[0].mouseExited(e);
				return;
			}
			/* ------------------------------------------------------- */

			if (CalendarView.isMousePressed)
				return;

			if (CalendarView.isResizeable) {
				CalendarView.isResizeable = false;
				baseFrameArea.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
			}
			try {
				if (!_event.isSelectable() || _frameArea.isSelected())
					return;
				getComponent().setCursor(new Cursor(Cursor.DEFAULT_CURSOR));

				_frameArea.setBrightness(false);
				if (_frameArea.getChildren() != null)
					for (FrameArea fa : _frameArea.getChildren()) {
						/*
						 * ------------------------------------------------------
						 * -
						 */
						fa.setBrightness(false);
						fa.setBorder(false);
						/*
						 * ------------------------------------------------------
						 * -
						 */
					}
				calPanel.repaint();

			} catch (Exception exc) {
				ErrorHandler.handleError(exc);
			}
			/* ================================================== */
		}

		public void mouseClicked(MouseEvent e) {
			// // TODO take code out for testing
			// /* ================================================== */
			// FrameArea baseFrameArea = getBaseArea();
			// if (!baseFrameArea.equals(_frameArea)) {
			// baseFrameArea.getMouseListeners()[0].mouseClicked(e);
			// return;
			// }
			// //
			// ===================================================================
			// // Pipe the mouse event to the calendar panel, if the event is
			// // a background event. We want to have the selection of a
			// timeslot
			// // also available on background events.
			// //
			// ===================================================================
			// if (_event.isBackground()) {
			// MouseEvent me = new MouseEvent(calPanel, e.getID(),
			// e.getWhen(), e.getModifiers(), e.getX()
			// + _frameArea.getX(), e.getY()
			// + _frameArea.getY(), e.getClickCount(),
			// e.isPopupTrigger(), e.getButton());
			//
			// calPanel.getMouseListeners()[0].mouseClicked(me);
			// }
			// /* ------------------------------------------------------- */
			// try {
			// if (e.getClickCount() == 1) {
			// // TODO check
			// // if (_event.isSelectable()) {
			// // FrameArea area = getFrameArea(_calId, _event);
			// // boolean isSelected = area.isSelected();
			// // if ((e.getModifiersEx() & InputEvent.CTRL_DOWN_MASK) ==
			// // 0)
			// // deselect();
			// // select(_calId, _event, !isSelected);
			// // }
			// } else if (e.getClickCount() == 2) {
			// if (listener != null)
			// listener.showEvent(_calId, _event);
			// }
			// } catch (Exception exc) {
			// ErrorHandler.handleError(exc);
			// }
			// /* ================================================== */
		}

		private void showPopup(MouseEvent e) {
			/* ================================================== */
			try {
				FrameArea area = getFrameArea(_calId, _event);
				if (_event.isSelectable()) {
					if (!area.isSelected())
					{
						deselect();
						select(_calId, _event, true);
					}
				}
				showEventpopup(e, _calId, _event);
			} catch (Exception exc) {
				ErrorHandler.handleError(exc);
			}
			/* ================================================== */
		}

		/**
		 * @return the base frame area, the area that is painted first for an
		 *         event
		 */
		private FrameArea getBaseArea() {
			/* ================================================== */
			if (this.baseArea == null)
				this.baseArea = frameAreaHash.get(_event);
			return baseArea;
			/* ================================================== */
		}

		public void mouseDragged(MouseEvent e) {
			// -------------------------------------------------------
			// if left mouse button down
			// -------------------------------------------------------
			if (SwingUtilities.isLeftMouseButton(e)) {
				// -------------------------------------------------------
				// filter events by time
				// to not let every drag position fire a new computation
				// -------------------------------------------------------
				if (this.lastEventTime == 0)
					this.lastEventTime = System.currentTimeMillis();
				else {
					long current = System.currentTimeMillis();
					if ((current - this.lastEventTime) < 15) {
						return;
					}
					this.lastEventTime = current;
				}
				// -------------------------------------------------------
				// get the baseframe and set dragged = true
				// to enable multiday events to be moved by dragging
				// all frameareas of this event
				// -------------------------------------------------------
				FrameArea baseFrameArea = getBaseArea();

				try {
					((FrameAreaMouseListener) baseFrameArea.getMouseListeners()[0]).dragged = true;
				} catch (Exception ex) {
					ex.printStackTrace();
				}
				this.dragged = true;
				// if shift key pressed dragging and resizeing steps pixel
				// accuracy
				_shiftKey = (e.getModifiersEx() & InputEvent.SHIFT_DOWN_MASK) != 0;
				try {
					// -------------------------------------------------------
					// if resizing call resizing function
					// -------------------------------------------------------
					if (CalendarView.isResizeable) {
						isResizeing = true;
						resizeDrag(baseFrameArea, e);
					}
					// -------------------------------------------------------
					// else dragging call dragging function
					// -------------------------------------------------------
					else {
						isDragging = true;
						moveDrag(baseFrameArea, e);
					}
				} catch (Exception e2) {
					e2.printStackTrace();
				}
			}
		}

		public void mouseMoved(MouseEvent e) {
			/* ================================================== */

			FrameArea baseFrameArea = frameAreaHash.get(_event);
			// if (!baseFrameArea.equals(_frameArea)) {
			// baseFrameArea.getMouseMotionListeners()[0].mouseMoved(e);
			// return;
			// }
			/* ------------------------------------------------------- */
			// scale resize region
			if (baseFrameArea.getHeight() <= DEFAULT_SIZE_Y_RESIZE_REGION * 2) {
				this.resizeRegionY = baseFrameArea.getHeight() / 2;
				if (this.resizeRegionY <= 0)
					this.resizeRegionY = 5;
			}
			/* ------------------------------------------------------- */
			FrameArea areaToChange = null;
			areaToChange = findLastFrameArea(baseFrameArea);
			/* ------------------------------------------------------- */
			if (areaToChange == null)
				areaToChange = baseFrameArea;

			// if mouse is at the bottom, switch to resize mode
			if (!areaToChange.getCursor().equals(this.resizeCursor)) {

				if (e.getPoint().y > areaToChange.getHeight() - resizeRegionY) {
					/* ------------------------------------------------------- */
					// this is the latest frame area
					if (e.getSource().equals(areaToChange)) {
						areaToChange.setCursor(this.resizeCursor);
						CalendarView.isResizeable = true;
						e.consume();
						return;
					}
					/* ------------------------------------------------------- */
				}
			} else {
				if (!areaToChange.getCursor().equals(this.handCursor)) {
					if (e.getPoint().y < areaToChange.getHeight()
							- resizeRegionY) {
						areaToChange.setCursor(this.handCursor);
						CalendarView.isResizeable = false;
						e.consume();
						return;
					}
				}
			}
		}

		/**
		 * 
		 */

		private void moveDrag(FrameArea baseFrameArea, MouseEvent e) {
			// -------------------------------------------------------
			// compute values for detecting the crossing of a
			// vertical line
			// -------------------------------------------------------
			int currX = _frameArea.getX();
			int currWidth = _frameArea.getWidth();
			int nextSmaller = findNextSmallerVerticalLine(currX + LINE_OFFSET);
			int nextGreater = findNextGreaterVerticalLine(currX + LINE_OFFSET);
			int gap2left = currX - nextSmaller;
			int gap2right = nextGreater - currX - currWidth;

			baseFrameArea.setIsMoving(true);
			// -------------------------------------------------------
			// horizontal moving (x-direction)
			// -------------------------------------------------------
			Integer newXPos = null;
			// move to left side / smaller / <---
			if (e.getPoint().x < 0) {
				// mousepointer has left the event to the left
				// compute the crossing of a vertical line
				if ((gap2left + e.getPoint().x) < 0) {
					// move to day before
					newXPos = findNextSmallerVerticalLine(baseFrameArea.getX() - 5);
					// smaller
				}
			}
			// move to the right side / greater / --->
			else {
				if (e.getPoint().x > currWidth)
					if (e.getPoint().x > currWidth + gap2right) {
						newXPos = findNextGreaterVerticalLine(baseFrameArea
								.getX());
						// greater
					}
			}

			if (newXPos != null
					&& newXPos <= calPanel.getX() + calPanel.getWidth()) {
				int y = baseFrameArea.getY();
				int width = baseFrameArea.getWidth();
				int height = baseFrameArea.getHeight();
				// move base frame
				baseFrameArea.setBounds(newXPos, y, width, height);
				// move additional frames
				if (baseFrameArea.getChildren() != null
						&& baseFrameArea.getChildren().size() > 0) {
					int countX = 1;
					for (FrameArea ac : baseFrameArea.getChildren()) {
						int acNewX = baseFrameArea.getX();
						for (int i = 0; i < countX; i++) {
							acNewX = findNextGreaterVerticalLine(acNewX);
						}
						countX++;
						ac.setBounds(acNewX, ac.getY(), width, ac.getHeight());
					}
				}
			} else {
				// -------------------------------------------------------
				// vertical move (y-direction)
				// -------------------------------------------------------
				int maus_y = e.getPoint().y;

				int diffPoint = (_startDrag.y - maus_y);
				if (Math.abs(diffPoint) > getTimeSlotHeight() || _shiftKey) {
					int mov;
					// move without the line steps
					if (_shiftKey) {
						mov = Math.abs(diffPoint);
					} else {
						mov = 0;
						int sy = 0;
						int gy = 0;
						int lastLy = 0;
						for (JLabel l : hLines) {
							if (((Integer) l.getY()) > baseFrameArea.getY()
									- diffPoint) // baseFrameArea.getY() -
													// diffPoint
							{
								sy = lastLy;
								gy = l.getY();
								break;
							}
							lastLy = l.getY();
						}

						if (diffPoint < maus_y) {
							baseFrameArea.setBounds(baseFrameArea.getX(), sy,
									baseFrameArea.getWidth(), baseFrameArea
											.getHeight());
						}
						if (diffPoint > maus_y) {
							baseFrameArea.setBounds(baseFrameArea.getX(), gy,
									baseFrameArea.getWidth(), baseFrameArea
											.getHeight());
						}
					}
					// -------------------------------------------------------
					if (_startDrag.y > maus_y) {
						mov = mov * (-1);
					}
					// -------------------------------------------------------

					if (baseFrameArea.getY() + mov >= calPanel.getY()) {
						baseFrameArea.setBounds(baseFrameArea.getX(),
								baseFrameArea.getY() + mov, baseFrameArea
										.getWidth(), baseFrameArea.getHeight());

						// -------------------------------------------------------
						// find last frame
						FrameArea lastArea = findLastFrameArea(baseFrameArea);
						// if the event lasts longer than a day
						if (baseFrameArea.getChildren() != null
								&& baseFrameArea.getChildren().size() > 0) {
							// -------------------------------------------------------
							// set the height of the base frame area to
							// the panels bottom
							baseFrameArea.setBounds(baseFrameArea.getX(),
									baseFrameArea.getY(), baseFrameArea
											.getWidth(), calPanel.getHeight()
											- baseFrameArea.getY());
						}
						if (lastArea != null)
							lastArea.setBounds(lastArea.getX(),
									lastArea.getY(), lastArea.getWidth(),
									lastArea.getHeight() + mov);
						// -------------------------------------------------------
						// recall the mousedragged event to the current
						// frame area if
						// this is not the first one, in order to update
						// the _startDrag
						// member
						// is this an evil hack?
						if (!_frameArea.equals(baseFrameArea))
							this.mousePressed(e);
						// -------------------------------------------------------
					}
				}
			}
			// set the time string the frame area
			try {
				_frameArea.setMovingTimeString(getDate(_frameArea.getX(),
						_frameArea.getY()), getDate(_frameArea.getX(),
						_frameArea.getY() + _frameArea.getHeight()));

			} catch (Exception e1) {
				e1.printStackTrace();
			}
			/* ================================================== */
		}

		/**
		 * 
		 */
		private void resizeDrag(FrameArea baseFrameArea, MouseEvent e) {
			/* ================================================== */
			// **************************************************************************
			//
			// compute values for detecting the crossing of a vertical line
			//
			/* ------------------------------------------------------- */
			int currX = _frameArea.getX();
			int currY = _frameArea.getY();
			int currWidth = _frameArea.getWidth();
			/* ------------------------------------------------------- */
			int nextSmaller = findNextSmallerVerticalLine(currX + LINE_OFFSET);
			int nextGreater = findNextGreaterVerticalLine(currX + LINE_OFFSET);
			/* ------------------------------------------------------- */
			int gap2leftColumn = currX - nextSmaller;
			int gap2rightColumn = nextGreater - currX - currWidth;
			// **************************************************************************

			baseFrameArea.setIsMoving(true);
			/* ------------------------------------------------------- */
			// try to make a new frame for a new day
			// or remove one
			// ###################################################################################
			// Adjustments for the current area
			// we assume, that it can only be the last child of the base area
			// that is in current state
			// to be resized.
			/* ------------------------------------------------------- */
			// if the baseFramearea owns some children, we take the last one as
			// current
			/* ------------------------------------------------------- */
			FrameArea currentArea = _frameArea;
			Point currentPoint = e.getPoint();

			if (baseFrameArea.getChildren() != null
					&& baseFrameArea.getChildren().size() > 0) {
				/* ------------------------------------------------------- */
				currentArea = baseFrameArea.getChildren().get(
						baseFrameArea.getChildren().size() - 1);
				/* ------------------------------------------------------- */
				// convert the point from the mouseevent to the coordinate
				// system of the
				// current area
				/* ------------------------------------------------------- */
				currentPoint = convertPoint(e.getPoint(), _frameArea,
						currentArea);
				/* ------------------------------------------------------- */
			} else if (!_frameArea.isVisible()) {
				currentArea = baseFrameArea;
				currentPoint = convertPoint(e.getPoint(), _frameArea,
						currentArea);
			}
			/* ------------------------------------------------------- */
			currX = currentArea.getX();
			currY = currentArea.getY();

			currWidth = currentArea.getWidth();
			gap2leftColumn = currX
					- findNextSmallerVerticalLine(currX + LINE_OFFSET);
			gap2rightColumn = findNextGreaterVerticalLine(currX + LINE_OFFSET)
					- currX - currWidth;
			// int pY =
			// findNextGreaterHorizontalLinePos(e.getPoint().y);
			/* ------------------------------------------------------- */
			// ensure, that the event area is at least as small as the
			// time slot height!
			/* ------------------------------------------------------- */
			if ((currentArea.getHeight() + currentPoint.y) < CalendarView.this.desc
					.getMinimumTimeSlotHeight()
					|| (currentArea.getHeight() + currentPoint.y) < getTimeSlotHeight()) {
				/* ------------------------------------------------------- */
				return;
				/* ------------------------------------------------------- */
			}
			// ###################################################################################

			/* ------------------------------------------------------- */
			// if the mouse pointer is not in the column of the clicked
			// frame area, we must check if we must delete or create new/old
			// FrameAreas
			//
			// e.getX() is relative to the boundaries of the clicked component.
			// So,
			// if the mousepointer is moved to the left out of the bounds,
			// the e.getX() will return a negative value.
			// Therefore, when the gap to the left column plus the (eventually)
			// negative
			// value of e.getX() if smaller than 0, we are in a column left to
			// the clicked
			// framearea. The same way for the other direction.
			//
			// gap2leftColumn/gap2rightColumn must be computed because we can
			// not ashure, that the
			// framearea is exactly that width as the column. e.G. if there are
			// overlapping events.
			/* ------------------------------------------------------- */
			if ((gap2leftColumn + currentPoint.x) < 0
					|| (currentPoint.x > currWidth + gap2rightColumn)) {
				/* ------------------------------------------------------- */
				// remove the last frame area, if the mouseX is smaller
				// than the boundX
				// ======================================================================
				// ======================================================================
				// <---- Resize DIRECTION
				//
				// ======================================================================
				// ======================================================================
				if (gap2leftColumn + currentPoint.x < 0) {
					/* ------------------------------------------------------- */
					// System.out.println("Direction <-----");
					// remove all areas that are greater than the
					// mousepointer
					if (baseFrameArea.getChildren() != null) {
						/*
						 * ------------------------------------------------------
						 * -
						 */
						List<FrameArea> deleteAreas = new ArrayList<FrameArea>();
						for (FrameArea fa : baseFrameArea.getChildren()) {
							/*
							 * --------------------------------------------------
							 * -----
							 */
							if (fa.getX() > findNextSmallerVerticalLine(currX
									+ currentPoint.x)) {
								// remove
								// we can not remove them from the
								// panel,
								// because we need the mouselistener
								// until the mouse released event
								fa.setVisible(false);
								deleteAreas.add(fa);
								deletedFrameAreas.add(fa);
								// removeAdditionalArea(fa);
							}
							/*
							 * --------------------------------------------------
							 * -----
							 */
						}
						// delete from the parent
						baseFrameArea.getChildren().removeAll(deleteAreas);
						// if (lastArea != null)
						// lastArea.getMouseMotionListeners()[0]
						// .mouseDragged(e);
					}
					/* ------------------------------------------------------- */
				} else {
					/* ------------------------------------------------------- */
					// ----> Resize DIRECTION
					//
					// remove the new areas
					/* ------------------------------------------------------- */
					// System.out.println("Direction ===>");
					// FrameArea currLast = findLastFrameArea(baseFrameArea);
					// currLast.setBounds(
					// currLast.getX(),
					// 0,
					// currWidth,
					// calPanel.getMaximumSize().height);
					// if (currLast != null && currX + gap2rightColumn +
					// currentPoint.x < currLast
					// .getX()) {
					// System.out.println("Wohoo unreachable code reached. Damn your so good.");
					// /*
					// -------------------------------------------------------
					// */
					// // we can not remove them from the panel,
					// // because we need the mouselistener
					// // until mouse released event
					// /*
					// -------------------------------------------------------
					// */
					// currLast.setVisible(false);
					// deletedFrameAreas.add(currLast);
					// System.out.println("     Deleted Area");
					// /*
					// -------------------------------------------------------
					// */
					// }

					// ##########################################################
					//
					// create new frame areas, if needed!
					/* ------------------------------------------------------- */
					// else
					{
						/*
						 * ------------------------------------------------------
						 * -
						 */
						// if the mouse pointer has crossed the next
						// vertical line
						/*
						 * ------------------------------------------------------
						 * -
						 */
						int crossPoint = 0;
						crossPoint = currentPoint.x;
						//						
						// if
						// (baseFrameArea.equals(CalendarView.originalClickedFrameArea))
						// {
						// crossPoint = currentPoint.x;
						// System.out.println("=============== Using currenPoint");
						// }
						// else {
						// crossPoint = currentPoint.x
						// + CalendarView.originalClickedFrameArea
						// .getX();
						// System.out.println("=============== Using originalPoint");
						// }
						/*
						 * ------------------------------------------------------
						 * -
						 */
						if (crossPoint > currWidth + gap2rightColumn) {
							/*
							 * --------------------------------------------------
							 * -----
							 */
							List<Integer> newLines = null;
							newLines = findUndrawnLines(currX + this.mouseXold,
									currX + currentPoint.x, baseFrameArea
											.getX());
							/*
							 * --------------------------------------------------
							 * -----
							 */
							if (newLines != null && newLines.size() > 0) {
								/*
								 * ----------------------------------------------
								 * ---------
								 */
								// create new frame areas for each line
								/*
								 * ----------------------------------------------
								 * ---------
								 */
								currentArea.setBounds(currentArea.getX(), 0,
										currWidth,
										calPanel.getMaximumSize().height);
								for (Integer i : newLines) {
									/*
									 * ------------------------------------------
									 * -------------
									 */
									// if frame is present, continue
									if (additionalFrames.containsKey(i)) {
										/*
										 * --------------------------------------
										 * -----------------
										 */
										FrameArea afa = additionalFrames.get(i);
										/*
										 * --------------------------------------
										 * -----------------
										 */
										if (afa.isVisible()) {
											// afa.setBounds(afa.getX(),
											// 0,
											// currWidth,20);
											lastCreatedFrameArea = afa;
											continue;
										}
										if (!baseFrameArea.getChildren()
												.contains(afa)) {
											/*
											 * ----------------------------------
											 * ---------------------
											 */
											baseFrameArea.addChild(afa);
											if (deletedFrameAreas.contains(afa))
												deletedFrameAreas.remove(afa);
											/*
											 * ----------------------------------
											 * ---------------------
											 */
										}
										// System.out.println("Setting available frame visible "
										// + i);
										afa.setVisible(true);
										afa
												.setBounds(
														afa.getX(),
														0,
														currWidth,
														calPanel
																.getMaximumSize().height);
										// System.out.println("MAX -> old " +
										// findNextGreaterVerticalLine(calPanel.getMaximumSize().height));
										lastCreatedFrameArea = afa;
										continue;
										/*
										 * --------------------------------------
										 * -----------------
										 */
									}
									/*
									 * ------------------------------------------
									 * -------------
									 */
									// create a new FrameArea
									FrameArea fa = new FrameArea();
									fa
											.setBounds(
													i,
													0,
													currWidth,
													findNextGreaterHorizontalLinePos(currY
															+ currentPoint.y));
									fa.setEvent(_event);

									/*
									 * ------------------------------------------
									 * -------------
									 */
									calPanel.add(fa, Integer.valueOf(3));
									calPanel.validate();
									calPanel.updateUI();
									fa.setVisible(true);
									additionalFrames.put(i, fa);
									baseFrameArea.addChild(fa);

									/*
									 * ------------------------------------------
									 * -------------
									 */
									if (lastCreatedFrameArea != null) {
										/*
										 * --------------------------------------
										 * -----------------
										 */
										lastCreatedFrameArea
												.setBounds(
														lastCreatedFrameArea
																.getX(),
														0,
														currWidth,
														calPanel
																.getMaximumSize().height);
										// System.out.println("MAX -> new");
									}
									this.lastCreatedFrameArea = fa;
									// this.lastCreatedKey = i;
									currentPoint = convertPoint(currentPoint,
											currentArea, fa);
									currentArea = fa;
									/*
									 * ------------------------------------------
									 * -------------
									 */
								}
							}

							/*
							 * --------------------------------------------------
							 * -----
							 */
						} // if
					} // else
				}// else
				/* ------------------------------------------------------- */
				// if (_frameArea != currentArea) {
				// _frameArea.setBounds(_frameArea.getX(), _frameArea.getX(),
				// _frameArea.getWidth(),
				// 800);
				// System.out.println("adjusting _frameArea "+_frameArea.getX()
				// + " -- " +_frameArea.getHeight() );
				// }
				/* ------------------------------------------------------- */
				// if (baseFrameArea.getChildren() != null &&
				// baseFrameArea.getChildren().size() > 1) {
				// FrameArea last = findLastFrameArea(baseFrameArea);
				// for (FrameArea fa : baseFrameArea.getChildren()) {
				// if (!fa.equals(last))
				// if (fa.getHeight() <= calPanel.getMaximumSize().height)
				// fa.setBounds(fa.getX(), fa.getY(), fa.getWidth(),
				// calPanel.getMaximumSize().height);
				// }
				// }

				/* ------------------------------------------------------- */
			} else {
				/* ------------------------------------------------------- */
				// the mouse pointer is in the same column as the original
				// dragged frame area and we must "only" adjust the bounds of
				// this
				// frame are
				/* ------------------------------------------------------- */
				// ==============================================================================
				// compute height
				// if shift is pressed, we take the current mouse y position
				// as new height.
				// otherwise we will use normal step wise height
				// ==============================================================================

				int newHeight = 0;
				if (_shiftKey)
					newHeight = currentPoint.y;
				else
					newHeight = findNextGreaterHorizontalLinePos(currentPoint.y);

				currentArea.setBounds(currX, currY, currWidth, newHeight);
				// System.out.println("resizing " + newHeight);
			}
			// ==============================================================================
			// compute height
			// if shift is pressed, we take the current mouse y position
			// as new height.
			// otherwise we will use normal step wise height
			// ==============================================================================

			// int newHeight = 0;
			// if (_shiftKey)
			// newHeight = currentPoint.y;
			// else
			// newHeight = findNextGreaterHorizontalLinePos(currentPoint.y);

			// /* ------------------------------------------------------- */
			// // set bounds of the base frame area
			// if (baseFrameArea.getChildren() == null
			// || baseFrameArea.getChildren().size() < 1) {
			// /* ------------------------------------------------------- */
			// // adjust bounds only if there are changes -->
			// // performance
			// if (baseFrameArea.getHeight() != newHeight) {
			// /* ------------------------------------------------------- */
			// if (baseFrameArea.equals(CalendarView.originalClickedFrameArea))
			// {
			// baseFrameArea.setBounds(baseFrameArea.getX(),
			// baseFrameArea.getY(),
			// currWidth,
			// newHeight);
			// } else {
			// baseFrameArea.setBounds(baseFrameArea.getX(),
			// baseFrameArea.getY(),
			// currWidth,
			// findNextGreaterHorizontalLinePos(
			// currentPoint.y));
			// }
			// /* ------------------------------------------------------- */
			// }
			// /* ------------------------------------------------------- */
			// } else {
			// /* ------------------------------------------------------- */
			// // set baseframe height to max
			// /* ------------------------------------------------------- */
			// if (baseFrameArea.getHeight() != calPanel
			// .getMaximumSize().height)
			// baseFrameArea.setBounds(
			// baseFrameArea.getX(), baseFrameArea
			// .getY(), currWidth, calPanel
			// .getMaximumSize().height);
			// /* ------------------------------------------------------- */
			//
			// int diffPoint = (_startDrag.y - currentPoint.y);
			// FrameArea lfa = findLastFrameArea(baseFrameArea);
			// if (Math.abs(diffPoint) > getTimeSlotHeight()) {
			// /* ------------------------------------------------------- */
			// int mov = getTimeSlotHeight();
			// if (_startDrag.y > currentPoint.y) {
			// mov = mov * (-1);
			// }
			// /* ------------------------------------------------------- */
			// // make sure that the new boundaries are inside the
			// // calendar panel
			// if (baseFrameArea.getY() + mov >= calPanel.getY()) {
			// // FrameArea lfa = findLastFrameArea(baseFrameArea);
			// /* ------------------------------------------------------- */
			// if (lfa != null) {
			// /* ------------------------------------------------------- */
			// lfa.setBounds(
			// lfa.getX(),
			// lfa.getY(),
			// currWidth,
			// findNextGreaterHorizontalLinePos(currY
			// + currentPoint.y) - 1);
			// /* ------------------------------------------------------- */
			// }
			// /* ------------------------------------------------------- */
			// }
			// }
			// /* ------------------------------------------------------- */
			// // set all but the last children to max size
			// /* ------------------------------------------------------- */
			// // FrameArea lastArea = findLastFrameArea(baseFrameArea);
			// for (FrameArea fa : baseFrameArea.getChildren()) {
			// if (!fa.equals(lfa)) {
			// /* ------------------------------------------------------- */
			// fa.setBounds(fa.getX(), 0, fa.getWidth(),
			// calPanel.getMaximumSize().height);
			// /* ------------------------------------------------------- */
			// }
			// }
			// /* ------------------------------------------------------- */
			// }

			// this.mouseXold = currentPoint.x;
			// /* ------------------------------------------------------- */
			// // hide the deleted frame areas
			// /* ------------------------------------------------------- */
			// for (FrameArea hfa : deletedFrameAreas)
			// hfa.setVisible(false);
			//			
			// FrameArea tempLast = null;
			// try {
			// tempLast = findLastFrameArea(baseFrameArea);
			// if (tempLast == null)
			// tempLast = baseFrameArea;
			// } catch (Exception e1) {
			// tempLast = baseFrameArea;
			// // e1.printStackTrace();
			// }
			//			
			// enableCommit(baseFrameArea, true);
			// baseFrameArea.commitBounds();
			// if (baseFrameArea.getChildren() != null)
			// for (FrameArea f : baseFrameArea.getChildren())
			// f.commitBounds();
			//			
			// // tempLast.setMovingTimeString(getDate(baseFrameArea
			// // .getX(), baseFrameArea.getY()),
			// // getDate(tempLast.getX(), tempLast
			// // .getY()
			// // + tempLast.getHeight()));
			/* ================================================== */

			// set the time string to draw it in the FrameArea
			try {
				_frameArea.setMovingTimeString(getDate(_frameArea.getX(),
						_frameArea.getY()), getDate(_frameArea.getX(),
						_frameArea.getY() + _frameArea.getHeight()));

			} catch (Exception e1) {
				e1.printStackTrace();
			}
		}

	}

	/**
	 * This is the key listener that is attached to each FrameArea. It handles
	 * the copy/paste things
	 * 
	 * @author martin.heinemann@tudor.lu 20.06.2007 09:23:04
	 * 
	 * 
	 * @version <br>
	 *          $Log: CalendarView.java,v $
	 *          Revision 1.60  2015/01/15 10:21:48  schorsch262
	 *          printlines removed
	 *
	 *          Revision 1.59  2015/01/06 15:12:54  schorsch262
	 *          calendar selection automatically deselects the former selected calendar
	 *
	 *          Revision 1.58  2012/06/19 16:16:24  thorstenroth
	 *          remove system.out.println.
	 *
	 *          Revision 1.57  2012/06/19 16:09:17  thorstenroth
	 *          Bug fix: double click on Appointment has not work.
	 *
	 *          Revision 1.56  2012/03/14 13:06:47  thorstenroth
	 *          Bug fix:  Popup menu open now again if there is a right click in the CalendarView.
	 *
	 *          Revision 1.55  2012/01/30 16:06:43  thorstenroth
	 *          Bug fix: right click on a event was broken.
	 *
	 *          Revision 1.54  2011/09/15 16:18:51  thorstenroth
	 *          Fix Bug - Now the line breaks in the FrameArea and tootips are are visible.
	 * <br>
	 *          Revision 1.53 2011/08/01 14:59:40 thorstenroth <br>
	 *          Implement a new function that scale the resize region when
	 *          resizing a appointment. <br>
	 * <br>
	 *          Revision 1.52 2011/07/28 08:37:28 thorstenroth <br>
	 *          fix bugs: <br>
	 *          - better movement by drag and resize a appointment. <br>
	 *          - current time line are now not repaint when a appointment is
	 *          create, dragged or resized. <br>
	 *          - the shift-key which allows a pixel accuracy dragging and
	 *          resizeing of appointments, can now pressed any time not only at
	 *          the beginning of drag or resize action. <br>
	 * <br>
	 *          Revision 1.51 2011/07/26 15:12:17 thorstenroth <br>
	 *          code clearup <br>
	 * <br>
	 *          Revision 1.50 2011/07/26 13:46:26 thorstenroth <br>
	 *          Fix Ticket #887. <br>
	 * <br>
	 *          Revision 1.49 2011/07/15 15:33:00 thorstenroth <br>
	 *          fixed Ticket #416 <br>
	 * <br>
	 *          Revision 1.48 2011/07/14 11:43:14 thorstenroth <br>
	 *          Upgrading the selection of appointments. <br>
	 * <br>
	 *          Revision 1.47 2011/07/06 13:55:50 thorstenroth <br>
	 *          fix the deadlock in class DayView in Line 660 when try to get a
	 *          date form empty hashmap. <br>
	 * <br>
	 *          Revision 1.46 2011/06/14 14:49:58 thorstenroth <br>
	 *          fix Bug #842 <br>
	 * <br>
	 *          Revision 1.45 2011/05/18 12:56:45 thorstenroth <br>
	 *          New final implementation of the FrameArea Paint method. <br>
	 * <br>
	 *          Revision 1.44 2011/05/17 15:22:46 thorstenroth <br>
	 *          1. fix bugs that compute a wrong end time when resize the
	 *          appointment. it only happens if appointment are recurrence. <br>
	 *          2. New implementation of the FrameArea Paint method (it is not
	 *          final). <br>
	 * <br>
	 *          Revision 1.43 2011/03/04 15:32:07 thorstenroth <br>
	 *          Little redesign of the frame area show now the start and end
	 *          time of a event in the footer too. <br>
	 * <br>
	 *          Revision 1.42 2011/03/04 12:45:35 thorstenroth <br>
	 *          1. Improvement of the mouse controls when event gets resize and
	 *          move in the calendar. <br>
	 *          2. Bug Fix: The position of the current timeline is now correct
	 *          and only shown ar the current day. <br>
	 *          3. Bug Fix: Because of the bug the view can not difference
	 *          between Events form different calendars which have the same
	 *          start and end time so sometimes by resize or move a event there
	 *          are side effects when drawing the events. <br>
	 * <br>
	 *          Revision 1.41 2011/02/22 15:10:26 thorstenroth <br>
	 *          Comment one bug in code <br>
	 * <br>
	 *          Revision 1.40 2011/02/22 14:59:32 thorstenroth <br>
	 *          1. Add a new layout for the day view. This layout split the day
	 *          column into a number of lines which is equal to the number of
	 *          calendars which are active. The events of one calendar are now
	 *          shown in one line, one below the other. <br>
	 * <br>
	 *          2. Add a new horizontal line to the day view to represent the
	 *          current time. <br>
	 * <br>
	 *          Revision 1.39 2010/04/21 08:28:28 hermenj <br>
	 *          fixed annoying bug when trying to show popup in some cases <br>
	 * <br>
	 *          Revision 1.38 2009/04/28 14:11:19 heine_ <br>
	 *          some dst fixes. Not yet finished but better than before... <br>
	 * <br>
	 *          Revision 1.37 2008/10/30 10:42:52 heine_ <br>
	 *          *** empty log message *** <br>
	 * <br>
	 *          Revision 1.36 2008/10/21 15:08:31 heine_ <br>
	 *          *** empty log message *** <br>
	 * <br>
	 *          Revision 1.35 2008/10/09 12:33:09 heine_ <br>
	 *          shows now the summary of an event in the header of a FrameArea
	 *          and the the description is now in the body. <br>
	 * <br>
	 *          Revision 1.34 2008/08/12 12:47:27 heine_ <br>
	 *          fixed some bugs and made code improvements <br>
	 * <br>
	 *          Revision 1.33 2008/06/19 12:20:00 heine_ <br>
	 *          *** empty log message *** <br>
	 * <br>
	 *          Revision 1.32 2008/06/10 13:16:36 heine_ <br>
	 *          *** empty log message *** <br>
	 * <br>
	 *          Revision 1.31 2008/06/09 14:10:09 heine_ <br>
	 *          *** empty log message *** <br>
	 * <br>
	 *          Revision 1.30 2008/05/30 11:36:47 heine_ <br>
	 *          *** empty log message *** <br>
	 * <br>
	 *          Revision 1.29 2008/05/26 08:15:31 heine_ <br>
	 *          removed MainThread locking by swing worker thread <br>
	 * <br>
	 *          Revision 1.28 2008/04/24 14:17:37 heine_ <br>
	 *          Improved timeslot search when clicking and moving <br>
	 * <br>
	 *          Revision 1.27 2008/04/08 13:17:53 heine_ <br>
	 *          *** empty log message *** <br>
	 * <br>
	 *          Revision 1.26 2008/03/28 08:45:12 heine_ <br>
	 *          *** empty log message *** <br>
	 * <br>
	 *          Revision 1.25 2008/03/21 15:02:35 heine_ <br>
	 *          fixed problem when selecting lasso area in a region that was in
	 *          the bottom of the panel. <br>
	 * <br>
	 *          Removed all the evil getBounds() statements. Should run fast now
	 *          and use lesser heap. <br>
	 * <br>
	 *          Revision 1.24 2008/01/21 14:13:26 heine_ <br>
	 *          *** empty log message *** <br>
	 * <br>
	 *          Revision 1.37 2008-01-21 14:06:22 heinemann <br>
	 *          code cleanup and java doc <br>
	 * 
	 */
	private class FrameAreaKeyListener extends KeyAdapter {
		@SuppressWarnings("unused")
		private Event _event;

		public FrameAreaKeyListener(Event event) {
			_event = event;
		}

		public void keyTyped(KeyEvent event) {

		}

		public void keyPressed(KeyEvent event) {
			/* ================================================== */
			try {
				/* ------------------------------------------------------- */
				// copy
				if ((event.isControlDown())
						&& (event.getKeyCode() == KeyEvent.VK_C)) {
					/* ------------------------------------------------------- */
					CalendarView.this.copy();
					/* ------------------------------------------------------- */
				}
				// paste
				if ((event.isControlDown())
						&& (event.getKeyCode() == KeyEvent.VK_V)) {
					/* ------------------------------------------------------- */
					if (listener != null)
						listener.paste(null, getSelectionDate());
					/* ------------------------------------------------------- */
				}
				// delete
				if (event.getKeyCode() == KeyEvent.VK_DELETE
						|| event.getKeyCode() == KeyEvent.VK_BACK_SPACE) {
					/* ------------------------------------------------------- */
					if (listener != null)
						listener.deleteEvents(_selectedEvents);
					/* ------------------------------------------------------- */
				}
			} catch (Exception exc) {
				/* ------------------------------------------------------- */
				throw BizcalException.create(exc);
				/* ------------------------------------------------------- */
			}
			/* ================================================== */
		}

		public void keyReleased(KeyEvent event) {
		}
	}

	/**
	 * This keylistener is for the underlying panel on which all FrameAreas are
	 * painted.
	 * 
	 * @author martin.heinemann@tudor.lu 20.06.2007 10:16:34
	 * 
	 * 
	 * @version <br>
	 *          $Log: CalendarView.java,v $
	 *          Revision 1.60  2015/01/15 10:21:48  schorsch262
	 *          printlines removed
	 *
	 *          Revision 1.59  2015/01/06 15:12:54  schorsch262
	 *          calendar selection automatically deselects the former selected calendar
	 *
	 *          Revision 1.58  2012/06/19 16:16:24  thorstenroth
	 *          remove system.out.println.
	 *
	 *          Revision 1.57  2012/06/19 16:09:17  thorstenroth
	 *          Bug fix: double click on Appointment has not work.
	 *
	 *          Revision 1.56  2012/03/14 13:06:47  thorstenroth
	 *          Bug fix:  Popup menu open now again if there is a right click in the CalendarView.
	 *
	 *          Revision 1.55  2012/01/30 16:06:43  thorstenroth
	 *          Bug fix: right click on a event was broken.
	 *
	 *          Revision 1.54  2011/09/15 16:18:51  thorstenroth
	 *          Fix Bug - Now the line breaks in the FrameArea and tootips are are visible.
	 * <br>
	 *          Revision 1.53 2011/08/01 14:59:40 thorstenroth <br>
	 *          Implement a new function that scale the resize region when
	 *          resizing a appointment. <br>
	 * <br>
	 *          Revision 1.52 2011/07/28 08:37:28 thorstenroth <br>
	 *          fix bugs: <br>
	 *          - better movement by drag and resize a appointment. <br>
	 *          - current time line are now not repaint when a appointment is
	 *          create, dragged or resized. <br>
	 *          - the shift-key which allows a pixel accuracy dragging and
	 *          resizeing of appointments, can now pressed any time not only at
	 *          the beginning of drag or resize action. <br>
	 * <br>
	 *          Revision 1.51 2011/07/26 15:12:17 thorstenroth <br>
	 *          code clearup <br>
	 * <br>
	 *          Revision 1.50 2011/07/26 13:46:26 thorstenroth <br>
	 *          Fix Ticket #887. <br>
	 * <br>
	 *          Revision 1.49 2011/07/15 15:33:00 thorstenroth <br>
	 *          fixed Ticket #416 <br>
	 * <br>
	 *          Revision 1.48 2011/07/14 11:43:14 thorstenroth <br>
	 *          Upgrading the selection of appointments. <br>
	 * <br>
	 *          Revision 1.47 2011/07/06 13:55:50 thorstenroth <br>
	 *          fix the deadlock in class DayView in Line 660 when try to get a
	 *          date form empty hashmap. <br>
	 * <br>
	 *          Revision 1.46 2011/06/14 14:49:58 thorstenroth <br>
	 *          fix Bug #842 <br>
	 * <br>
	 *          Revision 1.45 2011/05/18 12:56:45 thorstenroth <br>
	 *          New final implementation of the FrameArea Paint method. <br>
	 * <br>
	 *          Revision 1.44 2011/05/17 15:22:46 thorstenroth <br>
	 *          1. fix bugs that compute a wrong end time when resize the
	 *          appointment. it only happens if appointment are recurrence. <br>
	 *          2. New implementation of the FrameArea Paint method (it is not
	 *          final). <br>
	 * <br>
	 *          Revision 1.43 2011/03/04 15:32:07 thorstenroth <br>
	 *          Little redesign of the frame area show now the start and end
	 *          time of a event in the footer too. <br>
	 * <br>
	 *          Revision 1.42 2011/03/04 12:45:35 thorstenroth <br>
	 *          1. Improvement of the mouse controls when event gets resize and
	 *          move in the calendar. <br>
	 *          2. Bug Fix: The position of the current timeline is now correct
	 *          and only shown ar the current day. <br>
	 *          3. Bug Fix: Because of the bug the view can not difference
	 *          between Events form different calendars which have the same
	 *          start and end time so sometimes by resize or move a event there
	 *          are side effects when drawing the events. <br>
	 * <br>
	 *          Revision 1.41 2011/02/22 15:10:26 thorstenroth <br>
	 *          Comment one bug in code <br>
	 * <br>
	 *          Revision 1.40 2011/02/22 14:59:32 thorstenroth <br>
	 *          1. Add a new layout for the day view. This layout split the day
	 *          column into a number of lines which is equal to the number of
	 *          calendars which are active. The events of one calendar are now
	 *          shown in one line, one below the other. <br>
	 * <br>
	 *          2. Add a new horizontal line to the day view to represent the
	 *          current time. <br>
	 * <br>
	 *          Revision 1.39 2010/04/21 08:28:28 hermenj <br>
	 *          fixed annoying bug when trying to show popup in some cases <br>
	 * <br>
	 *          Revision 1.38 2009/04/28 14:11:19 heine_ <br>
	 *          some dst fixes. Not yet finished but better than before... <br>
	 * <br>
	 *          Revision 1.37 2008/10/30 10:42:52 heine_ <br>
	 *          *** empty log message *** <br>
	 * <br>
	 *          Revision 1.36 2008/10/21 15:08:31 heine_ <br>
	 *          *** empty log message *** <br>
	 * <br>
	 *          Revision 1.35 2008/10/09 12:33:09 heine_ <br>
	 *          shows now the summary of an event in the header of a FrameArea
	 *          and the the description is now in the body. <br>
	 * <br>
	 *          Revision 1.34 2008/08/12 12:47:27 heine_ <br>
	 *          fixed some bugs and made code improvements <br>
	 * <br>
	 *          Revision 1.33 2008/06/19 12:20:00 heine_ <br>
	 *          *** empty log message *** <br>
	 * <br>
	 *          Revision 1.32 2008/06/10 13:16:36 heine_ <br>
	 *          *** empty log message *** <br>
	 * <br>
	 *          Revision 1.31 2008/06/09 14:10:09 heine_ <br>
	 *          *** empty log message *** <br>
	 * <br>
	 *          Revision 1.30 2008/05/30 11:36:47 heine_ <br>
	 *          *** empty log message *** <br>
	 * <br>
	 *          Revision 1.29 2008/05/26 08:15:31 heine_ <br>
	 *          removed MainThread locking by swing worker thread <br>
	 * <br>
	 *          Revision 1.28 2008/04/24 14:17:37 heine_ <br>
	 *          Improved timeslot search when clicking and moving <br>
	 * <br>
	 *          Revision 1.27 2008/04/08 13:17:53 heine_ <br>
	 *          *** empty log message *** <br>
	 * <br>
	 *          Revision 1.26 2008/03/28 08:45:12 heine_ <br>
	 *          *** empty log message *** <br>
	 * <br>
	 *          Revision 1.25 2008/03/21 15:02:35 heine_ <br>
	 *          fixed problem when selecting lasso area in a region that was in
	 *          the bottom of the panel. <br>
	 * <br>
	 *          Removed all the evil getBounds() statements. Should run fast now
	 *          and use lesser heap. <br>
	 * <br>
	 *          Revision 1.24 2008/01/21 14:13:26 heine_ <br>
	 *          *** empty log message *** <br>
	 * <br>
	 *          Revision 1.37 2008-01-21 14:06:22 heinemann <br>
	 *          code cleanup and java doc <br>
	 * 
	 */
	protected class ThisKeyListener extends KeyAdapter {
		private int SHIFT = 16;
		private int CTRL = 17;

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.awt.event.KeyAdapter#keyPressed(java.awt.event.KeyEvent)
		 */
		public void keyPressed(KeyEvent event) {
			/* ================================================== */
			// set cursor for lasso
			if (event.getKeyCode() == SHIFT) {
				getComponent().setCursor(new Cursor(Cursor.CROSSHAIR_CURSOR));
			}
			try {
				/* ------------------------------------------------------- */
				// copy
				if ((event.isControlDown())
						&& (event.getKeyCode() == KeyEvent.VK_C)) {
					/* ------------------------------------------------------- */
					CalendarView.this.copy();
					/* ------------------------------------------------------- */
				}
				// paste
				if ((event.isControlDown())
						&& (event.getKeyCode() == KeyEvent.VK_V)) {
					/* ------------------------------------------------------- */
					if (listener != null)
						listener.paste(null, getSelectionDate());
					/* ------------------------------------------------------- */
				}
			} catch (Exception exc) {
				/* ------------------------------------------------------- */
				throw BizcalException.create(exc);
				/* ------------------------------------------------------- */
			}
			/* ================================================== */
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.awt.event.KeyAdapter#keyReleased(java.awt.event.KeyEvent)
		 */
		public void keyReleased(KeyEvent event) {
			/* ================================================== */
			try {
				if (event.getKeyCode() == SHIFT || event.getKeyCode() == CTRL)
					getComponent().setCursor(null);

			} catch (Exception exc) {
				ErrorHandler.handleError(exc);
			}
			/* ================================================== */
		}
	}

	/**
	 * @param xPos
	 * @param yPos
	 * @return
	 * @throws Exception
	 */
	protected abstract Date getDate(int xPos, int yPos) throws Exception;

	/**
	 * @author martin.heinemann@tudor.lu 20.06.2007 10:16:25
	 * 
	 * 
	 * @version <br>
	 *          $Log: CalendarView.java,v $
	 *          Revision 1.60  2015/01/15 10:21:48  schorsch262
	 *          printlines removed
	 *
	 *          Revision 1.59  2015/01/06 15:12:54  schorsch262
	 *          calendar selection automatically deselects the former selected calendar
	 *
	 *          Revision 1.58  2012/06/19 16:16:24  thorstenroth
	 *          remove system.out.println.
	 *
	 *          Revision 1.57  2012/06/19 16:09:17  thorstenroth
	 *          Bug fix: double click on Appointment has not work.
	 *
	 *          Revision 1.56  2012/03/14 13:06:47  thorstenroth
	 *          Bug fix:  Popup menu open now again if there is a right click in the CalendarView.
	 *
	 *          Revision 1.55  2012/01/30 16:06:43  thorstenroth
	 *          Bug fix: right click on a event was broken.
	 *
	 *          Revision 1.54  2011/09/15 16:18:51  thorstenroth
	 *          Fix Bug - Now the line breaks in the FrameArea and tootips are are visible.
	 * <br>
	 *          Revision 1.53 2011/08/01 14:59:40 thorstenroth <br>
	 *          Implement a new function that scale the resize region when
	 *          resizing a appointment. <br>
	 * <br>
	 *          Revision 1.52 2011/07/28 08:37:28 thorstenroth <br>
	 *          fix bugs: <br>
	 *          - better movement by drag and resize a appointment. <br>
	 *          - current time line are now not repaint when a appointment is
	 *          create, dragged or resized. <br>
	 *          - the shift-key which allows a pixel accuracy dragging and
	 *          resizeing of appointments, can now pressed any time not only at
	 *          the beginning of drag or resize action. <br>
	 * <br>
	 *          Revision 1.51 2011/07/26 15:12:17 thorstenroth <br>
	 *          code clearup <br>
	 * <br>
	 *          Revision 1.50 2011/07/26 13:46:26 thorstenroth <br>
	 *          Fix Ticket #887. <br>
	 * <br>
	 *          Revision 1.49 2011/07/15 15:33:00 thorstenroth <br>
	 *          fixed Ticket #416 <br>
	 * <br>
	 *          Revision 1.48 2011/07/14 11:43:14 thorstenroth <br>
	 *          Upgrading the selection of appointments. <br>
	 * <br>
	 *          Revision 1.47 2011/07/06 13:55:50 thorstenroth <br>
	 *          fix the deadlock in class DayView in Line 660 when try to get a
	 *          date form empty hashmap. <br>
	 * <br>
	 *          Revision 1.46 2011/06/14 14:49:58 thorstenroth <br>
	 *          fix Bug #842 <br>
	 * <br>
	 *          Revision 1.45 2011/05/18 12:56:45 thorstenroth <br>
	 *          New final implementation of the FrameArea Paint method. <br>
	 * <br>
	 *          Revision 1.44 2011/05/17 15:22:46 thorstenroth <br>
	 *          1. fix bugs that compute a wrong end time when resize the
	 *          appointment. it only happens if appointment are recurrence. <br>
	 *          2. New implementation of the FrameArea Paint method (it is not
	 *          final). <br>
	 * <br>
	 *          Revision 1.43 2011/03/04 15:32:07 thorstenroth <br>
	 *          Little redesign of the frame area show now the start and end
	 *          time of a event in the footer too. <br>
	 * <br>
	 *          Revision 1.42 2011/03/04 12:45:35 thorstenroth <br>
	 *          1. Improvement of the mouse controls when event gets resize and
	 *          move in the calendar. <br>
	 *          2. Bug Fix: The position of the current timeline is now correct
	 *          and only shown ar the current day. <br>
	 *          3. Bug Fix: Because of the bug the view can not difference
	 *          between Events form different calendars which have the same
	 *          start and end time so sometimes by resize or move a event there
	 *          are side effects when drawing the events. <br>
	 * <br>
	 *          Revision 1.41 2011/02/22 15:10:26 thorstenroth <br>
	 *          Comment one bug in code <br>
	 * <br>
	 *          Revision 1.40 2011/02/22 14:59:32 thorstenroth <br>
	 *          1. Add a new layout for the day view. This layout split the day
	 *          column into a number of lines which is equal to the number of
	 *          calendars which are active. The events of one calendar are now
	 *          shown in one line, one below the other. <br>
	 * <br>
	 *          2. Add a new horizontal line to the day view to represent the
	 *          current time. <br>
	 * <br>
	 *          Revision 1.39 2010/04/21 08:28:28 hermenj <br>
	 *          fixed annoying bug when trying to show popup in some cases <br>
	 * <br>
	 *          Revision 1.38 2009/04/28 14:11:19 heine_ <br>
	 *          some dst fixes. Not yet finished but better than before... <br>
	 * <br>
	 *          Revision 1.37 2008/10/30 10:42:52 heine_ <br>
	 *          *** empty log message *** <br>
	 * <br>
	 *          Revision 1.36 2008/10/21 15:08:31 heine_ <br>
	 *          *** empty log message *** <br>
	 * <br>
	 *          Revision 1.35 2008/10/09 12:33:09 heine_ <br>
	 *          shows now the summary of an event in the header of a FrameArea
	 *          and the the description is now in the body. <br>
	 * <br>
	 *          Revision 1.34 2008/08/12 12:47:27 heine_ <br>
	 *          fixed some bugs and made code improvements <br>
	 * <br>
	 *          Revision 1.33 2008/06/19 12:20:00 heine_ <br>
	 *          *** empty log message *** <br>
	 * <br>
	 *          Revision 1.32 2008/06/10 13:16:36 heine_ <br>
	 *          *** empty log message *** <br>
	 * <br>
	 *          Revision 1.31 2008/06/09 14:10:09 heine_ <br>
	 *          *** empty log message *** <br>
	 * <br>
	 *          Revision 1.30 2008/05/30 11:36:47 heine_ <br>
	 *          *** empty log message *** <br>
	 * <br>
	 *          Revision 1.29 2008/05/26 08:15:31 heine_ <br>
	 *          removed MainThread locking by swing worker thread <br>
	 * <br>
	 *          Revision 1.28 2008/04/24 14:17:37 heine_ <br>
	 *          Improved timeslot search when clicking and moving <br>
	 * <br>
	 *          Revision 1.27 2008/04/08 13:17:53 heine_ <br>
	 *          *** empty log message *** <br>
	 * <br>
	 *          Revision 1.26 2008/03/28 08:45:12 heine_ <br>
	 *          *** empty log message *** <br>
	 * <br>
	 *          Revision 1.25 2008/03/21 15:02:35 heine_ <br>
	 *          fixed problem when selecting lasso area in a region that was in
	 *          the bottom of the panel. <br>
	 * <br>
	 *          Removed all the evil getBounds() statements. Should run fast now
	 *          and use lesser heap. <br>
	 * <br>
	 *          Revision 1.24 2008/01/21 14:13:26 heine_ <br>
	 *          *** empty log message *** <br>
	 * <br>
	 *          Revision 1.37 2008-01-21 14:06:22 heinemann <br>
	 *          code cleanup and java doc
	 * 
	 */
	protected class ThisMouseListener extends MouseAdapter implements
			MouseMotionListener {

		private Point _startDrag;

		private boolean _dragging = false;

		private boolean _lasso = true;

		private Object _dragCalId = null;

		private int currPos = 0;

		private HashMap<Integer, FrameArea> additionalFrames = new HashMap<Integer, FrameArea>();

		private FrameArea lastCreatedFrameArea = null;

		private Integer lastCreatedKey = null;

		private int mouseXold = -1;

		private int startDragMouseY = -1;

		public void mouseClicked(MouseEvent e) {
			if (e.isPopupTrigger()) maybeShowPopup(e);
			else{
			/* ================================================== */
			try {
				if (e.getClickCount() < 2) {
					/* ------------------------------------------------------- */
					// set the lasso area to the clicked cell
					int lx = findNextSmallerVerticalLine(e.getX());
					int ly = findNextSmallerHorizontalLinePos(e.getY());
					int lwidth = findNextGreaterVerticalLine(e.getX()) - lx;
					int lheight = getTimeSlotHeight();
					_lassoArea.setBounds(lx, ly, lwidth, lheight);
					setSelectionDate(lx + (lwidth / 2), ly);

					_lassoArea.setVisible(true);
					/* ------------------------------------------------------- */
				} else {
					/* ------------------------------------------------------- */
					// on double click, create a new event
					/* ------------------------------------------------------- */
					// the date is in the range of the lasso area
					/* ------------------------------------------------------- */
					int newY = findNextSmallerHorizontalLinePos(e.getPoint().y);
					// Date date = getDate( e.getPoint().x, e.getPoint().y);
					Date date = getDate(e.getPoint().x, newY);
					Object id = getCalendarId(e.getPoint().x, e.getPoint().y);

					if (listener == null)
						return;

					if (!getModel().isInsertable(id, date))
						return;

					listener.newEvent(id, date);
					/* ------------------------------------------------------- */
				}
				/* ------------------------------------------------------- */
			} catch (Exception exc) {
				ErrorHandler.handleError(exc);
			}
			/* ================================================== */
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

		public void mousePressed(MouseEvent e) {
			/* ================================================== */
			if (e.isPopupTrigger()) maybeShowPopup(e);
			else{
			try {
				deselect();
				_startDrag = e.getPoint();
				_dragCalId = getCalendarId(e.getPoint().x, e.getPoint().y);
				_lasso = (e.getModifiersEx() & InputEvent.SHIFT_DOWN_MASK) != 0;
				// maybeShowPopup(e);

			} catch (Exception exc) {
				throw BizcalException.create(exc);
			}
			this.startDragMouseY = findNextSmallerHorizontalLinePos(e
					.getPoint().y);
			}
			/* ================================================== */
		}

		public void mouseReleased(MouseEvent e) {
			if (e.isPopupTrigger() && draggingEnabled) maybeShowPopup(e);
			else{
			/* ================================================== */
			isCreating = false;
			try {
				/* ------------------------------------------------------- */
				// maybeShowPopup(e);
				_dragging = false;
				/* ------------------------------------------------------- */
				// if nothing had been dragged, do nothing
				/* ------------------------------------------------------- */
				if (_dragArea == null)
					return;
				/* ------------------------------------------------------- */
				// get the calendar id
				/* ------------------------------------------------------- */
				Object calendarId = null;
				try {
					calendarId = getCalendarId(e.getPoint().x, e.getPoint().y);
				} catch (Exception e1) {
					return;
				}
				/* ------------------------------------------------------- */
				// find the date interval for a new event
				// start date is the left upper corner of the main drag area
				/* ------------------------------------------------------- */
				Date date1 = getDate(_dragArea.getX() + 5, _dragArea.getY());
				/* ------------------------------------------------------- */
				// if there are additionalFrameAreas, the end date is the last
				// position of the latest frame area
				/* ------------------------------------------------------- */
				Date date2 = null;
				if (additionalFrames != null && additionalFrames.size() > 0) {
					/* ------------------------------------------------------- */
					// find the last frame
					/* ------------------------------------------------------- */
					List<Integer> keys = new ArrayList<Integer>(
							additionalFrames.keySet());
					Collections.sort(keys);
					/* ------------------------------------------------------- */
					// get the last frame
					/* ------------------------------------------------------- */
					FrameArea lastArea = additionalFrames.get(keys.get(keys
							.size() - 1));
					date2 = getDate(lastArea.getX() + 2, lastArea.getY()
							+ lastArea.getHeight());
					/* ------------------------------------------------------- */
				} else {
					/* ------------------------------------------------------- */
					// we use the bounds of the _dragArea
					/* ------------------------------------------------------- */
					date2 = getDate(_dragArea.getX() + 10, _dragArea.getY()
							+ _dragArea.getHeight());
					/* ------------------------------------------------------- */
				}
				if (_lasso) {
					/* ------------------------------------------------------- */
					// we need 2 dates.
					// upper left and lower right
					// date1 is upper left
					/* ------------------------------------------------------- */
					// lower right
					int rightLowerCornerX = _dragArea.getX()
							+ _dragArea.getWidth();
					int rightLowerCornerY = _dragArea.getY()
							+ _dragArea.getHeight();
					Date lowerRightDate = getDate(rightLowerCornerX,
							rightLowerCornerY);
					/* ------------------------------------------------------- */
					lasso(calendarId, date1, lowerRightDate);
					/* ------------------------------------------------------- */
				}
				/* ------------------------------------------------------- */
				// notify the listener for a new event
				/* ------------------------------------------------------- */
				if (!_lasso)// && (date1.before(date2)))
					if (listener != null && selectedCalendar != null) {
						// try to get a better calendar id with
						// selectedCalendar.getId()
						// take the selected calendar if possible
						if (selectedCalendar.getId() != null)
							listener.newEvent(selectedCalendar.getId(),
									new DateInterval(date1, date2));
						// use the old method getCalendarId(e.getPoint().x,
						// e.getPoint().y) how try to get the selected calendar
						// over the day columns
						// TODO And i don't now how this will work
						else
							listener.newEvent(_dragCalId, new DateInterval(
									date1, date2));

					}
				// }
				_dragArea.setVisible(false);
				/* ------------------------------------------------------- */
				// hide all additional lassos
				/* ------------------------------------------------------- */
				if (additionalFrames != null
						&& additionalFrames.values() != null)
					for (FrameArea a : additionalFrames.values())
						a.setVisible(false);
				/* ------------------------------------------------------- */
				// reset the mouse pointer
				/* ------------------------------------------------------- */
				_dragArea.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
				/* ------------------------------------------------------- */
				// getCalenderArea().remove(_dragArea);
				/* ------------------------------------------------------- */
				_lasso = false;
				_dragArea = null;
				_dragCalId = null;
				/* ------------------------------------------------------- */
			} catch (Exception exc) {
				throw BizcalException.create(exc);
			}
			/* ------------------------------------------------------- */
			// enable new dragging
			/* ------------------------------------------------------- */
			draggingEnabled = true;
			/* ================================================== */
			}
		}

		public void mouseDragged(MouseEvent e) {
			/* ================================================== */
			try {
				isCreating = true;
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
					_dragArea.setBounds(e.getPoint().x, e.getPoint().y, 1,
							getTimeSlotHeight());
					// getCalenderArea().add(_dragArea);
					getComponent().revalidate();
				}

				if (_dragArea == null) {
					return;
				}
				Object calId = null;
				try {
					calId = getCalendarId(e.getPoint().x, e.getPoint().y);
				} catch (Exception et) {
					return;
				}
				if (!calId.equals(_dragCalId)) {
					e.consume();
					return;
				}
				int y = this.startDragMouseY;

				/* ------------------------------------------------------- */

				int pX = e.getPoint().x;
				int pY = findNextGreaterHorizontalLinePos(e.getPoint().y);
				Integer offset = null;

				/* ------------------------------------------------------- */
				/* ------------------------------------------------------- */
				if (draggingEnabled) {
					_dragArea.setCursor(new Cursor(Cursor.S_RESIZE_CURSOR));
					if (pX >= _startDrag.x) {

						// if the mouse is in the first column, the x value is 0
						if (pX < findSmallestLine().getX())
							currPos = 0;
						else
							for (JLabel l : vLines) {
								/*
								 * ----------------------------------------------
								 * ---------
								 */
								Integer xpos = l.getX();
								if (pX >= xpos)
									if (offset == null) {
										offset = pX - xpos;
										currPos = xpos;
									} else {
										if (offset > (pX - xpos)) {
											offset = pX - xpos;
											currPos = xpos;
										}
									}
							}
						/*
						 * ------------------------------------------------------
						 * -
						 */
					}
					// set start time
					if (_dragArea instanceof FrameArea) {
						((FrameArea) _dragArea).setStartTime(getDate(currPos,
								this.startDragMouseY));
					}
				}
				/* ------------------------------------------------------- */

				// ==================================================================
				// dragging of FrameArea
				// ==================================================================
				int gap;
				try {
					gap = vLines.get(0).getX();
				} catch (Exception ec) {
					gap = calPanel.getWidth();
				}

				// if (pX >= _startDrag.x) {
				if (pX >= currPos) {

					/* ------------------------------------------------------- */
					if (!(pX > currPos && pX < (currPos + gap))) {
						/*
						 * ------------------------------------------------------
						 * -
						 */
						// =================================================================
						// the lasso has a different behaviour than the normal
						// FrameArea
						// it just spans over the columns
						// =================================================================
						if (_lasso) {
							/*
							 * --------------------------------------------------
							 * -----
							 */

							List<Integer> newLines = findUndrawnLines(
									this.mouseXold, e.getPoint().x, currPos);

							// find greatest new line
							int dx = _dragArea.getX();
							Integer max = dx;
							try {
								max = Collections.max(newLines);
							} catch (Exception e2) {
							}

							int dy = _dragArea.getY();
							int dw = max - dx + getColumnWidth();

							_dragArea.setBounds(dx, dy, dw,
									findNextGreaterHorizontalLinePos(e
											.getPoint().y)
											- dy);

							getComponent().revalidate();
							return;
							/*
							 * --------------------------------------------------
							 * -----
							 */
						}
						/*
						 * ------------------------------------------------------
						 * -
						 */
						// formerly known as else
						/*
						 * ------------------------------------------------------
						 * -
						 */
						{
							// remove the last frame area, if the mouseX is
							// smaller
							// than the boundX
							if (lastCreatedFrameArea != null
									&& pX <= lastCreatedFrameArea.getX()) {
								/*
								 * ----------------------------------------------
								 * ---------
								 */
								// remove
								additionalFrames.values().remove(
										lastCreatedFrameArea);
								lastCreatedFrameArea.setVisible(false);
								calPanel.remove(lastCreatedFrameArea);
								additionalFrames.remove(lastCreatedKey);
								/*
								 * ----------------------------------------------
								 * ---------
								 */
								// but now we must get the next smaller frame
								// find the biggest key
								Integer biggest = 0;
								/*
								 * ----------------------------------------------
								 * ---------
								 */
								for (Integer k : additionalFrames.keySet())
									if (k > biggest)
										biggest = k;
								/*
								 * ----------------------------------------------
								 * ---------
								 */
								lastCreatedFrameArea = additionalFrames
										.get(biggest);
								lastCreatedKey = biggest;
								/*
								 * ----------------------------------------------
								 * ---------
								 */
							}

							/*
							 * --------------------------------------------------
							 * -----
							 */
							// ##########################################################
							//
							// create new frame areas, if needed!
							/*
							 * --------------------------------------------------
							 * -----
							 */
							List<Integer> newLines = null;
							if (lastCreatedKey == null)
								newLines = findUndrawnLines(this.mouseXold, e
										.getPoint().x, currPos);
							else
								newLines = findUndrawnLines(this.mouseXold, e
										.getPoint().x, lastCreatedKey);
							/*
							 * --------------------------------------------------
							 * -----
							 */
							if (newLines != null && newLines.size() > 0) {
								/*
								 * ----------------------------------------------
								 * ---------
								 */
								// create new frame areas for each line
								for (Integer i : newLines) {
									/*
									 * ------------------------------------------
									 * -------------
									 */
									// if frame is present, continue
									if (additionalFrames.containsKey(i))
										continue;
									/*
									 * ------------------------------------------
									 * -------------
									 */
									// create a new FrameArea
									FrameArea fa = new FrameArea();
									fa.setBounds(i, 0, gap, pY);
									/*
									 * ------------------------------------------
									 * -------------
									 */
									calPanel.add(fa, Integer.valueOf(3));
									fa.setVisible(true);
									additionalFrames.put(i, fa);
									/*
									 * ------------------------------------------
									 * -------------
									 */
									if (lastCreatedFrameArea != null) {
										/*
										 * --------------------------------------
										 * -----------------
										 */
										lastCreatedFrameArea
												.setBounds(
														lastCreatedFrameArea
																.getX(),
														0,
														gap,
														calPanel
																.getMaximumSize().height);
									}
									this.lastCreatedFrameArea = fa;
									this.lastCreatedKey = i;
									/*
									 * ------------------------------------------
									 * -------------
									 */
								}
								_dragArea.setBounds(currPos, y, gap, calPanel
										.getMaximumSize().height);
								/*
								 * ----------------------------------------------
								 * ---------
								 */
							}
							/*
							 * --------------------------------------------------
							 * -----
							 */

							/*
							 * --------------------------------------------------
							 * -----
							 */
							try {
								lastCreatedFrameArea.setBounds(
										lastCreatedFrameArea.getX(), 0, gap,
										findNextGreaterHorizontalLinePos(e
												.getPoint().y));
								/*
								 * ----------------------------------------------
								 * ---------
								 */
								lastCreatedFrameArea.setEndTime(getDate(
										currPos, lastCreatedFrameArea.getY()
												+ lastCreatedFrameArea
														.getHeight()));
							} catch (Exception e2) {
							}
						}

					} else {
						for (Integer k : additionalFrames.keySet()) {
							additionalFrames.get(k).setVisible(false);
							calPanel.remove(additionalFrames.get(k));
						}
						additionalFrames.clear();
						lastCreatedFrameArea = null;
						lastCreatedKey = null;
						/*
						 * ------------------------------------------------------
						 * -
						 */
						_dragArea
								.setBounds(currPos, y, gap,
										findNextGreaterHorizontalLinePos(e
												.getPoint().y)
												- y);
						if (_dragArea instanceof FrameArea)
							((FrameArea) _dragArea).setEndTime(getDate(currPos,
									_dragArea.getY() + _dragArea.getHeight()));

					}
				}

				// ensure, that the event area is at leat as small as the
				// time slot height!
				if (_dragArea.getHeight() < getTimeSlotHeight())
					_dragArea.setBounds(_dragArea.getX(), _dragArea.getY(),
							_dragArea.getWidth(), getTimeSlotHeight());

				/* ------------------------------------------------------- */
				getComponent().revalidate();
				draggingEnabled = false;
				this.mouseXold = e.getPoint().x;
				/* ------------------------------------------------------- */
			} catch (Exception exc) {
				exc.printStackTrace();
				throw BizcalException.create(exc);
			}
		}

		public void mouseMoved(MouseEvent e) {
			getComponent().requestFocusInWindow();
		}

	}

	/**
	 * Returns the last FrameArea that is connected as a child to the given
	 * FrameArea. Crucial factor is the x bound.
	 * 
	 * Returns null if there are no children.
	 * 
	 * @param base
	 * @return
	 */
	private synchronized FrameArea findLastFrameArea(FrameArea base) {
		/* ================================================== */
		if (base.getChildren() == null || base.getChildren().size() < 1)
			return null;

		return base.getChildren().get(base.getChildren().size() - 1);

		// /* ------------------------------------------------------- */
		// FrameArea last = null;
		// for (FrameArea fa : base.getChildren()) {
		// /* ------------------------------------------------------- */
		// if (last == null)
		// if (fa.isVisible())
		// last = fa;
		// else
		// continue;
		// /* ------------------------------------------------------- */
		// if (fa.getX() > last.getX() && fa.isVisible())
		// last = fa;
		// /* ------------------------------------------------------- */
		// }
		// // if (last == null)
		// // return base;
		// return last;
		/* ================================================== */
	}

	// /**
	// * Returns the first frame after the base frame
	// *
	// * @param base
	// * @return
	// */
	// private FrameArea findFirstFrameArea(FrameArea base) {
	// /* ================================================== */
	// if (base.getChildren() == null)
	// return null;
	// /* ------------------------------------------------------- */
	// FrameArea first = null;
	// for (FrameArea fa : base.getChildren()) {
	// /* ------------------------------------------------------- */
	// if (first == null)
	// first = fa;
	// /* ------------------------------------------------------- */
	// if (fa.getX() < first.getX())
	// first = fa;
	// /* ------------------------------------------------------- */
	// }
	// return first;
	// /* ================================================== */
	// }

	/**
	 * Returns the position for stepwise dragging
	 * 
	 * @param mouseY
	 * @return
	 */
	private int findNextGreaterHorizontalLinePos(int mouseY) {
		/* ================================================== */
		if (hLines == null)
			return -1;
		/* ------------------------------------------------------- */
		int linePos = 100000000;
		for (JLabel l : hLines) {
			/* ------------------------------------------------------- */
			if (l.getY() > mouseY) {
				if (l.getY() < linePos)
					linePos = l.getY();
			}
			/* ------------------------------------------------------- */
		}
		if (linePos == 100000000)
			return -1;
		return linePos;
		/* ================================================== */
	}

	/**
	 * 
	 * Gets the next smaller horizontal line according to the mouse pointer
	 * 
	 * @param mouseY
	 * @return
	 */
	private int findNextSmallerHorizontalLinePos(int mouseY) {
		/* ================================================== */
		if (hLines == null)
			return -1;
		/* ------------------------------------------------------- */
		// if the pointer is in the first row, return the startY of the panel
		/* ------------------------------------------------------- */
		int timeSlotHeight = getTimeSlotHeight();
		if (mouseY < timeSlotHeight)
			return calPanel.getY();
		/* ------------------------------------------------------- */
		int linePos = calPanel.getHeight();
		// System.out.println("======================================================================");
		for (JLabel l : hLines) {
			/* ------------------------------------------------------- */
			// System.out.println("LineY: " + l.getY());
			if (l.getY() < linePos && l.getY() >= mouseY - timeSlotHeight)
				linePos = l.getY();
			/* ------------------------------------------------------- */
		}
		// System.out.println("======================================================================");
		return linePos;
		/* ================================================== */
	}

	/**
	 * Find all lines that are unprinted between the last printed frame area and
	 * the current mouspointer
	 * 
	 * @param mouseXold
	 * @param mouseXnew
	 * @param gap
	 * @return
	 */
	private List<Integer> findUndrawnLines(int mouseXold, int mouseXnew,
			Integer lastFrameX) {
		/* ================================================== */
		List<Integer> returnList = new ArrayList<Integer>();
		// can only find lines if the current mouseposition is greater
		// than the last one
		if (mouseXnew > mouseXold)
			/* ------------------------------------------------------- */
			for (JLabel l : vLines) {
				/* ------------------------------------------------------- */
				if (l.getX() > lastFrameX) {
					if (l.getX() < mouseXnew && !returnList.contains(l.getX())) {
						returnList.add(l.getX());
					}
				}
				/* ------------------------------------------------------- */
			}
		return returnList;
		/* ================================================== */
	}

	/**
	 * Find the next vertical line
	 * 
	 * @param mouseX
	 * @return
	 */
	private int findNextGreaterVerticalLine(int mouseX) {
		/* ================================================== */
		if (vLines != null) {
			/* ------------------------------------------------------- */
			int linePos = calPanel.getWidth();
			for (JLabel l : vLines) {
				if (l.getX() > mouseX)
					if (l.getX() < linePos)
						linePos = l.getX();
			}
			return linePos;
			/* ------------------------------------------------------- */
		}
		System.out.println("Return -1");
		return -1;
		/* ================================================== */
	}

	/**
	 * Returns the next smalles vertical line position
	 * 
	 * @param mouseX
	 * @return
	 */
	private int findNextSmallerVerticalLine(int mouseX) {
		/* ================================================== */
		if (vLines != null) {
			/* ------------------------------------------------------- */
			int linePos = -1;
			for (JLabel l : vLines) {
				if (l.getX() < mouseX)
					if (l.getX() > linePos)
						linePos = l.getX();
			}
			return linePos;
			/* ------------------------------------------------------- */
		}
		return -1;
		/* ================================================== */
	}

	/**
	 * Returns the first line in the list
	 * 
	 * @return
	 */
	private JLabel findSmallestLine() {
		/* ================================================== */
		if (this.vLines == null)
			return null;
		/* ------------------------------------------------------- */
		// for single day view, there are no lines
		if (this.vLines.size() == 0) {
			JLabel sL = new JLabel();
			sL.setBounds(calPanel.getWidth(), 0, 0, 0);
			vLines.add(sL);
			return sL;
		}
		if (vLines.size() == 1) {
			vLines.get(0).setBounds(calPanel.getWidth(), 0, 0, 0);
		}

		/* ------------------------------------------------------- */
		JLabel smallest = vLines.get(0);
		for (JLabel l : vLines) {
			/* ------------------------------------------------------- */
			if (l.getX() < smallest.getX())
				smallest = l;
			/* ------------------------------------------------------- */
		}
		return smallest;
		/* ================================================== */
	}

	public void addVerticalLine(JLabel line) {
		/* ================================================== */
		this.vLines.add(line);
		/* ================================================== */
	}

	public void resetVerticalLines() {
		/* ================================================== */
		this.vLines.clear();
		/* ================================================== */
	}

	public void addHorizontalLine(JLabel line) {
		/* ================================================== */
		this.hLines.add(line);
		/* ================================================== */
	}

	public void resetHorizontalLines() {
		/* ================================================== */
		this.hLines.clear();
		/* ================================================== */
	}

	protected void addDraggingComponents(JComponent calPanel) throws Exception {
		_lassoArea = new LassoArea();
		calPanel.add(_lassoArea, 1000);
		_newEventArea = new FrameArea();
		_newEventArea.setRoundedRectangle(false);

		_newEventArea.setVisible(false);
		calPanel.add(_newEventArea, Integer.valueOf(2));
		this.calPanel = calPanel;
	}

	protected Object getCalendarId(int x, int y) throws Exception {
		return null;
	}

	public boolean isVisible() {
		return visible;
	}

	public void setVisible(boolean visible) throws Exception {
		this.visible = visible;
		if (visible)
			refresh();
	}

	protected int getXOffset() {
		return 0;
	}

	protected int getCaptionRowHeight() {
		return 0;
	}

	public void setCursor(Cursor cursor) {
		// rootPanel.setCursor(cursor);
		getComponent().setCursor(cursor);
	}

	protected void register(Object calId, Event event, FrameArea area) {
		_frameAreaMap.put("" + calId + event.getId()
				+ event.getStart().getTime(), area);
		List<Object> list = (List<Object>) _eventMap.get(calId);

		if (list == null) {
			list = new ArrayList<Object>();
			_eventMap.put(calId, list);
		}
		list.add(event);
	}

	protected FrameArea getFrameArea(Object calId, Event event) {
		return _frameAreaMap.get("" + calId + event.getId()
				+ event.getStart().getTime());
	}

	
	/**
	 * @param calId
	 * @param event
	 * @param flag
	 * @throws Exception
	 */
	public void select (Object calId, Event event, boolean flag)
			throws Exception
	{
		// FrameArea area = getFrameArea(calId, event);
		FrameArea area = frameAreaHash.get(event);
		if (area != null)
		{
			/* ------------------------------------------------------- */
			area.setSelected(flag);
			if (area.getChildren() != null)
				for (FrameArea fa : area.getChildren())
					fa.setSelected(true);
			/* ------------------------------------------------------- */
		}
		
		if (flag)
		{
			if (!_selectedEvents.contains(event))
				_selectedEvents.add(event);
		}
		else
		{
			_selectedEvents.remove(event);
		}
		
		// inform the listener
		if (listener != null)
		{
			listener.eventsSelected(_selectedEvents);
			listener.eventSelected(calId, event);
		}
		
		setSelectionDate(event.getStart());
	}

	/**
	 * Deselect frame areas. FIXME not working
	 * 
	 * @throws Exception
	 */
	public void deselect() throws Exception {
		/* ================================================== */
		_selectedEvents.clear();
		Iterator<?> iCal = broker.getSelectedCalendars().iterator();
		while (iCal.hasNext()) {
			bizcal.common.Calendar cal = (bizcal.common.Calendar) iCal.next();
			Object calId = cal.getId();
			List<Object> events = (List<Object>) _eventMap.get(calId);
			if (events == null)
				return;
			Iterator<Object> i = events.iterator();
			while (i.hasNext()) {
				Event event = (Event) i.next();
				// FrameArea area = getFrameArea(calId, event);
				FrameArea area = frameAreaHash.get(event);
				// if (area.isSelected())
				// area.setAlphaValue(area.getAlphaValue()-0.2f);
				if (area != null) {
					area.setSelected(false);
					if (area.getChildren() != null)
						for (FrameArea fa : area.getChildren())
							fa.setSelected(false);
				}

			}
		}
		if (listener != null) {
			/* ------------------------------------------------------- */
			listener.eventsSelected(_selectedEvents);
			listener.selectionReset();
			/* ------------------------------------------------------- */
		}
		calPanel.requestFocus();
		/* ================================================== */
	}

	public void copy() throws Exception {
		if (listener != null)
			listener.copy(_selectedEvents);
	}

	protected boolean supportsDrag() {
		return true;
	}

	/*
	 * protected void addDraggingComponents() throws Exception { _lassoArea =
	 * new LassoArea(); calPanel.add(_lassoArea, 1000); _newEventArea = new
	 * FrameArea(); _newEventArea.setVisible(false); calPanel.add(_newEventArea,
	 * new Integer(2)); }
	 */

	/**
	 * Select the events surrounded by the lasso
	 * 
	 * @param id
	 * @param date1
	 * @param date2
	 * @throws Exception
	 */
	private void lasso(Object id, Date date1, Date date2) throws Exception {
		/* ================================================== */
		// deselect all
		deselect();
		/* ------------------------------------------------------- */
		if (DateUtil.round2Day(date1).getTime() != DateUtil.round2Day(date2)
				.getTime()) {
			/* ------------------------------------------------------- */
			TimeOfDay startTime = DateUtil.getTimeOfDay(date1);
			TimeOfDay endTime = DateUtil.getTimeOfDay(date2);
			Date date = date1;
			// date = DateUtil.getDiffDay(date, +1);
			while (true) {
				/* ------------------------------------------------------- */
				Date start = DateUtil.setTimeOfDate(date, startTime);
				Date end = DateUtil.setTimeOfDate(date, endTime);
				if (end.after(date2))
					break;
				_selectedEvents.addAll(getEditibleEvents(id, new DateInterval(
						start, end)));
				date = DateUtil.getDiffDay(date, +1);
				/* ------------------------------------------------------- */
			}
			/* ------------------------------------------------------- */
		} else
			_selectedEvents.addAll(getEditibleEvents(id, new DateInterval(
					date1, date2)));
		Iterator<Event> i = _selectedEvents.iterator();
		while (i.hasNext()) {
			Event event = (Event) i.next();
			FrameArea area = getFrameArea(id, event);
			area.setSelected(true);
			if (listener != null)
				listener.eventSelected(id, event);
		}
		if (listener != null)
			listener.eventsSelected(_selectedEvents);
		/* ================================================== */
	}

	/**
	 * @param calId
	 * @param interval
	 * @return
	 * @throws Exception
	 */
	private List<Event> getEditibleEvents(Object calId, DateInterval interval)
			throws Exception {
		/* ================================================== */
		List<Event> result = new ArrayList<Event>();
		List<Object> events = (List<Object>) _eventMap.get(calId);
		/* ------------------------------------------------------- */
		if (events == null || events.size() < 1)
			return result;
		/* ------------------------------------------------------- */
		Iterator<Object> i = events.iterator();
		while (i.hasNext()) {
			/* ------------------------------------------------------- */
			Event event = (Event) i.next();
			if (event.isEditable()) {
				DateInterval eventInterval = new DateInterval(event.getStart(),
						event.getEnd());
				boolean overlap = eventInterval.overlap(interval);
				if (overlap)
					result.add(event);
			}
			/* ------------------------------------------------------- */
		}
		return result;
		/* ================================================== */
	}

	private boolean isSelected(Event event) {
		if (event == null || event.getId() == null)
			return false;
		Iterator<Event> i = _selectedEvents.iterator();
		while (i.hasNext()) {
			Event tmpEvent = (Event) i.next();
			if (tmpEvent.getId() != null && tmpEvent.getId().equals(event.getId()))
				return true;
		}
		return false;
	}

	public void setDescriptor(CalendarViewConfig desc) {
		this.desc = desc;
	}

	public CalendarViewConfig getDescriptor() {
		return desc;
	}

	protected JComponent createCorner(boolean left, boolean top)
			throws Exception {
		String direction = GradientArea.LEFT_RIGHT;
		if (!left && top)
			direction = GradientArea.TOP_BOTTOM;
		else if (left && top)
			direction = GradientArea.TOPLEFT_BOTTOMRIGHT;
		GradientArea area = new GradientArea(direction, Color.WHITE,
				ColumnHeaderPanel.GRADIENT_COLOR);
		area.setOpaque(true);
		area.setBorder(false);
		return area;
	}

	protected int getInitYPos() throws Exception {
		return 0;
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
					JPopupMenu popup = popupMenuCallback
							.getCalendarPopupMenu(calId);
					popup.show(e.getComponent(), e.getX(), e.getY());
				}
			} catch (Exception exc) {
				throw BizcalException.create(exc);
			}
		}

		public void mouseEntered(MouseEvent e) {
			// rootPanel.setCursor(new Cursor(Cursor.HAND_CURSOR));
		}

		public void mouseExited(MouseEvent e) {
			// rootPanel.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
		}
	}

	// /**
	// * @author martin.heinemann@tudor.lu
	// * 08.04.2008
	// * 12:56:59
	// *
	// *
	// * @version
	// * <br>$Log: CalendarView.java,v $
	// * <br>Revision 1.60  2015/01/15 10:21:48  schorsch262
	// * <br>printlines removed
	// * <br>
	// * <br>Revision 1.59  2015/01/06 15:12:54  schorsch262
	// * <br>calendar selection automatically deselects the former selected calendar
	// * <br>
	// * <br>Revision 1.58  2012/06/19 16:16:24  thorstenroth
	// * <br>remove system.out.println.
	// * <br>
	// * <br>Revision 1.57  2012/06/19 16:09:17  thorstenroth
	// * <br>Bug fix: double click on Appointment has not work.
	// * <br>
	// * <br>Revision 1.56  2012/03/14 13:06:47  thorstenroth
	// * <br>Bug fix:  Popup menu open now again if there is a right click in the CalendarView.
	// * <br>
	// * <br>Revision 1.55  2012/01/30 16:06:43  thorstenroth
	// * <br>Bug fix: right click on a event was broken.
	// * <br>
	// * <br>Revision 1.54  2011/09/15 16:18:51  thorstenroth
	// * <br>Fix Bug - Now the line breaks in the FrameArea and tootips are are visible.
	// * <br>
	// * <br>Revision 1.53 2011/08/01 14:59:40 thorstenroth
	// * <br>Implement a new function that scale the resize region when resizing
	// a appointment.
	// * <br>
	// * <br>Revision 1.52 2011/07/28 08:37:28 thorstenroth
	// * <br>fix bugs:
	// * <br>- better movement by drag and resize a appointment.
	// * <br>- current time line are now not repaint when a appointment is
	// create, dragged or resized.
	// * <br>- the shift-key which allows a pixel accuracy dragging and
	// resizeing of appointments, can now pressed any time not only at the
	// beginning of drag or resize action.
	// * <br>
	// * <br>Revision 1.51 2011/07/26 15:12:17 thorstenroth
	// * <br>code clearup
	// * <br>
	// * <br>Revision 1.50 2011/07/26 13:46:26 thorstenroth
	// * <br>Fix Ticket #887.
	// * <br>
	// * <br>Revision 1.49 2011/07/15 15:33:00 thorstenroth
	// * <br>fixed Ticket #416
	// * <br>
	// * <br>Revision 1.48 2011/07/14 11:43:14 thorstenroth
	// * <br>Upgrading the selection of appointments.
	// * <br>
	// * <br>Revision 1.47 2011/07/06 13:55:50 thorstenroth
	// * <br>fix the deadlock in class DayView in Line 660 when try to get a
	// date form empty hashmap.
	// * <br>
	// * <br>Revision 1.46 2011/06/14 14:49:58 thorstenroth
	// * <br>fix Bug #842
	// * <br>
	// * <br>Revision 1.45 2011/05/18 12:56:45 thorstenroth
	// * <br>New final implementation of the FrameArea Paint method.
	// * <br>
	// * <br>Revision 1.44 2011/05/17 15:22:46 thorstenroth
	// * <br>1. fix bugs that compute a wrong end time when resize the
	// appointment. it only happens if appointment are recurrence.
	// * <br>2. New implementation of the FrameArea Paint method (it is not
	// final).
	// * <br>
	// * <br>Revision 1.43 2011/03/04 15:32:07 thorstenroth
	// * <br>Little redesign of the frame area show now the start and end time
	// of a event in the footer too.
	// * <br>
	// * <br>Revision 1.42 2011/03/04 12:45:35 thorstenroth
	// * <br>1. Improvement of the mouse controls when event gets resize and
	// move in the calendar.
	// * <br>2. Bug Fix: The position of the current timeline is now correct and
	// only shown ar the current day.
	// * <br>3. Bug Fix: Because of the bug the view can not difference between
	// Events form different calendars which have the same start and end time so
	// sometimes by resize or move a event there are side effects when drawing
	// the events.
	// * <br>
	// * <br>Revision 1.41 2011/02/22 15:10:26 thorstenroth
	// * <br>Comment one bug in code
	// * <br>
	// * <br>Revision 1.40 2011/02/22 14:59:32 thorstenroth
	// * <br>1. Add a new layout for the day view. This layout split the day
	// column into a number of lines which is equal to the number of calendars
	// which are active. The events of one calendar are now shown in one line,
	// one below the other.
	// * <br>
	// * <br>2. Add a new horizontal line to the day view to represent the
	// current time.
	// * <br>
	// * <br>Revision 1.39 2010/04/21 08:28:28 hermenj
	// * <br>fixed annoying bug when trying to show popup in some cases
	// * <br>
	// * <br>Revision 1.38 2009/04/28 14:11:19 heine_
	// * <br>some dst fixes. Not yet finished but better than before...
	// * <br>
	// * <br>Revision 1.37 2008/10/30 10:42:52 heine_
	// * <br>*** empty log message ***
	// * <br>
	// * <br>Revision 1.36 2008/10/21 15:08:31 heine_
	// * <br>*** empty log message ***
	// * <br>
	// * <br>Revision 1.35 2008/10/09 12:33:09 heine_
	// * <br>shows now the summary of an event in the header of a FrameArea and
	// the the description is now in the body.
	// * <br>
	// * <br>Revision 1.34 2008/08/12 12:47:27 heine_
	// * <br>fixed some bugs and made code improvements
	// * <br>
	// * <br>Revision 1.33 2008/06/19 12:20:00 heine_
	// * <br>*** empty log message ***
	// * <br>
	// * <br>Revision 1.32 2008/06/10 13:16:36 heine_
	// * <br>*** empty log message ***
	// * <br>
	// * <br>Revision 1.31 2008/06/09 14:10:09 heine_
	// * <br>*** empty log message ***
	// * <br>
	// * <br>Revision 1.30 2008/05/30 11:36:47 heine_
	// * <br>*** empty log message ***
	// * <br>
	// * <br>Revision 1.29 2008/05/26 08:15:31 heine_
	// * <br>removed MainThread locking by swing worker thread
	// * <br>
	// * <br>Revision 1.28 2008/04/24 14:17:37 heine_
	// * <br>Improved timeslot search when clicking and moving
	// * <br>
	// * <br>Revision 1.27 2008/04/08 13:17:53 heine_
	// * <br>*** empty log message ***
	// * <br>
	// *
	// */
	// protected class DateLabelGroup extends ComponentAdapter {
	//		
	// private List<JLabel> labels = new ArrayList<JLabel>();
	//
	// private List<Date> dates = new ArrayList<Date>();
	//
	// private List<DateFormat> patterns = new ArrayList<DateFormat>();
	//
	// public void addLabel(JLabel label, Date date) {
	// labels.add(label);
	// dates.add(date);
	// }
	//
	// public void addPattern(String pattern) {
	// patterns.add(new SimpleDateFormat(pattern));
	// }
	//
	// public void addFormat(DateFormat format) {
	// patterns.add(format);
	// }
	//
	// public void componentResized(ComponentEvent event) {
	// /* ================================================== */
	// try {
	// if (patterns.size() == 0) {
	// /* ------------------------------------------------------- */
	// Locale l = LocaleBroker.getLocale();
	// patterns
	// .add(DateFormat.getDateInstance(DateFormat.LONG, l));
	// patterns.add(DateFormat.getDateInstance(DateFormat.MEDIUM,
	// l));
	// patterns.add(DateFormat
	// .getDateInstance(DateFormat.SHORT, l));
	// /* ------------------------------------------------------- */
	// }
	// int maxPatternIndex = 0;
	// for (int i = 0; i < labels.size(); i++) {
	// JLabel label = (JLabel) labels.get(i);
	// Date date = (Date) dates.get(i);
	// for (int j = 0; j < patterns.size(); j++) {
	// DateFormat format = (DateFormat) patterns.get(j);
	// FontMetrics metrics = label.getFontMetrics(label
	// .getFont());
	// int width = metrics.stringWidth(format.format(date));
	// if (width < event.getComponent().getWidth()) {
	// if (j > maxPatternIndex)
	// maxPatternIndex = j;
	// break;
	// }
	// if (j == patterns.size() - 1)
	// maxPatternIndex = patterns.size() - 1;
	// }
	// }
	// DateFormat format = (DateFormat) patterns.get(maxPatternIndex);
	// // DateFormat format = (DateFormat) patterns.get(0);
	// for (int i = 0; i < labels.size(); i++) {
	// JLabel label = (JLabel) labels.get(i);
	// Date date = (Date) dates.get(i);
	// label.setText(TextUtil.formatCase(format.format(date)));
	// }
	// } catch (Exception e) {
	// ErrorHandler.handleError(e);
	// }
	// }
	// /* ================================================== */
	// }

	protected List<?> getSelectedCalendars() throws Exception {
		return broker.getSelectedCalendars();
	}

	protected DateInterval getInterval() throws Exception {
		return broker.getInterval();
	}

	public CalendarModel getModel() {
		return broker;
	}

	protected FrameArea getBaseFrameArea(Event e) {
		return frameAreaHash.get(e);
	}

	/**
	 * @param calId
	 * @return
	 * @throws Exception
	 */
	protected Map<Date, List<Event>> createEventsPerDay(Object calId)
			throws Exception {
		/* ================================================== */
		Map<Date, List<Event>> map = new HashMap<Date, List<Event>>();
		/* ------------------------------------------------------- */
		// iterate over all events
		/* ------------------------------------------------------- */
		List<Event> eventList = getModel().getEvents(calId);

		if (eventList != null)
			for (Event event : eventList) {
				/* ------------------------------------------------------- */
				Date date = DateUtil.round2Day(event.getStart());
				List<Event> events = map.get(date);
				if (events == null) {
					/* ------------------------------------------------------- */
					events = new ArrayList<Event>();
					map.put(date, events);
					/* ------------------------------------------------------- */
				}
				events.add(event);
				/* ------------------------------------------------------- */
				// check if the event takes place in more than one day
				/* ------------------------------------------------------- */
				if (!DateUtil.isSameDay(event.getStart(), event.getEnd())) {
					/* ------------------------------------------------------- */
					// we iterate over the time as long as its finished
					/* ------------------------------------------------------- */
					Date next = DateUtil.move(event.getStart(), 1);
					// as long end date is not the same day as the next,
					// continue
					while (DateUtil.isBeforeDay(next, event.getEnd())
							|| DateUtil.isSameDay(event.getEnd(), next)) {
						/*
						 * ------------------------------------------------------
						 * -
						 */
						date = DateUtil.round2Day(next);
						events = map.get(date);
						if (events == null) {
							/*
							 * --------------------------------------------------
							 * -----
							 */
							events = new ArrayList<Event>();
							map.put(date, events);
							/*
							 * --------------------------------------------------
							 * -----
							 */
						}
						events.add(event);

						next = DateUtil.move(next, 1);
						/*
						 * ------------------------------------------------------
						 * -
						 */
					}
					/* ------------------------------------------------------- */
				}
			}
		return map;
		/* ================================================== */
	}

	public abstract JComponent getComponent();

	public void clear() {
		_selectedEvents.clear();
	}

	/**
	 * Converts a point from the source components coordinates system to the
	 * target components coordinates system.
	 * 
	 * @param p
	 *            the point to convert
	 * @param source
	 *            the source component from which the point is inherited
	 * @param target
	 *            the component for which the point should be converted
	 * @return
	 */
	public static Point convertPoint(Point p, Component source, Component target) {
		/* ================================================== */
		Point pNew = new Point(p);

		// convert point from source to screen
		SwingUtilities.convertPointToScreen(pNew, source);
		// convert point from screen to target
		SwingUtilities.convertPointFromScreen(pNew, target);

		return pNew;
		/* ================================================== */
	}

	// TODO Test Calid
	public void setSelectedCalendarInCV(NamedCalendar selectedCalendar) {
		try {
			// deselect();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.selectedCalendar = selectedCalendar;
	}

	/**
	 * Get creating status of CalendarView. Return true if a appointment is in
	 * creation process.
	 * 
	 * @return isCreating the creating status
	 */
	public boolean getIsCreating() {
		return isCreating;
	}

	/**
	 * Get dragging status of CalendarView. Return true if a appointment is in
	 * dragging.
	 * 
	 * @return isDragging the dragging status
	 */
	public boolean getIsDragging() {
		return isDragging;
	}

	/**
	 * Get resizeing status of CalendarView. Return true if a appointment is in
	 * resizeing.
	 * 
	 * @return isResizeing the resizeing status
	 */
	public boolean getIsResizeing() {
		return isResizeing;
	}
}