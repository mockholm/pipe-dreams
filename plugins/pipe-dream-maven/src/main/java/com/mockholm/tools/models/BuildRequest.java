package com.mockholm.tools.models;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * BuildRequest is the request model for starting a TeamCity Build.
 */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class BuildRequest {

    // buildType 
    @JsonProperty("buildType")
    private BuildType buildType;

    // branchName
    @JsonProperty("branchName")
    private String branchName;

    /**
     * Constructor
     * @param buildTypeId the build type ID
     * @param branchName the branch name
     */
    public BuildRequest(String buildTypeId, String branchName) {
        this.buildType = new BuildType(buildTypeId);
        this.branchName = branchName;
    }

    /**
     * Constructor
     * @param buildTypeId the build type ID
     */
    public BuildRequest(String buildTypeId) {
        this.buildType = new BuildType(buildTypeId);
    }

    /**
     * Gets the build type.
     * @return buildType {@link BuildType} ID used to start a specific build
     */
    public BuildType getBuildType() {
        return buildType;
    }

    /**
     * Sets the build type.
     * @param buildType {@link BuildType}
     */
    public void setBuildType(BuildType buildType) {
        this.buildType = buildType;
    }

    /**
     * Gets the branch name.
     * @return branchName name of the branch to run the build
     */
    public String getBranchName() {
        return branchName;
    }

    /**
     * Sets the branch name.
     * @param branchName name of the branch to run a build
     */
    public void setBranchName(String branchName) {
        this.branchName = branchName;
    }

    /**
     * BuildType object contains the ID needed to run a build.
     */
    public static class BuildType {
        /**
         * The build ID.
         */
        @JsonProperty("id")
        private String id;

        /**
         * Constructor
         * @param id build ID
         */
        public BuildType(String id) {
            this.id = id;
        }

        /**
         * Gets the build ID.
         * @return the build ID
         */
        public String getId() {
            return id;
        }

        /**
         * Sets the build ID.
         * @param id the build ID
         */
        public void setId(String id) {
            this.id = id;
        }
    }
}
