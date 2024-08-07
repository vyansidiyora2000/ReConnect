import { Component, OnInit } from '@angular/core';
import { ChartModule } from 'primeng/chart';
import { DashboardService } from './admin-dashboard.service';

@Component({
  selector: 'rc-admin-dashboard',
  standalone: true,
  imports: [ChartModule],
  templateUrl: './admin-dashboard.component.html',
  styleUrl: './admin-dashboard.component.scss',
  providers: [DashboardService]
})
export class AdminDashboardComponent implements OnInit {
  listOfUsersPerCountry: any;
  listOfUsersPerType: any;
  listOfUsersPerCompany: any;
  barOptions: any;
  pieOptions: any;

  constructor(private dashboardService: DashboardService) { }

  ngOnInit(): void {
    const documentStyle = getComputedStyle(document.documentElement);
    const textColor = documentStyle.getPropertyValue('--text-color');
    const textColorSecondary = documentStyle.getPropertyValue('--text-color-secondary');
    const surfaceBorder = documentStyle.getPropertyValue('--surface-border');

    this.barOptions = {
      plugins: {
        legend: {
          labels: {
            color: textColor
          }
        }
      },
      scales: {
        y: {
          beginAtZero: true,
          ticks: {
            color: textColorSecondary
          },
          grid: {
            color: surfaceBorder,
            drawBorder: false
          }
        },
        x: {
          ticks: {
            color: textColorSecondary
          },
          grid: {
            color: surfaceBorder,
            drawBorder: false
          }
        }
      }
    };

    this.pieOptions = {
      plugins: {
        legend: {
          labels: {
            usePointStyle: true,
            color: textColor
          }
        }
      }
    };


    this.getAllUsersPerCountry();
    this.getAllUsersPerType();
    this.getTopFiveCompanies();
  }

  private getAllUsersPerCountry() {
    this.dashboardService.getAllUsersPerCountry().subscribe(response => {
      const countries = response['body'];
      this.listOfUsersPerCountry = {
        labels: countries.map(country => country['countryName']),
        datasets: [{
          label: "No. of users",
          data: countries.map(country => country['userCount'])
        }]
      };
    });
  }

  private getAllUsersPerType() {
    this.dashboardService.getAllUsersPerType().subscribe(response => {
      const types = response['body'];
      this.listOfUsersPerType = {
        labels: types.map(type => type['typeName']),
        datasets: [{
          label: "Users By Type",
          data: types.map(type => type['userCount'])
        }]
      };
    });
  }

  private getTopFiveCompanies() {
    this.dashboardService.getTopFiveCompanies().subscribe(response => {
      const companies = response['body'];
      this.listOfUsersPerCompany = {
        labels: companies.map(company => company['companyName']),
        datasets: [
          {
            data: companies.map(type => type['userCount']),
            label: 'No. of users'
          }
        ]
      };
    });
  }


}
