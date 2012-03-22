package org.ag.addressbook.filter;
import java.io.File;

import javax.swing.filechooser.FileFilter;

import org.ag.addressbook.Importer;

/**
 * A FileFilter to filter out files that aren't .vcf or .vcard
 * @author Ashley Gwinnell
 */
public class VCardFileFilter extends FileFilter {
	
	public boolean singular = false;
	public VCardFileFilter() {
		this(false);
	}
	public VCardFileFilter(boolean singular) {
		this.singular = singular;
	}
	@Override
	public boolean accept(File f) {
		if (f.isDirectory()) { return true; }
		String extension = Importer.getFileExtension(f);
		if (extension != null) {
			if (extension.equals(Importer.VCARD_1)
					|| extension.equals(Importer.VCARD_2)
					|| extension.equals("lnk")) {
				return true;
			}
			return false;
		}
		return false;
	}

	@Override
	public String getDescription() {
		if (this.singular) {
			return "VCard File";
		} else {
			return "VCard Files";
		}
	}
	
}
