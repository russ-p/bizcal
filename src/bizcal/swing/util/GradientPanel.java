package bizcal.swing.util;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.LayoutManager;

import javax.swing.JPanel;

public class GradientPanel
	extends JPanel
{
	private JPanel panel;
	private GradientArea area;
	
	public GradientPanel(GradientArea area, JPanel panel)
	{
		setLayout(new Layout());
		panel.setOpaque(false);
		add(panel);
		add(area);
		this.panel = panel;
		this.area = area;
	}
	
	public Dimension getPreferredSize()
	{
		return panel.getPreferredSize();
	}
	
	public void setPreferredSize(Dimension dim)
	{
		panel.setPreferredSize(dim);
	}
	
	public void revalidate()
	{
		if (panel != null)
			panel.revalidate();
	}
	
	/*public void setBounds(int x, int y, int width, int height)
	{
		panel.setBounds(0, 0, width, height);
		area.setBounds(0, 0, width, height);
	}*/
	
	
	private class Layout implements LayoutManager {
		public void addLayoutComponent(String name, Component comp) {
		}

		public void removeLayoutComponent(Component comp) {
		}

		public Dimension preferredLayoutSize(Container parent) {
			return parent.getPreferredSize();
		}

		public Dimension minimumLayoutSize(Container parent) {
			return parent.getMinimumSize();
		}

		public void layoutContainer(Container parent) {
			area.setBounds(0, 0, parent.getWidth(), parent.getHeight());
			panel.setBounds(0, 0, parent.getWidth(), parent.getHeight());
		}
	}
	
	

}
