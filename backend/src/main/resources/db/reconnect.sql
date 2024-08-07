CREATE DATABASE RECONNECT;
USE RECONNECT;

-- User Types Table
CREATE TABLE UserType (
    TypeID INT AUTO_INCREMENT PRIMARY KEY,
    TypeName VARCHAR(50) NOT NULL
);

-- Country Table
CREATE TABLE Country (
    CountryID INT AUTO_INCREMENT PRIMARY KEY,
    CountryName VARCHAR(100) NOT NULL
);

-- City Table
CREATE TABLE City (
    CityID INT AUTO_INCREMENT PRIMARY KEY,
    CityName VARCHAR(100) NOT NULL,
    CountryID INT,
    FOREIGN KEY (CountryID) REFERENCES Country(CountryID)
);

-- Company Table
CREATE TABLE Company (
    CompanyID INT AUTO_INCREMENT PRIMARY KEY,
    CompanyName VARCHAR(100) NOT NULL
);

-- Users Table (for credentials)
CREATE TABLE Users (
    UserID INT AUTO_INCREMENT PRIMARY KEY,
    UserEmail VARCHAR(100) UNIQUE NOT NULL,
    Password VARCHAR(255) NOT NULL,
    UserType INT NOT NULL,
    FOREIGN KEY (UserType) REFERENCES UserType(TypeID)
);

-- User Details Table
CREATE TABLE UserDetails (
    DetailID INT AUTO_INCREMENT PRIMARY KEY,
    UserID INT NOT NULL,
    UserName VARCHAR(100) NOT NULL,
    CurrentCompany INT,  -- Reference to Company table
    Experience INT,
    Resume TEXT,
    ProfilePicture VARCHAR(255),  -- Column for profile picture URL
    CityID INT,  -- Reference to City table
    CountryID INT,  -- Reference to Country table
    FOREIGN KEY (UserID) REFERENCES Users(UserID),
    FOREIGN KEY (CurrentCompany) REFERENCES Company(CompanyID),
    FOREIGN KEY (CityID) REFERENCES City(CityID),
    FOREIGN KEY (CountryID) REFERENCES Country(CountryID)
);

-- Skills Table
CREATE TABLE Skills (
    SkillID INT AUTO_INCREMENT PRIMARY KEY,
    SkillName VARCHAR(100) NOT NULL,
    SkillDomain VARCHAR(100)
);

-- User Skills Table (Many-to-Many relationship)
CREATE TABLE UserSkills (
    UserSkillID INT AUTO_INCREMENT PRIMARY KEY,
    UserID INT NOT NULL,
    SkillID INT NOT NULL,
    FOREIGN KEY (UserID) REFERENCES Users(UserID),
    FOREIGN KEY (SkillID) REFERENCES Skills(SkillID)
);

-- Messages Table
CREATE TABLE Messages (
    MessageID INT AUTO_INCREMENT PRIMARY KEY,
    MessageContent TEXT NOT NULL,
    SenderID INT,
    ReceiverID INT,
    Time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    IsRead BOOLEAN DEFAULT FALSE,
    FOREIGN KEY (SenderID) REFERENCES Users(UserID),
    FOREIGN KEY (ReceiverID) REFERENCES Users(UserID)
);

-- Notifications Table
CREATE TABLE Notifications (
    NotificationID INT AUTO_INCREMENT PRIMARY KEY,
    UserID INT,
    NotificationType VARCHAR(100),
    NotificationContent TEXT,
    IsRead BOOLEAN DEFAULT FALSE,
    CreatedAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UpdatedAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (UserID) REFERENCES Users(UserID)
);

-- Referral Requests Table
CREATE TABLE ReferralRequests (
    RequestID INT AUTO_INCREMENT PRIMARY KEY,
    ReferentID INT,
    ReferrerID INT,
    Status VARCHAR(50) DEFAULT 'Pending',
    RequestDate TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    ResponseDate TIMESTAMP NULL,
    FOREIGN KEY (ReferentID) REFERENCES Users(UserID),
    FOREIGN KEY (ReferrerID) REFERENCES Users(UserID)
);

