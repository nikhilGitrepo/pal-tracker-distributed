package io.pivotal.pal.tracker.timesheets;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.client.RestOperations;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ProjectClient {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProjectClient.class);

    private final RestOperations restOperations;
    private final String endpoint;

    private final Map<Long, ProjectInfo> cachedProjects = new ConcurrentHashMap<>();

    public ProjectClient(RestOperations restOperations, String registrationServerEndpoint) {
        this.restOperations = restOperations;
        this.endpoint = registrationServerEndpoint;
    }

    @HystrixCommand(fallbackMethod = "getProjectFromCache")
    public ProjectInfo getProject(long projectId) {
        ProjectInfo projectInfo = restOperations.getForObject(endpoint + "/projects/" + projectId, ProjectInfo.class);
        cachedProjects.put(projectId, projectInfo);
        LOGGER.info("Retrieving project with id {} from DB", projectId);
        return projectInfo;
    }

    public ProjectInfo getProjectFromCache(long id) {
        LOGGER.info("Retrieving project with id {} from cache", id);
        return cachedProjects.get(id);
    }
}
