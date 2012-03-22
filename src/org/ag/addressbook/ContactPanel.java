package org.ag.addressbook;

import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.net.URI;
import java.util.ArrayList;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import org.ag.addressbook.property.Address;
import org.ag.addressbook.property.EmailAddress;
import org.ag.addressbook.property.TelephoneNumber;
import org.ag.util.undoredo.Action;

/**
 * ContactPanel
 * This panel is made to refresh or "repopulate" when the selected contact changes.
 * The vast number of ArrayLists are needed to edit the correct property of the contact.
 * @author Ashley Gwinnell
 */
public class ContactPanel extends JPanel
{
	// container?
	private AddressBook addressBook;
	
	// stuff for contact informationz
	private Contact currentContact;
	private JLabel lbl_fullname;
	
	private ArrayList<JTextField> addresses_po_box = new ArrayList<JTextField>();
	private ArrayList<JTextField> addresses_extended_address = new ArrayList<JTextField>();
	private ArrayList<JTextArea>  addresses_street_address = new ArrayList<JTextArea>();
	private ArrayList<JTextField> addresses_locality_city = new ArrayList<JTextField>();
	private ArrayList<JTextField> addresses_region_state_province_county = new ArrayList<JTextField>();
	private ArrayList<JTextField> addresses_postcode = new ArrayList<JTextField>();
	private ArrayList<JTextField> addresses_country = new ArrayList<JTextField>();
	private ArrayList<JButton> addresses_set_po_box = new ArrayList<JButton>();
	private ArrayList<JButton> addresses_set_extended_address = new ArrayList<JButton>();
	
	private ArrayList<JComboBox> addresses_type = new ArrayList<JComboBox>();
	private ArrayList<JButton> addresses_delete = new ArrayList<JButton>();
	
	private ArrayList<JTextField> telephoneNumbers = new ArrayList<JTextField>();
	private ArrayList<JComboBox> telephoneNumbers_type = new ArrayList<JComboBox>();
	private ArrayList<JButton> telephoneNumbers_delete = new ArrayList<JButton>();
	
	private ArrayList<JTextField> emailAddresses = new ArrayList<JTextField>();
	private ArrayList<JComboBox> emailAddresses_type = new ArrayList<JComboBox>();
	private ArrayList<JButton> emailAddresses_mailto = new ArrayList<JButton>();
	private ArrayList<JButton> emailAddresses_delete = new ArrayList<JButton>();
	
	// stuff for layout of contact informations!
	private JPanel pnl_fullname;
	private JPanel pnl_addresses;
	private JPanel pnl_telephoneNumbers;
	private JPanel pnl_emailAddresses;
	private JPanel pnl_buttons;
	
	// buttons
	private JButton btn_add_address;
	private JButton btn_add_telephoneNumber;
	private JButton btn_add_emailAddress;
	private JButton btn_edit_name;
	
	// width of general thing
	private final int w = 500;
	
	/**
	 * Create a new ContactPanel.
	 * TODO: Make it so the information is aligned at the top of it's parent.
	 * 		 setAlignmentX and setAlignmentY don't seem to work.
	 * @param addressbook The AddressBook instance.
	 */
	public ContactPanel(AddressBook addressbook) 
	{
		this.addressBook = addressbook;
		this.setAlignmentX(LEFT_ALIGNMENT);
		this.setAlignmentY(TOP_ALIGNMENT);
		GridBagConstraints c = new GridBagConstraints();
		this.setLayout(new GridBagLayout());
		
		c.gridx = 0;
		c.gridy = 0;
		
		pnl_fullname = new JPanel();
		pnl_fullname.setPreferredSize(new Dimension(w, 50));
		pnl_fullname.setLayout(null);
			lbl_fullname = new JLabel("<html><big>Address Book</big><br/><b>To set started, open a file or add a contact.</b></html>");
			lbl_fullname.setBounds(0, 0, 400, 50);
			pnl_fullname.add(lbl_fullname);
		this.add(pnl_fullname, c);
		
		c.gridx = 0;
		c.gridy = 1;
		pnl_addresses = new JPanel();
		pnl_addresses.setPreferredSize(new Dimension(w, 20));
		pnl_addresses.setLayout(null);
		pnl_addresses.setVisible(false);
		this.add(pnl_addresses, c);
		
		c.gridx = 0;
		c.gridy = 2;
		pnl_telephoneNumbers = new JPanel();
		pnl_telephoneNumbers.setPreferredSize(new Dimension(w, 20));
		pnl_telephoneNumbers.setLayout(null);
		pnl_telephoneNumbers.setVisible(false);
		this.add(pnl_telephoneNumbers, c);
		
		c.gridx = 0;
		c.gridy = 3;
		pnl_emailAddresses = new JPanel();
		pnl_emailAddresses.setPreferredSize(new Dimension(w, 20));
		pnl_emailAddresses.setLayout(null);
		pnl_emailAddresses.setVisible(false);
		this.add(pnl_emailAddresses, c);
		
		
		c.gridx = 0;
		c.gridy = 4;
		pnl_buttons = new JPanel();
		pnl_buttons.setPreferredSize(new Dimension(w, 60));
		pnl_buttons.setLayout(null);
		JLabel lbl_settings = new JLabel("Settings: ");
		lbl_settings.setBounds(0, 5, 100, 25);
		pnl_buttons.add(lbl_settings);
		btn_edit_name = new JButton("Edit Contact's Name");
		btn_edit_name.setBounds(150, 5, 200, 25);
		btn_edit_name.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				createAndShowEditNameDialog(addressBook.getFrame());
			}
		});
		pnl_buttons.add(btn_edit_name);
		pnl_buttons.setVisible(false);
		this.add(pnl_buttons, c);
		
		
		
	}
	
	@Override
	public void setEnabled(boolean enabled) {
		super.setEnabled(enabled);
		for (int i = 0; i < addresses_type.size(); i++) {
			try { addresses_po_box.get(i).setEnabled(enabled); } catch (IndexOutOfBoundsException e) { }
			try { addresses_extended_address.get(i).setEnabled(enabled); } catch (IndexOutOfBoundsException e) { }
			try { addresses_set_po_box.get(i).setVisible(enabled); } catch (IndexOutOfBoundsException e) { }
			try { addresses_set_extended_address.get(i).setVisible(enabled); } catch (IndexOutOfBoundsException e) { }
			addresses_street_address.get(i).setEnabled(enabled);
			addresses_locality_city.get(i).setEnabled(enabled);
			addresses_region_state_province_county.get(i).setEnabled(enabled);
			addresses_postcode.get(i).setEnabled(enabled);
			addresses_country.get(i).setEnabled(enabled);
			addresses_type.get(i).setEnabled(enabled);
			addresses_delete.get(i).setVisible(enabled);
		}
		for (int i = 0; i < telephoneNumbers.size(); i++) {
			telephoneNumbers.get(i).setEnabled(enabled);
			telephoneNumbers_type.get(i).setEnabled(enabled);
			telephoneNumbers_delete.get(i).setVisible(enabled);
		}
		for (int i = 0; i < emailAddresses.size(); i++) {
			emailAddresses.get(i).setEnabled(enabled);
			emailAddresses_type.get(i).setEnabled(enabled);
			emailAddresses_mailto.get(i).setVisible(enabled);
			emailAddresses_delete.get(i).setVisible(enabled);
		}
		btn_add_address.setVisible(enabled);
		btn_add_telephoneNumber.setVisible(enabled);
		btn_add_emailAddress.setVisible(enabled);
		
		pnl_buttons.setVisible(enabled);
		btn_edit_name.setVisible(enabled);
	}
	
	/**
	 * This should be called on every change to a contact's details.
	 * Whether something is deleted, or just the selected contact changes.
	 * This includes in the undo/redo stack.
	 * @param c
	 */
	public void repopulate(Contact c) 
	{
		addresses_po_box.clear();
		addresses_extended_address.clear();
		addresses_street_address.clear();
		addresses_locality_city.clear();
		addresses_region_state_province_county.clear();
		addresses_postcode.clear();
		addresses_country.clear();
		addresses_type.clear();
		addresses_delete.clear();
		telephoneNumbers.clear();
		telephoneNumbers_type.clear();
		telephoneNumbers_delete.clear();
		emailAddresses.clear();
		emailAddresses_type.clear();
		emailAddresses_mailto.clear();
		emailAddresses_delete.clear();
		pnl_addresses.removeAll();
		pnl_telephoneNumbers.removeAll();
		pnl_emailAddresses.removeAll();
		
		this.currentContact = c;
		
		if (c == null) {
			lbl_fullname.setText("<html><big>Address Book</big><br/><b>To set started, open a file or add a contact.</b></html>");
			pnl_addresses.setVisible(false);
			pnl_telephoneNumbers.setVisible(false);
			pnl_emailAddresses.setVisible(false);
			pnl_buttons.setVisible(false);
			return;
		} else {
			lbl_fullname.setText("<html><big>" + c.getForenames() + " " + c.getSurname() + "</big></html>");
			pnl_addresses.setVisible(true);
			pnl_telephoneNumbers.setVisible(true);
			pnl_emailAddresses.setVisible(true);
			pnl_buttons.setVisible(true);
		}
		//pnl_fullname.setPreferredSize(new Dimension(400, 35));
		//lbl_fullname.setBounds(0, 0, 400, 30);
	
		
		
		// fill addresses! :)
		JLabel lbl_addresses = new JLabel("Addresses: ");
		lbl_addresses.setBounds(0, 0, 120, 20);
		pnl_addresses.add(lbl_addresses);
		int current_x = 150;
		int current_y = 0;
		for (int i = 0; i < c.getAddresses().size(); i++) {
			final Address a = c.getAddresses().get(i);
	
			
			String[] types = {"Home", "Work"};
			final JComboBox box = new JComboBox(types);
			box.addFocusListener(new FocusListener() {
				int valueOnFocus;
				public Contact c;
				public void focusGained(FocusEvent e) {
					valueOnFocus = box.getSelectedIndex();
					c = currentContact;
				}
				public void focusLost(FocusEvent e) {
					if (box.getSelectedIndex() != valueOnFocus) { // changes!!
						
						addressBook.getActionStack().push(new Action() {
							public Address ad = a;
							public int adtp = valueOnFocus;
							public int adt = box.getSelectedIndex();
							public boolean secondPass = false;
							public void doAction() {
								if (adt == 0) {
									ad.setType(Address.Type.HOME);
								} else if (adt == 1) {
									ad.setType(Address.Type.WORK);
								}
								if (secondPass) { addressBook.getContactPanel().repopulate(c); }
								addressBook.getFrame().validate();
								secondPass = true;
							}
							public String getRedoText() {
								if (adt == 0) {
									return "Set Address Type As 'Home'";
								} else if (adt == 1) {
									return "Set Address Type As 'Work'";
								}
								return "BAD BAD BAD";
							}
							public String getUndoText() {
								if (adtp == 0) {
									return "Set Address Type As 'Home'";
								} else if (adtp == 1) {
									return "Set Address Type As 'Work'";
								}
								return "BAD BAD BAD!";
							}
							public void undoAction() {
								if (adtp == 0) {
									ad.setType(Address.Type.HOME);
								} else if (adtp == 1) {
									ad.setType(Address.Type.WORK);
								}
								addressBook.getContactPanel().repopulate(c);
								addressBook.getFrame().validate();
							} 	
						});
					}
				} 
			});
			box.setBounds(current_x + 205, current_y, 100, 25);
			if (a.getType().equals(Address.Type.HOME)) {
				box.setSelectedIndex(0);
			} else if (a.getType().equals(Address.Type.WORK)) {
				box.setSelectedIndex(1);
			}
			
			JButton delete = new JButton(new ImageIcon(getClass().getClassLoader().getResource("files/Delete.png")));
			delete.setToolTipText("Remove Address");
			delete.setBounds(current_x + 205 + 105, current_y, 25, 25);
			delete.addActionListener(new ActionListener() { 
				public void actionPerformed(ActionEvent e) {
					addressBook.getActionStack().push(new Action() {
						public Contact c = currentContact;
						public Address ad = a;
						public void doAction() {
							c.removeAddress(ad);
							addressBook.getContactPanel().repopulate(currentContact);
							addressBook.getFrame().validate();
						}
						public String getRedoText() {
							return "Remove Address from " + c.getName();
						}
						public String getUndoText() {
							return "Add Address to " + c.getName();
						}
						public void undoAction() {
							c.addAddress(ad);
							addressBook.getContactPanel().repopulate(currentContact);
							addressBook.getFrame().validate();
						}
					});
				}
			});
			
			
			//addresses.add(area);
			addresses_type.add(box);
			addresses_delete.add(delete);
			
			//pnl_addresses.add(pane);
			pnl_addresses.add(box);
			pnl_addresses.add(delete);
			
			if (!c.getAddresses().get(i).getPOBoxNumber().trim().equals("")) {
				JLabel lbl_number = new JLabel("PO Box #: ");
				lbl_number.setBounds(current_x - 70, current_y, 80, 20);
				pnl_addresses.add(lbl_number);
				
				final JTextField po_box = new JTextField();
				po_box.addFocusListener(null);
				po_box.setBounds(current_x, current_y, 50, 25);
				po_box.setText(c.getAddresses().get(i).getPOBoxNumber());
				po_box.addFocusListener(new FocusListener() {
					public String valueOnFocus;
					public Contact c;
					public void focusGained(FocusEvent e) {
						c = currentContact;
						valueOnFocus = po_box.getText();
					}
					public void focusLost(FocusEvent e) {
						if (!po_box.getText().equals(valueOnFocus)) {
							addressBook.getActionStack().push(new Action() {
								public Address ad = a;
								public String ad_str_previous = valueOnFocus;
								public String ad_str = po_box.getText();
								public boolean secondPass = false;
								public void doAction() {
									ad.setPOBoxNumber(ad_str);
									if (secondPass) { addressBook.getContactPanel().repopulate(c); }
									addressBook.getFrame().validate();
									secondPass = true;
								}
								public String getRedoText() {
									return "Change PO Box Number";
								}
								public String getUndoText() {
									return "Change PO Box Number";
								}
								public void undoAction() {
									ad.setPOBoxNumber(ad_str_previous);
									addressBook.getContactPanel().repopulate(c);
									addressBook.getFrame().validate();
								} 	
							});
						}
					}
				});
				
				addresses_po_box.add(po_box);
				pnl_addresses.add(po_box);
				current_y += 30;
			} else {
				int x = addresses_type.get(i).getX();
				int y = addresses_type.get(i).getY() + 28;
				JButton button = new JButton("Set POBox Number");
				button.setToolTipText("Set A PO BOX Number for this address.");
				button.setBounds(x, y, 130, 25);
				button.addActionListener(new ActionListener() { 
					public Address ad = a;
					public void actionPerformed(ActionEvent e) {
						createAndShowSetPOBoxNumberDialog(addressBook.getFrame(), ad);
					}
				});
				addresses_set_po_box.add(button);
				pnl_addresses.add(button);
			}
			
			if (!c.getAddresses().get(i).getExtendedAddress().trim().equals("")) {
				JLabel lbl_extended_address = new JLabel("Business: ");
				lbl_extended_address.setBounds(current_x - 70, current_y, 60, 20);
				pnl_addresses.add(lbl_extended_address);
				
				final JTextField extended_address = new JTextField();
				extended_address.addFocusListener(null);
				extended_address.setBounds(current_x, current_y, 200, 25);
				extended_address.setText(c.getAddresses().get(i).getExtendedAddress());
				extended_address.addFocusListener(new FocusListener() {
					public String valueOnFocus;
					public Contact c;
					public void focusGained(FocusEvent e) {
						c = currentContact;
						valueOnFocus = extended_address.getText();
					}
					public void focusLost(FocusEvent e) {
						if (!extended_address.getText().equals(valueOnFocus)) {
							addressBook.getActionStack().push(new Action() {
								public Address ad = a;
								public String ad_str_previous = valueOnFocus;
								public String ad_str = extended_address.getText();
								public boolean secondPass = false;
								public void doAction() {
									ad.setExtendedAddress(ad_str);
									if (secondPass) { addressBook.getContactPanel().repopulate(c); }
									addressBook.getFrame().validate();
									secondPass = true;
								}
								public String getRedoText() {
									return "Change Business Name";
								}
								public String getUndoText() {
									return "Change Business Name";
								}
								public void undoAction() {
									ad.setExtendedAddress(ad_str_previous);
									addressBook.getContactPanel().repopulate(c);
									addressBook.getFrame().validate();
								} 	
							});
						}
					}
				});
				addresses_extended_address.add(extended_address);
				pnl_addresses.add(extended_address);
				current_y += 30;
			} else {
				int x = addresses_type.get(i).getX();
				int y = addresses_type.get(i).getY() + 28;
				if (c.getAddresses().get(i).getPOBoxNumber().trim().equals("")) { y += 28; }
				JButton button = new JButton("Set Business Name");
				button.setToolTipText("Set A Business Name for this address.");
				button.setBounds(x, y, 130, 25);
				button.addActionListener(new ActionListener() { 
					public Address ad = a;
					public void actionPerformed(ActionEvent e) {
						createAndShowSetExtendedAddressDialog(addressBook.getFrame(), ad);
					}
				});
				addresses_set_extended_address.add(button);
				pnl_addresses.add(button);
			}
			
			JLabel lbl_street_address = new JLabel("Lines: ");
			lbl_street_address.setBounds(current_x - 70, current_y, 60, 20);
			pnl_addresses.add(lbl_street_address);
			
			final JTextArea street_address = new JTextArea();
			street_address.setFont(new Font("Arial", Font.PLAIN, 12));
			street_address.setLineWrap(true);
			street_address.setWrapStyleWord(true);
			street_address.addFocusListener(null);
			street_address.setText(c.getAddresses().get(i).getStreetAddress());
			street_address.addFocusListener(new FocusListener() {
				public String valueOnFocus;
				public Contact c;
				public void focusGained(FocusEvent e) {
					c = currentContact;
					valueOnFocus = street_address.getText();
				}
				public void focusLost(FocusEvent e) {
					if (!street_address.getText().equals(valueOnFocus)) {
						addressBook.getActionStack().push(new Action() {
							public Address ad = a;
							public String ad_str_previous = valueOnFocus;
							public String ad_str = street_address.getText();
							public boolean secondPass = false;
							public void doAction() {
								ad.setStreetAddress(ad_str);
								if (secondPass) { addressBook.getContactPanel().repopulate(c); }
								addressBook.getFrame().validate();
								secondPass = true;
							}
							public String getRedoText() {
								return "Change Street Address";
							}
							public String getUndoText() {
								return "Change Street Address";
							}
							public void undoAction() {
								ad.setStreetAddress(ad_str_previous);
								addressBook.getContactPanel().repopulate(c);
								addressBook.getFrame().validate();
							} 	
						});
					}
				}
			});
			
			JScrollPane street_address_scroll = new JScrollPane(street_address);
			street_address_scroll.setBounds(current_x, current_y, 200, 70);
			
			
			addresses_street_address.add(street_address);
			pnl_addresses.add(street_address_scroll);
			current_y += 75;
			
			JLabel lbl_city = new JLabel("City: ");
			lbl_city.setBounds(current_x - 70, current_y, 60, 20);
			pnl_addresses.add(lbl_city);
			
			final JTextField city = new JTextField();
			city.addFocusListener(new FocusListener() {
				public String valueOnFocus;
				public Contact c;
				public void focusGained(FocusEvent e) {
					c = currentContact;
					valueOnFocus = city.getText();
				}
				public void focusLost(FocusEvent e) {
					if (!city.getText().equals(valueOnFocus)) {
						addressBook.getActionStack().push(new Action() {
							public Address ad = a;
							public String ad_str_previous = valueOnFocus;
							public String ad_str = city.getText();
							public boolean secondPass = false;
							public void doAction() {
								ad.setCity(ad_str);
								if (secondPass) { addressBook.getContactPanel().repopulate(c); }
								addressBook.getFrame().validate();
								secondPass = true;
							}
							public String getRedoText() {
								return "Change City";
							}
							public String getUndoText() {
								return "Change City";
							}
							public void undoAction() {
								ad.setCity(ad_str_previous);
								addressBook.getContactPanel().repopulate(c);
								addressBook.getFrame().validate();
							} 	
						});
					}
				}
			});
			city.setBounds(current_x, current_y, 200, 25);
			city.setText(c.getAddresses().get(i).getCity());
			
			addresses_locality_city.add(city);
			pnl_addresses.add(city);
			current_y += 30;
			
			JLabel lbl_county = new JLabel("County: ");
			lbl_county.setBounds(current_x - 70, current_y, 60, 20);
			pnl_addresses.add(lbl_county);
			
			final JTextField county = new JTextField();
			county.addFocusListener(new FocusListener() {
				public String valueOnFocus;
				public Contact c;
				public void focusGained(FocusEvent e) {
					c = currentContact;
					valueOnFocus = county.getText();
				}
				public void focusLost(FocusEvent e) {
					if (!county.getText().equals(valueOnFocus)) {
						addressBook.getActionStack().push(new Action() {
							public Address ad = a;
							public String ad_str_previous = valueOnFocus;
							public String ad_str = county.getText();
							public boolean secondPass = false;
							public void doAction() {
								ad.setCounty(ad_str);
								if (secondPass) { addressBook.getContactPanel().repopulate(c); }
								addressBook.getFrame().validate();
								secondPass = true;
							}
							public String getRedoText() {
								return "Change County";
							}
							public String getUndoText() {
								return "Change County";
							}
							public void undoAction() {
								ad.setCounty(ad_str_previous);
								addressBook.getContactPanel().repopulate(c);
								addressBook.getFrame().validate();
							} 	
						});
					}
				}
			});
			county.setBounds(current_x, current_y, 200, 25);
			county.setText(c.getAddresses().get(i).getCounty());
			
			addresses_region_state_province_county.add(county);
			pnl_addresses.add(county);
			current_y += 30;
			
			JLabel lbl_postcode = new JLabel("Postcode: ");
			lbl_postcode.setBounds(current_x - 70, current_y, 60, 20);
			pnl_addresses.add(lbl_postcode);
			
			final JTextField postcode = new JTextField();
			postcode.addFocusListener(new FocusListener() {
				public String valueOnFocus;
				public Contact c;
				public void focusGained(FocusEvent e) {
					c = currentContact;
					valueOnFocus = postcode.getText();
				}
				public void focusLost(FocusEvent e) {
					if (!postcode.getText().equals(valueOnFocus)) {
						addressBook.getActionStack().push(new Action() {
							public Address ad = a;
							public String ad_str_previous = valueOnFocus;
							public String ad_str = postcode.getText();
							public boolean secondPass = false;
							public void doAction() {
								ad.setPostcode(ad_str);
								if (secondPass) { addressBook.getContactPanel().repopulate(c); }
								addressBook.getFrame().validate();
								secondPass = true;
							}
							public String getRedoText() {
								return "Change Postcode";
							}
							public String getUndoText() {
								return "Change Postcode";
							}
							public void undoAction() {
								ad.setPostcode(ad_str_previous);
								addressBook.getContactPanel().repopulate(c);
								addressBook.getFrame().validate();
							} 	
						});
					}
				}
			});
			postcode.setBounds(current_x, current_y, 200, 25);
			postcode.setText(c.getAddresses().get(i).getPostcode());
			
			addresses_postcode.add(postcode);
			pnl_addresses.add(postcode);
			current_y += 30;
			
			JLabel lbl_country = new JLabel("Country: ");
			lbl_country.setBounds(current_x - 70, current_y, 60, 20);
			pnl_addresses.add(lbl_country);
			
			final JTextField country = new JTextField();
			country.addFocusListener(new FocusListener() {
				public String valueOnFocus;
				public Contact c;
				public void focusGained(FocusEvent e) {
					c = currentContact;
					valueOnFocus = country.getText();
				}
				public void focusLost(FocusEvent e) {
					if (!country.getText().equals(valueOnFocus)) {
						addressBook.getActionStack().push(new Action() {
							public Address ad = a;
							public String ad_str_previous = valueOnFocus;
							public String ad_str = country.getText();
							public boolean secondPass = false;
							public void doAction() {
								ad.setCountry(ad_str);
								if (secondPass) { addressBook.getContactPanel().repopulate(c); }
								addressBook.getFrame().validate();
								secondPass = true;
							}
							public String getRedoText() {
								return "Change Country";
							}
							public String getUndoText() {
								return "Change Country";
							}
							public void undoAction() {
								ad.setPostcode(ad_str_previous);
								addressBook.getContactPanel().repopulate(c);
								addressBook.getFrame().validate();
							} 	
						});
					}
				}
			});
			country.setBounds(current_x, current_y, 200, 25);
			country.setText(c.getAddresses().get(i).getCountry());
			
			addresses_country.add(country);
			pnl_addresses.add(country);
			current_y += 30;
			
			if (i != c.getAddresses().size()-1) {
				current_y += 30;
			}
			
			
			
			
			
		}
		btn_add_address = new JButton("Add Address");
		btn_add_address.setBounds(current_x, current_y, 150, 25);
		btn_add_address.addActionListener(new ActionListener() { public void actionPerformed(ActionEvent e) { 
			createAndShowAddAddressDialog(addressBook.getFrame());
		}});
		pnl_addresses.add(btn_add_address);
		current_y += 40;
		pnl_addresses.setPreferredSize(new Dimension(w, current_y));
		pnl_addresses.validate();
		
		
		
		
		
		// fill telephone numbers! :)
		JLabel lbl_telephoneNumbers = new JLabel("Telephone Numbers: ");
		lbl_telephoneNumbers.setBounds(0, 0, 120, 20);
		pnl_telephoneNumbers.add(lbl_telephoneNumbers);
		current_x = 150;
		current_y = 0;
		for (int i = 0; i < c.getTelephoneNumbers().size(); i++) {
			final TelephoneNumber tn = c.getTelephoneNumbers().get(i);
			
			final JTextField field = new JTextField();
			field.setBounds(current_x, current_y, 200, 25);
			field.setText(c.getTelephoneNumbers().get(i).getNumber());
			field.addFocusListener(new FocusListener() {
				public String valueOnFocus; 
				public Contact c;
				public void focusGained(FocusEvent e) {
					valueOnFocus = field.getText();
					c = currentContact;
				}
				public void focusLost(final FocusEvent e) {
					if (!field.getText().equals(valueOnFocus)) {
						addressBook.getActionStack().push(new Action() {
							public TelephoneNumber t = tn;
							public String t_str_previous = valueOnFocus;
							public String t_str = field.getText();
							public boolean secondPass = false;
							public void doAction() {
								t.setNumber(t_str);
								if (secondPass) { addressBook.getContactPanel().repopulate(c); }
								addressBook.getFrame().validate();
								secondPass = true;
							}
							public String getRedoText() {
								return "Change " + c.getName() + "'s Telephone Number To " + t_str;
							}
							public String getUndoText() {
								return "Change " + c.getName() + "'s Telephone Number Back To " + t_str_previous;
							}
							public void undoAction() {
								t.setNumber(t_str_previous);
								addressBook.getContactPanel().repopulate(c);
								addressBook.getFrame().validate();
							} 	
						});
					}
				}  
			});
			
			String[] types = {"Home", "Mobile / Cell", "Work"};
			final JComboBox box = new JComboBox(types);
			box.setBounds(current_x + 205, current_y, 100, 25);
			if (tn.getType().equals(TelephoneNumber.Type.HOME)) {
				box.setSelectedIndex(0);
			} else if (tn.getType().equals(TelephoneNumber.Type.CELL)) {
				box.setSelectedIndex(1);
			} else if (tn.getType().equals(TelephoneNumber.Type.WORK)) {
				box.setSelectedIndex(2);
			}
			box.addFocusListener(new FocusListener() {
				int valueOnFocus;
				public Contact c;
				public void focusGained(FocusEvent e) {
					valueOnFocus = box.getSelectedIndex();
					c = currentContact;
				}
				public void focusLost(FocusEvent e) {
					if (box.getSelectedIndex() != valueOnFocus) { // changes!!
						
						addressBook.getActionStack().push(new Action() {
							public TelephoneNumber t = tn;
							public int telnumber_type_previous = valueOnFocus;
							public int telnumber_type = box.getSelectedIndex();
							public boolean secondPass = false;
							public void doAction() {
								if (telnumber_type == 0) {
									t.setType(TelephoneNumber.Type.HOME);
								} else if (telnumber_type == 1) {
									t.setType(TelephoneNumber.Type.CELL);
								} else if (telnumber_type == 2) {
									t.setType(TelephoneNumber.Type.WORK);
								}
								if (secondPass) { addressBook.getContactPanel().repopulate(c); }
								addressBook.getFrame().validate();
								secondPass = true;
							}
							public String getRedoText() {
								if (telnumber_type == 0) {
									return "Set Telephone Number Type As 'Home'";
								} else if (telnumber_type == 1) {
									return "Set Telephone Number Type As 'Mobile / Cell'";
								} else if (telnumber_type == 2) {
									return "Set Telephone Number Type As 'Work'";
								}
								return "BAD BAD BAD";
							}
							public String getUndoText() {
								if (telnumber_type_previous == 0) {
									return "Set Telephone Number Type As 'Home'";
								} else if (telnumber_type_previous == 1) {
									return "Set Telephone Number Type As 'Mobile / Cell'";
								} else if (telnumber_type == 2) {
									return "Set Telephone Number Type As 'Work'";
								}
								return "BAD BAD BAD!";
							}
							public void undoAction() {
								if (telnumber_type_previous == 0) {
									t.setType(TelephoneNumber.Type.HOME);
								} else if (telnumber_type_previous == 1) {
									t.setType(TelephoneNumber.Type.CELL);
								} else if (telnumber_type_previous == 2) {
									t.setType(TelephoneNumber.Type.WORK);
								}
								addressBook.getContactPanel().repopulate(c);
								addressBook.getFrame().validate();
							} 	
						});
					}
				} 
			});
			
			JButton delete = new JButton(new ImageIcon(getClass().getClassLoader().getResource("files/Delete.png")));
			delete.setToolTipText("Remove Telephone Number");
			delete.setBounds(current_x + 205 + 105, current_y, 25, 25);
			delete.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					addressBook.getActionStack().push(new Action() {
						public Contact c = currentContact;
						public TelephoneNumber t = tn;
						public void doAction() {
							c.removeTelephoneNumber(t);
							addressBook.getContactPanel().repopulate(currentContact);
							addressBook.getFrame().validate();
						}
						public String getRedoText() {
							return "Remove Telephone Number from " + c.getName();
						}
						public String getUndoText() {
							return "Add Telephone Number to " + c.getName();
						}
						public void undoAction() {
							c.addTelephoneNumber(t);
							addressBook.getContactPanel().repopulate(currentContact);
							addressBook.getFrame().validate();
						}
					});
				} 
			});
			telephoneNumbers.add(field);
			telephoneNumbers_type.add(box);
			telephoneNumbers_delete.add(delete);
			
			pnl_telephoneNumbers.add(field);
			pnl_telephoneNumbers.add(box);
			pnl_telephoneNumbers.add(delete);
			
			current_y += 30;
		}
		btn_add_telephoneNumber = new JButton("Add Telephone Number");
		btn_add_telephoneNumber.setBounds(current_x, current_y, 150, 25);
		btn_add_telephoneNumber.addActionListener(new ActionListener() { public void actionPerformed(ActionEvent e) { 
			createAndShowAddTelephoneNumberDialog(addressBook.getFrame());
		}});
		pnl_telephoneNumbers.add(btn_add_telephoneNumber);
		current_y += 40;
		pnl_telephoneNumbers.setPreferredSize(new Dimension(w, current_y));
		pnl_telephoneNumbers.validate();
		
		
		
		
		
		// email addressz? wut?
		JLabel lbl_emailAddresses = new JLabel("Email Addresses: ");
		lbl_emailAddresses.setBounds(0, 0, 120, 20);
		pnl_emailAddresses.add(lbl_emailAddresses);
		current_x = 150;
		current_y = 0;
		for (int i = 0; i < c.getEmailAddresses().size(); i++) {
			final EmailAddress ea = c.getEmailAddresses().get(i);
			final JTextField field = new JTextField();
			field.setBounds(current_x, current_y, 200, 25);
			field.setText(c.getEmailAddresses().get(i).getAddress());
			field.addFocusListener(new FocusListener() {
				public String valueOnFocus; 
				public Contact c;
				public void focusGained(FocusEvent e) {
					valueOnFocus = field.getText();
					c = currentContact;
				}
				public void focusLost(final FocusEvent event) {
					if (!field.getText().equals(valueOnFocus)) {
						addressBook.getActionStack().push(new Action() {
							public EmailAddress e = ea;
							public String email_str_previous = valueOnFocus;
							public String email_str = field.getText();
							public boolean secondPass = false;
							public void doAction() {
								e.setAddress(email_str);
								if (secondPass) { addressBook.getContactPanel().repopulate(c); }
								addressBook.getFrame().validate();
								secondPass = true;
							}
							public String getRedoText() {
								return "Change Email Address To " + email_str;
							}
							public String getUndoText() {
								return "Change Email Address Back To " + email_str_previous;
							}
							public void undoAction() {
								e.setAddress(email_str_previous);
								addressBook.getContactPanel().repopulate(c);
								addressBook.getFrame().validate();
							} 	
						});
					}
				}  
			});
			
			
			String[] types = {"Personal", "Corporate"};
			final JComboBox box = new JComboBox(types);
			box.setBounds(current_x + 205, current_y, 80, 25);
			if (c.getEmailAddresses().get(i).getType().equals(EmailAddress.Type.HOME)) {
				box.setSelectedIndex(0);
			} else if (c.getEmailAddresses().get(i).getType().equals(EmailAddress.Type.WORK)) {
				box.setSelectedIndex(1);
			}
			box.addFocusListener(new FocusListener() {
				int valueOnFocus;
				public Contact c;
				public void focusGained(FocusEvent e) {
					valueOnFocus = box.getSelectedIndex();
					c = currentContact;
				}
				public void focusLost(FocusEvent event) {
					if (box.getSelectedIndex() != valueOnFocus) { // changes!!
						addressBook.getActionStack().push(new Action() {
							public EmailAddress e = ea;
							public int email_type_previous = valueOnFocus;
							public int email_type = box.getSelectedIndex();
							public boolean secondPass = false;
							public void doAction() {
								if (email_type == 0) {
									e.setType(EmailAddress.Type.HOME);
								} else if (email_type == 1) {
									e.setType(EmailAddress.Type.WORK);
								}
								if (secondPass) { addressBook.getContactPanel().repopulate(c); }
								addressBook.getFrame().validate();
								secondPass = true;
							}
							public String getRedoText() {
								if (email_type == 0) {
									return "Set Email Type As 'Personal'";
								} else if (email_type == 1) {
									return "Set Email Type As 'Corporate'";
								} 
								return "BAD BAD BAD";
							}
							public String getUndoText() {
								if (email_type_previous == 0) {
									return "Set Email Type As 'Personal'";
								} else if (email_type_previous == 1) {
									return "Set Email Type As 'Corporate'";
								} 
								return "BAD BAD BAD!";
							}
							public void undoAction() {
								if (email_type_previous == 0) {
									e.setType(EmailAddress.Type.HOME);
								} else if (email_type_previous == 1) {
									e.setType(EmailAddress.Type.WORK);
								} 
								addressBook.getContactPanel().repopulate(c);
								addressBook.getFrame().validate();
							} 	
						});
					}
				} 
			});
			
			final JButton mailto = new JButton(new ImageIcon(getClass().getClassLoader().getResource("files/Mail.png")));
			mailto.setToolTipText("Send An Email To " + ea.getAddress());
			mailto.setBounds(current_x + 205 + 85, current_y, 25, 25);
			mailto.addActionListener(new ActionListener() { 
				public void actionPerformed(ActionEvent e) {
					try {
						Desktop desktop = Desktop.getDesktop();
						desktop.mail(new URI("mailto:" + ea.getAddress()));
					} catch (Exception ex) {
						JOptionPane.showMessageDialog(addressBook.getFrame(), "Unable to launch default mail application.", "Error:", JOptionPane.ERROR_MESSAGE);
					}	
				}
			});
			
			final JButton delete = new JButton(new ImageIcon(getClass().getClassLoader().getResource("files/Delete.png")));
			delete.setToolTipText("Remove Email Address");
			delete.setBounds(current_x + 205 + 85 + 30, current_y, 25, 25);
			delete.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					addressBook.getActionStack().push(new Action() {
						public Contact c = currentContact;
						public EmailAddress e = ea;
						public void doAction() {
							c.removeEmailAddress(e);
							addressBook.getContactPanel().repopulate(currentContact);
							addressBook.getFrame().validate();
						}
						public String getRedoText() {
							return "Remove Email Address from " + c.getName();
						}
						public String getUndoText() {
							return "Add Email Address to " + c.getName();
						}
						public void undoAction() {
							c.addEmailAddress(e);
							addressBook.getContactPanel().repopulate(currentContact);
							addressBook.getFrame().validate();
						}
					});
				} 
			});
			
			emailAddresses.add(field);
			emailAddresses_type.add(box);
			emailAddresses_mailto.add(mailto);
			emailAddresses_delete.add(delete);
			
			pnl_emailAddresses.add(field);
			pnl_emailAddresses.add(box);
			pnl_emailAddresses.add(mailto);
			pnl_emailAddresses.add(delete);
			
			current_y += 30;
		}
		btn_add_emailAddress = new JButton("Add Email Address");
		btn_add_emailAddress.setBounds(current_x, current_y, 150, 25);
		btn_add_emailAddress.addActionListener(new ActionListener() { public void actionPerformed(ActionEvent e) { 
			createAndShowAddEmailAddressDialog(addressBook.getFrame());
		}});
		pnl_emailAddresses.add(btn_add_emailAddress);
		current_y += 40;
		pnl_emailAddresses.setPreferredSize(new Dimension(w, current_y));
		pnl_emailAddresses.validate();
		
		pnl_buttons.setVisible(true);
	}
	
	/**
	 * Add a POBox number to a contact's address.
	 * @param parent The Parent Frame.
	 * @param ad The Address to add the POBox number to.
	 */
	protected void createAndShowSetPOBoxNumberDialog(JFrame parent, final Address ad) {
		final JDialog frame = new JDialog(parent, "Add PO BOX:", true, null);
		frame.setSize(330, 100);
		frame.setLayout(null);
		frame.setResizable(false);
		frame.setIconImage(new ImageIcon(getClass().getClassLoader().getResource("files/Home.png")).getImage());
		frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		frame.addWindowListener(new WindowListener() {
			public void windowActivated(WindowEvent e) {}
			public void windowClosed(WindowEvent e) {}
			public void windowClosing(WindowEvent e) {
				addressBook.getFrame().setEnabled(true);
				frame.dispose();
			}
			public void windowDeactivated(WindowEvent e) {}
			public void windowDeiconified(WindowEvent e) {}
			public void windowIconified(WindowEvent e) {}
			public void windowOpened(WindowEvent e) {} 
		});
		frame.setLocationRelativeTo(parent);
		
		JPanel panel = new JPanel();
		panel.setLayout(null);
		panel.setBounds(10, 10, 300, 100);
			
		JLabel label = new JLabel("PO Box: ");
		label.setBounds(0, 0, 100, 20);
		panel.add(label);
		
		final JTextField textfield = new JTextField("");
		textfield.setBounds(100, 0, 200, 20);
		panel.add(textfield);
		
		JButton btn_save = new JButton("Save");
		btn_save.setBounds(0, 30, 300, 25);
		btn_save.addActionListener(new ActionListener() { 
			public void actionPerformed(ActionEvent e) {
				addressBook.getActionStack().push(new Action() {
					Contact c = currentContact;
					Address a = ad;
					String POBOX = textfield.getText();
					public void doAction() {
						a.setPOBoxNumber(POBOX);
						addressBook.getContactPanel().repopulate(c);
						addressBook.getFrame().validate();
					}
					public String getRedoText() {
						return "Add PO Box # (" + c.getName() + ")";
					}
					public String getUndoText() {
						return "Remove PO Box # (" + c.getName() + ")";
					}
					public void undoAction() {
						a.setPOBoxNumber("");
						addressBook.getContactPanel().repopulate(c);
						addressBook.getFrame().validate();
					}
				});
				addressBook.getFrame().setEnabled(true);
				frame.dispose();
			}
		});
		panel.add(btn_save);
		
		frame.add(panel);
		frame.setVisible(true);
	}

	/**
	 * Add an Extended Address to a contact's address.
	 * @param parent The Parent Frame.
	 * @param ad The Address to add the Extended Address to.
	 */
	protected void createAndShowSetExtendedAddressDialog(JFrame parent, final Address ad) {
		final JDialog frame = new JDialog(parent, "Add Business:", true, null);
		frame.setSize(330, 100);
		frame.setLayout(null);
		frame.setResizable(false);
		frame.setIconImage(new ImageIcon(getClass().getClassLoader().getResource("files/Home.png")).getImage());
		frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		frame.addWindowListener(new WindowListener() {
			public void windowActivated(WindowEvent e) {}
			public void windowClosed(WindowEvent e) {}
			public void windowClosing(WindowEvent e) {
				addressBook.getFrame().setEnabled(true);
				frame.dispose();
			}
			public void windowDeactivated(WindowEvent e) {}
			public void windowDeiconified(WindowEvent e) {}
			public void windowIconified(WindowEvent e) {}
			public void windowOpened(WindowEvent e) {} 
		});
		frame.setLocationRelativeTo(parent);
		
		JPanel panel = new JPanel();
		panel.setLayout(null);
		panel.setBounds(10, 10, 300, 100);
			
		JLabel label = new JLabel("Business Name: ");
		label.setBounds(0, 0, 100, 20);
		panel.add(label);
		
		final JTextField textfield = new JTextField("");
		textfield.setBounds(100, 0, 200, 20);
		panel.add(textfield);
		
		JButton btn_save = new JButton("Save");
		btn_save.setBounds(0, 30, 300, 25);
		btn_save.addActionListener(new ActionListener() { 
			public void actionPerformed(ActionEvent e) {
				addressBook.getActionStack().push(new Action() {
					Contact c = currentContact;
					Address a = ad;
					String extendedaddress = textfield.getText();
					public void doAction() {
						a.setExtendedAddress(extendedaddress);
						addressBook.getContactPanel().repopulate(c);
						addressBook.getFrame().validate();
					}
					public String getRedoText() {
						return "Add Business Name (" + c.getName() + ")";
					}
					public String getUndoText() {
						return "Remove Business Name (" + c.getName() + ")";
					}
					public void undoAction() {
						a.setExtendedAddress("");
						addressBook.getContactPanel().repopulate(c);
						addressBook.getFrame().validate();
					}
				});
				addressBook.getFrame().setEnabled(true);
				frame.dispose();
			}
		});
		panel.add(btn_save);
		
		frame.add(panel);
		frame.setVisible(true);
	}

	/**
	 * Add an address to the currently selected. contact.
	 * @param parent The parent window/frame.
	 */
	protected void createAndShowAddAddressDialog(JFrame parent) {
		final JDialog add_address_dialog = new JDialog(parent, "Add Address: ", true, null);
		add_address_dialog.setIconImage(new ImageIcon(getClass().getClassLoader().getResource("files/Home.png")).getImage());
		add_address_dialog.setSize(330, 330);
		add_address_dialog.setLayout(null);
		add_address_dialog.setResizable(false);
		add_address_dialog.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		
		
		JPanel panel_add_address = new JPanel();
		panel_add_address.setLayout(null);
		panel_add_address.setBounds(10, 10, 300, 295);
			
			JLabel lbl_add_address = new JLabel("Street: ");
			lbl_add_address.setBounds(0, 0, 100, 20);
			panel_add_address.add(lbl_add_address);
			
			final JTextArea ta_street = new JTextArea();
			ta_street.setText("");
			ta_street.setFont(new Font("Arial", Font.PLAIN, 12));
			ta_street.setLineWrap(true);
			ta_street.setWrapStyleWord(true);
			JScrollPane sp_add_address = new JScrollPane(ta_street);
			sp_add_address.setBounds(100, 0, 200, 100);
			panel_add_address.add(sp_add_address);
			
			
			JLabel lbl_city = new JLabel("City: ");
			lbl_city.setBounds(0, 105, 100, 20);
			panel_add_address.add(lbl_city);
			final JTextField ta_city = new JTextField();
			ta_city.setBounds(100, 105, 200, 25);
			panel_add_address.add(ta_city);
			
			
			JLabel lbl_county = new JLabel("County: ");
			lbl_county.setBounds(0, 135, 100, 20);
			panel_add_address.add(lbl_county);
			final JTextField ta_county = new JTextField();
			ta_county.setBounds(100, 135, 200, 25);
			panel_add_address.add(ta_county);
			
			JLabel lbl_postcode = new JLabel("Postcode: ");
			lbl_postcode.setBounds(0, 165, 100, 20);
			panel_add_address.add(lbl_postcode);
			final JTextField ta_postcode = new JTextField();
			ta_postcode.setBounds(100, 165, 200, 25);
			panel_add_address.add(ta_postcode);
			
			JLabel lbl_country = new JLabel("Country: ");
			lbl_country.setBounds(0, 195, 100, 20);
			panel_add_address.add(lbl_country);
			final JTextField ta_country = new JTextField();
			ta_country.setBounds(100, 195, 200, 25);
			panel_add_address.add(ta_country);
			
			String[] types = { "Home", "Work"};
			JLabel lbl_add_addresstype = new JLabel("Type: ");
			lbl_add_addresstype.setBounds(0, 225, 100, 25);
			panel_add_address.add(lbl_add_addresstype);
			final JComboBox box_addresstype = new JComboBox(types);
			box_addresstype.setSelectedIndex(0);
			box_addresstype.setBounds(100, 225, 200, 25);
			panel_add_address.add(box_addresstype);
			
			JButton btn_add_address = new JButton("Save");
			btn_add_address.setBounds(0, 260, 300, 25);
			btn_add_address.addActionListener(new ActionListener() { 
				public void actionPerformed(ActionEvent e) {
					addressBook.getActionStack().push(new Action() {
						Contact c = currentContact;
						Address a = new Address();
						String street = ta_street.getText();
						String city = ta_city.getText();
						String county = ta_county.getText();
						String postcode = ta_postcode.getText();
						String country = ta_country.getText();
						int index = box_addresstype.getSelectedIndex();
						
						public void doAction() {
							if (index == 0) {
								a.setType(Address.Type.HOME);
							} else if (index == 1) {
								a.setType(Address.Type.WORK);
							}
							
							a.setStreetAddress(street);
							a.setCity(city);
							a.setCounty(county);
							a.setPostcode(postcode);
							a.setCountry(country);
							c.addAddress(a);
							addressBook.getContactPanel().repopulate(c);
							addressBook.getFrame().validate();
							addressBook.getFrame().setEnabled(true);
						}
						public String getRedoText() {
							return "Add Address to " + c.getName();
						}
						public String getUndoText() {
							return "Remove Address from " + c.getName();
						}
						public void undoAction() {
							c.removeAddress(a);
							addressBook.getContactPanel().repopulate(c);
							addressBook.getFrame().validate();
							addressBook.getFrame().setEnabled(true);
						}  
					});
					add_address_dialog.dispose();
				}
			});
			panel_add_address.add(btn_add_address);
			
		add_address_dialog.add(panel_add_address);
		add_address_dialog.setLocationRelativeTo(parent);
		add_address_dialog.setVisible(true);
	}
	
	/**
	 * Add a TelephoneNumber to the currently selected contact.
	 * @param parent The parent window/frame.
	 */
	protected void createAndShowAddTelephoneNumberDialog(JFrame parent) {
		final JDialog add_telephonenumber_dialog = new JDialog(parent, "Add Telephone Number: ", true, null);
		add_telephonenumber_dialog.setIconImage(new ImageIcon(getClass().getClassLoader().getResource("files/Phone.png")).getImage());
		add_telephonenumber_dialog.setSize(330, 130);
		add_telephonenumber_dialog.setLayout(null);
		add_telephonenumber_dialog.setResizable(false);
		add_telephonenumber_dialog.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		add_telephonenumber_dialog.addWindowListener(new WindowListener() {
			public void windowActivated(WindowEvent e) {}
			public void windowClosed(WindowEvent e) {}
			public void windowClosing(WindowEvent e) {
				addressBook.getFrame().setEnabled(true);
				add_telephonenumber_dialog.dispose();
			}
			public void windowDeactivated(WindowEvent e) {}
			public void windowDeiconified(WindowEvent e) {}
			public void windowIconified(WindowEvent e) {}
			public void windowOpened(WindowEvent e) {} 
		});
		
		JPanel panel_add_telephonenumber = new JPanel();
		panel_add_telephonenumber.setLayout(null);
		panel_add_telephonenumber.setBounds(10, 10, 300, 100);
			
			JLabel lbl_add_telephonenumner = new JLabel("Telephone Number: ");
			lbl_add_telephonenumner.setBounds(0, 0, 100, 20);
			panel_add_telephonenumber.add(lbl_add_telephonenumner);
			final JTextField tf_add_telephonenumber = new JTextField("");
			tf_add_telephonenumber.setBounds(100, 0, 200, 20);
			panel_add_telephonenumber.add(tf_add_telephonenumber);
			
			String[] types = { "Home", "Mobile / Cell", "Work"};
			JLabel lbl_add_telephonenumbertype = new JLabel("Type: ");
			lbl_add_telephonenumbertype.setBounds(0, 30, 100, 20);
			panel_add_telephonenumber.add(lbl_add_telephonenumbertype);
			final JComboBox box_telephonenumbertype = new JComboBox(types);
			box_telephonenumbertype.setSelectedIndex(0);
			box_telephonenumbertype.setBounds(100, 30, 200, 20);
			panel_add_telephonenumber.add(box_telephonenumbertype);
			
			JButton btn_add_telephonenumber = new JButton("Save");
			btn_add_telephonenumber.setBounds(0, 60, 300, 25);
			btn_add_telephonenumber.addActionListener(new ActionListener() { 
				public void actionPerformed(ActionEvent e) {
					addressBook.getActionStack().push(new Action() {
						Contact c = currentContact;
						TelephoneNumber tn = new TelephoneNumber();
						String number = tf_add_telephonenumber.getText();
						int index = box_telephonenumbertype.getSelectedIndex();
						public void doAction() {
							tn.setNumber(number);
							if (index == 0) {
								tn.setType(TelephoneNumber.Type.HOME);
							} else if (index == 1) {
								tn.setType(TelephoneNumber.Type.CELL);
							} else if (index == 2) {
								tn.setType(TelephoneNumber.Type.WORK);
							}
							c.addTelephoneNumber(tn);
							addressBook.getContactPanel().repopulate(currentContact);
							addressBook.getFrame().validate();
							addressBook.getFrame().setEnabled(true);
						}
						public String getRedoText() {
							return "Add Telephone Number to " + c.getName();
						}
						public String getUndoText() {
							return "Remove Telephone Number from " + c.getName();
						}
						public void undoAction() {
							c.removeTelephoneNumber(tn);
							addressBook.getContactPanel().repopulate(currentContact);
							addressBook.getFrame().validate();
							addressBook.getFrame().setEnabled(true);
						}
					});
					add_telephonenumber_dialog.dispose();
				}
			});
			panel_add_telephonenumber.add(btn_add_telephonenumber);
			
		add_telephonenumber_dialog.add(panel_add_telephonenumber);
		add_telephonenumber_dialog.setLocationRelativeTo(parent);
		addressBook.getFrame().setEnabled(false);
		add_telephonenumber_dialog.setVisible(true);
	}
	
	/**
	 * Add an EmailAddress to the currently selected contact.
	 * @param parent The parent window/frame.
	 */
	protected void createAndShowAddEmailAddressDialog(JFrame parent) {
		final JDialog add_email_dialog = new JDialog(parent, "Add Email Address: ", true, null);
		add_email_dialog.setIconImage(new ImageIcon(getClass().getClassLoader().getResource("files/Mail.png")).getImage());
		add_email_dialog.setSize(330, 130);
		add_email_dialog.setLayout(null);
		add_email_dialog.setResizable(false);
		add_email_dialog.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		add_email_dialog.addWindowListener(new WindowListener() {
			public void windowActivated(WindowEvent e) {}
			public void windowClosed(WindowEvent e) {}
			public void windowClosing(WindowEvent e) {
				addressBook.getFrame().setEnabled(true);
				add_email_dialog.dispose();
			}
			public void windowDeactivated(WindowEvent e) {}
			public void windowDeiconified(WindowEvent e) {}
			public void windowIconified(WindowEvent e) {}
			public void windowOpened(WindowEvent e) {} 
		});
		
		JPanel panel_add_email = new JPanel();
		panel_add_email.setLayout(null);
		panel_add_email.setBounds(10, 10, 300, 100);
			
			JLabel lbl_add_email = new JLabel("Email Address: ");
			lbl_add_email.setBounds(0, 0, 100, 20);
			panel_add_email.add(lbl_add_email);
			final JTextField tf_add_email = new JTextField("");
			tf_add_email.setBounds(100, 0, 200, 20);
			panel_add_email.add(tf_add_email);
			
			String[] types = { "Personal", "Corporate"};
			JLabel lbl_add_emailtype = new JLabel("Type: ");
			lbl_add_emailtype.setBounds(0, 30, 100, 20);
			panel_add_email.add(lbl_add_emailtype);
			final JComboBox box_emailtype = new JComboBox(types);
			box_emailtype.setSelectedIndex(0);
			box_emailtype.setBounds(100, 30, 200, 20);
			panel_add_email.add(box_emailtype);
			
			JButton btn_add_email = new JButton("Save");
			btn_add_email.setBounds(0, 60, 300, 25);
			btn_add_email.addActionListener(new ActionListener() { 
				public void actionPerformed(ActionEvent e) {
					addressBook.getActionStack().push(new Action() {
						Contact c = currentContact;
						EmailAddress ea = new EmailAddress();
						String addr = tf_add_email.getText();
						int index = box_emailtype.getSelectedIndex();
						public void doAction() {
							ea.setAddress(addr);
							if (index == 0) {
								ea.setType(EmailAddress.Type.HOME);
							} else if (box_emailtype.getSelectedIndex() == 1) {
								ea.setType(EmailAddress.Type.WORK);
							}
							c.addEmailAddress(ea);
							addressBook.getContactPanel().repopulate(currentContact);
							addressBook.getFrame().validate();
							addressBook.getFrame().setEnabled(true);
						}
						public String getRedoText() {
							return "Add Email Address to " + c.getName();
						}
						public String getUndoText() {
							return "Remove Email Address from " + c.getName();
						}
						public void undoAction() {
							c.removeEmailAddress(ea);
							addressBook.getContactPanel().repopulate(currentContact);
							addressBook.getFrame().validate();
							addressBook.getFrame().setEnabled(true);
						}  
					});
					add_email_dialog.dispose();
				}
			});
			panel_add_email.add(btn_add_email);
			
		add_email_dialog.add(panel_add_email);
		add_email_dialog.setLocationRelativeTo(parent);
		addressBook.getFrame().setEnabled(false);
		add_email_dialog.setVisible(true);
	}
	
	/**
	 * Edit the name of the currently selected contact.
	 * @param parent The parent window/frame.
	 */
	protected void createAndShowEditNameDialog(JFrame parent) {
		final JDialog e_name_d = new JDialog(parent, "Edit Contact's Name: ", true, null);
		e_name_d.setTitle("Edit Contact's Name: ");
		e_name_d.setIconImage(new ImageIcon(getClass().getClassLoader().getResource("files/User.png")).getImage());
		e_name_d.setSize(330, 130);
		e_name_d.setLayout(null);
		e_name_d.setResizable(false);
		
		JPanel panel_edit_name = new JPanel();
		panel_edit_name.setLayout(null);
		panel_edit_name.setBounds(10, 10, 300, 100);
			JLabel lbl_edit_forenames = new JLabel("Forenames: ");
			lbl_edit_forenames.setBounds(0, 0, 100, 20);
			panel_edit_name.add(lbl_edit_forenames);
			final JTextField tf_edit_forenames = new JTextField(currentContact.getForenames());
			tf_edit_forenames.setBounds(100, 0, 200, 20);
			panel_edit_name.add(tf_edit_forenames);
			
			JLabel lbl_edit_surname = new JLabel("Surname: ");
			lbl_edit_surname.setBounds(0, 30, 100, 20);
			panel_edit_name.add(lbl_edit_surname);
			final JTextField tf_edit_surname = new JTextField(currentContact.getSurname());
			tf_edit_surname.setBounds(100, 30, 200, 20);
			panel_edit_name.add(tf_edit_surname);
			
			JButton btn_edit_name_save = new JButton("Save");
			btn_edit_name_save.setBounds(0, 60, 300, 25);
			btn_edit_name_save.addActionListener(new ActionListener() { 
				public void actionPerformed(ActionEvent e) {
					String forenames = tf_edit_forenames.getText();
					String surname = tf_edit_surname.getText();
					
					currentContact.setForenames(forenames);
					currentContact.setSurname(surname);
					
					lbl_fullname.setText("<html><big>" + forenames + " " + surname + "</big></html>");
					
					addressBook.refreshList();
					e_name_d.dispose();
				}
			});
			panel_edit_name.add(btn_edit_name_save);
			
		e_name_d.add(panel_edit_name);
		e_name_d.setLocationRelativeTo(parent);
		e_name_d.setVisible(true);
	}	
}