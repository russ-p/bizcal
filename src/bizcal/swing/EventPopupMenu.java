package bizcal.swing;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collections;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import bizcal.common.Bundle;
import bizcal.common.Event;
import bizcal.swing.util.ErrorHandler;

/**
 * @author wmfrbre
 */
public class EventPopupMenu
	extends JPopupMenu
{
	private static final long serialVersionUID = 1L;

	private Object _calId;
	private Event _event;
	
	public EventPopupMenu(Object calId, Event event, CalendarListener calListener)
		throws Exception
	{
		
		//ANVÄNDS EJ?????????
		_calId = calId;
		_event = event;
		ActionListener listener = new ThisActionListener(calListener);
		JMenuItem item = new JMenuItem(Bundle.translate("Copy"));
		item.setActionCommand("copy");
		item.addActionListener(listener);
		add(item);
		addSeparator();
		item = new JMenuItem(Bundle.translate("Delete"));
		item.setActionCommand("delete");
		item.addActionListener(listener);
		add(item);
		addSeparator();
		item = new JMenuItem(Bundle.translate("Properties"));
		item.setActionCommand("properties");
		item.addActionListener(listener);
		add(item);
	}
	
	private class ThisActionListener
		implements ActionListener
	{
		private CalendarListener _listener;
		
		public ThisActionListener(CalendarListener listener)
		{
			_listener = listener;
		}
		
		public void actionPerformed(ActionEvent event)
		{
			try {
				if ("copy".equals(event.getActionCommand())) {
					_listener.copy(Collections.nCopies(1, _event));
					return;
				}
				if ("delete".equals(event.getActionCommand())) {
					_listener.deleteEvent(_event);
					return;
				}
				if ("properties".equals(event.getActionCommand())) {
					_listener.showEvent(_calId, _event);
				}
			} catch (Exception e) {
				ErrorHandler.handleError(e);
			}
				
		}
	}
	
}
