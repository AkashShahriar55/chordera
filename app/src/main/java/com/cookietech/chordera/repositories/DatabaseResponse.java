package com.cookietech.chordera.repositories;

public class DatabaseResponse {
    public enum Response{
        Fetching,
        Fetched,
        Error,
        No_internet,
        Invalid_data,
        Stored,
        Storing,
        Already_exist,
        Updating,
        Updated,
        LastSongFetched
    }
    String identifier;
    Exception errorException;
    Response response;

    public DatabaseResponse(String identifier, Exception errorException, Response response) {
        this.identifier = identifier;
        this.errorException = errorException;
        this.response = response;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public Exception getErrorException() {
        return errorException;
    }

    public void setErrorException(Exception errorException) {
        this.errorException = errorException;
    }

    public Response getResponse() {
        return response;
    }

    public void setResponse(Response response) {
        this.response = response;
    }
}
