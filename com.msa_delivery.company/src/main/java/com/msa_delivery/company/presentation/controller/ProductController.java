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

import java.nio.file.AccessDeniedException;
import java.util.UUID;

@RestController
@RequestMapping("/api/companies/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @PostMapping
    public ResponseEntity<?> creatProduct(@Valid @RequestBody ProductRequest request,
                                          @RequestHeader("X-User_Id") String userId,
                                          @RequestHeader("X-Username") String username,
                                          @RequestHeader("X-Role") String role) throws AccessDeniedException {
        ProductDto product = productService.createProduct(request, userId, username, role);
        return ResponseEntity.status(HttpStatus.CREATED).body(
                CommonResponse.success(HttpStatus.CREATED.value(), "상품 생성이 완료되었습니다.", product)
        );
    }

    @PutMapping("/{productId}")
    public ResponseEntity<?> updateProduct(@PathVariable UUID productId,
                                           @RequestBody ProductUpdateRequest request,
                                           @RequestHeader("X-User_Id") String userId,
                                           @RequestHeader("X-Username") String username,
                                           @RequestHeader("X-Role") String role) throws AccessDeniedException {
        ProductDto product = productService.updateProduct(productId, request, userId, username, role);
        return ResponseEntity.status(HttpStatus.OK).body(
                CommonResponse.success(HttpStatus.OK.value(), "상품 수정이 완료되었습니다.", product)
        );
    }

    @DeleteMapping("/{productId}")
    public ResponseEntity<?> deleteProduct(@PathVariable UUID productId,
                                           @RequestHeader("X-User_Id") String userId,
                                           @RequestHeader("X-Username") String username,
                                           @RequestHeader("X-Role") String role) throws AccessDeniedException {
        productService.deleteProduct(productId, userId, username, role);
        return ResponseEntity.status(HttpStatus.OK).body(
                CommonResponse.success(HttpStatus.OK.value(), "상품 삭제가 완료되었습니다.", null)
        );
    }

    @GetMapping("/{productId}")
    public ResponseEntity<?> getProductById(@PathVariable UUID productId,
                                            @RequestHeader("X-User_Id") String userId,
                                            @RequestHeader("X-Username") String username,
                                            @RequestHeader("X-Role") String role) throws AccessDeniedException {
        ProductDto product = productService.getProductById(productId, userId, username, role);
        return ResponseEntity.status(HttpStatus.OK).body(
                CommonResponse.success(HttpStatus.OK.value(), "상품 상세 조회가 완료되었습니다.", product)
        );
    }

    @GetMapping
    public ResponseEntity<?> getProducts(
            @RequestParam(value = "search", required = false) String search,
            @RequestParam(value = "hub_id", required = false) UUID hubId,
            @RequestParam(required = false) Integer minPrice,
            @RequestParam(required = false) Integer maxPrice,
            @RequestParam(required = false) Integer minQuantity,
            @RequestParam(required = false) Integer maxQuantity,
            @RequestHeader("X-User_Id") String userId,
            @RequestHeader("X-Username") String username,
            @RequestHeader("X-Role") String role,
            Pageable pageable) throws AccessDeniedException {
        Page<ProductDto> products =
                productService.getProducts(search, hubId, minPrice, maxPrice, minQuantity, maxQuantity, userId, username, role, pageable);

        return ResponseEntity.status(HttpStatus.OK).body(
                CommonResponse.success(HttpStatus.OK.value(), "검색 조회가 완료되었습니다.", products)
        );
    }

    // 수량 감소
    @GetMapping("/{productId}/reduceQuantity")
    public void reduceProductQuantity(@PathVariable UUID productId, @RequestParam int quantity) {
        productService.reduceProductQuantity(productId, quantity);
    }
}
