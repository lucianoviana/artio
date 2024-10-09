/*
 * Copyright 2022 Monotonic Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package uk.co.real_logic.artio.system_tests;

import org.agrona.collections.IntArrayList;
import uk.co.real_logic.artio.Constants;
import uk.co.real_logic.artio.builder.RejectEncoder;
import uk.co.real_logic.artio.decoder.AbstractResendRequestDecoder;
import uk.co.real_logic.artio.session.ResendRequestController;
import uk.co.real_logic.artio.session.ResendRequestResponse;
import uk.co.real_logic.artio.session.Session;

import static org.junit.Assert.assertNotNull;
import static uk.co.real_logic.artio.dictionary.SessionConstants.RESEND_REQUEST_MESSAGE_TYPE_CHARS;
import static uk.co.real_logic.artio.fields.RejectReason.OTHER;
import static uk.co.real_logic.artio.system_tests.SystemTestUtil.LOCK;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class FakeResendRequestController implements ResendRequestController
{
    public static final String CUSTOM_MESSAGE = "custom message";
    private AtomicBoolean resend = new AtomicBoolean(true);

    private AtomicInteger callCount = new AtomicInteger(0);
    private IntArrayList seenReplaysInFlight = new IntArrayList();
    private AtomicBoolean customResend = new AtomicBoolean(false);
    private AtomicInteger maxResends = new AtomicInteger(Integer.MAX_VALUE);

    public void onResend(
        final Session session,
        final AbstractResendRequestDecoder resendRequest,
        final int correctedEndSeqNo,
        final ResendRequestResponse response)
    {
        assertNotNull(resendRequest);

        if (callCount.incrementAndGet() > maxResends.get())
        {
            resend.set(false);
        }

        if (resend.get())
        {
            response.resend();
        }
        else if (customResend.get())
        {
            final RejectEncoder rejectEncoder = new RejectEncoder();
            rejectEncoder.refTagID(Constants.BEGIN_SEQ_NO);
            rejectEncoder.refMsgType(RESEND_REQUEST_MESSAGE_TYPE_CHARS);
            rejectEncoder.refSeqNum(resendRequest.header().msgSeqNum());
            rejectEncoder.sessionRejectReason(OTHER.representation());
            rejectEncoder.text(CUSTOM_MESSAGE);
            response.reject(rejectEncoder);
        }
        else
        {
            response.reject(Constants.BEGIN_SEQ_NO);
        }
    }

    public void onResendComplete(final Session session, final int remainingReplaysInFlight)
    {
        assertNotNull(session);
        seenReplaysInFlight.add(remainingReplaysInFlight);
    }

    public void resend(final boolean resend)
    {
        this.resend.set(resend);
    }

    public void maxResends(final int maxResends)
    {
        this.maxResends.set(maxResends);
    }

    public boolean wasCalled()
    {
        return callCount.get() > 0;
    }

    public int callCount()
    {
        return callCount.get();
    }

    public int completeCount()
    {
        synchronized (LOCK)
        {

            return seenReplaysInFlight.size();
        }
    }

    public IntArrayList seenReplaysInFlight()
    {
        synchronized (LOCK)
        {

            return seenReplaysInFlight;
        }
    }

    public void customResend(final boolean customResend)
    {
        this.customResend.set(customResend);
    }
}
