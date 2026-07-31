# Contributing to Nebula DB Microservices

Thank you for considering contributing to Nebula DB Microservices! We welcome contributions from the community to help improve this project.

Please read this document carefully to understand our contribution process and guidelines.

## Table of Contents

- [Code of Conduct](#code-of-conduct)
- [How Can I Contribute?](#how-can-i-contribute)
  - [Reporting Bugs](#reporting-bugs)
  - [Suggesting Features](#suggesting-features)
  - [Contributing Code](#contributing-code)
  - [Improving Documentation](#improving-documentation)
- [Development Setup](#development-setup)
- [Pull Request Process](#pull-request-process)
- [Coding Standards](#coding-standards)
- [Testing Guidelines](#testing-guidelines)
- [Community](#community)
- [Getting Help](#getting-help)

## Code of Conduct

Please note that this project is released with a Contributor Code of Conduct. By participating in this project you agree to abide by its terms. Please read the [CODE_OF_CONDUCT.md](CODE_OF_CONDUCT.md) file.

## How Can I Contribute?

### Reporting Bugs

Before submitting a bug report, please check if it has already been reported by searching the [Issues](https://github.com/parme/nebula-db-microservices/issues) page.

If you find a bug, please create an issue with the following information:

- **Clear and descriptive title**
- **Detailed description** of the problem
- **Steps to reproduce** the issue
- **Expected behavior** vs. **actual behavior**
- **Screenshots** or **logs** if applicable
- **Environment details** (Java version, Docker version, OS, etc.)
- **Possible solution** (if you have one)

### Suggesting Features

We love feature suggestions! Please open an issue with:

- **Clear and descriptive title** for the feature
- **Detailed description** of what the feature should do
- **Use cases** and **benefits** of the feature
- **Possible implementation approach** (if you have ideas)
- **Any potential drawbacks** or considerations

### Contributing Code

To contribute code, follow these steps:

1. **Fork the repository** on GitHub
2. **Clone your fork** locally:
   ```bash
   git clone https://github.com/your-username/nebula-db-microservices.git
   cd nebula-db-microservices
   ```
3. **Create a new branch** for your feature or bug fix:
   ```bash
   git checkout -b feature/awesome-feature
   # or
   git checkout -b bugfix/issue-description
   ```
4. **Make your changes** following our coding standards
5. **Add tests** for your changes
6. **Ensure all tests pass** (see [Testing Guidelines](#testing-guidelines))
7. **Commit your changes** with a clear, descriptive commit message
8. **Push to your fork**:
   ```bash
   git push origin feature/awesome-feature
   ```
9. **Open a Pull Request** against the `main` branch of the original repository

### Improving Documentation

Documentation improvements are highly valued! This includes:

- Fixing typos or grammar errors
- Improving clarity of existing documentation
- Adding new documentation for features
- Creating tutorials or examples
- Translating documentation to other languages

Please follow the same process as [Contributing Code](#contributing-code) for documentation changes.

## Development Setup

### Prerequisites

- [Java JDK 26](https://jdk.java.net/26/)
- [Maven 3.8+](https://maven.apache.org/)
- [Docker Engine](https://docs.docker.com/engine/install/) 24.0+
- [Docker Compose](https://docs.docker.com/compose/install/) v2.0+
- [Git](https://git-scm.com/)
- IDE of choice (IntelliJ IDEA, VS Code, Eclipse, etc.)

### Setting Up the Development Environment

1. **Clone the repository**:
   ```bash
   git clone https://github.com/your-username/nebula-db-microservices.git
   cd nebula-db-microservices
   ```

2. **Create a `.env` file** in the root directory:
   ```bash
   cp .env.example .env
   ```
   Edit the `.env` file to set appropriate values for:
   - Database credentials
   - JWT secret key
   - Any other environment-specific variables

3. **Build all services**:
   ```bash
   ./mvnw clean install -DskipTests
   ```

4. **Start the infrastructure services** (MySQL, Kafka, Eureka):
   ```bash
   docker-compose up -d mysql kafka eureka-server
   ```

5. **Wait for services to be healthy** (check with `docker-compose ps`)

6. **Run individual services** for development:
   ```bash
   # Example: Run the auth service
   cd service-infra/nebula-auth
   ../../mvnw spring-boot:run
   ```

### Useful Development Commands

- **Build all services**: `./mvnw clean install`
- **Run tests**: `./mvnw test`
- **Run a specific service**: `cd <service-directory> && ../mvnw spring-boot:run`
- **Start all services with Docker Compose**: `docker-compose up -d`
- **Stop and clean up**: `docker-compose down -v`
- **View logs**: `docker-compose logs -f <service-name>`

## Pull Request Process

When you submit a pull request, please follow these guidelines:

1. **Ensure your PR targets the `main` branch**
2. **Keep your PR focused** on a single feature or bug fix
3. **Write a clear and descriptive title** for your PR
4. **Provide a detailed description** in the PR body:
   - What changes were made
   - Why the changes are necessary
   - How to test the changes
   - Any breaking changes or migration steps
5. **Link to any related issues** using `Fixes #issue-number` or `References #issue-number`
6. **Ensure all tests pass** (both locally and in CI)
7. **Follow our [coding standards](#coding-standards)**
8. ** Update documentation** if your changes affect it
9. **Be responsive to feedback** from maintainers

### What We Look For in a PR

- **Correctness**: Does the code work as intended?
- **Clarity**: Is the code easy to read and understand?
- **Testing**: Are there adequate tests for new functionality?
- **Documentation**: Is the code well-commented? Is user documentation updated?
- **Standards compliance**: Does the code follow our style guides?
- **Performance**: Does the change introduce any performance regressions?

### Merging a PR

Once a PR has:
- Received approval from at least one maintainer
- Passed all CI checks
- Had any requested changes addressed

It can be merged by a maintainer using the "Squash and merge" option to keep a clean commit history.

## Coding Standards

### Java Code Style

We follow the [Google Java Style Guide](https://google.github.io/styleguide/javaguide.html) with the following exceptions:

- **Indentation**: 4 spaces (not tabs)
- **Line length**: Maximum 120 characters
- **Imports**: Use explicit imports (no wildcards)
- **Annotations**: Place annotations on the line above the element
- **Braces**: Opening brace on the same line as the declaration
- **Nullability**: Use `@NonNull` and `@Nullable` from `org.springframework.lang` package

### Dependency Management

- Use Maven for dependency management
- Keep dependencies up-to-date but stable
- Prefer Spring Boot managed versions
- Document any version overrides in the parent pom.xml

### Git Practices

- Write clear, descriptive commit messages in the present tense
- Reference issue numbers in commit messages when applicable: `Fix #123: Add user authentication endpoint`
- Keep commits atomic and focused
- Squash fixup commits before submitting a PR

### Logging

- Use SLF4J with Logback (already included in Spring Boot)
- Follow standard logging levels:
  - `ERROR`: System is unusable
  - `WARN`: Potential problems
  - `INFO`: General operational messages
  - `DEBUG`: Detailed information for debugging
  - `TRACE`: Very detailed information
- Avoid `System.out.println()` or `printStackTrace()` in production code

## Testing Guidelines

### Unit Tests

- Use JUnit 5 (Jupiter) and Mockito
- Place test classes in `src/test/java` mirroring the package structure
- Name test classes as `[ClassName]Test`
- Name test methods descriptively using `given_when_then` format
- Aim for high code coverage (80%+)
- Test edge cases and error conditions
- Mock external dependencies (databases, services, etc.)

### Integration Tests

- Use `@SpringBootTest` with `@AutoConfigureTestDatabase` or Testcontainers
- Testcontainers is preferred for databases and messaging systems
- Place integration tests in a separate source set if needed
- Clean up test data after each test

### Test Naming Conventions

```
given_precondition_when_action_then_expectedResult
```

Example:
```java
@Test
void givenValidCredentials_whenLoginAttempted_thenReturnsAuthToken() {
    // test implementation
}
```

### Running Tests

- **All tests**: `./mvnw test`
- **Specific test**: `./mvnw test -Dtest=ClassNameTest`
- **Skip tests**: `./mvnw install -DskipTests`
- **Generate coverage report**: `./mvnw jacoco:report`

## Community

### Communication

- **Issues**: Use GitHub issues for bug reports, feature requests, and discussions
- **Pull Requests**: Use GitHub pull requests for code contributions
- **Discussions**: GitHub Discussions for open-ended conversations and ideas

### Getting Help

If you need help with your contribution:

1. Check the [existing issues](https://github.com/parme/nebula-db-microservices/issues)
2. Ask for clarification in the issue thread
3. Tag a maintainer if you need direct assistance
4. Refer to the [documentation](#) for guidance

### Recognition

We appreciate all contributions! Contributors will be:

- Mentioned in the release notes
- Added to the CONTRIBUTORS.md file (if we create one)
- Recognized in our social media announcements (for significant contributions)

## License

By contributing to Nebula DB Microservices, you agree that your contributions will be licensed under the MIT License. Please see the [LICENSE](LICENSE) file for details.

## Questions?

If you have any questions about contributing, please open an issue or reach out to the maintainers.

Thank you for helping make Nebula DB Microservices better!