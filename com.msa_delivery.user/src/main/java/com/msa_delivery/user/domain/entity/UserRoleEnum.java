package com.msa_delivery.user.domain.entity;

public enum UserRoleEnum {

    MASTER(Authority.MASTER),
    HUB_MANAGER(Authority.HUB_MANAGER),
    DELIVERY_MANAGER(Authority.DELIVERY_MANAGER),
    COMPANY_MANAGER(Authority.COMPANY_MANAGER);

    private final String authority;

    UserRoleEnum(String authority) {
        this.authority = authority;
    }

    public String getAuthority() {
        return this.authority;
    }

    public static class Authority {
        public static final String MASTER = "MASTER";
        public static final String HUB_MANAGER = "HUB_MANAGER";
        public static final String DELIVERY_MANAGER = "DELIVERY_MANAGER";
        public static final String COMPANY_MANAGER  = "COMPANY_MANAGER ";
    }
}
