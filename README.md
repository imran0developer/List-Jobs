# Job Listing App

## Overview

The Job Listing App is a mobile application built using Jetpack Compose and Supabase. It allows users to browse job listings, post new jobs, and manage their job applications. The app supports two user roles: **Recruiter** and **Non-Recruiter**. Each role has different functionalities and views.

## Demo
![Job Listing Demo](demo/demo.gif)

## Features

- **User Authentication**: Users can sign up and log in as either a Recruiter or a Non-Recruiter.
- **Job Listings**: Users can view a list of available jobs.
- **Job Posting**: Recruiters can post new job listings.
- **Job Management**: Recruiters can edit or delete their job postings.
- **Dynamic UI**: The app's UI changes based on the user's role.

## Screens

### 1. Authentication Screen

- Allows users to sign up or log in.
- Users can choose to register as either a Recruiter or a Non-Recruiter.
- Validation for username and password is implemented.

### 2. Home Screen

- Displays the main navigation options based on user roles:
  - **Recruiters**: Can see options to add new jobs and manage existing listings.
  - **Non-Recruiters**: Can browse available job listings.

### 3. Job Listing Screen

- Shows a list of jobs fetched from the Supabase database.
- Users can refresh the list to see updated job postings.

### 4. Job Post Screen

- Allows recruiters to create or update job postings.
- Fields include:
  - Job Title
  - Job Description
  - Remote/On-site checkbox
  - Pay
  - Location
- The screen dynamically updates based on whether the user is posting a new job or editing an existing one.

### Role-Specific Features

#### Recruiter Role

- Can post new job listings.
- Can edit or delete their own job postings.
- Has access to additional management features.

#### Non-Recruiter Role

- Can browse through job listings.
- Can apply for jobs but cannot post new listings.

### View Changes Based on Role

The app's UI components and available actions change depending on whether the user is logged in as a Recruiter or a Non-Recruiter:

- **Recruiters** see buttons for posting and managing jobs.
- **Non-Recruiters** see options for applying to jobs but do not have posting capabilities.

## Technologies Used

- **Jetpack Compose**: For building the UI components in a declarative manner.
- **Supabase**: For backend services including authentication and database management.
- **Kotlin Coroutines**: For handling asynchronous operations.
