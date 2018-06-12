package example.async;

import example.retry.RetryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
public class AsyncService {

    private static final Logger logger = LoggerFactory.getLogger(AsyncService.class);

    private RetryService service;

    public AsyncService(RetryService service) {
        this.service = service;
    }

    @Async
    public CompletableFuture<User> findUser(String user) throws InterruptedException {
        logger.info("Looking up " + user);
        User results = service.callGithubApi(user);
        // Artificial delay of 1s for demonstration purposes
        Thread.sleep(1000L);
        return CompletableFuture.completedFuture(results);
    }



}