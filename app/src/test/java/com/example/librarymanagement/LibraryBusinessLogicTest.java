package com.example.librarymanagement;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Unit tests verifying business logic for fine calculations, date calculations, and model constraints.
 */
public class LibraryBusinessLogicTest {

    @Test
    public void testOnTimeReturnHasZeroFine() {
        // Due date: 2026-10-20, Return date: 2026-10-20
        double fine = DatabaseHelper.calculateFine("2026-10-20", "2026-10-20");
        assertEquals(0.0, fine, 0.001);
    }

    @Test
    public void testEarlyReturnHasZeroFine() {
        // Due date: 2026-10-20, Return date: 2026-10-18
        double fine = DatabaseHelper.calculateFine("2026-10-20", "2026-10-18");
        assertEquals(0.0, fine, 0.001);
    }

    @Test
    public void testOverdueFineCalculation() {
        // Due date: 2026-11-10, Return date: 2026-11-13 (3 days overdue)
        // Rate: NPR 10.0 per day => 3 * 10 = NPR 30.0
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

        // Simulate 2 loans
        book.setAvailableQuantity(3);
        int onLoan = book.getQuantity() - book.getAvailableQuantity();
        assertEquals(2, onLoan);

        // If total quantity is increased to 7, new available quantity becomes 7 - 2 = 5
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
                "9800000000", "Kathmandu", "2026-10-01", "Active");
        assertTrue(activeMember.isActive());

        Member inactiveMember = new Member("Inactive User", "LIB-1099", "user@test.com",
                "9800000001", "Pokhara", "2026-10-01", "Inactive");
        assertFalse(inactiveMember.isActive());
    }
}
