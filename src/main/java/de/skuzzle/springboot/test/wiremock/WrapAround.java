package de.skuzzle.springboot.test.wiremock;

/**
 * Defines the response behavior in case a {@link HttpStub} has multiple responses.
 *
 * @author Simon Taddiken
 */
public enum WrapAround {
    /**
     * If the mock reached the last defined response, it will respond with a 403 status
     * for every subsequent request.
     */
    RETURN_ERROR {
        @Override
        int determineNextState(int currentState, boolean hasNext) {
            return currentState + 1;
        }
    },
    /**
     * If the mock reached the last defined response, it will start over with the first
     * defined response.
     */
    START_OVER {

        @Override
        int determineNextState(int currentState, boolean hasNext) {
            return !hasNext ? 0 : currentState + 1;
        }

    },
    /**
     * If the mock reached the last defined response, it will repeat it forever for every
     * subsequent request.
     */
    REPEAT {

        @Override
        int determineNextState(int currentState, boolean hasNext) {
            return hasNext
                    ? currentState + 1
                    : currentState;
        }

    };

    abstract int determineNextState(int currentState, boolean hasNext);
}
