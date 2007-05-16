package bizcal.swing;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
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
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPopupMenu;
import javax.swing.JTextField;

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
import bizcal.util.LocaleBroker;
import bizcal.util.TextUtil;
import bizcal.util.TimeOfDay;

public abstract class CalendarView
{
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


	private static boolean draggingEnabled = true;

	/**
	 * Member to store the original clicked FrameArea in a dragging
	 * event.
	 */
	private static FrameArea originalClickedFrameArea = null;

	/**
	 * Offset to compute positions next to a vertical line
	 */
	private static final int LINE_OFFSET = 5;

	/**
	 * Static member to store if a mouse button was pressed.
	 * Used to avoid cursor checking in mouseMoved method of FrameAreas
	 */
	private static boolean isMousePressed = false;

	/**
	 * Member to store the state if a frameArea is resizable
	 */
	private static boolean isResizeable = false;


	protected List<JLabel> vLines = new ArrayList<JLabel>();
	private List<JLabel> hLines = new ArrayList<JLabel>();

	HashMap<Event, FrameArea> frameAreaHash = new HashMap<Event, FrameArea>();


	private JComponent calPanel;
	private int currentLine = 0;
	private JDialog mousePanel;
	private JTextField mousePosLabel;




	public CalendarView(CalendarViewConfig desc)
		throws Exception
	{
		this.desc = desc;
		font = desc.getFont();


//		this.mousePanel = new JDialog();
//		mousePanel.setLayout(new BorderLayout());
//		this.mousePosLabel = new JTextField(15);
//		mousePanel.add(mousePosLabel);
//
//		this.mousePanel.setPreferredSize(new Dimension(100,60));
//		mousePanel.pack();
//		mousePanel.setVisible(true);

    }

	protected void setMousePos(MouseEvent e) {
		/* ================================================== */
//		this.mousePosLabel.setText("Mouse: " + e.getX() + " : " + e.getY());
////		mousePosLabel.validate();
////		mousePosLabel.updateUI();
//		mousePanel.validate();
		/* ================================================== */
	}


	protected LayoutManager getLayout()
	{
		return null;
	}

	public final void refresh() throws Exception
	{
		_frameAreaMap.clear();
		_eventMap.clear();
		refresh0();
	}

	public abstract void refresh0() throws Exception;

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

		area.setEvent(event);

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
		area.setCursor(new Cursor(Cursor.HAND_CURSOR));

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


	/**
	 * Returns the estimated height for a time slot
	 *
	 * @return
	 */
	public int getTimeSlotHeight() {
		/* ================================================== */
		if (hLines != null){
			// find two lines with a y
			Integer y1 = null;
			Integer y2 = null;
			/* ------------------------------------------------------- */
			for (JLabel l : hLines) {
				if (l.getBounds().y > 0) {
					if (y1 == null)
						y1 = l.getBounds().y;
					else {
						y2 = l.getBounds().y;
						break;
					}
				}
			}
			/* ------------------------------------------------------- */
			// compute gap
			return (-1)*(y1-y2);
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
			return vLines.get(0).getBounds().x;
			/* ------------------------------------------------------- */
		}
		return -1;
		/* ================================================== */
	}


	private class FrameAreaMouseListener
		extends MouseAdapter
		implements MouseMotionListener
	{




		private Point _startDrag;
		private FrameArea _frameArea;
		private Object _calId;
		private Event _event;
		private Integer nextSmallerLinePos = null;
		private Integer nextGreaterLinePos;

//		private boolean isResizeable = false; --> moved to static
		private Cursor resizeCursor = new Cursor(Cursor.S_RESIZE_CURSOR);
		private Cursor handCursor = new Cursor(Cursor.HAND_CURSOR);

		private FrameArea lastCreatedFrameArea = null;
		private Integer lastCreatedKey = null;

		private Integer mouseXold = -1;

		private HashMap<Integer, FrameArea> additionalFrames = new HashMap<Integer, FrameArea>();
		private List<FrameArea> deletedFrameAreas = new ArrayList<FrameArea>();


		public FrameAreaMouseListener(FrameArea frameArea, Object calId, Event event)
		{
			_frameArea = frameArea;
			_calId = calId;
			_event = event;
		}

		public void mousePressed(MouseEvent e)
		{
			/* ------------------------------------------------------- */
			CalendarView.isMousePressed = true;

			// store the clicked FrameArea
			if (originalClickedFrameArea == null)
				originalClickedFrameArea = _frameArea;
			/* ------------------------------------------------------- */
			/* ------------------------------------------------------- */
			_startDrag = e.getPoint();
			/* ------------------------------------------------------- */
			FrameArea baseFrameArea = frameAreaHash.get(_event);
//			if (!_frameArea.equals(baseFrameArea)) {
//				baseFrameArea.getMouseListeners()[0].mousePressed(e);
//				return;
//			}
			// fill additional frames
			additionalFrames.clear();
			if (baseFrameArea.getChildren() != null)
				for (FrameArea fa : baseFrameArea.getChildren()) {
					additionalFrames.put(fa.getBounds().x, fa);
				}
			lastCreatedFrameArea = findLastFrameArea(baseFrameArea);
			if (lastCreatedFrameArea != null)
				lastCreatedKey = lastCreatedFrameArea.getBounds().x;
			/* ------------------------------------------------------- */
			maybeShowPopup(e);
		}

	    public void mouseReleased(MouseEvent e)
	    {
	    	FrameArea baseFrameArea = frameAreaHash.get(_event);
			if (!baseFrameArea.equals(_frameArea)) {
				baseFrameArea.getMouseListeners()[0].mouseReleased(e);
				return;
			}
			/* ------------------------------------------------------- */
			// clear the deleted
			for (FrameArea fa : deletedFrameAreas) {
				fa.setVisible(false);
				calPanel.remove(fa);
			}
			getComponent().revalidate();
			/* ------------------------------------------------------- */
	    	try {
	    		if (listener != null) {
	    			if (isResizeable) {
	    				/* ------------------------------------------------------- */
	    				FrameArea fa = findLastFrameArea(baseFrameArea);
	    				if (fa == null)
	    					fa = baseFrameArea;
	    				/* ------------------------------------------------------- */
	    				_event.setEnd(getDate(fa.getBounds().x+5, fa.getBounds().y + fa.getBounds().height));
	    				System.out.println("changed " + _event.getEnd());
	    				/* ------------------------------------------------------- */
	    			}
	    			System.out.println("moved");
	    			listener.moved(_event, _calId, _event.getStart(),
	    					_calId,
	    					getDate(baseFrameArea.getBounds().x+5, baseFrameArea.getBounds().y));
	    		}
				maybeShowPopup(e);
		    } catch (Exception exc) {
	    		ErrorHandler.handleError(exc);
		    }
		    _frameArea.setIsMoving(false);

		    // reset the original frameArea
		    originalClickedFrameArea = null;

		    CalendarView.isMousePressed = false;
	    }

	    public void mouseEntered(MouseEvent e)
	    {
	    	FrameArea baseFrameArea = frameAreaHash.get(_event);
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
	    		if (_frameArea.getChildren() != null)
	    			for (FrameArea fa : _frameArea.getChildren()) {
	    				fa.setAlphaValue(_frameArea.getAlphaValue()+0.2f);
	    				fa.setBorder(true);
	    				fa.repaint();
	    			}
	    		_frameArea.setAlphaValue(_frameArea.getAlphaValue()+0.2f);
	    		_frameArea.setBorder(true);
	    		_frameArea.repaint();


		    } catch (Exception exc) {
	    		ErrorHandler.handleError(exc);
		    }
	    }

	    public void mouseExited(MouseEvent e)
	    {
	    	FrameArea baseFrameArea = frameAreaHash.get(_event);
			if (!baseFrameArea.equals(_frameArea)) {
				baseFrameArea.getMouseListeners()[0].mouseExited(e);
				return;
			}
			/* ------------------------------------------------------- */
			if (CalendarView.isMousePressed)
				return;
	    	try {
	    		if (!_event.isSelectable())
	    			return;
	    		getComponent().setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
				_frameArea.setAlphaValue(_frameArea.getAlphaValue()-0.2f);
				//_frameArea.setBorder(false);
				_frameArea.repaint();
				if (_frameArea.getChildren() != null)
	    			for (FrameArea fa : _frameArea.getChildren()) {
	    				fa.setAlphaValue(_frameArea.getAlphaValue()-0.2f);
	    				fa.setBorder(false);
	    				fa.repaint();
	    			}

	    	} catch (Exception exc) {
	    		ErrorHandler.handleError(exc);
	    	}
	    }

	    public void mouseClicked(MouseEvent e)
	    {
	    	FrameArea baseFrameArea = frameAreaHash.get(_event);
			if (!baseFrameArea.equals(_frameArea)) {
				baseFrameArea.getMouseListeners()[0].mouseClicked(e);
				return;
			}
			/* ------------------------------------------------------- */
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
			FrameArea baseFrameArea = frameAreaHash.get(_event);
//
//			if (!baseFrameArea.equals(_frameArea)) {
//				e.setSource(baseFrameArea);
//				baseFrameArea.getMouseMotionListeners()[0].mouseDragged(e);
//				return;
//			}
			// if we are here, we are working on the baseFrameArea
			try  {
				// DEBUG
				setMousePos(e);
			// **************************************************************************
			//
			// compute values for detecting the crossing of a vertical line
			//
			int currX 		= _frameArea.getBounds().x;
			int currY 		= _frameArea.getBounds().y;
			int currWidth 	= _frameArea.getBounds().width;
			int currHeight  = _frameArea.getBounds().height;

			int nextSmaller = findNextSmallerVerticalLine(currX+LINE_OFFSET);
			int nextGreater = findNextGreaterVerticalLine(currX+LINE_OFFSET);

			int nextGreaterHorizontalLine = findNextGreaterHorizontalLinePos(currY + e.getPoint().y);

			int colWidth  = getColumnWidth();
			int gap2left  = currX - nextSmaller;
			int gap2right = nextGreater - currX - currWidth;
			// **************************************************************************

			baseFrameArea.setIsMoving(true);
			/* ------------------------------------------------------- */
			// resizing
			/* ------------------------------------------------------- */
			if (CalendarView.isResizeable) {

//				System.out.println("X: " + e.getPoint().x + " Y: "+ e.getPoint().y);
    			/* ------------------------------------------------------- */
    			// try to make a new frame for a new day
    			// or remove one

				// ###################################################################################
				// Adjustments for the last area

				FrameArea lastArea = (FrameArea) e.getSource();
				currX = lastArea.getBounds().x;
				currY = lastArea.getBounds().y;
				currWidth = lastArea.getWidth();

				gap2left  = currX - findNextSmallerVerticalLine(currX+LINE_OFFSET);
				gap2right = findNextGreaterVerticalLine(currX+LINE_OFFSET) - currX - currWidth;

				// ###################################################################################

				/* ------------------------------------------------------- */
				// if the mouse pointer is not in the original column
					if ((gap2left + e.getPoint().x) < 0
							|| (e.getPoint().x > currWidth + gap2right)) {
						/* ------------------------------------------------------- */
						// remove the last frame area, if the mouseX is smaller
						// than the boundX

						if (gap2left + e.getPoint().x < 0) {
							/* ------------------------------------------------------- */
							// remove all areas that are greater than the
							// mousepointer
							if (baseFrameArea.getChildren() != null) {
								List<FrameArea> deleteAreas = new ArrayList<FrameArea>();
								for (FrameArea fa : baseFrameArea.getChildren()) {
									System.out.println("currX: " + currX + " eX: " + e.getPoint().x
											+ " faX: " + fa.getBounds().x
											+ " next: " + findNextSmallerVerticalLine(currX+e.getPoint().x));
									/* ------------------------------------------------------- */
									if (fa.getBounds().x > findNextSmallerVerticalLine(currX+e.getPoint().x)) {
										// remove
										System.out.println("Tschüss");
										// we can not remove them from the
										// panel,
										// because we need the mouselistener
										// until mouse released event
										fa.setVisible(false);
										deleteAreas.add(fa);
										deletedFrameAreas.add(fa);
									}
									/* ------------------------------------------------------- */
								}
								// delete from the parent
								baseFrameArea.getChildren().removeAll(
										deleteAreas);
//								if (lastArea != null)
//									lastArea.getMouseMotionListeners()[0]
//											.mouseDragged(e);
							}
							/* ------------------------------------------------------- */
						}
						else {
							/* ------------------------------------------------------- */
							// <----
							// remove the new areas
							FrameArea currLast = findLastFrameArea(baseFrameArea);
							if (currLast != null
									&& currX + gap2right+e.getPoint().x < currLast.getBounds().x) {
								/* ------------------------------------------------------- */
								System.out.println("OOOOOLDDDDDD");
								System.out.println("Tschüss");
								// we can not remove them from the
								// panel,
								// because we need the mouselistener
								// until mouse released event
								currLast.setVisible(false);
//								baseFrameArea.getChildren().remove(currLast);
								deletedFrameAreas.add(currLast);
								/* ------------------------------------------------------- */
							}


						// ##########################################################
						//
						// create new frame areas, if needed!
						/* ------------------------------------------------------- */
						else {
							// if the mouse pointer has crossed the next
							// vertical line
							int xPoint = 0;
							if (baseFrameArea.equals(CalendarView.originalClickedFrameArea))
								xPoint = e.getPoint().x;
							else
								xPoint = e.getPoint().x + CalendarView.originalClickedFrameArea.getBounds().x;
							System.out.println("xPoint: " + xPoint);
							/* ------------------------------------------------------- */
							if (xPoint > currWidth + gap2right) {
								/* ------------------------------------------------------- */
								List<Integer> newLines = null;
								newLines = findUndrawnLines(currX + this.mouseXold,currX +  e.getPoint().x,
																				colWidth, baseFrameArea.getBounds().x);
								/* ------------------------------------------------------- */
								if (newLines != null && newLines.size() > 0) {
									System.out.println("new lines");
									/* ------------------------------------------------------- */
									// create new frame areas for each line
									for (Integer i : newLines) {
										/* ------------------------------------------------------- */
										// if frame is present, continue
										if (additionalFrames.containsKey(i)) {
											additionalFrames.get(i).setVisible(true);
											continue;
										}
										/* ------------------------------------------------------- */
										// create a new FrameArea
										FrameArea fa = new FrameArea();
//										fa.setBounds(i, 0, currWidth, nextGreaterHorizontalLine);
										fa.setBounds(i, 0, currWidth,
												findNextGreaterHorizontalLinePos(currY + e.getPoint().y));
										System.out.println("new Frame "+i);
										/* ------------------------------------------------------- */
										calPanel.add(fa, new Integer(3));
										fa.setVisible(true);
										additionalFrames.put(i, fa);
										/* ------------------------------------------------------- */
										if (lastCreatedFrameArea != null) {
											/* ------------------------------------------------------- */
												lastCreatedFrameArea.setBounds(
														lastCreatedFrameArea.getBounds().x,
														0, currWidth, calPanel.getMaximumSize().height);
										}
										this.lastCreatedFrameArea = fa;
										this.lastCreatedKey = i;
										baseFrameArea.addChild(fa);
										/* ------------------------------------------------------- */
									}
								}
							} // if
						} // else
						}// else
				}

				// set bounds of the base frame area
				if (baseFrameArea.getChildren() == null || baseFrameArea.getChildren().size() == 0) {
					/* ------------------------------------------------------- */
					// adjust bounds only if there are changes --> performance
					if (baseFrameArea.getBounds().height != findNextGreaterHorizontalLinePos(e.getPoint().y)) {
						System.out.println("new height");
						/* ------------------------------------------------------- */
						if (baseFrameArea.equals(CalendarView.originalClickedFrameArea)) {
							baseFrameArea.setBounds(baseFrameArea.getBounds().x,
													baseFrameArea.getBounds().y,
													currWidth,
													findNextGreaterHorizontalLinePos(e.getPoint().y));
						} else {
							baseFrameArea.setBounds(baseFrameArea.getBounds().x,
									baseFrameArea.getBounds().y,
									currWidth,
									findNextGreaterHorizontalLinePos(e.getPoint().y
											- baseFrameArea.getBounds().y));
						}
						/* ------------------------------------------------------- */
					}
					/* ------------------------------------------------------- */
				}
				else {
					/* ------------------------------------------------------- */
					// set baseframe height to max
					if (baseFrameArea.getBounds().height != calPanel.getMaximumSize().height)
						baseFrameArea.setBounds(baseFrameArea.getBounds().x,
								baseFrameArea.getBounds().y,
								currWidth,
								calPanel.getMaximumSize().height);
					/* ------------------------------------------------------- */

					int diffPoint = (_startDrag.y - e.getPoint().y);
					if (Math.abs(diffPoint) > getTimeSlotHeight()) {
						/* ------------------------------------------------------- */
						int mov = getTimeSlotHeight();
						if (_startDrag.y > e.getPoint().y) {
							mov = mov*(-1);
						}
						/* ------------------------------------------------------- */
						// make sure that the new boundaries are inside the calendar panel
						if (baseFrameArea.getBounds().y + mov >= calPanel.getBounds().y) {
							/* ------------------------------------------------------- */
							FrameArea lfa = findLastFrameArea(baseFrameArea);
							if (lfa != null) {
								/* ------------------------------------------------------- */
								lfa.setBounds(lfa.getBounds().x,
											  lfa.getBounds().y,
											  currWidth,
											  findNextGreaterHorizontalLinePos(currY + e.getPoint().y));
								/* ------------------------------------------------------- */
							}
							/* ------------------------------------------------------- */
						}
					}
				}
				/* ------------------------------------------------------- */
			}
			// ######################################################################################
			// ######################################################################################
			// ===============================================================
			// Non - Resizing  --> Moving
			//
			// ==============================================================
			else {
			/* ------------------------------------------------------- */
				// horizontal moving
				Integer newXPos = null;
				// <-----
				if (e.getPoint().x < 0) {
					/* ------------------------------------------------------- */
					// mousepointer has left the event to the left
					// compute the crossing of a vertical line
					if ((gap2left+e.getPoint().x) < 0) {
						// move to day before
						newXPos = findNextSmallerVerticalLine(baseFrameArea.getBounds().x-5);
						// smaller
					}
					/* ------------------------------------------------------- */
				} else {
					// ---->
					if (e.getPoint().x > currWidth)
						if (e.getPoint().x > currWidth + gap2right) {
							// /* ------------------------------------------------------- */
							newXPos = findNextGreaterVerticalLine(baseFrameArea.getBounds().x);
							// greater
						}
						/* ------------------------------------------------------- */
					 }


				if (newXPos != null && newXPos <= calPanel.getBounds().x+calPanel.getBounds().width)
				{
					/* ------------------------------------------------------- */
					System.out.println("Moving   <----> "+newXPos);

					int y = baseFrameArea.getBounds().y;
					int width = baseFrameArea.getBounds().width;
					int height = baseFrameArea.getBounds().height;

					baseFrameArea.setBounds(newXPos, y, width, height);
					// move additional frames
					//
					if (baseFrameArea.getChildren() != null && baseFrameArea.getChildren().size() > 0) {
						/* ------------------------------------------------------- */
						int count = 1;
						for (FrameArea ac : baseFrameArea.getChildren()) {
								/* ------------------------------------------------------- */
								int acNewX = baseFrameArea.getBounds().x;
								for (int i = 0; i < count; i++) {
									acNewX = findNextGreaterVerticalLine(acNewX);
								}
								/* ------------------------------------------------------- */
								count++;
//								int acNewX = findNextSmallerVerticalLine(ac.getBounds().x-5);
								ac.setBounds(acNewX,
										ac.getBounds().y,
//										ac.getBounds().width,
										width,
										ac.getBounds().height);
								/* ------------------------------------------------------- */
//							}
						}
					}
				} else {
						/* ------------------------------------------------------- */
						// vertical move
//					try {
//					System.out.println("k: " + _startDrag.y + " - " + e.getPoint().y);
//					} catch (Exception ex) {
//						ex.printStackTrace();
//					}

						int diffPoint = (_startDrag.y - e.getPoint().y);
						if (Math.abs(diffPoint) > getTimeSlotHeight()) {
							/* ------------------------------------------------------- */
							int mov = getTimeSlotHeight();
							if (_startDrag.y > e.getPoint().y) {
								mov = mov*(-1);
							}
							/* ------------------------------------------------------- */
							if (baseFrameArea.getBounds().y + mov >= calPanel.getBounds().y) {
								baseFrameArea.setBounds(
										baseFrameArea.getBounds().x,
										baseFrameArea.getBounds().y +mov,
										baseFrameArea.getBounds().width,
										baseFrameArea.getBounds().height);

								/* ------------------------------------------------------- */
								// find last frame
				    			FrameArea lastArea = findLastFrameArea(baseFrameArea);
				    			// if the event lasts longer than a day
				    			if (baseFrameArea.getChildren() != null && baseFrameArea.getChildren().size() > 0) {
				    				/* ------------------------------------------------------- */
									// set the height of the base frame area to
									// the panels bottom
									baseFrameArea.setBounds(baseFrameArea.getBounds().x,
											baseFrameArea.getBounds().y,
											baseFrameArea.getBounds().width,
											calPanel.getBounds().height - baseFrameArea.getBounds().y);
									/* ------------------------------------------------------- */
				    			}
				    			if (lastArea != null)
				    				lastArea.setBounds(lastArea.getBounds().x,
				    						lastArea.getBounds().y ,
				    						lastArea.getBounds().width,
				    						lastArea.getBounds().height + mov);
				    			/* ------------------------------------------------------- */
				    			// recall the mousedragged event to the current
								// frame area if
				    			// this is not the first one, in order to update
								// the _startDrag
				    			// member
				    			// is this an evil hack?
				    			if (!_frameArea.equals(baseFrameArea))
									this.mousePressed(e);
								/* ------------------------------------------------------- */
							}
						}
				}
				try {
					_frameArea.setMovingTimeString(
							getDate(_frameArea.getBounds().x, _frameArea.getBounds().y),
							_event.getEnd());

				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}
			}
				catch (Exception e2) {
					// TODO: handle exception
					e2.printStackTrace();
				}

			this.mouseXold = e.getPoint().x;

			/* ------------------------------------------------------- */
		}

		public void mouseMoved(MouseEvent e) {
			/* ================================================== */
			FrameArea baseFrameArea = frameAreaHash.get(_event);
//			if (!baseFrameArea.equals(_frameArea)) {
//				baseFrameArea.getMouseMotionListeners()[0].mouseMoved(e);
//				return;
//			}
			/* ------------------------------------------------------- */

			/* ------------------------------------------------------- */
			FrameArea areaToChange = null;
			areaToChange = findLastFrameArea(baseFrameArea);
			/* ------------------------------------------------------- */
			if (areaToChange == null)
				areaToChange = baseFrameArea;
			// if mouse is at the bottom, switch to resize mode

			if (!areaToChange.getCursor().equals(this.resizeCursor)) {
				if (e.getPoint().y > areaToChange.getBounds().height - 10) {
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
			}
				else {
					if (!areaToChange.getCursor().equals(this.handCursor)) {
						if (e.getPoint().y < areaToChange.getBounds().height - 10) {
							areaToChange.setCursor(this.handCursor);
							CalendarView.isResizeable = false;
							e.consume();
							return;
						}
					}
				}

			/* ================================================== */
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
				getComponent().setCursor(new Cursor(Cursor.CROSSHAIR_CURSOR));
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
					getComponent().setCursor(null);

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
		private int currPos = 0;
		private HashMap<Integer, FrameArea> additionalFrames = new HashMap<Integer, FrameArea>();

		private FrameArea lastCreatedFrameArea = null;
		private Integer lastCreatedKey = null;
		private int mouseXold = -1;

		private int startDragMouseY = -1;

		public void mouseClicked(MouseEvent e) {
			try {
				if (e.getClickCount() < 2)
					return;
				Date date = getDate(e.getPoint().x, e.getPoint().y);
				Object id = getCalendarId(e.getPoint().x, e.getPoint().y);
	    		if (listener == null)
	    			return;
	    		if (!getModel().isInsertable(id, date))
	    			return;
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

		public void mousePressed(MouseEvent e) {
			/* ================================================== */
			try {
				_startDrag = e.getPoint();
				_dragCalId = getCalendarId(e.getPoint().x, e.getPoint().y);
				_lasso = (e.getModifiersEx() & InputEvent.SHIFT_DOWN_MASK) != 0;
				maybeShowPopup(e);

				currentLine = currPos;

	    	} catch (Exception exc) {
	    		throw BizcalException.create(exc);
	    	}
	    	this.startDragMouseY = findNextSmallerHorizontalLinePos(e.getPoint().y);
			/* ================================================== */
		}

	    public void mouseReleased(MouseEvent e) {
			/* ================================================== */
			try {
				maybeShowPopup(e);
				_dragging = false;
				if (_dragArea == null)
					return;
				/* ------------------------------------------------------- */
				Object id = getCalendarId(e.getPoint().x, e.getPoint().y);
				/* ------------------------------------------------------- */
				// find the date interval for a new event
				// start date is the left upper corner of the main drag area
				Date date1 = getDate(_dragArea.getBounds().x+5, _dragArea
						.getBounds().y);
				/* ------------------------------------------------------- */
				// if there are additionalFrameAreas, the end date is the last
				// position of the latest frame area
				Date date2 = null;
				if (additionalFrames != null && additionalFrames.size() > 0) {
					/* ------------------------------------------------------- */
					// find the last frame
					List<Integer> keys = new ArrayList<Integer>(
							additionalFrames.keySet());
					Collections.sort(keys);

					// get the last frame
					FrameArea lastArea = additionalFrames.get(keys.get(keys
							.size() - 1));
					date2 = getDate(lastArea.getBounds().x + 2, lastArea
							.getBounds().y
							+ lastArea.getBounds().height);
					/* ------------------------------------------------------- */
				} else {
					/* ------------------------------------------------------- */
					// we use the bounds of the _dragArea
					date2 = getDate(_dragArea.getBounds().x + 10, _dragArea
							.getBounds().y
							+ _dragArea.getHeight());
					/* ------------------------------------------------------- */
				}
				if (_lasso) {
					lasso(id, date1, date2);
				}
				/* ------------------------------------------------------- */
				// notify the listener for a new event
				if (!_lasso)// && (date1.before(date2)))
					if (listener != null)
						listener.newEvent(_dragCalId, new DateInterval(date1,
								date2));
				// }
				_dragArea.setVisible(false);
				/* ------------------------------------------------------- */
				// hide all additional lassos
				for (FrameArea a : additionalFrames.values())
					a.setVisible(false);
				/* ------------------------------------------------------- */
				// reset the mouse pointer
				_dragArea.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
				/* ------------------------------------------------------- */
				// getCalenderArea().remove(_dragArea);
				_lasso = false;
				_dragArea = null;
				_dragCalId = null;
			} catch (Exception exc) {
				throw BizcalException.create(exc);
			}

			draggingEnabled = true;
			/* ================================================== */
		}

		public void mouseDragged(MouseEvent e) {
			/* ================================================== */

			// **************************************************************************
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
					_dragArea.setBounds(e.getPoint().x, e.getPoint().y, 1, 1);
					//getCalenderArea().add(_dragArea);
					getComponent().revalidate();
				}
				if (_dragArea == null) {
					return;
				}
				Object calId = getCalendarId(e.getPoint().x, e.getPoint().y);
				if (!calId.equals(_dragCalId)) {
					e.consume();
					return;
				}
				int y = this.startDragMouseY;



				/* ------------------------------------------------------- */


				int pX = e.getPoint().x;
				int pY = findNextGreaterHorizontalLinePos(e.getPoint().y);
				Integer offset = null;

				if (draggingEnabled) {
					_dragArea.setCursor(new Cursor(Cursor.S_RESIZE_CURSOR));
					if (pX >= _startDrag.x) {

						// if the mouse is in the first column, the x value is 0
						if (pX < findSmallestLine().getBounds().x)
							currPos = 0;
						else
						for (JLabel l : vLines) {
							/* ------------------------------------------------------- */
							Integer xpos = l.getBounds().x;
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
							/* ------------------------------------------------------- */
					}
					// set start time
					if (_dragArea instanceof FrameArea) {
						((FrameArea) _dragArea).setStartTime(getDate(currPos,
								this.startDragMouseY));
					}
				}
				/* ------------------------------------------------------- */

				/* ------------------------------------------------------- */
				int gap = vLines.get(0).getBounds().x;
//				if (pX >= _startDrag.x) {
				if (pX >= currPos) {

					/* ------------------------------------------------------- */

				if (!(pX > currPos && pX < (currPos + gap))) {
					/* ------------------------------------------------------- */
					// remove the last frame area, if the mouseX is smaller than the boundX
					if (lastCreatedFrameArea != null && pX <= lastCreatedFrameArea.getBounds().x) {
						/* ------------------------------------------------------- */
						// remove
						additionalFrames.values().remove(lastCreatedFrameArea);
						lastCreatedFrameArea.setVisible(false);
						calPanel.remove(lastCreatedFrameArea);
						additionalFrames.remove(lastCreatedKey);
						/* ------------------------------------------------------- */
						// but now we must get the next smaller frame
						// find the biggest key
						Integer biggest = 0;
						/* ------------------------------------------------------- */
						for (Integer k : additionalFrames.keySet())
							if (k > biggest)
								biggest = k;
						/* ------------------------------------------------------- */
						lastCreatedFrameArea = additionalFrames.get(biggest);
						lastCreatedKey = biggest;
						/* ------------------------------------------------------- */
					}

					/* ------------------------------------------------------- */
					// ##########################################################
					//
					// create new frame areas, if needed!
					/* ------------------------------------------------------- */
					List<Integer> newLines = null;
					if (lastCreatedKey == null)
						newLines = findUndrawnLines(this.mouseXold, e.getPoint().x,
																	gap, currPos);
					else
						newLines = findUndrawnLines(this.mouseXold, e.getPoint().x,
								gap, lastCreatedKey);
					/* ------------------------------------------------------- */
					if (newLines != null && newLines.size() > 0) {
						/* ------------------------------------------------------- */
						// create new frame areas for each line
						for (Integer i : newLines) {
							/* ------------------------------------------------------- */
							// if frame is present, continue
							if (additionalFrames.containsKey(i))
								continue;
							/* ------------------------------------------------------- */
							// create a new FrameArea
							FrameArea fa = new FrameArea();
							fa.setBounds(i, 0, gap, pY);
							/* ------------------------------------------------------- */
							calPanel.add(fa, new Integer(3));
							fa.setVisible(true);
							additionalFrames.put(i, fa);
							/* ------------------------------------------------------- */
							if (lastCreatedFrameArea != null) {
								/* ------------------------------------------------------- */
									lastCreatedFrameArea.setBounds(
											lastCreatedFrameArea.getBounds().x,
											0, gap, calPanel.getMaximumSize().height);
							}
							this.lastCreatedFrameArea = fa;
							this.lastCreatedKey = i;
							/* ------------------------------------------------------- */
						}
						_dragArea.setBounds(currPos, y,	gap, calPanel.getMaximumSize().height);
						/* ------------------------------------------------------- */
					}
					/* ------------------------------------------------------- */

				/* ------------------------------------------------------- */
					try {
						lastCreatedFrameArea.setBounds(
								lastCreatedFrameArea.getBounds().x,
								0, gap, findNextGreaterHorizontalLinePos(e.getPoint().y));
						/* ------------------------------------------------------- */
							((FrameArea) lastCreatedFrameArea).setEndTime(getDate(currPos,
									lastCreatedFrameArea.getBounds().y + lastCreatedFrameArea.getBounds().height));
					} catch (Exception e2) {
					}

				} else {
					for (Integer k : additionalFrames.keySet()) {
						additionalFrames.get(k).setVisible(false);
						calPanel.remove(additionalFrames.get(k));
					}
					additionalFrames.clear();
					lastCreatedFrameArea = null;
					lastCreatedKey = null;
					currentLine = 0;
					/* ------------------------------------------------------- */
					_dragArea.setBounds(currPos, y,	gap, findNextGreaterHorizontalLinePos(e.getPoint().y)-y);
					if (_dragArea instanceof FrameArea)
						((FrameArea) _dragArea).setEndTime(getDate(currPos,
								_dragArea.getBounds().y + _dragArea.getBounds().height));

				}
				}
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

		public void mouseMoved(MouseEvent e)
		{
			getComponent().requestFocusInWindow();
		}

	}


	/**
	 * Returns the last FrameArea that is connected as a child to the
	 * given FrameArea. Crucial factor is the x bound.
	 *
	 * Returns null if there are no children.
	 * @param base
	 * @return
	 */
	private FrameArea findLastFrameArea(FrameArea base) {
		/* ================================================== */
		if (base.getChildren() == null)
			return null;
		/* ------------------------------------------------------- */
		FrameArea last = null;
		for (FrameArea fa : base.getChildren()) {
			/* ------------------------------------------------------- */
			if (last == null)
				if (fa.isVisible())
					last = fa;
				else
					continue;
			/* ------------------------------------------------------- */
			if (fa.getBounds().x > last.getBounds().x && fa.isVisible())
				last = fa;
			/* ------------------------------------------------------- */
		}
		return last;
		/* ================================================== */
	}

	/**
	 * Returns the first frame after the base frame
	 *
	 * @param base
	 * @return
	 */
	private FrameArea findFirstFrameArea(FrameArea base) {
		/* ================================================== */
		if (base.getChildren() == null)
			return null;
		/* ------------------------------------------------------- */
		FrameArea first = null;
		for (FrameArea fa : base.getChildren()) {
			/* ------------------------------------------------------- */
			if (first == null)
				first = fa;
			/* ------------------------------------------------------- */
			if (fa.getBounds().x < first.getBounds().x)
				first = fa;
			/* ------------------------------------------------------- */
		}
		return first;
		/* ================================================== */
	}


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
			if (l.getBounds().y > mouseY) {
				if (l.getBounds().y < linePos)
					linePos = l.getBounds().y;
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
	 * Gets the next smaller horizontal line
	 * according to the mouse pointer
	 *
	 * @param mouseY
	 * @return
	 */
	private int findNextSmallerHorizontalLinePos(int mouseY) {
		/* ================================================== */
		if (hLines == null)
			return -1;
		/* ------------------------------------------------------- */
		if (mouseY < getTimeSlotHeight())
			return calPanel.getBounds().y;
		int linePos = 100000000;
		for (JLabel l : hLines) {
			/* ------------------------------------------------------- */
			if (l.getBounds().y < linePos && l.getBounds().y >= mouseY-20)
				linePos = l.getBounds().y;
			/* ------------------------------------------------------- */
		}
		return linePos;
		/* ================================================== */
	}



	/**
	 * Find all lines that are unprinted between the last
	 * printed frame area and the current mouspointer
	 *
	 * @param mouseXold
	 * @param mouseXnew
	 * @param gap
	 * @return
	 */
	private List<Integer> findUndrawnLines(int mouseXold, int mouseXnew, int gap, Integer lastFrameX) {
		/* ================================================== */
		List<Integer> returnList = new ArrayList<Integer>();
		// can only find lines if the current mouseposition is greater
		// than the last one

//		if (mouseXold == -1)
//			mouseXold = mouseXnew;
//		System.out.println(gap  + " " + lastFrameX + " "+mouseXnew);
		if (mouseXnew > mouseXold)
			/* ------------------------------------------------------- */
			for (JLabel l : vLines) {
				/* ------------------------------------------------------- */
				if (l.getBounds().x > lastFrameX) {
					if (l.getBounds().x < mouseXnew && !returnList.contains(l)) {
						returnList.add(l.getBounds().x);
					}
				}
				/* ------------------------------------------------------- */
			}
			return returnList;
			/* ------------------------------------------------------- */
		/* ================================================== */
	}

	private int mouseOverLineNew(int mouseXold, int mouseXnew, int gap) {
		/* ================================================== */

		if (mouseXold == -1)
			mouseXold = mouseXnew;
		// for the first column
		if (mouseXnew < gap)
			return -1;
		// direction  --->
		if (mouseXnew > mouseXold) {
			/* ------------------------------------------------------- */
			// find a line that is between these two values
			for (JLabel l : vLines) {
				if (l.getBounds().x > mouseXold
						&& l.getBounds().x < mouseXnew)
					return l.getBounds().x;
			}
			/* ------------------------------------------------------- */
		} else {
			// direction  <-----
			/* ------------------------------------------------------- */
			// find a line that is between these two values
			for (JLabel l : vLines) {
				if (l.getBounds().x +gap < mouseXold
						&& l.getBounds().x +gap > mouseXnew)
					return l.getBounds().x;
			}
			/* ------------------------------------------------------- */
		}
		return -1;
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
			int linePos = 100000000;
			for (JLabel l : vLines) {
				if (l.getBounds().x > mouseX)
					if (l.getBounds().x < linePos)
						linePos = l.getBounds().x;
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
				if (l.getBounds().x < mouseX)
					if (l.getBounds().x > linePos)
						linePos = l.getBounds().x;
			}
			return linePos;
			/* ------------------------------------------------------- */
		}
		return -1;
		/* ================================================== */
	}

	/**
	 * Returns the first line in the list
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
			sL.setBounds(calPanel.getBounds().width, 0, 0, 0);
			vLines.add(sL);
			return sL;
		}
		if (vLines.size() == 1) {
			vLines.get(0).setBounds(calPanel.getBounds().width, 0, 0, 0);
		}

		/* ------------------------------------------------------- */
		JLabel smallest = vLines.get(0);
		for (JLabel l : vLines) {
			/* ------------------------------------------------------- */
			if (l.getBounds().x < smallest.getBounds().x)
				smallest = l;
			/* ------------------------------------------------------- */
		}
		return smallest;
		/* ================================================== */
	}



	/**
	 * Repaints a FrameArea and all its children
	 *
	 * @param a
	 */
	private void repaintFrameAreas(FrameArea a) {
		/* ================================================== */
		if (a == null)
			return;
		/* ------------------------------------------------------- */
		a.repaint();
		if (a.getChildren() != null)
			for (FrameArea fa : a.getChildren())
				fa.repaint();
		/* ================================================== */
	}

//	/**
//	 * Adds the x position of a vertical line of the grid
//	 * to the list
//	 * @param pos
//	 */
//	public void addVerticalLinePosition(Integer pos) {
//		/* ================================================== */
//		this.vLinePos.add(pos);
//		Collections.sort(vLinePos);
//		/* ================================================== */
//	}


	public void addVerticalLine(JLabel line) {
		/* ================================================== */
		this.vLines.add(line);
		/* ================================================== */
	}


	public void addHorizontalLine(JLabel line) {
		/* ================================================== */
		this.hLines.add(line);
		/* ================================================== */
	}


	protected void addDraggingComponents(JComponent calPanel) throws Exception {
		_lassoArea = new LassoArea();
		calPanel.add(_lassoArea, 1000);
		_newEventArea = new FrameArea();
		_newEventArea.setVisible(false);
		calPanel.add(_newEventArea, new Integer(2));
		this.calPanel = calPanel;
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
		getComponent().setCursor(cursor);
	}

	@SuppressWarnings("unchecked")
	protected void register(Object calId, Event event, FrameArea area)
	{
		_frameAreaMap.put("" + calId + event.getId() + event.getStart().getTime(), area);
		List list = (List) _eventMap.get(calId);
		if (list == null) {
			list = new ArrayList();
			_eventMap.put(calId, list);
		}
		list.add(event);
	}

	protected FrameArea getFrameArea(Object calId, Event event)
	{
		return (FrameArea) _frameAreaMap.get("" + calId + event.getId() + event.getStart().getTime());
	}

	public void select(Object calId, Event event, boolean flag)
		throws Exception
	{
		FrameArea area = getFrameArea(calId, event);
		if(area!=null)
			area.setSelected(flag);
		_selectedEvents.add(event);
		listener.eventsSelected(_selectedEvents);
		listener.eventSelected(calId, event);
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
		listener.selectionReset();
	}

	public void copy()
		throws Exception
	{
		listener.copy(_selectedEvents);
	}

	protected boolean supportsDrag()
	{
		return true;
	}

	/*protected void addDraggingComponents()
		throws Exception
	{
		_lassoArea = new LassoArea();
		calPanel.add(_lassoArea, 1000);
		_newEventArea = new FrameArea();
		_newEventArea.setVisible(false);
		calPanel.add(_newEventArea, new Integer(2));
	}*/

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
			listener.eventSelected(id, event);
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

	protected JComponent createCorner(boolean left, boolean top)
	throws Exception
	{
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

	protected int getInitYPos()
	throws Exception
	{
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

	protected Map createEventsPerDay(Object calId) throws Exception {
		Map map = new HashMap();
		Iterator i = getModel().getEvents(calId).iterator();
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
		return map;
	}

	public abstract JComponent getComponent();

	public void clear()
	{
		_selectedEvents.clear();
	}

}