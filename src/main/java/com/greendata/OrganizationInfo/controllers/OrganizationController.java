package com.greendata.OrganizationInfo.controllers;

import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.greendata.OrganizationInfo.model.response.OrganizationResponse;
import com.greendata.OrganizationInfo.service.OrganizationService;

import java.util.List;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping("/api/v1/organizations")
@RequiredArgsConstructor
public class OrganizationController {
    private OrganizationService organizationService;

    @Autowired
    public OrganizationController(OrganizationService organizationService) {
        this.organizationService = organizationService;
    }

    //Получаем весь список организаций
    @GetMapping(produces = APPLICATION_JSON_VALUE)
    public List<OrganizationResponse> findAll() {
        return organizationService.findAll();
    }

    //Получаем организацию по inn
    @GetMapping(value = "/inn/{inn}", produces = APPLICATION_JSON_VALUE)
    public OrganizationResponse findByInn(@PathVariable String inn) {
        return organizationService.findByInn(inn);
    }

    //Получаем организацию по названию
    @GetMapping(value = "/", produces = APPLICATION_JSON_VALUE)
    public List<OrganizationResponse> findByName(@RequestParam String name) {
        return organizationService.findByName(name);
    }
}
