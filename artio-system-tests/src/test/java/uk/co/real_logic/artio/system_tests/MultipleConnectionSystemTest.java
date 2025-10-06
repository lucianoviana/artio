package uk.co.real_logic.artio.system_tests;

import org.junit.jupiter.api.AfterEach;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import uk.co.real_logic.artio.Reply;
import uk.co.real_logic.artio.library.LibraryConfiguration;
import uk.co.real_logic.artio.session.Session;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static uk.co.real_logic.artio.TestFixtures.cleanupMediaDriver;
import static uk.co.real_logic.artio.TestFixtures.launchMediaDriver;
import static uk.co.real_logic.artio.Timing.assertEventuallyTrue;
import static uk.co.real_logic.artio.dictionary.generation.Exceptions.closeAll;
import static uk.co.real_logic.artio.system_tests.SystemTestUtil.*;

public class MultipleConnectionSystemTest extends AbstractGatewayToGatewaySystemTest
{
    @BeforeEach
    public void launch()
    {
        deleteAcceptorLogs();

        mediaDriver = launchMediaDriver();

        launchAcceptingEngine();
        initiatingEngine = launchInitiatingEngine(libraryAeronPort, nanoClock);
        initiatingLibrary = newInitiatingLibrary(libraryAeronPort, initiatingHandler, nanoClock);

        final LibraryConfiguration acceptingLibraryConfig = acceptingLibraryConfig(acceptingHandler, nanoClock);
        acceptingLibrary = connect(acceptingLibraryConfig);

        testSystem = new TestSystem(initiatingLibrary, acceptingLibrary);

        connectSessions();
    }

    @Test
    @Timeout(TEST_TIMEOUT_IN_MS)
    public void shouldSupportConnectionAfterAuthenticationFailure()
    {
        // on first session
        messagesCanBeExchanged();

        failedAuthenticationWithInvalidCompId();

        // Complete a second connection
        final Reply<Session> successfulReply = initiate(initiatingLibrary, port, INITIATOR_ID2, ACCEPTOR_ID);
        completeConnectInitiatingSession(successfulReply);

        messagesCanBeExchanged();
    }

    @Test
    @Timeout(TEST_TIMEOUT_IN_MS)
    public void shouldSupportRepeatedConnectionOfTheSameSessionId()
    {
        acquireAcceptingSession();

        // on first session
        messagesCanBeExchanged();

        failedAuthenticationWithInvalidCompId();

        acceptingSession.logoutAndDisconnect();

        //initiatingSession.logoutAndDisconnect();
        assertSessionDisconnected(initiatingSession);

        assertEventuallyTrue("libraries receive disconnect messages",
            () ->
            {
                testSystem.poll();
                assertNotSession(initiatingHandler, initiatingSession);
            });

        connectSessions();
        messagesCanBeExchanged();
    }

    private void failedAuthenticationWithInvalidCompId()
    {
        final Reply<Session> failureReply =
            initiate(initiatingLibrary, port, "invalidSenderCompId", ACCEPTOR_ID);
        testSystem.awaitReply(failureReply);

        assertEquals(Reply.State.ERRORED, failureReply.state());
        assertEquals("UNABLE_TO_LOGON: Disconnected before session active", failureReply.error().getMessage());
    }

    @AfterEach
    public void shutdown()
    {
        closeAll(
            initiatingLibrary,
            acceptingLibrary,
            initiatingEngine,
            acceptingEngine,
            () -> cleanupMediaDriver(mediaDriver));
    }
}
