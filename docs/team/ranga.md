# Ranga - Project Portfolio Page

## Summary of Contributions
### [v1.0] - Core Functional Implementation(MVP)

| Category                     | Contribution Details                                                                                                                                                                                  |
|:-----------------------------|:------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| **Application Architecture** | Designed and implemented parts of the main **`TradeLog`** entry point, including the core input loop, command routing via `Parser`, and persistent storage lifecycle (load on startup, save on exit). |
| **Command Logic**            | Implemented **`ListCommand`** to display all logged trades in a structured, numbered format, and **`Command`** as the abstract base class defining the contract for all commands.                     |
| **UI Layer**                 | Designed and built the **`Ui`** class, centralising all user-facing output including welcome/goodbye messages, trade display, error handling, and performance summary formatting.                     |
| **Parser & Validation**      | Implemented **`Parser`** for command routing and **`ParserUtil`** for reusable data parsing and validation helpers (price parsing, ticker formatting, direction validation, stop loss validation).    |
| **Defensiveness**            | Added assertions, logging, and exception handling across `Ui`, `ListCommand`, and `TradeLog` to improve robustness and traceability.                                                                  |
| **Standards & QA**           | Followed the team's coding standards across all owned files, and wrote JUnit tests for `Ui`, `ListCommand`, and `Command`.                                                                            |
| **Testing Infrastructure**   | Set up the regression test framework including `input.txt` and `EXPECTED.TXT` covering all 6 MVP commands.                                                                                            |
| **Documentation**            | Contributed to reviewing the User Guide and Developer Guide by documenting the architecture, command routing flow, and UI design decisions.                                                           |