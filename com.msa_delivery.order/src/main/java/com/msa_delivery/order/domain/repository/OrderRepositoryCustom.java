package com.msa_delivery.order.domain.repository;

import com.msa_delivery.order.domain.model.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepositoryCustom {

    Page<Order> findOrdersWithSearch(Pageable pageable, String search);

}
