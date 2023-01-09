package com.greendata.OrganizationInfo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.greendata.OrganizationInfo.domain.Organization;


public interface OrganizationRepository extends JpaRepository<Organization, Integer> {

    // List<Organization> findByNameContainingIgnoreCase(String name);

    List<Organization> findByShortNameContaining(String name);

    Organization findByInn(String inn);
}
