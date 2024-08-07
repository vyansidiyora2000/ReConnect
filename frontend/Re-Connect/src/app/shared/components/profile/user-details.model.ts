export interface City {
    cityId: number;
    cityName: string;
    country: Country;
}

export interface Country {
    countryId: number;
    countryName: string;
}

export interface Company {
    companyId: number;
    companyName: string;
}

export interface Skill {
    skillId: number,
    skillName: string
}

export interface UserDetails {
    detailId: number;
    userName: string;
    experience: number;
    resume: string;
    profilePicture: string;
    city: number;
    country: number;
    company: number;
    cityName: string;
    countryName: string;
    companyName: string;
    skills: Skill[] | null;
}
