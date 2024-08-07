import { Component, Input, OnInit, ViewChild } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { City } from '../../../shared/services/city.service';
import { Country } from '../../../shared/services/country.service';
import { CityService } from '../../../shared/services/city.service';
import { CountryService } from '../../../shared/services/country.service';
import { DialogModule } from "primeng/dialog";
import { NgForOf } from "@angular/common"; // Import CountryService if not already imported
import { ButtonModule } from 'primeng/button';
import { Table, TableModule } from 'primeng/table';
import { IconFieldModule } from 'primeng/iconfield';
import { InputIconModule } from 'primeng/inputicon';
import { InputTextModule } from 'primeng/inputtext';

@Component({
  selector: 'app-admin-cities',
  templateUrl: './admin-cities.component.html',
  standalone: true,
  imports: [
    ReactiveFormsModule,
    DialogModule,
    NgForOf,
    ButtonModule,
    TableModule,
    IconFieldModule,
    InputIconModule,
    InputTextModule
  ],
  styleUrls: ['./admin-cities.component.scss']
})
export class AdminCitiesComponent implements OnInit {
  @ViewChild('dt') table!: Table;
  @Input() selectedCountry!: Country;
  cities: City[] = [];
  countries: Country[] = []; // Array to store list of countries
  displayAddDialog: boolean = false;
  displayEditDialog: boolean = false;
  addCityForm: FormGroup;
  editCityForm: FormGroup;

  constructor(
    private cityService: CityService,
    private countryService: CountryService, // Inject CountryService
    private fb: FormBuilder
  ) {
    this.addCityForm = this.fb.group({
      cityName: ['', Validators.required],
      countryId: ['', Validators.required] // Add countryId as a form control
    });

    this.editCityForm = this.fb.group({
      cityId: [''],
      cityName: ['', Validators.required],
      countryId: ['', Validators.required]
    });
  }

  ngOnInit(): void {
    this.loadCountries();
    if (this.selectedCountry) {
      this.fetchCitiesByCountry(this.selectedCountry.countryId);
    }
  }

  ngOnChanges(): void {
    if (this.selectedCountry) {
      this.fetchCitiesByCountry(this.selectedCountry.countryId);
    }
  }

  fetchCitiesByCountry(countryId: number) {
    this.cityService.getAllCities(countryId).subscribe(
      data => {
        this.cities = data.body;
      },
      error => {
        console.error('Error loading cities:', error);
      }
    );
  }

  loadCountries() {
    this.countryService.getAllCountries().subscribe(
      data => {
        this.countries = data.body;
      },
      error => {
        console.error('Error loading countries:', error);
      }
    );
  }

  showAddCityDialog() {
    this.loadCountries();
    this.displayAddDialog = true;
  }

  hideAddDialog() {
    this.displayAddDialog = false;
  }

  onAddCitySubmit() {
    const cityName = this.addCityForm.value.cityName;
    const countryId = this.addCityForm.value.countryId;
    const newCity: City = {
      cityName,
      countryId
    };
    this.cityService.addCity(newCity).subscribe(
      response => {
        this.fetchCitiesByCountry(newCity.countryId);
        this.hideAddDialog();
      },
      error => {
        console.error('Error adding city:', error);
      }
    );
  }

  showEditCityDialog(city: City) {
    this.loadCountries();
    this.editCityForm.patchValue({
      cityId: city.cityId,
      cityName: city.cityName,
      countryId: city.countryId
    });
    this.displayEditDialog = true;
  }

  hideEditDialog() {
    this.displayEditDialog = false;
  }

  onEditCitySubmit() {
    const { cityId, cityName, countryId } = this.editCityForm.value;
    const updatedCity: City = { cityId, cityName, countryId };
    this.cityService.editCity(updatedCity).subscribe(
      response => {
        this.fetchCitiesByCountry(this.selectedCountry.countryId);
        this.hideEditDialog();
      },
      error => {
        console.error('Error updating city:', error);
      }
    );
  }

  onDeleteCity(cityId: number | undefined) {
    this.cityService.deleteCity(cityId).subscribe(
      response => {
        this.fetchCitiesByCountry(this.selectedCountry.countryId);
      },
      error => {
        console.error('Error deleting city:', error);
      }
    );
  }

  filterCities(event: any) {
    this.table.filterGlobal(event.target.value, 'contains')
  }
}
