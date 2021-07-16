package net.openhft.chronicle.wire.utils;

import net.openhft.chronicle.wire.WireTestCommon;
import org.junit.Assert;
import org.junit.Test;

public class JavaSourceCodeFormatterTest extends WireTestCommon {

    @Test
    public void testAppend() {
        Assert.assertEquals("" +
                        "public Appendable append(final CharSequence csq) {\n" +
                        "    return sb.append(replaceNewLine(csq, 0, csq.length() - 1));\n" +
                        "}\n",
                new JavaSourceCodeFormatter()
                        .append("public Appendable append(final CharSequence csq) {\n")
                        .append("return sb.append(replaceNewLine(csq, 0, csq.length() - 1));\n")
                        .append('}').append('\n')
                        .toString());
    }
}