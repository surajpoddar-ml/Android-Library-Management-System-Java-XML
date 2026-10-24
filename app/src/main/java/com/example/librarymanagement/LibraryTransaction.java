package com.example.librarymanagement;

import java.io.Serializable;

public class LibraryTransaction implements Serializable {
    private long id;
    private long bookId;
    private long memberId;
    private String issueDate;
    private String dueDate;
    private String returnDate;
    private String status;
    private double fine;

    private String bookTitle;
    private String memberFullName;
    private String memberMembershipId;
    private String bookIsbn;

    public LibraryTransaction() {
    }

    public LibraryTransaction(long id, long bookId, long memberId, String issueDate,
                              String dueDate, String returnDate, String status, double fine) {
        this.id = id;
        this.bookId = bookId;
        this.memberId = memberId;
        this.issueDate = issueDate;
        this.dueDate = dueDate;
        this.returnDate = returnDate;
        this.status = status;
        this.fine = fine;
    }

    public LibraryTransaction(long bookId, long memberId, String issueDate, String dueDate) {
        this(-1, bookId, memberId, issueDate, dueDate, null, "ISSUED", 0.0);
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getBookId() {
        return bookId;
    }

    public void setBookId(long bookId) {
        this.bookId = bookId;
    }

    public long getMemberId() {
        return memberId;
    }

    public void setMemberId(long memberId) {
        this.memberId = memberId;
    }

    public String getIssueDate() {
        return issueDate;
    }

    public void setIssueDate(String issueDate) {
        this.issueDate = issueDate;
    }

    public String getDueDate() {
        return dueDate;
    }

    public void setDueDate(String dueDate) {
        this.dueDate = dueDate;
    }

    public String getReturnDate() {
        return returnDate;
    }

    public void setReturnDate(String returnDate) {
        this.returnDate = returnDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public double getFine() {
        return fine;
    }

    public void setFine(double fine) {
        this.fine = fine;
    }

    public String getBookTitle() {
        return bookTitle != null ? bookTitle : "Book #" + bookId;
    }

    public void setBookTitle(String bookTitle) {
        this.bookTitle = bookTitle;
    }

    public String getMemberFullName() {
        return memberFullName != null ? memberFullName : "Member #" + memberId;
    }

    public void setMemberFullName(String memberFullName) {
        this.memberFullName = memberFullName;
    }

    public String getMemberMembershipId() {
        return memberMembershipId;
    }

    public void setMemberMembershipId(String memberMembershipId) {
        this.memberMembershipId = memberMembershipId;
    }

    public String getBookIsbn() {
        return bookIsbn;
    }

    public void setBookIsbn(String bookIsbn) {
        this.bookIsbn = bookIsbn;
    }

    public boolean isReturned() {
        return "RETURNED".equalsIgnoreCase(status);
    }
}
