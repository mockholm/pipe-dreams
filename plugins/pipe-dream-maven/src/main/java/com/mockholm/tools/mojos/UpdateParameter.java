package com.mockholm.tools.mojos;

import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.*;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugin.MojoExecutionException;

import java.io.IOException;
import com.fasterxml.jackson.core.JsonProcessingException;

import com.mockholm.tools.models.ParameterRequest;

/**
 * Update a parameter in the build configuration
 */
@Mojo(name = "update-parameter", defaultPhase = LifecyclePhase.NONE)
public class UpdateParameter extends AbstractMojo {

    @Parameter(property = "teamcity.url", required = true)
    private String teamcityUrl; // URL of the TeamCity server       
    @Parameter(property = "teamcity.buildTypeId", required = true)
    private String buildTypeId; // ID of the build configuration
    @Parameter(property = "teamcity.token", required = true)
    private String token; // Authentication token for TeamCity      

    @Parameter(property = "teamcity.parameterName", required = true)
    private String parameterName; // Name of the parameter to update
    @Parameter(property = "teamcity.parameterValue", required = true)
    private String parameterValue; // New value for the parameter

    public void execute() throws MojoExecutionException {
        OkHttpClient client = new OkHttpClient();
        ObjectMapper objectMapper = new ObjectMapper();

        getLog().info("Updating parameter for TeamCity URL: " + teamcityUrl);
        getLog().info("Build Type ID: " + buildTypeId);
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
                .url(teamcityUrl + "/app/rest/buildTypes/" + buildTypeId + "/parameters")
                .post(body)
                .addHeader("Authorization", "Bearer " + token)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new MojoExecutionException("Failed to update parameter: " + response.message());
            }
            if (response.body() != null) {
                getLog().info("Parameter updated successfully: " + response.body().string());
            } else {
                getLog().info("Parameter updated successfully, no response body.");
            }
        } catch (IOException e) {
            throw new MojoExecutionException("Failed to update parameter", e);
        }
    }
}   
