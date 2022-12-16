package com.greendata.OrganizationInfo.domain;

import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
@Entity
@Table(name = "entities")
public class Organization {

    @Id
    @Column(name = "inn", nullable = false, length=10)
    private String inn;

    @Column(name = "ogrn", nullable = false, length=13)
    private String ogrn;

    @Column(name = "kpp", nullable = false, length=9)
    private String kpp;

    @Column(name = "name", nullable = false, length=500)
    private String name;

    @Column(name = "short_name", nullable = false, length=500)
    private String shortName;

    @Column(name = "address", nullable = false, length=500)
    private String address;

    @Column(name = "reg_date", nullable = false, length=10)
    private String regDate;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Organization company = (Organization) o;
        return inn.equals(company.inn) && kpp.equals(company.kpp) && name.equals(company.name) 
                && shortName.equals(company.shortName) && address.equals(company.address) && regDate.equals(company.regDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(inn, ogrn, kpp, name, shortName, address, regDate);
    }

    @Override
    public String toString() {
        return "Organization{" +
                "inn='" + inn + '\'' +
                ", ogrn='" + ogrn + '\'' +
                ", name='" + name + '\'' +
                ", short_name='" + shortName + '\'' +
                ", address='" + address + '\'' +
                ", reg_date='" + regDate + '\'' +
                '}';
    }
}
