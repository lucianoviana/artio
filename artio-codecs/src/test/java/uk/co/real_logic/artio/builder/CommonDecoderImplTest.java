/*
 * Copyright 2015-2025 Real Logic Limited.
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
package uk.co.real_logic.artio.builder;

import java.util.stream.Stream;
import uk.co.real_logic.artio.fields.DecimalFloat;
import uk.co.real_logic.artio.util.MutableAsciiBuffer;
import uk.co.real_logic.artio.util.float_parsing.CharReader;
import uk.co.real_logic.artio.util.float_parsing.DecimalFloatOverflowHandler;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

public class CommonDecoderImplTest
{

    public static Stream<Arguments> decimalFloatCodecData()
    {
        return Stream.of(
          Arguments.of("922337203.6854775807", 19, 9, 9223372036854775807L, 10),
          Arguments.of("922337203.6854775808", 19, 9, 999, 1),
          Arguments.of("9.223372036854775808", 19, 1, 999, 1),
          Arguments.of("922337203685477580.8", 19, 18, 999, 1),
          Arguments.of("0.009223372036854775808", 22, 1, 999, 1)
        );
    }

    static class SimpleDecoderImpl extends CommonDecoderImpl
    {
    }




    @ParameterizedTest(name = "{index}: {0} => {1},{2},{3},{4}")
    @MethodSource("decimalFloatCodecData")
    void testGetFloat(final String valueWithOverflow,
        final int positionOfOverflow,
        final int positionOfDecimalPoint,
        final long finalValue,
        final int finalScale)
    {
        final SimpleDecoderImpl decoder = new SimpleDecoderImpl();
        final DecimalFloat value = decoder.getFloat(
            new MutableAsciiBuffer(valueWithOverflow.getBytes(), 0, valueWithOverflow.length()),
            new DecimalFloat(),
            0,
            valueWithOverflow.length(),
            21,
            true,
            new DecimalFloatOverflowHandler()
            {
                @Override
                public <Data> DecimalFloat handleOverflow(
                    final CharReader<Data> charReader,
                    final Data data,
                    final int offset,
                    final int length,
                    final int posOverflow,
                    final int posDecimal,
                    final int tagId)
                {

                    assertEquals(valueWithOverflow, charReader.asString(data, offset, length));
                    assertEquals(positionOfOverflow, posOverflow);
                    assertEquals(positionOfDecimalPoint, posDecimal);
                    assertEquals(21, tagId);
                    return new DecimalFloat(999L, 1);
                }
            });
    }
}