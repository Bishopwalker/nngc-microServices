# Code Pattern Examples

This directory contains example code patterns used throughout the NNGC MicroServices project.

## Files

- `controller-pattern.java` - Standard REST controller pattern with proper error handling and ApiResponse wrapper
- `service-pattern.java` - Service layer implementation with repository pattern and DTO conversion
- `dto-pattern.java` - Data Transfer Object pattern with validation annotations and JSON formatting

## Usage

When implementing new features, reference these patterns to maintain consistency across the codebase. Each pattern follows the established conventions defined in CLAUDE.md.

## Key Principles

1. **Controller Layer**: Handle HTTP requests, validation, and response formatting
2. **Service Layer**: Business logic, transactions, and data transformations
3. **DTO Pattern**: Data validation, JSON serialization, and clean API contracts
4. **Error Handling**: Consistent error responses using ApiResponse wrapper
5. **Validation**: Use Bean Validation annotations for input validation