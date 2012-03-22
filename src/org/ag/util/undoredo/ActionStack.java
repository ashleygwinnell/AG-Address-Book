package org.ag.util.undoredo;

import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JMenuItem;

/**
 * ActionStack
 * This is used for the undo/redo stack. 
 * It should be portable to any Java swing/awt application.
 * @author Ashley Gwinnell
 */
public class ActionStack 
{
	private ArrayList<Action> stack = new ArrayList<Action>();
	private int top = 0;
	
	private JMenuItem m_undo, m_redo;
	private JButton tb_undo, tb_redo;
	private boolean autoRefreshingUI = true;
	
	/**
	 * Create a new ActionStack unlimited in size.
	 */
	public ActionStack() {
		
	}
	
	/**
	 * Set's the UI so the ActionStack can enable/disable appropriate elements accordingly.
	 * @param m_undo The JMenuItem for "Undo".
	 * @param m_redo The JMenuItem for "Redo".
	 * @param tb_undo The JButton on the JToolBar for "Undo".
	 * @param tb_redo The JButton on the JToolBar for "Redo".
	 */
	public final void setUI(JMenuItem m_undo, JMenuItem m_redo, JButton tb_undo, JButton tb_redo)
	{
		this.m_undo = m_undo;
		this.m_redo = m_redo;
		this.tb_undo = tb_undo;
		this.tb_redo = tb_redo;
	}
	
	/**
	 * If true, the UI will refresh on every action push/pop.
	 * @param autoRefreshingUI
	 */
	public final void setAutoRefreshingUI(boolean autoRefreshingUI) {
		this.autoRefreshingUI = autoRefreshingUI;
	}
	
	/**
	 * Checks whether the ActionStack is auto refreshing the UI on every action push/pop.
	 * @return whether the ActionStack is auto refreshing the UI on every action push/pop.
	 */
	public final boolean isAutoRefreshingUI() {
		return autoRefreshingUI;
	}
	
	/**
	 * Push the most recent action. 
	 * This should be called by your "redo" items.
	 */
	public final void push() {
		this.push(this.stack.get(this.top));
	}
	
	/**
	 * Push an action.
	 * @param a The Action to push onto the ActionStack.
	 */
	public final void push(Action a) {
		try {
			stack.set(top, a);
		} catch (IndexOutOfBoundsException e) {
			stack.add(top, a);
		}
		a.doAction();
		top++;
		if (autoRefreshingUI) { this.refreshUI(); }
	}
	
	/**
	 * Pops and returns the most recent Action.
	 * This should be called by your "undo" items.
	 * @return the most recent Action.
	 */
	public final Action pop() {
		Action a = stack.get(top-1);
		a.undoAction();
		top--;
		if (top < 0) {
			top = 0;
		}
		if (autoRefreshingUI) { this.refreshUI(); }
		return a;
	}
	
	/**
	 * Gets the size of the ActionStack. i.e. the total number of Actions in the stack.
	 * @return
	 */
	public final int getSize() {
		return stack.size();
	}
	
	/**
	 * Gets the pointer to the top of the ActionStack.
	 * @return
	 */
	public final int getTop() {
		return this.top;
	}
	
	/**
	 * Gets the action at the particular index of the stack.
	 * @param i The index to look at for an Action.
	 * @return the action at the particular cell of the stack.
	 */
	public final Action getAction(int i) {
		return this.stack.get(i);
	}
	
	/**
	 * Checks whether the ActionStack is at the most recent item. 
	 * i.e. there is nothing to redo.
	 * @return true of the Actionstack is at the most recent item.
	 */
	public final boolean isAtTop() {
		return (this.top == this.getSize());
	}
	
	/**
	 * Checks whether the ActionStack is at the first added item.
	 * i.e. there is nothing to undo.
	 * @return
	 */
	public final boolean isAtBottom() {
		return (this.top == 0);
	}
	
	/**
	 * Clears the action stack of all Action items.
	 */
	public final void clear() {
		this.stack.clear();
		this.top = 0;
	}
	
	/**
	 * Refreshes the UI. 
	 * This is called automatically on every push/pop if it is set 
	 * to automatic refreshing.
	 */
	public void refreshUI() 
	{
		if (m_redo == null || m_undo == null || tb_undo == null || tb_redo == null) {
			System.out.println("SHITSHIT!");
			return;
		}
		if (this.getSize() == 0) {
			m_redo.setText("Redo");
			m_redo.setEnabled(false);
			m_undo.setText("Undo");
			m_undo.setEnabled(false);
			tb_undo.setEnabled(false);
			tb_redo.setEnabled(false);
		} else if (this.isAtTop()) {
			m_redo.setText("Redo");
			m_redo.setEnabled(false);
			tb_redo.setEnabled(false);
			if (this.getTop() >= 1) {
				m_undo.setText("Undo (" + this.getAction(this.getTop()-1).getUndoText() + ")");
				m_undo.setEnabled(true);
				tb_undo.setEnabled(true);
			}
		} else if (!this.isAtTop() && !this.isAtBottom()) {
			m_undo.setText("Undo (" + this.getAction(this.getTop()-1).getUndoText() + ")");
			m_undo.setEnabled(true);
			m_redo.setText("Redo (" + this.getAction(this.getTop()).getRedoText() + ")");
			m_redo.setEnabled(true);
			tb_undo.setEnabled(true);
			tb_redo.setEnabled(true);
		} else if (this.isAtBottom()) {
			m_undo.setText("Undo");
			m_undo.setEnabled(false);
			tb_undo.setEnabled(false);
			if (this.getSize() > 0) {
				m_redo.setText("Redo (" + this.getAction(this.getTop()).getRedoText() + ")");
				m_redo.setEnabled(true);
				tb_redo.setEnabled(true);
			}
		}
	}
}