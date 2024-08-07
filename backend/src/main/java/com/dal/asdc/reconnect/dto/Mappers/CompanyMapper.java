package com.dal.asdc.reconnect.dto.Mappers;

import com.dal.asdc.reconnect.dto.Company.CompanyDTO;
import com.dal.asdc.reconnect.model.Company;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class CompanyMapper {
    public CompanyDTO mapCompanyToDTO(Company company) {
        CompanyDTO companyDTO = new CompanyDTO(company.getCompanyId(), company.getCompanyName());
        companyDTO.setCompanyId(company.getCompanyId());
        companyDTO.setCompanyName(company.getCompanyName());
        return companyDTO;
    }

    public Company mapDTOToCompany(CompanyDTO companyDTO) {
        Company company = new Company();
        company.setCompanyId(companyDTO.getCompanyId());
        company.setCompanyName(companyDTO.getCompanyName());
        return company;
    }

    public List<CompanyDTO> mapCompaniesToDTOs(List<Company> companies) {
        return companies.stream()
                .map(this::mapCompanyToDTO)
                .collect(Collectors.toList());
    }
}
