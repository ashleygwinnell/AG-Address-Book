package org.ag.addressbook;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.WindowConstants;

import org.ag.addressbook.filter.BUABFileFilter;
import org.ag.addressbook.filter.VCardFileFilter;
import org.ag.util.undoredo.Action;

/**
 * ABDialogs contains a bunch of static methods to create various 
 * frames in the aplication.
 * @author Ashley Gwinnell
 */
public class ABDialogs 
{
	/**
	 * The dialog to show when a file has an invalid file extension.
	 * @param parent The parent window/frame.
	 * @param f The File that has the invalid extension.
	 */
	public static void createAndShowInvalidExtensionDialog(JFrame parent, File f) {
		JOptionPane.showMessageDialog(	parent, 	
				"You file you specified was an invalid type.\r\n" +
				"Only .buab .vcf and .vcard are supported!", 
				"Error", 
				JOptionPane.ERROR_MESSAGE);
	}
	
	/**
	 * The dialog to show when the user has unsaved changes.
	 * @param frame The parent window/frame.
	 * @return The JOptionPane response from YES_NO_CANCEL.
	 */
	public static int createAndShowUnsavedChangesDialog(JFrame frame) {
		return JOptionPane.showConfirmDialog(frame, "You have unsaved changes, would you like to save?", "Warning: ", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE);
	}
	
	/**
	 * The dialog to show when a user wants to open a file.
	 * @param frame The parent window/frame.
	 * @param canSelectMultiple Whether the user is allowed to select multiple files.
	 * @param title The Dialog's title.
	 * @return An array of the selected files.
	 */
	public static File[] createAndShowOpenFileSelector(JFrame frame, boolean canSelectMultiple, String title) {
		
		JFileChooser chooser = new JFileChooser();
		chooser.setName(title);
		chooser.setDialogTitle(title);
		chooser.setMultiSelectionEnabled(canSelectMultiple);
		chooser.setAcceptAllFileFilterUsed(true);
		chooser.addChoosableFileFilter(new BUABFileFilter());
		chooser.addChoosableFileFilter(new VCardFileFilter());
		chooser.setFileFilter(chooser.getAcceptAllFileFilter());
		int returnVal = chooser.showOpenDialog(frame);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			if (canSelectMultiple) {
				File[] files = chooser.getSelectedFiles();
				return files;
			} else {
				File[] files = {chooser.getSelectedFile()};
				return files;
			}
		}
		return null;
	}
	
	/**
	 * The dialog to show when the user wants to save a new document or wants to save as.
	 * @param frame The parent window/frame.
	 * @return The file that the user has chosen to save to.
	 */
	public static File createAndShowSaveFileSelector( JFrame frame) {
		File f = null;
		JFileChooser chooser = new JFileChooser();
		chooser.setMultiSelectionEnabled(false);
		chooser.setAcceptAllFileFilterUsed(false);
		chooser.addChoosableFileFilter(new BUABFileFilter(true));
		chooser.addChoosableFileFilter(new VCardFileFilter(true));
		while (true) {
			int returnVal = chooser.showDialog(frame, "Save File");
			if (returnVal == JFileChooser.APPROVE_OPTION) {
				File file = chooser.getSelectedFile();
				if ((	chooser.getFileFilter() instanceof BUABFileFilter
							&& !Importer.getFileExtension(file).equals(Importer.BUAB))
						||
						(chooser.getFileFilter() instanceof VCardFileFilter
							&& !Importer.getFileExtension(file).equals(Importer.VCARD_1)
							&& !Importer.getFileExtension(file).equals(Importer.VCARD_2))
					) {
					if (Importer.getFileExtension(file).equals("")) {
						if (chooser.getFileFilter() instanceof BUABFileFilter) {
							file = new File(file.getAbsolutePath() + ".buab");
						} else if (chooser.getFileFilter() instanceof VCardFileFilter) {
							file = new File(file.getAbsolutePath() + ".vcard");
						}
					} else {
						JOptionPane.showMessageDialog(	chooser, 
														"File extension used does not match filter.\r\n" +
														" - VCard File must use .vcard or .vcf.\r\n" +
														" - BUAB File must use .buab.\r\n" +
														"You can leave the extension blank and we'll do the work!", 
														"Error:", 
														JOptionPane.ERROR_MESSAGE);
						continue;
					}
				}
				
				if (!file.exists()) {
					f = file;
					break;
				} else {
	                int confirm = JOptionPane.showConfirmDialog(chooser, "Overwrite file? " + file.getAbsolutePath());
	                if (confirm == JOptionPane.OK_OPTION) {
	                    f = file;
	                } else if (confirm == JOptionPane.NO_OPTION) {
	                    continue;
	                }
	                break;
	            }
			} else { // wasn't approved?!
				break;
			}
		}
		return f;
	}

	/**
	 * The dialog to show to add a new contact to the Address Book.
	 * @param ab The AddressBook instance.
	 */
	public static void createAndShowAddDialog(final AddressBook ab) {
		
		
		final JDialog frame_add = new JDialog(ab.getFrame(), "Add Contact: ", true, null);
		frame_add.setIconImage(new ImageIcon(ab.getClass().getClassLoader().getResource("files/User.png")).getImage());
		frame_add.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame_add.setResizable(false);
		frame_add.setLayout(null);
		
		int current_y = 10;
		
		JLabel lbl_addcontact = new JLabel("<html><b>Add Contact</b></html>");
		lbl_addcontact.setBounds(10, current_y, 150, 20);
		frame_add.add(lbl_addcontact);
		
		current_y += 30;
		
		JLabel lbl_forenames = new JLabel("Forenames: ");
		lbl_forenames.setBounds(10, current_y, 100, 20);
		frame_add.add(lbl_forenames);
		
		final JTextField tf_forenames = new JTextField();
		tf_forenames.setBounds(110, current_y, 175, 20);
		frame_add.add(tf_forenames);
		
		current_y += 30;
		
		JLabel lbl_surname = new JLabel("Surname: ");
		lbl_surname.setBounds(10, current_y, 100, 20);
		frame_add.add(lbl_surname);
		
		final JTextField tf_surname = new JTextField();
		tf_surname.setBounds(110, current_y, 175, 20);
		frame_add.add(tf_surname);
		
		current_y += 30;
		
		JButton btn_add = new JButton("Add Contact");
		btn_add.setBounds(10, current_y, 275, 30);
		btn_add.addActionListener(new ActionListener() { 
			public void actionPerformed(ActionEvent e) {
				// validation?
				if (tf_forenames.getText().trim().equals("")) {
					JOptionPane.showMessageDialog(ab.getFrame(), "Forename(s) cannot be left empty!", "Error:", JOptionPane.ERROR_MESSAGE);
					return;
				} //else if (tf_surname.getText().trim().equals("")) {
					//JOptionPane.showMessageDialog(frame, "Surname cannot be left empty!", "Error:", JOptionPane.ERROR_MESSAGE);
					//return;
				//}
				ab.getActionStack().push(new Action() {
					public String forenames = tf_forenames.getText();
					public String surname = tf_surname.getText();
					public Contact c = new Contact();
					public String getUndoText() {
						return "Remove Contact";
					}
					public String getRedoText() {
						return "Add Contact";
					}
					public void doAction() {
						c.setForenames(forenames);
						c.setSurname(surname);
						ab.getContacts().add(c);
						ab.refreshList();
					}
					public void undoAction() {
						ab.getContacts().remove(this.c);
						ab.refreshList();
					}
				});
				
				
				
				frame_add.dispose();
			}
		});
		frame_add.add(btn_add);
		
		frame_add.setSize(300, current_y + 65);
		frame_add.setLocationRelativeTo(ab.getFrame());
		
		frame_add.setVisible(true);
	}
	
	/**
	 * The application's About dialog shown when F1 is pressed or when the
	 * user selects Help > About.
	 * @param frame The parent window/frame.
	 */
	public static void createAndShowAboutDialog(AddressBook addressBook, JFrame frame) {
		JDialog dialog = new JDialog(frame, "About: ", true, null);
		dialog.setIconImage(new ImageIcon(addressBook.getClass().getClassLoader().getResource("files/Info.png")).getImage());
		dialog.setSize(400,250);
		dialog.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		dialog.setLocationRelativeTo(frame);
		dialog.setResizable(false);
		JTextArea a = new JTextArea("Address Book\r\nVersion 0.6\r\n\r\n" +
				"Free Icons by Axialis Team.\r\n" +
				"http://www.axialis.com/free/icons/\r\n\r\n" +
				"Created by Ashley Gwinnell.\r\n" +
				"Website: http://www.ashleygwinnell.co.uk/\r\n" +
				"Feedback: info@ashleygwinnell.co.uk\r\n\r\n" +
				"Copyright 2009.");
		a.setEditable(false);
		dialog.add(a);
		dialog.setVisible(true);
	}
	
	
	/** The response from createAndShowImportDialog() to combine all the contacts */
	public static final int IMPORT_DIALOG_COMBINE_ALL = 0;
	/** The response from createAndShowImportDialog() to combine one of the contacts */
	public static final int IMPORT_DIALOG_COMBINE_ONE = 1;
	/** The response from createAndShowImportDialog() to replace all the contacts */
	public static final int IMPORT_DIALOG_REPLACE_ALL = 2;
	/** The response from createAndShowImportDialog() to replace one of the contacts */
	public static final int IMPORT_DIALOG_REPLACE_ONE = 3;
	/** The response from createAndShowImportDialog() to keep all the existing contacts */
	public static final int IMPORT_DIALOG_KEEP_ALL = 4;
	/** The response from createAndShowImportDialog() to keep one of the existing contacts */
	public static final int IMPORT_DIALOG_KEEP_ONE = 5;
	/** used internally do not change. */
	private static int IMPORT_DIALOG_response = -1;
	
	/**
	 * The dialog to show when a duplicate contact is found on import.
	 * @param addressBook The AddressBook instance.
	 * @param parent The dialog's parent window/frame.
	 * @param title The title of the dialog.
	 * @param existing The existing contact.
	 * @param replacement The replacement contact
	 * @param fromFile Which file the replacement can be found in.
	 * @return either IMPORT_DIALOG_COMBINE_ALL, IMPORT_DIALOG_COMBINE_ONE , IMPORT_DIALOG_REPLACE_ALL, IMPORT_DIALOG_REPLACE_ONE , IMPORT_DIALOG_KEEP_ALL or IMPORT_DIALOG_KEEP_ONE
	 */
	public static int createAndShowImportDialog(AddressBook addressBook, JFrame parent, String title, Contact existing, Contact replacement, File fromFile) 
	{
		final JDialog dialog = new JDialog(parent, title, true, null);
		dialog.setSize(new Dimension(670, 480));
		dialog.setLocationRelativeTo(parent);
		dialog.setIconImage(new ImageIcon(addressBook.getClass().getClassLoader().getResource("files/User.png")).getImage());
		dialog.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		dialog.addWindowListener(new WindowListener() {
			public void windowActivated(WindowEvent e) { }
			public void windowClosed(WindowEvent e) { }
			public void windowDeactivated(WindowEvent e) { }
			public void windowDeiconified(WindowEvent e) { }
			public void windowIconified(WindowEvent e) { }
			public void windowOpened(WindowEvent e) { } 
			public void windowClosing(WindowEvent e) {
				if (IMPORT_DIALOG_response == -1) {
					JOptionPane.showMessageDialog(dialog, "Please select a displayed option.", "Error:", JOptionPane.ERROR_MESSAGE);
				}
			}
		});
		dialog.setResizable(false);
		dialog.setLayout(null);
		
		JLabel lbl_title = new JLabel("<html><big>Duplicate Contact Found:</big></html> ");
		lbl_title.setBounds(10, 10, 400, 35);
		dialog.add(lbl_title);
		
		JLabel lbl_existing = new JLabel("<html><b>Existing:</b></html>");
		lbl_existing.setBounds(46, 50, 100, 25);
		dialog.add(lbl_existing);
		
		ContactPanel pnl_existing = new ContactPanel(addressBook);
		pnl_existing.repopulate(existing);
		pnl_existing.setEnabled(false);
		pnl_existing.validate();
		JScrollPane scr_existing = new JScrollPane(pnl_existing);
		scr_existing.setBounds(110, 50, 540, 140);
		dialog.add(scr_existing);
		
		JLabel lbl_replacement = new JLabel("<html><b>Replacement:</b></html>");
		lbl_replacement.setBounds(15, 200, 100, 25);
		dialog.add(lbl_replacement);
		
		ContactPanel pnl_replacement = new ContactPanel(addressBook);
		pnl_replacement.repopulate(replacement);
		pnl_replacement.setEnabled(false);
		pnl_replacement.validate();
		JScrollPane scr_replacement = new JScrollPane(pnl_replacement);
		scr_replacement.setBounds(110, 200, 540, 140);
		dialog.add(scr_replacement);
		
		
		JButton combine_all = new JButton("Combine All Duplicates");
		JButton combine_one = new JButton("Combine One");
		combine_one.setBounds(110, 360, 150, 30);
		combine_all.setBounds(110, 395, 150, 30);
		combine_one.addActionListener(new ActionListener() { public void actionPerformed(ActionEvent e) { 
			IMPORT_DIALOG_response = IMPORT_DIALOG_COMBINE_ONE;
			dialog.dispose();
		}});
		combine_all.addActionListener(new ActionListener() { public void actionPerformed(ActionEvent e) { 
			IMPORT_DIALOG_response = IMPORT_DIALOG_COMBINE_ALL;
			dialog.dispose();
		}});
		dialog.add(combine_all);
		dialog.add(combine_one);
		
		JButton replace_all = new JButton("Replace All Duplicates");
		JButton replace_one = new JButton("Replace One");
		replace_one.setBounds(265, 360, 150, 30);
		replace_all.setBounds(265, 395, 150, 30);
		replace_one.addActionListener(new ActionListener() { public void actionPerformed(ActionEvent e) { 
			IMPORT_DIALOG_response = IMPORT_DIALOG_REPLACE_ONE;
			dialog.dispose();
		}});
		replace_all.addActionListener(new ActionListener() { public void actionPerformed(ActionEvent e) { 
			IMPORT_DIALOG_response = IMPORT_DIALOG_REPLACE_ALL;
			dialog.dispose();
		}});
		dialog.add(replace_all);
		dialog.add(replace_one);
		
		JButton keep_all = new JButton("Keep Existing Contacts");
		JButton keep_one = new JButton("Keep One");
		keep_one.setBounds(420, 360, 150, 30);
		keep_all.setBounds(420, 395, 150, 30);
		keep_one.addActionListener(new ActionListener() { public void actionPerformed(ActionEvent e) { 
			IMPORT_DIALOG_response = IMPORT_DIALOG_KEEP_ONE;
			dialog.dispose();
		}});
		keep_all.addActionListener(new ActionListener() { public void actionPerformed(ActionEvent e) { 
			IMPORT_DIALOG_response = IMPORT_DIALOG_KEEP_ALL;
			dialog.dispose();
		}});
		dialog.add(keep_all);
		dialog.add(keep_one);
		
		dialog.setVisible(true);
		
		int r = IMPORT_DIALOG_response;
		IMPORT_DIALOG_response = -1;
		return r;
	}
	
	
}
