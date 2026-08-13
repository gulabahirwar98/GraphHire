package com.graphhire.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.neo4j.driver.Driver;
import org.neo4j.driver.Session;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class GraphController {

    private final Driver driver;

    public GraphController(Driver driver) {
        this.driver = driver;
    }

    @GetMapping("/api/graph/{name}")
    public Map<String, Object> getDeveloperGraph(
            @PathVariable String name) {

        try (Session session = driver.session()) {

            return session.executeRead(tx -> {

                var result = tx.run("""
                    MATCH (d:Developer {name: $name})
                    OPTIONAL MATCH (d)-[:HAS_SKILL]->(s:Skill)
                    OPTIONAL MATCH (d)-[:WORKED_ON]->(p:Project)
                    RETURN d.name AS developer,
                           collect(DISTINCT s.name) AS skills,
                           collect(DISTINCT p.name) AS projects
                    """,
                    Map.of("name", name)
                );

                if (!result.hasNext()) {
                    return Map.of(
                        "message", "Developer not found"
                    );
                }

                var record = result.single();

                Map<String, Object> response = new HashMap<>();

                response.put(
                    "developer",
                    record.get("developer").asString()
                );

                response.put(
                    "skills",
                    record.get("skills").asList(value -> value.asString())
                );

                response.put(
                    "projects",
                    record.get("projects").asList(value -> value.asString())
                );

                return response;
            });
        }
    }
}