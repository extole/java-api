package com.extole.evaluateable.javascript.compiler;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.stream.Collectors;

import com.google.javascript.jscomp.CompilationLevel;
import com.google.javascript.jscomp.Compiler;
import com.google.javascript.jscomp.CompilerOptions;
import com.google.javascript.jscomp.JSError;
import com.google.javascript.jscomp.PropertyRenamingPolicy;
import com.google.javascript.jscomp.Result;
import com.google.javascript.jscomp.SourceFile;
import io.opentelemetry.instrumentation.annotations.WithSpan;

import com.extole.common.lang.ExtoleThreadFactory;

public final class JavascriptCompiler {
    static {
        Compiler.setLoggingLevel(Level.SEVERE);
    }

    private JavascriptCompiler() {
    }

    public static Builder create() {
        return new Builder();
    }

    public static final class Builder {
        private final Map<Path, String> pathsWithAssociatedJavascript = new LinkedHashMap<>();

        private int threadsCount = 1;
        private boolean minify = false;

        private Builder() {

        }

        public Builder addJavascript(Path path, String javascript) {
            pathsWithAssociatedJavascript.put(path, javascript);
            return this;
        }

        public Builder withMinifying() {
            minify = true;
            return this;
        }

        public Builder withParallelization(int threadsCount) {
            this.threadsCount = threadsCount;
            return this;
        }

        private CompilerOptions buildCompilerOptions() {
            CompilerOptions options = new CompilerOptions();
            options.setLanguage(CompilerOptions.LanguageMode.ECMASCRIPT_2015);
            options.setPropertyRenaming(PropertyRenamingPolicy.OFF);
            options.setTrustedStrings(true);
            options.setEmitUseStrict(false);

            if (minify) {
                CompilationLevel.SIMPLE_OPTIMIZATIONS.setOptionsForCompilationLevel(options);
            }

            return options;
        }

        @WithSpan
        public JavascriptCompilerResult compile() throws JavascriptCompilerException {

            if (threadsCount == 1) {
                return new JavascriptCompilerResult(Collections.unmodifiableMap(compileAllSequentially()));
            } else {
                return new JavascriptCompilerResult(Collections.unmodifiableMap(compileAllParallel()));
            }
        }

        private Map<Path, String> compileAllSequentially() throws JavascriptCompilerException {
            Map<Path, String> compiledFiles = new LinkedHashMap<>();

            for (Map.Entry<Path, String> entry : pathsWithAssociatedJavascript.entrySet()) {
                Path path = entry.getKey();
                String javascript = entry.getValue();
                compiledFiles.put(path, compile(javascript, path));
            }

            return compiledFiles;
        }

        private Map<Path, String> compileAllParallel() throws JavascriptCompilerException {
            Map<Path, String> compiledFiles = new LinkedHashMap<>();
            ExecutorService executorService = Executors.newFixedThreadPool(threadsCount,
                new ExtoleThreadFactory("JavascriptCompiler"));
            Optional<String> currentPath = Optional.empty();

            try {
                Map<Path, Future<String>> futures = new LinkedHashMap<>();
                for (Map.Entry<Path, String> entry : pathsWithAssociatedJavascript.entrySet()) {
                    String javascript = entry.getValue();
                    Path path = entry.getKey();
                    Callable<String> callable = () -> compile(javascript, path);
                    futures.put(path, executorService.submit(callable));
                }

                for (Map.Entry<Path, Future<String>> entry : futures.entrySet()) {
                    currentPath = Optional.of(entry.getKey().toString());
                    String compiledJavascript = entry.getValue().get();
                    compiledFiles.put(entry.getKey(), compiledJavascript);
                }

                return compiledFiles;
            } catch (ExecutionException e) {
                if (currentPath.isPresent()) {
                    throw new JavascriptCompilerException("Unable to compile javascript file: " + currentPath.get(), e,
                        currentPath.get());
                } else {
                    throw new JavascriptCompilerException("Unable to compile javascript file", e);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new JavascriptCompilerException("Interrupted while compiling javascript files", e);
            } finally {
                executorService.shutdownNow();
            }
        }

        private String compile(String javascript, Path javascriptFilePath)
            throws JavascriptCompilerException {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            Compiler compiler = new Compiler(new PrintStream(outputStream));
            CompilerOptions compilerOptions = buildCompilerOptions();
            String pathAsString = javascriptFilePath.toString();

            try {
                SourceFile input = SourceFile.fromCode(pathAsString, javascript);
                Result result =
                    compiler.compile(Collections.emptyList(), List.of(input), compilerOptions);
                List<String> errors =
                    new ArrayList<>(result.errors).stream().map(JSError::toString)
                        .collect(Collectors.toUnmodifiableList());
                if (!errors.isEmpty()) {
                    throw new JavascriptCompilerException("Unable to compile javascript file: " + pathAsString,
                        errors, pathAsString);
                }
                return compiler.toSource();
            } catch (RuntimeException e) {
                throw new JavascriptCompilerException(
                    "Unable to compile javascript file: " + pathAsString + " due to an internal compiler error",
                    e, pathAsString);
            }
        }
    }

    public static final class JavascriptCompilerResult {
        private final Map<Path, String> pathsWithCompiledJavascript;

        private JavascriptCompilerResult(Map<Path, String> pathsWithCompiledJavascript) {
            this.pathsWithCompiledJavascript = Optional.ofNullable(pathsWithCompiledJavascript)
                .map(Collections::unmodifiableMap)
                .orElse(Collections.emptyMap());
        }

        public Map<Path, String> getCompiledJavascriptByPath() {
            return pathsWithCompiledJavascript;
        }
    }

}
