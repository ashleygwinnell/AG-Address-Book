package org.ag.addressbook;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import org.ag.addressbook.property.Address;
import org.ag.addressbook.property.EmailAddress;
import org.ag.addressbook.property.TelephoneNumber;

/**
 * Exporter
 * This is used to save address book isntances to a file.
 * You can override or edit this class to implement extra data/file structures.
 * @author Ashley Gwinnell
 */
public class Exporter 
{
	private File f;
	private ArrayList<Contact> contacts;
	
	/**
	 * Create a new Exporter.
	 */
	public Exporter(File f, ArrayList<Contact> contacts) 
	{
		this.f = f;
		this.contacts = contacts;
	}
	
	/**
	 * Write the list of contacts to the file, overwriting the file.
	 * This method should determine the file type and how to write that file.
	 */
	public void write() {
		String ext = Importer.getFileExtension(this.f);
		if (ext.equals(Importer.BUAB)) {
			this.write(Structure.BUAB);
		} else if (ext.equals(Importer.VCARD_1) || ext.equals(Importer.VCARD_2)) {
			Structure s = Importer.getVCardVersion(this.f);
			if (s == null) {
				return;
			}
			this.write(s);
		}
	}
	
	/**
	 * Writes to the file from a structure enum.
	 * @param s
	 */
	private void write(Structure s) {
		if (s.equals(Structure.BUAB)) {
			this.writeBUAB();
		} else if (s.equals(Structure.VCARD21)) {
			 this.writeVCARD21();
		} else if (s.equals(Structure.VCARD30)) {
			this.writeVCARD30();
		}
	}
	
	/**
	 * Write a BUAB file.
	 */
	private void writeBUAB() {
		//System.out.println("Writing BUAB File!");
		try {
			String file = "";
			for (int i = 0; i < this.contacts.size(); i++) {
				Contact c = this.contacts.get(i);
				file += c.getForenames() + " " + c.getSurname() + "\r\n" +
						this.BUAB_getTelephoneNumber(c, TelephoneNumber.Type.HOME, true) + "\r\n" +
						this.BUAB_getTelephoneNumber(c, TelephoneNumber.Type.CELL, false) + "\r\n" +
						this.BUAB_getPreferredAddress(c) + "\r\n";
			}
			BufferedWriter writer = new BufferedWriter(new FileWriter(this.f));
			writer.write(file);
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Write a VCard2.1 file.
	 */
	private void writeVCARD21() {
		//System.out.println("Writing VCard File V2.1!");
	}
	
	/**
	 * Write a VCard3.0 file.
	 */
	private void writeVCARD30() {
		try {
			String file = "";
			for (int i = 0; i < this.contacts.size(); i++) {
				Contact c = this.contacts.get(i);
				String emails = "";
				for (int j = 0; j < c.getEmailAddresses().size(); j++) {
					EmailAddress ea = c.getEmailAddresses().get(j);
					emails += "EMAIL;TYPE=INTERNET";
					if (ea.getType().equals(EmailAddress.Type.HOME)) {
						emails += ";TYPE=HOME";
					} else if (ea.getType().equals(EmailAddress.Type.WORK)) {
						emails += ";TYPE=WORK";
					}
					if (ea.isPreferred()) {
						emails += ";TYPE=PREF";
					}
					emails += ":" + ea.getAddress() + "\r\n";
				}
				
				String telephones = "";
				for (int j = 0; j < c.getTelephoneNumbers().size(); j++) {
					TelephoneNumber t = c.getTelephoneNumbers().get(j);
					telephones += "TEL";
					if (t.getType().equals(TelephoneNumber.Type.HOME)) {
						telephones += ";TYPE=HOME";
					} else if (t.getType().equals(TelephoneNumber.Type.WORK)) {
						telephones += ";TYPE=WORK";
					} else if (t.getType().equals(TelephoneNumber.Type.CELL)) {
						telephones += ";TYPE=CELL";
					}
					if (t.isPreferred()) {
						telephones += ";TYPE=PREF";
					}
					telephones += ":" + t.getNumber() + "\r\n";
				}
				
				String addresses = "";
				for (int j = 0; j < c.getAddresses().size(); j++) {
					Address a = c.getAddresses().get(j);
					addresses += a.toString(c, Structure.VCARD30);
				}
					
				file += "BEGIN:VCARD\r\n" + 
						"VERSION:3.0\r\n" +
						"N:" + c.getFamilyName() + ";" + c.getGivenName() + ";" + c.getAdditionalNames() + ";" + c.getPrefixes() + ";" + c.getSuffixes() + "\r\n" +
						"FN:" + c.getName() + "\r\n" + 
						emails + 
						telephones + 
						addresses + 
						"END:VCARD\r\n";
			}
			BufferedWriter writer = new BufferedWriter(new FileWriter(this.f));
			writer.write(file);
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
		
	/**
	 * Attempt to get the preferred telephone number 
	 * for BUAB backwards compatibility.
	 * @param c The contact
	 * @param t The Type
	 * @param preferred Whether it must be preferred or not
	 * @return The preferred best effort telephone number.
	 */
	private String BUAB_getTelephoneNumber(Contact c, TelephoneNumber.Type t, boolean preferred) {
		if (c.getTelephoneNumbers().size() == 0) {
			return "";
		} else {
			// look for preferred home number.
			for (int i = 0; i < c.getTelephoneNumbers().size(); i++) {
				if (c.getTelephoneNumbers().get(i).getType().equals(t)) {
					if (preferred) {
						if (c.getTelephoneNumbers().get(i).isPreferred()) {
							return c.getTelephoneNumbers().get(i).getNumber();
						}
					} else {
						return c.getTelephoneNumbers().get(i).getNumber();
					}					
				}
			}
			return "";
		}
	}
	
	/**
	 * Similar principle to BUAB_getTelephoneNumber.
	 * @param c the contact
	 * @return the best effort address for the contact.
	 */
	private String BUAB_getPreferredAddress(Contact c) {
		if (c.getAddresses().size() == 0) {
			return "";
		} else {
			// look for preferred home number.
			for (int i = 0; i < c.getAddresses().size(); i++) {
				if (c.getAddresses().get(i).isPreferred()) {
					String buabline = "";
					buabline += c.getAddresses().get(i).getStreetAddress().replace("\n", ", ");
					return buabline;
				}
			}
			return "";
		}
	}
}
