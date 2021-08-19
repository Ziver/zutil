package zutil.net.acme;

import org.shredzone.acme4j.Authorization;
import org.shredzone.acme4j.challenge.Challenge;
import org.shredzone.acme4j.challenge.Dns01Challenge;
import org.shredzone.acme4j.exception.AcmeException;
import zutil.log.LogUtil;

import java.util.HashMap;
import java.util.logging.Logger;

/**
 * Class implementing HTTP based challenge logic of the ACME protocol through the Zutil HttpServer.
 */
public class AcmeManualDnsChallengeFactory implements AcmeChallengeFactory {
    private static final Logger logger = LogUtil.getLogger();

    private HashMap<String,Dns01Challenge> challengeCache = new HashMap<>();

    @Override
    public Challenge createChallenge(Authorization authorization) throws AcmeException {
        if (challengeCache.containsKey(authorization.getIdentifier().getDomain()))
            return challengeCache.get(authorization.getIdentifier().getDomain());

        Dns01Challenge challenge = authorization.findChallenge(Dns01Challenge.class);
        if (challenge == null) {
            throw new AcmeException("Found no " + Dns01Challenge.TYPE + " challenge.");
        }

        // Notify user of the required manual intervention

        logger.warning(
            "---------------------------- ATTENTION ----------------------------\n" +
                "For certificate challenge to pass please create a DNS TXT record:\n" +
                "_acme-challenge." + authorization.getIdentifier().getDomain() + ". IN TXT " + challenge.getDigest() + "\n" +
                "--------------------------------------------------------------------");

        challengeCache.put(authorization.getIdentifier().getDomain(), challenge);
        return challenge;
    }
}
