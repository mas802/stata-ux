/*
 * Copyright 2015 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package istata;

import java.util.logging.Logger;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.spi.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.event.ContextClosedEvent;

@SpringBootApplication
public class StataUXApp {

    public static void main(String[] args) {
        SpringApplication.run(StataUXApp.class, args);
    }
    
    @Bean
    public GracefulShutdown gracefulShutdown() {
        return new GracefulShutdown();
    }
    
    private static class GracefulShutdown implements 
                    ApplicationListener<ContextClosedEvent> {

        protected final Log logger = LogFactory.getLog(this.getClass());


        @Override
        public void onApplicationEvent(ContextClosedEvent event) {
            logger.info("shutdown hook");
        }
    }

}
