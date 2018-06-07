package org.mycore.frontend.servlets.tileserver;

import java.nio.file.Path;

public interface MCRTileFileProvider {
    public Path getTileFile(String derivate, String image);
}