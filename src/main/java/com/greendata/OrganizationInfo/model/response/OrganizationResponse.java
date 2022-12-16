package com.greendata.OrganizationInfo.model.response;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class OrganizationResponse {
    private String inn;
    private String ogrn;
    private String kpp;
    private String name;
    private String shortName;
    private String address;
    private String regDate;
}
