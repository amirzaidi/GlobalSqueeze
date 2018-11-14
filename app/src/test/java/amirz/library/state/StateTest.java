package amirz.library.state;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;

public class StateTest {
    private enum TestState {
        Idle,
        Running,
        Completed
    }

    private State<TestState> mState;
    private State.ChangeHandler<TestState> mHandler;

    @SuppressWarnings("unchecked")
    @Before
    public void create() {
        mState = new State<>(TestState.Idle);
        mHandler = mock(State.ChangeHandler.class);
        mState.addHandler(mHandler);
    }

    @After
    public void destroy() {
        mState.removeHandler(mHandler);
    }

    @Test
    public void testEnterRunning() {
        mState.setState(TestState.Running);

        verify(mHandler).onStateChange(TestState.Idle, TestState.Running);
    }

    @Test
    public void testDuplicateTransition() {
        mState.setState(TestState.Running);
        mState.setState(TestState.Running);

        verify(mHandler).onStateChange(TestState.Idle, TestState.Running);
    }

    @Test
    public void testHandlerDuplicateAddition() {
        mState.addHandler(mHandler);
        mState.setState(TestState.Completed);

        verify(mHandler).onStateChange(TestState.Idle, TestState.Completed);
    }

    @Test
    public void testHandlerRemoval() {
        mState.removeHandler(mHandler);
        mState.setState(TestState.Completed);

        verifyZeroInteractions(mHandler);
    }

    @Test
    public void testHandlerImpl() {
        TestState[] state = { TestState.Idle };
        State.ChangeHandlerImpl<TestState> handler = new State.ChangeHandlerImpl<TestState>(state[0]) {
            @Override
            public void onStateChange(TestState oldState, TestState newState) {
                state[0] = newState;
            }
        };

        handler.setState(TestState.Completed);
        assertEquals(state[0], TestState.Completed);
    }
}
