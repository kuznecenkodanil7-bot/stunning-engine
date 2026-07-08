package com.yourname.baritone.api;

import java.io.File;

public interface IBuilderBehavior {
    void build(File schematicFile);
    void clear();
    void stop();
    void pause();
    void resume();
}
