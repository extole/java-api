package com.extole.common.blacklist;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.ahocorasick.trie.Emit;
import org.ahocorasick.trie.Trie;
import org.ahocorasick.trie.Trie.TrieBuilder;

final class NaughtyWordsBlacklistStrategyImpl implements BlacklistStrategy {
    private static final String RESOURCE_FILE = "/naughty_words.txt";
    private static final List<String> DEFAULT_KEYWORDS = ResourceUtils.readLinesOfResourceFile(RESOURCE_FILE);

    private final Trie trie;

    NaughtyWordsBlacklistStrategyImpl() {
        this(false);
    }

    NaughtyWordsBlacklistStrategyImpl(boolean onlyWholeWords) {
        this(DEFAULT_KEYWORDS, onlyWholeWords);
    }

    NaughtyWordsBlacklistStrategyImpl(List<String> keyWords, boolean onlyWholeWords) {
        TrieBuilder builder = Trie.builder().ignoreOverlaps().ignoreCase();

        if (onlyWholeWords) {
            builder.onlyWholeWords();
        }

        for (String keyWord : keyWords) {
            builder.addKeyword(keyWord);
        }
        this.trie = builder.build();
    }

    @Override
    public BlacklistedResult verify(String value) {
        Collection<Emit> result = trie.parseText(value);
        List<String> blacklistedElements = result.stream().map(emit -> emit.getKeyword()).collect(
            Collectors.toUnmodifiableList());
        return new BlacklistedResult(blacklistedElements, BlacklistType.NAUGHTY_WORDS);
    }

}
