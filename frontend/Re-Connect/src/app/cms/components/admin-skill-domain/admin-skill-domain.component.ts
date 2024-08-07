import { Component, OnInit, ViewChild } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { SkillDomainService } from '../../../shared/services/skilldomain.service';
import { MessageService } from 'primeng/api';
import { Table, TableModule } from "primeng/table";
import { DialogModule } from "primeng/dialog";
import { ButtonModule } from 'primeng/button';
import { IconFieldModule } from 'primeng/iconfield';
import { InputIconModule } from 'primeng/inputicon';
import { InputTextModule } from 'primeng/inputtext';

interface SkillDomain {
  domainId: number;
  domainName: string;
}
@Component({
  selector: 'app-skill-domain',
  templateUrl: './admin-skill-domain.component.html',
  standalone: true,
  imports: [
    TableModule,
    DialogModule,
    ReactiveFormsModule,
    ButtonModule,
    IconFieldModule,
    InputIconModule,
    InputTextModule
  ],
  styleUrls: ['./admin-skill-domain.component.scss']
})
export class AdminSkillDomainComponent implements OnInit {
  @ViewChild('dt') table!: Table;
  listOfSkillDomains: SkillDomain[] = [];
  skillDomainForm: FormGroup;
  selectedSkillDomain: any = null;
  displayDialog = false;

  constructor(
    private skillDomainService: SkillDomainService,
    private messageService: MessageService,
    private fb: FormBuilder
  ) {
    this.skillDomainForm = this.fb.group({
      domainName: ['', Validators.required]
    });
  }

  ngOnInit(): void {
    this.fetchSkillDomains();
  }

  fetchSkillDomains(): void {
    this.skillDomainService.getAllSkillDomains().subscribe(
      (response: any) => {
        this.listOfSkillDomains = response.body;
      },
      (error) => {
        console.error('Error fetching skill domains:', error);
        this.messageService.add({
          severity: 'error',
          summary: 'Error',
          detail: 'Failed to fetch skill domains'
        });
      }
    );
  }

  showAddDialog(): void {
    this.selectedSkillDomain = null;
    this.skillDomainForm.reset();
    this.displayDialog = true;
  }

  addSkillDomain(): void {
    if (this.skillDomainForm.valid) {
      const newSkillDomain = {
        domainName: this.skillDomainForm.value.domainName
      };

      this.skillDomainService.addSkillDomain(newSkillDomain).subscribe(
        (response: any) => {
          this.messageService.add({ severity: 'success', summary: 'Success', detail: 'Skill Domain added successfully' });
          this.fetchSkillDomains();
          this.hideDialog();
        },
        (error) => {
          console.error('Error adding skill domain:', error);
          this.messageService.add({ severity: 'error', summary: 'Error', detail: 'Failed to add skill domain' });
        }
      );
    }
  }

  showEditDialog(skillDomain: any): void {
    this.selectedSkillDomain = skillDomain;
    this.skillDomainForm.patchValue({
      domainName: skillDomain.domainName
    });
    this.displayDialog = true;
  }

  editSkillDomain(): void {
    if (this.skillDomainForm.valid && this.selectedSkillDomain) {
      const updatedSkillDomain = {
        domainId: this.selectedSkillDomain.domainId,
        domainName: this.skillDomainForm.value.domainName
      };

      this.skillDomainService.editSkillDomain(updatedSkillDomain).subscribe(
        (response: any) => {
          this.messageService.add({ severity: 'success', summary: 'Success', detail: 'Skill Domain updated successfully' });
          this.fetchSkillDomains();
          this.hideDialog();
        },
        (error) => {
          console.error('Error updating skill domain:', error);
          this.messageService.add({ severity: 'error', summary: 'Error', detail: 'Failed to update skill domain' });
        }
      );
    }
  }

  deleteSkillDomain(domainId: number): void {
    if (confirm('Are you sure you want to delete this skill domain?')) {
      this.skillDomainService.deleteSkillDomain(domainId).subscribe(
        (response: any) => {
          this.messageService.add({ severity: 'success', summary: 'Success', detail: 'Skill Domain deleted successfully' });
          this.fetchSkillDomains();
        },
        (error) => {
          console.error('Error deleting skill domain:', error);
          this.messageService.add({ severity: 'error', summary: 'Error', detail: 'Failed to delete skill domain' });
        }
      );
    }
  }

  hideDialog(): void {
    this.skillDomainForm.reset();
    this.displayDialog = false;
  }

  filterDomains(event: any) {
    this.table.filterGlobal(event.target.value, 'contains')
  }
}
