package com.greendata.OrganizationInfo.service;

import com.greendata.OrganizationInfo.model.response.OrganizationResponse;
import com.sun.istack.NotNull;

import java.util.List;

public interface OrganizationService {

    @NotNull
    List<OrganizationResponse> findAll();

    @NotNull
    OrganizationResponse findByInn(@NotNull String inn);

    @NotNull
    List<OrganizationResponse> findByName(@NotNull String name);

}
