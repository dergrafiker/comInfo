package com.commerzinfo.input;

import com.commerzinfo.Constants;
import com.commerzinfo.util.CompressionUtil;
import com.google.common.collect.Lists;
import net.htmlparser.jericho.Element;
import net.htmlparser.jericho.MasonTagTypes;
import net.htmlparser.jericho.MicrosoftConditionalCommentTagTypes;
import net.htmlparser.jericho.PHPTagTypes;
import net.htmlparser.jericho.Source;
import org.apache.commons.lang.StringUtils;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.Collection;

public class HTMLReader {
    private static org.slf4j.Logger logger = LoggerFactory.getLogger(HTMLReader.class);

    static {
        MicrosoftConditionalCommentTagTypes.register();
        PHPTagTypes.register();
        PHPTagTypes.PHP_SHORT.deregister(); // remove PHP short tags for this example otherwise they override processing instructions
        MasonTagTypes.register();
    }

    public static Collection<String> getElementsFromFile(File file, String element) throws IOException {
        if (logger.isInfoEnabled())
            logger.info("READING FILE " + file.getAbsolutePath());
        if (!Constants.HTML_FILE_FILTER.accept(file)) {
            throw new IllegalArgumentException(file.getAbsolutePath() +
                    " does not match " + StringUtils.join(Constants.HTML_PATTERNS, ','));
        }

        Source source = new Source(CompressionUtil.getCorrectInputStream(file));
        Collection<String> lines = Lists.newLinkedList();
        for (Element currentElement : source.getAllElements(element)) {
            lines.add(currentElement.getContent().toString());
        }
        return lines;
    }
}
