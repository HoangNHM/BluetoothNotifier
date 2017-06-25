package com.mobile.ht.bluetoothnotifier.heart;

public abstract class PulseManager {
    private enum State {
        NORMAL,
        HIGH,
        ALERT
    }

    private State currentState = State.NORMAL;

    private State pulseToState(int pulse) {
        State state;
        if (pulse < 90) {
            state = State.NORMAL;
        } else if (pulse >= 90 && pulse <= 100) {
            state = State.HIGH;
        } else {
            state = State.ALERT;
        }
        return state;
    }

    public void changeState(int pulse) {
        State state = pulseToState(pulse);
        if (currentState != state) {
            currentState = state;
            switch (currentState) {
                case NORMAL:
                    normal();
                    break;
                case HIGH:
                    hight();
                    break;
                case ALERT:
                    alert();
                    break;
                default:
            }
        }
    }

    protected abstract void alert();

    protected abstract void hight();

    protected abstract void normal();

}
