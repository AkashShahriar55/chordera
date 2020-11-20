package com.cookietech.chordera.models;

public class Navigator {
    String navigatorTag = "none";
    int ContainerId = 0;

    public Navigator(String navigatorTag, int containerId) {
        this.navigatorTag = navigatorTag;
        ContainerId = containerId;
    }

    public String getNavigatorTag() {
        return navigatorTag;
    }

    public void setNavigatorTag(String navigatorTag) {
        this.navigatorTag = navigatorTag;
    }

    public int getContainerId() {
        return ContainerId;
    }

    public void setContainerId(int containerId) {
        ContainerId = containerId;
    }
}
