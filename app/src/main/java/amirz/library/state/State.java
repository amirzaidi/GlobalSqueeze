package amirz.library.state;

import android.support.annotation.NonNull;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * Facilitates switching between states
 * Callbacks will only be invoked for state transitions, not repeated states.
 */
public class State<T> {
    private final Set<ChangeHandler<T>> mHandlers = new HashSet<>();
    private T mState;

    /**
     * Create a new state transition handler.
     * No callback is invoked during creation, so the default state should be chosen to work without any callbacks.
     * @param defaultState The default state.
     */
    public State(T defaultState) {
        mState = defaultState;
    }

    /**
     * Adds a transition handler to this state wrapper.
     * @param handler The handler that will receive callbacks.
     */
    public void addHandler(@NonNull ChangeHandler<T> handler) {
        mHandlers.add(handler);
    }

    /**
     * Removes a transition handler from this state wrapper.
     * @param handler The handler that will not receive callbacks anymore.
     */
    public void removeHandler(@NonNull ChangeHandler<T> handler) {
        mHandlers.remove(handler);
    }

    /**
     * Sets the state to the parameter.
     * If this is not equal to the current state, a transition callback is triggered.
     * @param state The new state.
     */
    public void setState(T state) {
        if (!Objects.equals(state, mState)) {
            // Send callback to all handlers.
            for (ChangeHandler<T> handler : mHandlers) {
                handler.onStateChange(mState, state);
            }

            // Delayed state assignment to reduce variable bloat.
            // There is no visible getState() method, so this is valid.
            mState = state;
        }
    }

    /**
     * State transition handler.
     * @param <T> The type of the state.
     */
    interface ChangeHandler<T> {
        /**
         * Called when the state has changed.
         * @param oldState The old state.
         * @param newState The new state.
         */
        void onStateChange(T oldState, T newState);
    }

    /**
     * Direct implementation of the change handler that implicitly creates an internal state.
     * @param <T> The type of state.
     */
    public static abstract class ChangeHandlerImpl<T> extends AbstractChangeHandlerImpl<T, Object> {
        public ChangeHandlerImpl(T defaultState) {
            super(defaultState);
        }
    }

    /**
     * Direct implementation of the change handler that implicitly creates an internal state.
     * @param <T> The type of state.
     * @param <Args> The argument supplied to the factory method.
     */
    protected static abstract class AbstractChangeHandlerImpl<T, Args> implements ChangeHandler<T> {
        private final State<T> mState;

        /**
         * Creates a new change handler with a default state.
         * @param defaultState The default state.
         */
        public AbstractChangeHandlerImpl(T defaultState) {
            mState = new State<>(defaultState);

            // Unbinding the handler is not necessary, since the State wrapper is private.
            // It will be garbage collected together with this ChangeHandlerImpl.
            mState.addHandler(this);
        }

        /**
         * Creates a new change handler with a default state.
         * @param defaultState The default state.
         * @param args The arguments passed to the factory method.
         */
        protected AbstractChangeHandlerImpl(T defaultState, Args args) {
            mState = getStateWrapper(defaultState, args);
        }

        /**
         * Creates the state wrapper. This method is executed exactly once, during creation.
         * @param defaultState The default state.
         * @param args Arguments that can be used for instantiation.
         * @return The state wrapper used as the internal data structure.
         */
        protected State<T> getStateWrapper(T defaultState, Args args) {
            return null;
        }

        /**
         * Sets the state to a specified value.
         * @param state The new state.
         */
        public void setState(T state) {
            mState.setState(state);
        }
    }
}
