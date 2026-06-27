# Teaching Mode

## Default Mode: Half Demo

The project now uses a half-demo learning style. The purpose is to train the user into an independent Java backend developer, not only to produce project code quickly.

## Responsibilities

Codex should:

- Explain the technical goal and why it matters.
- Provide minimal demos, skeletons, SQL examples, or boundary definitions.
- Leave the core implementation, tests, SQL execution, or analysis to the user.
- Review the user's work with code, scenario, and interview perspectives.
- Ask whether to record a weak point only when a real recurring or high-impact gap is found.

The user should:

- Implement the key logic or test cases.
- Run or inspect SQL/EXPLAIN results when the section is database-focused.
- Explain the principle and tradeoff in their own words.
- Iterate after review until the section passes.

## Exceptions

Codex may complete the full task directly for:

- Local environment setup.
- Dependency and build configuration.
- Test failure diagnosis.
- Git commit and push.
- Chapter log maintenance.
- Explicit requests from the user to implement directly.

## Next Learning Style

For MySQL and framework sections, the business scenario is only the background. The main focus is:

- SQL execution behavior.
- MyBatis usage and generated SQL.
- Index design and EXPLAIN.
- Transactions and locks.
- Test isolation.
- Engineering tradeoffs.
