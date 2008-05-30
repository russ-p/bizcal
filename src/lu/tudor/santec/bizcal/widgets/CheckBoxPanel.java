/*******************************************************************************
 * Copyright (c) 2007 by CRP Henri TUDOR - SANTEC LUXEMBOURG 
 * check http://www.santec.tudor.lu for more information
 *  
 * Contributor(s):
 * Johannes Hermen  johannes.hermen(at)tudor.lu                            
 * Martin Heinemann martin.heinemann(at)tudor.lu  
 *  
 * This library is free software; you can redistribute it and/or modify it  
 * under the terms of the GNU Lesser General Public License (version 2.1)
 * as published by the Free Software Foundation.
 * 
 * This software is distributed in the hope that it will be useful, but     
 * WITHOUT ANY WARRANTY; without even the implied warranty of               
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU        
 * Lesser General Public License for more details.                          
 * 
 * You should have received a copy of the GNU Lesser General Public         
 * License along with this library; if not, write to the Free Software      
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA 
 *******************************************************************************/
package lu.tudor.santec.bizcal.widgets;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Iterator;
import java.util.Vector;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

public class CheckBoxPanel extends JPanel implements MouseListener {

	private static final long serialVersionUID = 1L;
	private ColoredCheckBox cb;
	private JLabel label;
	private boolean drawBackground = true;
	private Color bgColor;
	private boolean isSelected;
	private Vector<ActionListener> listeners = new Vector<ActionListener>();
	private boolean unselectable;

	public CheckBoxPanel(String text, Color c) {
		this(text, c, false, true);
	}

	public CheckBoxPanel(String text, Color c, boolean drawBG, boolean unselectable) {
		this.drawBackground = drawBG;
		this.unselectable = unselectable;
		this.bgColor = new Color(c.getRed(),c.getGreen(),c.getBlue(),50);
		this.setOpaque(false);
		this.setLayout(new BorderLayout());
		this.cb = new ColoredCheckBox("",c);
		this.add(cb, BorderLayout.WEST);
		
		cb.requestFocus(false);
		
		this.label = new  JLabel(text);
		this.add(label);
		this.addMouseListener(this);

	}


	@Override
	protected void paintComponent(Graphics g) {
		if (isSelected()) {
			g.setColor(new Color(200,200,200));
			g.fillRect(0,0, getWidth(), getHeight());
		}
		if (drawBackground) {
			g.setColor(bgColor);
			g.fillRoundRect(1, 1, getWidth()-2, getHeight()-2, 8,8);
		}
		super.paintComponent(g);
	}

	public void setActiv(boolean b) {
		this.cb.setSelected(b);
		this.cb.repaint();
	}

	public synchronized void addActionListener(ActionListener listener) {
		this.cb.addActionListener(listener);
		this.listeners .add(listener);
	}

	public synchronized void removeActionListener(ActionListener listener) {
		this.cb.removeActionListener(listener);
		this.listeners .remove(listener);
	}

	public boolean isActiv() {
		return cb.isSelected();
	}

	public void mouseClicked(MouseEvent e) {
		if (this.isSelected && unselectable)
			this.setSelected(! this.isSelected());
		else if (! this.isSelected)
			this.setSelected(! this.isSelected());
	}

	public void mouseEntered(MouseEvent e) {}
	public void mouseExited(MouseEvent e) {}
	public void mousePressed(MouseEvent e) {}
	public void mouseReleased(MouseEvent e) {}

	/**
	 * @return the isSelected
	 */
	public boolean isSelected() {
		return isSelected;
	}

	/**
	 * @param isSelected the isSelected to set
	 */
	public void setSelected(boolean isSelected) {
		/* ================================================== */
		this.isSelected = isSelected;
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				repaint();
			}
		});
		asynchInformActionListeners();
		/* ================================================== */
	}

	public void setColor(Color c) {
		this.bgColor = new Color(c.getRed(),c.getGreen(),c.getBlue(),50);
		this.cb.setBackground(c);
		cb.setColor(c);
	}

	/**
	 * Set the text of the button
	 *
	 * @param text
	 */
	public void setText(String text) {
		/* ================================================== */
		this.label.setText(text);
		this.label.validate();

		this.label.updateUI();
		/* ================================================== */
	}
	
	
	/**
	 * Inform action listeners asynchronous to speedup the gui
	 */
	private synchronized void asynchInformActionListeners() {
		/* ================================================== */
		Thread t = new Thread() {
			public void run() {
				for (Iterator iter = listeners.iterator(); iter.hasNext();) {
					ActionListener element = (ActionListener) iter.next();
					element.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "selection changed"));
				}
			}
		};
		t.start();
		/* ================================================== */
	}
	
	
}
