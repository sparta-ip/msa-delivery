package com.msa_delivery.user.infrastructure.repository;

import com.msa_delivery.user.application.UserSearchDto;
import com.msa_delivery.user.application.dtos.QUserDetailResponseDto;
import com.msa_delivery.user.application.dtos.UserDetailResponseDto;
import com.msa_delivery.user.domain.entity.UserRoleEnum;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

import static com.msa_delivery.user.domain.entity.QUser.user;

@Repository
@RequiredArgsConstructor
public class UserRepositoryImpl implements CustomUserRepository{

    private final JPAQueryFactory queryFactory;

    public Page<UserDetailResponseDto> searchUsers(UserSearchDto searchDto, Long userId, String role) {
        int size = Math.max(searchDto.getSize(), 20);
        int page = Math.max(searchDto.getPage(), 1);
        // TODO : 띄어쓰기 삭제, page, size가 움수일 시 기본 1, 20으로 설정하기
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
                        findBySlackId(searchDto.getSlackId()),
                        findByCreatedAtBetween(searchDto.getCreatedAtStart(), searchDto.getCreatedAtEnd()),
                        findByUpdatedAtBetween(searchDto.getUpdatedAtStart(), searchDto.getUpdatedAtEnd()),
                        findByIsDeleted(role, searchDto.isDeleted())
                )
                .offset((long) (page - 1) * size)
                .limit(size)
                .fetch();

        long total = queryFactory.selectOne()
                .from(user)
                .where(
                        checkMaster(userId, role),
                        usernameContains(searchDto.getUsername()),
                        findByRole(searchDto.getRole()),
                        findBySlackId(searchDto.getSlackId()),
                        findByCreatedAtBetween(searchDto.getCreatedAtStart(), searchDto.getCreatedAtEnd()),
                        findByUpdatedAtBetween(searchDto.getUpdatedAtStart(), searchDto.getUpdatedAtEnd()),
                        findByIsDeleted(role, searchDto.isDeleted())
                )
                .fetch()
                .size();

        // Page 객체 반환
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

    private BooleanExpression findBySlackId(String slackId) {
        return (slackId != null && !slackId.isEmpty()) ? user.slackId.eq(slackId) : null;
    }

    private BooleanExpression findByCreatedAtBetween(LocalDateTime start, LocalDateTime end) {
        return (start != null && end != null) ? user.createdAt.between(start, end) : null;
    }

    private BooleanExpression findByUpdatedAtBetween(LocalDateTime start, LocalDateTime end) {
        return (start != null && end != null) ? user.updatedAt.between(start, end) : null;
    }

    private BooleanExpression findByIsDeleted(String role, boolean isDeleted) {
        return role.equals(UserRoleEnum.MASTER.toString()) ? user.isDeleted.eq(isDeleted) : null;
    }

}
