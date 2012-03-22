package org.ag.util.undoredo;

/**
 * Action
 * An Action is an item can is performed by the user that can be undone and redone.
 * These should be added to the ActionStack by the use of Anonymous Inner Classes.
 * @author Ashley Gwinnell
 */
public interface Action 
{
	/** The text to display next to your "Undo" UI components. */
	public String getUndoText();
	
	/** The text to display next to your "Redo" UI components. */
	public String getRedoText();
	
	/** The code to perform when the action is performed. i.e. done or redone. */
	public void doAction();
	
	/** The code to perform when the action is undone. */
	public void undoAction();
}
