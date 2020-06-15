package com.tencent.tars.protocol.parse;


import com.tencent.tars.protocol.parse.ast.TarsInclude;
import com.tencent.tars.protocol.parse.ast.TarsNamespace;

import java.util.ArrayList;
import java.util.List;

public class TarsContent {

    private List<TarsNamespace> tarsNamespaces;

    private List<TarsInclude> tarsIncludes;

    public List<TarsNamespace> getNamespaces() {
        return new ArrayList<>(tarsNamespaces);
    }

    public void setNamespaces(List<TarsNamespace> tarsNamespaces) {
        this.tarsNamespaces = tarsNamespaces;
    }

    public List<TarsInclude> getIncludes() {
        return new ArrayList<>(tarsIncludes);
    }

    public void setIncludes(List<TarsInclude> tarsIncludes) {
        this.tarsIncludes = tarsIncludes;
    }
}
