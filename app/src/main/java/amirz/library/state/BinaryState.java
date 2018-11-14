package amirz.library.state;

import android.support.annotation.NonNull;

import amirz.library.SparseArray;

/**
 * Facilitates switching between enabled and disabled states.
 * Callbacks will only be invoked for state transitions, not repeated states.
 */
public class BinaryState extends State<Boolean> {
    private static final Boolean DEFAULT_STATE = false;
    private final SparseArray<State.ChangeHandler<Boolean>> mHandlers = new SparseArray<>();

    /**
     * Creates a new binary state.
     */
    public BinaryState() {
        super(DEFAULT_STATE);
    }

    /**
     * Adds a transition handler to this state wrapper.
     * @param handler The handler that will receive callbacks.
     */
    public void addBinaryHandler(@NonNull ChangeHandler handler) {
        addHandler(convertHandler(handler));
    }

    /**
     * Removes a transition handler from this state wrapper.
     * @param handler The handler that will not receive callbacks anymore.
     */
    public void removeBinaryHandler(@NonNull ChangeHandler handler) {
        removeHandler(convertHandler(handler));
    }

    /**
     * Creates and cached a proxy for the state transition handler.
     * This proxy converts the callback into a call to onEnable or onDisable.
     * @param handler Binary handler.
     * @return Proxy handler.
     */
    private State.ChangeHandler<Boolean> convertHandler(@NonNull ChangeHandler handler) {
        return mHandlers.getOrAdd(handler.hashCode(), () -> (oldState, newState) -> {
            if (newState) {
                handler.onEnable();
            } else {
                handler.onDisable();
            }
        });
    }

    /**
     * Specialized change handler for boolean state transitions.
     */
    interface ChangeHandler {
        /**
         * Called when the state has changed from false to true.
         */
        void onEnable();

        /**
         * Called when the state has changed from true to false.
         */
        void onDisable();
    }

    /**
     * Direct implementation of the change handler that implicitly creates an internal binary state.
     */
    public static abstract class ChangeHandlerImpl extends AbstractChangeHandlerImpl<Boolean, Object>
            implements ChangeHandler {
        /**
         * Create new binary state change handler.
         */
        public ChangeHandlerImpl() {
            super(DEFAULT_STATE, null);
        }

        @Override
        protected State<Boolean> getStateWrapper(Boolean defaultState, Object args) {
            BinaryState state = new BinaryState();
            state.addBinaryHandler(this);
            return state;
        }

        @Override
        public void onStateChange(Boolean oldState, Boolean newState) {
            // Never used because of the proxy in the constructor of BinaryState.
        }

        /**
         * Sets the state to true.
         */
        public void enable() {
            setState(true);
        }

        /**
         * Sets the state to false.
         */
        public void disable() {
            setState(false);
        }
    }
}
