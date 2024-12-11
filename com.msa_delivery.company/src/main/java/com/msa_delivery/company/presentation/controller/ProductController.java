package com.msa_delivery.company.presentation.controller;

import com.msa_delivery.company.application.dto.CommonResponse;
import com.msa_delivery.company.application.dto.ProductDto;
import com.msa_delivery.company.application.service.ProductService;
import com.msa_delivery.company.presentation.request.ProductRequest;
import com.msa_delivery.company.presentation.request.ProductUpdateRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

    @DeleteMapping("/{productId}")
    public ResponseEntity<?> deleteProduct(@PathVariable UUID productId,
                                           @RequestHeader("X-User_Id") Long userId,
                                           @RequestHeader("X-Username") String username,
                                           @RequestHeader("X-Role") String role) {
        try {
            productService.deleteProduct(productId, userId, username, role);
            return ResponseEntity.status(HttpStatus.OK).body(
                    CommonResponse.success(HttpStatus.OK, "상품 삭제가 완료되었습니다.")
            );
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(
                    CommonResponse.error(HttpStatus.FORBIDDEN, e.getMessage())
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    CommonResponse.error(HttpStatus.INTERNAL_SERVER_ERROR, "상품 삭제 중 오류가 발생했습니다.")
            );
        }
    }

    @GetMapping("/{productId}")
    public ResponseEntity<?> getProductById(@PathVariable UUID productId,
                                            @RequestHeader("X-User_Id") Long userId,
                                            @RequestHeader("X-Role") String role) {
        try {
            ProductDto product = productService.getProductById(productId, userId, role);
            return ResponseEntity.status(HttpStatus.OK).body(
                    CommonResponse.success(HttpStatus.OK, "상품 상세 조회가 완료되었습니다.", product)
            );
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    CommonResponse.error(HttpStatus.NOT_FOUND, e.getMessage())
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    CommonResponse.error(HttpStatus.INTERNAL_SERVER_ERROR, "상품 조회 중 오류가 발생했습니다.")
            );
        }
    }

    @GetMapping
    public ResponseEntity<?> getProducts(
            @RequestParam(value = "search", required = false) String search,
            @RequestParam(value = "hub_id", required = false) UUID hubId,
            @RequestParam(required = false) Integer minPrice,
            @RequestParam(required = false) Integer maxPrice,
            @RequestParam(required = false) Integer minQuantity,
            @RequestParam(required = false) Integer maxQuantity,
            @RequestHeader("X-User_Id") Long userId,
            @RequestHeader("X-Role") String role,
            Pageable pageable) {
        try {
            Page<ProductDto> products =
                    productService.getProducts(search, hubId, minPrice, maxPrice, minQuantity, maxQuantity, userId, role, pageable);

            return ResponseEntity.status(HttpStatus.OK).body(
                    CommonResponse.success(HttpStatus.OK, "검색 조회가 완료되었습니다.", products)
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    CommonResponse.error(HttpStatus.INTERNAL_SERVER_ERROR, "검색 중 오류가 발생했습니다.")
            );
        }
    }

    // 수량 감소
    @GetMapping("/{productId}/reduceQuantity")
    public void reduceProductQuantity(@PathVariable UUID productId, @RequestParam int quantity) {
        productService.reduceProductQuantity(productId, quantity);
    }
}
