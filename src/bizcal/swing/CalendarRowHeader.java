package bizcal.swing;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.LayoutManager;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;

import bizcal.common.Calendar;
import bizcal.common.CalendarModel;
import bizcal.swing.util.GradientArea;
import bizcal.util.BizcalException;

public class CalendarRowHeader
{
	private JLayeredPane panel;
	private List calLabels = new ArrayList();
	private List calLines = new ArrayList();
	private GradientArea gradientArea;
	private int width = 100;
	private Color lineColor = Color.LIGHT_GRAY;
	private int footerHeight = 0;
	private CalendarModel model;
	
	public CalendarRowHeader(CalendarModel model) 
	throws Exception 
	{
		this.model = model;
		panel = new JLayeredPane();
		panel.setLayout(new Layout());
	}
	
	public void refresh()
		throws Exception
	{
		panel.removeAll();
		calLabels.clear();
		calLines.clear();
		
		Iterator i = model.getSelectedCalendars().iterator();
 		while (i.hasNext()) {
 			Calendar cal = (Calendar) i.next();  			
 			JLabel label = new JLabel(cal.getSummary());
			label.setVerticalTextPosition(JLabel.CENTER);
			panel.add(label, new Integer(2));
			calLabels.add(label);
			JLabel line = new JLabel();
			line.setBackground(lineColor);
			line.setOpaque(true);
			calLines.add(line);
			panel.add(line, new Integer(2));
		}		
        gradientArea = new GradientArea(GradientArea.LEFT_RIGHT, Color.WHITE,
        		ColumnHeaderPanel.GRADIENT_COLOR);
        gradientArea.setOpaque(true);
		gradientArea.setBorder(false);
		panel.add(gradientArea, new Integer(1));		
	}
	
	private class Layout implements LayoutManager {
		public void addLayoutComponent(String name, Component comp) {
		}

		public void removeLayoutComponent(Component comp) {
		}

		public Dimension preferredLayoutSize(Container parent) {
			try {
				return new Dimension(width, GroupView.PREFERRED_ROW_HEIGHT * model.getSelectedCalendars().size() + footerHeight);
			} catch (Exception e) {
				throw BizcalException.create(e);
			}
		}

		public Dimension minimumLayoutSize(Container parent) {
			return new Dimension(50, 50);
		}

		public void layoutContainer(Container parent) {
			try {
				double rowHeight = GroupView.PREFERRED_ROW_HEIGHT; 
				for (int i=0; i < calLabels.size(); i++) {
					JLabel calLabel = (JLabel) calLabels.get(i);
					calLabel.setBounds(5, 
							(int) (i*rowHeight),
							width-5,
							(int) rowHeight);
					JLabel calLine = (JLabel) calLines.get(i);
					calLine.setBounds(0, 
							(int) ((i+1)*rowHeight),
							width,
							1);
				}
				gradientArea.setBounds(0, 0, parent.getWidth(), parent.getHeight());
			} catch (Exception e) {
				throw BizcalException.create(e);
			}
		}
	}
	
	public JComponent getComponent()
	{
		return panel;
	}

	public void setFooterHeight(int footerHeight) {
		this.footerHeight = footerHeight;
	}
}
