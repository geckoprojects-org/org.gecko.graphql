package org.gecko.whiteboard.graphql.emf.executation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import graphql.Assert;
import graphql.ErrorType;
import graphql.GraphQLError;
import graphql.execution.ExecutionStrategyParameters;
import graphql.language.Field;
import graphql.language.SourceLocation;
import graphql.schema.DataFetcher;

/**
 * A {@link GraphQLError} that has been changed from a {@link DataFetcher} relative error to an absolute one.
 */
class AbsoluteGraphQLError implements GraphQLError {

    /** serialVersionUID */
	private static final long serialVersionUID = 8942322849668021106L;
	private final List<SourceLocation> locations;
    private final List<Object> absolutePath;
    private final String message;
    private final ErrorType errorType;
    private final Map<String, Object> extensions;

    AbsoluteGraphQLError(ExecutionStrategyParameters executionStrategyParameters, GraphQLError relativeError) {
    	Assert.assertNotNull(executionStrategyParameters);
    	Assert.assertNotNull(relativeError);
        this.absolutePath = createAbsolutePath(executionStrategyParameters, relativeError);
        this.locations = createAbsoluteLocations(relativeError, executionStrategyParameters.getField());
        this.message = relativeError.getMessage();
        this.errorType = relativeError.getErrorType();
        if (relativeError.getExtensions() != null) {
            this.extensions = new HashMap<>();
            this.extensions.putAll(relativeError.getExtensions());
        } else {
            this.extensions = null;
        }
    }

    @Override
    public String getMessage() {
        return message;
    }

    @Override
    public List<SourceLocation> getLocations() {
        return locations;
    }

    @Override
    public ErrorType getErrorType() {
        return errorType;
    }

    @Override
    public List<Object> getPath() {
        return absolutePath;
    }

    @Override
    public Map<String, Object> getExtensions() {
        return extensions;
    }

    /**
     * Creating absolute paths follows the following logic:
     *  Relative path is null -> Absolute path null
     *  Relative path is empty -> Absolute paths is path up to the field.
     *  Relative path is not empty -> Absolute paths [base Path, relative Path]
     * @param relativeError relative error
     * @param executionStrategyParameters execution strategy params.
     * @return List of paths from the root.
     */
    private List<Object> createAbsolutePath(ExecutionStrategyParameters executionStrategyParameters,
                                            GraphQLError relativeError) {
        return Optional.ofNullable(relativeError.getPath())
                .map(originalPath -> {
                    List<Object> path = new ArrayList<>();
                    path.addAll(executionStrategyParameters.getPath().toList());
                    path.addAll(relativeError.getPath());
                    return path;
                })
                .map(Collections::unmodifiableList)
                .orElse(null);
    }

    /**
     * Creating absolute locations follows the following logic:
     *  Relative locations is null -> Absolute locations null
     *  Relative locations is empty -> Absolute locations base locations of the field.
     *  Relative locations is not empty -> Absolute locations [base line + relative line location]
     * @param relativeError relative error
     * @param fields fields on the current field.
     * @return List of locations from the root.
     */
    private List<SourceLocation> createAbsoluteLocations(GraphQLError relativeError, List<Field> fields) {
        Optional<SourceLocation> baseLocation;
        if (!fields.isEmpty()) {
            baseLocation = Optional.ofNullable(fields.get(0).getSourceLocation());
        } else {
            baseLocation = Optional.empty();
        }

        // relative error empty path should yield an absolute error with the base path
        if (relativeError.getLocations() != null && relativeError.getLocations().isEmpty()) {
            return baseLocation.map(Collections::singletonList).orElse(null);
        }

        return Optional.ofNullable(
                relativeError.getLocations())
                .map(locations -> locations.stream()
                        .map(l ->
                                baseLocation
                                        .map(base -> new SourceLocation(
                                                base.getLine() + l.getLine(),
                                                base.getColumn() + l.getColumn()))
                                        .orElse(null))
                        .collect(Collectors.toList()))
                .map(Collections::unmodifiableList)
                .orElse(null);
    }
}
