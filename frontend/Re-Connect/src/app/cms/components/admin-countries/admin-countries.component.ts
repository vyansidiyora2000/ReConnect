import { Component, OnInit, ViewChild } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { Country, CountryService } from '../../../shared/services/country.service';
import { NgForOf, NgIf } from "@angular/common";
import { AdminCitiesComponent } from "../admin-cities/admin-cities.component";
import { DialogModule } from "primeng/dialog";
import { ButtonModule } from 'primeng/button';
import { Table, TableModule } from 'primeng/table';
import { InputIconModule } from 'primeng/inputicon';
import { IconFieldModule } from 'primeng/iconfield';
import { InputTextModule } from 'primeng/inputtext';

@Component({
  selector: 'app-admin-countries',
  templateUrl: './admin-countries.component.html',
  standalone: true,
  imports: [
    NgForOf,
    AdminCitiesComponent,
    NgIf,
    ReactiveFormsModule,
    DialogModule,
    ButtonModule,
    TableModule,
    InputIconModule,
    IconFieldModule,
    InputTextModule
  ],
  styleUrls: ['./admin-countries.component.scss']
})
export class AdminCountriesComponent implements OnInit {
  @ViewChild('dt') table!: Table;
  countries: Country[] = [];
  selectedCountry: Country | null = null;
  displayAddDialog: boolean = false;
  displayEditDialog: boolean = false;
  addCountryForm: FormGroup;
  editCountryForm: FormGroup;

  constructor(private countryService: CountryService, private fb: FormBuilder) {
    this.addCountryForm = this.fb.group({
      countryName: ['', Validators.required]
    });

    this.editCountryForm = this.fb.group({
      countryId: [''],
      countryName: ['', Validators.required]
    });
  }

  ngOnInit(): void {
    this.loadCountries();
  }

  loadCountries() {
    this.countryService.getAllCountries().subscribe(
      response => {
        this.countries = response.body;
      },
      error => {
        console.error('Error loading countries:', error);
      }
    );
  }

  showAddCountryDialog() {
    this.displayAddDialog = true;
  }

  hideAddDialog() {
    this.displayAddDialog = false;
  }

  onAddCountrySubmit() {
    const { countryId, countryName } = this.addCountryForm.value;
    const Country: Country = { countryId, countryName };
    this.countryService.addCountry(Country).subscribe(
      response => {
        this.loadCountries();
        this.hideAddDialog();
      },
      error => {
        console.error('Error adding country:', error);
      }
    );
  }

  showEditCountryDialog(country: Country) {
    this.editCountryForm.patchValue({
      countryId: country.countryId,
      countryName: country.countryName
    });
    this.displayEditDialog = true;
  }

  hideEditDialog() {
    this.displayEditDialog = false;
  }

  onEditCountrySubmit() {
    const { countryId, countryName } = this.editCountryForm.value;
    const updatedCountry: Country = { countryId, countryName };
    this.countryService.editCountry(updatedCountry).subscribe(
      response => {
        this.loadCountries();
        this.hideEditDialog();
      },
      error => {
        console.error('Error updating country:', error);
      }
    );
  }

  onDeleteCountry(countryId: number) {
    this.countryService.deleteCountry(countryId).subscribe(
      response => {
        this.loadCountries();
      },
      error => {
        console.error('Error deleting country:', error);
      }
    );
  }

  onCountrySelect(country: Country) {
    this.selectedCountry = country;
  }

  filterCountries(event: any) {
    this.table.filterGlobal(event.target.value, 'contains')
  }

}
