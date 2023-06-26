package io.github.pangzixiang.whatsit.vertx.jax.rs.local.controller;

import io.github.pangzixiang.whatsit.vertx.jax.rs.ApplicationContext;
import io.github.pangzixiang.whatsit.vertx.jax.rs.annotation.RequestBody;
import io.github.pangzixiang.whatsit.vertx.jax.rs.annotation.RestController;
import io.github.pangzixiang.whatsit.vertx.jax.rs.controller.BaseController;
import io.github.pangzixiang.whatsit.vertx.jax.rs.local.model.TestTableEntity;
import io.github.pangzixiang.whatsit.vertx.jax.rs.local.repository.TestRepository;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.Future;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;

import java.time.LocalDateTime;

@RestController
@Path("/db")
public class DBTestController extends BaseController {

    @Override
    public void start() throws Exception {
        getRouter().route().handler(BodyHandler.create());
        super.start();
    }

    private final TestRepository testRepository;

    /**
     * Instantiates a new Base controller.
     *
     * @param router the router
     */
    public DBTestController(Router router) {
        super(router);
        this.testRepository = new TestRepository();
    }

    @Path("/insert")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Future<String> insert(RoutingContext routingContext, @RequestBody TestTableEntity testTableEntity) {
        testTableEntity.setLastUpdatedTimestamp(LocalDateTime.now());
        return ApplicationContext.getApplicationContext().getJdbcPool().withConnection(sqlConnection -> this.testRepository.insert(sqlConnection, testTableEntity))
                .map("inserted")
                .onFailure(throwable -> {
                    this.sendJsonResponse(routingContext, HttpResponseStatus.BAD_REQUEST, throwable.getMessage());
                });
    }

    @Path("/select/{testValue}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Future<TestTableEntity> select(RoutingContext routingContext, @PathParam("testValue") String testValue) {
        return ApplicationContext.getApplicationContext().getJdbcPool().withConnection(sqlConnection -> this.testRepository.select(sqlConnection, testValue))
                .onFailure(throwable -> {
                    this.sendJsonResponse(routingContext, HttpResponseStatus.BAD_REQUEST, throwable.getMessage());
                });
    }
}
