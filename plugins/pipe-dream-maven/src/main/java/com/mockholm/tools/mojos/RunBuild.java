package com.mockholm.tools.mojos;

import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.*;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;
import org.apache.maven.settings.Settings;

import java.io.IOException;

import com.mockholm.tools.models.BuildRequest;

/**
 * RunBuild Mojo to trigger a build in TeamCity.
 * This Mojo is intended to be used for starting builds in TeamCity.
 */
@Mojo(name = "run-build", defaultPhase = LifecyclePhase.NONE)
public class RunBuild extends AbstractMojo{
    @Parameter(property = "teamcity.url", required = true)
    private String teamcityUrl;

    @Parameter(property = "teamcity.buildTypeId", required = true)
    private String buildTypeId;

    @Parameter(property = "teamcity.token", required = true)
    private String token;

    @Parameter(property = "teamcity.branch", required = false)
    private String branch;

    
    public void execute() throws MojoExecutionException {
        OkHttpClient client = new OkHttpClient();
        ObjectMapper objectMapper = new ObjectMapper();

        getLog().info("Triggering build for TeamCity URL: " + teamcityUrl);

        getLog().info("Build Type ID: " + buildTypeId);

        // Create the JSON payload
        BuildRequest buildRequest = new BuildRequest(buildTypeId);

        if (branch != null) {
            getLog().info("Using branch: " + branch);
            // Set the branch name if provided
            buildRequest.setBranchName(branch);
        }

        String jsonPayload;
        try {
            jsonPayload = objectMapper.writeValueAsString(buildRequest);
        } catch (IOException e) {
            throw new MojoExecutionException("Failed to create JSON payload", e);
        }

        // Create the request
        RequestBody body = RequestBody.create(jsonPayload, MediaType.parse("application/json"));
        Request request = new Request.Builder()
                .url(teamcityUrl + "/app/rest/buildQueue")
                .post(body)
                .addHeader("Authorization", "Bearer " + token)
                .addHeader("Content-Type", "application/json")
                .build();

        // Execute the request
        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new MojoExecutionException("Failed to trigger build: " + response.message());
            }
            getLog().info("Build triggered successfully: " + response.body().string());
        } catch (IOException e) {
            getLog().error("Error during build trigger request", e);
            throw new MojoExecutionException("Failed to trigger build", e);
        }
    }
}
