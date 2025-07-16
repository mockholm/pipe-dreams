package com.mockholm.tools.models;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * Represents a chain of builds to be executed sequentially.
 * Each chain has a name, an ID, and a list of build entries.
 */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class ChainRequest {

    /**
     * The name of the build chain.
     */
    @JsonProperty("name")
    private String name;

    /**
     * The unique identifier of the build chain.
     */
    @JsonProperty("id")
    private String id;

    /**
     * The list of builds to be executed in the chain.
     */
    @JsonProperty("builds")
    private List<BuildEntry> builds;

    /**
     * Represents a single build entry in the chain.
     * Each entry includes the build ID and a human-readable name.
     */
    public static class BuildEntry {

        /**
         * The TeamCity build configuration ID.
         */
        private String buildId;

        /**
         * The name of the build step.
         */
        private String name;

        /**
         * Constructs a new BuildEntry with the specified build ID and name.
         *
         * @param buildId the TeamCity build configuration ID
         * @param name    the name of the build step
         */
        public BuildEntry(String buildId, String name) {
            this.buildId = buildId;
            this.name = name;
        }

        /**
         * Returns the build configuration ID.
         *
         * @return the build ID
         */
        public String getBuildId() {
            return buildId;
        }

        /**
         * Sets the build configuration ID.
         *
         * @param buildId the build ID to set
         */
        public void setBuildId(String buildId) {
            this.buildId = buildId;
        }

        /**
         * Returns the name of the build step.
         *
         * @return the build name
         */
        public String getName() {
            return name;
        }

        /**
         * Sets the name of the build step.
         *
         * @param name the build name to set
         */
        public void setName(String name) {
            this.name = name;
        }
    }

    /**
     * Returns the name of the build chain.
     *
     * @return the chain name
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name of the build chain.
     *
     * @param name the chain name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Returns the ID of the build chain.
     *
     * @return the chain ID
     */
    public String getId() {
        return id;
    }

    /**
     * Sets the ID of the build chain.
     *
     * @param id the chain ID to set
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Returns the list of build entries in the chain.
     *
     * @return the list of builds
     */
    public List<BuildEntry> getBuilds() {
        return builds;
    }

    /**
     * Sets the list of build entries in the chain.
     *
     * @param builds the list of builds to set
     */
    public void setBuilds(List<BuildEntry> builds) {
        this.builds = builds;
    }
}
