package accessories;

import javax.swing.text.AttributeSet;
import javax.swing.text.DocumentFilter;
import javax.swing.text.BadLocationException;

/* This filter can be added to a text area to make part of it not editable. 
 * Only the text after 'position' in the text area can be edited.*/

public class Filter extends DocumentFilter {
	private int position = 0;

 public void insertString(final FilterBypass fb, final int offset, final String string, final AttributeSet attr) throws BadLocationException {
	 if (offset >= position) {
		 super.insertString(fb, offset, string, attr);
	 }
 }

 public void remove(final FilterBypass fb, final int offset, final int length) throws BadLocationException {
	 if (offset >= position) {
		 super.remove(fb, offset, length);
	 }
 }

 public void replace(final FilterBypass fb, final int offset, final int length, final String text, final AttributeSet attrs) throws BadLocationException {
	 if (offset >= position) {
		 super.replace(fb, offset, length, text, attrs);
	 }
 }

 public void changePosition(int pos) {
	 position = pos;
 }
}