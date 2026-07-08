package com.yourname.baritone.api;

public interface IFollowBehavior {
    void followPlayer(String name);
    void followEntity(String entityName);
    void stop();
    void pause();
    void resume();
}
