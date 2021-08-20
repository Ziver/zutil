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
        Dns01Challenge challenge = authorization.findChallenge(Dns01Challenge.class);
        if (challenge == null) {
            throw new AcmeException("Found no " + Dns01Challenge.TYPE + " challenge.");
        }

        // Notify user of the required manual intervention

        logger.warning(
            "---------------------------- ATTENTION ----------------------------\n" +
                "Please deploy a DNS TXT record under the name\n" +
                getChallengeDnsName(authorization.getIdentifier().getDomain()) + " with the following value:\n\n" +
                challenge.getDigest() + "\n\n" +
                "Continue the process once this is deployed." +
                "--------------------------------------------------------------------");

        challengeCache.put(authorization.getIdentifier().getDomain(), challenge);
        return challenge;
    }

    /**
     * Returns the name of the record where the challenge value has to be assigned to.
     *
     * @param domain    the specific domain
     * @return a String DNS record name
     */
    public String getChallengeDnsName(String domain) {
        return "_acme-challenge." + domain;
    }

    /**
     * Gives the value of the required DNS record.
     *
     * @param domain    the specific domain
     * @return a special String value that needs to be added to the domains DNS record
     */
    public String getChallengeDnsValue(String domain) {
        return challengeCache.get(domain).getDigest();
    }
}
