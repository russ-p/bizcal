package bizcal.swing.util;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;

import javax.swing.JComponent;

/**
 * @author wmjnkal
 * Lasso area used when selecting by dragging the mouse
*/
public class LassoArea 
extends JComponent
{
	private static final long serialVersionUID = 1L;
	private Color lineColor;
	private boolean dashed;
	private boolean roundedRectangle;
	
	
	public LassoArea()
	throws Exception
	{
		this.lineColor = Color.BLACK;
		this.dashed = true;
		this.roundedRectangle = false;		
	}
	
	//The toolkit will invoke this method when it's time to paint
	public void paint(Graphics g)
	{
		Graphics2D g2 = (Graphics2D) g;
		//makes the graphics smoother
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		
		int width = (int) getWidth();
		int height = (int) getHeight();
		BufferedImage buffImg = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D gbi = buffImg.createGraphics();
        gbi.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        Stroke stroke;
        float dash1[] = {5f};
        if(this.dashed)
        	stroke = new BasicStroke(1.0f, BasicStroke.CAP_BUTT, 
					BasicStroke.JOIN_BEVEL, 
						10.0f, dash1, 0.0f);
        else
        	stroke = new BasicStroke(1.0f);
        gbi.setStroke(stroke);
		        
		if(this.roundedRectangle)
		{
			AlphaComposite ac = AlphaComposite.getInstance(AlphaComposite.DST_OVER, 1.0f);
			gbi.setComposite(ac);
			gbi.setPaint(this.lineColor);
			gbi.draw(new RoundRectangle2D.Double(1, 1, width-2, height-2, 17,17));
		}
		else
		{
			AlphaComposite ac = AlphaComposite.getInstance(AlphaComposite.DST_OVER, 1.0f);
			gbi.setComposite(ac);
			gbi.setPaint(this.lineColor);
			gbi.draw(new Rectangle2D.Double(1, 1, width-2, height-2));
		}
			
		g2.drawImage(buffImg, null, 0, 0);
	
		super.paint(g2);
		super.paint(gbi);
	}
	
	public void setRoundedRectangle(boolean rounded)
	{
		this.roundedRectangle = rounded;
	}
	
	public boolean isRoundedRectangle()
	{
		return this.roundedRectangle;
	}
	
	public void setDashed(boolean rounded)
	{
		this.dashed = rounded;
	}
	
	public boolean isDashed()
	{
		return this.dashed;
	}
	
	public void setLineColor(Color aColor)
	{
		this.lineColor = aColor;
	}
	
	public Color getLineColor()
	{
		return this.lineColor;
	}	
}
