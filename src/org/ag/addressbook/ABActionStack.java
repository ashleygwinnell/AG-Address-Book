package org.ag.addressbook;

import org.ag.util.undoredo.ActionStack;

/**
 * ABActionStack is an extension to the ActionStack class that I have 
 * created and abstracted from this application's package.
 * 
 * It extends the functionality of refreshUI to change the address book's
 * frame title.
 * 
 * @author Ashley Gwinnell
 */
public class ABActionStack extends ActionStack
{
	public AddressBook addressBook;
	
	/**
	 * Create a new ABActionStack
	 * @param ab The AddressBook instance.
	 */
	public ABActionStack(AddressBook ab) {
		this.addressBook = ab;
	}
	
	@Override
	/**
	 * Extended functionality to change the window/frame's title.
	 */
	public void refreshUI() {
		super.refreshUI();
		if (this.addressBook.getFrame() == null) {
			return;
		}
		if (this.addressBook.getSavedAtStackLocation() != -1) {
			if (this.addressBook.getSavedAtStackLocation() != this.getTop()) {
				this.addressBook.getFrame().setTitle("AG Address Book - " + this.addressBook.getCurrentlyOpenedFile().getAbsolutePath() + "(*)");
			} else {
				this.addressBook.getFrame().setTitle("AG Address Book - " + this.addressBook.getCurrentlyOpenedFile().getAbsolutePath());
			}
		} else {
			if (this.getTop() != 0) {
				this.addressBook.getFrame().setTitle("AG Address Book - Untitled Document(*)");
			} else {
				this.addressBook.getFrame().setTitle("AG Address Book - Untitled Document");
			}
		}
	}
}
