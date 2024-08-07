import { Component, OnInit, ViewChild } from '@angular/core';
import { SkillsService } from '../../../shared/services/skills.service';
import { SkillDomainService } from '../../../shared/services/skilldomain.service';
import { MessageService } from 'primeng/api';
import { Table, TableModule } from 'primeng/table';
import { DialogModule } from 'primeng/dialog';
import { FormControl, FormGroup, FormsModule, ReactiveFormsModule, Validators } from '@angular/forms';
import { NgForOf } from "@angular/common";
import { ButtonModule } from 'primeng/button';
import { IconFieldModule } from "primeng/iconfield";
import { InputIconModule } from "primeng/inputicon";
import { InputTextModule } from 'primeng/inputtext';
import { DropdownModule } from 'primeng/dropdown';


interface Skill {
  skillId: number;
  skillName: string;
  domainId: number;
  domainName: string;
}

interface SkillDomain {
  domainId: number;
  domainName: string;
}
@Component({
  selector: 'rc-admin-skills',
  templateUrl: './admin-skills.component.html',
  standalone: true,
  imports: [
    DialogModule,
    FormsModule,
    ReactiveFormsModule,
    TableModule,
    NgForOf,
    ButtonModule,
    IconFieldModule,
    InputIconModule,
    InputTextModule,
    DropdownModule
  ],
  styleUrls: ['./admin-skills.component.scss']
})
export class AdminSkillsComponent implements OnInit {
  @ViewChild('dt') table!: Table;
  listOfSkills: Skill[] = [];
  selectedSkill: Skill | null = null;
  listOfSkillDomains: SkillDomain[] = [];
  addSkillForm: FormGroup;
  editSkillForm: FormGroup;
  displayAddDialog: boolean = false;
  displayEditDialog: boolean = false;

  constructor(
    private skillsService: SkillsService,
    private skillDomainService: SkillDomainService,
    private messageService: MessageService
  ) {
    this.addSkillForm = new FormGroup({
      skillName: new FormControl('', Validators.required),
      domainId: new FormControl('', Validators.required)
    });

    this.editSkillForm = new FormGroup({
      skillName: new FormControl('', Validators.required),
      domainId: new FormControl('', Validators.required)
    });
  }

  ngOnInit(): void {
    this.loadSkills();
    this.loadSkillDomains();
  }

  loadSkills(): void {
    this.skillsService.getAllSkills().subscribe(
      (response: any) => {
        this.listOfSkills = response.body;
      },
      (error) => {
        console.error('Error loading skills:', error);
        this.messageService.add({
          severity: 'error',
          summary: 'Error',
          detail: 'Failed to load skills'
        });
      }
    );
  }

  loadSkillDomains(): void {
    this.skillDomainService.getAllSkillDomains().subscribe(
      (response: any) => {
        this.listOfSkillDomains = response.body;
      },
      (error) => {
        console.error('Error loading skill domains:', error);
        this.messageService.add({
          severity: 'error',
          summary: 'Error',
          detail: 'Failed to load skill domains'
        });
      }
    );
  }

  showAddDialog(): void {
    this.selectedSkill = null;
    this.addSkillForm.reset();
    this.displayAddDialog = true;
  }

  showEditDialog(skill: any): void {
    this.selectedSkill = skill;
    this.editSkillForm.patchValue({
      skillName: skill.skillName,
      domainId: skill.domainId
    });
    this.displayEditDialog = true;
  }

  hideAddDialog(): void {
    this.displayAddDialog = false;
    this.addSkillForm.reset();
  }

  hideEditDialog(): void {
    this.displayEditDialog = false;
    this.editSkillForm.reset();
  }

  onAddSkillSubmit(): void {
    if (this.addSkillForm.valid) {
      const newSkill = {
        skillName: this.addSkillForm.value.skillName,
        domainId: this.addSkillForm.value.domainId
      };
      this.skillsService.addSkill(newSkill).subscribe(response => {
        this.messageService.add({ severity: 'success', summary: 'Success', detail: 'Skill added successfully' });
        this.hideAddDialog();
        this.loadSkills();
      }, error => {
        this.messageService.add({ severity: 'error', summary: 'Error', detail: 'Failed to add skill' });
      });
    }
  }

  onEditSkillSubmit(): void {
    if (this.editSkillForm.valid && this.selectedSkill) {
      const updatedSkill = {
        skillId: this.selectedSkill.skillId,
        skillName: this.editSkillForm.value.skillName,
        domainId: this.editSkillForm.value.domainId
      };
      this.skillsService.editSkill(updatedSkill).subscribe(response => {
        this.messageService.add({ severity: 'success', summary: 'Success', detail: 'Skill updated successfully' });
        this.hideEditDialog();
        this.loadSkills();
      }, error => {
        this.messageService.add({ severity: 'error', summary: 'Error', detail: 'Failed to update skill' });
      });
    }
  }

  onDelete(skillId: number): void {
    this.skillsService.deleteSkill(skillId).subscribe(response => {
      this.messageService.add({ severity: 'success', summary: 'Success', detail: 'Skill deleted successfully' });
      this.loadSkills();
    }, error => {
      this.messageService.add({ severity: 'error', summary: 'Error', detail: 'Failed to delete skill' });
    });
  }

  filterSkills(event: any) {
    this.table.filterGlobal(event.target.value, 'contains')
  }
}
