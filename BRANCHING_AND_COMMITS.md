# Branching & Commit Convention (Team Simple Standard)

This project uses a lightweight Git workflow to keep `main` stable and enforce code review.

## 1) Core rules

- ✅ **No direct pushes to `main`**
- ✅ Every change goes through a **Pull Request (PR)**
- ✅ Small PRs preferred: **<= 200 lines** when possible
- ✅ At least **1 approval** required before merge

## 2) Branch naming

Create a new branch from `main` for each task/issue:

- `feature/<short-description>` — new features
- `fix/<short-description>` — bug fixes
- `chore/<short-description>` — tooling, config, infra
- `docs/<short-description>` — documentation only
- `refactor/<short-description>` — refactors (no behavior change intended)

**Examples**
- `feature/job-create-endpoint`
- `fix/job-status-transition`
- `chore/add-docker-compose`
- `docs/readme-improvements`

## 3) Commit messages

Use short, consistent commits:

- `feat: <what you added>`
- `fix: <what you fixed>`
- `chore: <tooling/config change>`
- `docs: <documentation change>`
- `refactor: <refactor change>`
- `test: <test change>`

**Examples**
- `feat: add Job entity and status enum`
- `feat: create POST /jobs endpoint (stub)`
- `fix: validate upload size and mime type`
- `chore: add flyway migrations`
- `test: add repository integration tests`
- `docs: add API usage examples`

## 4) Day-to-day commands

**Start a task**
```bash
git checkout main
git pull
git checkout -b feature/<short-description>
```

**Commit and push**
```bash
git add .
git commit -m "feat: <message>"
git push -u origin feature/<short-description>
```

**Open PR**
- Base: `main`
- Compare: your branch
- Fill the PR template
- Request review from at least 1 teammate

## 5) Merging

Recommended merge strategy:
- ✅ **Squash and merge** (keeps history clean for small teams)
- PR title should match the final commit message style.

