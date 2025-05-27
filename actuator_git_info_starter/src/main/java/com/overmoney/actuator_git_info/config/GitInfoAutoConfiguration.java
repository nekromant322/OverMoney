package com.overmoney.actuator_git_info.config;


import com.overmoney.actuator_git_info.ApplicationStartUpListener;
import com.overmoney.actuator_git_info.GitCommitEndpoint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.autoconfigure.endpoint.condition.ConditionalOnAvailableEndpoint;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

@AutoConfiguration
@EnableConfigurationProperties(InfoGitProperties.class)
public class GitInfoAutoConfiguration {



    @Bean
    @ConditionalOnMissingBean
    public ApplicationStartUpListener applicationStartUpListener() {
        return new ApplicationStartUpListener();
    }


    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnAvailableEndpoint(endpoint = GitCommitEndpoint.class)
    public GitCommitEndpoint gitCommitEndpoint(ApplicationStartUpListener listener, InfoGitProperties properties) {
        return new GitCommitEndpoint(listener, properties);
    }

}
