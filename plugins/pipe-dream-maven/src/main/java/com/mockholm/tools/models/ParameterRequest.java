package com.mockholm.tools.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * Represents a request to update a parameter in the build configuration.
 */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class ParameterRequest {
    // name of the parameter to update
    @JsonProperty("name")
    private String name;
    // new value for the parameter
    @JsonProperty("value")
    private String value;

    /**
     * Constructor for ParameterRequest.
     *
     * @param name  the name of the parameter to update
     * @param value the new value for the parameter
     */
    public ParameterRequest(String name, String value) {
        this.name = name;
        this.value = value;
    }


    /**
     * Gets the name of the parameter.
     * @return the name of the parameter
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name of the parameter.
     * @param name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets the value of the parameter.
     * @return the value of the parameter
     */
    public String getValue() {
        return value;
    }

    /**
     * Sets the value of the parameter.
     * @param value the new value for the parameter
     */
    public void setValue(String value) {
        this.value = value;
    }
}
