package com.mengo.product.domain.service

import com.mengo.product.domain.model.command.SagaCommand

interface ProductService {
    fun onReserveProduct(command: SagaCommand.ReserveProduct)

    fun onReleaseProduct(command: SagaCommand.ReleaseProduct)
}
