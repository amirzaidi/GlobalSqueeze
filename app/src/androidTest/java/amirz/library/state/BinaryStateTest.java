package amirz.library.state;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;

public class BinaryStateTest {
    private BinaryState mState;
    private BinaryState.ChangeHandler mHandler;

    @Before
    public void create() {
        mState = new BinaryState();
        mHandler = mock(BinaryState.ChangeHandler.class);
        mState.addBinaryHandler(mHandler);
    }

    @After
    public void destroy() {
        mState.removeBinaryHandler(mHandler);
    }


    @Test
    public void testSetTrue() {
        mState.setState(true);
        verify(mHandler).onEnable();
    }


    @Test
    public void testSetFalse() {
        mState.setState(false);
        verify(mHandler).onEnable();
    }

    @Test
    public void testDuplicateTransition() {
        mState.setState(true);
        mState.setState(true);

        verify(mHandler).onEnable();
    }

    @Test
    public void testHandlerDuplicateAddition() {
        mState.addBinaryHandler(mHandler);
        mState.setState(true);

        verify(mHandler).onEnable();
    }

    @Test
    public void testHandlerRemoval() {
        mState.removeBinaryHandler(mHandler);
        mState.setState(true);

        verifyZeroInteractions(mHandler);
    }

    @Test
    public void testHandlerImpl() {
        boolean[] state = { false };
        BinaryState.ChangeHandlerImpl handler = new BinaryState.ChangeHandlerImpl() {
            @Override
            public void onEnable() {
                state[0] = true;
            }

            @Override
            public void onDisable() {
            }
        };

        handler.enable();
        assertTrue(state[0]);
    }
}
