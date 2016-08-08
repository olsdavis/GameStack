package fr.creart.gamestack.common.log;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.log4j.Layout;
import org.apache.log4j.Level;
import org.apache.log4j.spi.LoggingEvent;

/**
 * @author Creart
 */
public class CustomLayout extends Layout {

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("'['HH':'mm']' ");

    private boolean file;

    public CustomLayout(boolean file)
    {
        this.file = file;
    }

    @Override
    public String format(LoggingEvent event)
    {
        if (event == null || (file && event.getLevel() == Level.DEBUG))
            return null;

        StringBuilder builder = new StringBuilder(); // StringBuilders are more efficient than simple String concatenations
        builder.append(currentTime());
        builder.append('[').append(event.getLogger().getName()).append("] ");
        builder.append(event.getLevel().toString()).append(": ");
        builder.append(event.getMessage());

        if (event.getThrowableInformation() != null)
            builder.append(" Exception: ").append(ExceptionUtils.getStackTrace(event.getThrowableInformation().getThrowable()));

        builder.append("\n");

        return builder.toString();
    }

    @Override
    public boolean ignoresThrowable()
    {
        return false;
    }

    @Override
    public void activateOptions()
    {
        // nothing to do here.
    }

    private static String currentTime()
    {
        return LocalDateTime.now().format(DATE_FORMAT);
    }

}