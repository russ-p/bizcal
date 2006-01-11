package bizcal.swing;

import java.awt.Color;
import java.text.DateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import bizcal.common.CalendarViewConfig;
import bizcal.common.Event;
import bizcal.swing.util.TableLayoutPanel;
import bizcal.swing.util.TableLayoutPanel.Row;
import bizcal.util.DateInterval;

/**
 * @author Fredrik Bertilsson
 */
public class TabularView extends CalendarView {
	private TableLayoutPanel panel;

	private int dayCount = 14;

	public TabularView(CalendarViewConfig desc) throws Exception {
		super(desc);
	}

	protected JComponent createCalendarPanel() throws Exception {
		panel = new TableLayoutPanel();
		panel.setBackground(Color.WHITE);
		return panel;
	}

	public long getTimeInterval() throws Exception {
		return 0;
	}

	public void refresh0() throws Exception {
		panel.deleteRows();
		panel.deleteColumns();
		panel.clear();
		panel.removeAll();
		panel.doLayout();
		panel.revalidate();
		Iterator i = getSelectedCalendars().iterator();
		while (i.hasNext()) {
			bizcal.common.Calendar cal = (bizcal.common.Calendar) i.next();
			panel.createColumn(TableLayoutPanel.FILL);
		}
		Row header = panel.createRow();
		Row row = panel.createRow(TableLayoutPanel.FILL);
		i = getSelectedCalendars().iterator();
		while (i.hasNext()) {
			bizcal.common.Calendar cal = (bizcal.common.Calendar) i.next();
			header.createCell(new JLabel(cal.getSummary()));
			CalendarTable calTab = new CalendarTable();
			calTab.refresh(broker.getEvents(cal.getId()));
			JScrollPane scrollPane = new JScrollPane(calTab);
			scrollPane.setBackground(Color.WHITE);
			row.createCell(scrollPane);
		}
		panel.revalidate();
	}

	public Date getDate(int x, int y) {
		return null;
	}

	private class CalendarTable extends JTable {
		private static final long serialVersionUID = 1L;

		public CalendarTable() {
			DefaultTableModel model = new DefaultTableModel();
			model.addColumn("Start");
			model.addColumn("Slut");
			model.addColumn("Beskrivning");
			setModel(model);
			setBackground(Color.WHITE);
		}

		public void refresh(List events) throws Exception {
			DateFormat format = DateFormat.getDateTimeInstance(
					DateFormat.SHORT, DateFormat.SHORT, Locale.getDefault());
			DefaultTableModel model = (DefaultTableModel) getModel();
			if (events == null)
				return;
			Iterator i = events.iterator();
			while (i.hasNext()) {
				Event event = (Event) i.next();
				Object row[] = new Object[3];
				row[0] = format.format(event.getStart());
				row[1] = format.format(event.getEnd());
				row[2] = event.getSummary();
				model.addRow(row);
			}
		}
	}

}
