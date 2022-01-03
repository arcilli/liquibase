package liquibase.extension.testing.testsystem.core;

import liquibase.extension.testing.testsystem.DatabaseTestSystem;
import liquibase.extension.testing.testsystem.wrapper.DatabaseWrapper;
import liquibase.extension.testing.testsystem.wrapper.DockerDatabaseWrapper;
import liquibase.util.StringUtil;
import org.jetbrains.annotations.NotNull;
import org.testcontainers.containers.CockroachContainer;
import org.testcontainers.containers.JdbcDatabaseContainer;
import org.testcontainers.utility.DockerImageName;

public class CockroachTestSystem extends DatabaseTestSystem {

    public CockroachTestSystem() {
        super("cockroachdb");
    }

    @Override
    protected @NotNull DatabaseWrapper createWrapper() {
        return new DockerDatabaseWrapper(new CockroachContainer(
                DockerImageName.parse(getImageName()).withTag(getVersion())),
                this
        );
    }

    @Override
    public String getUrl() {
        final JdbcDatabaseContainer container = ((DockerDatabaseWrapper) wrapper).getContainer();

        return "jdbc:postgresql://" + container.getHost() + ":" + container.getMappedPort(26257) +"/" + getCatalog();
    }

    @Override
    protected String[] getSetupSql() {
        String passwordClause = "";
        if (getPassword() != null) {
            passwordClause = " PASSWORD '" + StringUtil.trimToEmpty(getPassword()) + "'";
        }

        return new String[]{
                "CREATE USER IF NOT EXISTS " + getUsername() + passwordClause,
                "CREATE DATABASE IF NOT EXISTS " + getCatalog(),
                "CREATE DATABASE IF NOT EXISTS " + getAltCatalog(),
//                "CREATE SCHEMA " + getAltSchema(),
                "GRANT ALL ON DATABASE " + getCatalog() + " TO " + getUsername(),
                "GRANT ALL ON DATABASE " + getAltCatalog() + " TO " + getUsername(),
//                "GRANT ALL ON SCHEMA " + getAltSchema() + " TO " + getUsername(),
        };
    }

}