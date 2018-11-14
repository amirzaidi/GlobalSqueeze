package amirz.library.state;

/**
 * Facilitates switching between multiple enumerated states.
 * Callbacks will only be invoked for state transitions, not repeated states.
 */
public class EnumState extends State<Integer> {
    private static final Integer DEFAULT_STATE = 0;
    private final int mMax;

    /**
     * Creates a new numerical state.
     * @param max The maximum state value.
     */
    public EnumState(int max) {
        super(DEFAULT_STATE);
        mMax = max;
    }

    @Override
    public void setState(Integer state) {
        if (state > mMax) {
            throw new RuntimeException("Invalid EnumState: " + state);
        }
        super.setState(state);
    }

    /**
     * Direct implementation of the change handler that implicitly creates an internal EnumState;
     */
    public static abstract class ChangeHandlerImpl extends AbstractChangeHandlerImpl<Integer, Integer> {
        /**
         * Create new numerical state change handler.
         * @param max The maximum state value.
         */
        public ChangeHandlerImpl(int max) {
            super(DEFAULT_STATE, max);
        }

        @Override
        protected State<Integer> getStateWrapper(Integer defaultState, Integer max) {
            EnumState state = new EnumState(max);
            state.addHandler(this);
            return state;
        }
    }
}
