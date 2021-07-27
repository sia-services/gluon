package com.acc.gluon.ds;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.sql.DataSource;
import java.sql.SQLException;

@ApplicationScoped
public class SQLProvider {

    @Inject
    DataSource ds;

    @PostConstruct
    public void init() {
        System.out.println("Initialization of SQLProducer, ds: " + ds.getClass());
    }

    public SQLManager provide() {
        try {
            System.out.println("produce");
            var connection = ds.getConnection();
            connection.setAutoCommit(false);
            return new SQLManager(connection);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return null;
    }

    /*
    @Produces
    public SQLManager produce(InjectionPoint ip) {
        var qualifiers = ip.getQualifiers();

        for (var q : qualifiers) {
            System.out.println("SQLManager Qualifier: " + q.getClass());
        }

        var annotated = ip.getAnnotated();
        if (annotated != null) {
            var ds = annotated.getAnnotation(DataSource.class);
            if (ds != null) {
                System.out.println("Datasource name: " + ds);
            }
        }

        return null;
    }
    */
}
