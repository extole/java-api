package com.extole.file.parser.content;

import java.io.IOException;
import java.util.List;

public interface FileContentWriteStrategy {

    <T> ParsedFileContent writeFileContent(List<T> list) throws IOException;

}
