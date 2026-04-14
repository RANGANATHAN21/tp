# Gabriel - Project Portfolio Page

## Overview
**TradeLog** is a Java-based CLI application for financial journaling and trade analysis. It helps traders record trades in a structured, keyboard-driven workflow while automatically maintaining performance data and enforcing consistent logging behaviour.

## Summary of Contributions

* **Code Contributed**: [RepoSense link](https://nus-cs2113-ay2526-s2.github.io/tp-dashboard/?search=gabri3123&breakdown=true&sort=groupTitle%20dsc&sortWithin=title&since=2026-02-20T00%3A00%3A00&timeframe=commit&mergegroup=&groupSelect=groupByRepos&checkedFileTypes=docs~functional-code~test-code~other&filteredFileName=)

* **Enhancements Implemented**:
    * **`DeleteCommand` (Core trade removal logic)**: Implemented the `DeleteCommand` to delete trades by user-specified index. The command performs validation at construction time, ensuring the input is present, numeric, and a positive integer before execution begins. During execution, it checks index bounds and provides clear feedback for invalid deletions instead of allowing the program to fail unexpectedly.
    * **Environment-aware deletion restrictions**: Extended `DeleteCommand` with mode-sensitive behaviour using `ModeManager`. In `LIVE` mode, historical trades cannot be deleted, preserving the integrity of live trading records. This added an additional layer of state-based validation beyond simple index checking, since command behaviour depends on the global application mode and the trade date.
    * **`UndoCommand` (One-step state restoration system)**: Designed and implemented a one-level undo mechanism that restores the most recent pre-mutation state of the `TradeList`. Before a mutating action is performed, the system saves a deep copy snapshot of the current trades. When `undo` is executed, the current list is cleared and rebuilt from the saved snapshot, ensuring the restored state is not affected by object aliasing or later mutations.
    * **Deep-copy based state preservation**: Implemented a dedicated `copyTrade` helper to create full copies of `Trade` objects for undo support. This avoids shallow-copy bugs and ensures that undo restores the exact earlier state rather than references to mutable objects that may already have changed.
    * **Persistence-aware recovery flow**: Integrated both commands with storage handling so that successful deletions and undo operations are saved back to disk. Also handled storage failures gracefully by informing the user when in-memory changes succeeded but persistence failed.

* **Quality Assurance**:
    * Wrote **`DeleteCommandTest`** to verify constructor validation, successful deletion, out-of-bounds deletion, and LIVE mode restrictions.
    * Wrote **`UndoCommandTest`** to verify undo after add, undo after delete, unavailable undo state, one-step-only undo behaviour, and undo execution in LIVE mode.
    * These tests cover both normal and edge-case behaviour, improving the reliability of two core mutating commands.

* **Contributions to the User Guide (UG)**:
    * Documented the usage of the `delete` command, including command format, expected behaviour, and error handling for invalid indices.
    * Documented the `undo` command and clarified its one-step limitation so users understand exactly what can and cannot be reverted.
    * Contributed to explaining how application modes affect command behaviour, especially the restriction on deleting historical records in `LIVE` mode.

* **Contributions to the Developer Guide (DG)**:
    * Documented the implementation of **`DeleteCommand`**, including constructor-based input validation and execution-time bounds checking.
    * Documented the design of **`UndoCommand`**, especially the decision to use a deep-copy snapshot of the `TradeList` to ensure safe restoration of mutable state.
    * Helped explain the interaction between command logic and `ModeManager`, showing how global application state affects command execution.

* **Contributions to Team-Based Tasks**:
    * Contributed core command functionality that directly supports safe data management in the application.
    * Improved system reliability by ensuring mutating commands do not silently corrupt state and that reversibility is available for recent actions.
    * Helped maintain consistency with the existing command architecture and storage workflow.

* **Review / Mentoring Contributions**:
    * Helped strengthen project quality through test coverage for key command flows and failure cases.
    * Contributed implementation patterns that teammates could follow for validation, state protection, and error handling in other commands.

* **Contributions Beyond the Project Team**:
    * Strengthened the project’s engineering quality by implementing safe mutation and recovery mechanisms that support a more reliable user experience.
