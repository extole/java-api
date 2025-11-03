package com.extole.encoder;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class EncoderTest {
    private static final Encoder ENCODER = Encoder.getInstance();

    @Test
    void testSafeHtml() {
        String maliciousCode = "<p>I love cookie & sweats, \" '</p><script>alert('1')</script>";
        String htmlBlock = String.format("<div>%s</div>",
            ENCODER.safeHtml(maliciousCode));

        assertThat(htmlBlock).isEqualTo("<div>&lt;p&gt;I love cookie &amp; sweats, &#34; &#39;&lt;/p&gt;</div>");
    }

    @Test
    void testSafeHtmlContent() {
        String maliciousCode = "<p>I love cookie & sweats, \" '</p><script>alert('1')</script>";
        String htmlBlock = String.format("<div>%s</div>",
            ENCODER.safeHtmlContent(maliciousCode));

        assertThat(htmlBlock).isEqualTo(
            "<div>&lt;p&gt;I love cookie &amp; sweats, \" '&lt;/p&gt;&lt;script&gt;alert('1')&lt;/script&gt;</div>");
    }

    @Test
    void testSafeHtmlAttribute() {
        String maliciousCode = "<script>alert((2 === 1+1 && true) ? 'h1' : \"h2\");</script>";
        String htmlBlock = String.format("<div>%s</div>",
            ENCODER.safeHtmlAttribute(maliciousCode));

        assertThat(htmlBlock).isEqualTo(
            "<div>&lt;script>alert((2 === 1+1 &amp;&amp; true) ? &#39;h1&#39; : &#34;h2&#34;);&lt;/script></div>");
    }

    @Test
    void testSafeHtmlUnquotedAttribute() {
        String maliciousCode = "<html>\n" + "<body>\n" + "<p>\n" + "U+0009 (horizontal tab): &#9;<br>\n" +
            "U+000A (line feed): &#10;<br>\n" + "U+000C (form feed): &#12;<br>\n" +
            "U+000D (carriage return): &#13;<br>\n" + "U+0020 (space): &#32;<br>\n" + "&amp; (ampersand): &amp;<br>\n" +
            "&lt; (less than): &lt;<br>\n" + "&gt; (greater than): &gt;<br>\n" + "&quot; (double quote): &#34;<br>\n" +
            "&apos; (single quote): &#39;<br>\n" + "/ (forward slash): &#47;<br>\n" + "= (equals): &#61;<br>\n" +
            "` (backtick): &#96;<br>\n" + "U+0085 (next line): &#133;<br>\n" +
            "U+2028 (line separator): &#8232;<br>\n" + "U+2029 (paragraph separator): &#8233;<br>\n" + "</p>\n" +
            "</body>\n" + "</html>\n";
        String htmlBlock = String.format("<div>%s</div>",
            ENCODER.safeHtmlUnquotedAttribute(maliciousCode));

        assertThat(htmlBlock).isEqualTo(
            "<div>&lt;html&gt;&#10;&lt;body&gt;&#10;&lt;p&gt;&#10;U+0009&#32;" +
                "(horizontal&#32;tab):&#32;&amp;#9;&lt;br&gt;&#10;U+000A&#32;" +
                "(line&#32;feed):&#32;&amp;#10;&lt;br&gt;&#10;U+000C&#32;(form&#32;feed)" +
                ":&#32;&amp;#12;&lt;br&gt;&#10;U+000D&#32;(carriage&#32;return):&#32;" +
                "&amp;#13;&lt;br&gt;&#10;U+0020&#32;(space):&#32;&amp;#32;&lt;br&gt;&#10;" +
                "&amp;amp;&#32;(ampersand):&#32;&amp;amp;&lt;br&gt;&#10;&amp;lt;&#32;(less&#32;than)" +
                ":&#32;&amp;lt;&lt;br&gt;&#10;&amp;gt;&#32;(greater&#32;than):&#32;&amp;gt;&lt;br&gt;" +
                "&#10;&amp;quot;&#32;(double&#32;quote):&#32;&amp;#34;&lt;br&gt;&#10;&amp;apos;&#32;" +
                "(single&#32;quote):&#32;&amp;#39;&lt;br&gt;&#10;&#47;&#32;(forward&#32;slash):&#32;&amp;" +
                "#47;&lt;br&gt;&#10;&#61;&#32;(equals):&#32;&amp;#61;&lt;br&gt;&#10;&#96;&#32;(backtick):" +
                "&#32;&amp;#96;&lt;br&gt;&#10;U+0085&#32;(next&#32;line):&#32;&amp;#133;&lt;br&gt;&#10;U+2028&#32;" +
                "(line&#32;separator):&#32;&amp;#8232;&lt;br&gt;&#10;U+2029&#32;(paragraph&#32;separator):&#32;&amp;" +
                "#8233;&lt;br&gt;&#10;&lt;&#47;p&gt;&#10;&lt;&#47;body&gt;&#10;&lt;&#47;html&gt;&#10;</div>");
    }

    @Test
    void testSafeJavaScriptAttribute() {
        String maliciousCode = "' + (function() { alert('hacked'); return '';})() + '";
        String htmlBlock = String.format("<button onclick=\"alert('%s');\">",
            ENCODER.safeJavaScriptAttribute(maliciousCode));

        assertThat(htmlBlock).isEqualTo(
            "<button onclick=\"alert('\\x27 + (function() { alert(\\x27hacked\\x27); return \\x27\\x27;})() + \\x27')" +
                ";\">");
    }

    @Test
    void testSafeJavaScriptBlock() {
        String maliciousCode = "' + (function() { alert('hacked'); return '';})() + '";
        String htmlBlock = String.format("<button onclick=\"alert('%s');\">",
            ENCODER.safeJavaScriptBlock(maliciousCode));

        assertThat(htmlBlock).isEqualTo(
            "<button onclick=\"alert('\\' + (function() { alert(\\'hacked\\'); return \\'\\';})() + \\'');\">");
    }

    @Test
    void testSafeJavaScript() {
        String maliciousCode = "' + (function() { alert('hacked'); return '';})() + '";
        String htmlBlock = String.format("<button onclick=\"alert('%s');\">",
            ENCODER.safeJavaScript(maliciousCode));

        assertThat(htmlBlock).isEqualTo(
            "<button onclick=\"" +
                "alert('\\x27 + (function() { alert(\\x27hacked\\x27); return \\x27\\x27;})() + \\x27');\">");
    }

    @Test
    void testSafeUriComponent() {
        String maliciousCode = "' + (function() { alert('hacked'); return '';})() + '";
        String htmlBlock = String.format("<button onclick=\"alert('%s');\">",
            ENCODER.safeUriComponent(maliciousCode));

        assertThat(htmlBlock).isEqualTo(
            "<button onclick=\"alert('%27%20%2B%20%28function%28%29%20%7B%20alert%28%27hacked%27%29%3B%20return%20%27" +
                "%27%3B%7D%29%28%29%20%2B%20%27');\">");
    }

    @Test
    void testSafeCssString() {
        String maliciousCode = "' + (function() { alert('hacked'); return '';})() + '";
        String htmlBlock = String.format(" <div style=\"background: url('%s');\">",
            ENCODER.safeCssString(maliciousCode));

        assertThat(htmlBlock).isEqualTo(
            " <div style=\"background: url('\\27  + (function() { alert(\\27hacked\\27); return \\27\\27;})() + " +
                "\\27');\">");
    }

    @Test
    void testSafeCssUrl() {
        String maliciousCode = "' + (function() { alert('hacked'); return '';})() + '";
        String htmlBlock = String.format(" <div style=\"background: url('%s');\">",
            ENCODER.safeCssUrl(maliciousCode));

        assertThat(htmlBlock).isEqualTo(
            " <div style=\"background: url('\\27 \\20+\\20\\28 function\\28\\29 \\20{\\20 " +
                "alert\\28\\27hacked\\27\\29;\\20return\\20\\27\\27;}\\29\\28\\29 \\20+\\20\\27');\">");
    }

}
