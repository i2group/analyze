# Auditing the REST API endpoints

The i2 Analyze REST API, and the clients that you create to use it, make some powerful capabilities available to users.
In the walkthrough, you've seen how to execute text and geospatial searches that retrieve data from the Information Store.

If some of your users should not have access to certain data or functionality, you can use the security schema or command access control to enforce restrictions.
However, you might also need to understand what users are doing when they _do_ have access.
How frequently do they use certain functionality? What things are they searching for?

This requirement is what auditing can help with.

**Note:** If your deployment of i2 Analyze is secured so that users can access it only through the applications that you develop, then you might implement auditing in those applications.
But if users have access to Analyst's Notebook Premium or i2 Notebook, or if you want a single auditing point for everything that targets the server, then you need i2 Analyze to provide the audit data.

## The auditing extension point

i2 Analyze provides a Java extension point that enables auditing of specific REST API endpoints.
It enables you to write code to be executed at the point where the API request is received by the i2 Analyze server.

Your code receives information about which operation was invoked, when it was called, how it was called (in terms of the parameters supplied), which user called it, and so on.
You can then write this information to a file or a database, for example, where it can be analyzed when required.

**Note:** There is no default implementation of the auditing extension point.
In a standard deployment, the i2 Analyze server does not log user activity.
If you need this functionality, you must implement it.

i2 Analyze Developer Essentials contains several examples of how to use the extension point.
For example, see [Audit logging to file](../java-api/Configuring-the-audit-logging-to-file-example-project.md), which is a simple implementation that writes everything it receives to disk, as a single text string.

The important part of the example looks like this:

```java
public final class FileAuditLogger
    implements IAuditLogger
{
    private final AuditFileWriter mLogWriter = new AuditFileWriter(new FileConfiguration("FileAudit.properties"));

    @Override
    public boolean isQueryAuditEnabled()
    {
        return true;
    }

    @Override
    public boolean isRecordRetrievalAuditEnabled()
    {
        return true;
    }

    @Override
    public boolean isRecordCUDAuditEnabled()
    {
        return true;
    }

    @Override
    public void logDefault(final IAuditEvent event)
    {
        mLogWriter.writeAuditLog(event.toString());
    }
}
```

In this implementation, auditing is enabled for all the major areas of functionality: querying; record retrieval; and record create, update, and delete (CUD).

The `logDefault()` method captures the information from any API requests that target those functional areas, which is wrapped in the supplied `IAuditEvent` object. It then writes the string representation of that information to a file.

For more examples of using the auditing extension point, take a look at the [source code](/developer-essentials/SDK/java-api-projects) for the other sample projects.
