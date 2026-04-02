# Alexander - Project Portfolio Page

## Overview

TradeLog is a CLI-based trading journal that helps traders log, analyze, and backtest trading strategies. It automatically calculates key metrics such as Risk:Reward ratios and Expected Value (EV) to help traders identify their mathematical edge.

My contributions focused on **data persistence**, **trade filtering**, **safe trade editing**, and **multi-profile storage management**.

---

## Summary of Contributions

### [v1.0] - Core Backtesting Suite

| Category | Contribution Details |
|:---------|:--------------------|
| **Data Persistence** | Implemented **`Storage`** class with `saveTrades()` and `loadTrades()` methods, including directory creation, pipe-delimited serialization, and graceful handling of missing files. |
| **Trade Serialization** | Implemented `toStorageString()` in **`Trade`** to produce the pipe-delimited format used for persistent storage, ensuring round-trip consistency between save and load. |
| **Command Logic** | Implemented **`EditCommand`** with atomic partial updates — all fields are validated before any change is committed, preventing partially-updated trades from being saved in an invalid state. |
| **Defensiveness** | Added assertions and logging across `EditCommand`, `AddCommand`, `DeleteCommand`, and `SummaryCommand` to preserve internal invariants and improve traceability. |

### [v2.0] - System & Logic Enhancement

| Category | Contribution Details |
|:---------|:--------------------|
| **Filter Feature** | Implemented **`FilterCommand`** supporting filtering by ticker (`t/`), strategy (`strat/`), and date (`d/`), with an optional `-p` flag for partial/substring matching. Displays matching trade indices and a filtered performance summary. |
| **Filter Integration** | Integrated `FilterCommand` with `SummaryCommand` so filtered results automatically show aggregated metrics (Win Rate, EV, Total R) for the matched subset of trades. |
| **Multi-Profile Storage** | Implemented **`ProfileManager`** to support password-based storage profiles. On startup, users enter a password to load an existing profile or create a new one, enabling multiple separate trade logs on the same machine. |
| **UI Extensions** | Extended **`Ui`** with `readPassword()`, `readLine()`, and `readCommand()` to support the interactive password prompting flow required by `ProfileManager`. |
| **Defensiveness** | Added assertions and logging in `FilterCommand` and `ProfileManager` to enforce null checks and document expected state at runtime. |
| **Testing** | Wrote JUnit tests for `FilterCommand` covering exact and partial matching, no-match cases, and missing filter arguments. Updated `TradeLogTest` to include the password prompt in the input stream. |
| **Documentation** | Documented the `filter` command in the User Guide with format, flags, examples, and expected output. |

### Diagrams

Added the following UML Diagrams:

- `save-trades-on-exit-diagram`
- `existing-matching-profile-diagram`
- `password-mismatch-and-new-profile-creation-diagram`
- `filtering-trades-diagram`

### Team-based Tasks

- Contributed to the defensiveness of the code. Added most of the assertions for the commands and parser classes
- Contributed to updating the UG and DG for new functionalities that were added
- Contributed to JUnit tests for new functionalities and updated pervious tests to work with new changes
- Ensure teammates used issue tracking to track progress for the new milestone v2.0
- Further abstracted implementation of methods and features (e.g. abstracted scanner instantiation and closing to Ui class)
- Further streamlined TradeLog implementation

### Review/mentoring contributions

- Reviewed pull requests
- Checked implementation of new features for potential bugs
