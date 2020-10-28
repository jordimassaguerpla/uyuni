package com.redhat.rhn.domain.rhnpackage;

public enum PackageType {
    RPM("rpm"),
    DEB("deb");

    private final String dbString;

    PackageType(String dbStringIn) {
        dbString = dbStringIn;
    }

    public String getDbString() {
        return dbString;
    }
}
