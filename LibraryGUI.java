import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * The Swing window. It talks to a single Library object.
 * Styled with a header banner, coloured cards and coloured buttons.
 */
public class LibraryGUI extends JFrame {
    // ---------- Colour palette ----------
    private static final Color TEAL   = new Color(0x00B894);
    private static final Color BLUE   = new Color(0x0984E3);
    private static final Color ORANGE = new Color(0xFF7F3F);
    private static final Color PURPLE = new Color(0x6C5CE7);
    private static final Color PINK   = new Color(0xE84393);
    private static final Color YELLOW = new Color(0xFDCB6E);
    private static final Color GREY   = new Color(0x636E72);
    private static final Color GREEN  = new Color(0x00875A);
    private static final Color RED    = new Color(0xD63031);
    private static final Color PAGE   = new Color(0xF3EFFF);   // soft lavender
    private static final Color TEXT   = new Color(0x2D3436);
    private static final Color PAPER   = new Color(0xFFFCF2);  // warm cream for results

    private static final Font LABEL_FONT = new Font("SansSerif", Font.PLAIN, 13);
    private static final Font BOLD_FONT  = new Font("SansSerif", Font.BOLD, 13);

    private Library library = new Library();

    private JTextField titleField = makeField(14);
    private JTextField authorField = makeField(14);
    private JTextField nameField = makeField(14);
    private JTextField bookIdField = makeField(6);
    private JTextField memberIdField = makeField(6);
    private JTextField searchField = makeField(14);
    private JComboBox<String> sortBox =
            new JComboBox<>(new String[]{"Sort by ID", "Sort by Title", "Sort by Author"});
    private JTextArea output = new JTextArea();

    public LibraryGUI() {
        setTitle("Library Management System");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        getContentPane().setBackground(PAGE);
        setLayout(new BorderLayout());

        add(buildHeader(), BorderLayout.NORTH);
        add(buildCentre(), BorderLayout.CENTER);

        showText("Welcome! Add some books and register members to begin.");

        setMinimumSize(new Dimension(760, 380));
        pack();
        setLocationRelativeTo(null);
    }

    // ---------- Building the window ----------

    private JPanel buildHeader() {
        // A header that paints a purple -> blue -> teal gradient
        JPanel header = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                LinearGradientPaint paint = new LinearGradientPaint(
                        0, 0, getWidth(), 0,
                        new float[]{0f, 0.5f, 1f},
                        new Color[]{PURPLE, BLUE, TEAL});
                g2.setPaint(paint);
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.dispose();
            }
        };
        header.setBorder(BorderFactory.createEmptyBorder(16, 22, 14, 22));

        JLabel title = new JLabel("Library Management System");
        title.setFont(new Font("SansSerif", Font.BOLD, 26));
        title.setForeground(Color.WHITE);

        JLabel subtitle = new JLabel("Manage books, members, borrowing and returns");
        subtitle.setFont(new Font("SansSerif", Font.PLAIN, 13));
        subtitle.setForeground(new Color(0xF1EEFF));

        header.add(title, BorderLayout.NORTH);
        header.add(subtitle, BorderLayout.SOUTH);

        // A thin rainbow strip under the header
        JPanel strip = new JPanel(new GridLayout(1, 5));
        strip.setPreferredSize(new Dimension(10, 6));
        Color[] rainbow = {PINK, ORANGE, YELLOW, TEAL, PURPLE};
        for (Color c : rainbow) {
            JPanel seg = new JPanel();
            seg.setBackground(c);
            strip.add(seg);
        }

        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.add(header, BorderLayout.CENTER);
        wrapper.add(strip, BorderLayout.SOUTH);
        return wrapper;
    }

    private JPanel buildCentre() {
        JPanel centre = new JPanel(new BorderLayout(15, 0));
        centre.setBackground(PAGE);
        centre.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        centre.add(buildControls(), BorderLayout.WEST);
        centre.add(buildOutputPanel(), BorderLayout.CENTER);
        return centre;
    }

    /** Left side: four cards with the input fields and action buttons. */
    private JScrollPane buildControls() {
        JPanel controls = new JPanel();
        controls.setLayout(new BoxLayout(controls, BoxLayout.Y_AXIS));
        controls.setBackground(PAGE);

        // Card 1: add a book
        JPanel bookCard = makeCard("Add a Book", TEAL);
        addRow(bookCard, 1, "Title:", titleField);
        addRow(bookCard, 2, "Author:", authorField);
        JButton addBtn = makeButton("Add Book", TEAL);
        addButtons(bookCard, 3, addBtn);

        // Card 2: register a member
        JPanel memberCard = makeCard("Register a Member", BLUE);
        addRow(memberCard, 1, "Name:", nameField);
        JButton regBtn = makeButton("Register Member", BLUE);
        addButtons(memberCard, 2, regBtn);

        // Card 3: borrow / return
        JPanel borrowCard = makeCard("Borrow / Return", ORANGE);
        addRow(borrowCard, 1, "Book ID:", bookIdField);
        addRow(borrowCard, 2, "Member ID:", memberIdField);
        JButton borrowBtn = makeButton("Borrow", ORANGE);
        JButton returnBtn = makeButton("Return", PINK);
        addButtons(borrowCard, 3, borrowBtn, returnBtn);

        // Card 4: search
        JPanel searchCard = makeCard("Search Books", PURPLE);
        addRow(searchCard, 1, "Keyword:", searchField);
        JButton searchBtn = makeButton("Search", PURPLE);
        addButtons(searchCard, 2, searchBtn);

        addCard(controls, bookCard);
        addCard(controls, memberCard);
        addCard(controls, borrowCard);
        addCard(controls, searchCard);

        // Event handlers
        addBtn.addActionListener(e -> handleAddBook());
        regBtn.addActionListener(e -> handleRegister());
        borrowBtn.addActionListener(e -> handleBorrowReturn(true));
        returnBtn.addActionListener(e -> handleBorrowReturn(false));
        searchBtn.addActionListener(e -> handleSearch());

        // Put the cards in a scroll pane so the user can scroll down to any card
        JScrollPane scroll = new JScrollPane(controls,
                ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getViewport().setBackground(PAGE);
        scroll.getVerticalScrollBar().setUnitIncrement(20);   // faster mouse-wheel scrolling
        // Leave room for the scroll bar so the cards are not squeezed
        scroll.setPreferredSize(new Dimension(
                controls.getPreferredSize().width + 20,
                controls.getPreferredSize().height));
        return scroll;
    }

    /** Right side: heading, output area and the list buttons. */
    private JPanel buildOutputPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 10));
        panel.setBackground(PAGE);

        JLabel heading = new JLabel("Results");
        heading.setFont(new Font("SansSerif", Font.BOLD, 16));
        heading.setForeground(PURPLE);

        output.setEditable(false);
        output.setLineWrap(true);
        output.setWrapStyleWord(true);
        output.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 13));
        output.setBackground(PAPER);
        output.setMargin(new Insets(12, 12, 12, 12));

        JScrollPane scroll = new JScrollPane(output);
        scroll.setBorder(BorderFactory.createLineBorder(PURPLE, 2));
        scroll.setPreferredSize(new Dimension(560, 380));

        // Bottom bar with list buttons
        JPanel bar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        bar.setBackground(PAGE);
        sortBox.setFont(LABEL_FONT);
        JButton showBooks = makeButton("Show Books", BLUE);
        JButton showMembers = makeButton("Show Members", PURPLE);
        JButton clearBtn = makeButton("Clear", GREY);
        bar.add(sortBox);
        bar.add(showBooks);
        bar.add(showMembers);
        bar.add(clearBtn);

        showBooks.addActionListener(e -> handleShowBooks());
        showMembers.addActionListener(e -> showText(library.listMembers()));
        clearBtn.addActionListener(e -> showText(""));

        panel.add(heading, BorderLayout.NORTH);
        panel.add(scroll, BorderLayout.CENTER);
        panel.add(bar, BorderLayout.SOUTH);
        return panel;
    }

    // ---------- Styling helpers ----------

    private static JTextField makeField(int columns) {
        JTextField f = new JTextField(columns);
        f.setFont(LABEL_FONT);
        f.setForeground(TEXT);
        f.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0xCBD3E0)),
                BorderFactory.createEmptyBorder(5, 8, 5, 8)));
        return f;
    }

    private static JButton makeButton(String text, Color base) {
        JButton b = new JButton(text);
        b.setBackground(base);
        b.setForeground(Color.WHITE);
        b.setFont(BOLD_FONT);
        b.setFocusPainted(false);
        b.setBorderPainted(false);
        b.setOpaque(true);
        b.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        // Hover effect: darken the button while the mouse is over it
        b.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) { b.setBackground(base.darker()); }
            @Override
            public void mouseExited(MouseEvent e) { b.setBackground(base); }
        });
        return b;
    }

    /** Mixes a colour with white to make a light pastel shade. */
    private static Color tint(Color c) {
        int r = (int) (c.getRed() * 0.12 + 255 * 0.88);
        int g = (int) (c.getGreen() * 0.12 + 255 * 0.88);
        int b = (int) (c.getBlue() * 0.12 + 255 * 0.88);
        return new Color(r, g, b);
    }

    private static JPanel makeCard(String title, Color accent) {
        JPanel card = new JPanel(new GridBagLayout());
        card.setBackground(tint(accent));     // soft pastel version of the accent colour
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 6, 0, 0, accent),
                BorderFactory.createEmptyBorder(10, 14, 12, 14)));

        JLabel heading = new JLabel(title);
        heading.setFont(new Font("SansSerif", Font.BOLD, 15));
        heading.setForeground(accent);

        GridBagConstraints g = new GridBagConstraints();
        g.gridx = 0;
        g.gridy = 0;
        g.gridwidth = 2;
        g.anchor = GridBagConstraints.WEST;
        g.insets = new Insets(0, 0, 6, 0);
        card.add(heading, g);
        return card;
    }

    private static void addRow(JPanel card, int rowIndex, String labelText, JComponent field) {
        JLabel label = new JLabel(labelText);
        label.setFont(LABEL_FONT);
        label.setForeground(TEXT);

        GridBagConstraints g = new GridBagConstraints();
        g.gridy = rowIndex;
        g.gridx = 0;
        g.anchor = GridBagConstraints.WEST;
        g.insets = new Insets(3, 0, 3, 10);
        card.add(label, g);

        g.gridx = 1;
        g.weightx = 1;
        g.fill = GridBagConstraints.HORIZONTAL;
        g.insets = new Insets(3, 0, 3, 0);
        card.add(field, g);
    }

    private static void addButtons(JPanel card, int rowIndex, JButton... buttons) {
        JPanel row = new JPanel(new GridLayout(1, buttons.length, 8, 0));
        row.setOpaque(false);
        for (JButton b : buttons) {
            row.add(b);
        }
        GridBagConstraints g = new GridBagConstraints();
        g.gridy = rowIndex;
        g.gridx = 0;
        g.gridwidth = 2;
        g.fill = GridBagConstraints.HORIZONTAL;
        g.insets = new Insets(8, 0, 0, 0);
        card.add(row, g);
    }

    /** Adds a card to the left column and keeps it from stretching vertically. */
    private static void addCard(JPanel column, JPanel card) {
        card.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, card.getPreferredSize().height));
        column.add(card);
        column.add(Box.createVerticalStrut(12));
    }

    // ---------- Showing messages ----------

    /** Plain text (lists, welcome message) in the normal dark colour. */
    private void showText(String text) {
        output.setForeground(TEXT);
        output.setText(text);
        output.setCaretPosition(0);
    }

    /** A status message: green for success, red for a problem. */
    private void showMessage(String text, boolean success) {
        output.setForeground(success ? GREEN : RED);
        output.setText(text);
        output.setCaretPosition(0);
    }

    // ---------- Event handlers ----------

    private void handleAddBook() {
        String t = titleField.getText().trim();
        String a = authorField.getText().trim();
        if (t.isEmpty() || a.isEmpty()) {
            showMessage("Please enter both title and author.", false);
        } else if (library.addBook(t, a)) {
            showMessage("Book added: " + t, true);
            titleField.setText("");
            authorField.setText("");
        } else {
            showMessage("The library is full.", false);
        }
    }

    private void handleRegister() {
        String n = nameField.getText().trim();
        if (n.isEmpty()) {
            showMessage("Please enter the member's name.", false);
        } else if (library.registerMember(n)) {
            showMessage("Member registered: " + n, true);
            nameField.setText("");
        } else {
            showMessage("Cannot register more members.", false);
        }
    }

    private void handleBorrowReturn(boolean borrow) {
        try {
            int bId = Integer.parseInt(bookIdField.getText().trim());
            int mId = Integer.parseInt(memberIdField.getText().trim());
            String result;
            boolean success;
            if (borrow) {
                result = library.borrowBook(bId, mId);
                success = result.contains(" borrowed \"");
            } else {
                result = library.returnBook(bId, mId);
                success = result.contains(" was returned by ");
            }
            showMessage(result, success);
        } catch (NumberFormatException ex) {
            showMessage("Book ID and Member ID must be whole numbers.", false);
        }
    }

    private void handleSearch() {
        String k = searchField.getText().trim();
        if (k.isEmpty()) {
            showMessage("Please type a keyword to search.", false);
        } else {
            showText(library.searchBooks(k));
        }
    }

    private void handleShowBooks() {
        int mode;
        switch (sortBox.getSelectedIndex()) {
            case 1:
                mode = Library.SORT_BY_TITLE;
                break;
            case 2:
                mode = Library.SORT_BY_AUTHOR;
                break;
            default:
                mode = Library.SORT_BY_ID;
        }
        showText(library.listBooks(mode));
    }
}
