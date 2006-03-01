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
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

import bizcal.common.CalendarViewConfig;
import bizcal.swing.util.GradientArea;
import bizcal.util.BizcalException;
import bizcal.util.TimeOfDay;

public class TimeLabelPanel 
{
	private JPanel panel;
	private List hourLabels = new ArrayList();
	private List minuteLabels = new ArrayList();
	private List hourLines = new ArrayList();
	private List minuteLines = new ArrayList();
	private Font font = new Font("Verdana", Font.PLAIN, 11);
	private GradientArea gradientArea;
	private int height;
	private int width = 40;
	private int hourCount;
	private int footerHeight = 0;
	private CalendarViewConfig config;
	
	public TimeLabelPanel(CalendarViewConfig config, TimeOfDay start, TimeOfDay end) throws Exception 
	{
		this.config = config;
		hourCount = end.getHour() - start.getHour();
		if (hourCount == 0)
			hourCount = 24;
		panel = new JPanel();
		panel.setLayout(new Layout());
		DateFormat hourFormat = new SimpleDateFormat("HH");
		hourFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
		Font hourFont = font.deriveFont((float) 12);
		hourFont = hourFont.deriveFont(Font.BOLD);

		long pos = start.getValue();
 		while (pos < end.getValue()) {
			Date date = new Date(pos);
			String timeTxt = hourFormat.format(date);
			JLabel timeLabel = new JLabel(timeTxt);
			timeLabel.setVerticalTextPosition(JLabel.CENTER);
			timeLabel.setFont(hourFont);
			panel.add(timeLabel);
			hourLabels.add(timeLabel);
			JLabel line = new JLabel();
			line.setBackground(config.getLineColor());
			line.setOpaque(true);
			hourLines.add(line);
			panel.add(line);

			timeTxt = "00";
			timeLabel = new JLabel(timeTxt);
			timeLabel.setFont(font);
			panel.add(timeLabel);
			minuteLabels.add(timeLabel);
			line = new JLabel();
			line.setBackground(config.getLineColor());
			line.setOpaque(true);
			minuteLines.add(line);
			panel.add(line);			

			timeTxt = "30";
			timeLabel = new JLabel(timeTxt);
			timeLabel.setFont(font);
			panel.add(timeLabel);
			minuteLabels.add(timeLabel);
			line = new JLabel();
			line.setBackground(config.getLineColor());
			line.setOpaque(true);
			minuteLines.add(line);
			panel.add(line);

			pos += 3600 * 1000;
		}		
        gradientArea = new GradientArea(GradientArea.LEFT_RIGHT, Color.WHITE,
        		ColumnHeaderPanel.GRADIENT_COLOR);
        gradientArea.setOpaque(true);
		gradientArea.setBorder(false);
		panel.add(gradientArea);
	}
	
	private class Layout implements LayoutManager {
		public void addLayoutComponent(String name, Component comp) {
		}

		public void removeLayoutComponent(Component comp) {
		}

		public Dimension preferredLayoutSize(Container parent) {
			return new Dimension(width, DayView.PIXELS_PER_HOUR * hourCount + footerHeight);
		}

		public Dimension minimumLayoutSize(Container parent) {
			return new Dimension(50, 50);
		}

		public void layoutContainer(Container parent) {
			try {
				double totHeight = parent.getHeight() - footerHeight;
				double rowHeight = totHeight / hourCount; 
				double minuteRowHeight = rowHeight / 2;
				int colWidth = width / 2;
				int iMinute = 0;
				for (int i=0; i < hourLabels.size(); i++) {
					JLabel hourLabel = (JLabel) hourLabels.get(i);
					hourLabel.setBounds(0, 
							(int) (i*rowHeight),
							colWidth,
							(int) rowHeight);
					JLabel hourLine = (JLabel) hourLines.get(i);
					hourLine.setBounds(0, 
							(int) ((i+1)*rowHeight),
							width,
							1);

					JLabel minuteLabel = (JLabel) minuteLabels.get(iMinute);
					minuteLabel.setBounds(colWidth,
							(int) (i*rowHeight),
							colWidth,
							(int) (minuteRowHeight));
					JLabel minuteLine = (JLabel) minuteLines.get(iMinute);
					minuteLine.setBounds(colWidth, 
							(int) (i*rowHeight + minuteRowHeight),
							colWidth,
							1);
					iMinute++;

					minuteLabel = (JLabel) minuteLabels.get(iMinute);					
					minuteLabel.setBounds(colWidth,
							(int) (i*rowHeight + minuteRowHeight),
							colWidth,
							(int) minuteRowHeight);
					iMinute++;
					 
				}
				gradientArea.setBounds(0, 0, parent.getWidth(), parent.getHeight());
			} catch (Exception e) {
				throw BizcalException.create(e);
			}
		}
	}
	
	public void setHeight(int x)
	{
		height = x;
	}
	
	public JComponent getComponent()
	{
		return panel;
	}

	public void setFooterHeight(int footerHeight) {
		this.footerHeight = footerHeight;
	}
}
