package com.extole.spring.transaction;

import java.lang.reflect.Method;
import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.transaction.TransactionManager;
import org.springframework.transaction.annotation.AnnotationTransactionAttributeSource;
import org.springframework.transaction.annotation.SpringTransactionAnnotationParser;
import org.springframework.transaction.interceptor.RollbackRuleAttribute;
import org.springframework.transaction.interceptor.RuleBasedTransactionAttribute;
import org.springframework.transaction.interceptor.TransactionAttribute;
import org.springframework.transaction.interceptor.TransactionInterceptor;
import org.springframework.util.ClassUtils;

/**
 * Used to override Spring's default behavior of rolling transactions back only for RuntimeException or Error
 * and instead rolls back for all Throwable classes (Exception, RuntimeException, and Error).
 *
 * IMPORTANT: you must expose it as a Spring bean with exactly this name: transactionInterceptor
 * See org.springframework.transaction.annotation.ProxyTransactionManagementConfiguration
 */
public class RollbackForThrowableTransactionInterceptor extends TransactionInterceptor {
    private static final Logger LOG = LoggerFactory.getLogger(RollbackForThrowableTransactionInterceptor.class);

    private static final ThreadLocal<TransactionInvocationInfo> CURRENT_TRANSACTION_INVOCATION = new ThreadLocal<>();

    public RollbackForThrowableTransactionInterceptor() {
        setTransactionAttributeSource(
            new AnnotationTransactionAttributeSource(new AddThrowableRollbackRuleTransactionAttributeSource()));
    }

    private static final class AddThrowableRollbackRuleTransactionAttributeSource
        extends SpringTransactionAnnotationParser {

        @Override
        protected TransactionAttribute parseTransactionAnnotation(AnnotationAttributes attributes) {
            RuleBasedTransactionAttribute transactionAttribute =
                (RuleBasedTransactionAttribute) super.parseTransactionAnnotation(attributes);
            if (transactionAttribute.getRollbackRules().isEmpty()) {
                transactionAttribute.setRollbackRules(Arrays.asList(new RollbackRuleAttribute(Throwable.class)));
            }
            return transactionAttribute;
        }
    }

    @Override
    protected Object invokeWithinTransaction(Method method, Class<?> targetClass, InvocationCallback invocation)
        throws Throwable {
        boolean changed = checkTransactionInvocation(method, targetClass);
        try {
            return super.invokeWithinTransaction(method, targetClass, invocation);
        } finally {
            if (changed) {
                clearCurrentTransactionInvocation();
            }
        }
    }

    private boolean checkTransactionInvocation(Method method, Class<?> targetClass) {
        TransactionAttribute transactionAttribute =
            getTransactionAttributeSource().getTransactionAttribute(method, targetClass);
        boolean changed = false;
        if (transactionAttribute != null) {
            TransactionManager transactionManager = determineTransactionManager(transactionAttribute);
            String transactionManagerName = transactionAttribute.getQualifier();
            String joinpointIdentification = ClassUtils.getQualifiedMethodName(method, targetClass);

            TransactionInvocationInfo currentTransactionInvocation = CURRENT_TRANSACTION_INVOCATION.get();
            if (currentTransactionInvocation == null) {
                CURRENT_TRANSACTION_INVOCATION.set(
                    new TransactionInvocationInfo(transactionManager, transactionManagerName, joinpointIdentification));
                changed = true;
            } else if (transactionManager != currentTransactionInvocation.getTransactionManager()) {
                LOG.warn(
                    "Transactional method {} managed by transaction manager {}"
                        + " is being invoked by {} managed by {} transaction manager",
                    joinpointIdentification, transactionManagerName, currentTransactionInvocation.getMethodJoinPoint(),
                    currentTransactionInvocation.getTransactionManagerName());
            }
        }
        return changed;
    }

    private void clearCurrentTransactionInvocation() {
        CURRENT_TRANSACTION_INVOCATION.remove();
    }

    private static class TransactionInvocationInfo {
        private final TransactionManager transactionManager;
        private final String transactionManagerName;
        private final String methodJoinPoint;

        TransactionInvocationInfo(TransactionManager transactionManager, String transactionManagerName,
            String methodJoinPoint) {
            this.transactionManager = transactionManager;
            this.transactionManagerName = transactionManagerName;
            this.methodJoinPoint = methodJoinPoint;
        }

        public TransactionManager getTransactionManager() {
            return transactionManager;
        }

        public String getTransactionManagerName() {
            return transactionManagerName;
        }

        public String getMethodJoinPoint() {
            return methodJoinPoint;
        }

    }
}
