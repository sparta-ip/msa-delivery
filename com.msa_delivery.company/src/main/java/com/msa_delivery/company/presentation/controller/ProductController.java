package com.msa_delivery.company.presentation.controller;

import com.msa_delivery.company.application.dto.CommonResponse;
import com.msa_delivery.company.application.dto.ProductDto;
import com.msa_delivery.company.application.service.ProductService;
import com.msa_delivery.company.presentation.request.ProductRequest;
import com.msa_delivery.company.presentation.request.ProductUpdateRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @PostMapping
    public ResponseEntity<?> creatProduct(@Valid @RequestBody ProductRequest request,
                                           @RequestHeader("X-User_Id") Long userId,
                                           @RequestHeader("X-Username") String username,
                                           @RequestHeader("X-Role") String role) {
        try {
            ProductDto product = productService.createProduct(request, userId, username, role);
            return ResponseEntity.status(HttpStatus.CREATED).body(
                    CommonResponse.success(HttpStatus.CREATED, "상품 생성이 완료되었습니다.", product)
            );
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(
                    CommonResponse.error(HttpStatus.FORBIDDEN, e.getMessage())
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    CommonResponse.error(HttpStatus.INTERNAL_SERVER_ERROR, "상품 생성 중 오류가 발생했습니다.")
            );
        }
    }

    @PutMapping("/{productId}")
    public ResponseEntity<?> updateProduct(@PathVariable UUID productId,
                                           @RequestBody ProductUpdateRequest request,
                                           @RequestHeader("X-User_Id") Long userId,
                                           @RequestHeader("X-Username") String username,
                                           @RequestHeader("X-Role") String role) {
        try {
            ProductDto product = productService.updateProduct(productId, request, userId, username, role);
            return ResponseEntity.status(HttpStatus.OK).body(
                    CommonResponse.success(HttpStatus.OK, "상품 수정이 완료되었습니다.", product)
            );
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(
                    CommonResponse.error(HttpStatus.FORBIDDEN, e.getMessage())
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    CommonResponse.error(HttpStatus.INTERNAL_SERVER_ERROR, "상품 수정 중 오류가 발생했습니다.")
            );
        }
    }
}
