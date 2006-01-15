package bizcal.swing;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.LayoutManager;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;

import bizcal.common.CalendarModel;
import bizcal.swing.util.GradientArea;
import bizcal.swing.util.TrueGridLayout;
import bizcal.util.BizcalException;
import bizcal.util.DateUtil;
import bizcal.util.LocaleBroker;
import bizcal.util.TextUtil;

public class ColumnHeaderPanel 
{
	public static final Color GRADIENT_COLOR = new Color(230, 230, 230);	
	private PopupMenuCallback popupMenuCallback;
	private JPanel panel;
	private List calHeaders = new ArrayList();
	private List dateHeaders = new ArrayList();
	private List dateList = new ArrayList();
	private GradientArea gradientArea;
	private JLabel refLabel = new JLabel("AAA");
	private int rowCount;
	private int dayCount;
	private int width;
	private CalendarModel model;

	public ColumnHeaderPanel(CalendarModel model,
			PopupMenuCallback popupMenuCallback) throws Exception {
		this.popupMenuCallback = popupMenuCallback;
		this.model = model;
		dayCount = DateUtil.getDateDiff(model.getInterval().getEndDate(),
				model.getInterval().getStartDate());
		panel = new JPanel();
		panel.setLayout(new Layout());
		
		int calCount = model.getSelectedCalendars().size();
		if (dayCount > 1 || calCount > 1) {
			if (dayCount > 1 && calCount > 1)
				rowCount = 2;
			else
				rowCount = 1;
			DateFormat toolTipFormat = new SimpleDateFormat("EEEE d MMMM",
					LocaleBroker.getLocale());
			DateFormat dateFormat = 
				DateFormat.getDateInstance(DateFormat.SHORT, LocaleBroker.getLocale());
			if (dayCount == 5 || dayCount == 7) {
			}
			boolean first = true;
			for (int j = 0; j < calCount; j++) {
				bizcal.common.Calendar cal = (bizcal.common.Calendar) model
						.getSelectedCalendars().get(j);
				if (calCount > 1) {
					JLabel header = new JLabel(cal.getSummary(), JLabel.CENTER);
					header.addMouseListener(new CalHeaderMouseListener(cal
							.getId()));
					header.setAlignmentY(2);
					//header.setFont(font);	
					header.setCursor(new Cursor(Cursor.HAND_CURSOR));
					calHeaders.add(header);
				}
				JPanel dateHeaderPanel = new JPanel();
				dateHeaderPanel.setLayout(new TrueGridLayout(1, dayCount));
				dateHeaderPanel.setOpaque(false);
				Date date = model.getInterval().getStartDate();
				for (int i = 0; i < dayCount; i++) {
					JLabel header = new JLabel(dateFormat.format(date), JLabel.CENTER);
					header.setAlignmentY(2);
					//header.setFont(font);
					header.setToolTipText(toolTipFormat.format(date));
					if (model.isRedDay(date))
						header.setForeground(Color.RED);
					first = false;
					dateHeaders.add(header);
					dateList.add(date);
					panel.add(header);
					date = DateUtil.getDiffDay(date, +1);
				}
			}
		} else
			rowCount = 0;

		gradientArea = new GradientArea(
				GradientArea.TOP_BOTTOM, Color.WHITE, GRADIENT_COLOR);
		gradientArea.setBorder(false);
		gradientArea.setOpaque(true);
		panel.add(gradientArea);		
		
	}
	
	public JComponent getComponent()
	{
		return panel;
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
			//rootPanel.setCursor(new Cursor(Cursor.HAND_CURSOR));
		}

		public void mouseExited(MouseEvent e) {
			//rootPanel.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
		}
	}
	
	private class Layout implements LayoutManager {
		public void addLayoutComponent(String name, Component comp) {
		}

		public void removeLayoutComponent(Component comp) {
		}

		public Dimension preferredLayoutSize(Container parent) {
			int height = refLabel.getPreferredSize().height;
			height = rowCount * height;
			return new Dimension(width, height);
		}

		public Dimension minimumLayoutSize(Container parent) {
			return new Dimension(50, 100);
		}

		public void layoutContainer(Container parent) {
			try {
				double totWidth = parent.getWidth();
				double dateColWidth = totWidth / dateHeaders.size();
				double calColWidth = totWidth / calHeaders.size();
				double rowHeight = parent.getHeight() / rowCount;
				double dateYPos = 0;
				if (calHeaders.size() > 0)
					dateYPos = rowHeight;
				int dateI = 0;
				for (int i=0; i < model.getSelectedCalendars().size(); i++) {
					if (calHeaders.size() > 0) {
						JLabel label = (JLabel) calHeaders.get(i);
						label.setBounds((int) (i*calColWidth), 
								0,
								(int) calColWidth,
								(int) rowHeight);
					}
					for (int j=0; j < dayCount; j++) {
						JLabel dateLabel = (JLabel) dateHeaders.get(dateI);
						dateLabel.setBounds((int) (dateI*dateColWidth),
								(int) dateYPos,
								(int) dateColWidth, 
								(int) rowHeight);
						Rectangle r = dateLabel.getBounds();
						dateI++;
					}
				}
				resizeDates((int) dateColWidth);
				gradientArea.setBounds(0, 0, parent.getWidth(), parent.getHeight());
			} catch (Exception e) {
				throw BizcalException.create(e);
			}
		}
	}
	
	public void setWidth(int i)
	{
		width = i;
	}

	private void resizeDates(int width)
		throws Exception
	{
		if (dayCount != 5 && dayCount != 7)
			return;
		FontMetrics metrics = refLabel.getFontMetrics(refLabel.getFont());
		int charCount = 10;
		if (maxWidth(charCount, metrics) > width) {
			charCount = 5;
			if (maxWidth(charCount, metrics) > width) {
				charCount = 3;
				if (maxWidth(charCount, metrics) > width) {
					charCount = 1;
				}
			}
		}
		DateFormat format = new SimpleDateFormat("EEEEE");
		for (int i=0; i < dateHeaders.size(); i++) {
			JLabel label = (JLabel) dateHeaders.get(i);
			Date date = (Date) dateList.get(i);
			String str = format.format(date);
			if (str.length() > charCount)
				str = str.substring(0, charCount);
			str = TextUtil.formatCase(str);
			label.setText(str);
		}
	}
	
	private int maxWidth(int charCount, FontMetrics metrics)
		throws Exception
	{
		DateFormat format = new SimpleDateFormat("EEEEE", LocaleBroker.getLocale());
		Calendar cal = Calendar.getInstance(LocaleBroker.getLocale());
		cal.set(Calendar.DAY_OF_WEEK, 1);
		int maxWidth = 0;
		for (int i=0; i < 7; i++) {
			String str = format.format(cal.getTime());
			if (str.length() > charCount)
				str = str.substring(0, charCount);
			int width = metrics.stringWidth(str);
			if (width > maxWidth)
				maxWidth = width;
			cal.add(Calendar.DAY_OF_WEEK, +1);
		}
		return maxWidth;
	}
}
