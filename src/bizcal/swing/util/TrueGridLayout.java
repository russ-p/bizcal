package bizcal.swing.util;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.LayoutManager;

public class TrueGridLayout
	implements LayoutManager
{
	private double rows;
	private double cols;
	
	public TrueGridLayout(int rows, int cols)
	{
		this.rows = rows;
		this.cols = cols;
	}
	
	public void addLayoutComponent(String name, Component comp) {
	}

	public void removeLayoutComponent(Component comp) {
	}

	public Dimension preferredLayoutSize(Container parent) {
		int row = 0;
		int col = 0;
		int maxWidth = 0;
		int maxHeight = 0;
		for (int i=0; i < parent.getComponents().length; i++) {
			Component child = parent.getComponents()[i];
			Dimension dim = child.getPreferredSize();
			if (dim.height > maxHeight)
				maxHeight = dim.height;
			if (dim.width > maxWidth)
				maxWidth = dim.width;
			col++;
			if (col == cols) {
				col = 0;
				row++;
			}			
		}	
		return new Dimension(maxWidth*(int) cols, maxHeight*(int) rows);
	}

	public Dimension minimumLayoutSize(Container parent) {
		return new Dimension(50, 100);
	}

	public void layoutContainer(Container parent) 
	{
		double row = 0;
		double col = 0;
		double width = parent.getWidth() / cols;
		double height = parent.getHeight() / rows;
		for (int i=0; i < parent.getComponents().length; i++) {
			Component child = parent.getComponents()[i]; 
			double x1 = col * width;
			double y1 = row * height;
			child.setBounds((int) x1, (int) y1, (int) width, (int) height);
			col++;
			if (col == cols) {
				col = 0;
				row++;
			}			
		}
	}

}
