package org.ag.addressbook;
import java.awt.Color;
import java.awt.Dimension;

import javax.swing.JPanel;

/**
 * AB Separator
 * This is just a JPanel with a specified background 
 * and size to cut down on lines.
 * @author Ashley Gwinnell
 */
public class ABSeparator extends JPanel
{
	/**
	 * Create a new separator.
	 */
	public ABSeparator()
	{
		this.setBackground(Color.LIGHT_GRAY);
		this.setPreferredSize(new Dimension(10,30));
	}
}
