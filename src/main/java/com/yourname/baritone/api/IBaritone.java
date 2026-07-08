package com.yourname.baritone.api;

import com.yourname.baritone.settings.Settings;

public interface IBaritone {
    IPathingBehavior getPathingBehavior();
    IMineBehavior getMineBehavior();
    IBuilderBehavior getBuilderBehavior();
    IFarmBehavior getFarmBehavior();
    IFollowBehavior getFollowBehavior();
    Settings getSettings();
    void stopAll();
    void pause();
    void resume();
}
