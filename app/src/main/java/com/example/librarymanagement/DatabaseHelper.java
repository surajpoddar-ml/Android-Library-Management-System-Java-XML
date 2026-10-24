package com.example.librarymanagement;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String TAG = "DatabaseHelper";
    private static final String DATABASE_NAME = "library_management.db";
    private static final int DATABASE_VERSION = 1;

    public static final double DAILY_FINE_RATE = 10.0;
    public static final String DATE_FORMAT = "yyyy-MM-dd";

    public static final String TABLE_BOOKS = "books";
    public static final String TABLE_MEMBERS = "members";
    public static final String TABLE_TRANSACTIONS = "transactions";

    public static final String COL_BOOK_ID = "id";
    public static final String COL_BOOK_TITLE = "title";
    public static final String COL_BOOK_AUTHOR = "author";
    public static final String COL_BOOK_ISBN = "isbn";
    public static final String COL_BOOK_CATEGORY = "category";
    public static final String COL_BOOK_PUBLISHER = "publisher";
    public static final String COL_BOOK_PUB_YEAR = "publication_year";
    public static final String COL_BOOK_QUANTITY = "quantity";
    public static final String COL_BOOK_AVAILABLE_QTY = "available_quantity";
    public static final String COL_BOOK_DESCRIPTION = "description";

    public static final String COL_MEMBER_ID = "id";
    public static final String COL_MEMBER_NAME = "full_name";
    public static final String COL_MEMBER_MEMBERSHIP_ID = "membership_id";
    public static final String COL_MEMBER_EMAIL = "email";
    public static final String COL_MEMBER_PHONE = "phone";
    public static final String COL_MEMBER_ADDRESS = "address";
    public static final String COL_MEMBER_REG_DATE = "registration_date";
    public static final String COL_MEMBER_STATUS = "status";

    public static final String COL_TRANS_ID = "id";
    public static final String COL_TRANS_BOOK_ID = "book_id";
    public static final String COL_TRANS_MEMBER_ID = "member_id";
    public static final String COL_TRANS_ISSUE_DATE = "issue_date";
    public static final String COL_TRANS_DUE_DATE = "due_date";
    public static final String COL_TRANS_RETURN_DATE = "return_date";
    public static final String COL_TRANS_STATUS = "status";
    public static final String COL_TRANS_FINE = "fine";

    private static DatabaseHelper sInstance;

    public static synchronized DatabaseHelper getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new DatabaseHelper(context.getApplicationContext());
        }
        return sInstance;
    }

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
        db.setForeignKeyConstraintsEnabled(true);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createBooksTable = "CREATE TABLE " + TABLE_BOOKS + " ("
                + COL_BOOK_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COL_BOOK_TITLE + " TEXT NOT NULL, "
                + COL_BOOK_AUTHOR + " TEXT NOT NULL, "
                + COL_BOOK_ISBN + " TEXT NOT NULL UNIQUE, "
                + COL_BOOK_CATEGORY + " TEXT NOT NULL, "
                + COL_BOOK_PUBLISHER + " TEXT, "
                + COL_BOOK_PUB_YEAR + " INTEGER, "
                + COL_BOOK_QUANTITY + " INTEGER NOT NULL, "
                + COL_BOOK_AVAILABLE_QTY + " INTEGER NOT NULL, "
                + COL_BOOK_DESCRIPTION + " TEXT);";

        String createMembersTable = "CREATE TABLE " + TABLE_MEMBERS + " ("
                + COL_MEMBER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COL_MEMBER_NAME + " TEXT NOT NULL, "
                + COL_MEMBER_MEMBERSHIP_ID + " TEXT NOT NULL UNIQUE, "
                + COL_MEMBER_EMAIL + " TEXT, "
                + COL_MEMBER_PHONE + " TEXT, "
                + COL_MEMBER_ADDRESS + " TEXT, "
                + COL_MEMBER_REG_DATE + " TEXT NOT NULL, "
                + COL_MEMBER_STATUS + " TEXT NOT NULL);";

        String createTransactionsTable = "CREATE TABLE " + TABLE_TRANSACTIONS + " ("
                + COL_TRANS_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COL_TRANS_BOOK_ID + " INTEGER NOT NULL, "
                + COL_TRANS_MEMBER_ID + " INTEGER NOT NULL, "
                + COL_TRANS_ISSUE_DATE + " TEXT NOT NULL, "
                + COL_TRANS_DUE_DATE + " TEXT NOT NULL, "
                + COL_TRANS_RETURN_DATE + " TEXT, "
                + COL_TRANS_STATUS + " TEXT NOT NULL, "
                + COL_TRANS_FINE + " REAL NOT NULL DEFAULT 0, "
                + "FOREIGN KEY (" + COL_TRANS_BOOK_ID + ") REFERENCES " + TABLE_BOOKS + "(" + COL_BOOK_ID + "), "
                + "FOREIGN KEY (" + COL_TRANS_MEMBER_ID + ") REFERENCES " + TABLE_MEMBERS + "(" + COL_MEMBER_ID + "));";

        db.execSQL(createBooksTable);
        db.execSQL(createMembersTable);
        db.execSQL(createTransactionsTable);

        seedInitialData(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.i(TAG, "Upgrading database from version " + oldVersion + " to " + newVersion);
    }

    private void seedInitialData(SQLiteDatabase db) {
        String today = getTodayDate();

        insertBookInternal(db, "Introduction to Algorithms", "Thomas H. Cormen", "978-0262033848",
                "Computer Science", "MIT Press", 2009, 5, 5, "Comprehensive textbook on data structures and algorithms.");
        insertBookInternal(db, "Clean Code", "Robert C. Martin", "978-0132350884",
                "Software Engineering", "Prentice Hall", 2008, 4, 4, "A handbook of agile software craftsmanship.");
        insertBookInternal(db, "Operating System Concepts", "Abraham Silberschatz", "978-1118063330",
                "Computer Science", "Wiley", 2012, 6, 6, "Foundational principles of modern operating systems.");
        insertBookInternal(db, "Database System Concepts", "Abraham Silberschatz", "978-0073523323",
                "Databases", "McGraw-Hill", 2010, 4, 4, "Fundamental concepts of database management and SQL.");
        insertBookInternal(db, "Effective Java", "Joshua Bloch", "978-0134685991",
                "Programming", "Addison-Wesley", 2018, 5, 5, "Best practices for the Java programming language.");

        insertMemberInternal(db, "Aarav Sharma", "LIB-1001", "aarav.sharma@example.com",
                "+91 98765 43210", "New Delhi, Delhi", today, "Active");
        insertMemberInternal(db, "Priya Patel", "LIB-1002", "priya.patel@example.com",
                "+91 98123 45678", "Mumbai, Maharashtra", today, "Active");
        insertMemberInternal(db, "Rohan Verma", "LIB-1003", "rohan.verma@example.com",
                "+91 97234 56789", "Bengaluru, Karnataka", today, "Active");
    }

    private void insertBookInternal(SQLiteDatabase db, String title, String author, String isbn,
                                    String category, String publisher, int year, int qty, int avail, String desc) {
        ContentValues cv = new ContentValues();
        cv.put(COL_BOOK_TITLE, title);
        cv.put(COL_BOOK_AUTHOR, author);
        cv.put(COL_BOOK_ISBN, isbn);
        cv.put(COL_BOOK_CATEGORY, category);
        cv.put(COL_BOOK_PUBLISHER, publisher);
        cv.put(COL_BOOK_PUB_YEAR, year);
        cv.put(COL_BOOK_QUANTITY, qty);
        cv.put(COL_BOOK_AVAILABLE_QTY, avail);
        cv.put(COL_BOOK_DESCRIPTION, desc);
        db.insert(TABLE_BOOKS, null, cv);
    }

    private void insertMemberInternal(SQLiteDatabase db, String name, String memberId, String email,
                                      String phone, String address, String regDate, String status) {
        ContentValues cv = new ContentValues();
        cv.put(COL_MEMBER_NAME, name);
        cv.put(COL_MEMBER_MEMBERSHIP_ID, memberId);
        cv.put(COL_MEMBER_EMAIL, email);
        cv.put(COL_MEMBER_PHONE, phone);
        cv.put(COL_MEMBER_ADDRESS, address);
        cv.put(COL_MEMBER_REG_DATE, regDate);
        cv.put(COL_MEMBER_STATUS, status);
        db.insert(TABLE_MEMBERS, null, cv);
    }

    public long insertBook(Book book) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COL_BOOK_TITLE, book.getTitle().trim());
        cv.put(COL_BOOK_AUTHOR, book.getAuthor().trim());
        cv.put(COL_BOOK_ISBN, book.getIsbn().trim());
        cv.put(COL_BOOK_CATEGORY, book.getCategory().trim());
        cv.put(COL_BOOK_PUBLISHER, book.getPublisher() != null ? book.getPublisher().trim() : "");
        cv.put(COL_BOOK_PUB_YEAR, book.getPublicationYear());
        cv.put(COL_BOOK_QUANTITY, book.getQuantity());
        cv.put(COL_BOOK_AVAILABLE_QTY, book.getAvailableQuantity());
        cv.put(COL_BOOK_DESCRIPTION, book.getDescription() != null ? book.getDescription().trim() : "");
        return db.insert(TABLE_BOOKS, null, cv);
    }

    public int updateBook(Book book) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COL_BOOK_TITLE, book.getTitle().trim());
        cv.put(COL_BOOK_AUTHOR, book.getAuthor().trim());
        cv.put(COL_BOOK_ISBN, book.getIsbn().trim());
        cv.put(COL_BOOK_CATEGORY, book.getCategory().trim());
        cv.put(COL_BOOK_PUBLISHER, book.getPublisher() != null ? book.getPublisher().trim() : "");
        cv.put(COL_BOOK_PUB_YEAR, book.getPublicationYear());
        cv.put(COL_BOOK_QUANTITY, book.getQuantity());
        cv.put(COL_BOOK_AVAILABLE_QTY, book.getAvailableQuantity());
        cv.put(COL_BOOK_DESCRIPTION, book.getDescription() != null ? book.getDescription().trim() : "");
        return db.update(TABLE_BOOKS, cv, COL_BOOK_ID + " = ?", new String[]{String.valueOf(book.getId())});
    }

    public boolean deleteBook(long bookId) {
        if (getActiveLoansForBook(bookId) > 0) {
            return false;
        }
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_BOOKS, COL_BOOK_ID + " = ?", new String[]{String.valueOf(bookId)}) > 0;
    }

    public Book getBookById(long id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_BOOKS, null, COL_BOOK_ID + " = ?",
                new String[]{String.valueOf(id)}, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            Book book = cursorToBook(cursor);
            cursor.close();
            return book;
        }
        if (cursor != null) {
            cursor.close();
        }
        return null;
    }

    public boolean isIsbnExists(String isbn, long excludeId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT COUNT(*) FROM " + TABLE_BOOKS + " WHERE LOWER(" + COL_BOOK_ISBN + ") = LOWER(?) AND " + COL_BOOK_ID + " != ?";
        Cursor cursor = db.rawQuery(query, new String[]{isbn.trim(), String.valueOf(excludeId)});
        boolean exists = false;
        if (cursor != null && cursor.moveToFirst()) {
            exists = cursor.getInt(0) > 0;
            cursor.close();
        }
        return exists;
    }

    public int getActiveLoansForBook(long bookId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT COUNT(*) FROM " + TABLE_TRANSACTIONS + " WHERE " + COL_TRANS_BOOK_ID + " = ? AND " + COL_TRANS_STATUS + " = 'ISSUED'";
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(bookId)});
        int count = 0;
        if (cursor != null && cursor.moveToFirst()) {
            count = cursor.getInt(0);
            cursor.close();
        }
        return count;
    }

    public List<Book> getAllBooks() {
        List<Book> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_BOOKS, null, null, null, null, null, COL_BOOK_TITLE + " ASC");
        if (cursor != null && cursor.moveToFirst()) {
            do {
                list.add(cursorToBook(cursor));
            } while (cursor.moveToNext());
            cursor.close();
        }
        return list;
    }

    public List<Book> getAvailableBooks() {
        List<Book> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_BOOKS, null, COL_BOOK_AVAILABLE_QTY + " > 0",
                null, null, null, COL_BOOK_TITLE + " ASC");
        if (cursor != null && cursor.moveToFirst()) {
            do {
                list.add(cursorToBook(cursor));
            } while (cursor.moveToNext());
            cursor.close();
        }
        return list;
    }

    public List<Book> searchBooks(String keyword) {
        List<Book> list = new ArrayList<>();
        if (keyword == null || keyword.trim().isEmpty()) {
            return getAllBooks();
        }
        SQLiteDatabase db = this.getReadableDatabase();
        String wildcard = "%" + keyword.trim() + "%";
        String selection = COL_BOOK_TITLE + " LIKE ? OR " + COL_BOOK_AUTHOR + " LIKE ? OR "
                + COL_BOOK_ISBN + " LIKE ? OR " + COL_BOOK_CATEGORY + " LIKE ?";
        Cursor cursor = db.query(TABLE_BOOKS, null, selection,
                new String[]{wildcard, wildcard, wildcard, wildcard}, null, null, COL_BOOK_TITLE + " ASC");
        if (cursor != null && cursor.moveToFirst()) {
            do {
                list.add(cursorToBook(cursor));
            } while (cursor.moveToNext());
            cursor.close();
        }
        return list;
    }

    private Book cursorToBook(Cursor cursor) {
        return new Book(
                cursor.getLong(cursor.getColumnIndexOrThrow(COL_BOOK_ID)),
                cursor.getString(cursor.getColumnIndexOrThrow(COL_BOOK_TITLE)),
                cursor.getString(cursor.getColumnIndexOrThrow(COL_BOOK_AUTHOR)),
                cursor.getString(cursor.getColumnIndexOrThrow(COL_BOOK_ISBN)),
                cursor.getString(cursor.getColumnIndexOrThrow(COL_BOOK_CATEGORY)),
                cursor.getString(cursor.getColumnIndexOrThrow(COL_BOOK_PUBLISHER)),
                cursor.getInt(cursor.getColumnIndexOrThrow(COL_BOOK_PUB_YEAR)),
                cursor.getInt(cursor.getColumnIndexOrThrow(COL_BOOK_QUANTITY)),
                cursor.getInt(cursor.getColumnIndexOrThrow(COL_BOOK_AVAILABLE_QTY)),
                cursor.getString(cursor.getColumnIndexOrThrow(COL_BOOK_DESCRIPTION))
        );
    }

    public long insertMember(Member member) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COL_MEMBER_NAME, member.getFullName().trim());
        cv.put(COL_MEMBER_MEMBERSHIP_ID, member.getMembershipId().trim());
        cv.put(COL_MEMBER_EMAIL, member.getEmail() != null ? member.getEmail().trim() : "");
        cv.put(COL_MEMBER_PHONE, member.getPhone() != null ? member.getPhone().trim() : "");
        cv.put(COL_MEMBER_ADDRESS, member.getAddress() != null ? member.getAddress().trim() : "");
        cv.put(COL_MEMBER_REG_DATE, member.getRegistrationDate());
        cv.put(COL_MEMBER_STATUS, member.getStatus());
        return db.insert(TABLE_MEMBERS, null, cv);
    }

    public int updateMember(Member member) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COL_MEMBER_NAME, member.getFullName().trim());
        cv.put(COL_MEMBER_MEMBERSHIP_ID, member.getMembershipId().trim());
        cv.put(COL_MEMBER_EMAIL, member.getEmail() != null ? member.getEmail().trim() : "");
        cv.put(COL_MEMBER_PHONE, member.getPhone() != null ? member.getPhone().trim() : "");
        cv.put(COL_MEMBER_ADDRESS, member.getAddress() != null ? member.getAddress().trim() : "");
        cv.put(COL_MEMBER_REG_DATE, member.getRegistrationDate());
        cv.put(COL_MEMBER_STATUS, member.getStatus());
        return db.update(TABLE_MEMBERS, cv, COL_MEMBER_ID + " = ?", new String[]{String.valueOf(member.getId())});
    }

    public boolean deleteMember(long memberId) {
        if (getActiveLoansForMember(memberId) > 0) {
            return false;
        }
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_MEMBERS, COL_MEMBER_ID + " = ?", new String[]{String.valueOf(memberId)}) > 0;
    }

    public Member getMemberById(long id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_MEMBERS, null, COL_MEMBER_ID + " = ?",
                new String[]{String.valueOf(id)}, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            Member member = cursorToMember(cursor);
            cursor.close();
            return member;
        }
        if (cursor != null) {
            cursor.close();
        }
        return null;
    }

    public boolean isMembershipIdExists(String memberId, long excludeId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT COUNT(*) FROM " + TABLE_MEMBERS + " WHERE LOWER(" + COL_MEMBER_MEMBERSHIP_ID + ") = LOWER(?) AND " + COL_MEMBER_ID + " != ?";
        Cursor cursor = db.rawQuery(query, new String[]{memberId.trim(), String.valueOf(excludeId)});
        boolean exists = false;
        if (cursor != null && cursor.moveToFirst()) {
            exists = cursor.getInt(0) > 0;
            cursor.close();
        }
        return exists;
    }

    public int getActiveLoansForMember(long memberId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT COUNT(*) FROM " + TABLE_TRANSACTIONS + " WHERE " + COL_TRANS_MEMBER_ID + " = ? AND " + COL_TRANS_STATUS + " = 'ISSUED'";
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(memberId)});
        int count = 0;
        if (cursor != null && cursor.moveToFirst()) {
            count = cursor.getInt(0);
            cursor.close();
        }
        return count;
    }

    public List<Member> getAllMembers() {
        List<Member> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_MEMBERS, null, null, null, null, null, COL_MEMBER_NAME + " ASC");
        if (cursor != null && cursor.moveToFirst()) {
            do {
                list.add(cursorToMember(cursor));
            } while (cursor.moveToNext());
            cursor.close();
        }
        return list;
    }

    public List<Member> getActiveMembers() {
        List<Member> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_MEMBERS, null, COL_MEMBER_STATUS + " = 'Active'",
                null, null, null, COL_MEMBER_NAME + " ASC");
        if (cursor != null && cursor.moveToFirst()) {
            do {
                list.add(cursorToMember(cursor));
            } while (cursor.moveToNext());
            cursor.close();
        }
        return list;
    }

    public List<Member> searchMembers(String keyword) {
        List<Member> list = new ArrayList<>();
        if (keyword == null || keyword.trim().isEmpty()) {
            return getAllMembers();
        }
        SQLiteDatabase db = this.getReadableDatabase();
        String wildcard = "%" + keyword.trim() + "%";
        String selection = COL_MEMBER_NAME + " LIKE ? OR " + COL_MEMBER_MEMBERSHIP_ID + " LIKE ? OR "
                + COL_MEMBER_EMAIL + " LIKE ? OR " + COL_MEMBER_PHONE + " LIKE ?";
        Cursor cursor = db.query(TABLE_MEMBERS, null, selection,
                new String[]{wildcard, wildcard, wildcard, wildcard}, null, null, COL_MEMBER_NAME + " ASC");
        if (cursor != null && cursor.moveToFirst()) {
            do {
                list.add(cursorToMember(cursor));
            } while (cursor.moveToNext());
            cursor.close();
        }
        return list;
    }

    private Member cursorToMember(Cursor cursor) {
        return new Member(
                cursor.getLong(cursor.getColumnIndexOrThrow(COL_MEMBER_ID)),
                cursor.getString(cursor.getColumnIndexOrThrow(COL_MEMBER_NAME)),
                cursor.getString(cursor.getColumnIndexOrThrow(COL_MEMBER_MEMBERSHIP_ID)),
                cursor.getString(cursor.getColumnIndexOrThrow(COL_MEMBER_EMAIL)),
                cursor.getString(cursor.getColumnIndexOrThrow(COL_MEMBER_PHONE)),
                cursor.getString(cursor.getColumnIndexOrThrow(COL_MEMBER_ADDRESS)),
                cursor.getString(cursor.getColumnIndexOrThrow(COL_MEMBER_REG_DATE)),
                cursor.getString(cursor.getColumnIndexOrThrow(COL_MEMBER_STATUS))
        );
    }

    public boolean issueBook(long bookId, long memberId, String issueDate, String dueDate) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();
        try {
            Cursor cursor = db.query(TABLE_BOOKS, new String[]{COL_BOOK_AVAILABLE_QTY},
                    COL_BOOK_ID + " = ?", new String[]{String.valueOf(bookId)}, null, null, null);
            if (cursor == null || !cursor.moveToFirst()) {
                if (cursor != null) cursor.close();
                return false;
            }
            int available = cursor.getInt(0);
            cursor.close();

            if (available <= 0) {
                return false;
            }

            ContentValues bookUpdate = new ContentValues();
            bookUpdate.put(COL_BOOK_AVAILABLE_QTY, available - 1);
            int rowsUpdated = db.update(TABLE_BOOKS, bookUpdate, COL_BOOK_ID + " = ?",
                    new String[]{String.valueOf(bookId)});
            if (rowsUpdated <= 0) {
                return false;
            }

            ContentValues transCv = new ContentValues();
            transCv.put(COL_TRANS_BOOK_ID, bookId);
            transCv.put(COL_TRANS_MEMBER_ID, memberId);
            transCv.put(COL_TRANS_ISSUE_DATE, issueDate);
            transCv.put(COL_TRANS_DUE_DATE, dueDate);
            transCv.putNull(COL_TRANS_RETURN_DATE);
            transCv.put(COL_TRANS_STATUS, "ISSUED");
            transCv.put(COL_TRANS_FINE, 0.0);

            long transId = db.insert(TABLE_TRANSACTIONS, null, transCv);
            if (transId == -1) {
                return false;
            }

            db.setTransactionSuccessful();
            return true;
        } catch (Exception e) {
            Log.e(TAG, "Error issuing book: " + e.getMessage(), e);
            return false;
        } finally {
            db.endTransaction();
        }
    }

    public boolean returnBook(long transactionId, String returnDate) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();
        try {
            Cursor cursor = db.query(TABLE_TRANSACTIONS, null, COL_TRANS_ID + " = ?",
                    new String[]{String.valueOf(transactionId)}, null, null, null);
            if (cursor == null || !cursor.moveToFirst()) {
                if (cursor != null) cursor.close();
                return false;
            }

            String currentStatus = cursor.getString(cursor.getColumnIndexOrThrow(COL_TRANS_STATUS));
            if (!"ISSUED".equalsIgnoreCase(currentStatus)) {
                cursor.close();
                return false;
            }

            long bookId = cursor.getLong(cursor.getColumnIndexOrThrow(COL_TRANS_BOOK_ID));
            String dueDate = cursor.getString(cursor.getColumnIndexOrThrow(COL_TRANS_DUE_DATE));
            cursor.close();

            double fine = calculateFine(dueDate, returnDate);

            ContentValues transCv = new ContentValues();
            transCv.put(COL_TRANS_RETURN_DATE, returnDate);
            transCv.put(COL_TRANS_STATUS, "RETURNED");
            transCv.put(COL_TRANS_FINE, fine);

            int transRows = db.update(TABLE_TRANSACTIONS, transCv, COL_TRANS_ID + " = ?",
                    new String[]{String.valueOf(transactionId)});
            if (transRows <= 0) {
                return false;
            }

            Cursor bookCursor = db.query(TABLE_BOOKS, new String[]{COL_BOOK_QUANTITY, COL_BOOK_AVAILABLE_QTY},
                    COL_BOOK_ID + " = ?", new String[]{String.valueOf(bookId)}, null, null, null);
            if (bookCursor != null && bookCursor.moveToFirst()) {
                int totalQty = bookCursor.getInt(0);
                int availQty = bookCursor.getInt(1);
                bookCursor.close();

                int newAvail = Math.min(totalQty, availQty + 1);
                ContentValues bookUpdate = new ContentValues();
                bookUpdate.put(COL_BOOK_AVAILABLE_QTY, newAvail);
                db.update(TABLE_BOOKS, bookUpdate, COL_BOOK_ID + " = ?", new String[]{String.valueOf(bookId)});
            } else {
                if (bookCursor != null) bookCursor.close();
            }

            db.setTransactionSuccessful();
            return true;
        } catch (Exception e) {
            Log.e(TAG, "Error returning book: " + e.getMessage(), e);
            return false;
        } finally {
            db.endTransaction();
        }
    }

    public static double calculateFine(String dueDateStr, String returnDateStr) {
        if (dueDateStr == null || returnDateStr == null) {
            return 0.0;
        }
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT, Locale.US);
        try {
            Date dueDate = sdf.parse(dueDateStr);
            Date returnDate = sdf.parse(returnDateStr);
            if (dueDate != null && returnDate != null && returnDate.after(dueDate)) {
                long diffInMillis = returnDate.getTime() - dueDate.getTime();
                long days = TimeUnit.DAYS.convert(diffInMillis, TimeUnit.MILLISECONDS);
                if (days > 0) {
                    return days * DAILY_FINE_RATE;
                }
            }
        } catch (ParseException e) {
            Log.e(TAG, "Error parsing dates for fine calculation: " + e.getMessage());
        }
        return 0.0;
    }

    public static long calculateOverdueDays(String dueDateStr, String checkDateStr) {
        if (dueDateStr == null || checkDateStr == null) {
            return 0;
        }
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT, Locale.US);
        try {
            Date dueDate = sdf.parse(dueDateStr);
            Date checkDate = sdf.parse(checkDateStr);
            if (dueDate != null && checkDate != null && checkDate.after(dueDate)) {
                long diff = checkDate.getTime() - dueDate.getTime();
                return TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);
            }
        } catch (ParseException e) {
            Log.e(TAG, "Error parsing dates: " + e.getMessage());
        }
        return 0;
    }

    public List<LibraryTransaction> getActiveLoans() {
        return searchTransactions("", "ISSUED");
    }

    public List<LibraryTransaction> searchTransactions(String query, String statusFilter) {
        List<LibraryTransaction> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        StringBuilder sql = new StringBuilder();
        sql.append("SELECT t.*, b.").append(COL_BOOK_TITLE).append(" AS book_title, ")
           .append("b.").append(COL_BOOK_ISBN).append(" AS book_isbn, ")
           .append("m.").append(COL_MEMBER_NAME).append(" AS member_name, ")
           .append("m.").append(COL_MEMBER_MEMBERSHIP_ID).append(" AS member_code ")
           .append("FROM ").append(TABLE_TRANSACTIONS).append(" t ")
           .append("LEFT JOIN ").append(TABLE_BOOKS).append(" b ON t.").append(COL_TRANS_BOOK_ID).append(" = b.").append(COL_BOOK_ID).append(" ")
           .append("LEFT JOIN ").append(TABLE_MEMBERS).append(" m ON t.").append(COL_TRANS_MEMBER_ID).append(" = m.").append(COL_MEMBER_ID).append(" ")
           .append("WHERE 1=1 ");

        List<String> args = new ArrayList<>();

        if ("ISSUED".equalsIgnoreCase(statusFilter)) {
            sql.append("AND t.").append(COL_TRANS_STATUS).append(" = 'ISSUED' ");
        } else if ("RETURNED".equalsIgnoreCase(statusFilter)) {
            sql.append("AND t.").append(COL_TRANS_STATUS).append(" = 'RETURNED' ");
        } else if ("OVERDUE".equalsIgnoreCase(statusFilter)) {
            String today = getTodayDate();
            sql.append("AND t.").append(COL_TRANS_STATUS).append(" = 'ISSUED' AND t.").append(COL_TRANS_DUE_DATE).append(" < ? ");
            args.add(today);
        }

        if (query != null && !query.trim().isEmpty()) {
            String wildcard = "%" + query.trim() + "%";
            sql.append("AND (b.").append(COL_BOOK_TITLE).append(" LIKE ? OR ")
               .append("m.").append(COL_MEMBER_NAME).append(" LIKE ? OR ")
               .append("m.").append(COL_MEMBER_MEMBERSHIP_ID).append(" LIKE ? OR ")
               .append("t.").append(COL_TRANS_ID).append(" LIKE ?) ");
            args.add(wildcard);
            args.add(wildcard);
            args.add(wildcard);
            args.add(wildcard);
        }

        sql.append("ORDER BY t.").append(COL_TRANS_ID).append(" DESC");

        Cursor cursor = db.rawQuery(sql.toString(), args.toArray(new String[0]));
        if (cursor != null && cursor.moveToFirst()) {
            do {
                LibraryTransaction t = new LibraryTransaction(
                        cursor.getLong(cursor.getColumnIndexOrThrow(COL_TRANS_ID)),
                        cursor.getLong(cursor.getColumnIndexOrThrow(COL_TRANS_BOOK_ID)),
                        cursor.getLong(cursor.getColumnIndexOrThrow(COL_TRANS_MEMBER_ID)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_TRANS_ISSUE_DATE)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_TRANS_DUE_DATE)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_TRANS_RETURN_DATE)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_TRANS_STATUS)),
                        cursor.getDouble(cursor.getColumnIndexOrThrow(COL_TRANS_FINE))
                );
                t.setBookTitle(cursor.getString(cursor.getColumnIndexOrThrow("book_title")));
                t.setBookIsbn(cursor.getString(cursor.getColumnIndexOrThrow("book_isbn")));
                t.setMemberFullName(cursor.getString(cursor.getColumnIndexOrThrow("member_name")));
                t.setMemberMembershipId(cursor.getString(cursor.getColumnIndexOrThrow("member_code")));
                list.add(t);
            } while (cursor.moveToNext());
            cursor.close();
        }
        return list;
    }

    public int getTotalBookTitles() {
        return getCount("SELECT COUNT(*) FROM " + TABLE_BOOKS);
    }

    public int getTotalBookCopies() {
        return getCount("SELECT COALESCE(SUM(" + COL_BOOK_QUANTITY + "), 0) FROM " + TABLE_BOOKS);
    }

    public int getAvailableCopies() {
        return getCount("SELECT COALESCE(SUM(" + COL_BOOK_AVAILABLE_QTY + "), 0) FROM " + TABLE_BOOKS);
    }

    public int getIssuedCopies() {
        return getCount("SELECT COUNT(*) FROM " + TABLE_TRANSACTIONS + " WHERE " + COL_TRANS_STATUS + " = 'ISSUED'");
    }

    public int getTotalMembers() {
        return getCount("SELECT COUNT(*) FROM " + TABLE_MEMBERS);
    }

    public int getOverdueLoansCount() {
        String today = getTodayDate();
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT COUNT(*) FROM " + TABLE_TRANSACTIONS + " WHERE " + COL_TRANS_STATUS + " = 'ISSUED' AND " + COL_TRANS_DUE_DATE + " < ?";
        Cursor cursor = db.rawQuery(query, new String[]{today});
        int count = 0;
        if (cursor != null && cursor.moveToFirst()) {
            count = cursor.getInt(0);
            cursor.close();
        }
        return count;
    }

    private int getCount(String sql) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(sql, null);
        int count = 0;
        if (cursor != null && cursor.moveToFirst()) {
            count = cursor.getInt(0);
            cursor.close();
        }
        return count;
    }

    public static String getTodayDate() {
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT, Locale.US);
        return sdf.format(new Date());
    }
}
