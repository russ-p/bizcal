package bizcal.swing;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import bizcal.common.Bundle;
import bizcal.swing.util.ErrorHandler;
import bizcal.swing.util.TableLayoutPanel;
import bizcal.swing.util.TableLayoutPanel.Row;
import bizcal.util.DateUtil;
import bizcal.util.LocaleBroker;
import bizcal.util.StreamCopier;
import bizcal.util.TextUtil;

public class WeekStepper
{
	private TableLayoutPanel panel;
	private JLabel label;
	private Calendar cal;
	private List listeners = new ArrayList();
	private Font font = new Font("Verdana", Font.BOLD, 14);
	//private Color textColor = new Color(0, 100, 150);
	private Color textColor = Color.BLACK;
	private String fastRewindArrow = "/bizcal/res/go_fb.gif";
	private String prevArrow = "/bizcal/res/go_back.gif";
	private String nextArrow = "/bizcal/res/go_forward.gif";
	private String fastForwardArrow = "/bizcal/res/go_ff.gif";
	
	public WeekStepper()
		throws Exception
	{
		cal = Calendar.getInstance(LocaleBroker.getLocale());
		cal.setTime(DateUtil.round2Week(new Date()));
		panel = new TableLayoutPanel();
		//panel.setBackground(Color.WHITE);
		panel.createColumn();
		panel.createColumn();
		panel.createColumn(TableLayoutPanel.FILL);
		panel.createColumn();
		panel.createColumn();
		Row row = panel.createRow();
		ActionListener listener;
		listener = new ActionListener() {
			public void actionPerformed(ActionEvent event) {	
				previousMonth();
			}
		};
		row.createCell(createButton(fastRewindArrow, listener));
		listener = new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				previous();
			}
		};
		row.createCell(createButton(prevArrow, listener));
		label = new JLabel(getText());
		label.setHorizontalAlignment(JLabel.CENTER);
		label.setFont(font);
		label.setForeground(textColor);
		row.createCell(label, TableLayoutPanel.CENTER, TableLayoutPanel.CENTER);
		listener = new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				next();
			}
		};
		row.createCell(createButton(nextArrow, listener));
		listener = new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				nextMonth();
			}
		};
		row.createCell(createButton(fastForwardArrow, listener));
	}
	
	private String getText()
		throws Exception
	{
		StringBuffer str = new StringBuffer();
		str.append(Bundle.translate("Week") + " " + cal.get(Calendar.WEEK_OF_YEAR) + ": ");
		Calendar cal2 = Calendar.getInstance(LocaleBroker.getLocale());
		int currYear = cal2.get(Calendar.YEAR);
		int year = cal.get(Calendar.YEAR);
		if (currYear != year)
			str.append(year + " ");
		int month = cal.get(Calendar.MONTH);
		DateFormat monthFormat = new SimpleDateFormat("MMM");
		str.append(TextUtil.formatCase(monthFormat.format(cal.getTime())) + " ");
		int day = cal.get(Calendar.DAY_OF_MONTH);
		str.append(day);
		str.append(" - ");
		cal2.setTime(cal.getTime());
		cal2.add(Calendar.DAY_OF_WEEK, +6);
		int month2 = cal2.get(Calendar.MONTH);
		if (month != month2)
			str.append(TextUtil.formatCase(monthFormat.format(cal2.getTime())) + " ");			
		day = cal2.get(Calendar.DAY_OF_MONTH);
		str.append(day);
		return str.toString();
	}
	
	public JComponent getComponent()
	{
		return panel;
	}
	
	public Date getDate()
	{
		return cal.getTime();
	}
	
	private void next()
	{
		cal.add(Calendar.WEEK_OF_YEAR, +1);
		fireStateChanged();
	}

	private void previous()
	{
		cal.add(Calendar.WEEK_OF_YEAR, -1);
		fireStateChanged();
	}

	private void nextMonth()
	{
		System.err.println("WeekStepper.nextMonth1: " + cal.getTime());
		cal.add(Calendar.MONTH, +1);
		cal.set(Calendar.DAY_OF_WEEK, cal.getFirstDayOfWeek());
		System.err.println("WeekStepper.nextMonth2: " + cal.getTime());
		fireStateChanged();
	}

	private void previousMonth()
	{
		cal.add(Calendar.MONTH, -1);
		cal.set(Calendar.DAY_OF_WEEK, cal.getFirstDayOfWeek());
		fireStateChanged();
	}
	
	public void addChangeListener(ChangeListener listener)
	{
		listeners.add(listener);
	}
	
	private void fireStateChanged()
	{
		try {
			label.setText(getText());
			ChangeEvent event = new ChangeEvent(this);
			Iterator i = listeners.iterator();
			while (i.hasNext()) {
				ChangeListener l = (ChangeListener) i.next();
				l.stateChanged(event);
			}
		} catch (Exception e) {
			ErrorHandler.handleError(e);
		}
	}

	private Icon getIcon(String filename)
		throws Exception
	{
		byte[] bytes = StreamCopier.copyToByteArray(getClass().getResourceAsStream(filename));
        return new ImageIcon(bytes);        
	}
	
	private JComponent createButton(String filename, ActionListener listener)
		throws Exception
	{
		JButton button = new JButton(getIcon(filename));
		button.addActionListener(listener);
		return button;
	}
	
	public void setDate(Date date)
		throws Exception
	{
		cal.setTime(DateUtil.round2Week(date));
		fireStateChanged();
	}
	
	public void setFont(Font font)
	{
		this.font = font;
	}
}
