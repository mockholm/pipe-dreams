package com.mockholm.tools.mojos;

import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.*;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugin.MojoExecutionException;
import com.mockholm.tools.models.ParameterRequest;

import com.fasterxml.jackson.core.JsonProcessingException;
import java.io.IOException;


/**
 * Update a parameter in the project configuration
 * This Mojo is intended to be used for updating project-level parameters in TeamCity.
 * It extends the functionality of UpdateParameter to allow for project-level updates.
 */
@Mojo(name = "update-project-parameter", defaultPhase = LifecyclePhase.NONE)
public class UpdateProjectParameter extends AbstractMojo {

    @Parameter(property = "teamcity.url", required = true)
    private String teamcityUrl; // URL of the TeamCity server
    @Parameter(property = "teamcity.projectId", required = true)
    private String projectId; // ID of the project to update
    @Parameter(property = "teamcity.token", required = true)
    private String token; // Authentication token for TeamCity
    @Parameter(property = "teamcity.parameterName", required = true)
    private String parameterName; // Name of the parameter to update
    @Parameter(property = "teamcity.parameterValue", required = true)
    private String parameterValue; // New value for the parameter
    public void execute() throws MojoExecutionException {
        OkHttpClient client = new OkHttpClient();
        ObjectMapper objectMapper = new ObjectMapper();

        getLog().info("Updating project parameter for TeamCity URL: " + teamcityUrl);
        getLog().info("Project ID: " + projectId);
        getLog().info("Parameter Name: " + parameterName);
        getLog().info("New Parameter Value: " + parameterValue);

        // Create the JSON payload
        String jsonPayload;
        ParameterRequest parameterRequest = new ParameterRequest(parameterName, parameterValue);
        try {
            jsonPayload = objectMapper.writeValueAsString(parameterRequest);
        } catch (JsonProcessingException e) {
            throw new MojoExecutionException("Failed to create JSON payload", e);
        }

        // Create the request
        RequestBody body = RequestBody.create(jsonPayload, MediaType.parse("application/json"));
        Request request = new Request.Builder()
                .url(teamcityUrl + "/app/rest/projects/" + projectId + "/parameters")
                .post(body)
                .addHeader("Authorization", "Bearer " + token)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new MojoExecutionException("Failed to update parameter: " + response.message());
            }
            getLog().info("Project Parameter updated successfully: " + response.body().string());
        } catch (IOException e) {
            getLog().error("Failed to update project parameter", e);
            // This will ensure that the build fails if the parameter update fails
            // You can also choose to rethrow the exception or handle it differently
            // For now, we will rethrow it to indicate failure
            throw new MojoExecutionException("Failed to update project parameter", e);
        }
    }


}
