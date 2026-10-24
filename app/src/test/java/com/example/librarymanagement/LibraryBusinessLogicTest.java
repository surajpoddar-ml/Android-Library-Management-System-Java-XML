package com.example.librarymanagement;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class LibraryBusinessLogicTest {
    @Test
    public void testOnTimeReturnHasZeroFine() {
        double fine = DatabaseHelper.calculateFine("2026-10-20", "2026-10-20");
        assertEquals(0.0, fine, 0.001);
    }

    @Test
    public void testEarlyReturnHasZeroFine() {
        double fine = DatabaseHelper.calculateFine("2026-10-20", "2026-10-18");
        assertEquals(0.0, fine, 0.001);
    }

    @Test
    public void testOverdueFineCalculation() {
        double fine = DatabaseHelper.calculateFine("2026-11-10", "2026-11-13");
        assertEquals(30.0, fine, 0.001);
    }

    @Test
    public void testOverdueDaysCalculation() {
        long days = DatabaseHelper.calculateOverdueDays("2026-10-10", "2026-10-15");
        assertEquals(5, days);

        long zeroDays = DatabaseHelper.calculateOverdueDays("2026-10-15", "2026-10-10");
        assertEquals(0, zeroDays);
    }

    @Test
    public void testBookAvailabilityConsistency() {
        Book book = new Book("Clean Architecture", "Robert C. Martin", "978-0134494166",
                "Software Engineering", "Prentice Hall", 2017, 5, 5, "Architecture guide");

        assertEquals(5, book.getQuantity());
        assertEquals(5, book.getAvailableQuantity());

        book.setAvailableQuantity(3);
        int onLoan = book.getQuantity() - book.getAvailableQuantity();
        assertEquals(2, onLoan);

        int newTotal = 7;
        int newAvailable = newTotal - onLoan;
        book.setQuantity(newTotal);
        book.setAvailableQuantity(newAvailable);

        assertEquals(7, book.getQuantity());
        assertEquals(5, book.getAvailableQuantity());
    }

    @Test
    public void testMemberStatus() {
        Member activeMember = new Member("Aarav Sharma", "LIB-1001", "aarav@test.com",
                "+91 98765 43210", "New Delhi", "2026-10-01", "Active");
        assertTrue(activeMember.isActive());

        Member inactiveMember = new Member("Inactive User", "LIB-1099", "user@test.com",
                "+91 98123 45678", "Mumbai", "2026-10-01", "Inactive");
        assertFalse(inactiveMember.isActive());
    }
}
