package com.extole.api.impl.campaign;

import java.util.LinkedList;
import java.util.Optional;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.Lists;
import com.google.common.util.concurrent.UncheckedExecutionException;

public class CachedLoopPreventingEvaluatorImpl<KEY, EVALUATED> implements LoopPreventingEvaluator<KEY, EVALUATED> {

    private final LoadingCache<KEY, Optional<EVALUATED>> internalCache;
    private final LinkedList<KEY> stack = Lists.newLinkedList();

    public CachedLoopPreventingEvaluatorImpl(EvaluationLogic<KEY, EVALUATED> evaluationLogic) {
        this.internalCache = CacheBuilder.newBuilder().recordStats()
            .build(new CacheLoader<>() {
                @Override
                public Optional<EVALUATED> load(KEY unresolved) throws Exception {
                    return Optional.ofNullable(evaluationLogic.evaluate(unresolved));
                }
            });
    }

    @Override
    public EVALUATED evaluate(KEY unresolved) throws LoopDetectedException {
        if (stack.contains(unresolved)) {
            throw new LoopDetectedException(String.format("Failed to evaluate key = %s ", unresolved),
                Lists.newLinkedList(stack));
        }
        stack.add(unresolved);
        try {
            EVALUATED resolved = internalCache.getUnchecked(unresolved).orElse(null);
            stack.removeLast();
            return resolved;
        } catch (UncheckedExecutionException e) {
            stack.clear();
            throw e;
        }
    }

    public LinkedList<KEY> getStack() {
        return new LinkedList<>(stack);
    }
}
