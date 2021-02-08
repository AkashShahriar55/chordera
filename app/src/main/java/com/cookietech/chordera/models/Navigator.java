package com.cookietech.chordera.models;

import android.os.Bundle;

public class Navigator {
    String navigatorTag = "none";
    int ContainerId = 0;
    Bundle bundle;

    public Navigator(String navigatorTag, int containerId,Bundle args) {
        this.navigatorTag = navigatorTag;
        ContainerId = containerId;
        this.bundle = args;
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

    public Bundle getBundle() {
        return bundle;
    }

    public void setBundle(Bundle bundle) {
        this.bundle = bundle;
    }
}
