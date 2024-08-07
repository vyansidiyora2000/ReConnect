import { Component, OnInit, ViewChild } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { Company, CompanyService } from '../../../shared/services/company.service';
import { DialogModule } from "primeng/dialog";
import { NgForOf, NgIf } from "@angular/common";
import { ButtonModule } from 'primeng/button';
import { Table, TableModule } from 'primeng/table';
import { InputIconModule } from 'primeng/inputicon';
import { IconFieldModule } from 'primeng/iconfield';
import { InputTextModule } from 'primeng/inputtext';

@Component({
  selector: 'rc-admin-companies',
  templateUrl: './admin-companies.component.html',
  standalone: true,
  imports: [
    DialogModule,
    ReactiveFormsModule,
    NgIf,
    NgForOf,
    ButtonModule,
    TableModule,
    InputIconModule,
    IconFieldModule,
    InputTextModule
  ],
  styleUrls: ['./admin-companies.component.scss']
})
export class AdminCompaniesComponent implements OnInit {
  @ViewChild('dt') table!: Table;
  listOfCompanies: Company[] = [];
  showDialog: boolean = false;
  addCompanyForm: FormGroup;
  editCompanyForm: FormGroup;
  selectedCompany: Company | null = null;

  constructor(private companyService: CompanyService, private fb: FormBuilder) {
    this.addCompanyForm = this.fb.group({
      companyName: ['', Validators.required]
    });

    this.editCompanyForm = this.fb.group({
      companyId: [''],
      companyName: ['', Validators.required]
    });
  }

  ngOnInit(): void {
    this.loadCompanies();
  }

  loadCompanies() {
    this.companyService.getAllCompanies().subscribe(
      (response: any) => {
        this.listOfCompanies = response.body;
      },
      error => {
        console.error('Error loading companies:', error);
      }
    );
  }

  showAddCompanyDialog() {
    this.showDialog = true;
  }

  hideAddDialog() {
    this.showDialog = false;
  }

  onAddCompanySubmit() {
    const companyName = this.addCompanyForm.value.companyName;
    const newCompany: Company = {
      companyName
    };
    this.companyService.addCompany(newCompany).subscribe(
      response => {
        this.loadCompanies();
        this.hideAddDialog();
      },
      error => {
        console.error('Error adding company:', error);
      }
    );
  }

  showEditCompanyDialog(company: Company) {
    this.selectedCompany = company;
    this.editCompanyForm.patchValue({
      companyId: company.companyId,
      companyName: company.companyName
    });
    this.showDialog = true;
  }

  hideEditDialog() {
    this.showDialog = false;
    this.selectedCompany = null;
  }

  onEditCompanySubmit() {
    const { companyId, companyName } = this.editCompanyForm.value;
    const updatedCompany: Company = { companyId, companyName };
    this.companyService.editCompany(updatedCompany).subscribe(
      response => {
        this.loadCompanies();
        this.hideEditDialog();
      },
      error => {
        console.error('Error updating company:', error);
      }
    );
  }

  onDeleteCompany(companyId: number | undefined) {
    if (companyId) {
      this.companyService.deleteCompany(companyId).subscribe(
        response => {
          this.loadCompanies();
        },
        error => {
          console.error('Error deleting company:', error);
        }
      );
    }
  }

  filterCompanies(event: any) {
    this.table.filterGlobal(event.target.value, 'contains')
  }
}
