/**
 * Manages the arrays of books and members and holds the main business logic.
 */
public class Library {
    public static final int SORT_BY_ID = 0;
    public static final int SORT_BY_TITLE = 1;
    public static final int SORT_BY_AUTHOR = 2;

    private Book[] books = new Book[100];
    private Member[] members = new Member[50];
    private int bookCount = 0;
    private int memberCount = 0;
    private int nextBookId = 1;
    private int nextMemberId = 1;

    // ---------- Adding and registering ----------

    public boolean addBook(String title, String author) {
        if (bookCount >= books.length) {
            return false;                       // array is full
        }
        int id;
        do {                                    // make sure the ID is not already used
            id = nextBookId++;
        } while (findBook(id) != null);

        books[bookCount] = new Book(id, title, author);
        bookCount++;
        return true;
    }

    public boolean registerMember(String name) {
        if (memberCount >= members.length) {
            return false;
        }
        int id;
        do {
            id = nextMemberId++;
        } while (findMember(id) != null);

        members[memberCount] = new Member(id, name);
        memberCount++;
        return true;
    }

    // ---------- Searching ----------

    public Book findBook(int id) {
        for (int i = 0; i < bookCount; i++) {
            if (books[i].getId() == id) {
                return books[i];
            }
        }
        return null;
    }

    public Member findMember(int id) {
        for (int i = 0; i < memberCount; i++) {
            if (members[i].getId() == id) {
                return members[i];
            }
        }
        return null;
    }

    /** Searches books by part of the title or author (case-insensitive). */
    public String searchBooks(String keyword) {
        String k = keyword.toLowerCase();
        String result = "";
        int found = 0;
        for (int i = 0; i < bookCount; i++) {
            boolean match = books[i].getTitle().toLowerCase().contains(k)
                         || books[i].getAuthor().toLowerCase().contains(k);
            if (match) {
                result += books[i] + "\n";
                found++;
            }
        }
        if (found == 0) {
            return "No books match \"" + keyword + "\".";
        }
        return found + " result(s):\n" + result;
    }

    // ---------- Borrowing and returning ----------

    public String borrowBook(int bookId, int memberId) {
        Book b = findBook(bookId);
        Member m = findMember(memberId);

        if (b == null) {
            return "Book not found.";
        } else if (m == null) {
            return "Member not found.";
        } else if (!b.isAvailable()) {
            return "Book is already borrowed.";
        } else if (!m.canBorrow()) {
            return m.getName() + " has reached the limit of " + Member.MAX_BOOKS + " books.";
        }

        b.setAvailable(false);
        m.addBook(b);
        return m.getName() + " borrowed \"" + b.getTitle() + "\".";
    }

    public String returnBook(int bookId, int memberId) {
        Book b = findBook(bookId);
        Member m = findMember(memberId);

        if (b == null || m == null) {
            return "Book or member not found.";
        }
        if (m.removeBook(b)) {
            b.setAvailable(true);
            return "\"" + b.getTitle() + "\" was returned by " + m.getName() + ".";
        }
        return m.getName() + " did not borrow that book.";
    }

    // ---------- Sorting and listing ----------

    /** Returns true if book a should come after book b for the chosen sort mode. */
    private boolean shouldSwap(Book a, Book b, int sortMode) {
        switch (sortMode) {
            case SORT_BY_TITLE:
                return a.getTitle().compareToIgnoreCase(b.getTitle()) > 0;
            case SORT_BY_AUTHOR:
                return a.getAuthor().compareToIgnoreCase(b.getAuthor()) > 0;
            case SORT_BY_ID:
            default:
                return a.getId() > b.getId();
        }
    }

    /** Sorts the books array (bubble sort). */
    private void sortBooks(int sortMode) {
        for (int i = 0; i < bookCount - 1; i++) {
            for (int j = 0; j < bookCount - 1 - i; j++) {
                if (shouldSwap(books[j], books[j + 1], sortMode)) {
                    Book temp = books[j];
                    books[j] = books[j + 1];
                    books[j + 1] = temp;
                }
            }
        }
    }

    public String listBooks(int sortMode) {
        if (bookCount == 0) {
            return "No books yet.";
        }
        sortBooks(sortMode);
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < bookCount; i++) {
            sb.append(books[i]).append("\n");
        }
        return sb.toString();
    }

    public String listMembers() {
        if (memberCount == 0) {
            return "No members yet.";
        }
        String result = "";
        int i = 0;
        while (i < memberCount) {
            result += members[i] + "\n";
            i++;
        }
        return result;
    }

    public int getBookCount() { return bookCount; }
    public int getMemberCount() { return memberCount; }
}
