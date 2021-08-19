package zutil.net.acme;

import org.shredzone.acme4j.Authorization;
import org.shredzone.acme4j.challenge.Challenge;
import org.shredzone.acme4j.exception.AcmeException;

public interface AcmeChallengeFactory {

    /**
     * Create a new Challenge object and do any needed actions for the challenge to proceed.
     *
     * @param authorization
     * @return a Challenge object that will be used to authorize a domain.
     * @throws AcmeException in case of any issues
     */
    Challenge createChallenge(Authorization authorization) throws AcmeException;

    /**
     * Do any needed cleanup after challenge has completed successfully or failed.
     */
    default void postChallengeAction(Challenge challenge) {}
}
