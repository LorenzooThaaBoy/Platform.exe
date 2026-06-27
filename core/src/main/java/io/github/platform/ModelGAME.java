package io.github.platform;

//whole game state model
public class ModelGAME {
    public enum State {
        PLAYING, SHOP, PAUSED, GAME_OVER
    }

    private State state = State.PLAYING;
    private float transitionTimer;

    //state helpers for view/controller
    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }

    public boolean isPlaying() {
        return state == State.PLAYING;
    }

    public boolean isShop() {
        return state == State.SHOP;
    }

    public boolean isPaused() {
        return state == State.PAUSED;
    }

    public boolean isGameOver() {
        return state == State.GAME_OVER;
    }

    public float getTransitionTimer() {
        return transitionTimer;
    }

    public void startTransition(float duration) {
        //fade overlay timer
        transitionTimer = duration;
    }

    public void updateTransition(float delta) {
        transitionTimer = Math.max(0f, transitionTimer - delta);
    }
}
