# Contributing Guide

This repository is a **technical growth challenge** project. We prioritize good engineering practices over speed.

## 1) Workflow rules

- **No direct pushes to `main`**.
- All changes must go through a **Pull Request (PR)**.
- At least **1 approval** is required before merging.
- Prefer **small PRs** (<= 200 lines) to keep reviews fast and useful.

## 2) Branching & commits

Follow the team standard in:
- `BRANCHING_AND_COMMITS.md`

## 3) Code review checklist (what reviewers should verify)

### Correctness
- Does it solve the issue / requirement?
- Are edge cases handled (nulls, empty lists, invalid inputs, missing resources)?
- Are error responses consistent and meaningful?

### Architecture & design
- Are responsibilities separated (Controller vs Use-case vs Domain vs Infra)?
- No domain rules inside controllers.
- Infra details (JPA, messaging, OCR libs) are not leaking into the domain layer.
- Naming is clear and consistent.

### Data & persistence
- Are transactions used correctly?
- Any N+1 issues introduced?
- If DB schema changed, are migrations included (Flyway/Liquibase)?
- Are indexes considered for query-heavy paths?

### Reliability & async processing
- Is job processing idempotent (safe to retry)?
- Are retries bounded and failures surfaced properly?
- Are job state transitions valid (CREATED -> RUNNING -> DONE/FAILED)?

### Security
- No secrets committed.
- JWT/auth checks are correct.
- Users cannot access resources they don't own (resource-owner authorization).

### Testing
- Unit tests cover core domain logic and use-cases when applicable.
- Integration tests cover JPA and messaging paths when applicable.
- Tests are deterministic and do not depend on local machine state.

### Docs & maintainability
- README updated if behavior changes.
- Code is readable and documented where needed.
- Public APIs have examples when helpful.

## 4) PR etiquette

- Keep PRs focused on one concern.
- If a PR is large, split it into multiple PRs.
- Respond to review comments with fixes or clear reasoning.

