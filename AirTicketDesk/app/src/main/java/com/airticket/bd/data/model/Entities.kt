package com.airticket.bd.data.model

import androidx.room.*
import java.util.UUID

@Entity(tableName = "customers")
data class CustomerEntity(
    @PrimaryKey val id: String = "CUST-" + UUID.randomUUID().toString().take(8).uppercase(),
    val name: String,
    val mobile: String,
    val email: String? = null,
    val address: String? = null,
    val profession: String? = null,
    val designation: String? = null,
    val notes: String? = null,
    val isArchived: Boolean = false,
    val createdAtEpochMs: Long = System.currentTimeMillis(),
    val updatedAtEpochMs: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "ticket_sales",
    indices = [Index(value = ["pnr"]), Index(value = ["customerId"])]
)
data class TicketSaleEntity(
    @PrimaryKey val id: String, // TKT-2026-001
    val customerId: String,
    val customerName: String,
    val customerMobile: String,
    val issueDateEpochMs: Long = System.currentTimeMillis(),
    val airline: String? = null,
    val pnr: String, // MANDATORY PNR / Booking Reference
    val eTicketNumber: String? = null,
    val ticketType: String = "One-way",
    val fromCity: String,
    val toCity: String,
    val departureDate: String,
    val returnDate: String? = null,
    val passengerCount: Int = 1,
    // Integer minor currency units: 1 BDT = 100 Poisha
    val farePerPassengerPoisha: Long,
    val additionalChargesPoisha: Long = 0L,
    val discountPoisha: Long = 0L,
    val totalFarePoisha: Long,
    val totalPaidPoisha: Long = 0L,
    val outstandingDuePoisha: Long = totalFarePoisha,
    val paymentStatus: String = "UNPAID",
    val lifecycleStatus: String = "Confirmed", // Confirmed, Partially Paid, Fully Paid, Rescheduled, Refund Pending, Partially Refunded, Refunded, Cancelled
    val initialPaymentMethod: String? = null,
    val paymentReference: String? = null,
    val notes: String? = null,
    val isCancelled: Boolean = false,
    val createdAtEpochMs: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "passengers",
    foreignKeys = [
        ForeignKey(
            entity = TicketSaleEntity::class,
            parentColumns = ["id"],
            childColumns = ["ticketSaleId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["ticketSaleId"]), Index(value = ["pnr"])]
)
data class PassengerEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val ticketSaleId: String,
    val pnr: String,
    val name: String,
    val passengerType: String = "Adult",
    val eTicketNumber: String? = null,
    val farePoisha: Long,
    val isRefunded: Boolean = false,
    val isRescheduled: Boolean = false,
    val refundId: String? = null,
    val rescheduleId: String? = null
)

@Entity(
    tableName = "refunds",
    foreignKeys = [
        ForeignKey(
            entity = TicketSaleEntity::class,
            parentColumns = ["id"],
            childColumns = ["ticketSaleId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["ticketSaleId"]), Index(value = ["pnr"]), Index(value = ["voucherNumber"])]
)
data class RefundEntity(
    @PrimaryKey val id: String, // REF-2026-001
    val voucherNumber: String, // RFV-2026-001
    val ticketSaleId: String,
    val pnr: String,
    val passengerId: String? = null,
    val passengerName: String,
    val customerId: String,
    val customerName: String,
    val refundDateEpochMs: Long = System.currentTimeMillis(),
    val originalFarePoisha: Long,
    val refundChargePoisha: Long,
    val refundablePoisha: Long,
    val amountDisbursedPoisha: Long,
    val refundMethod: String,
    val referenceNumber: String? = null,
    val reason: String,
    val status: String = "APPROVED", // APPROVED, DISBURSED, CANCELLED
    val notes: String? = null
)

@Entity(
    tableName = "reschedules",
    foreignKeys = [
        ForeignKey(
            entity = TicketSaleEntity::class,
            parentColumns = ["id"],
            childColumns = ["ticketSaleId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["ticketSaleId"]), Index(value = ["pnr"])]
)
data class RescheduleEntity(
    @PrimaryKey val id: String, // RSC-2026-001
    val ticketSaleId: String,
    val pnr: String,
    val passengerId: String? = null,
    val passengerName: String,
    val rescheduleDateEpochMs: Long = System.currentTimeMillis(),
    val originalDepartureDate: String,
    val newDepartureDate: String,
    val originalReturnDate: String? = null,
    val newReturnDate: String? = null,
    val originalFarePoisha: Long,
    val newFarePoisha: Long,
    val rescheduleChargePoisha: Long,
    val otherChargesPoisha: Long = 0L,
    val fareDifferencePoisha: Long,
    val totalAdditionalDuePoisha: Long,
    val amountCollectedNowPoisha: Long = 0L,
    val paymentMethod: String? = null,
    val reason: String,
    val notes: String? = null
)

@Entity(tableName = "payments")
data class PaymentEntity(
    @PrimaryKey val id: String,
    val receiptNumber: String,
    val customerId: String,
    val customerName: String,
    val customerMobile: String,
    val paymentDateEpochMs: Long = System.currentTimeMillis(),
    val amountPoisha: Long,
    val paymentMethod: String,
    val referenceNumber: String? = null,
    val allocationMethod: String,
    val unallocatedAdvancePoisha: Long = 0L,
    val notes: String? = null,
    val isDeleted: Boolean = false
)

@Entity(tableName = "payment_allocations")
data class PaymentAllocationEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val paymentId: String,
    val ticketSaleId: String,
    val amountPoisha: Long,
    val createdAtEpochMs: Long = System.currentTimeMillis()
)

@Entity(tableName = "expenses")
data class ExpenseEntity(
    @PrimaryKey val id: String,
    val dateEpochMs: Long,
    val category: String,
    val description: String,
    val amountPoisha: Long,
    val paymentMethod: String,
    val referenceNumber: String? = null
)