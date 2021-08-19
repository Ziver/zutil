package zutil.net.acme;

import org.shredzone.acme4j.Authorization;
import org.shredzone.acme4j.challenge.Challenge;
import org.shredzone.acme4j.challenge.Http01Challenge;
import org.shredzone.acme4j.exception.AcmeException;
import zutil.log.LogUtil;
import zutil.net.http.HttpServer;
import zutil.net.http.HttpURL;
import zutil.net.http.page.HttpStaticContentPage;

import java.util.logging.Logger;

/**
 * Class implementing HTTP based challenge logic of the ACME protocol through the Zutil HttpServer.
 */
public class AcmeHttpChallengeFactory implements AcmeChallengeFactory {
    private static final Logger logger = LogUtil.getLogger();

    private HttpServer httpServer;
    private HttpURL url;


    public AcmeHttpChallengeFactory(HttpServer httpServer) {
        this.httpServer = httpServer;
    }


    @Override
    public Challenge createChallenge(Authorization authorization) throws AcmeException {
        Http01Challenge challenge = authorization.findChallenge(Http01Challenge.class);
        if (challenge == null) {
            throw new AcmeException("Found no " + Http01Challenge.TYPE + " challenge.");
        }

        url = new HttpURL();
        url.setProtocol("http");
        url.setHost(authorization.getIdentifier().getDomain());
        url.setPort(httpServer.getPort());
        url.setPath("/.well-known/acme-challenge/" + challenge.getToken());

        // Output the challenge, wait for acknowledge...
        logger.fine("Adding challenge HttpPage at: " + url);
        httpServer.setPage(url.getPath(), new HttpStaticContentPage(challenge.getAuthorization()));

        return challenge;
    }

    @Override
    public void postChallengeAction(Challenge challenge) {
        if (url != null) {
            httpServer.removePage(url.getPath());
        }
    }
}
