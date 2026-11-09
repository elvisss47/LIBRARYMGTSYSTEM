/*
 * CSC 1102: Structured and Object-Oriented Programming - Practical Exam Project
 *
 * Group Name   : GROUP TWO
 * Group Leader : AGUMIRE ELVIS
 * Session      : Day 
 *
 * Members (Name - Reg No - Student No):
 *   1. AGUMIRE ELVIS      26/U/03468/PS    2600703468
 *   2. TAZITA ANNA JOHN  26/X/26676/PSA   2600726676
 *   3. MUZAHURA JOSHUA	  26/U/31394/PS    2600731394
 *   4. ATUHURA MARGRET	   26/U/29612      2600729612
 *   
 *
 
 */
import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new LibraryGUI().setVisible(true));
    }
}
