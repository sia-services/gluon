#Support library for Quarqus applications

##auth

***AccRoles***

    enumeration of roles for authentification

>see: SECURITATE.ROLES

***UserInfo***

    short information about authorized user
    and extractors from JEE/Quarkus SecurityContext

> see: SECURITATE.USERS
 
> uses libraries: jakarta.ws.rs-api (SecurityContext), microprofile-jwt-auth-api (jwt.Claims),
smallrye-jwt (DefaultJWTCallerPrincipal)

##dto

    Data Transfer Objects for Boolean and String 

##mappers

    Automatic Mappers for some Exceptions to javax.ws.rs.core.Response

> uses: jakarta.ws.rs-api

***IllegalArgumentExceptionMapper***

    generate BAD REQUEST (400) response

***NoContentExceptionMapper***

    generate NO CONTENT (204) response

***NotFoundExceptionMapper***

    generate NOT FOUND (404) response

***SQLExceptionMapper**

    generate INTERNAL SERVER ERROR (500) response

***UnsupportedOperationExceptionMapper***

    generate BAD REQUEST (400) response

##sql

    thin wrapper over jdbc

***SQLManager***

    core class for database operations

***Extractors and Setters***

    static helpers for extract values from ResultSet and provide values for PreparedStatement

***SQLManager.groupBy***

    query from database and group values by ResultSetExtractor<PK> by

##utilities

    small usable utilities

***ImmutablePair***

    tuple of 2 (two) elements

***Month***

    contains utilities for Month manipulation (prev, next, delta, formatters)

***RefCell and BooleanRefCell***

    simulate IN/OUT parameters; (pass internal values by reference)
