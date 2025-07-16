package com.mockholm.tools.mojos;

import java.io.File;
import java.io.IOException;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mockholm.tools.models.BuildRequest;
import com.mockholm.tools.models.ChainRequest;

import okhttp3.*;

@Mojo(name = "run-build-chain", defaultPhase = LifecyclePhase.NONE)
public class RunBuildChain extends AbstractMojo {

    @Parameter(property = "teamcity.url", required = true)
    private String teamcityUrl;

    @Parameter(property = "teamcity.token", required = true)
    private String token;

    @Parameter(property = "chain.file", required = true)
    private File chainFile;

    public void execute() throws MojoExecutionException {
        ObjectMapper mapper = new ObjectMapper();
        OkHttpClient client = new OkHttpClient();

        try {
            ChainRequest chain = mapper.readValue(chainFile, ChainRequest.class);
            for (ChainRequest.BuildEntry build : chain.getBuilds()) {
                triggerBuildAndWait(client, build.getBuildId(), build.getName());
            }
        } catch (IOException | InterruptedException e) {
            throw new MojoExecutionException("Failed to execute build chain", e);
        }
    }

    private void triggerBuildAndWait(OkHttpClient client, String buildTypeId, String buildName)
            throws IOException, InterruptedException, MojoExecutionException {

        getLog().info("Triggering build: " + buildName + " (" + buildTypeId + ")");

        // Trigger build
        String buildId = triggerBuild(client, buildTypeId);

        // Poll for status
        String status;
        do {
            Thread.sleep(5000); // Wait 5 seconds between polls
            status = getBuildStatus(client, buildId);
            getLog().info("Build status: " + status);
        } while (!status.equals("SUCCESS") && !status.equals("FAILURE"));

        if (status.equals("FAILURE")) {
            throw new MojoExecutionException("Build failed: " + buildName);
        }

        getLog().info("Build completed successfully: " + buildName);
    }

    private String triggerBuild(OkHttpClient client, String buildTypeId) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        BuildRequest request = new BuildRequest(buildTypeId);
        String json = mapper.writeValueAsString(request);

        RequestBody body = RequestBody.create(json, MediaType.parse("application/json"));
        Request httpRequest = new Request.Builder()
                .url(teamcityUrl + "/app/rest/buildQueue")
                .post(body)
                .addHeader("Authorization", "Bearer " + token)
                .addHeader("Content-Type", "application/json")
                .build();

        try (Response response = client.newCall(httpRequest).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Failed to trigger build: " + response.message());
            }

            String responseBody = response.body().string();
            JsonNode node = mapper.readTree(responseBody);
            return node.get("id").asText(); // Extract build ID
        }
    }

    private String getBuildStatus(OkHttpClient client, String buildId) throws IOException {
        Request request = new Request.Builder()
                .url(teamcityUrl + "/app/rest/builds/id:" + buildId)
                .get()
                .addHeader("Authorization", "Bearer " + token)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Failed to get build status: " + response.message());
            }

            ObjectMapper mapper = new ObjectMapper();
            JsonNode node = mapper.readTree(response.body().string());
            return node.get("status").asText(); // "SUCCESS", "FAILURE", etc.
        }
    }
}

