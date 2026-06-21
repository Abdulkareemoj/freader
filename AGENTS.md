# Agent Skills in this Workspace

This repository includes locally managed agent skills and a locked list of external skills used for Kotlin Multiplatform / Compose Multiplatform development.

## Local agent skills

These skills are located under `.agents/skills/` and are available to any agent workflow or tooling that reads the workspace agent skill directory.

- `compose-skill`
  - Path: `.agents/skills/compose-skill/SKILL.md`
  - Description: Expert Kotlin Multiplatform (KMP) and Compose Multiplatform guidance.

- `kotlin-specialist`
  - Path: `.agents/skills/kotlin-specialist/SKILL.md`
  - Description: Idiomatic Kotlin implementation patterns, coroutine concurrency, Flow handling, MPP architecture, Compose UI, and modern Kotlin design.

- `kotlin-tooling-agp9-migration`
  - Path: `.agents/skills/kotlin-tooling-agp9-migration/SKILL.md`
  - Description: Migration guidance for Kotlin Multiplatform projects targeting Android Gradle Plugin 9.0+.

## External locked skills

The repository also maintains `skills-lock.json` to pin external skills and sources.

- `kotlin-specialist`
  - Source: `jeffallan/claude-skills`
  - Locked skill path: `skills/kotlin-specialist/SKILL.md`

- `kotlin-tooling-agp9-migration`
  - Source: `Kotlin/kotlin-agent-skills`
  - Locked skill path: `skills/kotlin-tooling-agp9-migration/SKILL.md`

## How to use these skills

1. Install or configure your agent tooling to load skills from the workspace.
2. Use the skill names and descriptions above to choose the right agent role.
3. For local skills, inspect the corresponding `.agents/skills/<skill-name>/SKILL.md` file for full instructions.
4. For locked skills, use `skills-lock.json` as the source of truth for external skill versions.

## Adding or updating skills

- Add a new local agent skill by creating a new directory under `.agents/skills/` and adding a `SKILL.md` file.
- Update `skills-lock.json` when adding or changing externally sourced skills.
- Keep descriptions aligned with the actual `name` and `description` metadata inside each `SKILL.md` file.
