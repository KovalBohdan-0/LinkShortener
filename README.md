<a name="readme-top"></a>

## Link shortener

### Link
[Link](linkshort.tech) - linkshort.tech

### Built With

* [![Spring Boot][Spring-boot.io]][SpringBoot-url]
* [![Spring Security][Spring-security.io]][SpringSecurity-url]
* [![Hibernate][Hibernate.org]][Hibernate-url]
* [![Postgresql][Postgresql.org]][Postgresql-url]
* [![AWS][Aws.com]][Aws-url]
* [![Docker][Docker.com]][Docker-url]
* [![Jwt][Jwt.io]][Jwt-url]
* [![Swagger][Swagger.io]][Swagger-url]
* [![TypeScript][TypeScript.org]][TypeScript-url]
* [![Angular][Angular.io]][Angular-url]
* [![Bootstrap][Bootstrap.com]][Bootstrap-url]

<p align="right">(<a href="#readme-top">back to top</a>)</p>

## About The Project

### Short url:
[![Link shortener Screen Shot][shortener-screenshot]](https://linkshort.tech)
### Urls:
[![Link shortener Screen Shot][myurls-screenshot]](https://linkshort.tech)
### Sign up:
[![Link shortener Screen Shot][signup-screenshot]](https://linkshort.tech)

Welcome to the readme file for the Link Shortener Project!

This project aims to create a simple yet powerful tool for shortening long URLs into shorter ones. The purpose of this tool is to make it easier for users to share links and save space when posting links in social media, emails, or any other platforms.

To use this link shortener, simply follow the steps below:

    Enter the long URL that you want to shorten into the input box on the homepage.
    Click the "Short url" button to generate a shortened link.
    Url automaticly copies the shortened link and you can use it wherever you need it.

Features:

    Customizable short link format: You can choose to generate short links in a format that suits your needs.
    Analytics: The link shortener provides basic analytics, such as the number of clicks, date of creation.
    Create account to have ability to change, delete, copy link, watch views, get creation time of link.
    
Installation:

The link shortener is a web-based tool and can be accessed directly from the website. No installation is required.

If you wish to host the tool on your own server, you can download the source code from the GitHub repository and follow the <a href="#installation">instructions</a> in the README file.

Contributing:

If you would like to contribute to the project, you can do so by forking the repository and submitting a pull request with your changes.

Bug Reports:

If you encounter any bugs or issues with the link shortener, please submit a detailed bug report in the Issues section of the GitHub repository.


<p align="right">(<a href="#readme-top">back to top</a>)</p>


<a name="installation"></a>
## Installation instruction

For installation with docker for development purpose you can use commands below:

```
    cd LinkShortener
    docker-compose up --build -d
```

Configuration of different environments: 
```
    /LinkShortener/src/main/resources/application-XXX.properties
    /LinkShortener/src/main/frontend/src/environments
```



<p align="right">(<a href="#readme-top">back to top</a>)</p>

<!-- MARKDOWN LINKS & IMAGES -->

[signup-screenshot]: src/main/frontend/src/images/screenshot-signup.png
[myurls-screenshot]: src/main/frontend/src/images/screenshot-urls.png
[shortener-screenshot]: src/main/frontend/src/images/screenshot-shortener.png
[Spring-boot.io]: https://img.shields.io/badge/Spring%20Boot-6DB33F?style=for-the-badge&logo=springboot&logoColor=white
[SpringBoot-url]: https://spring.io/projects/spring-boot
[Spring-security.io]: https://img.shields.io/badge/Spring%20Security-6DB33F?style=for-the-badge&logo=springsecurity&logoColor=white
[SpringSecurity-url]: https://spring.io/projects/spring-security
[Hibernate.org]: https://img.shields.io/badge/Hibernate-59666C?style=for-the-badge&logo=hibernate&logoColor=white
[Hibernate-url]: https://hibernate.org
[Postgresql.org]: https://img.shields.io/badge/Postgresql-4169E1?style=for-the-badge&logo=postgresql&logoColor=white
[Postgresql-url]: https://postgresql.org
[Jwt.io]: https://img.shields.io/badge/Json%20Web%20Tokens-000000?style=for-the-badge&logo=jsonwebtokens&logoColor=white
[Jwt-url]: https://jwt.io
[Docker.com]: https://img.shields.io/badge/Docker-2496ED?style=for-the-badge&logo=docker&logoColor=white
[Docker-url]: https://www.docker.com/
[Aws.com]: https://img.shields.io/badge/Aws-232F3E?style=for-the-badge&logo=amazonaws&logoColor=white
[Aws-url]: https://aws.amazon.com
[Swagger.io]: https://img.shields.io/badge/Swagger-85EA2D?style=for-the-badge&logo=swagger&logoColor=white
[Swagger-url]: https://swagger.io
[TypeScript.org]: https://img.shields.io/badge/TypeScript-3178C6?style=for-the-badge&logo=typescript&logoColor=white
[TypeScript-url]: https://www.typescriptlang.org/
[Angular.io]: https://img.shields.io/badge/Angular-DD0031?style=for-the-badge&logo=angular&logoColor=white
[Angular-url]: https://angular.io/
[Bootstrap.com]: https://img.shields.io/badge/Bootstrap-563D7C?style=for-the-badge&logo=bootstrap&logoColor=white
[Bootstrap-url]: https://getbootstrap.com
