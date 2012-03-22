package org.ag.addressbook.property;

import org.ag.addressbook.Contact;
import org.ag.addressbook.Structure;

/**
 * An Address that can be added to a Contact.
 * @author Ashley Gwinnell
 */
public class Address 
{
	public enum Type {HOME, WORK};
	private Type type = Type.HOME;
	private boolean preferred = false;
	
	private String poBoxNumber = "";
	private String extendedAddress = "";
	private String streetAddress = "";
	private String city = ""; // locality / city.
	private String county = ""; // region / state / province / county.
	private String postcode = "";
	private String country = "";
	
	public Address() {
		
	}
	
	public Address(Type type, boolean preferred) {
		this.setType(type);
		this.setPreferred(preferred);
	}
	
	public void setPOBoxNumber(String poBoxNumber) {
		this.poBoxNumber = poBoxNumber;
	}
	
	public String getPOBoxNumber() {
		return poBoxNumber;
	}
	
	public void setExtendedAddress(String extendedAddress) {
		this.extendedAddress = extendedAddress;
	}
	public String getExtendedAddress() {
		return extendedAddress;
	}
	
	public void setStreetAddress(String streetAddress) {
		this.streetAddress = streetAddress;
	}
	public String getStreetAddress() {
		return streetAddress;
	}
	
	public void setCity(String city) {
		this.city = city;
	}
	public String getCity() {
		return city;
	}
	
	public void setCounty(String county) {
		this.county = county;
	}
	public String getCounty() {
		return county;
	}
	
	public void setPostcode(String postcode) {
		this.postcode = postcode;
	}
	public String getPostcode() {
		return postcode;
	}
	
	public void setCountry(String country) {
		this.country = country;
	}
	public String getCountry() {
		return country;
	}
	
	
	
	
	public void setPreferred(boolean preferred) {
		this.preferred = preferred;
	}
	public boolean isPreferred() {
		return preferred;
	}
	public void setType(Type type) {
		this.type = type;
	}
	public Type getType() {
		return type;
	}
	/*
	public void addLine(String s) {
		this.lines.add(s);
	}
	public void addLines(String[] s) {
		for (int i = 0; i < s.length; i++) {
			if (s[i].trim().length() != 0) {
				this.addLine(s[i].trim());
			}
		}
	}
	public void setLines(String[] s) {
		this.lines.clear();
		this.addLines(s);
	}
	public ArrayList<String> getLines() {
		return this.lines;
	}	
	*/
	
	private String clean(Structure s, String str) {
		if (s.equals(Structure.VCARD30)) {
			return str.replace(",", "\\,").replace("\n", "\\n");
		}
		return "";
	}
	
	public String toString(Contact c, Structure f) {
		if (f.equals(Structure.BUAB)) {
			
		} else if (f.equals(Structure.VCARD30)) {
			// addr
			String line = "ADR";
			if (this.getType().equals(Address.Type.HOME)) {
				line += ";TYPE=HOME";
			} else if (this.getType().equals(Address.Type.WORK)) {
				line += ";TYPE=WORK";
			}
			if (this.isPreferred()) {
				line += ";TYPE=PREF";
			}
			line += ":";
			line += this.clean(Structure.VCARD30, this.getPOBoxNumber()) + ";";
			line += this.clean(Structure.VCARD30, this.getExtendedAddress()) + ";";
			line += this.clean(Structure.VCARD30, this.getStreetAddress()) + ";";
			line += this.clean(Structure.VCARD30, this.getCity()) + ";";
			line += this.clean(Structure.VCARD30, this.getCounty()) + ";";
			line += this.clean(Structure.VCARD30, this.getPostcode()) + ";";
			line += this.clean(Structure.VCARD30, this.getCountry()) + "\r\n";
			
			// label
			line += "LABEL";
			if (this.getType().equals(Address.Type.HOME)) {
				line += ";TYPE=HOME";
			} else if (this.getType().equals(Address.Type.WORK)) {
				line += ";TYPE=WORK";
			}
			if (this.isPreferred()) {
				line += ";TYPE=PREF";
			}
			
			String label_line = "";
			label_line += ":";
			label_line += c.getName() + "\\n";
			if (this.getPOBoxNumber().trim().length() != 0) {
				label_line += this.getPOBoxNumber() + "\\n";
			} if (this.getExtendedAddress().trim().length() != 0) {
				label_line += this.getExtendedAddress() + "\\n";
			} if (this.getStreetAddress().trim().length() != 0) {
				label_line += this.clean(Structure.VCARD30, this.getStreetAddress()) + "\\n";
			} if (this.getCity().trim().length() != 0) {
				label_line += this.getCity() + "\\n";
			} if (this.getCounty().trim().length() != 0) {
				label_line += this.getCounty() + "\\n";
			} if (this.getPostcode().trim().length() != 0) {
				label_line += this.getPostcode() + "\\n";
			} if (this.getCountry().trim().length() != 0) {
				label_line += this.getCountry() + "\\n";
			}
			if (label_line.substring(label_line.length()-2, label_line.length()).equals("\\n")) {
				label_line = label_line.substring(0, label_line.length()-2);
			}
			label_line += "\r\n";
			return line + label_line;
		}
		return "";
	}
	
	public String toSearchString() {
		String searchString = new String("");
		searchString += poBoxNumber + " " + extendedAddress + " " +
						streetAddress + " " + city + " " +
						county + " " + postcode + " " + country;
		return searchString;
	}
}