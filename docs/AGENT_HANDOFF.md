# Advent of Code 2024 — coding handoff

Snapshot: **2026-11-02 UTC**. This is a takeover map, not release authorization. Recheck Git status, the requested scope and current runtime before acting. Existing AGENTS.md and linked contract/runbooks remain applicable. Older dated status notes are evidence from their stated date, not current permissions.

## Purpose and architecture

Standalone Java solutions for Advent of Code 2024.

Day-specific main classes read repository-relative puzzle inputs. There is no package manager, backend or deployment pipeline; use each class comment and parser as the problem-specific guide.

## Locations and current work

- Usual local workspace: `/Users/jgreco/Documents/advent2024`.
- Source inspected for this handoff: `/Users/jgreco/Documents/advent2024`.
- Existing repository: [jimgreco/advent2024](https://github.com/jimgreco/advent2024); default branch `main`, verified source head `8005f3b469e0fa5212482b7d03e4e731b0f31043`.
- Documentation branch: `docs/agent-handoff-2026-11-02`, based on that head; application code was not changed.
- Primary source HEAD matched the verified remote at inventory time; branch `main`.

## Code and guide map

- [src/advent2024](../src/advent2024)
- [resources](../resources)

## Local setup and dependencies

Commands below are source-verified entry points, **not commands executed during this documentation pass**. Use disposable local data. Read the command comments and linked setup guide before running migrations, creating fixtures or starting listeners. Reuse prepared environments where suitable.

- `Use a JDK compatible with the selected source language features; the repository does not pin one.`
- `javac -d /tmp/advent2024-classes src/advent2024/Day01.java`
- `java -cp /tmp/advent2024-classes advent2024.Day01  # run from repository root`

Configuration names/locations only; never copy secret values into source, terminal output or handoff documents:

No secret-bearing configuration is needed for the documented local path; do not invent a credential setup.

Use existing lockfiles/manifests; do not upgrade dependencies to perform a handoff. For native work, select an actually installed Xcode SDK/simulator from the checked-in project configuration.

## Tests and verification

- `Compile the changed day and compare against its known sample/expected answer. No automated test harness was found.`

Documentation-only changes require path/link, accuracy, diff and secret-exclusion review; do not run heavyweight builds or production smoke mutations for prose changes. Application tests were not rerun in this pass. Past test/release evidence below retains its original date. UI changes still require actual rendered inspection, and synthetic tests do not prove real provider/device acceptance.

## Decisions and constraints

- Keep original input files unchanged; use separate synthetic/sample fixtures for regression tests. Do not publish private input values or answer logs in handoff notes.

## Release state and workflow effects

Remote main matches the local source. No application release applies.

No exact-head GitHub run was present in the bounded recent-main query. This is not proof of deployment, build success or absence of older runs.

The handoff is published on a documentation feature branch, not main. Current checked-in GitHub push triggers are main-only (or absent). Blog hosting branch settings were separately checked. No native release, image publication or server deployment is intended from this documentation push.

## Known gaps and next safe work

- JDK compatibility and individual solution correctness were not rerun. Some days depend on helper classes; compile those together when required.

Before the next task, verify source HEAD, dirty/staged work and the selected subsystem; read only the relevant guide. Check whether a listed idea is already implemented in another branch before rebuilding it. Preserve unrelated work and paused experiments. Release/permission/data changes require the applicable current authorization.
