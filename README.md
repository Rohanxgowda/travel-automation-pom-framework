# travel-automation-pom-framework
✅ Clear that it’s travel-related ✅ Mentions POM (Page Object Model) ✅ Uses “framework” — shows it’s reusable, not a one-off test script
>>>>>>> f6d377ef6dfe051bc25c3843096d3a248476e469
=======
# Travel Website Test Automation Framework

This project is a test automation framework for travel websites using Selenium WebDriver, Java, TestNG, and Maven, following the Page Object Model (POM) design pattern.

## Prerequisites

- Java JDK 11 or higher
- Maven
- Chrome or Firefox browser

## Project Structure

```
travel-automation/
├── src/
│   ├── main/
│   │   └── java/
│   │       └── com/
│   │           └── travel/
│   │               ├── pages/
│   │               │   ├── BasePage.java
│   │               │   └── HomePage.java
│   │               └── utils/
│   │                   └── WebDriverFactory.java
│   └── test/
│       └── java/
│           └── com/
│               └── travel/
│                   └── tests/
│                       └── FlightSearchTest.java
├── pom.xml
├── testng.xml
└── README.md
```

## Features

- Page Object Model design pattern
- Selenium WebDriver for browser automation
- TestNG for test execution and assertions
- Maven for project management and dependencies
- WebDriverManager for automated driver management
- Support for Chrome and Firefox browsers

## Running the Tests

To run the tests, execute the following command in the project root directory:

```bash
mvn clean test
```

## Code Linting

This project uses Checkstyle for code quality and style checking. Checkstyle is configured to run automatically during the Maven validate phase.

### Running Linting Manually

To run linting checks manually, use the following command:

```bash
mvn checkstyle:check
```

This will check your Java code against the rules defined in `checkstyle.xml` and report any violations in the console.

### Linting Configuration

- Configuration file: `checkstyle.xml`
- Checks include: naming conventions, whitespace, imports, size violations, and common coding problems
- Linting runs automatically during `mvn validate` (which is part of `mvn clean test`)
- If linting fails, the build will fail with `failsOnError=true`

### Fixing Linting Issues

Common issues and fixes:
- **LineLength**: Ensure lines are no longer than 120 characters
- **UnusedImports**: Remove unused import statements
- **JavadocMethod**: Add Javadoc comments to public methods
- **MagicNumber**: Replace magic numbers with named constants
- **WhitespaceAround**: Add spaces around operators and keywords

To generate a detailed report, run:

```bash
mvn checkstyle:checkstyle
```

This will generate an HTML report in `target/site/checkstyle.html`.

## Test Scenarios

1. Navigate to the travel website
2. Go to Flights section
3. Enter source and destination locations
4. Select a date for next month
5. Search for flights
6. Print cheapest and second cheapest flight details
7. Open Google in a new tab
=======
# travel-automation-pom-framework
✅ Clear that it’s travel-related ✅ Mentions POM (Page Object Model) ✅ Uses “framework” — shows it’s reusable, not a one-off test script
>>>>>>> f6d377ef6dfe051bc25c3843096d3a248476e469
