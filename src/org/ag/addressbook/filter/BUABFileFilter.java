package org.ag.addressbook.filter;
import java.io.File;

import javax.swing.filechooser.FileFilter;

import org.ag.addressbook.Importer;

/**
 * A FileFilter to filter out files that aren't .BUAB
 * @author Ashley Gwinnell
 */
public class BUABFileFilter extends FileFilter {

	private boolean singular = false;
	public BUABFileFilter() {
		this(false);
	}
	public BUABFileFilter(boolean singular) {
		this.singular = singular;
	}
	@Override
	public boolean accept(File f) {
		if (f.isDirectory()) { return true; }
		String extension = Importer.getFileExtension(f);
		if (extension != null) {
			if (extension.equals(Importer.BUAB)
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
			return "BUAB File";
		} else {
			return "BUAB Files";
		}
	}

}
