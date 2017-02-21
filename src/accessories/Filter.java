package accessories;

import javax.swing.text.AttributeSet;
import javax.swing.text.DocumentFilter;
import javax.swing.text.BadLocationException;

/**
 *<h1>Filter.java</h1>
 *
 *<p>
 * This filter can be added to a text area to make 
 * part of it not editable. Only the text after 
 * 'position' in the text area can be edited.
 * 
 * @author Sourish Banerjee
 */

public class Filter extends DocumentFilter {
	private int position = 0; // Position after which text in the text area is editable.

	/**
	 * Allows text to be inserted to text area only 
	 * if position of insertion is after 'position'.
	 */
	public void insertString(final FilterBypass fb, final int offset, final String string, final AttributeSet attr) throws BadLocationException {
		if (offset >= position) {
			super.insertString(fb, offset, string, attr);
		}
	}

	/**
	 * Allows text to be removed from text area only 
	 * if position of removal is after 'position'.
	 */
	public void remove(final FilterBypass fb, final int offset, final int length) throws BadLocationException {
		if (offset >= position) {
			super.remove(fb, offset, length);
		}
	}

	/**
	 * Allows text in text area to be replaced only 
	 * if position of replacement is after 'position'.
	 */
	public void replace(final FilterBypass fb, final int offset, final int length, final String text, final AttributeSet attrs) throws BadLocationException {
		if (offset >= position) {
			super.replace(fb, offset, length, text, attrs);
		}
	}

	/**
	 * Setter method to update the value of 'position'.
	 * 
	 * @param pos New value of position.
	 */
	public void changePosition(int pos) {
		position = pos;
	}
}