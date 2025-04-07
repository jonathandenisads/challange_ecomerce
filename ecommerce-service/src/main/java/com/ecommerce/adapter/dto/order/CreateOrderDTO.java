package com.ecommerce.adapter.dto.order;

import com.ecommerce.adapter.enums.PaymentMethod;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;


import java.util.List;

public record CreateOrderDTO(
        @NotNull(message = "Itens são obrigatórios")
        @Valid
        List<CreateOrderItemDTO> items,

        @NotNull(message = "Método de pagamento é obrigatório")
        PaymentMethod paymentMethod
) {}
