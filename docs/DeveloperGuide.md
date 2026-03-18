# Developer Guide: TradeLog

## 1. Acknowledgements

* **Java Standard Library**: Used for core logic, collections, and I/O operations.
* **Checkstyle**: Enforcement of coding standards (Google Style).
* **Gradle**: Build automation and dependency management.
* **SE-EDU AddressBook-Level 3**: Structural inspiration for CLI parsing and architectural patterns. [Source](https://se-education.org/addressbook-level3/)

---

## 2. Design & Implementation

### 2.1 Architecture Overview
TradeLog follows a modular CLI architecture, separating concerns into four main components:
* **Logic**: Handles prefix-based command parsing (e.g., `t/`, `d/`) and execution flow for commands.
* **Model**: Encapsulates the `Trade` entity and the `TradeList` collection, handling in-memory data representation and ROI calculations.
* **Storage**: Implements an **immediate-save mechanism**. After every successful state-changing command (add, edit, delete), the data is persisted to `tradelog.txt`.
* **UI**: Manages formatted console output and user interaction.

---

## 3. Product Scope

### 3.1 Target User Profile
**Daniel** is a proprietary trader who works independently and relies heavily on data to refine his trading strategies. He spends most of his day analysing charts and executing trades, and prefers fast, keyboard-based tools over graphical interfaces. He values efficiency, accuracy, and structured data analysis to improve his trading performance.

### 3.2 Value Proposition
Provides a CLI-based, systematic way to log trades and test systems that is faster and more efficient than logging trades on Google Sheets. Has the ability to automatically calculate trade details such as Risk:Reward ratio, ROI, expected value (EV) of the system with varying timeframes.

### 3.3 Scope
TradeLog helps financial trading professionals systematically log, manage, and analyze their trading data through a fast CLI-based system. It enables users to:
* Log and manage trades efficiently.
* Calculate key trading metrics (ROI, Risk:Reward, EV, YTD performance).
* Filter and analyze trades by strategy, ticker, or timeframe.
* Test and compare trading systems.
* Monitor risk exposure.

---

## 4. User Stories

| Version | As a ... | I want to ... | So that I can ... |
| :--- | :--- | :--- | :--- |
| **v1.0** | trader | Log my trading data | I can call on the data to run tests on trading systems |
| **v1.0** | trader | Automatically calculate my Year-To-Date (YTD) ROI | Easily track my progress and performance |
| **v1.0** | trader | delete an incorrectly entered set of data | my statistics remain accurate |
| **v1.0** | trader | edit previously logged trades | I can correct mistakes in my data |
| **v1.0** | forgetful trader | trades are saved automatically after every command | I don't lose recent entries due to distraction or fatigue |
| **v2.0** | trader | Filter my trades by a specific ticker symbol | Review my performance on a single asset |
| **v2.0** | trader | Save trading systems | I can easily test them on different datasets |
| **v2.0** | trader | Automatically calculate my risk:reward ratio | Quickly decide if I want to confirm a trade |
| **v2.0** | trader | Switch between testing mode and live trading mode | I can separate live trades from backtest trades |
| **v2.0** | trader | Automatically calculate Expected Value (EV) of a specific strategy | I know the mathematical advantage of my system |
| **v2.0** | trader | Automatically convert and export my data to CSV | Better review my performance and use other tools |
| **v2.0** | trader | Set a Daily Loss Limit that warns me | Prevents me from taking unnecessarily large risk |
| **v2.0** | trader | tag trades with a specific strategy name | I can group and evaluate them easily |
| **v2.0** | trader | sort trades by profit or loss | I can quickly identify my biggest wins and losses |
| **v2.0** | trader | Automatically calculate the EV of multiple strategies | Decide which strategy has the best performance |
| **v2.0** | trader | see my current win or loss streak | I remain aware of potential overconfidence or tilt |
| **v2.0** | careless trader | be warned if I enter a duplicate record | I don't accidentally double-count the same fill |
| **v2.0** | expert trader | set short aliases for long tickers | I don't type dots and hyphens hundreds of times |
| **v2.0** | trader | see tickers I've looked up but didn't trade | So that I can quickly enter them if I circle back |
| **v2.0** | trader | mark a ticker as "watched but not taken" | Remember which setups I passed on during review |
| **v3.0** | trader | tag each trade with my emotional state | I can identify psychological patterns |
| **v3.0** | trader | view a summary over a selected date range | I can analyze short-term results |
| **v3.0** | trader | view my win rate for a specific strategy | I can assess its consistency |
| **v3.0** | trader | calculate average risk per trade | I can monitor my risk management discipline |
| **v3.0** | trader | back up my trading data locally | I do not lose my records |
| **v3.0** | trader | load previously saved trading sessions | I can continue my analysis seamlessly |
| **v3.0** | trader | complete a pre-trade checklist before entry | I follow my trading plan consistently |
| **v3.0** | trader | view multiple strategies side-by-side | Objectively compare their performance |
| **v3.0** | trader | automatically calculate maximum drawdown | I understand my worst-case risk exposure |
| **v3.0** | trader | export trades from a specific date range to CSV | Share selected periods with my mentor or accountant |
| **v3.0** | trader | automatically calculate the R-multiple | I evaluate performance relative to risk |
| **v3.0** | trader | review a summary and confirm before saving | So that I catch typos before they enter my records |
| **v3.0** | inexperienced trader | see how many trades I've taken today | So that I know if I'm overtrading |
| **v3.0** | trader | receive an alert if win rate drops below threshold | I can review and adjust my strategy promptly |
| **v3.0** | trader | write reflections for each trade | I can improve my decision-making process |
| **v3.0** | trader | filter and analyze trades by time of day | I can identify when I perform best |
| **v3.0** | trader | Bulk import historical trades | I can test my trading systems on other datasets |
| **v3.0** | trader | attach a chart screenshot to each trade | I can visually review my entry and exit decisions |
| **v3.0** | trader | see my total capital currently at risk | I avoid overexposure |

---

## 5. Non-Functional Requirements

1. **Platform Independence**: Must run on any OS with Java 17 or higher installed.
2. **Performance**: Statistics calculation (EV, ROI) should take <100ms for up to 2,000 trades.
3. **Data Persistence**: Immediate auto-save to `tradelog.txt` after every valid state-changing command.
4. **Offline Capability**: All trade data must be stored locally without requiring cloud connectivity.

---

## 6. Glossary

* **Ticker**: Unique symbol representing a traded asset (e.g., AAPL).
* **R:R (Risk:Reward)**: The ratio of potential profit to potential loss.
* **EV (Expected Value)**: The average amount a trader can expect to win or lose per trade.
* **ROI (Return on Investment)**: Percentage return relative to capital.

---

## 7. Instructions for Manual Testing

### 7.1 Initial Launch
1. Ensure the `data/` folder is empty.
2. Run `java -jar TradeLog.jar`.
3. Verify that the application creates a fresh `tradelog.txt` file.

### 7.2 Testing CRUD (v1.0)
1. **Add**: `add t/TSLA d/2026-03-18 dir/long e/200 x/220 s/190 o/win strat/Trend`
2. **Edit**: `edit 1 x/230`
3. **Delete**: `delete 1`
4. **List**: `list` (Verify it reflects changes immediately in the console).