package bizcal.swing;

import java.awt.Color;
import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Vector;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import bizcal.common.CalendarViewConfig;
import bizcal.common.Event;
import bizcal.util.DateUtil;
import bizcal.util.LocaleBroker;

/**
 * @author Fredrik Bertilsson
 */
public class TabularView extends CalendarView {
	private JPanel panel;

	private int dayCount = 14;
	private JTable table;
	private JScrollPane scroll;

	public TabularView(CalendarViewConfig desc) throws Exception {
		super(desc);
		table = new JTable();
		scroll = new JScrollPane(table);
	}
	
	public JComponent getComponent()
	{
		return scroll;
	}


	public long getTimeInterval() throws Exception {
		return 0;
	}
	

	public void refresh0() throws Exception {
		DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.SHORT, LocaleBroker.getLocale());
		DateFormat timeFormat = DateFormat.getTimeInstance(DateFormat.SHORT, LocaleBroker.getLocale());
		DefaultTableModel model = new DefaultTableModel();
		model.addColumn(tr("Date"));
		Map eventMap = new HashMap();
		Iterator i = getSelectedCalendars().iterator();
		while (i.hasNext()) {
			bizcal.common.Calendar cal = 
				(bizcal.common.Calendar) i.next();
			model.addColumn(cal.getSummary());
			eventMap.put(cal.getId(), createEventsPerDay(cal.getId()));
		}
		Date date = getInterval().getStartDate();
		while (date.before(getInterval().getEndDate())) {
			Vector row = new Vector();
			row.add(dateFormat.format(date));
			i = getSelectedCalendars().iterator();
			while (i.hasNext()) {
				bizcal.common.Calendar cal = 
					(bizcal.common.Calendar) i.next();
				Map eventsPerDay = (Map) eventMap.get(cal.getId());
				List events = (List) eventsPerDay.get(date);
				StringBuffer str = new StringBuffer();
				if (events != null) {
					Iterator j = events.iterator();
					while (j.hasNext()) {
						Event event = (Event) j.next();
						str.append(timeFormat.format(event.getStart()) + "-");
						str.append(timeFormat.format(event.getEnd()));
						if (j.hasNext())
							str.append(", ");					
					}
				}				
				row.add(str);
			}
			model.addRow(row);
			System.err.println("TabularView: " + row);
			date = DateUtil.getDiffDay(date, +1);
		}
		table.setModel(model);
		model.fireTableDataChanged();
	}

	public Date getDate(int x, int y) {
		return null;
	}

	private String tr(String str)
	{
		return str;
	}
	
}
