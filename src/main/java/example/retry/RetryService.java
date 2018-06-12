package example.retry;

import example.async.AsyncService;
import example.async.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.remoting.RemoteAccessException;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

public class RetryService {
    private static final Logger logger = LoggerFactory.getLogger(RetryService.class);

    private final RestTemplate restTemplate;

    public RetryService(RestTemplateBuilder restTemplateBuilder) {
        this.restTemplate = restTemplateBuilder.build();
    }

    @Retryable(value = {HttpServerErrorException.class}, exclude = {HttpClientErrorException.class})
    public User callGithubApi(String user) {
        return restCall(user);
    }

    @Recover
    public User recover(String user) {
        logger.info("Recoving call for user: "+user);
        return new User();
    }

    private User restCall(String user) {
        try {
            logger.info("Calling github with: "+ user);
            String url = String.format("https://api.github.com/users/%s", user);
            if(user.equals("CloudFoundry"))
                throw new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR);
            return restTemplate.getForObject(url, User.class);
        }catch (HttpStatusCodeException e){
            logger.error("Error calling github api: statusCode:"+ e.getStatusCode()+": \n"+e);
            throw e;
        }
    }

}
