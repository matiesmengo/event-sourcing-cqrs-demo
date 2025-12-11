package com.mengo.payment.domain.service

import com.mengo.payment.domain.model.command.PaymentCommand

interface PaymentService {
    fun onRequestPayment(command: PaymentCommand.BookingPayment)

    fun onPaymentInitiated(command: PaymentCommand.PaymentInitiated)
}
