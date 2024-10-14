package gafawork.scopre.repository.util;


import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.Appender;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.LoggerConfig;

import java.util.Map;

public class LogConfigManager {
    private static Logger logger = LogManager.getLogger();

    public void setLogLevel(String loggerName, String level) {
        Level newLevel = Level.valueOf(level);
        LoggerContext logContext = (LoggerContext) LogManager.getContext(false);
        Configuration configuration = logContext.getConfiguration();
        LoggerConfig loggerConfig = configuration.getLoggerConfig(loggerName);

        if (loggerConfig.getName().equalsIgnoreCase(loggerName)) {
            loggerConfig.setLevel(newLevel);
            logger.info("Changed logger level for {} to {} ", loggerName, newLevel);
        } else {
            // create a new config.
            loggerConfig = new LoggerConfig(loggerName, newLevel, false);
            logger.info("Adding config for: {} with level: {}", loggerConfig, newLevel);
            configuration.addLogger(loggerName, loggerConfig);


            LoggerConfig parentConfig = loggerConfig.getParent();
            do {
                for (Map.Entry<String, Appender> entry : parentConfig.getAppenders().entrySet()) {
                    loggerConfig.addAppender(entry.getValue(), null, null);
                }
                parentConfig = parentConfig.getParent();
            } while (null != parentConfig && parentConfig.isAdditive());
        }
        logContext.updateLoggers();
    }

    public static void setLevel(Logger logger, Level level) {
        final LoggerContext ctx = (LoggerContext) LogManager.getContext(false);
        final Configuration config = ctx.getConfiguration();

        LoggerConfig loggerConfig = config.getLoggerConfig(logger.getName());
        LoggerConfig specificConfig = loggerConfig;

        // We need a specific configuration for this logger,
        // otherwise we would change the level of all other loggers
        // having the original configuration as parent as well

        if (!loggerConfig.getName().equals(logger.getName())) {
            specificConfig = new LoggerConfig(logger.getName(), level, true);
            specificConfig.setParent(loggerConfig);
            config.addLogger(logger.getName(), specificConfig);
        }
        specificConfig.setLevel(level);
        ctx.updateLoggers();
    }
}