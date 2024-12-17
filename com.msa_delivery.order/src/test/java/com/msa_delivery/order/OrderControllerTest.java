package com.msa_delivery.order;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.msa_delivery.order.application.dto.OrderDataDto;
import com.msa_delivery.order.application.dto.OrderRequestDto;
import com.msa_delivery.order.application.dto.ResponseDto;
import com.msa_delivery.order.application.service.OrderService;
import com.msa_delivery.order.presentation.controller.OrderController;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(OrderController.class)
class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private OrderService orderService;

    @Autowired
    private ObjectMapper objectMapper;

    private OrderRequestDto.Create createOrderRequest;
    private OrderRequestDto.Update updateOrderRequest;
    private ResponseDto<Page<OrderDataDto>> orderResponse;

    @BeforeEach
    void setUp() {
        createOrderRequest = OrderRequestDto.Create.builder()
            .receiver_id(UUID.randomUUID())
            .supplier_id(UUID.randomUUID())
            .product_id(UUID.randomUUID())
            .quantity(10)
            .request("2024-12-25T10:00:00")
            .build();

        updateOrderRequest = OrderRequestDto.Update.builder()
            .quantity(20)
            .request("2024-12-26T12:00:00")
            .build();
    }


    @Test
    @DisplayName("POST /api/orders - Create Order")
    void createOrderTest() throws Exception {
        ResponseDto<OrderDataDto> orderResponse = new ResponseDto<>(200, "Success", new OrderDataDto());

        Mockito.when(orderService.createOrder(any(OrderRequestDto.Create.class), anyString())).thenReturn(orderResponse);

        mockMvc.perform(post("/api/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .header("X-User_Id", "12345")
                .header("X-Username", "testuser")
                .header("X-Role", "USER")
                .content(objectMapper.writeValueAsString(createOrderRequest)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value(200))
            .andExpect(jsonPath("$.message").value("Success"));
    }

    @Test
    @DisplayName("PUT /api/orders/{order_id} - Update Order")
    void updateOrderTest() throws Exception {
        ResponseDto<OrderDataDto> orderResponse = new ResponseDto<>(200, "Success", new OrderDataDto());

        UUID orderId = UUID.randomUUID();
        Mockito.when(orderService.updateOrder(any(UUID.class), any(OrderRequestDto.Update.class))).thenReturn(orderResponse);

        mockMvc.perform(put("/api/orders/" + orderId)
                .contentType(MediaType.APPLICATION_JSON)
                .header("X-User_Id", "12345")
                .header("X-Username", "testuser")
                .header("X-Role", "USER")
                .content(objectMapper.writeValueAsString(updateOrderRequest)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value(200))
            .andExpect(jsonPath("$.message").value("Success"));
    }

    @Test
    @DisplayName("DELETE /api/orders/{order_id} - Delete Order")
    void deleteOrderTest() throws Exception {
        ResponseDto<OrderDataDto> orderResponse = new ResponseDto<>(200, "Success", new OrderDataDto());

        UUID orderId = UUID.randomUUID();
        Mockito.when(orderService.deleteOrder(any(UUID.class), anyString())).thenReturn(orderResponse);

        mockMvc.perform(delete("/api/orders/" + orderId)
                .header("X-User_Id", "12345")
                .header("X-Username", "testuser")
                .header("X-Role", "USER"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value(200))
            .andExpect(jsonPath("$.message").value("Success"));
    }

    @Test
    @DisplayName("GET /api/orders/{order_id} - Get Order By ID")
    void getOrderTest() throws Exception {
        ResponseDto<OrderDataDto> orderResponse = new ResponseDto<>(200, "Success", new OrderDataDto());

        UUID orderId = UUID.randomUUID();
        Mockito.when(orderService.getOrder(any(UUID.class))).thenReturn(orderResponse);

        mockMvc.perform(get("/api/orders/" + orderId)
                .header("X-User_Id", "12345")
                .header("X-Username", "testuser")
                .header("X-Role", "USER"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value(200))
            .andExpect(jsonPath("$.message").value("Success"));
    }

    @Test
    @DisplayName("GET /api/orders - Get All Orders")
    void getAllOrdersTest() throws Exception {
        // 하나의 OrderDataDto 객체를 포함한 페이지 생성
        Page<OrderDataDto> orderDataDtoPage = new PageImpl<>(List.of(new OrderDataDto()));
        // 페이지를 ResponseDto로 래핑
        ResponseDto<Page<OrderDataDto>> orderResponse = new ResponseDto<>(200, "Success", orderDataDtoPage);

        // 서비스 호출을 Mocking
        Mockito.when(orderService.getAllOrders(any(Integer.class), any(Integer.class), anyString(), anyString(), anyString(), anyString(), anyString(), anyString())).thenReturn(orderResponse);

        String responseContent = mockMvc.perform(get("/api/orders")
                .header("X-User_Id", "12345")
                .header("X-Username", "testuser")
                .header("X-Role", "USER")
                .param("page_number", "1")
                .param("page_size", "10"))
            .andExpect(status().isOk())  // 상태 코드 200 확인
            .andReturn()
            .getResponse()
            .getContentAsString();

        System.out.println("Response: " + responseContent);
    }

}
