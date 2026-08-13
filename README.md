# GraphHire

GraphHire is a graph-based developer discovery application built with
Spring Boot and CognoDB.

The application allows users to discover developers based on their
skills and explore relationships between developers, skills, and projects.

---

## Features

- Search developers by skill
- View developer profiles
- View project profiles
- Explore developer-to-skill relationships
- Explore developer-to-project relationships
- Multi-hop graph traversal
- REST APIs
- Web-based user interface

---

## Technology Stack

- Java 21
- Spring Boot
- CognoDB
- Neo4j Java Driver
- Maven
- HTML
- CSS
- JavaScript

---

## Graph Model

The application uses a graph-based data model.

### Nodes

- Developer
- Skill
- Project

### Relationships

```text
Developer ──HAS_SKILL──> Skill

Developer ──WORKED_ON──> Project

Project ──USES──> Skill