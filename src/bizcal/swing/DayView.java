package bizcal.swing;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.LayoutManager;
import java.awt.Point;
import java.awt.Rectangle;
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
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;

import bizcal.common.DayViewConfig;
import bizcal.common.Event;
import bizcal.swing.CalendarView.ThisKeyListener;
import bizcal.swing.CalendarView.ThisMouseListener;
import bizcal.swing.util.FrameArea;
import bizcal.swing.util.GradientArea;
import bizcal.swing.util.GradientPanel;
import bizcal.swing.util.TrueGridLayout;
import bizcal.util.BizcalException;
import bizcal.util.DateInterval;
import bizcal.util.DateUtil;
import bizcal.util.Interval;
import bizcal.util.TimeOfDay;
import bizcal.util.Tuple;
import bizcal.util.WeekdayFormat;

public class DayView extends CalendarView {
	public static final int PIXELS_PER_HOUR = 50;
	
	private static final int CAPTION_ROW_HEIGHT0 = 20;

	public static final Color LINE_COLOR = new Color(230, 230, 230);

	public static final Color LINE_COLOR_DARKER = new Color(200, 200, 200);

	public static final Color LINE_COLOR_EVEN_DARKER = new Color(100, 100, 100);

	public static final Color HOUR_LINE_COLOR = LINE_COLOR;
	
	public static final int PREFERRED_DAY_WIDTH = 10;
	
	private List frameAreaCols = new ArrayList();

	private List eventColList = new ArrayList();

	private List dateHeaders = new ArrayList();

	private List _dateList = new ArrayList();

	private Map timeLines = new HashMap();

	private Map hourLabels = new HashMap();

	private Map minuteLabels = new HashMap();

	private List vLines = new ArrayList();

	private List calBackgrounds = new ArrayList();

	private ColumnHeaderPanel columnHeader;

	private TimeLabelPanel rowHeader;
	
	private int dayCount;
	
	private JScrollPane scrollPane;
	
	private JLayeredPane calPanel;

	public DayView(DayViewConfig desc) throws Exception {
		super(desc);
		calPanel = new JLayeredPane();
		calPanel.setLayout(new Layout());
		ThisMouseListener mouseListener = new ThisMouseListener();
        ThisKeyListener keyListener = new ThisKeyListener();
        calPanel.addMouseListener(mouseListener);
        calPanel.addMouseMotionListener(mouseListener);
        calPanel.addKeyListener(keyListener);		
        scrollPane = 
        	new JScrollPane(calPanel,
        			JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
					JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setCursor(Cursor.getDefaultCursor());
		scrollPane.getVerticalScrollBar().setUnitIncrement(15);
        scrollPane.setCorner(JScrollPane.UPPER_LEFT_CORNER, createCorner(true, true));
        scrollPane.setCorner(JScrollPane.LOWER_LEFT_CORNER, createCorner(true, false));
        scrollPane.setCorner(JScrollPane.UPPER_RIGHT_CORNER, createCorner(false, true));
		columnHeader = new ColumnHeaderPanel();			
        scrollPane.setColumnHeaderView(columnHeader.getComponent());
		rowHeader = new TimeLabelPanel(new TimeOfDay(0,0),
				new TimeOfDay(24,0));
        scrollPane.setRowHeaderView(rowHeader.getComponent());
	}

	public void refresh0() throws Exception {
		if (calPanel == null)
			return;
		
		dayCount = (int) (getModel().getInterval().getDuration() / (24*3600*1000));
		calPanel.removeAll();
		calPanel.setBackground(Color.WHITE);

		frameAreaCols.clear();
		eventColList.clear();
		timeLines.clear();
		hourLabels.clear();
		minuteLabels.clear();
		calBackgrounds.clear();
		vLines.clear();

		//addDraggingComponents();

		Font hourFont = getDesc().getFont().deriveFont((float) 12);
		hourFont = hourFont.deriveFont(Font.BOLD);

		//Steps through the time axis and adds hour labels, minute labels
		//and timelines in different maps.
		//key: date, value: label
		long pos = getFirstInterval().getStartDate().getTime();
		while (pos < getFirstInterval().getEndDate().getTime()) {
			Date date = new Date(pos);

			// Hour line
			JLabel line = new JLabel();
			line.setBackground(getDesc().getLineColor());
			line.setOpaque(true);
			calPanel.add(line, new Integer(1));
			timeLines.put(new Tuple(date, "00"), line);

			// Half hour line
			//ev lägga denna loop efter att vi placerat ut aktiviteterna
			//då kommer den hamna längst bak men ändå synas
			line = new JLabel();
			line.setBackground(getDesc().getLineColor());
			line.setOpaque(true);
			calPanel.add(line, new Integer(1));
			timeLines.put(new Tuple(date, "30"), line);

			pos += 3600 * 1000;
		}

		createColumns();
		
		Iterator i = getSelectedCalendars().iterator();
		while (i.hasNext()) {
			bizcal.common.Calendar cal = (bizcal.common.Calendar) i.next();
			JPanel calBackground = new JPanel();
			calBackground.setBackground(cal.getColor());
			calBackgrounds.add(calBackground);
			calPanel.add(calBackground);
		}

		calPanel.validate();
		calPanel.repaint();
		
		columnHeader.setModel(getModel());
		columnHeader.setPopupMenuCallback(popupMenuCallback);
		columnHeader.refresh();
		
		initScroll();
		// Hack to make to init scroll work
		JScrollBar scrollBar = scrollPane.getVerticalScrollBar();
		scrollBar.setValue(scrollBar.getValue()-1);
		
	}

	private int getColCount() 
		throws Exception
	{
		return dayCount * getSelectedCalendars().size();
	}

	private DateInterval getFirstInterval() throws Exception {
		Date start = getInterval().getStartDate();
		Date end = DateUtil.getDiffDay(start, +1);
		return new DateInterval(start, end);
	}
	
	private void createColumns() throws Exception {
		DateInterval interval = getFirstInterval();
		int cols = getColCount();

		List events = null;
		DateInterval interval2 = null;
		for (int it = 0; it < cols; it++) {
			int iCal = it / dayCount;
			bizcal.common.Calendar cal = (bizcal.common.Calendar) getSelectedCalendars()
					.get(iCal);
			Object calId = cal.getId();
			events = broker.getEvents(calId);

			if (it % dayCount == 0) 
				interval2 = new DateInterval(interval);

			_dateList.add(interval2.getStartDate());

			Calendar startdate = Calendar.getInstance(Locale.getDefault());
			startdate.setTime(interval2.getStartDate());

			if (it > 0) {
				JLabel verticalLine = new JLabel();
				verticalLine.setOpaque(true);
				verticalLine.setBackground(getDesc().getLineColor());
				if (startdate.get(Calendar.DAY_OF_WEEK) == startdate.getFirstDayOfWeek()) 
					verticalLine.setBackground(LINE_COLOR_DARKER);
				if (getSelectedCalendars().size() > 1 && it % dayCount == 0)
					verticalLine.setBackground(LINE_COLOR_EVEN_DARKER);
				calPanel.add(verticalLine, new Integer(1));
				vLines.add(verticalLine);
			}
			
			List frameAreas = new ArrayList();
			//lägger till en framearea-lista för varje dag
			frameAreaCols.add(frameAreas);
			//får alla event för personen inom intervallet
			if (calId == null)
				continue;
			Interval currDayInterval = getInterval(it % dayCount);
			List colEvents = new ArrayList();
			eventColList.add(colEvents);
			int iEvent = 0;
			if (events == null)
				events = new ArrayList();
			Iterator j = events.iterator();

			while (j.hasNext()) {
				Event event = (Event) j.next();
				DateInterval eventInterv = new DateInterval(event.getStart(),
						event.getEnd());
				if (!currDayInterval.overlap(eventInterv))
					continue;
				FrameArea area = createFrameArea(calId, event);
				frameAreas.add(area);
				colEvents.add(event);
				calPanel.add(area, new Integer(event.getLevel()));
				iEvent++;
			}

			if (dayCount > 1)
				interval2 = incDay(interval2);
		}

	}

	//Får in ett events start- eller slutdatum, höjden på fönstret samt
	//intervallet som positionen ska beräknas utifrån
	private int getYPos(Date aDate, int dayNo) throws Exception {
		long time = aDate.getTime();
		return getYPos(time, dayNo);
	}

	private int getYPos(long time, int dayNo) throws Exception {
		DateInterval interval = getInterval(dayNo);
		time -= interval.getStartDate().getTime();
		double viewPortHeight = getHeight() - getCaptionRowHeight();
		//double timeSpan = (double) getTimeSpan();
		double timeSpan = 24*3600*1000;
		double dblTime = time;
		int ypos = (int) (dblTime / timeSpan * viewPortHeight);
		ypos += getCaptionRowHeight();
		return ypos;
	}

	/*private long getTimeSpan() throws Exception {
		return getDesc().getViewEndTime().getValue()
				- getDesc().getViewStartTime().getValue();
	}*/

	protected Date getDate(int xPos, int yPos) throws Exception {
		int colNo = getColumn(xPos);
		int dayNo = colNo % dayCount;
		DateInterval interval = getInterval(dayNo);
		yPos -= getCaptionRowHeight();
		double ratio = ((double) yPos) / ((double) getTimeHeight());
		long time = (long) (interval.getDuration() * ratio);
		time += interval.getStartDate().getTime();
		return new Date(time);
	}

	private DateInterval getInterval(int dayNo) throws Exception {
		DateInterval interval = getFirstInterval();
		for (int i = 0; i < dayNo; i++)
			interval = incDay(interval);
		return interval;
	}

	private int getColumn(int xPos) 
	throws Exception
	{
		xPos -= getXOffset();
		int width = getWidth() - getXOffset();
		double ratio = ((double) xPos) / ((double) width);
		return (int) (ratio * getColCount());
	}

	private Object getCalendarId(int colNo) 
	throws Exception
	{
		int pos = colNo / dayCount;
		bizcal.common.Calendar cal = (bizcal.common.Calendar) getSelectedCalendars()
				.get(pos);
		return cal.getId();
	}

	private int getColumnWidth() 
	throws Exception
	{
		int colCount = getColCount();
		if (colCount == 0)
			colCount = 1;
		return (calPanel.getWidth() - getXOffset()) / colCount;
	}

	protected int getXOffset() {
		//return LABEL_COL_WIDTH;
		return 0;
	}

	private int getXPos(int colno) 
	throws Exception
	{
		double x = getWidth();
		x = x - getXOffset();
		double ratio = ((double) colno) / ((double) getColCount());
		return ((int) (x * ratio)) + getXOffset();
		/*
		 BigDecimal xPos = new BigDecimal((x * ratio) + getXOffset());
		 return xPos.setScale(0,BigDecimal.ROUND_CEILING).intValue();
		 */
	}

	private int getWidth() {
		return calPanel.getWidth();
	}

	private int getHeight() {
		return calPanel.getHeight();
	}

	private int getTimeHeight() throws Exception {
		return getHeight() - getCaptionRowHeight();
	}

	private class Layout implements LayoutManager {
		public void addLayoutComponent(String name, Component comp) {
		}

		public void removeLayoutComponent(Component comp) {
		}

		public Dimension preferredLayoutSize(Container parent) {
			try {
				int width = dayCount * getModel().getSelectedCalendars().size() * PREFERRED_DAY_WIDTH;
				return new Dimension(width, getPreferredHeight());
			} catch (Exception e) {
				throw BizcalException.create(e);
			}
		}

		public Dimension minimumLayoutSize(Container parent) {
			return new Dimension(50, 100);
		}

		public void layoutContainer(Container parent0) {
			try {
				int width = getWidth();
				int height = getHeight();
				DateInterval day = getFirstInterval();

				int numberOfCols = getColCount();
				if (numberOfCols == 0)
					numberOfCols = 1;

				for (int i = 0; i < eventColList.size(); i++) {
					bizcal.common.Calendar calInfo = (bizcal.common.Calendar) getSelectedCalendars()
							.get(i / dayCount);
					int dayNo = i % dayCount;
					int xpos = getXPos(i);
					int captionYOffset = getCaptionRowHeight()
							- CAPTION_ROW_HEIGHT0;
					int colWidth = getXPos(i + 1) - getXPos(i);
					//Obs. temporär lösning med korrigering med +2. Lägg till korrigeringen på rätt ställe
					//kan höra ihop synkning av tidsaxel och muslyssnare
					int vLineTop = captionYOffset + CAPTION_ROW_HEIGHT0 + 2;
					if (dayNo == 0 && (getSelectedCalendars().size() > 1)) {
						vLineTop = 0;
						day = getFirstInterval();
					}

					Calendar startinterv = Calendar.getInstance(Locale
							.getDefault());
					startinterv.setTime(day.getStartDate());
					
					if (i > 0) {
						JLabel verticalLine = (JLabel) vLines.get(i-1);
						int vLineHeight = height - vLineTop;					
						verticalLine.setBounds(xpos, vLineTop, 1, vLineHeight);
					}

					DateInterval currIntervall = getInterval(dayNo);
					FrameArea prevArea = null;
					int overlapCol = 0;
					int overlapColCount = 0;
					List events = (List) eventColList.get(i);
					List areas = (List) frameAreaCols.get(i);
					int overlapCols[] = new int[events.size()];
					for (int j = 0; j < events.size(); j++) {
						FrameArea area = (FrameArea) areas.get(j);
						Event event = (Event) events.get(j);
						Date startTime = event.getStart();
						if (startTime.before(currIntervall.getStartDate()))
							startTime = currIntervall.getStartDate();
						Date endTime = event.getEnd();
						if (endTime.after(currIntervall.getEndDate()))
							endTime = currIntervall.getEndDate();
						int y1 = getYPos(startTime, dayNo);
						if (y1 < getCaptionRowHeight())
							y1 = getCaptionRowHeight();
						int y2 = getYPos(endTime, dayNo);
						int dy = y2 - y1;
						int x1 = xpos;
						area.setBounds(x1, y1, colWidth, dy);
						
						// Overlap logic
						if (!event.isBackground()) {
							if (prevArea != null) {
								Rectangle r = prevArea.getBounds();
								int prevY2 = r.y + r.height;
								if (prevY2 > y1) {
									// Previous event overlap
									overlapCol++;
									if (prevY2 < y2) {
										// This events finish later than previous
										prevArea = area;
									}
								} else {
									overlapCol = 0;
									prevArea = area;
								}
							}  else
								prevArea = area;
							overlapCols[j] = overlapCol;
							if (overlapCol > overlapColCount)
								overlapColCount = overlapCol;
						} else
							overlapCols[j] = 0;						
					}
					// Overlap logic. Loop the events/frameareas a second 
					// time and set the xpos and widths
					if (overlapColCount > 0) {
						int slotWidth = colWidth / (overlapColCount+1);
						for (int j = 0; j < areas.size(); j++) {
							Event event = (Event) events.get(j);
							if (event.isBackground())
								continue;
							FrameArea area = (FrameArea) areas.get(j);
							int index = overlapCols[j];
							Rectangle r = area.getBounds();
							area.setBounds(r.x + index*slotWidth, r.y, slotWidth, r.height);
						}
					}
					
					if (dayCount > 1)
						day = incDay(day);
				}

				Iterator i = timeLines.keySet().iterator();
				while (i.hasNext()) {
					Tuple key = (Tuple) i.next();
					Date date = (Date) key.elementAt(0);
					int minutes = Integer.parseInt((String) key.elementAt(1));
					JLabel line = (JLabel) timeLines.get(key);
					Date date1 = new Date(date.getTime() + minutes * 60 * 1000);
					int y1 = getYPos(date1, 0);
					int x1 = 0;
					int lineheight = 1;
					if (minutes > 0) {
						//x1 = 25;
						lineheight = 1;
					}
					line.setBounds(x1, y1, width, lineheight);
				}

				for (int iCal = 0; iCal < calBackgrounds.size(); iCal++) {
					int x1 = getXPos(iCal * dayCount);
					int x2 = getXPos((iCal + 1) * dayCount);
					JPanel calBackground = (JPanel) calBackgrounds.get(iCal);
					calBackground.setBounds(x1, getCaptionRowHeight(), x2 - x1,
							getHeight());
				}
				columnHeader.setWidth(getWidth());
			} catch (Exception e) {
				throw BizcalException.create(e);
			}
		}
	}

	private int getNoOfStatusRows() throws Exception {
		int rows = 0;
		if (getDesc().isSummedDay())
			rows++;
		if (getDesc().isSummedWeek())
			rows++;
		if (getDesc().isSummedTotal())
			rows++;
		return rows;
	}

	protected Object getCalendarId(int x, int y) throws Exception {
		return getCalendarId(getColumn(x));
	}

	private DayViewConfig getDesc() {
		DayViewConfig result = (DayViewConfig) getDescriptor();
		if (result == null) {
			result = new DayViewConfig();
			setDescriptor(result);
		}
		return result;
	}

	protected int getInitYPos() throws Exception {
		double viewStart = getModel().getViewStart().getValue();
		double ratio = viewStart / (24*3600*1000);
		return (int) (ratio * 24 * PIXELS_PER_HOUR);
	}
		
	private int getPreferredHeight()
	{
		int hours = 24;				
		return hours * PIXELS_PER_HOUR;		
	}
	
	public JComponent getComponent()
	{
		return scrollPane;
	}
	
	public void initScroll() throws Exception {
		scrollPane.getViewport().setViewPosition(new Point(0, getInitYPos()));
	}
	
		
}
