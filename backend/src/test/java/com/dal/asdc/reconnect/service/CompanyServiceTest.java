package com.dal.asdc.reconnect.service;

import com.dal.asdc.reconnect.dto.Company.CompanyDTO;
import com.dal.asdc.reconnect.model.Company;
import com.dal.asdc.reconnect.repository.CompanyRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CompanyServiceTest {

    @InjectMocks
    private CompanyService companyService;

    @Mock
    private CompanyRepository companyRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetAllCompanies() {
        Company company1 = new Company();
        company1.setCompanyId(1);
        company1.setCompanyName("Company1");

        Company company2 = new Company();
        company2.setCompanyId(2);
        company2.setCompanyName("Company2");

        when(companyRepository.findAll()).thenReturn(Arrays.asList(company1, company2));

        List<CompanyDTO> result = companyService.getAllCompanies();

        assertEquals(2, result.size());
        verify(companyRepository, times(1)).findAll();
    }

    @Test
    void testAddCompany() {
        String companyName = "Company1";
        Company company = new Company();
        company.setCompanyName(companyName);

        when(companyRepository.save(any(Company.class))).thenReturn(company);

        Company result = companyService.addCompany(companyName);

        assertEquals(companyName, result.getCompanyName());
        verify(companyRepository, times(1)).save(any(Company.class));
    }

    @Test
    void testModifyCompany() {
        Company existingCompany = new Company();
        existingCompany.setCompanyId(1);
        existingCompany.setCompanyName("OldCompany");

        CompanyDTO companyDTO = new CompanyDTO(1, "NewCompany");

        when(companyRepository.findById(1)).thenReturn(Optional.of(existingCompany));
        when(companyRepository.save(any(Company.class))).thenReturn(existingCompany);

        Company result = companyService.modifyCompany(companyDTO);

        assertEquals("NewCompany", result.getCompanyName());
        verify(companyRepository, times(1)).findById(1);
        verify(companyRepository, times(1)).save(existingCompany);
    }

    @Test
    void testModifyCompany_CompanyNotFound() {
        CompanyDTO companyDTO = new CompanyDTO(1, "NewCompany");

        when(companyRepository.findById(1)).thenReturn(Optional.empty());

        Company result = companyService.modifyCompany(companyDTO);

        assertNull(result);
        verify(companyRepository, times(1)).findById(1);
    }

    @Test
    void testDeleteCompany() {
        Company existingCompany = new Company();
        existingCompany.setCompanyId(1);

        when(companyRepository.findById(1)).thenReturn(Optional.of(existingCompany));
        doNothing().when(companyRepository).deleteById(1);

        boolean result = companyService.deleteCompany(1);

        assertTrue(result);
        verify(companyRepository, times(1)).findById(1);
        verify(companyRepository, times(1)).deleteById(1);
    }

    @Test
    void testDeleteCompany_CompanyNotFound() {
        when(companyRepository.findById(1)).thenReturn(Optional.empty());

        boolean result = companyService.deleteCompany(1);

        assertFalse(result);
        verify(companyRepository, times(1)).findById(1);
    }

    @Test
    void testGetCompanyByName() {
        String companyName = "Company1";
        Company company = new Company();
        company.setCompanyName(companyName);

        when(companyRepository.findCompanyByCompanyName(companyName)).thenReturn(company);

        Company result = companyService.getCompanyByName(companyName);

        assertEquals(companyName, result.getCompanyName());
        verify(companyRepository, times(1)).findCompanyByCompanyName(companyName);
    }

    @Test
    void testGetCompanyById() {
        Company company = new Company();
        company.setCompanyId(1);
        company.setCompanyName("Company1");

        when(companyRepository.findById(1)).thenReturn(Optional.of(company));

        Company result = companyService.getCompanyById(1);

        assertEquals(1, result.getCompanyId());
        assertEquals("Company1", result.getCompanyName());
        verify(companyRepository, times(1)).findById(1);
    }

    @Test
    void testGetCompanyById_CompanyNotFound() {
        when(companyRepository.findById(1)).thenReturn(Optional.empty());

        Company result = companyService.getCompanyById(1);

        assertNull(result);
        verify(companyRepository, times(1)).findById(1);
    }
}
