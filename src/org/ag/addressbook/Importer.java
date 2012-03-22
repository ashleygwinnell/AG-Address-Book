package org.ag.addressbook;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

import javax.swing.JOptionPane;

import org.ag.addressbook.property.Address;
import org.ag.addressbook.property.EmailAddress;
import org.ag.addressbook.property.TelephoneNumber;
import org.ag.util.StringUtil;

/**
 * Importer
 * This is used to import contacts from a file into the existing address book instance.
 * Note the difference between Open and Import. They both use this class to add contacts from a file.
 * @author Ashley Gwinnell
 */
public class Importer 
{
	private ArrayList<File> fs = new ArrayList<File>();
	
	/**
	 * Create a new Importer from a string reference.
	 * @param f The Absolute Filename in a string.
	 */
	public Importer(String f) {
		this(new File(f));
	}
	
	/**
	 * Create a new Importer from a file.
	 * @param f The file to import.
	 */
	public Importer(File f)
	{
		this.fs.add(f);
	}
	
	/**
	 * Create a new Importer with multiple files. 
	 * Used in Import and not in Open.
	 * @param f An array of files to open/import/get contacts from.
	 */
	public Importer(File[] f)
	{
		for (int i = 0; i < f.length; i++) {
			this.fs.add(f[i]);
		}
	}
	
	/**
	 * Reads all of the files asked and returns the contacts from all combined.
	 * @return the contacts from all files combined.
	 */
	public ArrayList<Contact> load() 
	{	
		ArrayList<Contact> list = new ArrayList<Contact>();
		try 
		{
			for (int i = 0; i < this.fs.size(); i++) {
				String ext = Importer.getFileExtension(this.fs.get(i));
				if (ext.equals(Importer.BUAB)) {
					list.addAll(this.importBUAB(i));
				} else if (ext.equals(Importer.VCARD_1) || ext.equals(Importer.VCARD_2)) {
					Structure s = Importer.getVCardVersion(this.fs.get(i));
					if (s == null) {
						return list;
					} else if (s.equals(Structure.VCARD30)) {
						list.addAll(this.importVCard30(i));
					}
				}
			}
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}
		return list;
	}
	
	
	/**
	 * Cleans a string on importing.
	 * @param s the file structure.
	 * @param str the "dirty" string to clean for that file structure.
	 * @return a "clean" string.
	 */
	private String clean(Structure s, String str) {
		if (s.equals(Structure.VCARD30)) {
			return str.replace("\\,", ",").replace("\\n", "\n");
		}
		return "";
	}
	
	/**
	 * Import contacts from a VCard 3.0 file.
	 * @param x The array index from a list of files in the importer.
	 * @return contacts from a VCard 3.0 file.
	 */
	private ArrayList<Contact> importVCard30(int x) { 
		ArrayList<Contact> contacts = new ArrayList<Contact>();
		
		try {
			BufferedReader reader = new BufferedReader(new FileReader(this.fs.get(x)));
			String file = "";
			String line = "";
			while ((line = reader.readLine()) != null) {
				file += line + "\n";
			}
			
			Contact c = null;
			Scanner s = new Scanner(file);
			while (s.hasNextLine()) {
				line = s.nextLine();
				try {
					if (line.equals("BEGIN:VCARD")) 
					{
						c = new Contact();
					} 
					else if (line.substring(0, 3).equals("FN:")) 
					{
						c.setName(line.substring(3));
					}
					else if (line.substring(0, 6).equals("EMAIL;")) 
					{
						EmailAddress ea = new EmailAddress();
						if (line.toUpperCase().contains("TYPE=HOME")) {
							ea.setType(EmailAddress.Type.HOME);
						} else if (line.toUpperCase().contains("TYPE=WORK")) {
							ea.setType(EmailAddress.Type.WORK);
						}
						if (line.toUpperCase().contains("TYPE=pref")) {
							ea.setPreferred(true);
						} else {
							ea.setPreferred(false);
						}
						ea.setAddress(line.substring(line.lastIndexOf(":")+1));
						c.addEmailAddress(ea);
					} 
					else if (line.substring(0, 4).toUpperCase().equals("TEL;")) 
					{
						TelephoneNumber tel = new TelephoneNumber();
						if (line.toUpperCase().contains("TYPE=HOME")) {
							tel.setType(TelephoneNumber.Type.HOME);
						} else if (line.toUpperCase().contains("TYPE=CELL")) {
							tel.setType(TelephoneNumber.Type.CELL);
						} else if (line.toUpperCase().contains("TYPE=WORK")) {
							tel.setType(TelephoneNumber.Type.WORK);
						}
						if (line.toUpperCase().contains("TYPE=PREF")) {
							tel.setPreferred(true);
						} else {
							tel.setPreferred(false);
						}
						tel.setNumber(line.substring(line.lastIndexOf(":")+1));
						c.addTelephoneNumber(tel);
					} 
					else if (line.substring(0, 4).toUpperCase().equals("ADR;")) 
					{
						Address a = new Address();
						if (line.toUpperCase().contains("TYPE=HOME")) {
							a.setType(Address.Type.HOME);
						} else if (line.toUpperCase().contains("TYPE=WORK")) {
							a.setType(Address.Type.WORK);
						}
						if (line.toUpperCase().contains("TYPE=PREF")) {
							a.setPreferred(true);
						} else {
							a.setPreferred(false);
						}
						String addressLine = line.substring(line.lastIndexOf(":")+1);
						String[] parts = StringUtil.splitWithoutTrimming(addressLine, ';');
						a.setPOBoxNumber(this.clean(Structure.VCARD30, parts[0]));
						a.setExtendedAddress(this.clean(Structure.VCARD30, parts[1]));
						a.setStreetAddress(this.clean(Structure.VCARD30, parts[2]));
						a.setCity(this.clean(Structure.VCARD30, parts[3]));
						a.setCounty(this.clean(Structure.VCARD30, parts[4]));
						a.setPostcode(this.clean(Structure.VCARD30, parts[5]));
						a.setCountry(this.clean(Structure.VCARD30, parts[6]));
						c.addAddress(a);
					}
					else if (line.equals("END:VCARD")) 
					{
						contacts.add(c);
						c = null;
					}
				} catch (Exception e) {
					//e.printStackTrace();
					JOptionPane.showMessageDialog(null, "Could not load part of the file.", "Error:", JOptionPane.ERROR_MESSAGE);
					return contacts;
				}
			}
			
		} catch (IOException e) {
			return contacts;
		}
		return contacts;
	}
	
	/**
	 * Import contacts from a BUAB file.
	 * @param x the array index of the file in the list in the importer.
	 * @return a list of contacts from the BUAB file.
	 * @throws FileNotFoundException
	 */
	private ArrayList<Contact> importBUAB(int x) throws FileNotFoundException 
	{
		ArrayList<Contact> contacts = new ArrayList<Contact>();
		Scanner scan = new Scanner(fs.get(x));
		String line;
		int i = 0;
		int lines_per_contact = 4;
		
		String forenames = "";
		String surname = "";
		TelephoneNumber homephone = new TelephoneNumber();
		TelephoneNumber mobilephone = new TelephoneNumber();
		
		while (scan.hasNextLine()) {
			line = scan.nextLine();
			if (i == lines_per_contact) {
				i = 0;
			}
			
			switch (i) {
				case 0:
					String[] names = line.split(" ");
					for (int j = 0; j < names.length-1; j++) {
						forenames += names[j];
					}
					surname = names[names.length-1];
					break;
				case 1:
					homephone.setNumber(line);
					homephone.setType(TelephoneNumber.Type.HOME);
					homephone.setPreferred(true);
					break;
				case 2:
					mobilephone.setNumber(line);
					mobilephone.setType(TelephoneNumber.Type.CELL);
					mobilephone.setPreferred(false);
					break;
				case 3:
					Contact c = new Contact();
					c.setForenames(forenames);
					c.setSurname(surname);
					c.addTelephoneNumber(homephone);
					c.addTelephoneNumber(mobilephone);
						Address a = new Address();
						a.setStreetAddress(line.replace(", ", "\n").replace(",", "\n"));
						a.setType(Address.Type.HOME);
						a.setPreferred(true);
						c.addAddress(a);
					contacts.add(c);
					forenames = "";
					surname = "";
					homephone = new TelephoneNumber();
					mobilephone = new TelephoneNumber();
					break;
			}
			
			
			i++;
		}
		
		return contacts;
	}
	

	/** File extension for buab **/
	public static final String BUAB = new String("buab");
	/** File extension for vcard **/
	public static final String VCARD_1 = new String("vcf");
	/** Another file extension for vcard **/
	public static final String VCARD_2 = new String("vcard");
	
	/**
	 * Get the file extension from a file object.
	 * @param f the file object to get the extension for.
	 * @return the file extension.
	 */
	public static String getFileExtension(File f) {
        String ext = "";
        String s = f.getName();
        int i = s.lastIndexOf('.');

        if (i > 0 &&  i < s.length() - 1) {
            ext = s.substring(i+1).toLowerCase();
        }
        return ext;
    }
	
	/**
	 * Checks whether the file has a valid extension. 
	 * @param f the file to check the extension on.
	 * @return true on valid extension.
	 */
	public static boolean isValidExtension(File f) {
		String s = Importer.getFileExtension(f);
		if (s.equals(Importer.BUAB) || s.equals(Importer.VCARD_1) || s.equals(Importer.VCARD_2)) {
			return true;
		} else {
			return false;
		}
	}
	
	/**
	 * Reads the VCard file and returns the version in a Structure enumeration.
	 * @param f the VCard file to check the version of.
	 * @return the version in a Structure enumeration
	 */
	public static Structure getVCardVersion(File f) {
		 // read part of the file and determine it's vcard version?
		try {
			String file = "";
			String line = "";
			BufferedReader reader = new BufferedReader(new FileReader(f));
			while ((line = reader.readLine()) != null) {
				file += line;
			}
			if (file.length() == 0) {
				return Structure.VCARD30;
			} else if (file.contains("VERSION:3.0")) {
				return Structure.VCARD30;
			} else if (file.contains("VERSION:2.1")) {
				return Structure.VCARD21;
			}
		} catch (IOException e) { 
			JOptionPane.showMessageDialog(null, "Could not get VCard version from file:\r\n " + f.getAbsolutePath(), "Error: ", JOptionPane.ERROR_MESSAGE);
		}
		return null;
	}
}