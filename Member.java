/**
 * Represents a library member (reader). A member can borrow up to MAX_BOOKS books.
 */
public class Member {
    public static final int MAX_BOOKS = 3;

    private int id;
    private String name;
    private Book[] borrowed = new Book[MAX_BOOKS];
    private int borrowedCount = 0;

    public Member(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public int getBorrowedCount() { return borrowedCount; }

    public void setName(String name) { this.name = name; }

    public boolean canBorrow() {
        return borrowedCount < MAX_BOOKS;
    }

    public void addBook(Book b) {
        if (canBorrow()) {
            borrowed[borrowedCount] = b;
            borrowedCount++;
        }
    }

    /** Removes a book from this member's array, shifting the rest left. */
    public boolean removeBook(Book b) {
        for (int i = 0; i < borrowedCount; i++) {
            if (borrowed[i] == b) {
                for (int j = i; j < borrowedCount - 1; j++) {
                    borrowed[j] = borrowed[j + 1];
                }
                borrowedCount--;
                borrowed[borrowedCount] = null;
                return true;
            }
        }
        return false;
    }

    /** Lists the titles of the books this member currently holds. */
    public String borrowedTitles() {
        if (borrowedCount == 0) {
            return "none";
        }
        String result = "";
        for (int i = 0; i < borrowedCount; i++) {
            result += borrowed[i].getTitle();
            if (i < borrowedCount - 1) {
                result += ", ";
            }
        }
        return result;
    }

    @Override
    public String toString() {
        return String.format("%-4d | %-20s | Borrowed (%d/%d): %s",
                id, name, borrowedCount, MAX_BOOKS, borrowedTitles());
    }
}
