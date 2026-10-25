package com.airticket.bd.accounting

import kotlin.math.max
import kotlin.math.min

object AccountingEngine {
    const val BDT_POISHA_FACTOR = 100L

    fun takaToPoisha(taka: Double): Long = (taka * BDT_POISHA_FACTOR).toLong()
    fun poishaToTaka(poisha: Long): Double = poisha.toDouble() / BDT_POISHA_FACTOR

    fun formatBDT(poisha: Long, isBangla: Boolean = true): String {
        val taka = poishaToTaka(poisha)
        val formattedEn = String.format("%,.2f", taka)
        if (!isBangla) return "৳ $formattedEn"

        val bnDigits = charArrayOf('০', '১', '২', '৩', '৪', '৫', '৬', '৭', '৮', '৯')
        val sb = StringBuilder()
        for (ch in formattedEn) {
            if (ch in '0'..'9') sb.append(bnDigits[ch - '0']) else sb.append(ch)
        }
        return "৳ $sb"
    }

    data class FareResult(val subtotalPoisha: Long, val totalFarePoisha: Long)

    data class TieredFareResult(
        val subtotalAdultPoisha: Long,
        val subtotalChildPoisha: Long,
        val subtotalInfantPoisha: Long,
        val subtotalPoisha: Long,
        val totalFarePoisha: Long,
        val totalPassengers: Int
    )

    fun calculateTieredTicketFare(
        adultCount: Int,
        adultFarePoisha: Long,
        childCount: Int,
        childFarePoisha: Long,
        infantCount: Int,
        infantFarePoisha: Long,
        additionalChargesPoisha: Long = 0L,
        discountPoisha: Long = 0L
    ): TieredFareResult {
        val aSub = max(0, adultCount) * max(0L, adultFarePoisha)
        val cSub = max(0, childCount) * max(0L, childFarePoisha)
        val iSub = max(0, infantCount) * max(0L, infantFarePoisha)
        val subtotal = aSub + cSub + iSub
        val gross = subtotal + max(0L, additionalChargesPoisha)
        val validDiscount = min(gross, max(0L, discountPoisha))
        return TieredFareResult(
            subtotalAdultPoisha = aSub,
            subtotalChildPoisha = cSub,
            subtotalInfantPoisha = iSub,
            subtotalPoisha = subtotal,
            totalFarePoisha = max(0L, gross - validDiscount),
            totalPassengers = max(0, adultCount) + max(0, childCount) + max(0, infantCount)
        )
    }

    fun calculateTicketFare(
        passengerCount: Int,
        farePerPassengerPoisha: Long,
        additionalChargesPoisha: Long = 0L,
        discountPoisha: Long = 0L
    ): FareResult {
        val subtotal = max(0, passengerCount) * max(0L, farePerPassengerPoisha)
        val gross = subtotal + max(0L, additionalChargesPoisha)
        val validDiscount = min(gross, max(0L, discountPoisha))
        return FareResult(subtotal, gross - validDiscount)
    }

    data class RefundCalculation(
        val refundablePoisha: Long,
        val netCustomerBalanceImpactPoisha: Long,
        val refundPayableToCustomerPoisha: Long,
        val remainingAmountPayableByCustomerPoisha: Long
    )

    /**
     * Exact Aviation Refund Rules:
     * Refundable Amount = Original Ticket Fare - Refund Charge
     * Net Customer Balance Impact = (Original Fare - Refund Charge) - Amount Already Paid and Retained
     */
    fun calculateRefund(
        originalFarePoisha: Long,
        refundChargePoisha: Long,
        retainedPriorPaymentPoisha: Long
    ): RefundCalculation {
        val boundedCharge = min(max(0L, refundChargePoisha), max(0L, originalFarePoisha))
        val refundable = max(0L, originalFarePoisha - boundedCharge)
        val netImpact = refundable - retainedPriorPaymentPoisha

        val payableToCustomer = if (retainedPriorPaymentPoisha > 0) {
            max(0L, min(refundable, retainedPriorPaymentPoisha - boundedCharge))
        } else 0L

        val payableByCustomer = if (retainedPriorPaymentPoisha == 0L) {
            boundedCharge
        } else if (netImpact > 0 && retainedPriorPaymentPoisha < boundedCharge) {
            boundedCharge - retainedPriorPaymentPoisha
        } else 0L

        return RefundCalculation(
            refundablePoisha = refundable,
            netCustomerBalanceImpactPoisha = netImpact,
            refundPayableToCustomerPoisha = payableToCustomer,
            remainingAmountPayableByCustomerPoisha = payableByCustomer
        )
    }

    data class RescheduleCalculation(
        val fareDifferencePoisha: Long,
        val totalAdditionalDuePoisha: Long
    )

    /**
     * Incremental Aviation Reschedule Rules:
     * Fare Difference = max(0, New Fare - Original Fare)
     * Additional Due = Fare Difference + Reschedule Charge + Other Charges
     * Original fare is never duplicated!
     */
    fun calculateReschedule(
        originalFarePoisha: Long,
        newFarePoisha: Long,
        rescheduleChargePoisha: Long,
        otherChargesPoisha: Long = 0L
    ): RescheduleCalculation {
        val fareDiff = max(0L, newFarePoisha - originalFarePoisha)
        val totalDue = fareDiff + max(0L, rescheduleChargePoisha) + max(0L, otherChargesPoisha)
        return RescheduleCalculation(
            fareDifferencePoisha = fareDiff,
            totalAdditionalDuePoisha = totalDue
        )
    }

    fun derivePaymentStatus(totalFarePoisha: Long, totalPaidPoisha: Long): String = when {
        totalPaidPoisha <= 0L -> "UNPAID"
        totalPaidPoisha >= totalFarePoisha -> "PAID"
        else -> "PARTIAL"
    }
}