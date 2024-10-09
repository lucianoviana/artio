package uk.co.real_logic.artio.system_tests;

import java.util.HashMap;
import java.util.Map;

import uk.co.real_logic.artio.decoder.BusinessMessageRejectDecoder;
import uk.co.real_logic.artio.decoder.ExecutionReportDecoder;
import uk.co.real_logic.artio.decoder.HeartbeatDecoder;
import uk.co.real_logic.artio.decoder.LogonDecoder;
import uk.co.real_logic.artio.decoder.LogoutDecoder;
import uk.co.real_logic.artio.decoder.MessageDecoder;
import uk.co.real_logic.artio.decoder.NewOrderSingleDecoder;
import uk.co.real_logic.artio.decoder.RejectDecoder;
import uk.co.real_logic.artio.decoder.ResendRequestDecoder;
import uk.co.real_logic.artio.decoder.SequenceResetDecoder;
import uk.co.real_logic.artio.decoder.TestRequestDecoder;

public class MsgTypeByDecoderMap
{
    static Map<Class<? extends MessageDecoder>, Long> map = new HashMap<>()
    {
        {
            put(LogonDecoder.class, LogonDecoder.MESSAGE_TYPE);
            put(LogoutDecoder.class, LogoutDecoder.MESSAGE_TYPE);
            put(HeartbeatDecoder.class, HeartbeatDecoder.MESSAGE_TYPE);
            put(ResendRequestDecoder.class, ResendRequestDecoder.MESSAGE_TYPE);
            put(SequenceResetDecoder.class, SequenceResetDecoder.MESSAGE_TYPE);
            put(RejectDecoder.class, RejectDecoder.MESSAGE_TYPE);
            put(BusinessMessageRejectDecoder.class, BusinessMessageRejectDecoder.MESSAGE_TYPE);
            put(NewOrderSingleDecoder.class, NewOrderSingleDecoder.MESSAGE_TYPE);
            put(TestRequestDecoder.class, TestRequestDecoder.MESSAGE_TYPE);
            put(ExecutionReportDecoder.class, ExecutionReportDecoder.MESSAGE_TYPE);
        }
    };

    public static long getMessageTypeOf(final MessageDecoder decoder)
    {
        return map.get(decoder.getClass());
    }
}
