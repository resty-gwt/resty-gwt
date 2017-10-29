package org.fusesource.restygwt.client.basic;

public class AttributeDTO {

    public Long publicId;
    private Long privateId;
    private String path;

    public Long getPrivateId() {
        return privateId;
    }

    public void setPrivateId(Long privateId) {
        this.privateId = privateId;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}