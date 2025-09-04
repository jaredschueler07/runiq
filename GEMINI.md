# Gemini Interaction Guidelines

This document outlines the rules and guidelines for interacting with me, Gemini.

## Core Directives

1.  **Consult CLAUDE.md:** Before taking any action or providing any information, I must first consult `CLAUDE.md`. This file contains critical project context, architectural decisions, and ongoing updates that are essential for me to understand and follow. It is the primary source of truth for project-related guidance.

2.  **Maintain CLAUDE.md Log:** Every time I make a change, update, or perform any significant action (including creating or modifying this `GEMINI.md` file), I must append a log entry to the bottom of `CLAUDE.md`. This log should be under a "Gemini Interaction Log" section and must detail the action taken, the date, and any relevant context. I must never delete existing content from `CLAUDE.md`.

3.  **Adhere to Project Conventions:** I will rigorously follow the coding styles, patterns, and conventions established in the project. I will analyze existing code to ensure my contributions are consistent.

4.  **Verify Dependencies:** I will not assume any library or framework is available. I will check `build.gradle.kts`, `libs.versions.toml`, and other configuration files to verify dependencies before using them.

5.  **Proactive but Cautious:** I will be proactive in fulfilling requests, but I will always ask for clarification before making significant changes that are not explicitly requested.

## Linear Project Integration

When a request involves analysis or understanding of our Linear project, use the following details to interact with the Linear GraphQL API.

-   **API Key**: The API key is stored in `local.properties` under the key `linear.apiKey`. You must read this file to retrieve the key before making any API calls.
-   **GraphQL Endpoint**: `https://api.linear.app/graphql`
-   **Schema Reference**: For information on the API schema, refer to the [Apollo Studio documentation](https://studio.apollographql.com/public/Linear-API/variant/current/schema/reference).