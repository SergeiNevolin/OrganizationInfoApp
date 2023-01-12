package com.greendata.OrganizationInfo.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.greendata.OrganizationInfo.domain.Organization;
import com.greendata.OrganizationInfo.model.response.OrganizationResponse;
import com.greendata.OrganizationInfo.repository.OrganizationRepository;
import com.sun.istack.NotNull;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OrganizationServiceImpl implements OrganizationService {

    @Autowired
    OrganizationRepository organizationRepository;

    // Получаем весь список организаций
    @NotNull
    @Override
    @Transactional(readOnly = true)
    public List<OrganizationResponse> findAll() {
        Pageable limit = PageRequest.of(0, 10);
        return organizationRepository.findAll(limit).stream().map(this::buildOrganizationResponse)
                .collect(Collectors.toList());
    }

    // Получаем организацию по инн
    @NotNull
    @Override
    @Transactional(readOnly = true)
    public OrganizationResponse findByInn(@NotNull String inn) {
        Organization organization = organizationRepository.findByInn(inn);
        return buildOrganizationResponse(organization);
        // return organizationRepository.findByInn(inn)
        // .map(this::buildOrganizationResponse)
        // .orElseThrow(() -> new EntityNotFoundException("Organization " + inn + " is
        // not found"));
    }

    // Получаем организацию по названию
    @NotNull
    @Override
    @Transactional(readOnly = true)
    public List<OrganizationResponse> findByName(@NotNull String name) {
        return organizationRepository.findByShortNameContaining(name.toUpperCase()).stream()
                .map(this::buildOrganizationResponse).collect(Collectors.toList());
    }

    @NotNull
    private OrganizationResponse buildOrganizationResponse(@NotNull Organization organization) {
        return new OrganizationResponse().setInn(organization.getInn())
                .setOgrn(organization.getOgrn()).setKpp(organization.getKpp())
                .setName(organization.getName()).setShortName(organization.getShortName())
                .setAddress(organization.getAddress()).setRegDate(organization.getRegDate());
    }
}
