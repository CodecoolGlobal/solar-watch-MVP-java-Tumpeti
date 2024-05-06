# SolarWatch

Welcome to SolarWatch, a Spring Boot Web API project that provides sunrise and sunset times for a given city on a given date, in UTC times. This project aims to build up the application from the ground zero, implementing the minimal viable product (MVP) and then expanding its functionality in the future.

## Features

- Retrieve sunrise and sunset times for a given city and date
- Interact with external APIs for sunrise/sunset and geocoding
- Integrate PostgreSQL database for storing city data
- Implement repository interfaces for data management
- Unit testing for reliability

## Getting Started

### Prerequisites

- Java Development Kit (JDK)
- Maven
- Git
- PostgreSQL database

### Installation

1. Clone this repository:

   ```bash
   git clone https://github.com/CodecoolGlobal/solar-watch-MVP-java-Tumpeti.git
   ```

2. Navigate to the project directory:

   ```bash
   cd solar-watch-MVP-java-Tumpeti
   ```

3. Build the project using Maven:

   ```bash
   mvn clean package
   ```

4. Run the application:

   ```bash
   java -jar target/solar-watch-MVP-java-Tumpeti.jar
   ```

### Usage

Once the application is running, you can access the following endpoints:

- `GET /api/sunrise-sunset?city={city}&date={date}`: Get sunrise and sunset times for the specified city and date.

### Database Integration

SolarWatch now utilizes a PostgreSQL database for storing city coordinates and sunrise/sunset information. The data model includes entities for cities and sunrise/sunset times, with appropriate repository interfaces for data management.

## Acknowledgments

This project was developed as part of a learning experience. Special thanks to [SunriseSunset](https://sunrisesunset.io/api/) for the Sunrise/Sunset API and to [OpenWeather](https://openweathermap.org/) for providing the Geocoding API.

---
