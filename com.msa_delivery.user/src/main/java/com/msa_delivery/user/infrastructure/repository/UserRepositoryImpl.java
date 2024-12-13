package com.msa_delivery.user.infrastructure.repository;

import com.msa_delivery.user.application.dtos.UserSearchDto;
import com.msa_delivery.user.application.dtos.QUserDetailResponseDto;
import com.msa_delivery.user.application.dtos.UserDetailResponseDto;
import com.msa_delivery.user.domain.entity.QUser;
import com.msa_delivery.user.domain.entity.UserRoleEnum;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static com.msa_delivery.user.domain.entity.QUser.user;

@Slf4j
@Repository
@RequiredArgsConstructor
public class UserRepositoryImpl implements CustomUserRepository{

    private final JPAQueryFactory queryFactory;

    public Page<UserDetailResponseDto> searchUsers(UserSearchDto searchDto, Long userId, String role) {
        int size = Math.max(searchDto.getSize(), 1);
        int page = Math.max(searchDto.getPage(), 1);

        List<OrderSpecifier<?>> orderSpecifiers = createOrderSpecifiers(searchDto);

        // 데이터 목록 쿼리 실행
        List<UserDetailResponseDto> users = queryFactory.select(new QUserDetailResponseDto(
                        user.userId,
                        user.username,
                        user.role.stringValue(),
                        user.slackId,
                        user.createdAt,
                        user.createdBy,
                        user.updatedAt,
                        user.updatedBy,
                        user.deletedAt,
                        user.deletedBy,
                        user.isDeleted))
                .from(user)
                .where(
                        checkMaster(userId, role),
                        usernameContains(searchDto.getUsername()),
                        findByRole(searchDto.getRole()),
                        slackIdContains(searchDto.getSlackId()),
                        findByCreatedAtBetween(searchDto.getCreatedAtStart(), searchDto.getCreatedAtEnd()),
                        findByUpdatedAtBetween(searchDto.getUpdatedAtStart(), searchDto.getUpdatedAtEnd()),
                        findByIsDeleted(role, searchDto.getIsDeleted())
                )
                .orderBy(orderSpecifiers.toArray(new OrderSpecifier[0]))
                .offset((long) (page - 1) * size)
                .limit(size)
                .fetch();

        Long totalResult = queryFactory.select(user.count())
                .from(user)
                .where(
                        checkMaster(userId, role),
                        usernameContains(searchDto.getUsername()),
                        findByRole(searchDto.getRole()),
                        slackIdContains(searchDto.getSlackId()),
                        findByCreatedAtBetween(searchDto.getCreatedAtStart(), searchDto.getCreatedAtEnd()),
                        findByUpdatedAtBetween(searchDto.getUpdatedAtStart(), searchDto.getUpdatedAtEnd()),
                        findByIsDeleted(role, searchDto.getIsDeleted())
                )
                .fetchOne();
        long total = (totalResult != null) ? totalResult : 0;

        return new PageImpl<>(users, Pageable.ofSize(size).withPage(page), total);
    }

    private BooleanExpression checkMaster(Long userId, String role) {
        return role.equals(UserRoleEnum.MASTER.toString()) ? null : user.userId.eq(userId);
    }

    private BooleanExpression usernameContains(String username) {
        return (username != null && !username.isEmpty()) ? user.username.containsIgnoreCase(username) : null;
    }

    private BooleanExpression findByRole(UserRoleEnum role) {
        return (role != null) ? user.role.eq(role) : null;
    }

    private BooleanExpression slackIdContains(String slackId) {
        return (slackId != null && !slackId.isEmpty()) ? user.slackId.containsIgnoreCase(slackId) : null;
    }

    private BooleanExpression findByCreatedAtBetween(LocalDateTime start, LocalDateTime end) {
        return (start != null && end != null) ? user.createdAt.between(start, end) : null;
    }

    private BooleanExpression findByUpdatedAtBetween(LocalDateTime start, LocalDateTime end) {
        return (start != null && end != null) ? user.updatedAt.between(start, end) : null;
    }

    private BooleanExpression findByIsDeleted(String role, boolean isDeleted) {
        return role.equals(UserRoleEnum.MASTER.toString()) ? user.isDeleted.eq(isDeleted) : user.isDeleted.eq(false);
    }

    private List<OrderSpecifier<?>> createOrderSpecifiers(UserSearchDto searchDto) {
        List<OrderSpecifier<?>> orderSpecifiers = new ArrayList<>();

        if (searchDto.getSort() != null) {
            for (int i = 0; i < searchDto.getSort().length; i++) {
                String property = searchDto.getSort()[i];
                com.querydsl.core.types.Order direction =
                        searchDto.getDirection()[i].equalsIgnoreCase("asc")
                                ? com.querydsl.core.types.Order.ASC
                                : com.querydsl.core.types.Order.DESC;

                    switch (property.toLowerCase()) {
                        case "username" -> orderSpecifiers.add(new OrderSpecifier<>(direction, user.username));
                        case "role" -> orderSpecifiers.add(new OrderSpecifier<>(direction, user.role));
                        case "slackid" -> orderSpecifiers.add(new OrderSpecifier<>(direction, user.slackId));
                        case "createdat" -> orderSpecifiers.add(new OrderSpecifier<>(direction, user.createdAt));
                        case "updatedat" -> orderSpecifiers.add(new OrderSpecifier<>(direction, user.updatedAt));
                        case "isdeleted" -> orderSpecifiers.add(new OrderSpecifier<>(direction, user.isDeleted));
                        default -> {}
                    }
                }
            }
        return orderSpecifiers;
    }

}
