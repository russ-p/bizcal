package bizcal.swing.util;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.Point;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.PrintWriter;
import java.io.StringWriter;

import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;

import bizcal.common.Bundle;

/**
 * Shows a error message dialog.
 *
 * @author Fredrik Bertilsson
 */
public class ErrorHandler
{
    public static void handleError(Throwable e)
    {
        handleError(e, null);
    }

    public static void handleError(Throwable e, Component comp)
    {

        try {
        	e.printStackTrace();
        showError(comp,
                  Bundle.translate("Technical error"),
                  Bundle.translate("Technical error"),
                  e);
        } catch (Exception exc) {
            showError(comp,
                    "Error message",
                    "Error message",
                    exc);
        }
    }

    public static void showError(Component comp, String title, String msg,
                                 Throwable t)
    {
        if (t != null) {
            Window win = null;
            if (comp != null)
                win = comp instanceof Window ? (Window) comp :
                    SwingUtilities.getWindowAncestor(comp);
                DetailsDialog dd = null;
            if (win instanceof Frame)
                dd = new DetailsDialog( (Frame) win, title,
                                       JOptionPane.ERROR_MESSAGE);
            else if (win instanceof Dialog)
                dd = new DetailsDialog( (Dialog) win, title,
                                       JOptionPane.ERROR_MESSAGE);
            else {
                dd = new DetailsDialog(title, JOptionPane.ERROR_MESSAGE);
            }
            if (msg == null || msg.length() == 0)
                dd.setMsgText(t.getMessage());
            else
                dd.setMsgText(msg);
            dd.setDetailsText(getStackTrace(t));
            dd.setLocationRelativeTo(comp);
            dd.setVisible(true);
        }
        else {
            showError(comp, title, msg);
        }
    }

    public static void showError(Component comp, String title, String msg)
    {
        JOptionPane.showMessageDialog(comp, msg, title,
                                      JOptionPane.ERROR_MESSAGE);
    }

    private static String getStackTrace(Throwable t)
    {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        t.printStackTrace(pw);
        return sw.toString();
    }

    private static class DetailsDialog
        extends JDialog
    {
        private static final long serialVersionUID = 1L;
        
        private JLabel msg;
        private JTextArea details;
        public DetailsDialog(String title,
                             int icon_type) {
            super( (JFrame)null, title, false);
            setup(icon_type);
        }

        public DetailsDialog(Frame owner,
                             String title,
                             int icon_type)
        {
            super(owner, title, true);
            setup(icon_type);
        }

        public DetailsDialog(Dialog owner,
                             String title,
                             int icon_type)
        {
            super(owner, title, true);
            setup(icon_type);
        }

        private void setup(int icon_type)
        {
            msg = new JLabel();
            msg.setIcon(getIconForType(icon_type));
            details = new JTextArea();
            details.setLineWrap(false);
            details.setWrapStyleWord(false);
            final JScrollPane dpane = new JScrollPane(details);
            dpane.setVisible(false);            
            final JButton ok = new JButton(translate("ok"));
            ok.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    dispose();
                }
            });
            final String showDetails = translate("showDetails");
            final String hideDetails = translate("hideDetails");
            final JButton dbutton = new JButton(showDetails);
            dbutton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    if (dpane.isVisible()) {
                        dpane.setVisible(false);
                        dbutton.setText(showDetails);
                        doSizePack();
                    }
                    else {
                        dpane.setVisible(true);
                        dbutton.setText(hideDetails);
                        doSizePack();
                    }
                }
            });
            JPanel bpan = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));
            bpan.add(dbutton);
            bpan.add(ok);
            JPanel cont = new JPanel(new BorderLayout(5, 5));
            cont.add(msg, BorderLayout.NORTH);
            cont.add(dpane, BorderLayout.CENTER);
            cont.add(bpan, BorderLayout.SOUTH);
            cont.setBorder(new EmptyBorder(5, 5, 5, 5));
            setContentPane(cont);
            doSizePack();
        }
                

        public void setMsgText(String txt)
        {
            msg.setText(txt);
            doSizePack();
        }

        public void setDetailsText(String txt)
        {
            details.setText(txt);
            doSizePack();
        }

        private void doSizePack()
        {
            pack();
            Dimension screenSize = getToolkit().getScreenSize();
            Dimension size = getSize();
            Point parentLoc = getParent().getLocation();
            setLocation(parentLoc.x + 25, parentLoc.y + 25);
            Point location = getLocation();
            int width = -1;
            int height = -1;
            if (size.height + location.y > screenSize.height)
                height = screenSize.height - location.y - 30;
            if (size.width + location.x > screenSize.width)
                width = screenSize.width - location.x;
            if (size.width < 300)
                width = 300;
            if (size.width > 640)
                width = 750;
            if (size.height > 480)
                height = 550;
            if (width != -1 || height != -1) {
                setSize(new Dimension(width == -1 ? size.width : width,
                                      height == -1 ? size.height : height));
                doLayout();
            }
        }
    }

    private static Icon getIconForType(int messageType)
    {
        if (messageType < 0 || messageType > 3)
            return null;
        switch (messageType) {
            case 0:
                return UIManager.getIcon("OptionPane.errorIcon");
            case 1:
                return UIManager.getIcon("OptionPane.informationIcon");
            case 2:
                return UIManager.getIcon("OptionPane.warningIcon");
            case 3:
                return UIManager.getIcon("OptionPane.questionIcon");
        }
        return null;
    }
    
    private static String translate(String key)
    {
        try {
            return Bundle.translate(key);
        } catch (Exception e) {
        	return key;
        }
    }
}
