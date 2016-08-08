package fr.creart.gamestack.common.connection.database.sql;

import com.google.common.base.Strings;
import fr.creart.gamestack.common.connection.database.AbstractDatabase;
import fr.creart.gamestack.common.connection.database.RequestType;
import fr.creart.gamestack.common.lang.ClassUtil;
import fr.creart.gamestack.common.lang.MoreArrays;
import fr.creart.gamestack.common.log.CommonLogger;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Arrays;
import org.apache.commons.lang3.Validate;

/**
 * Represents a SQL database
 *
 * @author Creart
 */
public abstract class SQLDatabase extends AbstractDatabase<Connection, SQLRequest, SQLConnectionData> {

    protected String databaseSystemName;
    protected String driver;

    public SQLDatabase(int threads)
    {
        super(threads);
    }

    @Override
    protected boolean connect(SQLConnectionData connectionData)
    {
        try {
            connection = DriverManager.
                    getConnection("jdbc:" + databaseSystemName + "://" + connectionData.getHost() + ":"
                            + connectionData.getPort() + "/" + connectionData.getDatabase());
            return true;
        } catch (Exception e) {
            CommonLogger.error("Failed to connect to the database.", e);
            return false;
        }
    }

    @Override
    protected void end()
    {
        try {
            if (isEstablished())
                connection.close();
        } catch (Exception e) {
            CommonLogger.error("Could not close SQL connection.", e);
        }
    }

    private boolean isEstablished()
    {
        try {
            return connection != null && !connection.isClosed();
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public boolean isConnectionEstablished()
    {
        return super.isConnectionEstablished() && isEstablished();
    }

    @Override
    public void executeRequests(SQLRequest... requests)
    {
        if (requests.length == 0)
            return;

        if (MoreArrays.isTrueForAll(requests, (request) -> request != null && request.getType() != RequestType.QUERY)) {
            enqueueTask(conn -> {
                Statement statement = null;
                try {
                    statement = conn.createStatement();
                    for (SQLRequest request : requests)
                        if (!Strings.isNullOrEmpty(request.getRequest()))
                            statement.addBatch(request.getRequest());
                    statement.executeBatch();
                } catch (Exception e) {
                    CommonLogger.error("Encountered an exception during the execution of an SQL batch (requests=" + Arrays.toString(requests) + ").", e);
                } finally {
                    if (statement != null && !statement.isClosed())
                        statement.close();
                }
            });
            return;
        }

        for (SQLRequest request : requests) {
            Validate.notNull(request, "request can't be null");
            Validate.notEmpty(request.getRequest(), "the sql request can't be empty or null");
            Validate.notNull(request.getType(), "request's type can't be null");

            enqueueTask(conn -> {
                PreparedStatement statement = null;
                ResultSet result = null;
                try {
                    statement = conn.prepareStatement(request.getRequest());
                    switch (request.getType()) {
                        case QUERY:
                            result = statement.executeQuery();
                            request.getCallback().call(result);
                            break;
                        case INSERT:
                        case UPDATE:
                            statement.executeUpdate();
                            break;
                    }
                } catch (Exception e) {
                    CommonLogger.error("Encountered an exception during the execution of the following SQL request: " + request.toString() + ".", e);
                } finally {
                    if (result != null && !result.isClosed())
                        result.close();
                    if (statement != null && !statement.isClosed())
                        statement.close();
                }
            });
        }
    }

    @Override
    protected final void initializeDriver()
    {
        ClassUtil.loadClass(driver);
    }

}