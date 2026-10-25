package com.airticket.bd

import com.airticket.bd.accounting.AccountingEngine
import org.junit.Assert.assertEquals
import org.junit.Test

class AccountingCalculationTest {

    @Test
    fun testSection8_RefundTestCase1_FullRefundPaidTicket() {
        // Original Fare = ৳10,000, Refund Charge = ৳1,000, Paid = ৳10,000
        val originalFare = AccountingEngine.takaToPoisha(10000.0)
        val refundCharge = AccountingEngine.takaToPoisha(1000.0)
        val amountPaid = AccountingEngine.takaToPoisha(10000.0)

        val result = AccountingEngine.calculateRefund(originalFare, refundCharge, amountPaid)
        assertEquals(AccountingEngine.takaToPoisha(9000.0), result.refundablePoisha)
        assertEquals(AccountingEngine.takaToPoisha(9000.0), result.refundPayableToCustomerPoisha)
        assertEquals(0L, result.remainingAmountPayableByCustomerPoisha)
    }

    @Test
    fun testSection8_RefundTestCase2_UnpaidTicketRefund() {
        // Original Fare = ৳10,000, Refund Charge = ৳1,000, Paid = ৳0
        val originalFare = AccountingEngine.takaToPoisha(10000.0)
        val refundCharge = AccountingEngine.takaToPoisha(1000.0)
        val amountPaid = 0L

        val result = AccountingEngine.calculateRefund(originalFare, refundCharge, amountPaid)
        assertEquals(AccountingEngine.takaToPoisha(9000.0), result.refundablePoisha)
        // Zero cash payout for unpaid ticket!
        assertEquals(0L, result.refundPayableToCustomerPoisha)
        // Customer owes ৳1,000 cancellation fee
        assertEquals(AccountingEngine.takaToPoisha(1000.0), result.remainingAmountPayableByCustomerPoisha)
    }

    @Test
    fun testSection8_RescheduleTestCase3_CaseA_EqualFare() {
        // New fare equal to original fare (৳12,000 = ৳12,000), Fee = ৳1,500
        val originalFare = AccountingEngine.takaToPoisha(12000.0)
        val newFare = AccountingEngine.takaToPoisha(12000.0)
        val fee = AccountingEngine.takaToPoisha(1500.0)

        val result = AccountingEngine.calculateReschedule(originalFare, newFare, fee, 0L)
        assertEquals(0L, result.fareDifferencePoisha)
        assertEquals(AccountingEngine.takaToPoisha(1500.0), result.totalAdditionalDuePoisha)
    }

    @Test
    fun testSection8_RescheduleTestCase4_CaseB_HigherFare() {
        // New fare higher (Original ৳12,000, New ৳15,000), Fee = ৳1,500
        val originalFare = AccountingEngine.takaToPoisha(12000.0)
        val newFare = AccountingEngine.takaToPoisha(15000.0)
        val fee = AccountingEngine.takaToPoisha(1500.0)

        val result = AccountingEngine.calculateReschedule(originalFare, newFare, fee, 0L)
        assertEquals(AccountingEngine.takaToPoisha(3000.0), result.fareDifferencePoisha)
        assertEquals(AccountingEngine.takaToPoisha(4500.0), result.totalAdditionalDuePoisha)
    }

    @Test
    fun testSection8_RescheduleTestCase5_CaseC_LowerFare() {
        // New fare lower (Original ৳12,000, New ৳10,000), Fee = ৳1,500
        val originalFare = AccountingEngine.takaToPoisha(12000.0)
        val newFare = AccountingEngine.takaToPoisha(10000.0)
        val fee = AccountingEngine.takaToPoisha(1500.0)

        val result = AccountingEngine.calculateReschedule(originalFare, newFare, fee, 0L)
        // No automatic negative credit
        assertEquals(0L, result.fareDifferencePoisha)
        assertEquals(AccountingEngine.takaToPoisha(1500.0), result.totalAdditionalDuePoisha)
    }

    @Test
    fun testSection8_TestCase6_RepeatRescheduling() {
        val originalFare = AccountingEngine.takaToPoisha(20000.0)
        // Reschedule 1: New fare ৳22,000, Fee ৳2,000 -> Additional ৳4,000
        val res1 = AccountingEngine.calculateReschedule(originalFare, AccountingEngine.takaToPoisha(22000.0), AccountingEngine.takaToPoisha(2000.0), 0L)
        assertEquals(AccountingEngine.takaToPoisha(4000.0), res1.totalAdditionalDuePoisha)

        // Reschedule 2: New fare ৳25,000, Fee ৳2,500 -> Additional ৳5,500
        val res2 = AccountingEngine.calculateReschedule(AccountingEngine.takaToPoisha(22000.0), AccountingEngine.takaToPoisha(25000.0), AccountingEngine.takaToPoisha(2500.0), 0L)
        assertEquals(AccountingEngine.takaToPoisha(5500.0), res2.totalAdditionalDuePoisha)
    }
}