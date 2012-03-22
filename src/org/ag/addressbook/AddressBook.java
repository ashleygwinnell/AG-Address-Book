package org.ag.addressbook;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.UIManager;
import javax.swing.border.MatteBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.ag.util.recentfileset.RecentFileSet;
import org.ag.util.undoredo.Action;
import org.ag.util.undoredo.ActionStack;

/**
 * Address Book
 *  TODO: 
 *   - UI
 *   	- ContactPanel to be aligned at top.
 *   - help file. 
 *   	- search is separated by commas.
 *   	- open is different from import.
 *   - link up to facebook / netbin.
 *   - clean up import of duplicate contacts so it shows "duplicate contact 1 of 6".
 *  
 * @version 0.6
 * @author Ashley Gwinnell
 */
public class AddressBook 
{
	private JFrame frame;
	private DefaultListModel model;
	private JList list;
	private File currentlyOpenedFile;
	private ContactPanel contactPanel;
	
	private ABActionStack actionStack;
	private int savedAtStackLocation = -1;
	private RecentFileSet recentFileSet;
	
	private JMenuItem m_undo;
	private JMenuItem m_redo;
	private JButton tb_undo;
	private JButton tb_redo;
	
	private JTextField tf_search;
	private String filter = "";
	
	private ArrayList<Contact> contacts = new ArrayList<Contact>();
	private ArrayList<Contact> filteredContacts = new ArrayList<Contact>();
	private ArrayList<Contact> unfilteredContacts = new ArrayList<Contact>();
	
	/**
	 * Create a new Address book window.
	 */
	public AddressBook() 
	{
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} 
		catch (Exception e) { }
		
		JFrame.setDefaultLookAndFeelDecorated(false);
		
		final AddressBook addressBook = this;
		
		
		frame = new JFrame();
		frame.setTitle("AG Address Book - Untitled Document");
		frame.setSize(750, 560);
		frame.setMinimumSize(new Dimension(750, 560));
		frame.setIconImage(new ImageIcon(getClass().getClassLoader().getResource("files/FrameIcon.png")).getImage());
		frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		frame.setLocationRelativeTo(null);
		
		recentFileSet = new RecentFileSet(5);
		
		JMenuBar menubar = new JMenuBar();
			
			JMenu menu_file = new JMenu("File");
			menu_file.setMnemonic(KeyEvent.VK_F);
			
				JMenuItem menuitem_new = new JMenuItem("New");
				menuitem_new.setMnemonic(KeyEvent.VK_N);
				menuitem_new.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, InputEvent.CTRL_DOWN_MASK));
				menuitem_new.addActionListener(new ActionListener() { public void actionPerformed(ActionEvent e) { 
					newFile();
				}});
				menuitem_new.setIcon(new ImageIcon(getClass().getClassLoader().getResource("files/Doc-Add.png")));
				menu_file.add(menuitem_new);
				
				menu_file.add(new JSeparator());
				
				JMenuItem menuitem_open = new JMenuItem("Open File");
				menuitem_open.setMnemonic(KeyEvent.VK_O);
				menuitem_open.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, InputEvent.CTRL_DOWN_MASK));
				menuitem_open.setIcon(new ImageIcon(getClass().getClassLoader().getResource("files/Folder.png")));
				menuitem_open.addActionListener(new ActionListener() { public void actionPerformed(ActionEvent e) {
					open();
				}});
				menu_file.add(menuitem_open);
				
				JMenu menu_recentfiles = new JMenu("Open Recent File ");
				menu_recentfiles.setIcon(new ImageIcon(getClass().getClassLoader().getResource("files/Folder.png")));
				final ArrayList<String> recentfiles = this.recentFileSet.get();
				for (int i = 0; i < recentfiles.size(); i++) {
					final int j = i;
					JMenuItem menuitem_recentfile = new JMenuItem((i + 1) + ") " + recentfiles.get(i));
					menuitem_recentfile.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent e) {
							open(new File(recentfiles.get(j)));
						}
					});
					menu_recentfiles.add(menuitem_recentfile);
				}
				if (recentfiles.size() == 0) {
					menu_recentfiles.setEnabled(false);
				}
				menu_file.add(menu_recentfiles);
				
				menu_file.add(new JSeparator());
				
				JMenuItem menuitem_save = new JMenuItem("Save");
				menuitem_save.setMnemonic(KeyEvent.VK_S);
				menuitem_save.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_DOWN_MASK));
				menuitem_save.addActionListener(new ActionListener() { public void actionPerformed(ActionEvent e) {
					save();
				}});
				menuitem_save.setIcon(new ImageIcon(getClass().getClassLoader().getResource("files/Save.png")));
				menu_file.add(menuitem_save);
				
				JMenuItem menuitem_saveas = new JMenuItem("Save As");
				menuitem_saveas.setMnemonic(KeyEvent.VK_A);
				menuitem_saveas.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_DOWN_MASK + InputEvent.SHIFT_DOWN_MASK));
				menuitem_saveas.addActionListener(new ActionListener() { public void actionPerformed(ActionEvent e) {
					saveAs();
				}});
				menuitem_saveas.setIcon(new ImageIcon(getClass().getClassLoader().getResource("files/SaveAs.png")));
				menu_file.add(menuitem_saveas);
				
				menu_file.add(new JSeparator());
				
				JMenuItem menuitem_import = new JMenuItem("Import Contacts From File");
				menuitem_import.setMnemonic(KeyEvent.VK_I);
				menuitem_import.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_I, InputEvent.CTRL_DOWN_MASK));
				menuitem_import.addActionListener(new ActionListener() { public void actionPerformed(ActionEvent e) {
					importContacts();
				}});
				menuitem_import.setIcon(new ImageIcon(getClass().getClassLoader().getResource("files/Datbase-Add.png")));
				menu_file.add(menuitem_import);
				
				JMenu menu_import_recentfiles = new JMenu("Import Contacts From Recent File ");
				menu_import_recentfiles.setIcon(new ImageIcon(getClass().getClassLoader().getResource("files/Datbase-Add.png")));
				final ArrayList<String> import_recentfiles = this.recentFileSet.get();
				for (int i = 0; i < recentfiles.size(); i++) {
					final int j = i;
					JMenuItem menuitem_recentfile = new JMenuItem((i + 1) + ") " + recentfiles.get(i));
					menuitem_recentfile.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent e) {
							importContacts(new File[] {new File(recentfiles.get(j))});
						}
					});
					menu_import_recentfiles.add(menuitem_recentfile);
				}
				if (import_recentfiles.size() == 0) {
					menu_import_recentfiles.setEnabled(false);
				}
				menu_file.add(menu_import_recentfiles);
				
				menu_file.add(new JSeparator());
				
				JMenuItem menuitem_exit = new JMenuItem("Exit ");
				menuitem_exit.setMnemonic(KeyEvent.VK_E);
				menuitem_exit.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_W, InputEvent.CTRL_DOWN_MASK));
				menuitem_exit.addActionListener(new ActionListener() { 
					public void actionPerformed(ActionEvent e) {
						close();
					}
				});
				menuitem_exit.setIcon(new ImageIcon(getClass().getClassLoader().getResource("files/Delete.png")));
				menu_file.add(menuitem_exit);
			
			menubar.add(menu_file);
			
			JMenu menu_edit = new JMenu("Edit");
			menu_edit.setMnemonic(KeyEvent.VK_E);
				m_undo = new JMenuItem("Undo");
				m_undo.setEnabled(false);
				m_undo.setMnemonic(KeyEvent.VK_U);
				m_undo.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Z, InputEvent.CTRL_DOWN_MASK + InputEvent.ALT_DOWN_MASK));
				m_undo.addActionListener(new ActionListener() { 
					public void actionPerformed(ActionEvent e) {
						actionStack.pop();
					}
				});
				m_undo.setIcon(new ImageIcon(getClass().getClassLoader().getResource("files/Left.png")));
				menu_edit.add(m_undo);
				m_redo = new JMenuItem("Redo");
				m_redo.setEnabled(false);
				m_redo.setMnemonic(KeyEvent.VK_R);
				m_redo.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Y, InputEvent.CTRL_DOWN_MASK + InputEvent.ALT_DOWN_MASK));
				m_redo.addActionListener(new ActionListener() { 
					public void actionPerformed(ActionEvent e) {
						actionStack.push();
					}
				});
				m_redo.setIcon(new ImageIcon(getClass().getClassLoader().getResource("files/Right.png")));
				menu_edit.add(m_redo);				
			menubar.add(menu_edit);
			
			JMenu menu_help = new JMenu("Help");
			menu_help.setMnemonic(KeyEvent.VK_H);
			
				JMenuItem menuitem_about = new JMenuItem("About");
				menuitem_about.setMnemonic(KeyEvent.VK_A);
				menuitem_about.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F1, 0));
				menuitem_about.addActionListener(new ActionListener() { public void actionPerformed(ActionEvent e) { 
					ABDialogs.createAndShowAboutDialog(addressBook, getFrame());
				}});
				menuitem_about.setIcon(new ImageIcon(getClass().getClassLoader().getResource("files/Info.png")));
				menu_help.add(menuitem_about);
				
				
			menubar.add(menu_help);
			
		frame.setJMenuBar(menubar);
		
		frame.addWindowListener(new WindowListener() {
			public void windowActivated(WindowEvent e) { }
			public void windowClosed(WindowEvent e) { }
			public void windowDeactivated(WindowEvent e) { }
			public void windowDeiconified(WindowEvent e) { }
			public void windowIconified(WindowEvent e) { }
			public void windowOpened(WindowEvent e) { } 
			public void windowClosing(WindowEvent e) {
				close();
			}
		});
		
		JPanel toolBar = new JPanel();
		toolBar.setPreferredSize(new Dimension(toolBar.getWidth(), 36));
		toolBar.setLayout(null);
			int toolbar_separator_num = 0;
			int toolbar_item_num = 0;
			int toolbar_item_width = 32;
			
			
			
			JButton tb_new = new JButton(new ImageIcon(getClass().getClassLoader().getResource("files/Doc-Add.png")));
			tb_new.setToolTipText("New Document");
			tb_new.setBounds((toolbar_item_num*toolbar_item_width) + 2 + (7*toolbar_separator_num), 2, toolbar_item_width, toolbar_item_width);
			tb_new.setMargin(new Insets(1,1,1,1));
			tb_new.addActionListener(new ActionListener() { public void actionPerformed(ActionEvent e) { 
				newFile();
			} });
			toolBar.add(tb_new);
			toolbar_item_num++;
			
			JButton tb_open = new JButton(new ImageIcon(getClass().getClassLoader().getResource("files/Folder.png")));
			tb_open.setToolTipText("Open Document");
			tb_open.setBounds((toolbar_item_num*toolbar_item_width) + 2 + (7*toolbar_separator_num), 2,toolbar_item_width, toolbar_item_width);
			tb_open.setMargin(new Insets(1,1,1,1));
			tb_open.addActionListener(new ActionListener() { public void actionPerformed(ActionEvent e) {
				open();
			}});
			toolBar.add(tb_open);
			toolbar_item_num++;
			
			JButton tb_save = new JButton(new ImageIcon(getClass().getClassLoader().getResource("files/Save.png")));
			tb_save.setToolTipText("Save Document");
			tb_save.setBounds((toolbar_item_num*toolbar_item_width) + 2 + (7*toolbar_separator_num), 2,toolbar_item_width, toolbar_item_width);
			tb_save.setMargin(new Insets(1,1,1,1));
			tb_save.addActionListener(new ActionListener() { public void actionPerformed(ActionEvent e) {
				save();
			}});
			toolBar.add(tb_save);
			toolbar_item_num++;
			
			JButton tb_saveas = new JButton(new ImageIcon(getClass().getClassLoader().getResource("files/SaveAs.png")));
			tb_saveas.setToolTipText("Save Document As Copy");
			tb_saveas.setBounds((toolbar_item_num*toolbar_item_width) + 2 + (7*toolbar_separator_num), 2,toolbar_item_width, toolbar_item_width);
			tb_saveas.setMargin(new Insets(1,1,1,1));
			tb_saveas.addActionListener(new ActionListener() { public void actionPerformed(ActionEvent e) {
				saveAs();
			}});		
			toolBar.add(tb_saveas);
			toolbar_item_num++;
			
			ABSeparator s2 = new ABSeparator();
			s2.setBounds((toolbar_item_num*toolbar_item_width) + 5 + (7*toolbar_separator_num), 6, 1, toolbar_item_width - 8);
			toolBar.add(s2);
			toolbar_separator_num++;
			
			JButton tb_import = new JButton(new ImageIcon(getClass().getClassLoader().getResource("files/Datbase-Add.png")));
			tb_import.setToolTipText("Import Contacts From A File");
			tb_import.setBounds(	(toolbar_item_num*toolbar_item_width) + 2 + (7*toolbar_separator_num), 
									2,
									toolbar_item_width, 
									toolbar_item_width);
			tb_import.setMargin(new Insets(1,1,1,1));
			tb_import.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					importContacts();
				}
			});
			toolBar.add(tb_import);
			toolbar_item_num++;
			
			ABSeparator ss = new ABSeparator();
			ss.setBounds((toolbar_item_num*toolbar_item_width) + 5 + (7*toolbar_separator_num), 6, 1, toolbar_item_width - 8);
			toolBar.add(ss);
			toolbar_separator_num++;
			
			tb_undo = new JButton(new ImageIcon(getClass().getClassLoader().getResource("files/Left.png")));
			tb_undo.setToolTipText("Undo");
			tb_undo.setBounds((toolbar_item_num*toolbar_item_width) + 2 + (7*toolbar_separator_num), 2,toolbar_item_width, toolbar_item_width);
			tb_undo.setMargin(new Insets(1,1,1,1));
			tb_undo.addActionListener(new ActionListener() { public void actionPerformed(ActionEvent e) {
				actionStack.pop();
			}});		
			tb_undo.setEnabled(false);
			toolBar.add(tb_undo);
			toolbar_item_num++;
			
			tb_redo = new JButton(new ImageIcon(getClass().getClassLoader().getResource("files/Right.png")));
			tb_redo.setToolTipText("Redo");
			tb_redo.setBounds((toolbar_item_num*toolbar_item_width) + 2 + (7*toolbar_separator_num), 2,toolbar_item_width, toolbar_item_width);
			tb_redo.setMargin(new Insets(1,1,1,1));
			tb_redo.addActionListener(new ActionListener() { public void actionPerformed(ActionEvent e) {
				actionStack.push();
			}});	
			tb_redo.setEnabled(false);
			toolBar.add(tb_redo);
			toolbar_item_num++;
			
			ABSeparator s = new ABSeparator();
			s.setBounds((toolbar_item_num*toolbar_item_width) + 5 + (7*toolbar_separator_num), 6, 1, toolbar_item_width - 8);
			toolBar.add(s);
			toolbar_separator_num++;
			
			JButton tb_add = new JButton(new ImageIcon(getClass().getClassLoader().getResource("files/User.png")));
			tb_add.setToolTipText("Add Contact");
			tb_add.setBounds(	(toolbar_item_num*toolbar_item_width) + 2 + (7*toolbar_separator_num), 
								2,
								toolbar_item_width, 
								toolbar_item_width);
			tb_add.setMargin(new Insets(1,1,1,1));
			final AddressBook ab = this;
			tb_add.addActionListener(new ActionListener() { 
				public void actionPerformed(ActionEvent e) {
					ABDialogs.createAndShowAddDialog(ab);
				}
			});
			toolBar.add(tb_add);
			toolbar_item_num++;
			
			JButton tb_del = new JButton(new ImageIcon(getClass().getClassLoader().getResource("files/User-Del.png")));
			tb_del.setToolTipText("Remove Contact(s)");
			tb_del.setBounds(	(toolbar_item_num*toolbar_item_width) + 2 + (7*toolbar_separator_num), 
								2,
								toolbar_item_width, 
								toolbar_item_width);
			tb_del.setMargin(new Insets(1,1,1,1));
			tb_del.addActionListener(new ActionListener() { 
				public void actionPerformed(ActionEvent e) {
					if (list.getSelectedIndex() != -1) {
						getActionStack().push(new Action() {
							public int index = list.getSelectedIndex();
							public Contact c = contacts.get(index);
							public void doAction() {
								contacts.remove(c);
								refreshList();
								int current_index = 0;
								list.setSelectedIndex(index); current_index = index;
								if (index >= model.size()) {
									list.setSelectedIndex(index - 1); current_index = index - 1;
									if (index - 1 >= model.size()) {
										list.setSelectedIndex(index - 2); current_index = index - 2;
										if (index - 2 >= model.size()) {
											list.setSelectedIndex(0); current_index = 0;
										}
									}
								}
								if (model.size() > 0) {
									contactPanel.repopulate(contacts.get(current_index));
								}
							}
							public String getRedoText() {
								return "Delete " + c.getName();
							}
							public String getUndoText() {
								return "Undelete " + c.getName();
							}
							public void undoAction() {
								contacts.add(c); 
								refreshList();
							}
						});
					}
				}
			});
			toolBar.add(tb_del);
			toolbar_item_num++;
			
			
			
			ABSeparator s3 = new ABSeparator();
			s3.setBounds((toolbar_item_num*toolbar_item_width) + 5 + (7*toolbar_separator_num), 6, 1, toolbar_item_width - 8);
			toolBar.add(s3);
			toolbar_separator_num++;
			
			int xOffset = 0;
			JPanel pnl_search = new JPanel();
			pnl_search.setBounds(	(toolbar_item_num*toolbar_item_width) + 2 + (7*toolbar_separator_num), 
									2, 
									toolbar_item_width + 200, 
									toolbar_item_width);
			pnl_search.setLayout(null);
			pnl_search.setBackground(Color.white);
				JLabel lbl_icon = new JLabel(new ImageIcon(getClass().getClassLoader().getResource("files/Search.png")));
				lbl_icon.setBounds(0, 0, 32, pnl_search.getHeight());
				lbl_icon.setBorder(new MatteBorder(1, 1, 1, 0, Color.LIGHT_GRAY));
				pnl_search.add(lbl_icon);
				
				tf_search = new JTextField("");
				tf_search.setBounds(30,0, 200 + toolbar_item_width - 30, pnl_search.getHeight());
				tf_search.setBackground(Color.white);
				tf_search.setBorder(new MatteBorder(1, 0, 1, 1, Color.LIGHT_GRAY));
				tf_search.addKeyListener(new KeyListener() {
					public void keyPressed(KeyEvent e) {}
					public void keyReleased(KeyEvent e) { 
						setFilter(tf_search.getText()); 
					}
					public void keyTyped(KeyEvent e) {}
				});
				pnl_search.add(tf_search);
			toolBar.add(pnl_search);
			toolbar_item_num++;
			xOffset += 200;
			
			ABSeparator s4 = new ABSeparator();
			s4.setBounds((toolbar_item_num*toolbar_item_width) + 5 + (7*toolbar_separator_num) + xOffset, 6, 1, toolbar_item_width - 8);
			toolBar.add(s4);
			toolbar_separator_num++;
			JButton tb_about = new JButton(new ImageIcon(getClass().getClassLoader().getResource("files/Info.png")));
			tb_about.setToolTipText("About Application");
			tb_about.setBounds(	(toolbar_item_num*toolbar_item_width) + 2 + (7*toolbar_separator_num) + xOffset, 2, toolbar_item_width, toolbar_item_width);
			tb_about.setMargin(new Insets(1,1,1,1));
			tb_about.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					ABDialogs.createAndShowAboutDialog(addressBook, getFrame());
				}
			});
			toolBar.add(tb_about);
			toolbar_item_num++;
			
			JButton tb_exit = new JButton(new ImageIcon(getClass().getClassLoader().getResource("files/Delete.png")));
			tb_exit.setToolTipText("Exit Application");
			tb_exit.setBounds(	(toolbar_item_num*toolbar_item_width) + 2 + (7*toolbar_separator_num) + xOffset, 2, toolbar_item_width, toolbar_item_width);
			tb_exit.setMargin(new Insets(1,1,1,1));
			tb_exit.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					close();
				}
			});
			toolBar.add(tb_exit);
			toolbar_item_num++;
			
			frame.add(toolBar, BorderLayout.NORTH);

		
		JSplitPane pane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		pane.setDividerLocation(pane.getSize().width
				                - pane.getInsets().right
				                - pane.getDividerSize()
				                - 180);
		
			
			JPanel listPanel = new JPanel();
			listPanel.setLayout(new GridLayout(1,1));
			
			model = new DefaultListModel();
			list = new JList(model);
			list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			list.addListSelectionListener(new ListSelectionListener() {
				public void valueChanged(ListSelectionEvent e) {
					try {
						int i = list.getSelectedIndex();
						if (getFilter().equals("")) {
							contactPanel.repopulate(contacts.get(i));
						} else {
							contactPanel.repopulate(filteredContacts.get(i));
						}
						frame.validate();
						// contactPanel.setVisibility(true);
					} catch (ArrayIndexOutOfBoundsException ex) {
						//JOptionPane.showMessageDialog(frame, "")
					}
				}
			});
			
			listPanel.add(list);
			
			JScrollPane listPanelScroll = new JScrollPane(listPanel);
			listPanelScroll.setMinimumSize(new Dimension(180, 0));
			listPanelScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
			pane.setLeftComponent(listPanelScroll);
			
			contactPanel = new ContactPanel(this);		
			JScrollPane contactPanelScroll = new JScrollPane(contactPanel);
			contactPanelScroll.setMinimumSize(new Dimension(547, 0));
			contactPanelScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
			contactPanelScroll.setBorder(null);
			pane.setRightComponent(contactPanelScroll);
			
		frame.add(pane, BorderLayout.CENTER);
		
		actionStack = new ABActionStack(this);
		actionStack.setUI(m_undo, m_redo, tb_undo, tb_redo);
		
		
		frame.setVisible(true);
	}
	
	/**
	 * This will set the search filter string and refresh the list.
	 * @param text
	 */
	public void setFilter(String text) 
	{
		this.filter = text.toUpperCase();
		this.refreshList();
	}
	
	/**
	 * return the search filter.
	 * @return
	 */
	public String getFilter() {
		return filter;
	}

	/**
	 * Clear any existing contacts in the address book. 
	 * will ask the user if there are unsaved changes.
	 */
	public void newFile() {
		if (this.isChangedAndUnsaved()) {
			int response = ABDialogs.createAndShowUnsavedChangesDialog(getFrame());
		
			if (response == JOptionPane.CANCEL_OPTION) {
				return;
			} else if (response == JOptionPane.YES_OPTION) {
				this.save();
				if (this.save() == false) {
					return;
				}
			}
		}
		this.clearList();
		this.currentlyOpenedFile = null;
		this.savedAtStackLocation = -1;
		this.actionStack.clear();
		this.actionStack.refreshUI();
		
		frame.setTitle("AG Address Book - Untitled Document");
		this.actionStack.refreshUI();
		refreshList();
	}
	
	/**
	 * Opens the "import file" dialog where the user picks one or
	 * more files to import.
	 */
	public void importContacts() {
		File[] files = ABDialogs.createAndShowOpenFileSelector(getFrame(), true, "Import Contacts From File: ");
		if (files != null) {
			this.importContacts(files);
		}
	}
	
	/**
	 * Imports all the contacts from the files into hte address book.
	 * Will ask the user what to do on duplicate contacts.
	 * @param files A collective of files to attempt to import.
	 */
	public void importContacts(File[] files) 
	{
		int all_response = -1;
		boolean duplicateThisPass = false;
		for (int i = 0; i < files.length; i++) {
			
			if (!Importer.isValidExtension(files[i])) {
				ABDialogs.createAndShowInvalidExtensionDialog(getFrame(), files[i]);
				continue;
			}
			Importer importer = new Importer(files[i]);
			ArrayList<Contact> contacts = importer.load();
			for (int j = 0; j < contacts.size(); j++) {
				for (int k = 0; k < this.contacts.size(); k++) {
					if (contacts.get(j).getNameForSorting().equals(this.contacts.get(k).getNameForSorting())) {
						duplicateThisPass = true;
						int response;
						if (all_response != -1) {
							response = all_response;
						} else {
							// ask the user what to do with the duplicate contact!
							response = ABDialogs.createAndShowImportDialog(this, this.frame, "Duplicate Contacts Found: ", this.contacts.get(k), contacts.get(j), files[i]);
						}
						
						// DO TO ALL.
						if (response == ABDialogs.IMPORT_DIALOG_COMBINE_ALL) {
							all_response = ABDialogs.IMPORT_DIALOG_COMBINE_ONE;
						} else if (response == ABDialogs.IMPORT_DIALOG_REPLACE_ALL) {
							all_response = ABDialogs.IMPORT_DIALOG_REPLACE_ONE;
						} else if (response == ABDialogs.IMPORT_DIALOG_KEEP_ALL) {
							// do nothing for all. :3
							all_response = ABDialogs.IMPORT_DIALOG_KEEP_ONE;
						}
						
						// DO SINGULAR
						if (response == ABDialogs.IMPORT_DIALOG_COMBINE_ONE || all_response == ABDialogs.IMPORT_DIALOG_COMBINE_ONE) {
							// combine contacts.
							this.contacts.get(k).combine(contacts.get(j));
						} else if (response == ABDialogs.IMPORT_DIALOG_REPLACE_ONE || all_response == ABDialogs.IMPORT_DIALOG_REPLACE_ONE) {
							this.contacts.set(k, contacts.get(j));
						} else if (response == ABDialogs.IMPORT_DIALOG_KEEP_ONE || all_response == ABDialogs.IMPORT_DIALOG_KEEP_ONE) {
							// do nothing!
						}
						break;
					}
				}
				if (!duplicateThisPass) {
					this.contacts.add(contacts.get(j));
				}
				duplicateThisPass = false;
			}
		}
		this.refreshList();
	}
	
	/**
	 * Close the Address Book safely asking the user what to 
	 * do with their unsaved changes.
	 */
	public void close() {
		if (this.isChangedAndUnsaved()) {
			int response = ABDialogs.createAndShowUnsavedChangesDialog(getFrame());
		
			if (response == JOptionPane.CANCEL_OPTION) {
				return;
			} else if (response == JOptionPane.YES_OPTION) {
				this.save();
				if (this.save() == false) {
					return;
				}
			}
			System.exit(0);
		} else {
			System.exit(0);
		}
	}
	
	/**
	 * Opens the "open file" dialog where the user picks 
	 * one file to open.
	 */
	public void open()
	{
		File[] files = ABDialogs.createAndShowOpenFileSelector(getFrame(), false, "Open File:");
		if (files != null) {
			this.open(files[0]);
		}	
	}
	
	/**
	 * Opens the file specified and oimports all of the contacts in the file
	 * into the address book. Will stop working with the previous file. 
	 * Will clear the existing address book entries.
	 * @param f The file to open.
	 */
	public void open(File f) {
		if (!Importer.isValidExtension(f)) {
			ABDialogs.createAndShowInvalidExtensionDialog(getFrame(), f);
			return;
		}
		if (this.isChangedAndUnsaved()) {
			int response = ABDialogs.createAndShowUnsavedChangesDialog(getFrame());
			
			if (response == JOptionPane.CANCEL_OPTION) {
				return;
			} else if (response == JOptionPane.YES_OPTION) {
				if (this.save() == false) {
					return;
				}
			}
		}
		this.clearList();
		this.actionStack.clear();
		this.savedAtStackLocation = 0;
		this.currentlyOpenedFile = f;
		this.actionStack.refreshUI();
		
		Importer i = new Importer(f);
		frame.setTitle("AG Address Book - " + f.getAbsolutePath());
		this.addToList(i.load());
		if (!this.recentFileSet.isRecentFile(f.getAbsolutePath())) {
			this.recentFileSet.add(f.getAbsolutePath());
		}
		
		this.refreshList();		
	}
	
	
	/**
	 * Determines whether the address book has unsaved changes.
	 * @return whether the address book has unsaved changes.
	 */
	public boolean isChangedAndUnsaved() {
		if (savedAtStackLocation != -1) {
			if (savedAtStackLocation != this.actionStack.getTop()) {
				return true;
			}
		} 
		return false;
	}
	
	
	/**
	 * Saves the address book data to the currently opened file.
	 * @return true on success, false on failure.
	 */
	public boolean save() {
		if (this.currentlyOpenedFile == null) {
			// no file is opened, so have to create one to save to!
			this.saveAs();
		} else {
			int response = this.checkForInformationNotStorableInBUAB();
			if (response == JOptionPane.NO_OPTION) {
				return false;
			}
			// file is opened.
			if (!this.recentFileSet.isRecentFile(this.currentlyOpenedFile.getAbsolutePath())) {
				this.recentFileSet.add(this.currentlyOpenedFile.getAbsolutePath());
			}
			frame.setTitle("AG Address Book - " + this.currentlyOpenedFile.getAbsolutePath());
			savedAtStackLocation = this.actionStack.getTop();
			if (!this.currentlyOpenedFile.exists()) {
				try {
					this.currentlyOpenedFile.createNewFile();
				} catch (IOException e1) {
					JOptionPane.showMessageDialog(frame, "Cannot save file.\r\nMake sure it is not in use by any other programs and try again.", "Error: ", JOptionPane.ERROR_MESSAGE);
					return false;
				}
			}
			
			Exporter e = new Exporter(this.currentlyOpenedFile, this.contacts);
			e.write();
			
		}
		return true;
	}
	
	/**
	 * Saves the address book data to a file specified by the user with a dialog.
	 */
	public void saveAs() {
		if (contacts.size() == 0) {
			JOptionPane.showMessageDialog(frame, "You are saving an empty address book document.", "Warning:", JOptionPane.WARNING_MESSAGE);
		}
		File f = ABDialogs.createAndShowSaveFileSelector(frame);
		if (f != null) {
			this.currentlyOpenedFile = f;
			this.save();
		}
	}
	
	/**
	 * Checks whether the current address book data can be stored in a BUAB file.
	 * @return -1 if data is storable in buab and the JOptionPane response otherwise.
	 */
	public int checkForInformationNotStorableInBUAB() {
		if (Importer.getFileExtension(this.currentlyOpenedFile).equals(Importer.BUAB)) {
			boolean valid = true;
			for (int i = 0; i < this.contacts.size(); i++) {
				if (this.contacts.get(i).hasInformationNotStorableInBUAB()) {
					valid = false;
				}
			}
			if (!valid) {
				int response = JOptionPane.showConfirmDialog(
										this.frame, 
										"Some of your Address Book Data cannot be stored in the BUAB format.\n" +
										"We advise that you save in VCard format.\n" +
										"Are you sure that you want to continue (and lose data)?", 
										"Warning:", 
										JOptionPane.YES_NO_OPTION, 
										JOptionPane.WARNING_MESSAGE);
				return response;
			}
		}
		return -1;

	}
	
	/**
	 * Clears the current working list and contacts.
	 */
	public void clearList() {
		model.clear();
		contacts.clear();
	}
	
	/**
	 * Adds contacts to the address books UI.
	 * @param list
	 */
	public void addToList(ArrayList<Contact> list) {
		for (int i = 0; i < list.size(); i++) {
			contacts.add(list.get(i));
			model.addElement(" " + list.get(i).getForenames() + " " + list.get(i).getSurname());
		}
	}
	
	/**
	 * Refreshes the address book list interface taking  
	 * into account the currently set filter.
	 */
	public void refreshList() { 
		model.clear();
		
		for (int i = 0; i < this.contacts.size(); i++) {
			for (int j = this.contacts.size()-1; j > i; j--) {
				if (this.contacts.get(i).getNameForSorting().compareTo(this.contacts.get(j).getNameForSorting()) > 0) {
					Contact c = this.contacts.get(j);
					this.contacts.set(j, this.contacts.get(i));
					this.contacts.set(i, c);
				}
			}
		}
		
		if (this.filter.equals("")) {
			// no filter.
			//this.contacts.addAll(this.filteredContacts);
			for (int i = 0; i < this.contacts.size(); i++) {
				model.addElement(" " + this.contacts.get(i).getForenames() + " " + this.contacts.get(i).getSurname());
			}
		} else {
			// apply filter
			filteredContacts.clear();
			unfilteredContacts.clear();
			String[] filter_items = filter.split(",");
			for (int i = 0; i < this.contacts.size(); i++) {
				for (int j = 0; j < filter_items.length; j++) {
					if (!this.filteredContacts.contains(this.contacts.get(i))) {
						if (this.contacts.get(i).toSearchString().toUpperCase().contains(filter_items[j])) {
							this.filteredContacts.add(this.contacts.get(i));	
						} else {
							this.unfilteredContacts.add(this.contacts.get(i));
						}
					}
				}
			}
			ArrayList<Contact> remove = new ArrayList<Contact>();
			for (int i = 0; i < this.filteredContacts.size(); i++) {
				for (int j = 0; j < filter_items.length; j++) {
					if (!this.filteredContacts.get(i).toSearchString().toUpperCase().contains(filter_items[j])) {
						remove.add(this.filteredContacts.get(i));
						break;
					}
				}
			}
			this.filteredContacts.removeAll(remove);
			for (int i = 0; i < this.filteredContacts.size(); i++) {
			//	System.out.println(this.filteredContacts.get(i).toSearchString().toUpperCase());
				model.addElement(" " + this.filteredContacts.get(i).getForenames() + " " + this.filteredContacts.get(i).getSurname());
			}
		}
		
		if (this.contacts.size() == 0) {
			this.contactPanel.repopulate(null);
		}
	}
	
	/**
	 * Get the contact panel
	 * @return the address books contact panel.
	 */
	public ContactPanel getContactPanel() {
		return contactPanel;
	}
	
	/**
	 * get the undo/redo stack.
	 * @return the undo/redo action stack.
	 */
	public ActionStack getActionStack() {
		return actionStack;
	}
	
	/**
	 * get the address books frame.
	 * @return the address book's frame.
	 */
	public JFrame getFrame() {
		return frame;
	}
	
	/**
	 * The address books stack location
	 * @return -1 if document is not open.
	 */
	public int getSavedAtStackLocation() {
		return savedAtStackLocation;
	}
	
	/**
	 * get the file that is currently "open".
	 * @return the file that is currently "open".
	 */
	public File getCurrentlyOpenedFile() {
		return currentlyOpenedFile;
	}
	
	/**
	 * a list of all of the contacts that are currently in the address book.
	 * @return a list of all of the contacts that are currently in the address book.
	 */
	public ArrayList<Contact> getContacts() {
		return contacts;
	}

	/**
	 * Entry point to the program with any command line arguments.
	 * @param args Command line parameters.
	 */
	public static void main(String[] args) 
	{
		AddressBook a = new AddressBook();
	}
}