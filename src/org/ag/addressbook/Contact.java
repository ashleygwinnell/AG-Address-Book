package org.ag.addressbook;

import java.util.ArrayList;

import org.ag.addressbook.property.Address;
import org.ag.addressbook.property.EmailAddress;
import org.ag.addressbook.property.TelephoneNumber;

/**
 * Contact represents a real-world person with their details.
 * The properties of the contact allow export to the vCard standard 
 * and probably LDAP.
 * 
 * @author Ashley Gwinnell
 */
public class Contact 
{
	private String forenames;
	private String surname;
	private String prefixes;
	private String suffixes;
	
	private ArrayList<EmailAddress> emailAddresses = new ArrayList<EmailAddress>();
	private ArrayList<TelephoneNumber> telephoneNumbers = new ArrayList<TelephoneNumber>();
	private ArrayList<Address> addresses = new ArrayList<Address>();
	
	/**
	 * Create a new contact with no information specified.
	 * Use getters/setters.
	 */
	public Contact() {
		this.forenames = new String();
		this.surname = new String();
		this.prefixes = new String();
		this.suffixes = new String();
	}
	
	/**
	 * Sets the name of the contact.
	 * @param fullname name of the contact.
	 */
	public void setName(String fullname) {
		String[] names = fullname.split(" ");
		if (names.length == 1) {
			this.setForenames(fullname);
			return;
		}
		for (int i = 0; i < names.length-1; i++) {
			this.addForename(names[i]);
		}
		this.setSurname(names[names.length-1]);
	}
	/**
	 * Gets the name of the contact.
	 * @return the name of the contact.
	 */
	public String getName() {
		return (prefixes.trim() + " " + forenames.trim() + " " + surname.trim() + " " + suffixes.trim()).trim();
	}
	
	/**
	 * Gets the name of the contact for sorting in the JList.
	 * @return the name of the contact for sorting in the JList.
	 */
	public String getNameForSorting() {
		return surname.trim().toUpperCase() + ", " + forenames.trim().toUpperCase();
	}
	
	/**
	 * Gets the contacts forenames.
	 * @return the contact's forenames.
	 */
	public String getForenames() {
		return forenames.trim();
	}
	
	/**
	 * Gets hte contacts forenames separated by the parameter given.
	 * @param delimiter the forename separation string.
	 * @return the contact's forenames separated by delimiter.
	 */
	public String getForenames(String delimiter) {
		String name = "";
		String[] names = forenames.trim().split(" ");
		for (int i = 0; i < names.length; i++) {
			name += names[i] + delimiter;
		}
		return name;
	}
	
	/**
	 * Set the contact's forename string.
	 * @param forenames the contact's forename string.
	 */
	public void setForenames(String forenames) {
		this.forenames = forenames.trim();
	}
	
	/**
	 * Add a name to the contact's forename string.
	 * @param forename a name to add to the contacts forenames.
	 */
	public void addForename(String forename) {
		this.forenames += forename.trim() + " ";
	}
	
	/**
	 * Gets the contact's surname.
	 * @return the contact's surname.
	 */
	public String getSurname() {
		return surname;
	}
	
	/**
	 * Sets the contact's surname.
	 * @param surname the contact's surname specified.
	 */
	public void setSurname(String surname) {
		this.surname = surname.trim();
	}
	
	private String getForenames(String splitDelimiter, String joinDelimiter, int startIndex, int length) {
		String name = "";
		String[] names = this.forenames.trim().split(splitDelimiter);
		for (int i = startIndex; i < startIndex+length; i++) {
			name += names[i] + joinDelimiter;
		}
		return name;
	}
	
	/**
	 * Given name is another name for a contacts first name, singular.
	 * This is used in the vCard implementation.
	 * @return Given name is another name for a contacts first name, singular.
	 */
	public String getGivenName() {
		return this.getForenames(" ", "", 0, 1);
	}
	
	/**
	 * Get's the contact's surname aka family name in the vCard specification.
	 * @return the contact's surname aka family name 
	 */
	public String getFamilyName() {
		return this.surname.trim();
	}
	
	/**
	 * Gets the contact's additional forenames. 
	 * Additional forenames are names that are neither forename or surname, 
	 * such as middle names.
	 * @return the contact's additional forenames.
	 */
	public String getAdditionalNames() {
		return this.getForenames(" ", ",", 1, this.forenames.trim().split(" ").length-1);
	}
	
	/**
	 * The contact's honorable suffixies, eg. BSc.
	 * @return The contact's honorable suffixies
	 */
	public String getSuffixes() {
		return suffixes;
	}
	
	/**
	 * Add an honorable suffix to a contact.
	 * @param suffix the honorable suffix.
	 */
	public void addSuffix(String suffix) {
		this.addSuffix(suffix, ",");
	}
	
	/**
	 * Add an honorable suffix to a contact using a joinString.
	 * @param suffix the honorable suffix
	 * @param joinString the join string, typically a comma.
	 */
	public void addSuffix(String suffix, String joinString) {
		this.suffixes += suffix + joinString;
	}
	
	/**
	 * Get the honorable prefixes for the contact, eg. Dr.
	 * @return
	 */
	public String getPrefixes() {
		return prefixes;
	}
	
	/**
	 * Add an honorable prefix to a contact.
	 * @param prefix the honorable prefix.
	 */
	public void addPrefix(String prefix) {
		this.addPrefix(prefix, ",");
	}
	
	/**
	 * Add an honorable prefix to a contact with a joining string.
	 * @param prefix the honorable prefix
	 * @param joinString the join string typically a comma.
	 */
	public void addPrefix(String prefix, String joinString) {
		this.prefixes += prefix + joinString;
	}
	
	/**
	 * Add an email address to a contact.
	 * @param e the EmailAddress Object to add.
	 */
	public void addEmailAddress(EmailAddress e) {
		this.emailAddresses.add(e);
	}
	
	/**
	 * Get a list of the contact's email addreses.
	 * @return a list of the contact's email addresses.
	 */
	public ArrayList<EmailAddress> getEmailAddresses() {
		return this.emailAddresses;
	}
	
	/**
	 * Remove an email address from a contact.
	 * @param e the EmailAddress Object to remove.
	 */
	public void removeEmailAddress(EmailAddress e) {
		this.emailAddresses.remove(e);
	}
	
	/**
	 * Add an address to the contact.
	 * @param e the Address object to give the contact.
	 */
	public void addAddress(Address e) {
		this.addresses.add(e);
	}
	
	/**
	 * Get a ist of the contact's Addresses.
	 * @return a list of the contact's Addresses.
	 */
	public ArrayList<Address> getAddresses() {
		return this.addresses;
	}
	
	/**
	 * Remove an Address from teh contact.
	 * @param a the Address Object to remove.
	 */
	public void removeAddress(Address a) {
		this.addresses.remove(a);
	}
	
	/**
	 * Add a TelephoneNumber to a contact.
	 * @param e the TelephoneNumber to add.
	 */
	public void addTelephoneNumber(TelephoneNumber e) {
		this.telephoneNumbers.add(e);
	}
	
	/**
	 * Get a list of the contact's TelephoneNumbers.
	 * @return a list of the contact's TelephoneNumbers.
	 */
	public ArrayList<TelephoneNumber> getTelephoneNumbers() {
		return this.telephoneNumbers;
	}
	
	/**
	 * Remove a TelephoneNumber from a contact.
	 * @param a the TelephoneNumber to remove.
	 */
	public void removeTelephoneNumber(TelephoneNumber a) {
		this.telephoneNumbers.remove(a);
	}
	
	/**
	 * Combine two contacts into one. 
	 * This is used when importing and there are duplicate contacts.
	 * @param c the Contact to combine/merge with.
	 */
	public void combine(Contact c) {
		this.addresses.addAll(c.getAddresses());
		this.telephoneNumbers.addAll(c.getTelephoneNumbers());
		this.emailAddresses.addAll(c.getEmailAddresses());
	}
	
	/**
	 * Determines whether the contact has information about it that 
	 * cannot be stored in the BUAB file format.
	 * @return whether the contact has information about it that 
	 * 		   cannot be stored in the BUAB file format.
	 */
	public boolean hasInformationNotStorableInBUAB() {
		if (this.emailAddresses.size() > 0
				|| this.telephoneNumbers.size() > 2
				|| this.addresses.size() > 1) {
			return true;
		} else {
			for (int i = 0; i < this.addresses.size(); i++) {
				Address a = this.addresses.get(i);
				if (		a.getPOBoxNumber().trim().length() > 0
						||  a.getExtendedAddress().trim().length() > 0
						||  a.getCity().trim().length() > 0
						||  a.getCounty().trim().length() > 0
						||  a.getCountry().trim().length() > 0
						||  a.getPostcode().trim().length() > 0) {
					return true;
				}	
			}
			return false;
		}
	}
	
	
	/**
	 * Get the search String for this contact. this will be searched when using Quick Search.
	 * @return the search String for this contact. used in quick search.
	 */
	public String toSearchString() {
		String searchString = new String("");
		searchString += prefixes + " " + forenames + " " + surname + " " + suffixes + " ";
		for (int i = 0; i < addresses.size(); i++) {
			searchString += addresses.get(i).toSearchString() + " ";
		}
		for (int i = 0; i < telephoneNumbers.size(); i++) {
			searchString += telephoneNumbers.get(i).toSearchString() + " ";
		}
		for (int i = 0; i < emailAddresses.size(); i++) {
			searchString += emailAddresses.get(i).toSearchString() + " ";
		}
		return searchString;
	}
}